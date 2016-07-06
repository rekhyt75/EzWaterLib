/**
 * 
 */
package org.rekhyt.ezwaterlib.model;

import java.math.BigDecimal;

/**
 * @author michele.antonecchia
 * 
 * The WaterProfile class represent the water profile of the water 
 *
 */
public class WaterProfile {
    
    private BigDecimal calcium = new BigDecimal(0);
    private BigDecimal magnesium = new BigDecimal(0);
    private BigDecimal sodium = new BigDecimal(0);
    private BigDecimal chloride = new BigDecimal(0);
    private BigDecimal sulfate = new BigDecimal(0);
    private BigDecimal bicarbonate = new BigDecimal(0);
    private BigDecimal alkalinity = new BigDecimal(0);
    
    private BigDecimal ph = new BigDecimal(0);
    
    
    public WaterProfile(BigDecimal calcium, BigDecimal magnesium, BigDecimal sodium, BigDecimal chloride,
            BigDecimal sulfate, BigDecimal bicarbonate, BigDecimal alkalinity) {
        
        this.calcium = calcium;
        this.magnesium = magnesium;
        this.sodium = sodium;
        this.chloride = chloride;
        this.sulfate = sulfate;
        this.bicarbonate = bicarbonate;
        this.alkalinity = alkalinity;
    }
    

    
    public BigDecimal getCalcium() {
        return calcium;
    }
    public void setCalcium(BigDecimal calcium) {
        this.calcium = calcium;
    }
    public BigDecimal getMagnesium() {
        return magnesium;
    }
    public void setMagnesium(BigDecimal magnesium) {
        this.magnesium = magnesium;
    }
    public BigDecimal getSodium() {
        return sodium;
    }
    public void setSodium(BigDecimal sodium) {
        this.sodium = sodium;
    }
    public BigDecimal getChloride() {
        return chloride;
    }
    public void setChloride(BigDecimal chloride) {
        this.chloride = chloride;
    }
    public BigDecimal getSulfate() {
        return sulfate;
    }
    public void setSulfate(BigDecimal sulfate) {
        this.sulfate = sulfate;
    }
    public BigDecimal getBicarbonate() {
        return bicarbonate;
    }
    public void setBicarbonate(BigDecimal bicarbonate) {
        this.bicarbonate = bicarbonate;
    }
    public BigDecimal getAlkalinity() {
        return alkalinity;
    }
    public void setAlkalinity(BigDecimal alkalinity) {
        this.alkalinity = alkalinity;
    }
    public BigDecimal getPh() {
        return ph;
    }
    public void setPh(BigDecimal ph) {
        this.ph = ph;
    }

}
