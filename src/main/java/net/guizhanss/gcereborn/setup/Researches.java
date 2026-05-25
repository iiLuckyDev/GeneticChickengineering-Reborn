package net.guizhanss.gcereborn.setup;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.items.GCEItems;
import net.guizhanss.gcereborn.utils.Keys;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Researches {

    public static final Research CHICKEN_NET = create("chicken_net", 29841, "Chicken Net", 6);
    public static final Research POCKET_CHICKEN = create("pocket_chicken", 29842, "Pocket Chicken", 8);
    public static final Research GENETIC_SEQUENCER = create("genetic_sequencer", 29843, "Genetic Sequencer", 18);
    public static final Research PRIVATE_COOP = create("private_coop", 29844, "Private Coop", 24);
    public static final Research GROWTH_CHAMBER = create("growth_chamber", 29845, "Growth Chamber", 28);
    public static final Research RESTORATION_CHAMBER = create("restoration_chamber", 29846, "Restoration Chamber", 28);
    public static final Research WATER_EGG = create("water_egg", 29847, "Water Egg", 30);
    public static final Research LAVA_EGG = create("lava_egg", 29848, "Lava Egg", 30);
    public static final Research EXCITATION_CHAMBER = create("excitation_chamber", 29849, "Excitation Chamber", 36);
    public static final Research EXCITATION_CHAMBER_2 = create("excitation_chamber_2", 29850, "Boosted Excitation Chamber", 48);
    public static final Research EXCITATION_CHAMBER_3 = create("excitation_chamber_3", 29851, "Ultimate Excitation Chamber", 78);

    public static void setup() {
        register(CHICKEN_NET, GCEItems.CHICKEN_NET);
        register(POCKET_CHICKEN, GCEItems.POCKET_CHICKEN);
        register(GENETIC_SEQUENCER, GCEItems.GENETIC_SEQUENCER);
        register(PRIVATE_COOP, GCEItems.PRIVATE_COOP);
        register(WATER_EGG, GCEItems.WATER_EGG);
        register(LAVA_EGG, GCEItems.LAVA_EGG);
        register(EXCITATION_CHAMBER, GCEItems.EXCITATION_CHAMBER);
        register(EXCITATION_CHAMBER_2, GCEItems.EXCITATION_CHAMBER_2);
        register(EXCITATION_CHAMBER_3, GCEItems.EXCITATION_CHAMBER_3);

        if (GeneticChickengineering.getConfigService().isGrowthChamberEnabled()) {
            register(GROWTH_CHAMBER, GCEItems.GROWTH_CHAMBER);
        }

        if (GeneticChickengineering.getConfigService().isPainEnabled()) {
            register(RESTORATION_CHAMBER, GCEItems.RESTORATION_CHAMBER);
        }
    }

    private static Research create(String key, int id, String name, int cost) {
        return new Research(Keys.get(key), id, name, cost);
    }

    private static void register(Research research, SlimefunItemStack item) {
        SlimefunItem slimefunItem = item.getItem();
        research.addItems(slimefunItem);
        research.register();
    }
}
