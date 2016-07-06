/**
 * 
 */
package org.rekhyt.ezwaterlib.model;

import java.math.BigDecimal;

/**
 * @author michele.antonecchia
 * 
 * This class extends 
 * 
 */
public class GrainCrystal extends Grain {

    public GrainCrystal(BigDecimal weight, int color) {
        this.setWeight(weight);
        this.setColor(color);
        this.setCrystal(true);
        
        BigDecimal phBase = new BigDecimal("5.22");
        BigDecimal factor = new BigDecimal("-0.00504");
        BigDecimal phGrain = phBase.add(factor.multiply(new BigDecimal(color)));
        
        this.setDistilledWaterPh(phGrain);

    }
    
    

}
