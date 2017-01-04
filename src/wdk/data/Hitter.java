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
public class Hitter extends Player {
    int atBats;
    int runs;
    int homeruns;
    int rbis;
    int stolenBases;
    
    public Hitter() {
        super();
    }

    public int getAtBats() {
        return atBats;
    }

    public void setAtBats(int atBats) {
        this.atBats = atBats;
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

    public int getRbis() {
        return rbis;
    }

    public void setRbis(int rbis) {
        this.rbis = rbis;
    }

    public int getStolenBases() {
        return stolenBases;
    }

    public void setStolenBases(int stolenBases) {
        this.stolenBases = stolenBases;
    }
    
    // Batting Average (BA, calculated as Hits/At Bats, i.e. H/AB)
    public void computeBattingAverage() {
        if (atBats == 0)
            this.baWHIP = 0;
        else {
            double d = (double)hits / atBats;
            d = Math.round(d*1000);
            d = d/1000;
            this.baWHIP = d;
        }
    }
    
    public void adjustPositions() {
        String p = this.getPositions();
        
        // IF FIRST BASE OR THIRD BASE THEN CORNER INFIELD
        if (p.contains("1B") || p.contains("3B"))
            p = p + "_CI";
        
        // IF SECOND BASE OR SHORTSTOP THEN MIDDLE INFIELD
        if (p.contains("2B") || p.contains("SS"))
            p = p + "_MI";
        
        // ALL HITTERS ARE UTILITY PLAYERS
        p = p + "_U";
        
        this.positions = p;
    }
}
