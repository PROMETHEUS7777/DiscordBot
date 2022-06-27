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
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class TrackActivityCmd extends AdminCommand
{
    public TrackActivityCmd(Bot bot)
    {
        this.name = "trackactivity";
        this.help = "starts tracking server activity for use with activity roles";
        this.arguments = "<start|stop>";
        this.aliases = bot.getConfig().getAliases(this.name);
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
    	
    	Settings s = event.getClient().getSettingsFor(event.getGuild());
        if(event.getArgs().isEmpty())
        {
        	
        	if(s.getTracking()) 
        	{
        		event.replySuccess("Activity tracking currently enabled for *" + event.getGuild().getName() + "*\n"
        				+ "To disable activity tracking, use `" + event.getClient().getPrefix() + "trackactivity stop`");
        	}
        	else
        	{
        		event.replySuccess("Activity tracking currently disabled for *" + event.getGuild().getName() + "*\n"
        				+ "To enable activity tracking, use `" + event.getClient().getPrefix() + "trackactivity start`");
        	}
        	return;
        }
        
        
        if(event.getArgs().equalsIgnoreCase("start"))
        {
            s.setTrackActivity(true);
            event.replySuccess("Activity tracking started for *" + event.getGuild().getName() + "*");
        }
        else if(event.getArgs().equalsIgnoreCase("stop"))
        {
            s.setTrackActivity(false);
            event.replySuccess("Activity tracking stopped for *" + event.getGuild().getName() + "*");
        }
        else
        {
        	event.replyError("Please include either start or stop");
        }
    }
}
