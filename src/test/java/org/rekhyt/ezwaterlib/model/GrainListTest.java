package org.rekhyt.ezwaterlib.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;

class GrainListTest {

    @Test
    void addAndRemoveGrainByKey() {
        GrainList grainList = new GrainList();
        grainList.addGrain("1", new GrainNormal(BigDecimal.ONE, BigDecimal.ONE));
        grainList.addGrain("2", new GrainNormal(BigDecimal.ONE, BigDecimal.ONE));

        grainList.removeGrain("1");

        assertEquals(1, grainList.getGrainList().size());
        assertFalse(grainList.getGrainList().containsKey("1"));
    }

    @Test
    void getGrainListReturnsAnUnmodifiableView() {
        GrainList grainList = new GrainList();
        grainList.addGrain("1", new GrainNormal(BigDecimal.ONE, BigDecimal.ONE));

        Map<String, Grain> grainMap = grainList.getGrainList();

        assertThrows(UnsupportedOperationException.class,
                () -> grainMap.put("2", new GrainNormal(BigDecimal.ONE, BigDecimal.ONE)));
    }
}
