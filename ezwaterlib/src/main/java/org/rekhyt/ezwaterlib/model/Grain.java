/**
 * 
 */
package org.rekhyt.ezwaterlib.model;

import java.math.BigDecimal;

/**
 * @author michele.antonecchia
 *
 * Color is used to calculate acid. So it is used only if isCrystal is true
 * 
 */
public class Grain {
    
    private BigDecimal weight = new BigDecimal(0);
    private int color = 0;
    private boolean isCrystal = false;
    private BigDecimal distilledWaterPh = new BigDecimal(0);



    
    public BigDecimal getWeight() {
        return weight;
    }
    
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    
    public int getColor() {
        return color;
    }
    
    public void setColor(int color) {
        this.color = color;
    }
    
    public boolean isCrystal() {
        return isCrystal;
    }
    
    public void setCrystal(boolean isCrystal) {
        this.isCrystal = isCrystal;
    }

    public BigDecimal getDistilledWaterPh() {
        return distilledWaterPh;
    }

    public void setDistilledWaterPh(BigDecimal distilledWaterPh) {
        this.distilledWaterPh = distilledWaterPh;
    } 
    
    

}
