/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.skywars.commands.mainsubcommands;

import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyPortalData;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.commands.filters.QueueNameValidFilter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPortalCommand extends SubCommand {

    private final SkyWars plugin;

    public SetPortalCommand(SkyWars plugin) {
        super("setportal", false, "skywars.setportal", SkyTrans.get(TransKey.CMD_SETPORTAL_DESCRIPTION));
        if (plugin.getConfiguration().areMultipleQueuesEnabled()) {
            this.addArgumentNames(SkyTrans.get(TransKey.CMD_ARG_QUEUE_NAME));
            this.addCommandFilter(new QueueNameValidFilter(plugin, 0));
            this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.GREATER_THAN, 0, SkyTrans.get(TransKey.NOT_ENOUGH_PARAMS)));
            this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.LESS_THAN, 2, SkyTrans.get(TransKey.TOO_MANY_PARAMS)));
        } else {
            this.addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.EQUALS, 0, SkyTrans.get(TransKey.TOO_MANY_PARAMS)));
        }
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        Player player = (Player) sender;
        if (plugin.getCurrentGameTracker().isInGame(player.getUniqueId())) {
            sender.sendMessage(SkyTrans.get(TransKey.CMD_SETPORTAL_IN_GAME));
        } else {
            String queueName;
            if (plugin.getConfiguration().areMultipleQueuesEnabled()) {
                queueName = subCommandArgs[0];
            } else {
                queueName = null;
            }
            plugin.getLocationStore().getPortals().add(new SkyPortalData(new SkyBlockLocation(player.getLocation()), queueName));
            sender.sendMessage(SkyTrans.get(TransKey.CMD_SETPORTAL_CONFIRMATION));
        }
    }
}
