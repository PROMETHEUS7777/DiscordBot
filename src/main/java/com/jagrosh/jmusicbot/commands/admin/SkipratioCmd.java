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
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class SkipratioCmd extends AdminCommand
{
    public SkipratioCmd(Bot bot)
    {
        this.name = "setskip";
        this.help = "sets a server-specific skip percentage";
        this.arguments = "<0 - 100>";
        this.aliases = bot.getConfig().getAliases(this.name);

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER, "percent", "the percentage of users in a voice channel required to vote skip a song, 0-100").setRequired(true));

        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        try
        {
            int val = event.getOption("percent").getAsInt();
            if( val < 0 || val > 100)
            {
                event.reply("The provided value must be between 0 and 100!").queue();
                return;
            }
            Settings s = event.getClient().getSettingsFor(event.getGuild());
            s.setSkipRatio(val / 100.0);
            event.reply("Skip percentage has been set to `" + val + "%` of listeners on *" + event.getGuild().getName() + "*").queue();
        }
        catch(NumberFormatException ex)
        {
            event.reply("Please include an integer between 0 and 100 (default is 55). This number is the percentage of listening users that must vote to skip a song.").queue();
        }
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        try
        {
            int val = Integer.parseInt(event.getArgs().endsWith("%") ? event.getArgs().substring(0,event.getArgs().length()-1) : event.getArgs());
            if( val < 0 || val > 100)
            {
                event.replyError("The provided value must be between 0 and 100!");
                return;
            }
            Settings s = event.getClient().getSettingsFor(event.getGuild());
            s.setSkipRatio(val / 100.0);
            event.replySuccess("Skip percentage has been set to `" + val + "%` of listeners on *" + event.getGuild().getName() + "*");
        }
        catch(NumberFormatException ex)
        {
            event.replyError("Please include an integer between 0 and 100 (default is 55). This number is the percentage of listening users that must vote to skip a song.");
        }
    }
}
