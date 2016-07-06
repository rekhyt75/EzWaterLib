package org.rekhyt.ezwaterlib;

import java.math.BigDecimal;

import org.rekhyt.ezwaterlib.model.AdjustWater;
import org.rekhyt.ezwaterlib.model.EzWaterResult;
import org.rekhyt.ezwaterlib.model.GrainCrystal;
import org.rekhyt.ezwaterlib.model.GrainList;
import org.rekhyt.ezwaterlib.model.GrainNormal;
import org.rekhyt.ezwaterlib.model.ResultWaterProfile;
import org.rekhyt.ezwaterlib.model.WaterProfile;
import org.rekhyt.ezwaterlib.model.WaterVolume;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args )
    {
        // CREATE WATER PROFILE
        BigDecimal calcium = new BigDecimal("82.78");
        BigDecimal magnesium = new BigDecimal("19.83");
        BigDecimal sodium = new BigDecimal("11.9");
        BigDecimal chloride = new BigDecimal("8.6");
        BigDecimal sulfate = new BigDecimal("6.39");
        BigDecimal bicarbonate = new BigDecimal("382");
        BigDecimal alkalinity = new BigDecimal(0);
        
        WaterProfile initialWater = new WaterProfile(calcium, magnesium, sodium, chloride, sulfate, bicarbonate, alkalinity);
        
        // Define Water volumes
        WaterVolume volume = new WaterVolume(new BigDecimal(20), BigDecimal.ZERO, new BigDecimal(15), BigDecimal.ZERO);
        
        // Add Grains
        GrainNormal g1 = new GrainNormal(new BigDecimal("5"), new BigDecimal("5.7"));
        GrainCrystal g2 = new GrainCrystal(new BigDecimal("0.4"), 80);
        GrainNormal g3 = new GrainNormal(new BigDecimal("0.2"), new BigDecimal("4.71"));
        
        GrainList grainList = new GrainList();
        grainList.addGrain("1", g1);
        grainList.addGrain("2", g2);
        grainList.addGrain("3", g3);

        /*
         *  start EX calculator
         *  
         *  Calculate initial values without adjustments
         *  
         */
        
        // Set initial water adjust (all 0) for mash
        AdjustWater adjustWaterMash = new AdjustWater(); // default all 0 = use base water        
        // Set initial water adjust for sparge scaling upon mash 
        AdjustWater adjustWaterSparge = EzWaterCalculator.scaleAdjustment(adjustWaterMash, volume);
        
        EzWaterResult ezWaterResult = EzWaterCalculator.calcEzWater(initialWater, volume, grainList, adjustWaterMash, adjustWaterSparge);
        
        System.out.println("Mash water: " + ezWaterResult);
        
        /*
         * Start correction with ingredients 
         */
  
        // adding ingredients to mash water
        adjustWaterMash.setCa_OH_2(new BigDecimal("1"));
        adjustWaterMash.setCaCO3(new BigDecimal("1"));
        adjustWaterMash.setCaCl2(new BigDecimal("1"));
        adjustWaterMash.setCaSO4(new BigDecimal("1"));
        adjustWaterMash.setMgSO4(new BigDecimal("1"));
        adjustWaterMash.setNaHCO3(new BigDecimal("1"));
        adjustWaterMash.setAcidulatedMalt(new BigDecimal("1"));
        adjustWaterMash.setLacticAcid(new BigDecimal("1"));
        // scaling sparge ingredients (using all the same used into mash)
        adjustWaterSparge = EzWaterCalculator.scaleAdjustment(adjustWaterMash, volume);
        
        ezWaterResult = EzWaterCalculator.calcEzWater(initialWater, volume, grainList, adjustWaterMash, adjustWaterSparge);
        
        System.out.println("Mash water after correction: " + ezWaterResult);
    }
}
