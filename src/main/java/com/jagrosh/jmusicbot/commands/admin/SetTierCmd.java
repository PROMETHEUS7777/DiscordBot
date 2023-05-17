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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONArray;
import org.json.JSONObject;

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
        this.arguments = "<tier number> <tier value> <role|NONE>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER, "tier", "the activity tier to set").setRequired(true));
        options.add(new OptionData(OptionType.INTEGER, "value", "the activity needed for this tier").setRequired(false));
        options.add(new OptionData(OptionType.ROLE, "role", "the role to set as this tier, leave empty to clear role").setRequired(false));

        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        int tier = event.getOption("tier").getAsInt();
        JSONObject tierData = new JSONObject();
        int value;
        Role role;
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        JSONArray tiers = s.getTiers();

        if(tiers.length() < tier)
        {
            event.reply("Must fill all lower tiers starting at 0 before filling tier *" + tier + "*").queue();
            return;
        }
        if (event.getOption("value") == null && event.getOption("role") != null){
            event.reply("please include a value for the activity tier").queue();
            return;
        }
        //check if clearing a role
        if(event.getOption("role") == null)
        {
            tiers.remove(tier);
            s.setTiers(tiers);
            event.reply("Tier *" + tier + "* cleared, any higher roles have been lowered by 1").queue();
            return;
        }
        value = event.getOption("value").getAsInt();
        role = event.getOption("role").getAsRole();
        if(tiers.length() < tier)
        {
            event.reply("Must fill all lower tiers starting at 0 before filling tier *" + tier + "*").queue();
            return;
        }
        tierData.put("id", role.getIdLong());
        tierData.put("value", value);
        tiers.put(tier, tierData);
        s.setTiers(tiers);
        event.reply("Set tier *" + tier + "* role to <@&" + tiers.getJSONObject(tier).getLong("id") + ">"
                + "\n and tier *" + tier + "* value to *" +tiers.getJSONObject(tier).getInt("value") + "*").queue();
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
    	 JSONObject tierData = new JSONObject();
    	 int value;
    	 List<Role> roleId;
    	 int spaceloc;
    	 int space2loc;
         
         
         
         //get tier
         spaceloc = event.getArgs().indexOf(" ");
    	 space2loc = event.getArgs().indexOf(" ",spaceloc + 1);
         
         if (spaceloc == -1)
         {
        	 event.replyError("Please include a `@role` or NONE");
        	 return;
         }
         if (space2loc == -1)
         {
        	 event.replyError("Please include a value for the tier");
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
         try 
         {
        	 value = Integer.parseInt(event.getArgs().substring(spaceloc + 1, space2loc));
         }
         catch(NumberFormatException e)
         {
        	 event.replyError("Invalid tier value, please enter a valid tier number");
        	 return;
         }
         Settings s = event.getClient().getSettingsFor(event.getGuild());
         JSONArray tiers = s.getTiers();
         
         //check if clearing a role
         if(event.getArgs().contains("NONE"))
         {
        	 tiers.remove(tier);
        	 s.setTiers(tiers);
        	 event.replySuccess("Tier *" + tier + "* cleared, any higher roles have been lowered by 1");
        	 return;
         }         
         //get roles mentioned
         roleId = event.getMessage().getMentions().getRoles();
         if(roleId.isEmpty())
         {
        	 event.replyError("Must include a role or NONE, do this by pinging the role you want to set as tier *" + tier + "*");
        	 return;
         }
         if(roleId.size() > 1)
         {
        	 event.replyError("Please only include 1 role");
        	 return;
         }                  
         if(tiers.length() < tier)
         {
        	 event.replyError("Must fill all lower tiers starting at 0 before filling tier *" + tier + "*");
        	 return;
         }
         tierData.put("id", roleId.get(0).getIdLong());
         tierData.put("value", value);
         tiers.put(tier, tierData);
         s.setTiers(tiers);
         event.replySuccess("Set tier *" + tier + "* role to <@&" + tiers.getJSONObject(tier).getLong("id") + ">"
         		+ "\n and tier *" + tier + "* value to *" +tiers.getJSONObject(tier).getInt("value") + "*");
    }
}
