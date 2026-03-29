package net.guizhanss.gcereborn.setup;

import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.items.GCEItems;
import net.guizhanss.gcereborn.utils.Keys;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Researches {

    public static final Research ANIMAL_HUSBANDRY = new Research(
        Keys.get("animal_husbandry"),
        29841,
        "Animal Husbandry",
        8
    );

    public static final Research GENETIC_ANALYSIS = new Research(
        Keys.get("genetic_analysis"),
        29842,
        "Genetic Analysis",
        18
    );

    public static final Research SELECTIVE_BREEDING = new Research(
        Keys.get("selective_breeding"),
        29843,
        "Selective Breeding",
        24
    );

    public static final Research RESOURCE_STIMULATION = new Research(
        Keys.get("resource_stimulation"),
        29844,
        "Resource Stimulation",
        30
    );

    public static final Research INDUSTRIAL_STIMULATION = new Research(
        Keys.get("industrial_stimulation"),
        29845,
        "Industrial Stimulation",
        48
    );

    public static final Research NUCLEAR_STIMULATION = new Research(
        Keys.get("nuclear_stimulation"),
        29846,
        "Nuclear Stimulation",
        78
    );

    public static void setup() {
        addItems(
            ANIMAL_HUSBANDRY,
            GCEItems.CHICKEN_NET,
            GCEItems.POCKET_CHICKEN
        );

        addItems(
            GENETIC_ANALYSIS,
            GCEItems.GENETIC_SEQUENCER
        );

        addItems(
            SELECTIVE_BREEDING,
            GCEItems.PRIVATE_COOP
        );

        if (GeneticChickengineering.getConfigService().isGrowthChamberEnabled()) {
            addItems(SELECTIVE_BREEDING, GCEItems.GROWTH_CHAMBER);
        }

        if (GeneticChickengineering.getConfigService().isPainEnabled()) {
            addItems(SELECTIVE_BREEDING, GCEItems.RESTORATION_CHAMBER);
        }

        addItems(
            RESOURCE_STIMULATION,
            GCEItems.WATER_EGG,
            GCEItems.LAVA_EGG,
            GCEItems.EXCITATION_CHAMBER
        );

        addItems(
            INDUSTRIAL_STIMULATION,
            GCEItems.EXCITATION_CHAMBER_2
        );

        addItems(
            NUCLEAR_STIMULATION,
            GCEItems.EXCITATION_CHAMBER_3
        );

        ANIMAL_HUSBANDRY.register();
        GENETIC_ANALYSIS.register();
        SELECTIVE_BREEDING.register();
        RESOURCE_STIMULATION.register();
        INDUSTRIAL_STIMULATION.register();
        NUCLEAR_STIMULATION.register();
    }

    private static void addItems(Research research, SlimefunItemStack... items) {
        SlimefunItem[] slimefunItems = new SlimefunItem[items.length];

        for (int i = 0; i < items.length; i++) {
            slimefunItems[i] = items[i].getItem();
        }

        research.addItems(slimefunItems);
    }
}
