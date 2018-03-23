package com.blogspot.groglogs.bbmanager.sqlite;

import com.blogspot.groglogs.bbmanager.structures.Player;
import main.java.com.blogspot.groglogs.bbmanager.structures.Team;
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

    public static Player getPlayerFromName(String name){
        if(name == null || name.equals("")) throw new IllegalArgumentException("Name cannot be empty!");

        Player p = new Player(name);

        connect();

        try {
            String op = "SELECT * FROM " + bb_player_listing + " WHERE lower(name) = lower(?)";

            PreparedStatement stmt = conn.prepareStatement(op);

            stmt.setString(1, name);

            if(com.blogspot.groglogs.bbmanager.BBManager.isDebugEnabled()){
                System.out.println("STMT: " + op);
                System.out.println(name);
                System.out.println();
            }

            ResultSet rs = stmt.executeQuery();

            p.setID(rs.getInt("ID"));
            if(com.blogspot.groglogs.bbmanager.BBManager.isDebugEnabled()){
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
            if(com.blogspot.groglogs.bbmanager.BBManager.isDebugEnabled()){
                System.out.println("Got EXP: " + p.getExperience());
                System.out.println();
            }
                
            p.setNbLevelsUp(rs.getInt("nbLevelsUp"));
            p.setStar(rs.getInt("star"));
            p.setDead(rs.getInt("dead"));
            p.setRetired(rs.getInt("retired"));
            p.setAge(rs.getInt("age"));
            p.setNbMatchsSinceAgeRoll(rs.getInt("nbMatchsSinceAgeRoll"));

            rs.close();

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

    public static void updatePlayer(Player p){
        if(p == null) throw new IllegalArgumentException("Player cannot be empty!");

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
        op.append("star = ?, ");
        op.append("dead = ?, ");
        op.append("retired = ?, ");
        op.append("age = ?, ");
        op.append("nbMatchsSinceAgeRoll = ? ");
        op.append("WHERE ID = ?");

        try{
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(op.toString());

            stmt.setInt(1, p.getCharacsMovementAllowance());
            stmt.setInt(2, p.getCharacsStrength());
            stmt.setInt(3, p.getCharacsAgility());
            stmt.setInt(4, p.getCharacsArmourValue());
            stmt.setInt(5, p.getIdPlayerLevels());
            stmt.setInt(6, p.getExperience());
            stmt.setInt(7, p.getNbLevelsUp());
            stmt.setInt(8, p.getStar());
            stmt.setInt(9, p.getDead());
            stmt.setInt(10, p.getRetired());
            stmt.setInt(11, p.getAge());
            stmt.setInt(12, p.getNbMatchsSinceAgeRoll());
            stmt.setInt(13, p.getID());

            stmt.executeUpdate();

            if(com.blogspot.groglogs.bbmanager.BBManager.isDebugEnabled()){
                System.out.println("STMT: " + op.toString());
                System.out.println(p.getCharacsMovementAllowance());
                System.out.println(p.getCharacsStrength());
                System.out.println(p.getCharacsAgility());
                System.out.println(p.getCharacsArmourValue());
                System.out.println(p.getIdPlayerLevels());
                System.out.println(p.getExperience());
                System.out.println(p.getNbLevelsUp());
                System.out.println(p.getStar());
                System.out.println(p.getDead());
                System.out.println(p.getRetired());
                System.out.println(p.getAge());
                System.out.println(p.getNbMatchsSinceAgeRoll());
                System.out.println(p.getID());
                System.out.println();
            }

            conn.commit();

            stmt.close();

            System.out.println("Player '" + p.getName() + "' updated to MA: " + p.getCharacsMovementAllowance() + ", ST: " + p.getCharacsStrength() + ", AG: " + p.getCharacsAgility() + ", AV: " + p.getCharacsArmourValue());
            System.out.println("EXP: " + p.getExperience() + ", Level UP: " + p.getNbLevelsUp() + ", dead: " + p.getDead());
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

    public static Team getTeamFromName(String name){
        if(name == null || name.equals("")) throw new IllegalArgumentException("Name cannot be empty!");

        Team t = new Team(name);

        connect();

        try {
            String op = "SELECT * FROM " + bb_team_listing + " WHERE lower(name) = lower(?)";

            PreparedStatement stmt = conn.prepareStatement(op);

            stmt.setString(1, name);

            if(com.blogspot.groglogs.bbmanager.BBManager.isDebugEnabled()){
                System.out.println("STMT: " + op);
                System.out.println(name);
                System.out.println();
            }

            ResultSet rs = stmt.executeQuery();

            t.setID(rs.getInt("ID"));
            if(com.blogspot.groglogs.bbmanager.BBManager.isDebugEnabled()){
                System.out.println("Got ID: " + t.getID());
                System.out.println();
            }

            t.setCash(rs.getInt("cash"));
            if(com.blogspot.groglogs.bbmanager.BBManager.isDebugEnabled()){
                System.out.println("Got cash: " + t.getCash());
                System.out.println();
            }

            t.setPopularity(rs.getInt("popularity"));
            if(com.blogspot.groglogs.bbmanager.BBManager.isDebugEnabled()){
                System.out.println("Got popularity: " + t.getPopularity());
                System.out.println();
            }

            rs.close();

        } catch(Exception e) {
            System.err.println("getTeamFromName error: " + e.getMessage());
            System.out.println();
            e.printStackTrace();
        } finally{
            try{
                conn.close();
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

            if(com.blogspot.groglogs.bbmanager.BBManager.isDebugEnabled()){
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
