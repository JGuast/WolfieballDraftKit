/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.data;

import java.util.Collections;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Guacamole
 */
public class Draft {
    String name;
    
    ObservableList<Team> fantasyTeams;
    ObservableList<Hitter> hitters;
    ObservableList<Pitcher> pitchers;
    ObservableList<Player> availablePlayers;
    ObservableList<Player> draftedPlayers;
    
    public Draft() {
        name = "DefaultName";
        fantasyTeams = FXCollections.observableArrayList();
        hitters = FXCollections.observableArrayList();
        pitchers = FXCollections.observableArrayList();
        availablePlayers = FXCollections.observableArrayList();
        draftedPlayers = FXCollections.observableArrayList();
    }
    
    public void calculateEstimatedValues() {
        // calc total money in draft
        int moneyInDraft = 0;
        for (int i=0;i<fantasyTeams.size();i++)
            moneyInDraft += fantasyTeams.get(i).getMoney();
        
        // determine player's rank in all 5 categories
        // this shit will be irritating
        
        // get avg rank of each player in their categories
        
        // x = num hitters needed by all teams
        // y = num pitchers needed by all teams
        int x = 0;  int y = 0;
        for (int i=0;i<fantasyTeams.size();i++){
            x += fantasyTeams.get(i).getHittersNeeded();
            y += fantasyTeams.get(i).getPitchersNeeded();
        }
        
        // calc median salary:  (total $ remaining)/(2 * X) for hitters
        int medianSalary = 0;
        
        // determine player's rank in all 5 categories
        // get avg rank of each player in their categories
        // EST VALUE:  (median salary) * (X * 2/player rank)
        Collections.sort(availablePlayers, new RWComparator());
    }
    
    public boolean isFull() {
        for (int i=0;i<fantasyTeams.size();i++) {
            if (fantasyTeams.get(i).hasVacancy() || fantasyTeams.get(i).hasTaxiVacancy())
                return false;
        }
        return true;
    }
    
    public boolean hasTaxiOpening() {
        for (int i=0;i<fantasyTeams.size();i++) {
            if (fantasyTeams.get(i).getTaxiSquad().size() < 8) 
                return true;
        }
        return false;
    }
    
    public Team getTeamWithTaxiSpot() {
        for (int i=0;i<fantasyTeams.size();i++) {
            if (fantasyTeams.get(i).getTaxiSquad().size() < 8)
                return fantasyTeams.get(i);
        }
        return new Team();
    }
    
    public void calculatePointTotals() {
        for (int i=0;i<fantasyTeams.size();i++) {
            Team t1 = fantasyTeams.get(i);
            //R, HR, RBI, SB, BA, W, SV, K, ERA, WHIP
            int r = 1; int hr = 1; int rbi = 1; int sb = 1; int ba = 1; 
            int w = 1; int sv = 1; int k = 1; int era = 1; int whip = 1;
            for (int j=0;j<fantasyTeams.size();j++) {
                Team t2 = fantasyTeams.get(j);
                if (fantasyTeams.get(i).equals(fantasyTeams.get(j)) == false) {
                    //compare all stats
                    if (t1.getRuns() >= t2.getRuns())
                        r++;
                    if (t1.getHomeruns() >= t2.getHomeruns())
                        hr++;
                    if (t1.getRbi() >= t2.getRbi())
                        rbi++;
                    if (t1.getStolenBases() >= t2.getStolenBases())
                        sb++;
                    if (t1.getBattingAvg() >= t2.getBattingAvg())
                        ba++;
                    if (t1.getWins() >= t2.getWins())
                        w++;
                    if (t1.getSaves() >= t2.getSaves())
                        sv++;
                    if (t1.getStrikeouts() >= t2.getStrikeouts())
                        k++;
                    if (t1.getEra() <= t2.getEra()) 
                        era++;
                    if (t1.getWhip() <= t2.getWhip())
                        whip++;
                }
            }
            int sum = r+hr+rbi+sb+ba+w+sv+k+era+whip;
            fantasyTeams.get(i).setPoints(sum);
        }
    }
    
