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
package net.daboross.bukkitdev.skywars.economy;

import java.util.List;
import java.util.UUID;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.economy.SkyEconomyAbstraction;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.events.events.GameEndInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerKillPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SkyEconomyGameRewards {

    private final SkyWars plugin;

    public SkyEconomyGameRewards(final SkyWars plugin) {
        this.plugin = plugin;
    }

    public void onPlayerKillPlayer(PlayerKillPlayerInfo info) {
        // TODO: Should we have rewards on forfeit?
        int reward = plugin.getConfiguration().getEconomyKillReward();
        UUID killerUuid = info.getKillerUuid();
        if (plugin.getConfiguration().areEconomyRewardMessagesEnabled()) {
            Player p = Bukkit.getPlayer(killerUuid);
            if (p != null) {
                p.sendMessage(SkyTrans.get(TransKey.ECO_REWARD_KILL, getSymboledReward(reward), info.getKilled().getName()));
            }
        }
        plugin.getEconomyHook().addReward(killerUuid, reward);
    }

    public void onGameEnd(GameEndInfo info) {
        int reward = plugin.getConfiguration().getEconomyWinReward();
        boolean enableMessages = plugin.getConfiguration().areEconomyRewardMessagesEnabled();
        SkyEconomyAbstraction eco = plugin.getEconomyHook();
        List<Player> alive = info.getAlivePlayers();
        if (!alive.isEmpty() && alive.size() <= info.getGame().getArena().getTeamSize()) {
            for (Player p : alive) {
                if (enableMessages) {
                    p.sendMessage(SkyTrans.get(TransKey.ECO_REWARD_WIN, getSymboledReward(reward)));
                }
                eco.addReward(p, reward);
            }
        }
    }

    public String getSymboledReward(int reward) {
        String symbol = plugin.getEconomyHook().getCurrencySymbol(reward);
        if (symbol.equals("$")) {
            return symbol + reward;
        } else {
            return reward + symbol;
        }
    }
}
