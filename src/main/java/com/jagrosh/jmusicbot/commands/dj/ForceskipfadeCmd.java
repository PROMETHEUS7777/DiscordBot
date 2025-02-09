/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
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
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.RequestMetadata;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.settings.Settings;

import net.dv8tion.jda.api.entities.User;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class ForceskipfadeCmd extends DJCommand 
{
    public ForceskipfadeCmd(Bot bot)
    {
        super(bot);
        this.name = "forceskipfade";
        this.help = "fades out and skips the current song";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;
    }

    @Override
    public void doDjCommand(SlashCommandEvent event)
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        RequestMetadata rm = handler.getRequestMetadata();
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        event.reply(event.getClient().getSuccess()+" Faded and skipped **"+handler.getPlayer().getPlayingTrack().getInfo().title
                +"** "+(rm.getOwner() == 0L ? "(autoplay)" : "(requested by **" + rm.user.username + "**)")).queue();
        int volume = handler.getPlayer().getVolume();
        //loop to decrease volume until 0
        for (int tvol = volume; tvol > 0; tvol--) {
            handler.getPlayer().setVolume(tvol);
            settings.setVolume(tvol);
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
            }
        }
        handler.getPlayer().stopTrack();
        handler.getPlayer().setVolume(volume);
        settings.setVolume(volume);
    }

    @Override
    public void doDjCommand(CommandEvent event)
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        RequestMetadata rm = handler.getRequestMetadata();
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        event.reply(event.getClient().getSuccess()+" Faded and skipped **"+handler.getPlayer().getPlayingTrack().getInfo().title
                +"** "+(rm.getOwner() == 0L ? "(autoplay)" : "(requested by **" + rm.user.username + "**)"));
        int volume = handler.getPlayer().getVolume();
        //loop to decrease volume until 0
        for (int tvol = volume; tvol > 0; tvol--) {
        	handler.getPlayer().setVolume(tvol);
        	settings.setVolume(tvol);
        	try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
        }
        handler.getPlayer().stopTrack();
        handler.getPlayer().setVolume(volume);
        settings.setVolume(volume);
    }
}
