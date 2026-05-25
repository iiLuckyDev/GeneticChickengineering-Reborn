package net.guizhanss.gcereborn.core.commands.subcommands;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.core.commands.AbstractSubCommand;
import net.guizhanss.gcereborn.core.gui.ChickenProductionControlPanel;
import net.guizhanss.guizhanlib.minecraft.commands.AbstractCommand;

public final class ControlPanelCommand extends AbstractSubCommand {

    public ControlPanelCommand(@Nonnull AbstractCommand parent) {
        super(parent, "controlpanel", "");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onExecute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            GeneticChickengineering.getLocalization().sendMessage(sender, "no-permission");
            return;
        }
        if (!(sender instanceof Player player)) {
            GeneticChickengineering.getLocalization().sendMessage(sender, "no-console");
            return;
        }

        ChickenProductionControlPanel.open(player);
    }
}
