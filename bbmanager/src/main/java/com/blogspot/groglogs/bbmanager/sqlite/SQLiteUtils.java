package com.blogspot.groglogs.bbmanager.sqlite;

import com.blogspot.groglogs.bbmanager.BBLogic.BBLogic;
import com.blogspot.groglogs.bbmanager.BBManager;
import com.blogspot.groglogs.bbmanager.structures.Player;
import com.blogspot.groglogs.bbmanager.structures.Team;
import org.sqlite.JDBC;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class SQLiteUtils {

    private static final String route = "Management.db";
    private static final String backup = "Management.db.bak";

    //tables
    private static final String bb_player_listing = "bb_player_listing";
    private static final String bb_team_listing = "bb_team_listing";
    private static final String bb_player_casualties = "bb_player_casualties";

    private static Connection conn;

    public static boolean doBackup(){
        try {
            Files.copy(Paths.get(route), Paths.get(backup), REPLACE_EXISTING);
            return true;
        } catch(Exception e){
            System.err.println("Error during backup of DB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void connect(){
        try{
            DriverManager.registerDriver(new JDBC());
            conn = DriverManager.getConnection("jdbc:sqlite:" + route);
        } catch(SQLException e){
            System.err.println("Connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Player getPlayerFromNameAndTeamName(String name, String team_name){
        if(name == null || name.equals("")) throw new IllegalArgumentException("Player name cannot be empty!");
        if(team_name == null || team_name.equals("")) throw new IllegalArgumentException("Team name cannot be empty!");

        Player p = new Player(name);

        connect();

        Team t = getTeamFromName(team_name, false); //use current connection

        try {

            String op = "SELECT * FROM " + bb_player_listing + " WHERE lower(name) = lower(?) AND idTeamListing = ?";

            PreparedStatement stmt = conn.prepareStatement(op);

            stmt.setString(1, name);
            stmt.setInt(2, t.getID());

            if(BBManager.isDebugEnabled()){
                System.out.println("STMT: " + op);
                System.out.println(name);
                System.out.println(team_name + " " + t.getID());
                System.out.println();
            }

            ResultSet rs = stmt.executeQuery();

            p.setID(rs.getInt("ID"));
            if(BBManager.isDebugEnabled()){
                System.out.println("Got ID: " + p.getID());
                System.out.println();
            }

            p.setIdPlayerTypes(rs.getInt("idPlayerTypes"));
            p.setIdTeamListing(rs.getInt("idTeamListing"));
            p.setCharacsMovementAllowance(rs.getInt("characsMovementAllowance"));
            p.setCharacsStrength(rs.getInt("characsStrength"));
            p.setCharacsAgility(rs.getInt("characsAgility"));
            p.setCharacsArmourValue(rs.getInt("characsArmourValue"));
            p.setIdPlayerLevels(rs.getInt("idPlayerLevels"));
            p.setExperience(rs.getInt("experience"));
            if(BBManager.isDebugEnabled()){
                System.out.println("Got EXP: " + p.getExperience());
                System.out.println();
            }

            p.setMatchSuspended(rs.getInt("matchSuspended"));
            if(BBManager.isDebugEnabled()){
                System.out.println("Got matchSuspended: " + p.getMatchSuspended());
                System.out.println();
            }

            p.setNbLevelsUp(rs.getInt("nbLevelsUp"));
            p.setStar(rs.getInt("star"));
            p.setDead(rs.getInt("dead"));
            if(BBManager.isDebugEnabled()){
                System.out.println("Got dead: " + p.getDead());
                System.out.println();
            }

            p.setRetired(rs.getInt("retired"));
            p.setAge(rs.getInt("age"));
            p.setNbMatchsSinceAgeRoll(rs.getInt("nbMatchsSinceAgeRoll"));

            rs.close();
            stmt.close();

        } catch(Exception e) {
            System.err.println("getPlayerFromName error: " + e.getMessage());
            e.printStackTrace();
        } finally{
            try{
                conn.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return p;

    }

    //should also track the casualty type, but the only relevant one is death.
    //Other injuries that alter player characteristics are user tracked and inputted manually
    private static void updatePlayerCasualties(Player p){
        if(p == null) throw new IllegalArgumentException("Player cannot be empty!");

        //connection has already been opened in the parent call

        //use null for ID since it is autoincrement
        String op = "INSERT INTO " + bb_player_casualties + " (ID, idPlayerListing, idPlayerCasualtyTypes) VALUES(null, ?, ?)";

        try{
            //autocommit has already been changed in the parent call
            PreparedStatement stmt = conn.prepareStatement(op);

            stmt.setInt(1, p.getID());
            stmt.setInt(2, BBLogic.CASUALTY_DEAD);

            stmt.execute();

            if(BBManager.isDebugEnabled()){
                System.out.println("STMT: " + op);
                System.out.println(p.getID());
                System.out.println(BBLogic.CASUALTY_DEAD);
                System.out.println();
            }

            //commit will be done in parent call

            stmt.close();

            System.out.println("Player '" + p.getName() + "' updated to casualty: " + BBLogic.CASUALTY_DEAD);
            System.out.println();

        } catch(Exception e){
            System.err.println("updatePlayerCasualties error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updatePlayer(Player p, String team_name, boolean is_dead){
        if(p == null) throw new IllegalArgumentException("Player cannot be empty!");
        if(team_name == null) throw new IllegalArgumentException("Team name cannot be empty!");

        connect();

        StringBuilder op = new StringBuilder();

        op.append("UPDATE " + bb_player_listing + " SET ");
        op.append("characsMovementAllowance = ?, ");
        op.append("characsStrength = ?, ");
        op.append("characsAgility = ?, ");
        op.append("characsArmourValue = ?, ");
        op.append("idPlayerLevels = ?, ");
        op.append("experience = ?, ");
        op.append("nbLevelsUp = ?, ");
        op.append("matchSuspended = ?, ");
        op.append("star = ?, ");
        op.append("dead = ?, ");
        op.append("retired = ?, ");
        op.append("age = ?, ");
        op.append("nbMatchsSinceAgeRoll = ? ");
        op.append("WHERE ID = ?");

        try{
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(op.toString());

            //track it is dead, although the actual relevant value is stored in a separate table
            if(is_dead && p.getDead() == 0) p.setDead(1);

            stmt.setInt(1, p.getCharacsMovementAllowance());
            stmt.setInt(2, p.getCharacsStrength());
            stmt.setInt(3, p.getCharacsAgility());
            stmt.setInt(4, p.getCharacsArmourValue());
            stmt.setInt(5, p.getIdPlayerLevels());
            stmt.setInt(6, p.getExperience());
            stmt.setInt(7, p.getNbLevelsUp());
            stmt.setInt(8, p.getMatchSuspended());
            stmt.setInt(9, p.getStar());
            stmt.setInt(10, p.getDead());
            stmt.setInt(11, p.getRetired());
            stmt.setInt(12, p.getAge());
            stmt.setInt(13, p.getNbMatchsSinceAgeRoll());
            stmt.setInt(14, p.getID());

            stmt.executeUpdate();

            if(BBManager.isDebugEnabled()){
                System.out.println("STMT: " + op.toString());
                System.out.println(p.getCharacsMovementAllowance());
                System.out.println(p.getCharacsStrength());
                System.out.println(p.getCharacsAgility());
                System.out.println(p.getCharacsArmourValue());
                System.out.println(p.getIdPlayerLevels());
                System.out.println(p.getExperience());
                System.out.println(p.getNbLevelsUp());
                System.out.println(p.getMatchSuspended());
                System.out.println(p.getStar());
                System.out.println(p.getDead());
                System.out.println(p.getRetired());
                System.out.println(p.getAge());
                System.out.println(p.getNbMatchsSinceAgeRoll());
                System.out.println(p.getID());
                System.out.println();
            }

            //update player status to dead. This is tracked in a separate table
            if(is_dead) updatePlayerCasualties(p);

            conn.commit();

            stmt.close();

            System.out.println("Player '" + p.getName() + "' from team '" + team_name + "' updated to MA: " + p.getCharacsMovementAllowance() + ", ST: " + p.getCharacsStrength() + ", AG: " + p.getCharacsAgility() + ", AV: " + p.getCharacsArmourValue());
            System.out.println("EXP: " + p.getExperience() + ", Level UP: " + p.getNbLevelsUp() + ", Dead: " + (p.getDead() == 1 ? "Yes" : "No") + ", Miss next match: " + (p.getMatchSuspended() == 1 ? "Yes" : "No"));
            System.out.println();

        } catch(Exception e){
            System.err.println("updatePlayer error: " + e.getMessage());
            e.printStackTrace();
        } finally{
            try{
                conn.setAutoCommit(true);
                conn.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    //allow reusing parent connection if not called directly with the boolean flag
    public static Team getTeamFromName(String name, boolean do_connect){
        if(name == null || name.equals("")) throw new IllegalArgumentException("Team name cannot be empty!");

        Team t = new Team(name);

        if(do_connect) connect();

        try {
            //exclude inactive, deleted and predefined teams
            String op = "SELECT * FROM " + bb_team_listing + " WHERE lower(name) = lower(?) AND active = 1 AND deleted = 0 AND predefined = 0";

            PreparedStatement stmt = conn.prepareStatement(op);

            stmt.setString(1, name);

            if(BBManager.isDebugEnabled()){
                System.out.println("STMT: " + op);
                System.out.println(name);
                System.out.println();
            }

            ResultSet rs = stmt.executeQuery();

            t.setID(rs.getInt("ID"));
            if(BBManager.isDebugEnabled()){
                System.out.println("Got ID: " + t.getID());
                System.out.println();
            }

            t.setCash(rs.getInt("cash"));
            if(BBManager.isDebugEnabled()){
                System.out.println("Got cash: " + t.getCash());
                System.out.println();
            }

            t.setPopularity(rs.getInt("popularity"));
            if(BBManager.isDebugEnabled()){
                System.out.println("Got popularity: " + t.getPopularity());
                System.out.println();
            }

            rs.close();
            stmt.close();

        } catch(Exception e) {
            System.err.println("getTeamFromName error: " + e.getMessage());
            System.out.println();
            e.printStackTrace();
        } finally{
            try{
                if(do_connect) conn.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return t;

    }

    public static void updateTeam(Team t){
        if(t == null) throw new IllegalArgumentException("Team cannot be empty!");

        connect();

        StringBuilder op = new StringBuilder();

        op.append("UPDATE " + bb_team_listing + " SET ");
        op.append("cash = ?, ");
        op.append("popularity = ? ");
        op.append("WHERE ID = ?");

        try{
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(op.toString());

            stmt.setInt(1, t.getCash());
            stmt.setInt(2, t.getPopularity());
            stmt.setInt(3, t.getID());

            stmt.executeUpdate();

            if(BBManager.isDebugEnabled()){
                System.out.println("STMT: " + op.toString());
                System.out.println(t.getCash());
                System.out.println(t.getPopularity());
                System.out.println(t.getID());
                System.out.println();
            }

            conn.commit();

            stmt.close();

            System.out.println("Team '" + t.getName() + "' new cash: " + t.getCash() + " and popularity: " + t.getPopularity());
            System.out.println();

        } catch(Exception e){
            System.err.println("updateTeam error: " + e.getMessage());
            System.out.println();
            e.printStackTrace();
        } finally{
            try{
                conn.setAutoCommit(true);
                conn.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
