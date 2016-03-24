/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net>
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
package net.daboross.bukkitdev.skywars.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaConfig;
import net.daboross.bukkitdev.skywars.api.config.SkyConfigurationException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SkyArenaConfigLoader {

    public SkyArenaConfig loadArena(Path file, String name) throws SkyConfigurationException {
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file.toFile());
        } catch (FileNotFoundException ex) {
            throw new SkyConfigurationException("File " + file.toAbsolutePath() + " could not be found.", ex);
        } catch (IOException ex) {
            throw new SkyConfigurationException("IOException loading file " + file.toAbsolutePath(), ex);
        } catch (InvalidConfigurationException ex) {
            throw new SkyConfigurationException("Failed to load configuration file " + file.toAbsolutePath(), ex);
        }
        if (!checkVersion(config)) {
            throw new SkyConfigurationException("Unknown config-version " + config.getInt("config-version") + " in file " + file.toAbsolutePath());
        }
        SkyArenaConfig arenaConfig = SkyArenaConfig.deserialize(config);
        arenaConfig.setArenaName(name);
        arenaConfig.setFile(file);
        return arenaConfig;
    }

    private boolean checkVersion(ConfigurationSection config) {
        int version = config.getInt("config-version", 0);
        if (version == 0) {
            version0To1(config);
            version = 1;
        }
        if (version == 1) {
            version1To2(config);
            version = 2;
        }
        System.out.println("version: " + version + " is 2? " + (version == 2));
        return version == 2;
    }

    private void version0To1(ConfigurationSection config) {
        config.set("config-version", 1);
        config.set("num-teams", config.get("num-players"));
        config.set("team-size", 1);
        config.set("placement-y", config.get("placement.placement-y"));
        config.set("num-players", null);
        config.set("placement", null);
    }

    private void version1To2(final ConfigurationSection config) {
        config.set("config-version", 2);
    }
}
