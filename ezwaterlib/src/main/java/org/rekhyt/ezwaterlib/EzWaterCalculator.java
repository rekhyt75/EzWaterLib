/**
 * 
 */
package org.rekhyt.ezwaterlib;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;

import org.rekhyt.ezwaterlib.model.AdjustWater;
import org.rekhyt.ezwaterlib.model.EzWaterResult;
import org.rekhyt.ezwaterlib.model.Grain;
import org.rekhyt.ezwaterlib.model.GrainList;
import org.rekhyt.ezwaterlib.model.ResultWaterProfile;
import org.rekhyt.ezwaterlib.model.WaterProfile;
import org.rekhyt.ezwaterlib.model.WaterVolume;

/**
 * @author michele.antonecchia
 *
 */
public class EzWaterCalculator {

    private static final MathContext mc = new MathContext(6, RoundingMode.HALF_UP);

    // pH prediction constant, metric (liters / kg). Imperial equivalent (gallons / lbs) is 0.1085
    private static final BigDecimal PH_SPECIFIC = new BigDecimal("0.0130011821");
    private static final BigDecimal PH_SPECIFIC_OFFSET = new BigDecimal("0.013");
    private static final BigDecimal PH_RESIDUAL_ALK_DIVISOR = new BigDecimal("50");

    private static final BigDecimal LITERS_PER_GALLON = new BigDecimal("3.785412");

    // Ion contribution factors: mg/L of ion per gram of salt added per gallon of water
    private static final BigDecimal CALCIUM_FROM_CACO3 = new BigDecimal("105.89");
    private static final BigDecimal CALCIUM_FROM_CASO4 = new BigDecimal("60");
    private static final BigDecimal CALCIUM_FROM_CACL2 = new BigDecimal("72");
    private static final BigDecimal CALCIUM_FROM_CA_OH_2 = new BigDecimal("143");

    private static final BigDecimal MAGNESIUM_FROM_MGSO4 = new BigDecimal("24.6");

    private static final BigDecimal SODIUM_FROM_NAHCO3 = new BigDecimal("72.3");

    private static final BigDecimal CHLORIDE_FROM_CACL2 = new BigDecimal("127.47");

    private static final BigDecimal SULFATE_FROM_CASO4 = new BigDecimal("147.4");
    private static final BigDecimal SULFATE_FROM_MGSO4 = new BigDecimal("103");

    // Effective alkalinity contribution factors, mg/L as CaCO3, per gram per gallon
    private static final BigDecimal ALK_FROM_CACO3 = new BigDecimal("130");
    private static final BigDecimal ALK_FROM_NAHCO3 = new BigDecimal("157");
    private static final BigDecimal ALK_FROM_CA_OH_2 = new BigDecimal("357");
    private static final BigDecimal ALK_FROM_LACTIC_ACID = new BigDecimal("352.2");
    private static final BigDecimal ALK_FROM_ACIDULATED_MALT_NUMERATOR = new BigDecimal("10401");
    private static final BigDecimal ALK_FROM_ACIDULATED_MALT_DENOMINATOR = new BigDecimal("28.34952");

    private static final BigDecimal BICARBONATE_TO_ALKALINITY_NUMERATOR = new BigDecimal("50");
    private static final BigDecimal BICARBONATE_TO_ALKALINITY_DENOMINATOR = new BigDecimal("61");

    private static final BigDecimal RESIDUAL_ALK_CALCIUM_DIVISOR = new BigDecimal("1.4");
    private static final BigDecimal RESIDUAL_ALK_MAGNESIUM_DIVISOR = new BigDecimal("1.7");

