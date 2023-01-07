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

import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.MediaCommand;
import com.jagrosh.jmusicbot.utils.ImageUtil;
import com.jagrosh.jmusicbot.utils.ImageUtil.CapMetrics;

import magick.DrawInfo;
import magick.ImageInfo;
import magick.Magick;
import magick.MagickException;
import magick.MagickImage;
import magick.PixelPacket;
import magick.ProfileInfo;
import magick.TypeMetric;
import net.dv8tion.jda.api.entities.Message;

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
    
    
    
    
    
    
    
    
    // need to find a way to merge 2 images together, and need to find how caption in the cmd works
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
        
        //check if there is a caption
        if (event.getArgs().isBlank() || event.getArgs().lastIndexOf("https://") == 0) {
        	event.replyError("No caption");
        	return;
        }
        
        byte[] blob;
        URL url = null;
        String filename = null;
        String caption = null;
               
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
            	String tempurl = event.getArgs().substring(event.getArgs().lastIndexOf("https://"));
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
    	
    	//get caption
    	if(event.getMessage().getAttachments().isEmpty()) {
    		caption = event.getArgs().substring(0, event.getArgs().lastIndexOf(url.toString()));
    	}
    	else {
    		caption = event.getArgs();
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
			return;
		}
    	
    	//make image
    	MagickImage image;
    	ImageInfo info;
    	try {
			image = new MagickImage(info = new ImageInfo(),blob);
			
		} catch (MagickException e) {
			event.replyError("That's either not an image/gif or a bad link.\nIf it was from tenor, or a similar site, go and get the link to the actual gif.");
			return;
		}
    	
    	//
    	Message pmsg = event.getChannel().sendMessage("Processing...").complete();
    	
    	
    	
    	
    	//convert image to array
    	MagickImage[] iarray= null;
    	try {
			iarray = image.breakFrames();
		} catch (MagickException e2) {
			event.replyError("Failed to caption");
			e2.printStackTrace();
			return;
		}
    	
    	
    	//generate caption
    	MagickImage icap = new MagickImage();
    	ImageInfo capinfo = null;
    	DrawInfo dinfo =  null;
    	double caph = 0;
    	
    	try {
    		int height2;
    		int width2 = image.getDimension().width;
    		dinfo = new DrawInfo(new ImageInfo());
    		capinfo = new ImageInfo();
    		dinfo = new DrawInfo(capinfo);
    		dinfo.setFont("caption-font.otf");
    		dinfo.setGravity(5);
    		dinfo.setPointsize(width2/10);
    		dinfo.setUnderColor(PixelPacket.queryColorDatabase("white"));
    		icap.allocateImage(capinfo);
    		
    		//format the caption
    		
    		CapMetrics cm = ImageUtil.makeCaption(width2, new StringBuilder(caption), dinfo);
    		caph = cm.getTotalHeight() * 1.2;
    		dinfo.setText(cm.getCaption());
    		height2 = (int) (caph);
    		
    		
    		
    		byte[] cappixels = new byte[(int) (height2 * width2 * 4)];
    		for (int i = 0; i < height2 * width2; i++) {
                cappixels[4 * i] = (byte) 255;
                cappixels[4 * i + 1] = (byte) 255;
                cappixels[4 * i + 2] = (byte) 255;
                cappixels[4 * i + 3] = (byte) 1;
            }
    		icap.constituteImage(width2, height2, "RGBA", cappixels);
    		icap.setMagick("PNG");   		
    		icap.annotateImage(dinfo);
    		
    		icap.setFileName("icap.png");
    		//icap.writeImage(capinfo);
    		
    		
    		
		} catch (MagickException e3) {
			
			e3.printStackTrace();
			event.replyError("Failed to caption");
			return;
		}
    	
    	
    	//caption image
    	try {
    		
    		int iwidth = image.getDimension().width;
    		int iheight = image.getDimension().height;
	    	for (int i = 0; i < iarray.length; i++) {
	    		iarray[i] = iarray[i].extentImage(iwidth, (int) (iheight + caph), 8);
	    		iarray[i].compositeImage(0, icap, 0, 0);
	    		
			  }
    	} catch (MagickException e) {
    		e.printStackTrace();
    	}
    	
    	
    	//return to image
    	try {
			image = new MagickImage(iarray);
		} catch (MagickException e1) {
			e1.printStackTrace();
		}
    	
    	//if filename has no format, put a format on it
    	if(filename.indexOf('.') == -1)
		{
    		try {
    			filename += '.' + image.getImageFormat();
    		} catch (MagickException e) {
    			event.replyError("Failed to caption");
    			e.printStackTrace();
    			return;
    		}
		}
    	
		/*
		 * //if it's to big, try making it smaller until it isn't to big try {
		 * 
		 * if(image.sizeBlob() >= 8000000) { image.strip();
		 * event.getChannel().editMessageById(pmsg.getId(),"Result too large to upload")
		 * .queue(); return; }
		 * 
		 * 
		 * } catch (MagickException e) { e.printStackTrace(); }
		 */
    	
    	
    	//return image to blob
    	blob = image.imagesToBlob(info);
    	
    	//update processing message
    	event.getChannel().editMessageById(pmsg.getId(),"Uploading result...").queue();
    	
    	//send finished image/gif
    	event.getChannel().sendFile(blob, "captioned_" + filename).queue();
    	
    	//finalize processing message
    	event.getChannel().editMessageById(pmsg.getId(), caption ).queue();
    	
    	
    	
    }
}
