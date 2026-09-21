package org.rekhyt.ezwaterlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.rekhyt.ezwaterlib.model.AdjustWater;
import org.rekhyt.ezwaterlib.model.EzWaterResult;
import org.rekhyt.ezwaterlib.model.GrainList;
import org.rekhyt.ezwaterlib.model.GrainNormal;
import org.rekhyt.ezwaterlib.model.ResultWaterProfile;
import org.rekhyt.ezwaterlib.model.WaterProfile;
import org.rekhyt.ezwaterlib.model.WaterVolume;

class EzWaterCalculatorTest {

    // 1 US gallon, expressed in liters, so that dividing by it in the calculator yields exactly 1 gallon.
    private static final BigDecimal ONE_GALLON_IN_LITERS = new BigDecimal("3.785412");
    private static final double DELTA = 0.01;

    private static WaterProfile zeroWaterProfile() {
        return new WaterProfile(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private static GrainList singleGrain() {
        GrainList grainList = new GrainList();
        grainList.addGrain("1", new GrainNormal(BigDecimal.ONE, new BigDecimal("5.0")));
        return grainList;
    }

    private static WaterVolume oneGallonMashOnly() {
        return new WaterVolume(ONE_GALLON_IN_LITERS, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test
    void chlorideComesFromCaCl2NotFromNaHCO3() {
        AdjustWater adjustWater = new AdjustWater();
        adjustWater.setCaCl2(BigDecimal.ONE);

        ResultWaterProfile mashWater = EzWaterCalculator
                .calcEzWater(zeroWaterProfile(), oneGallonMashOnly(), singleGrain(), adjustWater, new AdjustWater())
                .getMashWater();

        assertEquals(127.47, mashWater.getChloride().doubleValue(), DELTA);
        assertEquals(72.0, mashWater.getCalcium().doubleValue(), DELTA);
        assertEquals(0.0, mashWater.getMagnesium().doubleValue(), DELTA);
        assertEquals(0.0, mashWater.getSodium().doubleValue(), DELTA);
    }

    @Test
    void naHCO3DoesNotAffectChloride() {
        AdjustWater adjustWater = new AdjustWater();
        adjustWater.setNaHCO3(BigDecimal.ONE);

        ResultWaterProfile mashWater = EzWaterCalculator
                .calcEzWater(zeroWaterProfile(), oneGallonMashOnly(), singleGrain(), adjustWater, new AdjustWater())
                .getMashWater();

        assertEquals(0.0, mashWater.getChloride().doubleValue(), DELTA);
        assertEquals(72.3, mashWater.getSodium().doubleValue(), DELTA);
    }

    @Test
    void magnesiumAndSulfateComeFromMgSO4() {
        AdjustWater adjustWater = new AdjustWater();
        adjustWater.setMgSO4(BigDecimal.ONE);

        ResultWaterProfile mashWater = EzWaterCalculator
                .calcEzWater(zeroWaterProfile(), oneGallonMashOnly(), singleGrain(), adjustWater, new AdjustWater())
                .getMashWater();

        assertEquals(24.6, mashWater.getMagnesium().doubleValue(), DELTA);
        assertEquals(103.0, mashWater.getSulfate().doubleValue(), DELTA);
    }

    @Test
    void calciumAndSulfateComeFromCaSO4() {
        AdjustWater adjustWater = new AdjustWater();
        adjustWater.setCaSO4(BigDecimal.ONE);

        ResultWaterProfile mashWater = EzWaterCalculator
                .calcEzWater(zeroWaterProfile(), oneGallonMashOnly(), singleGrain(), adjustWater, new AdjustWater())
                .getMashWater();

        assertEquals(60.0, mashWater.getCalcium().doubleValue(), DELTA);
        assertEquals(147.4, mashWater.getSulfate().doubleValue(), DELTA);
    }

    @Test
    void chlorideSulfateRatioIsZeroWhenSulfateIsZero() {
        AdjustWater adjustWater = new AdjustWater();
        adjustWater.setCaCl2(BigDecimal.ONE); // chloride > 0, sulfate == 0

        ResultWaterProfile mashWater = EzWaterCalculator
                .calcEzWater(zeroWaterProfile(), oneGallonMashOnly(), singleGrain(), adjustWater, new AdjustWater())
                .getMashWater();

        assertEquals(0.0, mashWater.getChlorideSulfateRatio().doubleValue(), DELTA);
    }

    @Test
    void chlorideSulfateRatioIsComputedWhenSulfateIsPositive() {
        AdjustWater adjustWater = new AdjustWater();
        adjustWater.setCaCl2(BigDecimal.ONE);
        adjustWater.setCaSO4(BigDecimal.ONE);

        ResultWaterProfile mashWater = EzWaterCalculator
                .calcEzWater(zeroWaterProfile(), oneGallonMashOnly(), singleGrain(), adjustWater, new AdjustWater())
                .getMashWater();

        // chloride = 127.47, sulfate = 60(Ca) contribution is calcium not sulfate; sulfate here = 147.4 (from CaSO4)
        assertEquals(127.47 / 147.4, mashWater.getChlorideSulfateRatio().doubleValue(), 0.001);
    }

    @Test
    void calcEzWaterDoesNotMutateInputAdjustWaterObjects() {
        WaterVolume volume = new WaterVolume(ONE_GALLON_IN_LITERS, BigDecimal.ZERO, ONE_GALLON_IN_LITERS,
                BigDecimal.ZERO);

        AdjustWater adjustWaterMash = new AdjustWater();
        adjustWaterMash.setCaCl2(BigDecimal.ONE);

        AdjustWater adjustWaterSparge = new AdjustWater();
        adjustWaterSparge.setCaCl2(BigDecimal.ONE);

        EzWaterCalculator.calcEzWater(zeroWaterProfile(), volume, singleGrain(), adjustWaterMash, adjustWaterSparge);

        assertEquals(0, BigDecimal.ONE.compareTo(adjustWaterMash.getCaCl2()),
                "adjustWaterMash must not be mutated by calcEzWater");
        assertEquals(0, BigDecimal.ONE.compareTo(adjustWaterSparge.getCaCl2()),
                "adjustWaterSparge must not be mutated by calcEzWater");
    }

    @Test
    void calcEzWaterThrowsOnEmptyGrainList() {
        assertThrows(IllegalArgumentException.class, () -> EzWaterCalculator.calcEzWater(zeroWaterProfile(),
                oneGallonMashOnly(), new GrainList(), new AdjustWater(), new AdjustWater()));
    }

    @Test
    void calcEzWaterThrowsOnZeroVolume() {
        WaterVolume zeroVolume = new WaterVolume(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class, () -> EzWaterCalculator.calcEzWater(zeroWaterProfile(),
                zeroVolume, singleGrain(), new AdjustWater(), new AdjustWater()));
    }

    @Test
    void scaleAdjustmentAppliesVolumeRatioToEachComponent() {
        AdjustWater adjustWater = new AdjustWater();
        adjustWater.setCaCl2(new BigDecimal("2"));

        WaterVolume volume = new WaterVolume(new BigDecimal("10"), BigDecimal.ZERO, new BigDecimal("5"),
                BigDecimal.ZERO);

        AdjustWater scaled = EzWaterCalculator.scaleAdjustment(adjustWater, volume);

        assertEquals(1.0, scaled.getCaCl2().doubleValue(), DELTA); // 2 * (5/10)
    }

    @Test
    void endToEndCalcEzWaterReturnsNonNullResult() {
        AdjustWater adjustWaterMash = new AdjustWater();
        adjustWaterMash.setCaCl2(BigDecimal.ONE);
        adjustWaterMash.setCaSO4(BigDecimal.ONE);
        adjustWaterMash.setNaHCO3(BigDecimal.ONE);

        WaterVolume volume = new WaterVolume(new BigDecimal("20"), BigDecimal.ZERO, new BigDecimal("15"),
                BigDecimal.ZERO);
        AdjustWater adjustWaterSparge = EzWaterCalculator.scaleAdjustment(adjustWaterMash, volume);

        EzWaterResult result = EzWaterCalculator.calcEzWater(zeroWaterProfile(), volume, singleGrain(),
                adjustWaterMash, adjustWaterSparge);

        assertEquals(0, BigDecimal.ONE.compareTo(adjustWaterMash.getCaCl2()), "input must not be mutated");
        assertTrue(result.getMashWater().getChloride().doubleValue() > 0);
        assertTrue(result.getMashSpargeWater().getChloride().doubleValue() > 0);
    }
}
