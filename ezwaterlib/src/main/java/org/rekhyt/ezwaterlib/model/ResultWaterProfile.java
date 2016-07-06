/**
 * 
 */
package org.rekhyt.ezwaterlib.model;

import java.math.BigDecimal;

/**
 * @author michele.antonecchia
 *
 */
public class ResultWaterProfile {
    
    private BigDecimal effectiveAlk = new BigDecimal(0);
    private BigDecimal residualAlk = new BigDecimal(0);

    private BigDecimal calcium = new BigDecimal(0);
    private BigDecimal magnesium = new BigDecimal(0);
    private BigDecimal sodium = new BigDecimal(0);
    private BigDecimal chloride = new BigDecimal(0);
    private BigDecimal sulfate = new BigDecimal(0);
    private BigDecimal chlorideSulfateRatio = new BigDecimal(0);
    
    public BigDecimal getEffectiveAlk() {
        return effectiveAlk;
    }
    public void setEffectiveAlk(BigDecimal effectiveAlk) {
        this.effectiveAlk = effectiveAlk;
    }
    public BigDecimal getResidualAlk() {
        return residualAlk;
    }
    public void setResidualAlk(BigDecimal residualAlk) {
        this.residualAlk = residualAlk;
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
    public BigDecimal getChlorideSulfateRatio() {
        return chlorideSulfateRatio;
    }
    public void setChlorideSulfateRatio(BigDecimal chlorideSulfateRatio) {
        this.chlorideSulfateRatio = chlorideSulfateRatio;
    }

    @Override
    public String toString() {
        return "ResultWaterProfile [\n\t\teffectiveAlk=" + effectiveAlk + ", \n\t\tresidualAlk=" + residualAlk 
                + ", \n\t\tcalcium=" + calcium + ", \n\t\tmagnesium=" + magnesium + ", \n\t\tsodium=" + sodium
                + ", \n\t\tchloride=" + chloride + ", \n\t\tsulfate=" + sulfate + ", \n\t\tchlorideSulfateRatio=" + chlorideSulfateRatio
                + "\n\t\t]";
    }

    
}
