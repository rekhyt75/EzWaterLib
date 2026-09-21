/**
 * 
 */
package org.rekhyt.ezwaterlib.model;

import java.math.BigDecimal;

/**
 * @author michele.antonecchia
 *
 */
public class EzWaterResult {
    
    private BigDecimal ph = new BigDecimal("0");
    private ResultWaterProfile mashWater;
    private ResultWaterProfile mashSpargeWater;
    
    
    public BigDecimal getPh() {
        return ph;
    }
    public void setPh(BigDecimal ph) {
        this.ph = ph;
    }
    public ResultWaterProfile getMashWater() {
        return mashWater;
    }
    public void setMashWater(ResultWaterProfile mashWater) {
        this.mashWater = mashWater;
    }
    public ResultWaterProfile getMashSpargeWater() {
        return mashSpargeWater;
    }
    public void setMashSpargeWater(ResultWaterProfile mashSpargeWater) {
        this.mashSpargeWater = mashSpargeWater;
    }
    
    @Override
    public String toString() {
        return "EzWaterResult [ph=" + ph + ", \n\t\tmashWater=" + mashWater + ", mashSpargeWater=" + mashSpargeWater + "]";
    }

}
