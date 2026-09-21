/**
 * 
 */
package org.rekhyt.ezwaterlib.model;

import java.math.BigDecimal;

/**
 * @author michele.antonecchia
 *
 */
public class GrainNormal extends Grain {
    
      public GrainNormal(BigDecimal weight, BigDecimal distilledWaterPh) {
          
          this.setWeight(weight);
          this.setDistilledWaterPh(distilledWaterPh);
          this.setCrystal(false);
          this.setColor(0);

    }

}
