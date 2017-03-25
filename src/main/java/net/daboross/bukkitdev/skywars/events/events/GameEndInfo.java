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
package net.daboross.bukkitdev.skywars.events.events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.daboross.bukkitdev.skywars.game.ArenaGame;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GameEndInfo {

    private final ArenaGame game;
    private final List<Player> alivePlayers;
    private final boolean broadcast;
    private final boolean endSyncNowPluginShutdown;

    public GameEndInfo(ArenaGame game, boolean broadcast, boolean endSyncNowPluginShutdown) {
        Validate.notNull(game, "Game cannot be null");
        this.game = game;
        this.broadcast = broadcast;
        this.endSyncNowPluginShutdown = endSyncNowPluginShutdown;
        List<UUID> alive = game.getAlivePlayers();
        alivePlayers = new ArrayList<>();
        for (UUID uuid : alive) {
            Player p = Bukkit.getPlayer(uuid);
            Validate.isTrue(p != null, String.format("Player (uuid: %s) not online", uuid));
            alivePlayers.add(p);
        }
    }

    public ArenaGame getGame() {
        return game;
    }

    public List<Player> getAlivePlayers() {
        return alivePlayers;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public boolean isEndSyncNowPluginShutdown() {
        return endSyncNowPluginShutdown;
    }
}
