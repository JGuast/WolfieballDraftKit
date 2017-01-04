/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.data;

/**
 *
 * @author Guacamole
 */
    public class Pitcher extends Player{
        double inningsPitched;
        int earnedRuns;
        int wins;
        int saves;
        int walks;
        int strikeouts;
    
    public Pitcher() {
        super();
        positions = "P";
    }

    public double getInningsPitched() {
        return inningsPitched;
    }

    public void setInningsPitched(double inningsPitched) {
        this.inningsPitched = inningsPitched;
    }

    public int getEarnedRuns() {
        return earnedRuns;
    }

    public void setEarnedRuns(int earnedRuns) {
        this.earnedRuns = earnedRuns;
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

    public int getWalks() {
        return walks;
    }

    public void setWalks(int walks) {
        this.walks = walks;
    }

    public int getStrikeouts() {
        return strikeouts;
    }

    public void setStrikeouts(int strikeouts) {
        this.strikeouts = strikeouts;
    }
    
    // Earned Run Average (ERA, Earned Runs*9/Innings Pitched, i.e. ER*9/IP)
    public void computeERA() {
        if (inningsPitched == 0 || earnedRuns == 0)
            this.sbERA = 0;
        else {
            double d = (earnedRuns * 9) / inningsPitched;
            d = Math.round(d*100);
            d = d/100;
            this.sbERA = d;
        }
    }
    
    // WHIP (i.e. Walks + Hits/Innings Pitched)
    public void computeWHIP() {
        if (walks+hits == 0 || inningsPitched == 0)
            this.baWHIP = 0;
        else {
            double d = (walks + hits) / inningsPitched;
            d = Math.round(d*100);
            d = d/100;
            this.baWHIP = d;
        }
    }
}
