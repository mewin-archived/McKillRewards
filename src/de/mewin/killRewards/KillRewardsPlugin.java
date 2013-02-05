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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class KillRewardsPlugin extends JavaPlugin
{
    private KillListener listener;
    private HashMap<String, Integer> sprees;
    private HashMap<Integer, Reward> rewards;
    private HashMap<String, HashMap<Integer, Reward>> worldRewards;
    
    @Override
    public void onEnable()
    {
        sprees = new HashMap<String, Integer>();
        rewards = new HashMap<Integer, Reward>();
        worldRewards = new HashMap<String, HashMap<Integer, Reward>>();
        listener = new KillListener(this);
        loadRewards();
        getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable()
    {
        
    }
    
    public void addSpree(String player)
    {
        Player pl = getServer().getPlayer(player);
        World w = pl.getWorld();
        int spree = 1;
        if (sprees.containsKey(player))
        {
            spree = sprees.get(player) + 1;
        }
        sprees.put(player, spree);
        
        if (rewards.containsKey(spree))
        {
            Reward reward = rewards.get(spree);
            if (reward.getMessage() != null)
            {
                pl.sendMessage(ChatColor.translateAlternateColorCodes('&', reward.getMessage()));
            }
            reward.give(pl);
            if (reward.getGlobalMessage() != null)
            {
                for (Player oPlayer : getServer().getOnlinePlayers())
                {
                    oPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', reward.getGlobalMessage()
                            .replaceAll("\\{player\\}", pl.getDisplayName())));
                }
            }
        }
        
        if (worldRewards.containsKey(w.getName()) && worldRewards.get(w.getName()).containsKey(spree))
        {
            Reward reward = worldRewards.get(w.getName()).get(spree);
            if (reward.getMessage() != null)
            {
                pl.sendMessage(ChatColor.translateAlternateColorCodes('&', reward.getMessage()));
            }
            reward.give(pl);
            if (reward.getGlobalMessage() != null)
            {
                for (Player oPlayer : getServer().getOnlinePlayers())
                {
                    oPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', reward.getGlobalMessage()
                            .replaceAll("\\{player\\}", pl.getDisplayName())));
                }
            }
        }
    }
    
    public void resetSpree(String player)
    {
        sprees.remove(player);
    }
    
    public int getSpree(String player)
    {
        if (sprees.containsKey(player))
        {
            return sprees.get(player);
        }
        else
        {
            return 0;
        }
    }
    
    private HashMap<Integer, Reward> loadRewards(File rewardsFile)
    {
        HashMap<Integer, Reward> lRewards = new HashMap<Integer, Reward>();
        FileInputStream str = null;
        try
        {
            Yaml yaml = new Yaml();
            str = new FileInputStream(rewardsFile);
            HashMap<String, Object> map = (HashMap) yaml.load(str);
            ArrayList<HashMap> list = (ArrayList) map.get("rewards");
            for (HashMap rMap : list)
            {
                Reward reward = Reward.rewardFromYaml(rMap);
                if (reward != null)
                {
                    int kills = reward.getKills();
                    Reward currentReward = lRewards.get(kills);
                    getLogger().log(Level.INFO, "Reward {0} loaded.", reward.getName());
                    if (currentReward == null)
                    {
                        lRewards.put(reward.getKills(), reward);
                    }
                    else
                    {
                        HiddenMultiReward multi;
                        if (currentReward instanceof HiddenMultiReward)
                        {
                            multi = (HiddenMultiReward) currentReward;
                        }
                        else
                        {
                            multi = new HiddenMultiReward(kills, currentReward.getName());
                            multi.addReward(currentReward);
                        }
                        multi.addReward(reward);
                    }
                }
                else
                {
                    getLogger().log(Level.WARNING, "Error loading reward.");
                }
            }
            str.close();
        }
        catch(Exception ex)
        {
            getLogger().log(Level.SEVERE, "Could not load rewards: ", ex);
        }
        finally
        {
            if (str != null)
            {
                try
                {
                    str.close();
                }
                catch(Exception ex)
                {}
            }
        }
        
        getLogger().log(Level.INFO, "{0} rewards loaded.", lRewards.size());
        
        return lRewards;
    }
    
    private void loadRewards()
    {
        File rewardsFile = new File(getDataFolder(), "rewards.yml");
        
        rewards = loadRewards(rewardsFile);
        
        for (World world : getServer().getWorlds())
        {
            File worldFile = new File(getDataFolder(), "rewards-" + world.getName() + ".yml");
            if (worldFile.exists())
            {
                worldRewards.put(world.getName(), loadRewards(worldFile));
            }
        }
    }
}