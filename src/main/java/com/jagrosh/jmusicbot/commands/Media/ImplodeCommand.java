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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.MediaCommand;

import magick.ImageInfo;
import magick.Magick;
import magick.MagickException;
import magick.MagickImage;
import net.dv8tion.jda.api.MessageBuilder;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class ImplodeCommand extends MediaCommand
{
    
    
    public ImplodeCommand(Bot bot)
    {
        
        this.name = "implode";
        this.help = "implode an image or gif";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.arguments = "<image/gif>";
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
    	//check if there is actually an image/gif
        if(event.getMessage().getAttachments().isEmpty() && event.getMessage().getEmbeds().isEmpty())
        {
        	if(event.getArgs().indexOf("https://") == -1) {
        		event.replyError("No attachment or embed");
            	return;
        	}
        }
        
        byte[] blob;
        URL url = null;
        String filename = null;
        
        //get url and filename of attachment/embed
        
    	try {
    		if(!event.getMessage().getAttachments().isEmpty()) {
    			url = new URL(event.getMessage().getAttachments().get(0).getUrl());
    			filename = event.getMessage().getAttachments().get(0).getFileName();
    		}
    		if(!event.getMessage().getEmbeds().isEmpty()) {
    			String tempurl = event.getMessage().getEmbeds().get(0).getUrl();
    			url = new URL(tempurl);
    			filename = tempurl.substring(tempurl.lastIndexOf('/'));
    		}
    		if(event.getMessage().getAttachments().isEmpty() && event.getMessage().getEmbeds().isEmpty()) {
            	String tempurl = event.getArgs().substring(event.getArgs().indexOf("https://"), event.getArgs().indexOf(' '));
    			url = new URL(tempurl);
            	filename = tempurl.substring(tempurl.lastIndexOf('/'));
            }
    		if(url == null) {
    			event.replyError("Unable to get url of image/gif, please send it as an attachment or embed");
    			return;
    		}			
		} catch (MalformedURLException e) {
			event.replyError("Something fucked up");
			e.printStackTrace();
			return;
		}
    	
    	//download blob from url
    	try(InputStream iStream = url.openStream()){
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = iStream.read(data, 0, data.length)) != -1) {
			  baos.write(data, 0, nRead);
			}
			blob = baos.toByteArray();
		} catch (IOException e) {
			event.replyError("Unable to download image, please try again");
			e.printStackTrace();
			return;
		}
    	
    	//make image
    	MagickImage image;
    	ImageInfo info;
    	try {
			image = new MagickImage(info = new ImageInfo(),blob);
		} catch (MagickException e) {
			event.replyError("what did you send me");
			e.printStackTrace();
			return;
		}
    	
    	//implode the image
    	try {
			image = image.implodeImage(0.5);
		} catch (MagickException e) {
			event.replyError("Failed to implode");
			e.printStackTrace();
			return;
		}
    	
    	//return to blob
    	if(filename.indexOf('.') == -1)
		{
    		try {
    			filename += '.' + image.getImageFormat();
    		} catch (MagickException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
		}
    	
    	blob = image.imageToBlob(info);
    	
    	event.getChannel().sendFile(blob, "imploded_" + filename).queue();
    	
    	
    }
}
