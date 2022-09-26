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
package com.jagrosh.jmusicbot.commands.Dice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.DiceCommand;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */

//class to return from roll()
class RollRes
{
  public int diceNum = 0;
  public int diceValue = 0;
  public ArrayList<Integer> result = new ArrayList<Integer>();
  public int bonus = 0;
  
  public RollRes (RollRes copied) {
	  this.diceNum = copied.diceNum;
	  this.diceValue = copied.diceValue;
	  this.result = copied.result;
	  this.bonus = copied.bonus;
  }
  
  public RollRes () { }
};

public class RollCommand extends DiceCommand
{
    
    
    public RollCommand(Bot bot)
    {
        
        this.name = "roll";
        this.help = "rolls a specified amount of dice with a specified value";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.arguments = "<#d#|#d#+#|NONE>";
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
    	
    	
    	//if there is no arguments, set it to 1d20
    	String toRoll = event.getArgs();
    	if (toRoll.isEmpty()) {
    		toRoll = "1d20";
    	}
    	
    	toRoll = toRoll.toLowerCase();
    	
    	//make toRoll have something to signal the end of it
        toRoll += ']';
        
      //set last iterator to the start of toRoll
        int lit = 0;
        
        RollRes tempRolls = new RollRes();
        ArrayList<RollRes> rolls =  new ArrayList<RollRes>();
        
      //go through toRoll and find what actually needs rolled
        for (int it = 1; it < toRoll.length(); it++)
        {
            switch (toRoll.charAt(it))
            {
            case 'd':
            	try
            	{
                tempRolls.diceNum = Integer.parseInt(toRoll.substring(lit, it));
            	}
            	catch(NumberFormatException e)
            	{
            		event.replyError("You dont need to roll that many of them");
            		return;
            	}
                lit = it;
                break;
            case '+':
            case'-':
                switch (toRoll.charAt(lit))
                {
                case 'd':
                	try
                	{
                		tempRolls.diceValue = Integer.parseInt(toRoll.substring(lit + 1, it));
                	}
                	catch(NumberFormatException e)
                	{
                		event.replyError("Your dice aren't that big");
                		return;
                	}
                    break;
                case '+':
                case'-':
                	try
                	{
                		tempRolls.bonus = Integer.parseInt(toRoll.substring(lit, it));
                	}
                	catch(NumberFormatException e)
                	{
                		event.replyError("Your bonus isn't that long");
                		return;
                	}
                    break;
                }
                rolls.add(tempRolls);
                
                //clear tempRolls
                tempRolls = new RollRes();
                
                lit = it;
                break;

            case ']':
                switch (toRoll.charAt(lit))
                {
                case '+':
                case'-':
                	try
                	{
                		tempRolls.bonus = Integer.parseInt(toRoll.substring(lit, it));
                	}
                	catch(NumberFormatException e)
                	{
                		event.replyError("Your bonus isn't that long");
                		return;
                	}
                    break;
                case 'd':
                	try
                	{
                		tempRolls.diceValue = Integer.parseInt(toRoll.substring(lit + 1, it));
                	}
                	catch(NumberFormatException e)
                	{
                		event.replyError("Your bonus isn't that long");
                		return;
                	}
                    break;
                }
                rolls.add(tempRolls);
                break;
            }
        }
        //if shit is broke, clear rolls
        if (rolls.get(0).diceValue == 0) {
            rolls.clear();
        }
        else {
        //loop through each type of dice being rolled
        for (RollRes temp : rolls) {

            if (temp.diceNum != 0 && temp.diceValue != 0)
            {

                Random rand = new Random();
                
                //if diceNum is less the 500, then record all dice rolls
                if (temp.diceNum <= 500) {
                    //loop through and roll all the dice
                    for (int i = 0; i < temp.diceNum; i++) {
                        temp.result.add(rand.nextInt(temp.diceValue) + 1);
                    }
                }
                //otherwise, add every roll to a running total
                else {

                    //if diceNum is over 1M, then take the average roll for every roll over 1M
                    if (temp.diceNum > 1000000) {


                        //roll 1M

                        //make an intital value in element 0
                        temp.result.add(rand.nextInt(temp.diceValue) + 1);

                        //loop through and roll all the dice
                        for (int i = 0; i < 1000000; i++) {
                            temp.result.set(0, temp.result.get(0) + (rand.nextInt(temp.diceValue) + 1));
                        }

                        //add the fake rolls
                        temp.result.set(0, temp.result.get(0) + (temp.diceValue + 1) * (temp.diceNum - 1000000) / 2);

                    }
                    //otherwise randomize every roll
                    else {

                        //make an intital value in element 0
                        temp.result.add(rand.nextInt(temp.diceValue) + 1);

                        //loop through and roll all the dice
                        for (int i = 0; i < temp.diceNum; i++) {
                        	temp.result.set(0, temp.result.get(0) + (rand.nextInt(temp.diceValue) + 1));
                        }
                    }
                }

            }

        }
        }
        //reply with result
        //variables
        int rsum = 0;
        String allNums = new String();
        String allRolls = new String();

        //if the vector empty, then return an error message
        if (rolls.isEmpty())
        {
            event.reply("Invalid roll command. Please try again");
        }
        //if the vector isn't empty, display the results of the roll
        else {

            //loop through the vector of dice types/bonues
            for (RollRes it : rolls) {

                //sort the results
                it.result.sort(Comparator.reverseOrder());

                //find the sum of all the numbers rolled
                for (int it2 : it.result)
                    rsum += it2;

                //add the bonus
                rsum += it.bonus;

                //set allNums with every number from the vector
                if (!it.result.isEmpty()) {
                    allNums += "[" + it.result.get(0);
                    for (int i = 1; i < it.result.size(); i++) {
                        allNums += ", " + it.result.get(i);
                    }
                    allNums += "]";
                }

                //if there is diceNum and diceValue, then add them to allRolls
                if (it.diceNum != 0 && it.diceValue != 0)
                {
                    allRolls += "+" + it.diceNum + 'd' + it.diceValue;
                }

                //add bonus to allNums and allRoles
                if (it.bonus != 0) {
                    //if positive, add a + before
                    if (it.bonus > 0) {
                        allNums += "[+" + it.bonus + "]";
                        allRolls += "+" + it.bonus;
                    }
                    //otherwise dont add the +
                    else {
                        allNums += "[" + it.bonus + "]";
                        allRolls += it.bonus;

                    }

                }

            }

            //remove the + from the front of allRolls
            allRolls = allRolls.substring(1);




            //send the reply to discord if the reply would be less then 2k characters, show every roll
            if (allNums.length() < 1900) {

                //if there is more then 1 die being rolled, display every number rolled
                if (allNums.indexOf("][") == -1 && allNums.indexOf(',') == -1) {

                    event.reply("Rolling " + allRolls + "\nResult: `" + allNums + "`");
                }
                else {
                    event.reply("Rolling " + allRolls + "\nResult: `" + allNums + "` \nSum: `" + rsum + "`");
                }
            }
            else {
                event.reply("Rolling " + allRolls + "\nSum: `" + rsum + "`");
            }

            //clear everything
            rolls.clear();
            allNums = "";
            allRolls = "";
            rsum = 0;
        }
        
        
        
    }
}
