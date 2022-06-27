/*
 * Copyright 2017 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.general;

import org.json.JSONArray;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.api.entities.Role;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class ListTierRolesCmd extends Command 
{
    
    public ListTierRolesCmd(Bot bot)
    {
        this.name = "listtierroles";
        this.help = "shows activity tier roles for current guild";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
    	Settings s = event.getClient().getSettingsFor(event.getGuild());
    	JSONArray tierIds = s.getTierIds();
    	String msg = "Tier Roles for " + event.getGuild().getName();
    	
    	if(tierIds.isEmpty())
    	{
    		event.replySuccess("No tier roles set for this server, use `" + event.getClient().getPrefix() + "settier` to set tier roles");
    		return;
    	}
    	for(int i = 0; i < tierIds.length() ; i++) 
    	{
    		msg = msg + "\n Tier" + i + ": <@&" + tierIds.getLong(i) + ">";
    	}
    	event.replySuccess(msg);
    	
    }
    
}
