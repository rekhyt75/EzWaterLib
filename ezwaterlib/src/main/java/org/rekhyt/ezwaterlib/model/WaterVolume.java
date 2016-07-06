/**
 * 
 */
package org.rekhyt.ezwaterlib.model;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author michele.antonecchia
 *
 *
 *  This class represent the volume of mash and sparge water
 *  and the correspondi addiction of distilled water (percentage)
 *  
 */
public class WaterVolume {
    

    private BigDecimal mash = new BigDecimal(0);
    private BigDecimal mashDistilledPercentage = new BigDecimal(0);
    private BigDecimal sparge = new BigDecimal(0);
    private BigDecimal spargeDistilledPercentage = new BigDecimal(0);
    
    public WaterVolume(BigDecimal mash, BigDecimal sparge) {
        super();
        this.mash = mash;
        this.sparge = sparge;
    }
    
    public WaterVolume(BigDecimal mash, BigDecimal mashDistilledPercentage, BigDecimal sparge, BigDecimal spargeDistilledPercentage) {
        super();
        this.mash = mash;
        this.mashDistilledPercentage = mashDistilledPercentage;
        this.sparge = sparge;
        this.spargeDistilledPercentage = spargeDistilledPercentage;
    }

    public BigDecimal getMash() {
        return mash;
    }

    public void setMash(BigDecimal mash) {
        this.mash = mash;
    }

    public BigDecimal getMashDistilledPercentage() {
        return mashDistilledPercentage;
    }

    public void setMashDistilledPercentage(BigDecimal mashDistilledPercentage) {
        if(mashDistilledPercentage.compareTo(BigDecimal.ONE) == 1){
            mashDistilledPercentage = BigDecimal.ONE;
        }
        this.mashDistilledPercentage = mashDistilledPercentage;
    }

    public BigDecimal getSparge() {
        return sparge;
    }

    public void setSparge(BigDecimal sparge) {
        this.sparge = sparge;
    }

    public BigDecimal getSpargeDistilledPercentage() {
        return spargeDistilledPercentage;
    }

    public void setSpargeDistilledPercentage(BigDecimal spargeDistilledPercentage) {
        if(spargeDistilledPercentage.compareTo(BigDecimal.ONE) == 1){
            spargeDistilledPercentage = BigDecimal.ONE;
        }
        this.spargeDistilledPercentage = spargeDistilledPercentage;
    }

}
