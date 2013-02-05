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

package de.mewin.killRewards.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class ChatHandler extends Handler
{
    private CommandSender receiver;
    
    public ChatHandler(CommandSender to)
    {
        this.receiver = to;
    }

    @Override
    public void publish(LogRecord lr)
    {
        String message = lr.getMessage();
        if (lr.getParameters() != null)
        {
            for (int i = 0; i < lr.getParameters().length; i++)
            {
                message = message.replaceAll("\\{" + i + "\\}", lr.getParameters()[i].toString());
            }
        }
        ChatColor color;
        if (lr.getLevel().intValue() > Level.INFO.intValue())
        {
            color = ChatColor.RED;
        }
        else
        {
            color = ChatColor.GREEN;
        }
        
        if (receiver instanceof Player)
        {
            receiver.sendMessage(color + lr.getLevel().getName() + ":" + message);
        }
    }

    @Override
    public void flush() {
        
    }

    @Override
    public void close() throws SecurityException {
        
    }

}