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
package com.jagrosh.jmusicbot.utils;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import magick.DrawInfo;
import magick.MagickException;
import magick.MagickImage;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class ImageUtil {
	
	public static class CapMetrics{
		String caption = "";
		int lineHeight = 0;
		int totalHeight = 0;
		int lines = 1;
		
		public String getCaption() {
			return caption;
		}
		public int getLineHeight() {
			return lineHeight;
		}
		public int getTotalHeight() {
			return totalHeight;
		}
		public int getLines() {
			return lines;
		}
	}
    
	public static CapMetrics makeCaption(int width, StringBuilder caption, DrawInfo draw) throws MagickException {
		
		//loop through the caption, keep track of last whitespace, last whitespace starts at the begginging, at each whitespace, check if it's too long, if it is, add a newline at last whitespace 
		//before all of this see if it even needs a new line at all, if it doesn't then just return caption
		
		
		Font f = new Font(draw.getFont(),Font.PLAIN, (int) draw.getPointsize());
		FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
		int cWidth = (int)(f.getStringBounds(caption.toString(), frc).getWidth());
		CapMetrics retmet = new CapMetrics();
		retmet.lineHeight = (int)(f.getStringBounds(caption.toString(), frc).getHeight());
		int maxWidth = (int) (width * 0.9);
		
		//if no newlines are needed, set retmet and return it
		if(cWidth < maxWidth) {
			retmet.caption = caption.toString();
			retmet.totalHeight = retmet.lineHeight;
			return retmet;
		}
		
		int lastWhite = 0;
		int lastNewLine = 0;
		StringBuilder retCaption = new StringBuilder(caption);
		
		//loop through the caption
		for (int i = 0; i < retCaption.length(); i++) {
			
			//if at a whitespace
			if(Character.isWhitespace(retCaption.charAt(i))) {
				
				//if it's to wide, add a newline
				if ((int)(f.getStringBounds(retCaption.substring(lastNewLine, i), frc).getWidth()) > maxWidth){
					retCaption.setCharAt(lastWhite, '\n');
					lastNewLine = lastWhite;
					retmet.lines++;
				}
				//update lastWhite
				lastWhite = i;
			}
		}
		
		//put the caption in retmet and return it
		retmet.caption = retCaption.toString();
		retmet.totalHeight = retmet.lineHeight * retmet.lines;
		return retmet;
	}
	
	public static MagickImage[] removeOtherElements(MagickImage[] arg) {
		MagickImage[] ret = new MagickImage[arg.length/2];
		for(int i = 0; i < ret.length; i++) {
			ret[i] = arg[i*2];
		}
		return ret;
	}
}
