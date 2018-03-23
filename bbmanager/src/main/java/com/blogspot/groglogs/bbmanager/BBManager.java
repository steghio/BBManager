package com.blogspot.groglogs.bbmanager;

import com.blogspot.groglogs.bbmanager.BBLogic.BBLogic;
import com.blogspot.groglogs.bbmanager.sqlite.SQLiteUtils;

import java.util.Scanner;

import static com.blogspot.groglogs.bbmanager.BBLogic.BBLogic.getRandom;

public class BBManager {

    private static final String options = "type 'p' to insert player data, 't' to insert team data, 'q' to quit, 'h' for help";
    private static boolean doDebug = false;

    public static boolean isDebugEnabled(){
        return doDebug;
    }

    private static void showMainMenu(Scanner in){
        System.out.println(options);
        System.out.println();
        String input = in.next();
        System.out.println();

        switch(input){
            case "H":
            case "h":
                showMainMenu(in);
                break;
            case "Q":
            case "q":
                System.exit(0);
                break;
            case "P":
            case "p":
                showPlayerOptions(in);
                break;
            case "T":
            case "t":
                showTeamOptions(in);
                break;
            default:
                System.out.println("Invalid option: '" + input + "' - type 'h' for help");
                System.out.println();
        }

    }

    private static Boolean showProceed(Scanner in){
        System.out.println("Proceed? (Y/N/Q)?");
        String choice = in.next();
        System.out.println();

        switch(choice){
            case "Y":
            case "y":
                return true;
            case "N":
            case "n":
                return false;
            case "Q":
            case "q":
                return null;
            default:
                System.out.println("Invalid option: '" + choice + "' - Y to confirm, N to abort, Q to return to main menu");
                System.out.println();
                return showProceed(in);
        }
    }

    private static void showPlayerOptions(Scanner in){
        String name = null, input;
        System.out.println("Insert player name (mandatory):");
        name = in.next();
        System.out.println();

        if(name == null || name.equals("")){
            System.out.println("Player name is mandatory!");
            System.out.println();
            showPlayerOptions(in);
        }

        System.out.println("Insert player values or 'X' to skip setting a specific value:");
        System.out.println();

        System.out.println("MA: ");
        input = in.next();
        Integer characsMovementAllowance = (!input.equalsIgnoreCase("X") ? Integer.parseInt(input) : null);
        System.out.println();

        System.out.println("ST: ");
        input = in.next();
        Integer characsStrength = (!input.equalsIgnoreCase("X") ? Integer.parseInt(input) : null);
        System.out.println();

        System.out.println("AG: ");
        input = in.next();
        Integer characsAgility = (!input.equalsIgnoreCase("X") ? Integer.parseInt(input) : null);
        System.out.println();

        System.out.println("AV: ");
        input = in.next();
        Integer characsArmourValue = (!input.equalsIgnoreCase("X") ? Integer.parseInt(input) : null);
        System.out.println();

        System.out.println("EXP: ");
        input = in.next();
        Integer experience = (!input.equalsIgnoreCase("X") ? Integer.parseInt(input) : null);
        System.out.println();

        System.out.println("DEAD: ");
        input = in.next();
        Integer dead = (!input.equalsIgnoreCase("X") ? Integer.parseInt(input) : null);
        System.out.println();

        System.out.println("Player " + name + " will be updated with following values:");
        if(characsMovementAllowance != null) System.out.println("MA: " + characsMovementAllowance);
        if(characsStrength != null) System.out.println("ST: " + characsStrength);
        if(characsAgility != null) System.out.println("AG: " + characsAgility);
        if(characsArmourValue != null) System.out.println("AV: " + characsArmourValue);
        if(experience != null) System.out.println("EXP: " + experience);
        if(dead != null) System.out.println("DEAD: " + dead);
        System.out.println();

        Boolean proceed = showProceed(in);

        if(proceed == null) showMainMenu(in);
        else if(proceed) BBLogic.updatePlayerValues(name, characsMovementAllowance, characsStrength, characsAgility, characsArmourValue, experience, dead);
        else showPlayerOptions(in);

    }

    private static Integer showMatchResultOptions(Scanner in){
        System.out.println("Insert 'W' if team won, 'X' if tied, 'L' if lost, 'Q' to quit:");
        String result = in.next();
        System.out.println();

        switch(result){
            case "W":
            case "w":
                return BBLogic.MATCH_WIN;
            case "X":
            case "x":
                return BBLogic.MATCH_TIE;
            case "L":
            case "l":
                return BBLogic.MATCH_LOSE;
            case "Q":
            case "q":
                return null;
            default:
                System.out.println("Invalid option: '" + result + "' - 'W' if team won, 'X' if tied, 'L' if lost, 'Q' to return to main menu");
                System.out.println();
                return showMatchResultOptions(in);
        }

    }

    private static void showTeamOptions(Scanner in){
        String name = null;
        System.out.println("Insert team name (mandatory): ");
        name = in.next();
        System.out.println();

        if(name == null || name.equals("")){
            System.out.println("Team name is mandatory!");
            System.out.println();
            showTeamOptions(in);
        }

        Integer matchResult = showMatchResultOptions(in);

        if(matchResult == null) showMainMenu(in);

        int roll = BBLogic.getRandom(6);

        if(matchResult != BBLogic.MATCH_LOSE){
            System.out.println("Roll result is: " + roll + ". You can reroll only once and must keep the new roll result! Type 'N' to reroll.");

            Boolean proceed = showProceed(in);

            if(proceed == null) showMainMenu(in);
            else if(!proceed){
                roll = BBLogic.getRandom(6);
                System.out.println("New roll is: " + roll + ". Updating team now.");
                System.out.println();
            }
        }

        BBLogic.updateTeamValues(name, roll, matchResult);

    }

    public static void main(String[] args){
        boolean skip_db_bak = false;

        if(args.length > 0){
            for(String arg : args){
                switch(arg){
                    case "--DEBUG":
                    case "--debug":
                        doDebug = true;
                        break;
                    case "--SKIP-DB-BAK":
                    case "--SKIP_DB_BAK":
                    case "--skip-db-bak":
                    case "--skip_db_bak":
                        skip_db_bak = true;
                        break;
                    default:
                        System.err.println("Unrecognized option: '" + arg + "'");
                        System.out.println("Usage: java -jar bbmanager.jar [--skip-db-bak] [--debug]");
                        System.exit(1);
                }
            }
        }

        //backup current DB
        if(!skip_db_bak){
            if(!SQLiteUtils.doBackup()) System.err.println("DB backup FAILED!");
            System.out.println();
        }

        Scanner in = new Scanner(System.in);
        in.useDelimiter(System.lineSeparator());

        System.out.println("BBManager v0.1 started");
        System.out.println();

        while(true){
            showMainMenu(in);
        }

    }
}
