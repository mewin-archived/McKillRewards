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
import org.bukkit.entity.Player;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public abstract class Reward
{
    private int kills;
    private String name;
    
    public Reward(int kills, String name)
    {
        this.kills = kills;
        this.name = name;
    }
    
    public static Reward rewardFromYaml(HashMap<String, Object> yaml)
    {
        String type = (String) yaml.get("type");
        if (type.equalsIgnoreCase("items"))
        {
            return new ItemReward((Integer) yaml.get("kills"), (ArrayList) yaml.get("items"), (String) yaml.get("name"));
        }
        else
        {
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
    
    public abstract void give(Player player);
}