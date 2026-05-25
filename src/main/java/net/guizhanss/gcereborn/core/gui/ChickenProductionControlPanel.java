package net.guizhanss.gcereborn.core.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.core.services.ConfigurationService;
import net.guizhanss.gcereborn.items.chicken.ChickenProduct;
import net.guizhanss.gcereborn.items.chicken.ChickenTypes;

public final class ChickenProductionControlPanel implements Listener {

    private static final int LIST_SIZE = 54;
    private static final int DETAIL_SIZE = 27;
    private static final int CHICKENS_PER_PAGE = 45;
    private static final int PREVIOUS_PAGE_SLOT = 45;
    private static final int INFO_SLOT = 49;
    private static final int NEXT_PAGE_SLOT = 53;

    private static final int ENABLED_SLOT = 10;
    private static final int TIME_MULTIPLIER_SLOT = 11;
    private static final int EXTRA_TIME_SLOT = 12;
    private static final int OUTPUT_AMOUNT_SLOT = 13;
    private static final int FAIL_RATE_SLOT = 14;
    private static final int RESTART_REQUIRED_SLOT = 16;
    private static final int BACK_SLOT = 22;

    public static void open(@Nonnull Player player) {
        openList(player, 0);
    }

    private static void openList(@Nonnull Player player, int page) {
        ControlPanelHolder holder = new ControlPanelHolder(ControlPanelView.LIST, page, null);
        Inventory inventory = Bukkit.createInventory(holder, LIST_SIZE, color("&2GCE Chicken Controls"));
        holder.setInventory(inventory);

        List<Map.Entry<Integer, ChickenProduct>> chickens = new ArrayList<>(ChickenTypes.getTypes().entrySet());
        int maxPage = Math.max(0, (chickens.size() - 1) / CHICKENS_PER_PAGE);
        int safePage = Math.max(0, Math.min(page, maxPage));
        holder.setPage(safePage);

        int start = safePage * CHICKENS_PER_PAGE;
        int end = Math.min(start + CHICKENS_PER_PAGE, chickens.size());
        for (int index = start; index < end; index++) {
            Map.Entry<Integer, ChickenProduct> entry = chickens.get(index);
            ChickenProduct product = entry.getValue();
            inventory.setItem(index - start, chickenIcon(product));
        }

        if (safePage > 0) {
            inventory.setItem(PREVIOUS_PAGE_SLOT, menuItem(Material.ARROW, "&ePrevious Page", List.of("&7Go to page " + safePage + ".")));
        }
        inventory.setItem(INFO_SLOT, menuItem(
            Material.WRITABLE_BOOK,
            "&aProduction Control Panel",
            List.of(
                "&7Adjusts only runtime-safe chicken",
                "&7production values.",
                "",
                "&eChanges save to config.yml.",
                "&eExisting chamber cycles are not interrupted.",
                "&7Page &f" + (safePage + 1) + "&7/&f" + (maxPage + 1)
            )
        ));
        if (safePage < maxPage) {
            inventory.setItem(NEXT_PAGE_SLOT, menuItem(Material.ARROW, "&eNext Page", List.of("&7Go to page " + (safePage + 2) + ".")));
        }

        player.openInventory(inventory);
    }

    private static void openChicken(@Nonnull Player player, @Nonnull String chickenKey, int page) {
        ControlPanelHolder holder = new ControlPanelHolder(ControlPanelView.CHICKEN, page, chickenKey);
        Inventory inventory = Bukkit.createInventory(holder, DETAIL_SIZE, color("&2GCE: " + chickenKey));
        holder.setInventory(inventory);
        renderChicken(inventory, chickenKey, page);
        player.openInventory(inventory);
    }

