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
public class SetPrefixCmd extends AdminCommand
{
    public SetPrefixCmd(Bot bot)
    {
        this.name = "setprefix";
        this.help = "sets a server-specific prefix";
        this.arguments = "<prefix|NONE>";
        this.aliases = bot.getConfig().getAliases(this.name);

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "prefix", "what to set the server-specific prefix to, use NONE to clear server prefix").setRequired(true));

        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event){
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        if(event.getOption("prefix").getAsString().equalsIgnoreCase("none"))
        {
            s.setPrefix(null);
            event.reply("Prefix cleared.").queue();
        }
        else
        {
            s.setPrefix(event.getOption("prefix").getAsString());
            event.reply("Server prefix set to `" + event.getOption("prefix").getAsString() + "` on *" + event.getGuild().getName() + "*").queue();
        }
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        if(event.getArgs().isEmpty())
        {
            event.replyError("Please include a prefix or NONE");
            return;
        }
        
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        if(event.getArgs().equalsIgnoreCase("none"))
        {
            s.setPrefix(null);
            event.replySuccess("Prefix cleared.");
        }
        else
        {
            s.setPrefix(event.getArgs());
            event.replySuccess("Custom prefix set to `" + event.getArgs() + "` on *" + event.getGuild().getName() + "*");
        }
    }
}
