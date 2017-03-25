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
package net.daboross.bukkitdev.skywars.events.listeners;

import net.daboross.bukkitdev.skywars.api.SkyStatic;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayer;
import net.daboross.bukkitdev.skywars.api.players.SkySavedInventory;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerLeaveGameInfo;
import net.daboross.bukkitdev.skywars.events.events.PlayerRespawnAfterGameEndInfo;
import net.daboross.bukkitdev.skywars.player.SavedInventory;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventorySaveListener {

    private final SkyWars plugin;

    public InventorySaveListener(final SkyWars plugin) {
        this.plugin = plugin;
    }

    public void onGameStart(GameStartInfo info) {
        boolean save = plugin.getConfiguration().isInventorySaveEnabled();
        boolean savePgh = plugin.getConfiguration().isPghSaveEnabled();
        for (Player p : info.getPlayers()) {
            if (save) {
                SkyPlayer skyPlayer = plugin.getPlayers().getPlayer(p);
                skyPlayer.setSavedInventory(new SavedInventory(p, savePgh));
            }
            SkyStatic.debug("Clearing %s's inventory. [InventorySaveListener.onGameStart]", p.getUniqueId());
            PlayerInventory inv = p.getInventory();
            inv.clear();
            inv.setArmorContents(new ItemStack[inv.getArmorContents().length]);
        }
    }

    /**
     * This is a workaround for when working with MultiInv and other inventory save plugins which may save player
     * inventory when they leave the SkyWarsArenaWorld. Everything is restored once the player respawns, of course.
     */
    public void onPlayerLeaveGame(PlayerLeaveGameInfo info) {
        Player player = info.getPlayer();
        SkyStatic.debug("Clearing %s's inventory. [InventorySaveListener.onPlayerLeaveGame]", player.getUniqueId());
        PlayerInventory inv = player.getInventory();
        inv.clear();
        inv.setArmorContents(new ItemStack[inv.getArmorContents().length]);
    }

    public void onPlayerRespawn(PlayerRespawnAfterGameEndInfo info) {
        boolean save = plugin.getConfiguration().isInventorySaveEnabled();
        boolean restoreExp = plugin.getConfiguration().isExperienceSaveEnabled();
        boolean restorePgh = plugin.getConfiguration().isPghSaveEnabled();
        Player player = info.getPlayer();
        SkyStatic.debug("Clearing %s's inventory. [InventorySaveListener.onPlayerRespawn]", player.getUniqueId());
        PlayerInventory inv = player.getInventory();
        inv.clear();
        inv.setArmorContents(new ItemStack[inv.getArmorContents().length]);
        if (save) {
            SkyPlayer skyPlayer = plugin.getPlayers().getPlayer(player);
            SkySavedInventory savedInventory = skyPlayer.getSavedInventory();
            Validate.notNull(savedInventory, "Saved inventory for " + skyPlayer.getName() + " was null!");
            savedInventory.apply(player, restoreExp, restorePgh);
            skyPlayer.setSavedInventory(null); // no need to keep this around
        }
    }
}
