package com.blogspot.groglogs.bbmanager.BBLogic;

import com.blogspot.groglogs.bbmanager.sqlite.SQLiteUtils;
import com.blogspot.groglogs.bbmanager.structures.Player;
import main.java.com.blogspot.groglogs.bbmanager.structures.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BBLogic {

    //needs 1 more than the value to level up
    private static final Map<Integer, Integer> levelsExp ;
    static
    {
        levelsExp = new HashMap<>();
        levelsExp.put(1, 5);
        levelsExp.put(2, 15);
        levelsExp.put(3, 30);
        levelsExp.put(4, 50);
        levelsExp.put(5, 75);
        levelsExp.put(6, 175);
        levelsExp.put(7, Integer.MAX_VALUE);
    }

    public static final int MATCH_WIN = 1;
    public static final int MATCH_TIE = 0;
    public static final int MATCH_LOSE = -1;

    private static Random rand = new Random();

    //1 is minimum and dice is maximum
    public static int getRandom(int dice){
        return rand.nextInt(dice) + 1;
    }

    public static void updatePlayerValues(String name, Integer characsMovementAllowance, Integer characsStrength, Integer characsAgility, Integer characsArmourValue, Integer experience, Integer dead){
        if(name == null || name.equals("")) throw new IllegalArgumentException("Name cannot be empty!");

        if(characsMovementAllowance == null &&
                characsStrength == null &&
                characsAgility == null &&
                characsArmourValue == null &&
                experience == null &&
                dead == null) throw new IllegalArgumentException("At least one attribute must be set for the player!");

        Player p = SQLiteUtils.getPlayerFromName(name);

        //minimum value for a characteristic is 1
        if(characsMovementAllowance != null && p.getCharacsMovementAllowance() > 1) p.setCharacsMovementAllowance(p.getCharacsMovementAllowance() + characsMovementAllowance);
        if(characsStrength != null && p.getCharacsStrength() > 1) p.setCharacsStrength(p.getCharacsStrength() + characsStrength);
        if(characsAgility != null && p.getCharacsAgility() > 1) p.setCharacsAgility(p.getCharacsAgility() + characsAgility);
        if(characsArmourValue != null && p.getCharacsArmourValue() > 1) p.setCharacsArmourValue(p.getCharacsArmourValue() + characsArmourValue);

        if(experience != null){
            int totLevels = 0, currLevel = p.getIdPlayerLevels();

            p.setExperience(p.getExperience() + experience);
            //level up until possible
            while(p.getExperience() > levelsExp.get(currLevel)){
                totLevels++;
                currLevel++;
            }
            p.setNbLevelsUp(totLevels);
        }

        if(dead != null) p.setDead(dead);

        SQLiteUtils.updatePlayer(p);

    }

    public static void updateTeamValues(String name, int roll, int matchResult){
        if(name == null || name.equals("")) throw new IllegalArgumentException("Name cannot be empty!");

        Team t = SQLiteUtils.getTeamFromName(name);

        int gold = (t.getPopularity() + roll) * 10000 + ((matchResult != -1) ? 10000 : 0);
        t.setCash(t.getCash() + gold);

        int fan_factor_roll = getRandom(6) + getRandom(6);

        //3D6 on win, 2D6 on tie or lose
        if(matchResult == MATCH_WIN){
            fan_factor_roll += getRandom(6);
        }

        //If win or tie and roll is more than current fame, increase it
        //If lose or tie and roll is less than current fame, decrease it unless it's already 0
        if(fan_factor_roll > t.getPopularity()){
            if(matchResult != MATCH_LOSE) t.setPopularity(t.getPopularity() + 1);
        } else{
            if(matchResult != MATCH_WIN && t.getPopularity() > 0) t.setPopularity(t.getPopularity() - 1);
        }

        SQLiteUtils.updateTeam(t);

    }
}
