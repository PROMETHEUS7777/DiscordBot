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
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.*;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class AboutCommand extends SlashCommand
{
	private boolean IS_AUTHOR = true;
	private String REPLACEMENT_ICON = "+";
	private final Color color;
	private final String description;
	private final Permission[] perms;
	private String oauthLink;
	private final String[] features;

	public AboutCommand(Color color, String description, String[] features, Permission... perms) {
		this.color = color;
		this.description = description;
		this.features = features;
		this.name = "about";
		this.help = "shows info about the bot";
		this.guildOnly = false;
		this.perms = perms;
		this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
	}

	public void setIsAuthor(boolean value) {
		this.IS_AUTHOR = value;
	}

	public void setReplacementCharacter(String value) {
		this.REPLACEMENT_ICON = value;
	}

	@Override
	protected void execute(SlashCommandEvent event) {
		if (this.oauthLink == null) {
			try {
				ApplicationInfo info = (ApplicationInfo)event.getJDA().retrieveApplicationInfo().complete();
				this.oauthLink = info.isBotPublic() ? info.getInviteUrl(0L, this.perms) : "";
			} catch (Exception var12) {
				Logger log = LoggerFactory.getLogger("OAuth2");
				log.error("Could not generate invite link ", var12);
				this.oauthLink = "";
			}
		}

		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(event.isFromType(ChannelType.TEXT) ? event.getGuild().getSelfMember().getColor() : this.color);
		builder.setAuthor("All about " + event.getJDA().getSelfUser().getName() + "!", (String)null, event.getJDA().getSelfUser().getAvatarUrl());
		boolean join = event.getClient().getServerInvite() != null && !event.getClient().getServerInvite().isEmpty();
		boolean inv = !this.oauthLink.isEmpty();
		String invline = "\n" + (join ? "Join my server [`here`](" + event.getClient().getServerInvite() + ")" : (inv ? "Please " : "")) + (inv ? (join ? ", or " : "") + "[`invite`](" + this.oauthLink + ") me to your server" : "") + "!";
		String author = event.getJDA().getUserById(event.getClient().getOwnerId()) == null ? "<@" + event.getClient().getOwnerId() + ">" : event.getJDA().getUserById(event.getClient().getOwnerId()).getName();
		StringBuilder descr = (new StringBuilder()).append("Hello! I am **").append(event.getJDA().getSelfUser().getName()).append("**, ").append(this.description).append("\nI ").append(this.IS_AUTHOR ? "was written in Java" : "am owned").append(" by **").append(author).append("** using Chew's [Commands Extension](https://github.com/Chew/JDA-Chewtils) (").append(JDAUtilitiesInfo.VERSION).append(") and the [JDA library](https://github.com/DV8FromTheWorld/JDA) (").append(JDAInfo.VERSION).append(")\nType `").append(event.getClient().getTextualPrefix()).append(event.getClient().getHelpWord()).append("` to see my commands!").append(!join && !inv ? "" : invline).append("\n\nSome of my features include: ```css");
		String[] var8 = this.features;
		int var9 = var8.length;

		for(int var10 = 0; var10 < var9; ++var10) {
			String feature = var8[var10];
			descr.append("\n").append(event.getClient().getSuccess().startsWith("<") ? this.REPLACEMENT_ICON : event.getClient().getSuccess()).append(" ").append(feature);
		}

		descr.append(" ```");
		builder.setDescription(descr);
		if (event.getJDA().getShardInfo() == JDA.ShardInfo.SINGLE) {
			builder.addField("Stats", event.getJDA().getGuilds().size() + " servers\n1 shard", true);
			builder.addField("Users", event.getJDA().getUsers().size() + " unique\n" + event.getJDA().getGuilds().stream().mapToInt((g) -> {
				return g.getMembers().size();
			}).sum() + " total", true);
			builder.addField("Channels", event.getJDA().getTextChannels().size() + " Text\n" + event.getJDA().getVoiceChannels().size() + " Voice", true);
		} else {
			builder.addField("Stats", event.getClient().getTotalGuilds() + " Servers\nShard " + (event.getJDA().getShardInfo().getShardId() + 1) + "/" + event.getJDA().getShardInfo().getShardTotal(), true);
			builder.addField("This shard", event.getJDA().getUsers().size() + " Users\n" + event.getJDA().getGuilds().size() + " Servers", true);
			builder.addField("", event.getJDA().getTextChannels().size() + " Text Channels\n" + event.getJDA().getVoiceChannels().size() + " Voice Channels", true);
		}

		builder.setFooter("Last restart", (String)null);
		builder.setTimestamp(event.getClient().getStartTime());
		event.replyEmbeds(builder.build()).queue();

	}

	@Override
	protected void execute(CommandEvent event) {
		if (this.oauthLink == null) {
			try {
				ApplicationInfo info = (ApplicationInfo)event.getJDA().retrieveApplicationInfo().complete();
				this.oauthLink = info.isBotPublic() ? info.getInviteUrl(0L, this.perms) : "";
			} catch (Exception var12) {
				Logger log = LoggerFactory.getLogger("OAuth2");
				log.error("Could not generate invite link ", var12);
				this.oauthLink = "";
			}
		}

		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(event.isFromType(ChannelType.TEXT) ? event.getGuild().getSelfMember().getColor() : this.color);
		builder.setAuthor("All about " + event.getJDA().getSelfUser().getName() + "!", (String)null, event.getJDA().getSelfUser().getAvatarUrl());
		boolean join = event.getClient().getServerInvite() != null && !event.getClient().getServerInvite().isEmpty();
		boolean inv = !this.oauthLink.isEmpty();
		String invline = "\n" + (join ? "Join my server [`here`](" + event.getClient().getServerInvite() + ")" : (inv ? "Please " : "")) + (inv ? (join ? ", or " : "") + "[`invite`](" + this.oauthLink + ") me to your server" : "") + "!";
		String author = event.getJDA().getUserById(event.getClient().getOwnerId()) == null ? "<@" + event.getClient().getOwnerId() + ">" : event.getJDA().getUserById(event.getClient().getOwnerId()).getName();
		StringBuilder descr = (new StringBuilder()).append("Hello! I am **").append(event.getJDA().getSelfUser().getName()).append("**, ").append(this.description).append("\nI ").append(this.IS_AUTHOR ? "was written in Java" : "am owned").append(" by **").append(author).append("** using Chew's [Commands Extension](https://github.com/Chew/JDA-Chewtils) (").append(JDAUtilitiesInfo.VERSION).append(") and the [JDA library](https://github.com/DV8FromTheWorld/JDA) (").append(JDAInfo.VERSION).append(")\nType `").append(event.getClient().getTextualPrefix()).append(event.getClient().getHelpWord()).append("` to see my commands!").append(!join && !inv ? "" : invline).append("\n\nSome of my features include: ```css");
		String[] var8 = this.features;
		int var9 = var8.length;

		for(int var10 = 0; var10 < var9; ++var10) {
			String feature = var8[var10];
			descr.append("\n").append(event.getClient().getSuccess().startsWith("<") ? this.REPLACEMENT_ICON : event.getClient().getSuccess()).append(" ").append(feature);
		}

		descr.append(" ```");
		builder.setDescription(descr);
		if (event.getJDA().getShardInfo() == JDA.ShardInfo.SINGLE) {
			builder.addField("Stats", event.getJDA().getGuilds().size() + " servers\n1 shard", true);
			builder.addField("Users", event.getJDA().getUsers().size() + " unique\n" + event.getJDA().getGuilds().stream().mapToInt((g) -> {
				return g.getMembers().size();
			}).sum() + " total", true);
			builder.addField("Channels", event.getJDA().getTextChannels().size() + " Text\n" + event.getJDA().getVoiceChannels().size() + " Voice", true);
		} else {
			builder.addField("Stats", event.getClient().getTotalGuilds() + " Servers\nShard " + (event.getJDA().getShardInfo().getShardId() + 1) + "/" + event.getJDA().getShardInfo().getShardTotal(), true);
			builder.addField("This shard", event.getJDA().getUsers().size() + " Users\n" + event.getJDA().getGuilds().size() + " Servers", true);
			builder.addField("", event.getJDA().getTextChannels().size() + " Text Channels\n" + event.getJDA().getVoiceChannels().size() + " Voice Channels", true);
		}

		builder.setFooter("Last restart", (String)null);
		builder.setTimestamp(event.getClient().getStartTime());
		event.reply(builder.build());

	}
}


