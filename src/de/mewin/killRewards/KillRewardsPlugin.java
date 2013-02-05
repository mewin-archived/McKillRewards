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

import de.mewin.killRewards.util.ChatHandler;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
        
        if (rewards.containsKey(spree) && pl.hasPermission("mcrewards.rewards"))
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
        
        if (worldRewards.containsKey(w.getName()) && worldRewards.get(w.getName()).containsKey(spree)
                && (pl.hasPermission("mcrewards.rewards") || pl.hasPermission("mcrewards.rewards." + w.getName())))
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
                        lRewards.put(kills, multi);
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
        
        return lRewards;
    }
    
    private void loadRewards()
    {
        File rewardsFile = new File(getDataFolder(), "rewards.yml");
        
        rewards = loadRewards(rewardsFile);
        
        getLogger().log(Level.INFO, "{0} rewards loaded.", rewards.size());
        
        for (World world : getServer().getWorlds())
        {
            File worldFile = new File(getDataFolder(), "rewards-" + world.getName() + ".yml");
            if (worldFile.exists())
            {
                HashMap<Integer, Reward> wRewards = loadRewards(worldFile);
                worldRewards.put(world.getName(), wRewards);
                getLogger().log(Level.INFO, "{0} rewards loaded for world {1}.", new Object[] {wRewards.size(), world.getName()});
            }
        }
    }
    
    public void loadWorld(String name)
    {
        File worldFile = new File(getDataFolder(), "rewards-" + name + ".yml");
        
        if (worldFile.exists() && !worldRewards.containsKey(name))
        {
            HashMap<Integer, Reward> wRewards = loadRewards(worldFile);
            worldRewards.put(name, wRewards);
            getLogger().log(Level.INFO, "{0} rewards loaded for world {1}.", new Object[] {wRewards.size(), name});
        }
    }
    
    public void reload()
    {
        rewards.clear();
        worldRewards.clear();
        System.gc();
        loadRewards();
    }
    
    public void reloadWorld(String name)
    {
        worldRewards.remove(name);
        loadWorld(name);
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] params)
    {
        if (cmd.getLabel().equals("rewards"))
        {
            if (params.length < 1)
            {
                cs.sendMessage(ChatColor.GOLD + cmd.getUsage());
            }
            else 
            {
                if (params[0].equalsIgnoreCase("reload"))
                {
                    if (params.length < 2)
                    {
                        ChatHandler handler = new ChatHandler(cs);
                        getLogger().addHandler(handler);
                        cs.sendMessage(ChatColor.GREEN + "Reloading rewards...");
                        reload();
                        getLogger().removeHandler(handler);
                    }
                    else
                    {   
                        if (getServer().getWorld(params[1]) == null)
                        {
                            cs.sendMessage(ChatColor.RED + "World not found: " + params[1]);
                        }
                        else
                        {
                            ChatHandler handler = new ChatHandler(cs);
                            getLogger().addHandler(handler);
                            cs.sendMessage(ChatColor.GREEN + "Reloading rewards for world " + params[1] + "...");
                            reloadWorld(params[1]);
                            getLogger().removeHandler(handler);
                        }
                    }
                }
                else if (params[0].equalsIgnoreCase("reset"))
                {
                    if (params.length < 2)
                    {
                        cs.sendMessage(ChatColor.GREEN + "Reseting killing sprees...");
                        sprees.clear();
                        cs.sendMessage(ChatColor.GREEN + "Sprees reseted.");
                    }
                    else
                    {
                        List<Player> pl = getServer().matchPlayer(params[1]);
                        if (pl.size() < 1)
                        {
                            cs.sendMessage(ChatColor.RED + "No player found with that name.");
                        }
                        else if (pl.size() > 1)
                        {
                            cs.sendMessage(ChatColor.RED + "Multiple players found");
                        }
                        else
                        {
                            cs.sendMessage(ChatColor.GREEN + "Reseting killing spree of player " + pl.get(0).getName());
                            sprees.remove(pl.get(0).getName());
                            cs.sendMessage(ChatColor.GREEN + "Killing sprees for " + pl.get(0).getName() + "reseted.");
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] params)
    {
        if (cmd.getLabel().equalsIgnoreCase("rewards"))
        {
            ArrayList<String> completitions = new ArrayList<String>();
            if (params.length < 2)
            {
                completitions.add("reload");
                completitions.add("reset");
            }
            else if (params.length < 3)
            {
                if (params[0].equalsIgnoreCase("reload"))
                {
                    for (World w : getServer().getWorlds())
                    {
                        completitions.add(w.getName());
                    }
                }
                else if (params[0].equalsIgnoreCase("reset"))
                {
                    for (Player p : getServer().getOnlinePlayers())
                    {
                        completitions.add(p.getName());
                    }
                }
            }
            
            Iterator<String> itr = completitions.iterator();
            while (itr.hasNext())
            {
                if (!itr.next().toLowerCase().startsWith(params[params.length - 1].toLowerCase()))
                {
                    itr.remove();
                }
            }
            
            return completitions;
        }
        else
        {
            return super.onTabComplete(sender, cmd, label, params);
        }
    }
    
    
}