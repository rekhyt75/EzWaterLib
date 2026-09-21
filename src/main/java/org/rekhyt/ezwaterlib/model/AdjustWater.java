/**
 * 
 */
package org.rekhyt.ezwaterlib.model;

import java.math.BigDecimal;

/**
 * @author michele.antonecchia
 *
 *  Define the list of ingredients:
 *  caSO4 - grams
 *  caCl2 - grams
 *  mgSO4 - grams
 *  acidulatedMalt - grams
 *  lacticAcid - ml
 *  
 */
public class AdjustWater {
    
    //PH Down
    private BigDecimal caSO4 = new BigDecimal(0); // Gypsum  
    private BigDecimal caCl2 = new BigDecimal(0); // Calc. Chloride
    private BigDecimal mgSO4 = new BigDecimal(0); // Epsom Salt
    private BigDecimal acidulatedMalt = new BigDecimal(0);
    private BigDecimal acidulatedMaltContent = new BigDecimal("0.02");  // TODO: set to properties - default 2%
    private BigDecimal lacticAcid = new BigDecimal(0);
    private BigDecimal lacticAcidContent = new BigDecimal("0.88"); // TODO: set to properties - default 88%
    
    //PH UP
    private BigDecimal ca_OH_2 = new BigDecimal(0); // Slaked Lime
    private BigDecimal naHCO3 = new BigDecimal(0); // Baking Soda
    private BigDecimal caCO3 = new BigDecimal(0); // Chalk
    
    public BigDecimal getCaSO4() {
        return caSO4;
    }
    public void setCaSO4(BigDecimal caSO4) {
        this.caSO4 = caSO4;
    }
    public BigDecimal getCaCl2() {
        return caCl2;
    }
    public void setCaCl2(BigDecimal caCl2) {
        this.caCl2 = caCl2;
    }
    public BigDecimal getMgSO4() {
        return mgSO4;
    }
    public void setMgSO4(BigDecimal mgSO4) {
        this.mgSO4 = mgSO4;
    }
    public BigDecimal getAcidulatedMalt() {
        return acidulatedMalt;
    }
    public void setAcidulatedMalt(BigDecimal acidulatedMalt) {
        this.acidulatedMalt = acidulatedMalt;
    }
    public BigDecimal getAcidulatedMaltContent() {
        return acidulatedMaltContent;
    }
    public void setAcidulatedMaltContent(BigDecimal acidulatedMaltContent) {
        this.acidulatedMaltContent = acidulatedMaltContent;
    }
    public BigDecimal getLacticAcid() {
        return lacticAcid;
    }
    public void setLacticAcid(BigDecimal lacticAcid) {
        this.lacticAcid = lacticAcid;
    }
    public BigDecimal getLacticAcidContent() {
        return lacticAcidContent;
    }
    public void setLacticAcidContent(BigDecimal lacticAcidContent) {
        this.lacticAcidContent = lacticAcidContent;
    }
    public BigDecimal getCa_OH_2() {
        return ca_OH_2;
    }
    public void setCa_OH_2(BigDecimal ca_OH_2) {
        this.ca_OH_2 = ca_OH_2;
    }
    public BigDecimal getNaHCO3() {
        return naHCO3;
    }
    public void setNaHCO3(BigDecimal naHCO3) {
        this.naHCO3 = naHCO3;
    }
    public BigDecimal getCaCO3() {
        return caCO3;
    }
    public void setCaCO3(BigDecimal caCO3) {
        this.caCO3 = caCO3;
    }
    
    
 

}