    private static void renderChicken(@Nonnull Inventory inventory, @Nonnull String chickenKey, int page) {
        inventory.clear();
        ConfigurationService config = GeneticChickengineering.getConfigService();

        boolean enabled = config.isChickenProductionEnabled(chickenKey);
        inventory.setItem(ENABLED_SLOT, menuItem(
            enabled ? Material.LIME_DYE : Material.GRAY_DYE,
            enabled ? "&aEnabled" : "&cDisabled",
            List.of(
                "&7Controls whether this chicken can",
                "&7start new Excitation Chamber cycles.",
                "",
                "&fCurrent: " + (enabled ? "&aEnabled" : "&cDisabled"),
                "",
                "&eClick to toggle.",
                "&7Applies next production cycle."
            )
        ));

        inventory.setItem(TIME_MULTIPLIER_SLOT, menuItem(
            Material.CLOCK,
            "&eTime Multiplier",
            List.of(
                "&7Multiplies final production time.",
                "&7Higher values are slower.",
                "",
                "&fCurrent: &b" + config.getChickenTimeMultiplier(chickenKey) + "x",
                "",
                "&eLeft Click: +0.25",
                "&eRight Click: -0.25",
                "&6Shift Click: +/-1.00",
                "&7Range: 0.10x - 100.00x"
            )
        ));

        inventory.setItem(EXTRA_TIME_SLOT, menuItem(
            Material.REPEATER,
            "&eExtra Time Seconds",
            List.of(
                "&7Adds flat seconds after chamber",
                "&7speed calculation.",
                "",
                "&fCurrent: &b" + config.getChickenExtraTimeSeconds(chickenKey) + "s",
                "",
                "&eLeft Click: +5s",
                "&eRight Click: -5s",
                "&6Shift Click: +/-60s",
                "&7Range: 0s - 3600s"
            )
        ));

        inventory.setItem(OUTPUT_AMOUNT_SLOT, menuItem(
            Material.CHEST,
            "&eOutput Amount",
            List.of(
                "&7Items produced on successful cycles.",
                "",
                "&fCurrent: &b" + config.getChickenOutputAmount(chickenKey),
                "",
                "&eLeft Click: +1",
                "&eRight Click: -1",
                "&6Shift Click: +/-8",
                "&7Range: 1 - 64"
            )
        ));

        inventory.setItem(FAIL_RATE_SLOT, menuItem(
            Material.EGG,
            "&eFail Rate",
            List.of(
                "&7Chance to produce a normal egg",
                "&7instead of the resource.",
                "",
                "&fCurrent: &b" + config.getChickenFailRate(chickenKey) + "%",
                "",
                "&eLeft Click: +5%",
                "&eRight Click: -5%",
                "&6Shift Click: +/-25%",
                "&7Range: 0% - 100%"
            )
        ));

        inventory.setItem(RESTART_REQUIRED_SLOT, menuItem(
            Material.BARRIER,
            "&cRestart Required Settings",
            List.of(
                "&7Researches, machines, language,",
                "&7commands, and Slimefun registration",
                "&7settings are intentionally not editable",
                "&7from this live control panel."
            )
        ));

        inventory.setItem(BACK_SLOT, menuItem(Material.ARROW, "&eBack", List.of("&7Return to chicken list page " + (page + 1) + ".")));
    }

