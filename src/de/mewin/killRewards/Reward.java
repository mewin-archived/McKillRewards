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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public abstract class Reward
{
    private int kills;
    private String name;
    private String globalMessage = null;
    
    public Reward(int kills, String name)
    {
        this.kills = kills;
        this.name = name;
    }
    
    public static Reward rewardFromYaml(HashMap<String, Object> yaml)
    {
        try
        {
            String type = (String) yaml.get("type");
            int kills = yaml.containsKey("kills") ? (Integer) yaml.get("kills") : 0;
            Reward reward;
            if (type.equalsIgnoreCase("items"))
            {
                reward = new ItemReward(kills, (ArrayList) yaml.get("items"), (String) yaml.get("name"));
            }
            else if (type.equalsIgnoreCase("exp"))
            {
                reward = new ExpReward(kills, (String) yaml.get("name"), yaml.containsKey("exp") ? (Integer) yaml.get("exp") : 0, yaml.containsKey("levels") ? (Integer) yaml.get("levels") : 0);
            }
            else if (type.equalsIgnoreCase("multi"))
            {
                reward = new MultiReward(kills, (String) yaml.get("name"), (ArrayList) yaml.get("rewards"));
            }
            else if (type.equalsIgnoreCase("random"))
            {
                reward = new RandomReward(kills, (String) yaml.get("name"), (ArrayList) yaml.get("rewards"));
            }
            else if (type.equalsIgnoreCase("potion"))
            {
                reward = new PotionReward(kills, (String) yaml.get("name"), (HashMap) yaml.get("effect"));
            }
            else
            {
                Bukkit.getLogger().log(Level.WARNING, "Unknown reward type.");
                return null;
            }
            
            if (yaml.containsKey("global-message"))
            {
                reward.setGlobalMessage((String) yaml.get("global-message"));
            }
            
            return reward;
        }
        catch(ClassCastException ex)
        {
            Bukkit.getLogger().log(Level.WARNING, "ClassCastException: {0}", ex.getStackTrace()[0].getFileName() + ":" + ex.getStackTrace()[0].getLineNumber());
            return null;
        }
        catch(NullPointerException ex)
        {
            Bukkit.getLogger().log(Level.WARNING, "NullPointerException: {0}", ex.getStackTrace()[0].getFileName() + ":" + ex.getStackTrace()[0].getLineNumber());
            return null;
        }
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getKills()
    {
        return kills;
    }
    
    public void setGlobalMessage(String message)
    {
        this.globalMessage = message;
    }
    
    public String getGlobalMessage()
    {
        return this.globalMessage;
    }
    
    public abstract void give(Player player);
}