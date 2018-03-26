package com.blogspot.groglogs.bbmanager.structures;

public class Player {
    private int ID, idPlayerTypes, idTeamListing;
    private String name;
    //attributes
    private int characsMovementAllowance, characsStrength, characsAgility, characsArmourValue;
    //experience
    private int idPlayerLevels, experience, nbLevelsUp;
    //status
    private int matchSuspended, star, dead, retired, age, nbMatchsSinceAgeRoll;

    public Player(String name){
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getIdPlayerTypes() {
        return idPlayerTypes;
    }

    public void setIdPlayerTypes(int idPlayerTypes) {
        this.idPlayerTypes = idPlayerTypes;
    }

    public int getIdTeamListing() {
        return idTeamListing;
    }

    public void setIdTeamListing(int idTeamListing) {
        this.idTeamListing = idTeamListing;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCharacsMovementAllowance() {
        return characsMovementAllowance;
    }

    public void setCharacsMovementAllowance(int characsMovementAllowance) {
        this.characsMovementAllowance = characsMovementAllowance;
    }

    public int getCharacsStrength() {
        return characsStrength;
    }

    public void setCharacsStrength(int characsStrength) {
        this.characsStrength = characsStrength;
    }

    public int getCharacsAgility() {
        return characsAgility;
    }

    public void setCharacsAgility(int characsAgility) {
        this.characsAgility = characsAgility;
    }

    public int getCharacsArmourValue() {
        return characsArmourValue;
    }

    public void setCharacsArmourValue(int characsArmourValue) {
        this.characsArmourValue = characsArmourValue;
    }

    public int getIdPlayerLevels() {
        return idPlayerLevels;
    }

    public void setIdPlayerLevels(int idPlayerLevels) {
        this.idPlayerLevels = idPlayerLevels;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getNbLevelsUp() {
        return nbLevelsUp;
    }

    public void setNbLevelsUp(int nbLevelsUp) {
        this.nbLevelsUp = nbLevelsUp;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getDead() {
        return dead;
    }

    public void setDead(int dead) {
        this.dead = dead;
    }

    public int getRetired() {
        return retired;
    }

    public void setRetired(int retired) {
        this.retired = retired;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getNbMatchsSinceAgeRoll() {
        return nbMatchsSinceAgeRoll;
    }

    public void setNbMatchsSinceAgeRoll(int nbMatchsSinceAgeRoll) {
        this.nbMatchsSinceAgeRoll = nbMatchsSinceAgeRoll;
    }

    public int getMatchSuspended() {
        return matchSuspended;
    }

    public void setMatchSuspended(int matchSuspended) {
        this.matchSuspended = matchSuspended;
    }
}