    public static EzWaterResult calcEzWater(WaterProfile initialWater, WaterVolume volume, GrainList grainList,
            AdjustWater adjustWaterMash, AdjustWater adjustWaterSparge) {

        EzWaterResult ezWaterResult = new EzWaterResult();

        Map<String, Grain> grainMap = grainList.getGrainList();
        if (grainMap.isEmpty()) {
            throw new IllegalArgumentException("grainList must contain at least one grain");
        }

        ResultWaterProfile mashWater = calculateResultWaterProfile(initialWater, volume.getMash(),
                volume.getMashDistilledPercentage(), adjustWaterMash);
        ezWaterResult.setMashWater(mashWater);

        // Residual Alkalinity
        BigDecimal residualAlk = mashWater.getResidualAlk();

        // calculate PH
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal grainPh = BigDecimal.ZERO;
        for (Grain grain : grainMap.values()) {
            totalWeight = totalWeight.add(grain.getWeight());
            grainPh = grainPh.add(grain.getWeight().multiply(grain.getDistilledWaterPh()));
        }
        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("grainList must contain at least one grain with a positive weight");
        }
        grainPh = grainPh.divide(totalWeight, mc);

        BigDecimal ph = volume.getMash().divide(totalWeight, mc).multiply(PH_SPECIFIC, mc)
                .add(PH_SPECIFIC_OFFSET, mc);
        ph = ph.multiply(residualAlk.divide(PH_RESIDUAL_ALK_DIVISOR, mc), mc);
        ph = ph.add(grainPh, mc);

        ezWaterResult.setPh(ph);

        // calculate distilled percentage
        BigDecimal distilled = volume.getMashDistilledPercentage().multiply(volume.getMash(), mc);
        distilled = distilled.add(volume.getSpargeDistilledPercentage().multiply(volume.getSparge(), mc));
        distilled = distilled.divide(volume.getMash().add(volume.getSparge()), mc);
        
        // calculate total ingredients
        AdjustWater adjustWaterMashSparge = calcTotalAdjustment(adjustWaterMash, adjustWaterSparge);
        
        ResultWaterProfile mashSpargeWater = calculateResultWaterProfile(initialWater, volume.getSparge().add(volume.getMash()),
                distilled, adjustWaterMashSparge);
        
        ezWaterResult.setMashSpargeWater(mashSpargeWater);

