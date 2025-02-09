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
package com.jagrosh.jmusicbot;

import com.jagrosh.jmusicbot.utils.OtherUtil;
import com.jagrosh.jmusicbot.roles.UpdateActivityRoles;
import com.jagrosh.jmusicbot.settings.Settings;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class Listener extends ListenerAdapter
{
    private final Bot bot;
    
    public Listener(Bot bot)
    {
        this.bot = bot;
    }
    
    @Override
    public void onReady(ReadyEvent event) 
    {
        if(event.getJDA().getGuildCache().isEmpty())
        {
            Logger log = LoggerFactory.getLogger("MusicBot");
            log.warn("This bot is not on any guilds! Use the following link to add the bot to your guilds!");
            log.warn(event.getJDA().getInviteUrl(JMusicBot.RECOMMENDED_PERMS));
        }
        event.getJDA().getGuilds().forEach((guild) -> 
        {
            try
            {
                String defpl = bot.getSettingsManager().getSettings(guild).getDefaultPlaylist();
                AudioChannelUnion vc = (AudioChannelUnion) bot.getSettingsManager().getSettings(guild).getVoiceChannel(guild);
                if(defpl!=null && vc!=null && bot.getPlayerManager().setUpHandler(guild).playFromDefault())
                {
                    guild.getAudioManager().openAudioConnection(vc);
                }
            }
            catch(Exception ignore) {}
        });
        if(bot.getConfig().useUpdateAlerts())
        {
            bot.getThreadpool().scheduleWithFixedDelay(() -> 
            {
                try
                {
                    User owner = bot.getJDA().retrieveUserById(bot.getConfig().getOwnerId()).complete();
                    String currentVersion = OtherUtil.getCurrentVersion();
                    String latestVersion = OtherUtil.getLatestVersion();
                    if(latestVersion!=null && !currentVersion.equalsIgnoreCase(latestVersion))
                    {
                        String msg = String.format(OtherUtil.NEW_VERSION_AVAILABLE, currentVersion, latestVersion);
                        owner.openPrivateChannel().queue(pc -> pc.sendMessage(msg).queue());
                    }
                }
                catch(Exception ex) {} // ignored
            }, 0, 24, TimeUnit.HOURS);
        }
    }

	//i can't figure out why it tracks the now playing messages, so it's going to stop doing that
//    @Override
//    public void onMessageDelete(MessageDeleteEvent event)
//    {
//        bot.getNowplayingHandler().onMessageDelete(event.getGuild(), event.getMessageIdLong());
//    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) 
    {
        //check if it's a bot
    	if (event.getAuthor().isBot()) 
    	{
    		return;
    	}
    	//check if it's a direct message
    	if (!event.isFromGuild())
    	{
    		return;
    	}
    	//check if the server has tracking on
    	Settings s = bot.getSettingsManager().getSettings(event.getGuild());
    	if (!s.getTracking())
    	{
    		return;
    	}
    	//find what user sent it, and put it in activity
    	JSONObject activity = s.getActivity();
    	
    	//if user is already in activity, then increase their message 
    	if (activity.has(event.getAuthor().getId()))
    	{
    		JSONObject uAct = activity.getJSONObject(event.getAuthor().getId());
    		uAct.put("msgs", uAct.getInt("msgs") + 1);
    		activity.put(event.getAuthor().getId(), uAct);
    		s.setActivity(activity);
    		UpdateActivityRoles uRoles = new UpdateActivityRoles();
    		uRoles.UpdatemActRoles(event.getMember());
    		return;
    	}
    	//if user isnt already in activity, add them
    	else
    	{
    		JSONObject uAct = new JSONObject();
    		uAct.put("msgs", 1);
    		uAct.put("voice", 0);
    		uAct.put("last", -1);
    		activity.put(event.getAuthor().getId(), uAct);
    		s.setActivity(activity);
    		UpdateActivityRoles uRoles = new UpdateActivityRoles();
    		uRoles.UpdatemActRoles(event.getMember());
    		return;
    	}
    }

    public void onGuildVoiceJoin(GuildVoiceUpdateEvent event)
    {
    	//check if it's a bot
    	if (event.getMember().getUser().isBot()) 
    	{
    		return;
    	}
    	Settings s = bot.getSettingsManager().getSettings(event.getGuild());
    	//check if the server has tracking on
    	if (!s.getTracking())
    	{
    		return;
    	}
    	//find what user joined, and put it in activity
    	JSONObject activity = s.getActivity();
    	
    	//if user is already in activity, then increase their message 
    	if (activity.has(event.getMember().getId()))
    	{
    		JSONObject uAct = activity.getJSONObject(event.getMember().getId());
    		uAct.put("last", Instant.now().getEpochSecond());
    		activity.put(event.getMember().getId(), uAct);
    		s.setActivity(activity);
    		UpdateActivityRoles uRoles = new UpdateActivityRoles();
    		uRoles.UpdatemActRoles(event.getMember());
    		return;
    	}
    	//if user isnt already in activity, add them
    	else
    	{
    		JSONObject uAct = new JSONObject();
    		uAct.put("msgs", 0);
    		uAct.put("voice", 0);
    		uAct.put("last", Instant.now().getEpochSecond());
    		activity.put(event.getMember().getId(), uAct);
    		s.setActivity(activity);
    		UpdateActivityRoles uRoles = new UpdateActivityRoles();
    		uRoles.UpdatemActRoles(event.getMember());
    		return;
    	}
    	
    }
    

    public void onGuildVoiceLeave(GuildVoiceUpdateEvent event)
    {
    	//check if it's a bot
    	if (event.getMember().getUser().isBot()) 
    	{
    		return;
    	}
    	Settings s = bot.getSettingsManager().getSettings(event.getGuild());
    	//check if the server has tracking on
    	if (!s.getTracking())
    	{
    		return;
    	}
    	//find what user joined, and put it in activity
    	JSONObject activity = s.getActivity();
    	
    	//if user is already in activity, then increase their message 
    	if (activity.has(event.getMember().getId()))
    	{
    		// if last isnt -1, add the number of minutes user was in vc to voice
    		JSONObject uAct = activity.getJSONObject(event.getMember().getId());
    		if(uAct.getLong("last") != -1) 
    		{
    			uAct.put("voice", uAct.getInt("voice") + ((Instant.now().getEpochSecond() - uAct.getLong("last"))/60));
    			uAct.put("last", -1);
    			
    			activity.put(event.getMember().getId(), uAct);
        		s.setActivity(activity);
        		UpdateActivityRoles uRoles = new UpdateActivityRoles();
        		uRoles.UpdatemActRoles(event.getMember());
    		}
    		return;
    	}
    	//if user isnt already in activity, add them
    	else
    	{
    		JSONObject uAct = new JSONObject();
    		uAct.put("msgs", 0);
    		uAct.put("voice", 0);
    		uAct.put("last", -1);
    		activity.put(event.getMember().getId(), uAct);
    		s.setActivity(activity);
    		UpdateActivityRoles uRoles = new UpdateActivityRoles();
    		uRoles.UpdatemActRoles(event.getMember());
    		return;
    	}
    	
    }
    
    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event)
    {
		if (event.getOldValue() != null){
			onGuildVoiceLeave(event);
		}
		if (event.getNewValue() != null){
			onGuildVoiceJoin(event);
		}
        bot.getAloneInVoiceHandler().onVoiceUpdate(event);
    }

    @Override
    public void onShutdown(ShutdownEvent event) 
    {
        bot.shutdown();
    }
}
