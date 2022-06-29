package com.jagrosh.jmusicbot.roles;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.settings.SettingsManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class UpdateActivityRoles {

	private final static SettingsManager settings = new SettingsManager();
	
	public static void UpdatemActRoles(Member member)
	{
		Settings s  = settings.getSettings(member.getGuild().getIdLong());
		
		//find what tier member should have
		int sTier = 0;
		int aNum = s.getActivity().getJSONObject(member.getId()).getInt("msgs") + (s.getActivity().getJSONObject(member.getId()).getInt("voice")/s.getVoiceRatio());		
		JSONArray tiers = s.getTiers();
		for(int i = 0; i < tiers.length() ; i++)
		{
			if (tiers.getJSONObject(i).getInt("value") >= aNum)
			{
				sTier = i;
				break;
			}
		}
		
		//find what roles the guild has
		List<Role> roles = member.getGuild().getRoles();
		List<Long> roleIds = new ArrayList<Long>();
		for (Role r : roles)
		{
			roleIds.add(r.getIdLong());
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
	
	public static void UpdateAllActRoles() 
	{
		
	}
}