    public boolean hasTeamWithVacancy() {
        for (int i=0;i<fantasyTeams.size();i++) {
            if (fantasyTeams.get(i).hasVacancy())
                return true;
        }
        return false;
    }
    
    public Team getTeamWithVacancy() {
        Team t = new Team();
        for (int i=0;i<fantasyTeams.size();i++) {
            if (fantasyTeams.get(i).hasVacancy())
                return fantasyTeams.get(i);
        }
        return t;
    }
    
    public ObservableList<Player> getAvailablePlayers() {
        return availablePlayers;
    }

    public void setAvailablePlayers(ObservableList<Player> availablePlayers) {
        this.availablePlayers = availablePlayers;
    }
    
    public Draft(ObservableList<Team> t, ObservableList<Hitter> h, ObservableList<Pitcher> p) {
        fantasyTeams = t;
        hitters = h;
        pitchers = p;
    }

    public ObservableList<Team> getFantasyTeams() {
        return fantasyTeams;
    }
    
    public Team getTeamByName(String teamName) {
        try {
        ObservableList<String> names = this.getTeamsAsStrings();
        int i = names.indexOf(teamName);
        return fantasyTeams.get(i);
        } catch (Exception e) {
            return new Team();
        }
    }
    
    public ObservableList<String> getTeamsAsStrings() {
        ObservableList<String> names = FXCollections.observableArrayList();
        for (int i=0;i<fantasyTeams.size();i++)
            names.add(fantasyTeams.get(i).getName());
        return names;
    }

    public void setFantasyTeams(ObservableList<Team> fantasyTeams) {
        this.fantasyTeams = fantasyTeams;
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
    
    public void clearTeams() {
        hitters.clear();
    }
    
    public void clearPitchers() {
        pitchers.clear();
    }
    
    public void clearHitters() {
        hitters.clear();
    }

    public void removePlayer(Player playerToRemove) {
        if (availablePlayers.contains(playerToRemove))
            availablePlayers.remove(playerToRemove);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
     public void removeTeam(Team t) {
         fantasyTeams.remove(t);
     }
     
    // COMPARATORS FOR HELPING TO ATTAIN ESTIMATED VALUES
    class RWComparator implements Comparator<Player> {

    @Override
    public int compare(Player p1, Player p2) {
        if (p1.getRunsWins() < p2.getRunsWins())
            return 1;
        else if (p1.getRunsWins() > p2.getRunsWins())
            return -1;
        else return 0;
    }

    }
    
    class HRSComparator implements Comparator<Player> {

    @Override
    public int compare(Player p1, Player p2) {
        if (p1.getHrSave() < p2.getHrSave())
            return 1;
        else if (p1.getHrSave() > p2.getHrSave())
            return -1;
        else return 0;
    }

    }
    
    class RBIKComparator implements Comparator<Player> {

    @Override
    public int compare(Player p1, Player p2) {
        if (p1.getRbiK() < p2.getRbiK())
            return 1;
        else if (p1.getRbiK() > p2.getRbiK())
            return -1;
        else return 0;
    }
    
    }
    
    class SBERAComparator implements Comparator<Player> {

    @Override
    public int compare(Player p1, Player p2) {
        if (p1.getSbERA() < p2.getSbERA())
            return 1;
        else if (p1.getSbERA() > p2.getSbERA())
            return -1;
        else return 0;
    }
    
    }
    
    class BAWHIPComparator implements Comparator<Player> {

    @Override
    public int compare(Player p1, Player p2) {
        if (p1.getBaWHIP() < p2.getBaWHIP())
            return 1;
        else if (p1.getBaWHIP() > p2.getBaWHIP())
            return -1;
        else return 0;
    }
    
    }
}
