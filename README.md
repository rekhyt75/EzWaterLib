# EzWaterLib

A Brewing Java Library to calculate water characteristics and mash pH (based on [EZ Water Calculator 3.0.2](http://www.ezwatercalculator.com/), metric edition).

Given your source water profile, mash/sparge volumes, grain bill and any brewing-salt additions, `EzWaterLib` computes:

- the resulting mineral profile (calcium, magnesium, sodium, chloride, sulfate) of the mash water and of the mash+sparge water,
- the chloride/sulfate ratio,
- effective and residual alkalinity,
- the estimated mash pH.

## Requirements

- Java 11+
- Maven

## Installation

Build and install the artifact into your local Maven repository:

```bash
cd ezwaterlib
mvn install
```

Then depend on it from another project:

```xml
<dependency>
  <groupId>org.rekhyt</groupId>
  <artifactId>ezwaterlib</artifactId>
  <version>1.0.0-FINAL</version>
</dependency>
```

## Units

All quantities use the metric units of the source spreadsheet:

| Concept | Unit |
|---|---|
| Water volumes (`WaterVolume`) | liters |
| Water ion concentrations (`WaterProfile`, results) | ppm (mg/L) |
| Grain weight (`Grain`) | kilograms |
| Grain color (`GrainCrystal`) | °L (Lovibond) |
| Salt additions (`AdjustWater`, most fields) | grams |
| Lactic acid addition (`AdjustWater.lacticAcid`) | milliliters |
| Percentages (distilled water %, acid content %) | fraction 0–1 (e.g. `0.5` = 50%) |

## Core concepts

- **`WaterProfile`** — the mineral profile of your source water: calcium, magnesium, sodium, chloride, sulfate, bicarbonate, alkalinity (all ppm). Provide either `bicarbonate` or `alkalinity`; if `alkalinity` is greater than zero it takes precedence, otherwise alkalinity is derived from bicarbonate.
- **`WaterVolume`** — mash and sparge volumes (liters), plus the percentage of each that is distilled/RO water (0–1).
- **`Grain` / `GrainNormal` / `GrainCrystal`** — a grain addition. `GrainNormal` takes a weight and a known distilled-water pH for that malt. `GrainCrystal` takes a weight and a color (°L) and derives the distilled-water pH from it.
- **`GrainList`** — the grain bill: a keyed collection of `Grain` entries (`addGrain`/`removeGrain`).
- **`AdjustWater`** — brewing salt/acid additions: `caSO4` (gypsum), `caCl2` (calcium chloride), `mgSO4` (epsom salt), `caCO3` (chalk), `ca_OH_2` (slaked lime), `naHCO3` (baking soda), `acidulatedMalt`, `lacticAcid` — all in grams except `lacticAcid` (ml). `acidulatedMaltContent`/`lacticAcidContent` default to 2% and 88% and can be overridden.
- **`EzWaterCalculator`** — the stateless calculator: `calcEzWater(...)` runs the full calculation, `scaleAdjustment(...)` scales a mash addition to the equivalent sparge addition.
- **`EzWaterResult` / `ResultWaterProfile`** — the output: `getPh()` (estimated mash pH), `getMashWater()` and `getMashSpargeWater()` (each a `ResultWaterProfile` with calcium/magnesium/sodium/chloride/sulfate, chloride/sulfate ratio, effective and residual alkalinity).

> **Note:** effective/residual alkalinity on `getMashSpargeWater()` is a convenience extension of this library — the source spreadsheet only computes alkalinity and pH from the mash water. `getPh()` is always based on the mash water only, consistent with the original calculator.

`calcEzWater` throws `IllegalArgumentException` if the grain list is empty (or its total weight is zero) or if a volume is zero.

## Quick start

Minimal example: no salt additions, just evaluating your source water and grain bill as-is.

```java
import java.math.BigDecimal;
import org.rekhyt.ezwaterlib.EzWaterCalculator;
import org.rekhyt.ezwaterlib.model.*;

// 1. Source water profile (ppm)
WaterProfile sourceWater = new WaterProfile(
        new BigDecimal("82.78"),  // calcium
        new BigDecimal("19.83"),  // magnesium
        new BigDecimal("11.9"),   // sodium
        new BigDecimal("8.6"),    // chloride
        new BigDecimal("6.39"),   // sulfate
        new BigDecimal("382"),    // bicarbonate
        BigDecimal.ZERO);         // alkalinity (0 => derive from bicarbonate)

// 2. Mash / sparge volumes (liters), no distilled water added
WaterVolume volume = new WaterVolume(new BigDecimal("20"), BigDecimal.ZERO,
        new BigDecimal("15"), BigDecimal.ZERO);

// 3. Grain bill (kg)
GrainList grainList = new GrainList();
grainList.addGrain("2-row", new GrainNormal(new BigDecimal("5"), new BigDecimal("5.7")));
grainList.addGrain("crystal-80", new GrainCrystal(new BigDecimal("0.4"), 80));
grainList.addGrain("roasted", new GrainNormal(new BigDecimal("0.2"), new BigDecimal("4.71")));

// 4. No salt additions
AdjustWater mashAdditions = new AdjustWater();
AdjustWater spargeAdditions = new AdjustWater();

EzWaterResult result = EzWaterCalculator.calcEzWater(sourceWater, volume, grainList,
        mashAdditions, spargeAdditions);

System.out.println("Estimated mash pH: " + result.getPh());
System.out.println("Mash water profile: " + result.getMashWater());
```

## Adding brewing salts

Set the amounts (grams) you plan to add to the mash, then scale that same addition to the sparge water proportionally to its volume with `scaleAdjustment`:

```java
AdjustWater mashAdditions = new AdjustWater();
mashAdditions.setCaCl2(new BigDecimal("3"));   // 3 g calcium chloride
mashAdditions.setCaSO4(new BigDecimal("2"));   // 2 g gypsum

// scale the same additions to the sparge water, proportionally to its volume
AdjustWater spargeAdditions = EzWaterCalculator.scaleAdjustment(mashAdditions, volume);

EzWaterResult result = EzWaterCalculator.calcEzWater(sourceWater, volume, grainList,
        mashAdditions, spargeAdditions);

System.out.println("Chloride/Sulfate ratio: "
        + result.getMashWater().getChlorideSulfateRatio());
```

`scaleAdjustment` has two more overloads if you need finer control:

```java
// scale each salt with an explicit ratio (0 excludes that salt from the sparge)
AdjustWater spargeAdditions = EzWaterCalculator.scaleAdjustment(mashAdditions, volumeRatio,
        scaleCaOH2, scaleNaHCO3, scaleCaCO3, scaleCaSO4, scaleCaCl2, scaleMgSO4);

// scale everything by an explicit ratio, computed however you like
AdjustWater spargeAdditions = EzWaterCalculator.scaleAdjustment(mashAdditions, volumeRatio);
```

Note: `acidulatedMalt` and `lacticAcid` are always mash-only additions and are **not** scaled to the sparge water by `scaleAdjustment` — set them directly on `mashAdditions` if needed.

## Adjusting mash pH

```java
AdjustWater mashAdditions = new AdjustWater();
mashAdditions.setLacticAcid(new BigDecimal("2"));       // 2 ml, 88% content by default
mashAdditions.setAcidulatedMalt(new BigDecimal("50"));  // 50 g, 2% acid content by default

AdjustWater spargeAdditions = EzWaterCalculator.scaleAdjustment(mashAdditions, volume);

EzWaterResult result = EzWaterCalculator.calcEzWater(sourceWater, volume, grainList,
        mashAdditions, spargeAdditions);

System.out.println("Estimated mash pH: " + result.getPh());
System.out.println("Residual alkalinity: " + result.getMashWater().getResidualAlk());
```

See [`App.java`](ezwaterlib/src/main/java/org/rekhyt/ezwaterlib/App.java) for a complete runnable example, and [`EzWaterCalculatorTest.java`](ezwaterlib/src/test/java/org/rekhyt/ezwaterlib/EzWaterCalculatorTest.java) for further usage patterns.

## Why no pounds, ounces, or gallons (a true story)

If you're wondering why `EzWaterLib` only accepts liters, kilograms, and grams, and won't touch inches, pounds, or gallons, the short answer is: because on September 23, 1999, NASA lost an entire spacecraft over this exact issue, and we'd rather not risk the same with your brew day.

The spacecraft was the **Mars Climate Orbiter**, which cost about $327.6 million and had just arrived at Mars after a nearly ten-month journey. The navigation software at NASA's Jet Propulsion Laboratory computed everything in metric units (newton-seconds), as specified. The team at Lockheed Martin that wrote the thruster control software, however, supplied it with thrust data in **pound-force-seconds** (imperial units) — without converting them.

Nobody noticed for months. The result: every small trajectory correction computed by the software was off by a factor of 4.45 (the ratio between pound-force and newton). Small errors, compounding orbit after orbit, over the entire cruise to Mars.

On September 23, 1999, the spacecraft entered the Martian atmosphere at roughly 57 km altitude instead of the intended ~140-150 km — far too low to survive the thermal and aerodynamic stresses. Mars Climate Orbiter disintegrated or was lost in orbit, and was never heard from again. A NASA inquiry (the Mishap Investigation Board) officially concluded that the root cause was exactly this: a data file in imperial units fed into a system expecting metric units, with no conversion and no cross-check catching it.

Moral of the story: if a mix-up between pounds and newtons can burn a $327 million spacecraft up in another planet's atmosphere, imagine what mistaking grams of `caCl2` for ounces could do to the pH of your mash. `EzWaterLib` stays metric on principle — one less conversion to get wrong is one more batch of beer that doesn't go to waste.
