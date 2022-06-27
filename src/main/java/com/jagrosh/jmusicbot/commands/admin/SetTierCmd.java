/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands.admin;

import java.util.List;

import org.json.JSONArray;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;

import net.dv8tion.jda.api.entities.Role;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class SetTierCmd extends AdminCommand
{
    public SetTierCmd(Bot bot)
    {
        this.name = "settier";
        this.help = "sets an activity tier role for this server";
        this.arguments = "<0-10> <rolename|NONE>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
    	 if(event.getArgs().isEmpty())
         {
             event.replyError("Please include a tier and a role");
             return;
         }
         
    	 int tier;
    	 List<Role> roleId;
    	 int spaceloc;
         
         
         
         //get tier
         spaceloc = event.getArgs().indexOf(" ");
         if (spaceloc == -1)
         {
        	 event.replyError("Please include a `@role` or NONE");
        	 return;
         }
         try 
         {
        	 tier = Integer.parseInt(event.getArgs().substring(0, spaceloc));
         }
         catch(NumberFormatException e)
         {
        	 event.replyError("Invalid tier, please enter a valid number");
        	 return;
         }
         
         //get roles mentioned
         roleId = event.getMessage().getMentionedRoles();
         if(roleId.isEmpty())
         {
        	 event.replyError("Must include a role, do this by pinging the role you want to set as tier *" + tier + "*");
        	 return;
         }
         if(roleId.size() > 1)
         {
        	 event.replyError("Please only include 1 role");
        	 return;
         }
         
         Settings s = event.getClient().getSettingsFor(event.getGuild());
         JSONArray tierIds = s.getTierIds();
         
         if(tierIds.length() < tier)
         {
        	 event.replyError("Must fill all lower tiers starting at 0 before filling tier *" + tier + "*");
        	 return;
         }
         
         tierIds.put(roleId.get(0).getIdLong());
         s.setTierIds(tierIds);
         event.replySuccess("Set tier role *" + tier + "* to <@&" + tierIds.getLong(tier) + ">");
         
         
         
         
         
    }
}
