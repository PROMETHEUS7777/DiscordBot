package com.jagrosh.jmusicbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;

import java.io.File;
import java.io.IOException;

public class RestartCmd extends OwnerCommand
{
    private final Bot bot;

    public RestartCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "restart";
        this.help = "restarts the bot";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
        event.replyWarning("Restarting...");

        try {
            Process process = Runtime.getRuntime().exec("java -jar " + new File(RestartCmd.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName());
        } catch (IOException e) {
            event.replyError("Failed to Restart");
            e.printStackTrace();
            return;
        }
        bot.shutdown();
    }
}