        return ezWaterResult;
    }

    private static AdjustWater calcTotalAdjustment(AdjustWater adjustWaterMash, AdjustWater adjustWaterSparge) {

        AdjustWater total = new AdjustWater();

        total.setCa_OH_2(adjustWaterMash.getCa_OH_2().add(adjustWaterSparge.getCa_OH_2()));
        total.setCaCl2(adjustWaterMash.getCaCl2().add(adjustWaterSparge.getCaCl2()));
        total.setCaCO3(adjustWaterMash.getCaCO3().add(adjustWaterSparge.getCaCO3()));
        total.setCaSO4(adjustWaterMash.getCaSO4().add(adjustWaterSparge.getCaSO4()));
        total.setMgSO4(adjustWaterMash.getMgSO4().add(adjustWaterSparge.getMgSO4()));
        total.setNaHCO3(adjustWaterMash.getNaHCO3().add(adjustWaterSparge.getNaHCO3()));
        total.setAcidulatedMalt(adjustWaterMash.getAcidulatedMalt().add(adjustWaterSparge.getAcidulatedMalt()));
        total.setLacticAcid(adjustWaterMash.getLacticAcid().add(adjustWaterSparge.getLacticAcid()));
        total.setAcidulatedMaltContent(adjustWaterMash.getAcidulatedMaltContent());
        total.setLacticAcidContent(adjustWaterMash.getLacticAcidContent());

        return total;
    }

    private static ResultWaterProfile calculateResultWaterProfile(WaterProfile initialWater, BigDecimal volume,
            BigDecimal distilledPercentage, AdjustWater adjustWaterMash) {

        ResultWaterProfile resultWaterProfile = new ResultWaterProfile();

        // Calculate minerals
        BigDecimal gallons = volume.divide(LITERS_PER_GALLON, mc);
        if (gallons.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("volume must be greater than zero");
        }

        // - Calcium
        BigDecimal calcium = BigDecimal.ZERO;
        calcium = calcium.add(adjustWaterMash.getCaCO3().multiply(CALCIUM_FROM_CACO3, mc));
        calcium = calcium.add(adjustWaterMash.getCaSO4().multiply(CALCIUM_FROM_CASO4, mc));
        calcium = calcium.add(adjustWaterMash.getCaCl2().multiply(CALCIUM_FROM_CACL2, mc));
        calcium = calcium.add(adjustWaterMash.getCa_OH_2().multiply(CALCIUM_FROM_CA_OH_2, mc));
        calcium = calcium.divide(gallons, mc); // Expression in gallons
        calcium = calcium
                .add((BigDecimal.ONE.subtract(distilledPercentage, mc)).multiply(initialWater.getCalcium(), mc));
        resultWaterProfile.setCalcium(calcium);

        // - Magnesium
        BigDecimal magnesium = BigDecimal.ZERO;
        magnesium = magnesium.add(adjustWaterMash.getMgSO4().multiply(MAGNESIUM_FROM_MGSO4, mc));
        magnesium = magnesium.divide(gallons, mc); // Expression in gallons
        magnesium = magnesium
                .add((BigDecimal.ONE.subtract(distilledPercentage, mc)).multiply(initialWater.getMagnesium(), mc));
        resultWaterProfile.setMagnesium(magnesium);

        // - Sodium
        BigDecimal sodium = BigDecimal.ZERO;
        sodium = sodium.add(adjustWaterMash.getNaHCO3().multiply(SODIUM_FROM_NAHCO3, mc));
        sodium = sodium.divide(gallons, mc); // Expression in gallons
        sodium = sodium.add((BigDecimal.ONE.subtract(distilledPercentage, mc)).multiply(initialWater.getSodium(), mc));
        resultWaterProfile.setSodium(sodium);

        // - Chloride
        BigDecimal chloride = BigDecimal.ZERO;
        chloride = chloride.add(adjustWaterMash.getCaCl2().multiply(CHLORIDE_FROM_CACL2, mc));
        chloride = chloride.divide(gallons, mc); // Expression in gallons
        chloride = chloride
                .add((BigDecimal.ONE.subtract(distilledPercentage, mc)).multiply(initialWater.getChloride(), mc));
        resultWaterProfile.setChloride(chloride);

        // - Sulfate
        BigDecimal sulfate = BigDecimal.ZERO;
        sulfate = sulfate.add(adjustWaterMash.getCaSO4().multiply(SULFATE_FROM_CASO4, mc));
        sulfate = sulfate.add(adjustWaterMash.getMgSO4().multiply(SULFATE_FROM_MGSO4, mc));
        sulfate = sulfate.divide(gallons, mc); // Expression in gallons
        sulfate = sulfate
                .add((BigDecimal.ONE.subtract(distilledPercentage, mc)).multiply(initialWater.getSulfate(), mc));
        resultWaterProfile.setSulfate(sulfate);

        // - Chloride / Sulfate Ratio (undefined, reported as zero, when there is no sulfate)
        BigDecimal chlorideSulfateRatio = sulfate.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                : chloride.divide(sulfate, mc);
        resultWaterProfile.setChlorideSulfateRatio(chlorideSulfateRatio);

        // Claculate alkalinity

        // - Effective Alkalinity
        BigDecimal alk;
        if (initialWater.getAlkalinity().compareTo(BigDecimal.ZERO) == 1) {
            // alkalinity value wins over Bicarbonates
            alk = initialWater.getAlkalinity();
        } else {
            // estimate alkalinity from bicarbonates
            alk = initialWater.getBicarbonate().multiply(BICARBONATE_TO_ALKALINITY_NUMERATOR, mc)
                    .divide(BICARBONATE_TO_ALKALINITY_DENOMINATOR, mc);
        }

        BigDecimal effectiveAlk = BigDecimal.ZERO;
        effectiveAlk = effectiveAlk.add(adjustWaterMash.getCaCO3().multiply(ALK_FROM_CACO3, mc));
        effectiveAlk = effectiveAlk.add(adjustWaterMash.getNaHCO3().multiply(ALK_FROM_NAHCO3, mc));
        effectiveAlk = effectiveAlk.add(adjustWaterMash.getCa_OH_2().multiply(ALK_FROM_CA_OH_2, mc));
        effectiveAlk = effectiveAlk.subtract(adjustWaterMash.getLacticAcid()
                .multiply(adjustWaterMash.getLacticAcidContent(), mc).multiply(ALK_FROM_LACTIC_ACID, mc));
        effectiveAlk = effectiveAlk
                .subtract(adjustWaterMash.getAcidulatedMalt().multiply(adjustWaterMash.getAcidulatedMaltContent(), mc)
                        .multiply(ALK_FROM_ACIDULATED_MALT_NUMERATOR, mc)
                        .divide(ALK_FROM_ACIDULATED_MALT_DENOMINATOR, mc));
        effectiveAlk = effectiveAlk.divide(gallons, mc); // Expression in gallons
        effectiveAlk = effectiveAlk.add((BigDecimal.ONE.subtract(distilledPercentage, mc)).multiply(alk, mc));
        resultWaterProfile.setEffectiveAlk(effectiveAlk);

        // - Residual Alkalinity
        BigDecimal residualAlk = effectiveAlk.subtract(calcium.divide(RESIDUAL_ALK_CALCIUM_DIVISOR, mc));
        residualAlk = residualAlk.subtract(magnesium.divide(RESIDUAL_ALK_MAGNESIUM_DIVISOR, mc));
        resultWaterProfile.setResidualAlk(residualAlk);

        return resultWaterProfile;
    }

    /*
     * Scale the adjustment with volume ratio and scale each components with
     * different ratio use scale = 0 to exclude a component into adjustment
     * 
     */
    public static AdjustWater scaleAdjustment(AdjustWater adjustWater, BigDecimal volumeRatio, BigDecimal scaleCaOH2,
            BigDecimal scaleNaHCO3, BigDecimal scaleCaCO3, BigDecimal scaleCaSO, BigDecimal scaleCaCl2,
            BigDecimal scaleMgSO4) {

        AdjustWater adjustWaterResult = new AdjustWater();

        adjustWaterResult.setCa_OH_2(adjustWater.getCa_OH_2().multiply(volumeRatio, mc).multiply(scaleCaOH2, mc));
        adjustWaterResult.setNaHCO3(adjustWater.getNaHCO3().multiply(volumeRatio, mc).multiply(scaleNaHCO3, mc));
        adjustWaterResult.setCaCO3(adjustWater.getCaCO3().multiply(volumeRatio, mc).multiply(scaleCaCO3, mc));
        adjustWaterResult.setCaSO4(adjustWater.getCaSO4().multiply(volumeRatio, mc).multiply(scaleCaSO, mc));
        adjustWaterResult.setCaCl2(adjustWater.getCaCl2().multiply(volumeRatio, mc).multiply(scaleCaCl2, mc));
        adjustWaterResult.setMgSO4(adjustWater.getMgSO4().multiply(volumeRatio, mc).multiply(scaleMgSO4, mc));

        return adjustWaterResult;

    }

    /*
     * Scale the adjustment with volume ratio using all components with the same
     * ratio
     * 
     */
    public static AdjustWater scaleAdjustment(AdjustWater adjustWater, BigDecimal volumeRatio) {

        return scaleAdjustment(adjustWater, volumeRatio, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);

    }
    
    /*
     * Scale the adjustment with volume ratio calculated by input volume and
     *  using all components with the same ratio
     * 
     */
    public static AdjustWater scaleAdjustment(AdjustWater adjustWater, WaterVolume volume) {

        BigDecimal volumeRatio = volume.getSparge().divide(volume.getMash(),mc);
        return scaleAdjustment(adjustWater, volumeRatio);

    }
}