    @EventHandler
    public void onInventoryClick(@Nonnull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ControlPanelHolder holder)) {
            return;
        }

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }
        if (!player.hasPermission("geneticchickengineering.command.controlpanel")) {
            player.closeInventory();
            GeneticChickengineering.getLocalization().sendMessage(player, "no-permission");
            return;
        }

        if (holder.getView() == ControlPanelView.LIST) {
            handleListClick(player, holder, event.getSlot());
        } else {
            handleChickenClick(player, holder, event.getSlot(), event.getClick());
        }
    }

    private static void handleListClick(@Nonnull Player player, @Nonnull ControlPanelHolder holder, int slot) {
        if (slot == PREVIOUS_PAGE_SLOT) {
            openList(player, holder.getPage() - 1);
            return;
        }
        if (slot == NEXT_PAGE_SLOT) {
            openList(player, holder.getPage() + 1);
            return;
        }
        if (slot < 0 || slot >= CHICKENS_PER_PAGE) {
            return;
        }

        List<Map.Entry<Integer, ChickenProduct>> chickens = new ArrayList<>(ChickenTypes.getTypes().entrySet());
        int index = holder.getPage() * CHICKENS_PER_PAGE + slot;
        if (index >= chickens.size()) {
            return;
        }

        openChicken(player, chickens.get(index).getValue().getName(), holder.getPage());
    }

    private static void handleChickenClick(
        @Nonnull Player player,
        @Nonnull ControlPanelHolder holder,
        int slot,
        @Nonnull ClickType click
    ) {
        String chickenKey = holder.getChickenKey();
        if (chickenKey == null) {
            return;
        }

        ConfigurationService config = GeneticChickengineering.getConfigService();
        int direction = click.isRightClick() ? -1 : 1;

        switch (slot) {
            case ENABLED_SLOT -> config.setChickenProductionEnabled(chickenKey, !config.isChickenProductionEnabled(chickenKey));
            case TIME_MULTIPLIER_SLOT -> {
                double amount = click.isShiftClick() ? 1.0d : 0.25d;
                config.setChickenTimeMultiplier(chickenKey, config.getChickenTimeMultiplier(chickenKey) + (direction * amount));
            }
            case EXTRA_TIME_SLOT -> {
                int amount = click.isShiftClick() ? 60 : 5;
                config.setChickenExtraTimeSeconds(chickenKey, config.getChickenExtraTimeSeconds(chickenKey) + (direction * amount));
            }
            case OUTPUT_AMOUNT_SLOT -> {
                int amount = click.isShiftClick() ? 8 : 1;
                config.setChickenOutputAmount(chickenKey, config.getChickenOutputAmount(chickenKey) + (direction * amount));
            }
            case FAIL_RATE_SLOT -> {
                int amount = click.isShiftClick() ? 25 : 5;
                config.setChickenFailRate(chickenKey, config.getChickenFailRate(chickenKey) + (direction * amount));
            }
            case BACK_SLOT -> {
                openList(player, holder.getPage());
                return;
            }
            default -> {
                return;
            }
        }

        renderChicken(holder.getInventory(), chickenKey, holder.getPage());
    }

    @Nonnull
    private static ItemStack chickenIcon(@Nonnull ChickenProduct product) {
        ItemStack item = product.getProduct().clone();
        item.setAmount(1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            ConfigurationService config = GeneticChickengineering.getConfigService();
            String chickenKey = product.getName();
            meta.setDisplayName(color("&e" + product.getProductName() + " Chicken"));
            meta.setLore(color(List.of(
                "&7Click to edit production settings.",
                "",
                "&fEnabled: " + (config.isChickenProductionEnabled(chickenKey) ? "&aYes" : "&cNo"),
                "&fTime Multiplier: &b" + config.getChickenTimeMultiplier(chickenKey) + "x",
                "&fExtra Time: &b" + config.getChickenExtraTimeSeconds(chickenKey) + "s",
                "&fOutput Amount: &b" + config.getChickenOutputAmount(chickenKey),
                "&fFail Rate: &b" + config.getChickenFailRate(chickenKey) + "%"
            )));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Nonnull
    private static ItemStack menuItem(@Nonnull Material material, @Nonnull String name, @Nonnull List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(name));
            meta.setLore(color(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Nonnull
    private static String color(@Nonnull String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @Nonnull
    private static List<String> color(@Nonnull List<String> lines) {
        return lines.stream().map(ChickenProductionControlPanel::color).toList();
    }

    private enum ControlPanelView {
        LIST,
        CHICKEN
    }

    private static final class ControlPanelHolder implements InventoryHolder {

        private final ControlPanelView view;
        private final String chickenKey;
        private int page;
        private Inventory inventory;

        private ControlPanelHolder(@Nonnull ControlPanelView view, int page, @Nullable String chickenKey) {
            this.view = view;
            this.page = page;
            this.chickenKey = chickenKey;
        }

        @Nonnull
        @Override
        public Inventory getInventory() {
            return inventory;
        }

        private ControlPanelView getView() {
            return view;
        }

        private int getPage() {
            return page;
        }

        private void setPage(int page) {
            this.page = page;
        }

        @Nullable
        private String getChickenKey() {
            return chickenKey;
        }

        private void setInventory(@Nonnull Inventory inventory) {
            this.inventory = inventory;
        }
    }
}
