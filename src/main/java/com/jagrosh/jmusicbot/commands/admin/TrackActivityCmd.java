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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Liam Ussery
 */
public class TrackActivityCmd extends AdminCommand
{
    private final String[] autoOptions = new String[]{"stop", "start"};
    public TrackActivityCmd(Bot bot)
    {
        this.name = "trackactivity";
        this.help = "starts tracking server activity for use with activity roles";
        this.arguments = "<start|stop>";
        this.aliases = bot.getConfig().getAliases(this.name);

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "option", "start or stop, leave empty to get current").setRequired(false).setAutoComplete(true));

        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {

        Settings s = event.getClient().getSettingsFor(event.getGuild());
        if(event.getOption("option") == null)
        {

            if(s.getTracking())
            {
                event.reply("Activity tracking currently enabled for **" + event.getGuild().getName() + "**\n"
                        + "To disable activity tracking, use `/trackactivity stop`").queue();
            }
            else
            {
                event.reply("Activity tracking currently disabled for **" + event.getGuild().getName() + "**\n"
                        + "To enable activity tracking, use `/trackactivity start`").queue();
            }
            return;
        }

        if(event.getOption("option").getAsString().equalsIgnoreCase("start"))
        {
            s.setTrackActivity(true);
            event.reply("Activity tracking started for **" + event.getGuild().getName() + "**").queue();
        }
        else if(event.getOption("option").getAsString().equalsIgnoreCase("stop"))
        {
            s.setTrackActivity(false);
            event.reply("Activity tracking stopped for **" + event.getGuild().getName() + "**").queue();
        }
        else
        {
            event.reply("Please include either start or stop").queue();
        }
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
    	
    	Settings s = event.getClient().getSettingsFor(event.getGuild());
        if(event.getArgs().isEmpty())
        {
        	
        	if(s.getTracking()) 
        	{
        		event.replySuccess("Activity tracking currently enabled for **" + event.getGuild().getName() + "**\n"
        				+ "To disable activity tracking, use `" + event.getClient().getPrefix() + "trackactivity stop`");
        	}
        	else
        	{
        		event.replySuccess("Activity tracking currently disabled for **" + event.getGuild().getName() + "**\n"
        				+ "To enable activity tracking, use `" + event.getClient().getPrefix() + "trackactivity start`");
        	}
        	return;
        }

        if(event.getArgs().equalsIgnoreCase("start"))
        {
            s.setTrackActivity(true);
            event.replySuccess("Activity tracking started for **" + event.getGuild().getName() + "**");
        }
        else if(event.getArgs().equalsIgnoreCase("stop"))
        {
            s.setTrackActivity(false);
            event.replySuccess("Activity tracking stopped for **" + event.getGuild().getName() + "**");
        }
        else
        {
        	event.replyError("Please include either start or stop");
        }
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals(this.name) && event.getFocusedOption().getName().equals("option")) {
            List<Command.Choice> options = Stream.of(autoOptions)
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }
}
