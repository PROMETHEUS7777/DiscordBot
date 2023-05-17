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
package com.jagrosh.jmusicbot.commands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.json.JSONArray;

import java.time.temporal.ChronoUnit;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PingCommand extends SlashCommand
{

	public PingCommand() {
		this.name = "ping";
		this.help = "checks the bot's latency";
		this.guildOnly = false;
		this.aliases = new String[]{"pong"};
	}
	@Override
	protected void execute(CommandEvent event) {
		event.reply("Ping: ...", (m) -> {
			long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
			m.editMessage("Ping: " + ping + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms").queue();
		});
	}

	@Override
	protected void execute(SlashCommandEvent event){


		event.deferReply().queue(
				h -> {h.deleteOriginal().queue();}
		);
		Message m = event.getChannel().sendMessage("ping: ...").complete();
		long ping = event.getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
		m.editMessage("Ping: " + ping + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms").queue();

	}
}
