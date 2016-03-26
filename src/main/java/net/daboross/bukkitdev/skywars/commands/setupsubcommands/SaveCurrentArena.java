/*
 * Copyright (C) 2013-2016 Dabo Ross <http://www.daboross.net/>
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
package net.daboross.bukkitdev.skywars.commands.setupsubcommands;

import java.io.IOException;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaConfig;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.commands.setupstuff.BoundariesSetCondition;
import net.daboross.bukkitdev.skywars.commands.setupstuff.EnoughSpawnsSetCondition;
import net.daboross.bukkitdev.skywars.commands.setupstuff.SetupStates;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveCurrentArena extends SubCommand {

    private final SkyWars plugin;
    private final SetupStates states;

    public SaveCurrentArena(SkyWars plugin, SetupStates states) {
        super("save", false, null, SkyTrans.get(TransKey.SWS_SAVE_DESCRIPTION));
        Validate.notNull(states, "SetupStates cannot be null");
        BoundariesSetCondition condition1 = new BoundariesSetCondition(states);
        EnoughSpawnsSetCondition condition2 = new EnoughSpawnsSetCondition(states);
        addCommandFilter(condition1);
        addCommandPreCondition(condition1);
        addCommandFilter(condition2);
        addCommandPreCondition(condition2);
        addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.EQUALS, 0, SkyTrans.get(TransKey.TOO_MANY_PARAMS)));
        this.plugin = plugin;
        this.states = states;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        sender.sendMessage(SkyTrans.get(TransKey.SWS_SAVE_SAVING));
        SkyArenaConfig config = states.getSetupState(((Player) sender).getUniqueId()).convertToArenaConfig();
        plugin.getConfiguration().saveArena(config);
        try {
            ((SkyWarsPlugin) plugin).getWorldHandler().loadNewArena(config, true);
        } catch (IOException e) {
            sender.sendMessage(SkyTrans.get(TransKey.SWS_SAVE_FAILED));
            return;
        }
        sender.sendMessage(SkyTrans.get(TransKey.SWS_SAVE_SAVED));
    }
}
