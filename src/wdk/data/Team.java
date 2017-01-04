/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Guacamole
 */
public class Team implements Comparable<Team>{
    final int pitcher = 9;
    final int catchers = 2;
    final int firstBasemen = 1;
    final int thirdBasemen = 1;
    final int cornerInfield = 1;
    final int secondBasemen = 1;
    final int shortstop = 1;
    final int middleInfield = 1;
    final int outfield = 5;
    final int utility = 1;
    
    String owner;
    String name;
    int money;
    ObservableList<Hitter> hitters;
    ObservableList<Pitcher> pitchers;
    ObservableList<Player> roster;
    ObservableList<Player> taxiSquad;
    
    // DATA FOR STANDINGS CALCULATIONS
    // name
    int playersNeeded;
    // money
    int moneyPerPlayer;
    int runs;
    int homeruns;
    int rbi;
    int stolenBases;
    double battingAvg;
    int wins;
    int saves;
    int strikeouts;
    double era;
    double whip;
    int points;
    
    public Team() {
        owner = "";
        hitters = FXCollections.observableArrayList();
        pitchers = FXCollections.observableArrayList();
        roster = FXCollections.observableArrayList();
        taxiSquad = FXCollections.observableArrayList();
        
        // VARIABLES FOR STANSINGS SCREEN
        playersNeeded = 23;
        money = 260;
        moneyPerPlayer = money / playersNeeded;
        runs = 0;
        homeruns = 0;
        rbi = 0;
        stolenBases = 0;
        battingAvg = 0;
        wins = 0;
        saves = 0;
        strikeouts = 0;
        era = 0;
        whip = 0;
        points = 0;
        
    }
    
    public void calculateAggregateStats() {
        this.clearStats();
        playersNeeded = 23 - (roster.size());
        
        for (int i=0; i<roster.size(); i++) {
            money = money - roster.get(i).getSalary();
            if (roster.get(i).isPitcher()) {
                wins += roster.get(i).getRunsWins();
                strikeouts += roster.get(i).getRbiK();
                saves += roster.get(i).getHrSave();
                era = this.calcAvg(1);          //calcERA
                whip = this.calcAvg(2);         //calcWHIP
            } else {
                runs += roster.get(i).getRunsWins();
                homeruns += roster.get(i).getHrSave();
                rbi += roster.get(i).getRbiK();
                stolenBases += roster.get(i).getSbERA();
                battingAvg = this.calcAvg(3);   //calcBattingAverage
            }
        }
        if (playersNeeded != 0)
            moneyPerPlayer = money/playersNeeded;
    }
    
    public void clearStats() {
        playersNeeded = 0;
        money = 260;
        wins = 0;
        strikeouts = 0;
        saves = 0;
        whip = 0;
        era = 0;
        runs = 0;
        homeruns = 0;
        rbi = 0;
        stolenBases = 0;
        battingAvg = 0;
    }
    
    private double calcAvg(int n) {
        //n=1 for era
        if (n == 1) {
            double cum = 0;
            for(int i=0;i<roster.size();i++)
                cum += roster.get(i).getSbERA();
            if (cum == 0)
                return 0;
            else {
                double temp = cum/(double)roster.size();
                temp = (double) Math.round(temp * 100) / 100;
                return temp;
            }
        }
        //n=2 for whip
        if (n==2) {
            double cum = 0;
            for (int i=0;i<roster.size();i++) {
                if (roster.get(i).isPitcher())
                    cum += roster.get(i).getBaWHIP();
            }
            if (cum == 0)
                return 0;
            else {
                double temp = cum/(double)roster.size();
                temp = (double) Math.round(temp * 100) / 100;
                return temp;
            }
        }
        //n=3 for batting average
        if (n==3) {
            double cum = 0;
            for (int i=0;i<roster.size();i++) {
                if (roster.get(i).isPitcher() == false)
                    cum += roster.get(i).getBaWHIP();
            }
            if (cum ==0)
                return 0;
            else {
                double temp = cum/(double)roster.size();
                temp = (double) Math.round(temp * 1000) / 1000;
                return temp;
            }
        }
        return -1;
    }

    public boolean hasVacancy() {
        if (roster.size() < 23)
            return true;
        else 
            return false;
    }
    
    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
    
    public int getHittersNeeded() {
        int count = 0;
        for (int i=0;i<roster.size();i++) {
            if (roster.get(i).getPositions().contains("P") == false) 
                count++;
        }
        return count;
    }
    
    public int getPitchersNeeded() {
        int count = 0;
        for (int i=0;i<roster.size();i++) {
            if (roster.get(i).getPositions().contains("P")) 
                count++;
        }
        return count;
    }

    public ObservableList<Player> getRoster() {
        return roster;
    }

