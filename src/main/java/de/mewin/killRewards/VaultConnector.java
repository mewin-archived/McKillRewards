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

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class VaultConnector
{
    private static VaultConnector instance = null;
    private boolean hasVault;
    
    public VaultConnector()
    {
        hasVault = Bukkit.getPluginManager().getPlugin("Vault") != null;
    }
    
    public boolean hasVault()
    {
        return hasVault;
    }
    
    public net.milkbowl.vault.economy.Economy getEconomy()
    {
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null)
        {
            return economyProvider.getProvider();
        }
        else
        {
            return null;
        }
    }
    
    public static VaultConnector getInstance()
    {
        if (instance == null)
        {
            instance = new VaultConnector();
        }
        
        return instance;
    }
}