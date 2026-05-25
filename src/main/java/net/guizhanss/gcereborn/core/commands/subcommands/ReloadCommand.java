package net.guizhanss.gcereborn.core.commands.subcommands;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.core.commands.AbstractSubCommand;
import net.guizhanss.guizhanlib.minecraft.commands.AbstractCommand;

public final class ReloadCommand extends AbstractSubCommand {

    public ReloadCommand(@Nonnull AbstractCommand parent) {
        super(parent, "reload", "");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onExecute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            GeneticChickengineering.getLocalization().sendMessage(sender, "no-permission");
            return;
        }

        GeneticChickengineering.getConfigService().reloadRuntimeProductionSettings();
        GeneticChickengineering.getLocalization().sendMessage(sender, "reload-production-settings");
    }
}
