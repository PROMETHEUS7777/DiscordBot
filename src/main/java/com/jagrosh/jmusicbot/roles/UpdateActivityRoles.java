package com.jagrosh.jmusicbot.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.entities.UserSnowflake;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.settings.SettingsManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class UpdateActivityRoles {

	private final SettingsManager settings = new SettingsManager();
	
	public void UpdatemActRoles(Member member)
	{
		Settings s  = settings.getSettings(member.getGuild().getIdLong());
		
		//find what tier member should have
		int sTier = 0;
		int aNum = s.getActivity().getJSONObject(member.getId()).getInt("msgs") + s.getActivity().getJSONObject(member.getId()).getInt("voice")/s.getVoiceRatio();		
		JSONArray tiers = s.getTiers();
		for(int i = 0; i < tiers.length() ; i++)
		{
			if (aNum >= tiers.getJSONObject(i).getInt("value"))
			{
				sTier = i;
			}
		}
		
		//find what roles the guild has
		List<Role> groles = member.getGuild().getRoles();
		List<Long> roleIds = new ArrayList<Long>();
		for (Role r : groles)
		{
			roleIds.add(r.getIdLong());
		}
		
		//find what roles a user has
		List<Role> mRoles = member.getRoles();
		
		
		//if the user has a tier role higher then or equal to sTier, return
		for (int i = 0 ; i < tiers.length() ; i++)
		{
			if(mRoles.contains(member.getGuild().getRoleById(tiers.getJSONObject(i).getLong("id"))) && i >= sTier)
			{
				return;
			}
			
		}

	

		//loop through each tier
		for (int i = 0 ; i < tiers.length() ; i++)
		{
			//if user has a role in this tier
			if (roleIds.contains(tiers.getJSONObject(i).getLong("id")))
			{
				if(i == sTier)
				{
					member.getGuild().addRoleToMember(member, member.getGuild().getRoleById(tiers.getJSONObject(i).getLong("id"))).queue();
				}
				else
				{
					member.getGuild().removeRoleFromMember(member, member.getGuild().getRoleById(tiers.getJSONObject(i).getLong("id"))).queue();
				}
			}
		}
		
		
	}
	
	public void UpdateAllActRoles(Bot bot) 
	{
		SettingsManager settings = new SettingsManager();
		HashMap<Long,Settings> sMap = settings.getAllSettings();
		for (long gid: sMap.keySet()) {
			
			if (settings.getSettings(gid).getTracking()) {
			JSONArray tiers = settings.getSettings(gid).getTiers();
			List<Role> roles = bot.getJDA().getGuildById(gid).getRoles();
			List<Long> roleIds = new ArrayList<Long>();
			for (Role r : roles)
			{
				roleIds.add(r.getIdLong());
			}
    		for (String member : settings.getSettings(gid).getOldActivity().keySet()) {
    			//find what tier member should have
    			int sTier = 0;
    			int aNum = settings.getSettings(gid).getOldActivity().getJSONObject(member).getInt("msgs") + settings.getSettings(gid).getOldActivity().getJSONObject(member).getInt("voice")/settings.getSettings(gid).getVoiceRatio();
    			for(int i = 0; i < tiers.length() ; i++)
    			{
    				if (aNum >= tiers.getJSONObject(i).getInt("value"))
    				{
    					sTier = i;
    				}
    			}
    			//loop through each tier
    			for (int i = 0 ; i < tiers.length() ; i++)
    			{
    				//if user has a role in this tier
    				if (roleIds.contains(tiers.getJSONObject(i).getLong("id")))
    				{
    					if(i == sTier)
    					{
    						bot.getJDA().getGuildById(gid).addRoleToMember(UserSnowflake.fromId(member), bot.getJDA().getGuildById(gid).getRoleById(tiers.getJSONObject(i).getLong("id"))).queue();
    					}
    					else
    					{
    						bot.getJDA().getGuildById(gid).removeRoleFromMember(UserSnowflake.fromId(member), bot.getJDA().getGuildById(gid).getRoleById(tiers.getJSONObject(i).getLong("id"))).queue();
    					}
    				}
    			}
    			
    		}
    	}
		}
	}
}
