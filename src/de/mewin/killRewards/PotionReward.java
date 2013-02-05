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

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class PotionReward extends Reward
{
    private PotionEffectType type;
    private int duration, level;
    
    public PotionReward(int kills, String name, HashMap<String, Object> map)
    {
        super(kills, name);
        
        Object tObj = map.get("type");
        if (tObj instanceof Integer)
        {
            type = PotionEffectType.getById((Integer) tObj);
        }
        else
        {
            type = PotionEffectType.getByName(((String) tObj).toUpperCase());
        }
        
        if (type == null)
        {
            throw new RuntimeException("Unknown potion type: " + tObj.toString());
        }
        
        duration = (Integer) map.get("duration");
        if (map.containsKey("level"))
        {
            level = (Integer) map.get("level");
        }
        else
        {
            level = 1;
        }
    }
    
    @Override
    public void give(Player player)
    {
        player.addPotionEffect(type.createEffect((int) (duration / 50 / type.getDurationModifier()), level - 1), true);
    }

}