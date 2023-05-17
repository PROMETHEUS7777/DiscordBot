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
import java.util.List;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetvcCmd extends AdminCommand 
{
    public SetvcCmd(Bot bot)
    {
        this.name = "setvc";
        this.help = "sets the voice channel for playing music";
        this.arguments = "<channel|NONE>";
        this.aliases = bot.getConfig().getAliases(this.name);

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.CHANNEL, "channel", "the channel to allow music, leave empty to clear channel").setRequired(false));

        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        if(event.getOption("channel") == null)
        {
            s.setVoiceChannel(null);
            event.reply(event.getClient().getSuccess()+" Music can now be played in any channel").queue();
        }
        else
        {
            List<VoiceChannel> list = FinderUtil.findVoiceChannels(event.getOption("channel").getAsChannel().getId(), event.getGuild());
            if(list.isEmpty())
                event.reply(event.getClient().getWarning()+" No Voice Channels found matching \""+event.getOption("channel").getAsChannel()+"\"").queue();
            else if (list.size()>1)
                event.reply(event.getClient().getWarning()+FormatUtil.listOfVChannels(list.stream().map(vc -> (AudioChannelUnion)vc).toList(), event.getOption("channel").getAsChannel().getId())).queue();
            else
            {
                s.setVoiceChannel(list.get(0));
                event.reply(event.getClient().getSuccess()+" Music can now only be played in "+list.get(0).getAsMention()).queue();
            }
        }
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        if(event.getArgs().isEmpty())
        {
            event.reply(event.getClient().getError()+" Please include a voice channel or NONE");
            return;
        }
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        if(event.getArgs().equalsIgnoreCase("none"))
        {
            s.setVoiceChannel(null);
            event.reply(event.getClient().getSuccess()+" Music can now be played in any channel");
        }
        else
        {
            List<VoiceChannel> list = FinderUtil.findVoiceChannels(event.getArgs(), event.getGuild());
            if(list.isEmpty())
                event.reply(event.getClient().getWarning()+" No Voice Channels found matching \""+event.getArgs()+"\"");
            else if (list.size()>1)
                event.reply(event.getClient().getWarning()+FormatUtil.listOfVChannels(list.stream().map(vc -> (AudioChannelUnion)vc).toList(), event.getArgs()));
            else
            {
                s.setVoiceChannel(list.get(0));
                event.reply(event.getClient().getSuccess()+" Music can now only be played in "+list.get(0).getAsMention());
            }
        }
    }
}
