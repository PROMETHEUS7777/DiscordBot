/*
 * Copyright 2017 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands.Media;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.MediaCommand;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class CaptionCommand extends MediaCommand
{
    
    
    public CaptionCommand(Bot bot)
    {
        
        this.name = "caption";
        this.help = "adds a caption to an image or gif";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.arguments = "<caption text> <image/gif>";
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
    	//check if there is actually a caption and an image/gif
        if(event.getMessage().getAttachments().isEmpty() || event.getMessage().getEmbeds().isEmpty())
        {
        	event.replyError("No attachment or embed");
        	return;
        }
        
        if(event.getArgs() == "") 
        {
        	event.replyError("No caption text");
        	return;
        }
        
        //download image/gif to blob
        
        
        
    }
}
