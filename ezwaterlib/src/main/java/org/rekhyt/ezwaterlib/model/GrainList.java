/**
 * 
 */
package org.rekhyt.ezwaterlib.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author michele.antonecchia
 *
 */
public class GrainList {
    
    private HashMap<String, Grain> grainList = new HashMap<String, Grain>();
    
    public void addGrain(String key, Grain grain){
        grainList.put(key, grain);
    }
    
    public void removeGrain(String key){
        for(Iterator<Entry<String, Grain>> it = grainList.entrySet().iterator(); it.hasNext(); ) {
            Entry<String, Grain> entry = it.next();
            if(entry.getKey().equals(key)) {
              it.remove();
            }
        }
    }

    public HashMap<String, Grain> getGrainList() {
        return grainList;
    }


}
