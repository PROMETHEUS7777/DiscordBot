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
package com.jagrosh.jmusicbot.settings;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import java.util.Collection;
import java.util.Collections;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class Settings implements GuildSettingsProvider
{
    private final SettingsManager manager;
    protected long textId;
    protected long voiceId;
    protected long roleId;
    protected boolean trackActivity;
    protected int voiceRatio;
    protected JSONArray tiers;
    public JSONObject activity;
    public JSONObject oldActivity;
    private int volume;
    private String defaultPlaylist;
    private RepeatMode repeatMode;
    private String prefix;
    private double skipRatio;

    public Settings(SettingsManager manager, String textId, String voiceId, String roleId, boolean trackActivity, int voiceRatio, JSONArray tiers, JSONObject activity, JSONObject oldActivity, int volume, String defaultPlaylist, RepeatMode repeatMode, String prefix, double skipRatio)
    {
        this.manager = manager;
        try
        {
            this.textId = Long.parseLong(textId);
        }
        catch(NumberFormatException e)
        {
            this.textId = 0;
        }
        try
        {
            this.voiceId = Long.parseLong(voiceId);
        }
        catch(NumberFormatException e)
        {
            this.voiceId = 0;
        }
        try
        {
            this.roleId = Long.parseLong(roleId);
        }
        catch(NumberFormatException e)
        {
            this.roleId = 0;
        }
        this.trackActivity = trackActivity;
        this.voiceRatio = voiceRatio;
        this.tiers = tiers;
        this.activity = activity;
        this.oldActivity = oldActivity;
        this.volume = volume;
        this.defaultPlaylist = defaultPlaylist;
        this.repeatMode = repeatMode;
        this.prefix = prefix;
        this.skipRatio = skipRatio;
    }
    
    public Settings(SettingsManager manager, long textId, long voiceId, long roleId, boolean trackActivity, int voiceRatio, JSONArray tiers, JSONObject activity, JSONObject oldActivity, int volume, String defaultPlaylist, RepeatMode repeatMode, String prefix, double skipRatio)
    {
        this.manager = manager;
        this.textId = textId;
        this.voiceId = voiceId;
        this.roleId = roleId;
        this.trackActivity = trackActivity;
        this.voiceRatio = voiceRatio;
        this.tiers = tiers;
        this.activity = activity;
        this.oldActivity = oldActivity;
        this.volume = volume;
        this.defaultPlaylist = defaultPlaylist;
        this.repeatMode = repeatMode;
        this.prefix = prefix;
        this.skipRatio = skipRatio;
    }
    
    // Getters
    public TextChannel getTextChannel(Guild guild)
    {
        return guild == null ? null : guild.getTextChannelById(textId);
    }
    
    public VoiceChannel getVoiceChannel(Guild guild)
    {
        return guild == null ? null : guild.getVoiceChannelById(voiceId);
    }
    
    public Role getRole(Guild guild)
    {
        return guild == null ? null : guild.getRoleById(roleId);
    }
    
    public boolean getTracking()
    {
    	return trackActivity;
    }
    
    public int getVoiceRatio()
    {
    	return voiceRatio;
    }
    
    public JSONArray getTiers()
    {
    	return tiers;
    }
    
    public JSONObject getActivity()
    {
    	return activity;
    }
    
    public JSONObject getOldActivity()
    {
    	return oldActivity;
    }
    
    public int getVolume()
    {
        return volume;
    }
    
    public String getDefaultPlaylist()
    {
        return defaultPlaylist;
    }
    
    public RepeatMode getRepeatMode()
    {
        return repeatMode;
    }
    
    public String getPrefix()
    {
        return prefix;
    }
    
    public double getSkipRatio()
    {
        return skipRatio;
    }

    @Override
    public Collection<String> getPrefixes()
    {
        return prefix == null ? Collections.EMPTY_SET : Collections.singleton(prefix);
    }
    
    // Setters
    public void setTextChannel(TextChannel tc)
    {
        this.textId = tc == null ? 0 : tc.getIdLong();
        this.manager.writeSettings();
    }
    
    public void setVoiceChannel(VoiceChannel vc)
    {
        this.voiceId = vc == null ? 0 : vc.getIdLong();
        this.manager.writeSettings();
    }
    
    public void setDJRole(Role role)
    {
        this.roleId = role == null ? 0 : role.getIdLong();
        this.manager.writeSettings();
    }
    
    public void setTrackActivity(boolean temp)
    {
        this.trackActivity = temp;
        this.manager.writeSettings();
    }
    
    public void setVoiceRatio(int ratio)
    {
    	this.voiceRatio = ratio;
    	this.manager.writeSettings();
    }
    
    public void setTiers(JSONArray tiers)
    {
    	this.tiers = tiers;
    	this.manager.writeSettings();
    }
    
    public void setActivity(JSONObject activity)
    {
    	this.activity = activity;
    	this.manager.writeSettings();
    }
    
    public void setOldActivity(JSONObject oldActivity)
    {
    	this.oldActivity = oldActivity;
    	this.manager.writeSettings();
    }
    
    public void setVolume(int volume)
    {
        this.volume = volume;
        this.manager.writeSettings();
    }
    
    public void setDefaultPlaylist(String defaultPlaylist)
    {
        this.defaultPlaylist = defaultPlaylist;
        this.manager.writeSettings();
    }
    
    public void setRepeatMode(RepeatMode mode)
    {
        this.repeatMode = mode;
        this.manager.writeSettings();
    }
    
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
        this.manager.writeSettings();
    }

    public void setSkipRatio(double skipRatio)
    {
        this.skipRatio = skipRatio;
        this.manager.writeSettings();
    }
}
