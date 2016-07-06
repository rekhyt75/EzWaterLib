/**
 * 
 */
package org.rekhyt.ezwaterlib;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;

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

    private static final BigDecimal PH_SPECIFIC = new BigDecimal("0.0130011821"); // 0.1085
                                                                                  // for
                                                                                  // imperial
                                                                                  // (gallons
                                                                                  // /
                                                                                  // lbs)

    public static EzWaterResult calcEzWater(WaterProfile initialWater, WaterVolume volume, GrainList grainList,
            AdjustWater adjustWaterMash, AdjustWater adjustWaterSparge) {

        EzWaterResult ezWaterResult = new EzWaterResult();

        HashMap<String, Grain> grainMap = grainList.getGrainList();

        ResultWaterProfile mashWater = calculateResultWaterProfile(initialWater, volume.getMash(),
                volume.getMashDistilledPercentage(), adjustWaterMash);
        ezWaterResult.setMashWater(mashWater);

        // Residual Alkalinity
        BigDecimal residualAlk = mashWater.getResidualAlk();

        // calculate PH
        BigDecimal totalWeight = new BigDecimal(0, mc);
        BigDecimal grainPh = new BigDecimal(0, mc);
        for (String key : grainMap.keySet()) {
            Grain grain = grainMap.get(key);
            totalWeight = totalWeight.add(grain.getWeight());
            grainPh = grainPh.add(grain.getWeight().multiply(grain.getDistilledWaterPh()));
        }
        grainPh = grainPh.divide(totalWeight, mc);

        BigDecimal ph = volume.getMash().divide(totalWeight, mc).multiply(PH_SPECIFIC, mc)
                .add(new BigDecimal("0.013", mc));
        ph = ph.multiply(residualAlk.divide(new BigDecimal("50")), mc);
        ph = ph.add(grainPh, mc);

        ezWaterResult.setPh(ph);
        
        // calculate distilled percentage
        BigDecimal distilled = volume.getMashDistilledPercentage().multiply(volume.getMash(), mc);
        distilled = distilled.add(volume.getSpargeDistilledPercentage().multiply(volume.getSparge(), mc));
        distilled = distilled.divide(volume.getMash().add(volume.getSparge()));
        
        // calculate total ingredients
        AdjustWater adjustWaterMashSparge = calcTotalAdjustment(adjustWaterMash, adjustWaterSparge);
        
        ResultWaterProfile mashSpargeWater = calculateResultWaterProfile(initialWater, volume.getSparge().add(volume.getMash()),
                distilled, adjustWaterMashSparge);
        
        ezWaterResult.setMashSpargeWater(mashSpargeWater);

        return ezWaterResult;
    }

    private static AdjustWater calcTotalAdjustment(AdjustWater adjustWaterMash, AdjustWater adjustWaterSparge) {

        adjustWaterMash.setCa_OH_2(adjustWaterMash.getCa_OH_2().add(adjustWaterSparge.getCa_OH_2()));
        adjustWaterMash.setCaCl2(adjustWaterMash.getCaCl2().add(adjustWaterSparge.getCaCl2()));
        adjustWaterMash.setCaCO3(adjustWaterMash.getCaCO3().add(adjustWaterSparge.getCaCO3()));
        adjustWaterMash.setCaSO4(adjustWaterMash.getCaSO4().add(adjustWaterSparge.getCaSO4()));
        adjustWaterMash.setMgSO4(adjustWaterMash.getMgSO4().add(adjustWaterSparge.getMgSO4()));
        adjustWaterMash.setNaHCO3(adjustWaterMash.getNaHCO3().add(adjustWaterSparge.getNaHCO3()));
        
        return adjustWaterMash;
    }

    private static ResultWaterProfile calculateResultWaterProfile(WaterProfile initialWater, BigDecimal volume,
            BigDecimal distilledPercentage, AdjustWater adjustWaterMash) {

        ResultWaterProfile resultWaterProfile = new ResultWaterProfile();

        // Calculate minerals
        BigDecimal gallons = volume.divide(new BigDecimal("3.785412"), mc);

        // - Calcium
        BigDecimal calcium = new BigDecimal(0, mc);
        calcium = calcium.add(adjustWaterMash.getCaCO3().multiply(new BigDecimal("105.89"), mc));
        calcium = calcium.add(adjustWaterMash.getCaSO4().multiply(new BigDecimal("60"), mc));
        calcium = calcium.add(adjustWaterMash.getCaCl2().multiply(new BigDecimal("72"), mc));
        calcium = calcium.add(adjustWaterMash.getCa_OH_2().multiply(new BigDecimal("143"), mc));
        calcium = calcium.divide(gallons, mc); // Expression in gallons
        calcium = calcium
                .add((BigDecimal.ONE.subtract(distilledPercentage, mc)).multiply(initialWater.getCalcium(), mc));
        resultWaterProfile.setCalcium(calcium);

        // - Magnesium
        BigDecimal magnesium = new BigDecimal(0, mc);
        magnesium = magnesium.add(adjustWaterMash.getMgSO4().multiply(new BigDecimal("24.6"), mc));
        magnesium = magnesium.divide(gallons, mc); // Expression in gallons
        magnesium = magnesium
                .add((BigDecimal.ONE.subtract(distilledPercentage, mc)).multiply(initialWater.getMagnesium(), mc));
        resultWaterProfile.setMagnesium(magnesium);

        // - Sodium
        BigDecimal sodium = new BigDecimal(0, mc);
        sodium = sodium.add(adjustWaterMash.getNaHCO3().multiply(new BigDecimal("72.3"), mc));
        sodium = sodium.divide(gallons, mc); // Expression in gallons
        sodium = sodium.add((BigDecimal.ONE.subtract(distilledPercentage, mc)).multiply(initialWater.getSodium(), mc));
        resultWaterProfile.setSodium(sodium);

        // - Chloride
        BigDecimal chloride = new BigDecimal(0, mc);
        chloride = chloride.add(adjustWaterMash.getNaHCO3().multiply(new BigDecimal("127.47"), mc));
        chloride = chloride.divide(gallons, mc); // Expression in gallons
        chloride = chloride
                .add((BigDecimal.ONE.subtract(distilledPercentage, mc)).multiply(initialWater.getChloride(), mc));
        resultWaterProfile.setChloride(chloride);

        // - Sulfate
        BigDecimal sulfate = new BigDecimal(0, mc);
        sulfate = sulfate.add(adjustWaterMash.getCaSO4().multiply(new BigDecimal("147.4"), mc));
        sulfate = sulfate.add(adjustWaterMash.getMgSO4().multiply(new BigDecimal("103"), mc));
        sulfate = sulfate.divide(gallons, mc); // Expression in gallons
        sulfate = sulfate
                .add((BigDecimal.ONE.subtract(distilledPercentage, mc)).multiply(initialWater.getSulfate(), mc));
        resultWaterProfile.setSulfate(sulfate);

        // - Chloride / Sulfate Ratio
        resultWaterProfile.setChlorideSulfateRatio(chloride.divide(sulfate, mc));

        // Claculate alkalinity

        // - Effective Alkalinity
        BigDecimal alk = new BigDecimal("0");
        if (initialWater.getAlkalinity().compareTo(BigDecimal.ZERO) == 1) {
            // alkalinity value wins over Bicarbonates
            alk = initialWater.getAlkalinity();
        } else {
            // estimate alkalinity from bicarbonates
            alk = initialWater.getBicarbonate().multiply(new BigDecimal("50"), mc).divide(new BigDecimal("61"), mc);
        }

        BigDecimal effectiveAlk = new BigDecimal(0, mc);
        effectiveAlk = effectiveAlk.add(adjustWaterMash.getCaCO3().multiply(new BigDecimal("130"), mc));
        effectiveAlk = effectiveAlk.add(adjustWaterMash.getNaHCO3().multiply(new BigDecimal("157"), mc));
        effectiveAlk = effectiveAlk.add(adjustWaterMash.getCa_OH_2().multiply(new BigDecimal("357"), mc));
        effectiveAlk = effectiveAlk.subtract(adjustWaterMash.getLacticAcid()
                .multiply(adjustWaterMash.getLacticAcidContent(), mc).multiply(new BigDecimal("352.2"), mc));
        effectiveAlk = effectiveAlk
                .subtract(adjustWaterMash.getAcidulatedMalt().multiply(adjustWaterMash.getAcidulatedMaltContent(), mc)
                        .multiply(new BigDecimal("10401"), mc).divide(new BigDecimal("28.34952"), mc));
        effectiveAlk = effectiveAlk.divide(gallons, mc); // Expression in
                                                         // gallons
        effectiveAlk = effectiveAlk.add((BigDecimal.ONE.subtract(distilledPercentage, mc)).multiply(alk, mc));
        resultWaterProfile.setEffectiveAlk(effectiveAlk);

        // - Residual Alkalinity
        BigDecimal residualAlk = new BigDecimal(0, mc);
        residualAlk = effectiveAlk.subtract(calcium.divide(new BigDecimal("1.4"), mc));
        residualAlk = residualAlk.subtract(magnesium.divide(new BigDecimal("1.7"), mc));
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
