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

package de.mewin.killRewards.listeners;

import de.mewin.killRewards.KillRewardsPlugin;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class KillListener implements Listener
{
    private HashMap<String, Object[]> lastAttack;
    private KillRewardsPlugin plugin;
    private int getKillDifference = 15000;
    
    public KillListener(KillRewardsPlugin plugin)
    {
        this.plugin = plugin;
        lastAttack = new HashMap<String, Object[]>();
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
    {
        if (e.getDamage() > 0 && e.getDamager() instanceof Player && e.getEntity() instanceof Player)
        {
            lastAttack.put(((Player) e.getEntity()).getName(), new Object[] {
                ((Player) e.getDamager()).getName(), 
                System.currentTimeMillis()
            });
        }
        else if (e.getDamage() > 0 && e.getDamager() instanceof Projectile
                && ((Projectile) e.getDamager()).getShooter() instanceof Player)
        {
            lastAttack.put(((Player) e.getEntity()).getName(), new Object[] {
                ((Player) ((Projectile) e.getDamager()).getShooter()).getName(), 
                System.currentTimeMillis()
            });
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e)
    {
        if (lastAttack.containsKey(e.getEntity().getName()))
        {
            Object[] la = lastAttack.get(e.getEntity().getName());
            
            String attacker = (String) la[0];
            long time = (Long) la[1];
            
            if (time + getKillDifference > System.currentTimeMillis())
            {
                plugin.addSpree(attacker);
            }
        }
        
        plugin.resetSpree(e.getEntity().getName());
        
        lastAttack.remove(e.getEntity().getName());
    }
}