package wdk.data;

import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Guacamole
 */
public class Player implements Comparable<Player> {
    String proTeam;
    String lastName;
    String firstName;
    String positions;
    String positionInTeam;
    String notes;
    int birthYear;
    String birthNation;
    int estimatedValue;     // calc total money in draft
                            // determine player's rank in all 5 categories
                            // get avg rank of each player in their categories
                            // x = num hitters needed by all teams
                            // y = num pitchers needed by all teams
                            // calc median salary:  (total $ remaining)/(2 * X) for hitters
                            // EST VALUE:  (median salary) * (X * 2/player rank)
    
    int hits;
    int runsWins;
    int hrSave;
    int rbiK;
    double sbERA;      // Earned Run Average (ERA, Earned Runs*9/Innings Pitched, i.e. ER*9/IP)
    double baWHIP;     // WHIP (i.e. Walks + Hits/Innings Pitched)
    
    int pick;
    String fantasyTeam;
    String contract;
    int salary;
    ObservableList<String> positionCandidates;
    
    int rank;
    
    public Player () {
        firstName = "<First Name>";
        lastName = "<Last Name>";
        proTeam = "N/A";
        sbERA = 0;
        notes = "";
        estimatedValue =  (int)(Math.random() * 45) +1;
        fantasyTeam = "Free Agent";
        contract = "X";
        salary = 0;
        positionInTeam = "N/A";
        positionCandidates = FXCollections.observableArrayList();
        pick = -1;
        
        rank = 0;
    }

    public void incrementPick() {
        pick++;
    }
    
    public void decrementPick() {
        pick--;
    }
    
    public boolean isPitcher() {
        return this.positions.contains("P");
    }
    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getPick() {
        return pick;
    }

    public void setPick(int pick) {
        this.pick = pick;
    }

    public ObservableList<String> getPositionCandidates() {
        return positionCandidates;
    }
    
    public ObservableList<String> getElligiblePositions() {
        ObservableList<String> elligiblePositions = FXCollections.observableArrayList();
        if (positions.contains("P")) {
            elligiblePositions.add("P");
            return elligiblePositions;
        }
        if (positions.contains("C_"))
            elligiblePositions.add("C");
        if (positions.contains("1B"))
            elligiblePositions.add("1B");
        if (positions.contains("3B"))
            elligiblePositions.add("3B");
        if (positions.contains("CI"))
            elligiblePositions.add("CI");
        if (positions.contains("2B"))
            elligiblePositions.add("2B");
        if (positions.contains("SS"))
            elligiblePositions.add("SS");
        if (positions.contains("MI"))
            elligiblePositions.add("MI");
        if (positions.contains("OF"))
            elligiblePositions.add("OF");
        if (positions.contains("U"))
            elligiblePositions.add("U");
        positionCandidates = elligiblePositions;
        return elligiblePositions;
    }

    public void setPositionCandidates(ObservableList<String> positionCandidates) {
        this.positionCandidates = positionCandidates;
    }

    public String getPositionInTeam() {
        return positionInTeam;
    }

    public void setPositionInTeam(String positionInTeam) {
        this.positionInTeam = positionInTeam;
    }

    public int getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(int estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public String getFantasyTeam() {
        return fantasyTeam;
    }

    public void setFantasyTeam(String fantasyTeam) {
        this.fantasyTeam = fantasyTeam;
    }
    
    public String getProTeam() {
        return proTeam;
    }

    public void setProTeam(String proTeam) {
        this.proTeam = proTeam;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPositions() {
        return positions;
    }

    public void setPositions(String positions) {
        this.positions = positions;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getBirthNation() {
        return birthNation;
    }

    public void setBirthNation(String birthNation) {
        this.birthNation = birthNation;
    }

    ///////////// STRANGE VARIABLE MUTATORS

    public int getRunsWins() {
        return runsWins;
    }

    public void setRunsWins(int runsWins) {
        this.runsWins = runsWins;
    }

    public int getHrSave() {
        return hrSave;
    }

    public void setHrSave(int hrSave) {
        this.hrSave = hrSave;
    }

    public int getRbiK() {
        return rbiK;
    }

    public void setRbiK(int rbiK) {
        this.rbiK = rbiK;
    }

    public double getSbERA() {
        return sbERA;
    }

    public void setSbERA(double sbERA) {
        this.sbERA = sbERA;
    }

    public double getBaWHIP() {
        return baWHIP;
    }

    public void setBaWHIP(double baWHIP) {
        this.baWHIP = baWHIP;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public int compareTo(Player p) {
        if (this.getPick() > p.getPick())
            return 1;
        else
            if (this.getPick() < p.getPick())
                return -1;
        else
                return 0;
    }
    
    public class Comparators {
        public Comparator<Player> PICK = new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                if (p1.getPick() > p2.getPick())
            return 1;
        else
            if (p1.getPick() < p2.getPick())
                return -1;
        else
                return 0;
            }
        };
    }
    
}
