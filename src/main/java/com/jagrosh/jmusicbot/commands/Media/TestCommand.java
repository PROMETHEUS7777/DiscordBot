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
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.MediaCommand;
import net.dv8tion.jda.api.Permission;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class TestCommand extends MediaCommand
{
    
    
    public TestCommand(Bot bot)
    {
        
        this.name = "test";
        this.help = "replies with test";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        event.reply("https://i.imgflip.com/6a8fc2.gif\ntest \n from slash command").queue();
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.reply("https://i.imgflip.com/6a8fc2.gif\ntest \n from normal prefix");
    }
}
