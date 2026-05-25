package net.guizhanss.gcereborn.core.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.bukkit.configuration.file.YamlConfiguration;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.guizhanlib.slimefun.addon.AddonConfig;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
public final class ConfigurationService {

    private static final String CONFIG_FILE_NAME = "config.yml";
    private static final String CONFIG_VERSION_KEY = "config-version";
    private static final int CURRENT_CONFIG_VERSION = 5;
    private static final DateTimeFormatter BACKUP_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String CHICKEN_PRODUCTION_PATH = "chickens.production";

    @Getter(AccessLevel.NONE)
    private final AddonConfig config;

    private boolean autoUpdate;
    private boolean debug;
    private boolean test;
    private String lang;
    private boolean displayResources;
    private int maxMutation;
    private int mutationRate;
    private int resourceFailRate;
    private int resourceBaseTime;
    private boolean painEnabled;
    private double painChance;
    private boolean painDeathEnabled;
    private int healRate;
    private boolean netherWaterEnabled;
    private boolean growthChamberEnabled;
    private int growthChamberTime;
    private boolean researchesEnabled;
    private boolean commandsEnabled;

    public ConfigurationService(GeneticChickengineering plugin) {
        config = new AddonConfig(plugin, CONFIG_FILE_NAME);
        reload();
    }

