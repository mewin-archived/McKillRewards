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
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class KillListener implements Listener
{
    private HashMap<String, Object[]> lastAttack;
    private HashMap<UUID, Object[]> lastMobAttack;
    private KillRewardsPlugin plugin;
    private int getKillDifference = 15000;
    
    public KillListener(KillRewardsPlugin plugin)
    {
        this.plugin = plugin;
        lastAttack = new HashMap<String, Object[]>();
        lastMobAttack = new HashMap<UUID, Object[]>();
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
    {
        Entity damager = e.getDamager();
        if (damager instanceof Projectile)
        {
            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (shooter instanceof Entity)
            {
                damager = (Entity) shooter;
            }
        }
        if (e.getDamage() > 0 && damager != null && damager instanceof Player)
        {
            if (e.getEntity() instanceof Player)
            {
                lastAttack.put(((Player) e.getEntity()).getName(), new Object[] {
                    ((Player) damager).getName(), 
                    System.currentTimeMillis()
                });
            }
            else if (plugin.isValidMob(e.getEntityType()))
            {
                lastMobAttack.put(e.getEntity().getUniqueId(), new Object[] {
                    ((Player) damager).getName(), 
                    System.currentTimeMillis()
                });
            }
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
                plugin.addSpree(attacker, false);
            }
        }
        
        plugin.resetSpree(e.getEntity().getName());
        
        lastAttack.remove(e.getEntity().getName());
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e)
    {
        if (!(e.getEntity() instanceof Player) && lastMobAttack.containsKey(e.getEntity().getUniqueId()))
        {
            Object[] la = lastMobAttack.get(e.getEntity().getUniqueId());
            
            String attacker = (String) la[0];
            long time = (Long) la[1];
            
            if (time + getKillDifference > System.currentTimeMillis())
            {
                plugin.addSpree(attacker, true);
            }
        }
        
        lastMobAttack.remove(e.getEntity().getUniqueId());
    }
}