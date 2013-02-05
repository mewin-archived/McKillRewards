/*
 * Copyright (C) 2013 mewin<mewin001@hotmail.de>
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

package de.mewin.killRewards;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class WorldListener implements Listener
{
    private KillRewardsPlugin plugin;
    
    public WorldListener(KillRewardsPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent e)
    {
        plugin.loadWorld(e.getWorld().getName());
    }
    
    @EventHandler
    public void onWorldInit(WorldInitEvent e)
    {
        plugin.loadWorld(e.getWorld().getName());
    }
}