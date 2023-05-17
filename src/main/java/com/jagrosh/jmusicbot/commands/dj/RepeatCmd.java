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
package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.settings.RepeatMode;
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
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class RepeatCmd extends DJCommand
{
    private final String[] autoOptions = new String[]{"off", "all", "single"};

    public RepeatCmd(Bot bot)
    {
        super(bot);
        this.name = "repeat";
        this.help = "re-adds music to the queue when finished";
        this.arguments = "[off|all|single]";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "mode", "repeat mode to use: off, all, or single").setRequired(false).setAutoComplete(true));

        this.options = options;
    }
    
    // override musiccommand's execute because we don't actually care where this is used


    @Override
    protected void execute(SlashCommandEvent event) {
        //String args = event.getOption("mode").getAsString();
        RepeatMode value;
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        if(event.getOption("mode") == null)
        {
            if(settings.getRepeatMode() == RepeatMode.OFF)
                value = RepeatMode.ALL;
            else
                value = RepeatMode.OFF;
        }
        else if(event.getOption("mode").getAsString().equalsIgnoreCase("false") || event.getOption("mode").getAsString().equalsIgnoreCase("off"))
        {
            value = RepeatMode.OFF;
        }
        else if(event.getOption("mode").getAsString().equalsIgnoreCase("true") || event.getOption("mode").getAsString().equalsIgnoreCase("on") || event.getOption("mode").getAsString().equalsIgnoreCase("all"))
        {
            value = RepeatMode.ALL;
        }
        else if(event.getOption("mode").getAsString().equalsIgnoreCase("one") || event.getOption("mode").getAsString().equalsIgnoreCase("single"))
        {
            value = RepeatMode.SINGLE;
        }
        else
        {
            event.reply("Valid options are `off`, `all` or `single` (or leave empty to toggle between `off` and `all`)").queue();
            return;
        }
        settings.setRepeatMode(value);
        event.reply("Repeat mode is now `"+value.getUserFriendlyName()+"`").queue();
    }

    @Override
    protected void execute(CommandEvent event) 
    {
        String args = event.getArgs();
        RepeatMode value;
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        if(args.isEmpty())
        {
            if(settings.getRepeatMode() == RepeatMode.OFF)
                value = RepeatMode.ALL;
            else
                value = RepeatMode.OFF;
        }
        else if(args.equalsIgnoreCase("false") || args.equalsIgnoreCase("off"))
        {
            value = RepeatMode.OFF;
        }
        else if(args.equalsIgnoreCase("true") || args.equalsIgnoreCase("on") || args.equalsIgnoreCase("all"))
        {
            value = RepeatMode.ALL;
        }
        else if(args.equalsIgnoreCase("one") || args.equalsIgnoreCase("single"))
        {
            value = RepeatMode.SINGLE;
        }
        else
        {
            event.replyError("Valid options are `off`, `all` or `single` (or leave empty to toggle between `off` and `all`)");
            return;
        }
        settings.setRepeatMode(value);
        event.replySuccess("Repeat mode is now `"+value.getUserFriendlyName()+"`");
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals(this.name) && event.getFocusedOption().getName().equals("mode")) {
            List<Command.Choice> options = Stream.of(autoOptions)
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }

    @Override
    public void doDjCommand(SlashCommandEvent event){ /*Intentionally Empty */}

    @Override
    public void doDjCommand(CommandEvent event) { /* Intentionally Empty */ }
}
