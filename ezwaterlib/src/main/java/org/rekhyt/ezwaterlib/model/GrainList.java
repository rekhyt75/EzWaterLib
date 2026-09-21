/**
 * 
 */
package org.rekhyt.ezwaterlib.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        grainList.remove(key);
    }

    public Map<String, Grain> getGrainList() {
        return Collections.unmodifiableMap(grainList);
    }


}