    public void setRoster(ObservableList<Player> roster) {
        this.roster = roster;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public ObservableList<Hitter> getHitters() {
        return hitters;
    }

    public void setHitters(ObservableList<Hitter> hitters) {
        this.hitters = hitters;
    }

    public ObservableList<Pitcher> getPitchers() {
        return pitchers;
    }

    public void setPitchers(ObservableList<Pitcher> pitchers) {
        this.pitchers = pitchers;
    }

    public ObservableList<Player> getTaxiSquad() {
        return taxiSquad;
    }

    public void setTaxiSquad(ObservableList<Player> taxiSquad) {
        this.taxiSquad = taxiSquad;
    }
    
    public boolean hasTaxiVacancy() {
        return taxiSquad.size() < 8;
    }
    
    public boolean hasMetQuota(String pos) {
        int counter = 0;
        for (int i=0;i<roster.size();i++) {
            if (roster.get(i).positionInTeam.equals(pos))
                counter++;
        }
        
        switch(pos) {
            case "C":  return counter == catchers;
            case "1B": return counter == firstBasemen;
            case "3B": return counter == thirdBasemen;
            case "CI": return counter == cornerInfield;
            case "2B": return counter == secondBasemen;
            case "SS": return counter == shortstop;
            case "MI": return counter == middleInfield;
            case "OF": return counter == outfield;
            case "U": return counter == utility;
            case "P": return counter == pitcher;
        }
        return true;
    }

    public int getPlayersNeeded() {
        return playersNeeded;
    }

    public void setPlayersNeeded(int playersNeeded) {
        this.playersNeeded = playersNeeded;
    }

    public int getMoneyPerPlayer() {
        return moneyPerPlayer;
    }

    public void setMoneyPerPlayer(int moneyPerPlayer) {
        this.moneyPerPlayer = moneyPerPlayer;
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public int getHomeruns() {
        return homeruns;
    }

    public void setHomeruns(int homeruns) {
        this.homeruns = homeruns;
    }

    public int getRbi() {
        return rbi;
    }

    public void setRbi(int rbi) {
        this.rbi = rbi;
    }

    public int getStolenBases() {
        return stolenBases;
    }

    public void setStolenBases(int stolenBases) {
        this.stolenBases = stolenBases;
    }

    public double getBattingAvg() {
        return battingAvg;
    }

    public void setBattingAvg(double battingAvg) {
        this.battingAvg = battingAvg;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getSaves() {
        return saves;
    }

    public void setSaves(int saves) {
        this.saves = saves;
    }

    public int getStrikeouts() {
        return strikeouts;
    }

    public void setStrikeouts(int strikeouts) {
        this.strikeouts = strikeouts;
    }

    public double getEra() {
        return era;
    }

    public void setEra(double era) {
        this.era = era;
    }

    public double getWhip() {
        return whip;
    }

    public void setWhip(double whip) {
        this.whip = whip;
    }
    
    
    public ObservableList<String> getVacantPositions(ObservableList<String> playerPositions) {
        ObservableList<String> vacantPositions = FXCollections.observableArrayList();
        int catcherCount = 0;
        int outfieldCount = 0;
        int pitcherCount = 0;
        int fbCount = 0;
        int tbCount = 0;
        int ciCount = 0;
        int sbCount = 0;
        int ssCount = 0;
        int miCount = 0;
        int uCount = 0;
        for (int i=0; i<roster.size(); i++) {
            if (roster.get(i).getPositionInTeam() != null) {
            switch (roster.get(i).getPositionInTeam()) {
                case "C":   catcherCount++;  break;
                case "OF":  outfieldCount++; break;
                case "P":   pitcherCount++;  break;
                case "1B":  fbCount++;       break;
                case "3B":  tbCount++;       break;
                case "CI":  ciCount++;       break;
                case "2B":  sbCount++;       break;
                case "SS":  ssCount++;       break;
                case "MI":  miCount++;       break;
                case "U":   uCount++;        break;
            }}
        }
        
        for (int i=0; i<playerPositions.size(); i++) {
            if (playerPositions.get(i).equals("C") && catcherCount < catchers)
               vacantPositions.add(playerPositions.get(i));
            if (playerPositions.get(i).equals("1B") && fbCount < firstBasemen)
               vacantPositions.add(playerPositions.get(i));
            if (playerPositions.get(i).equals("3B") && tbCount < thirdBasemen)
               vacantPositions.add(playerPositions.get(i));
            if (playerPositions.get(i).equals("CI") && ciCount < cornerInfield)
               vacantPositions.add(playerPositions.get(i));
            if (playerPositions.get(i).equals("2B") && sbCount < secondBasemen)
               vacantPositions.add(playerPositions.get(i));
            if (playerPositions.get(i).equals("SS") && ssCount < shortstop)
               vacantPositions.add(playerPositions.get(i));
            if (playerPositions.get(i).equals("MI") && miCount < middleInfield)
               vacantPositions.add(playerPositions.get(i));
            if (playerPositions.get(i).equals("OF") && outfieldCount < outfield)
               vacantPositions.add(playerPositions.get(i));
            if (playerPositions.get(i).equals("U") && uCount < utility)
               vacantPositions.add(playerPositions.get(i));
            if (playerPositions.get(i).equals("P") && pitcherCount < pitcher)
                vacantPositions.add(playerPositions.get(i));
        }
        return vacantPositions;
    }

    @Override
    public int compareTo(Team t) {
        if (this.getPoints() > t.getPoints())
            return -1;
        else
            if (this.getPoints() < t.getPoints())
                return 1;
        else
                return 0;
    }
    
}
