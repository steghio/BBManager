package com.blogspot.groglogs.bbmanager.BBLogic;

import com.blogspot.groglogs.bbmanager.sqlite.SQLiteUtils;
import com.blogspot.groglogs.bbmanager.structures.Player;
import com.blogspot.groglogs.bbmanager.structures.Team;

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

    //match result
    public static final int MATCH_WIN = 1;
    public static final int MATCH_TIE = 0;
    public static final int MATCH_LOSE = -1;

    //player casualty type
    public static final int CASUALTY_DEAD = 18;

    private static Random rand = new Random();

    //1 is minimum and dice is maximum
    public static int getRandom(int dice){
        return rand.nextInt(dice) + 1;
    }

    public static void updatePlayerValues(String name, String team_name, Integer characsMovementAllowance, Integer characsStrength, Integer characsAgility, Integer characsArmourValue, Integer experience, boolean dead, boolean missNextMatch){
        if(name == null || name.equals("")) throw new IllegalArgumentException("Player name cannot be empty!");
        if(team_name == null || team_name.equals("")) throw new IllegalArgumentException("Team name cannot be empty!");

        Player p = SQLiteUtils.getPlayerFromNameAndTeamName(name, team_name);

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

        //miss next match can be reverted after a match
        p.setMatchSuspended(missNextMatch ? 1 : 0);

        //death is permanent and a player can't die twice, avoid duplicating records in it's already dead
        SQLiteUtils.updatePlayer(p, team_name, dead && p.getDead() == 0);

    }

    public static void updateTeamValues(String name, int roll, int matchResult){
        if(name == null || name.equals("")) throw new IllegalArgumentException("Team name cannot be empty!");

        Team t = SQLiteUtils.getTeamFromName(name, true); //open a new connection

        int gold = (t.getPopularity() + roll) * 10000 + ((matchResult != MATCH_LOSE) ? 10000 : 0);
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