    public static void prepareConfig(@Nonnull GeneticChickengineering plugin) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new IllegalStateException("Failed to create plugin data folder");
        }

        File configFile = new File(dataFolder, CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            plugin.saveResource(CONFIG_FILE_NAME, false);
            return;
        }

        int installedVersion = readConfigVersion(configFile);
        if (installedVersion == CURRENT_CONFIG_VERSION) {
            return;
        }

        File backupFile = createBackupFile(dataFolder, installedVersion);

        try {
            Files.createDirectories(backupFile.getParentFile().toPath());
            Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to back up config.yml before updating it", e);
        }

        plugin.saveResource(CONFIG_FILE_NAME, true);
        plugin.getLogger().log(
            Level.INFO,
            "Updated config.yml from version {0} to version {1}. Backup saved to {2}.",
            new Object[] { formatVersion(installedVersion), CURRENT_CONFIG_VERSION, backupFile.getPath() }
        );
    }

    public void reload() {
        config.reload();
        config.addMissingKeys();
        removeUnusedKeys();
        config.set(CONFIG_VERSION_KEY, CURRENT_CONFIG_VERSION);

        autoUpdate = config.getBoolean("options.auto-update", true);
        debug = config.getBoolean("options.debug", false);
        test = config.getBoolean("options.test", false);
        lang = config.getString("options.lang", "en-US");
        displayResources = config.getBoolean("options.display-resource-in-name", true);
        maxMutation = config.getInt("options.max-mutation", 1, 2, 6);
        mutationRate = config.getInt("options.mutation-rate", 1, 30, 100);
        resourceFailRate = config.getInt("options.resource-fail-rate", 0, 0, 100);
        resourceBaseTime = config.getInt("options.resource-base-time", 14, 14, 100);
        painEnabled = config.getBoolean("options.enable-pain", false);
        painChance = config.getDouble("options.pain-chance", 0d, 2d, 100d);
        painDeathEnabled = config.getBoolean("options.pain-kills", false);
        healRate = config.getInt("options.heal-rate", 1, 2, 120);
        netherWaterEnabled = config.getBoolean("options.allow-nether-water", false);
        growthChamberEnabled = config.getBoolean("options.enable-growth-chamber", false);
        growthChamberTime = config.getInt("options.growth-chamber-time", 1, 60, 600);
        researchesEnabled = config.getBoolean("researches.enabled", true);
        commandsEnabled = config.getBoolean("commands.enabled", true);

        config.save();
    }

    public void reloadRuntimeProductionSettings() {
        config.reload();
        config.addMissingKeys();
        config.set(CONFIG_VERSION_KEY, CURRENT_CONFIG_VERSION);

        resourceFailRate = config.getInt("options.resource-fail-rate", 0, 0, 100);
        resourceBaseTime = config.getInt("options.resource-base-time", 14, 14, 100);

        config.save();
    }

    public boolean isSubCommandEnabled(@Nonnull String subCommand) {
        return config.getBoolean("commands.subcommands." + subCommand + ".enabled", true);
    }

    public boolean isChickenProductionEnabled(@Nonnull String chickenKey) {
        String path = chickenPath(chickenKey) + ".enabled";
        return !config.contains(path) || config.getBoolean(path, true);
    }

    public int getChickenOutputAmount(@Nonnull String chickenKey) {
        String path = chickenPath(chickenKey) + ".output-amount";
        if (!config.contains(path)) {
            return 1;
        }

        return Math.max(1, Math.min(64, config.getInt(path, 1)));
    }

    public int getChickenFailRate(@Nonnull String chickenKey) {
        String path = chickenPath(chickenKey) + ".fail-rate";
        if (!config.contains(path)) {
            return resourceFailRate;
        }

        return Math.max(0, Math.min(100, config.getInt(path, resourceFailRate)));
    }

    public int getChickenExtraTimeSeconds(@Nonnull String chickenKey) {
        String path = chickenPath(chickenKey) + ".extra-time-seconds";
        if (!config.contains(path)) {
            return 0;
        }

        return Math.max(0, config.getInt(path, 0));
    }

    public double getChickenTimeMultiplier(@Nonnull String chickenKey) {
        String path = chickenPath(chickenKey) + ".time-multiplier";
        if (!config.contains(path)) {
            return 1.0d;
        }

        double value = config.getDouble(path, 1.0d);
        if (value <= 0d) {
            return 1.0d;
        }

        return value;
    }

    public void setChickenProductionEnabled(@Nonnull String chickenKey, boolean enabled) {
        setChickenProductionValue(chickenKey, "enabled", enabled);
    }

    public void setChickenOutputAmount(@Nonnull String chickenKey, int amount) {
        setChickenProductionValue(chickenKey, "output-amount", Math.max(1, Math.min(64, amount)));
    }

    public void setChickenFailRate(@Nonnull String chickenKey, int failRate) {
        setChickenProductionValue(chickenKey, "fail-rate", Math.max(0, Math.min(100, failRate)));
    }

    public void setChickenExtraTimeSeconds(@Nonnull String chickenKey, int seconds) {
        setChickenProductionValue(chickenKey, "extra-time-seconds", Math.max(0, Math.min(3600, seconds)));
    }

    public void setChickenTimeMultiplier(@Nonnull String chickenKey, double multiplier) {
        double clamped = Math.max(0.1d, Math.min(100.0d, multiplier));
        setChickenProductionValue(chickenKey, "time-multiplier", Math.round(clamped * 100.0d) / 100.0d);
    }

    private static int readConfigVersion(@Nonnull File configFile) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(configFile);
        return yaml.getInt(CONFIG_VERSION_KEY, -1);
    }

    @Nonnull
    private static File createBackupFile(@Nonnull File dataFolder, int installedVersion) {
        File backupDirectory = new File(dataFolder, "config-backups");
        String version = formatVersion(installedVersion);
        String timestamp = LocalDateTime.now().format(BACKUP_TIME_FORMAT);
        return new File(backupDirectory, "config-v" + version + "-" + timestamp + ".yml");
    }

    @Nonnull
    private static String formatVersion(int version) {
        return version >= 0 ? Integer.toString(version) : "unknown";
    }

    private void removeUnusedKeys() {
        Set<String> keys = new HashSet<>(config.getKeys(true));
        for (String key : keys) {
            if (isChickenProductionKey(key)) {
                continue;
            }

            if (!config.getDefaults().contains(key)) {
                config.set(key, null);
            }
        }
    }

    @Nonnull
    private static String chickenPath(@Nonnull String chickenKey) {
        return CHICKEN_PRODUCTION_PATH + "." + chickenKey.toUpperCase(Locale.ROOT);
    }

    private void setChickenProductionValue(@Nonnull String chickenKey, @Nonnull String setting, @Nonnull Object value) {
        config.set(chickenPath(chickenKey) + "." + setting, value);
        config.save();
    }

    private static boolean isChickenProductionKey(@Nonnull String key) {
        return key.equals("chickens") || key.equals(CHICKEN_PRODUCTION_PATH) || key.startsWith(CHICKEN_PRODUCTION_PATH + ".");
    }
}
