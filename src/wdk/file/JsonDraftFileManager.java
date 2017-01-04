package wdk.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import static wdk.base.WDK_StartupConstants.PATH_DRAFTS;
import wdk.data.Draft;
import wdk.data.Hitter;
import wdk.data.Pitcher;
import wdk.data.Player;
import wdk.data.Team;

/**
 *
 * @author Guacamole
 */
public class JsonDraftFileManager implements DraftFileManager{
    String JSON_DRAFT_NAME = "Name";
    String JSON_TEAMS = "Teams";
    String JSON_FIRST_NAME_VAR = "firstName";
    String JSON_LAST_NAME_VAR = "lastName";
    String JSON_LAST_NAME = "LAST_NAME";
    String JSON_PLAYERS = "Players";
    String JSON_HITTERS = "Hitters";
    String JSON_PITCHERS = "Pitchers";
    String JSON_TEAM_NAME = "name";
    String JSON_TEAM_OWNER = "owner";
    String JSON_MONEY = "money";
    String JSON_ROSTER = "roster";
    String JSON_TAXI_SQUAD = "taxiSquad";
    String JSON_EXT = ".json";
    String SLASH = "/";

    
    // LOADS A JSON FILE AS A SINGLE OBJECT AND RETURNS IT
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
        InputStream is = new FileInputStream(jsonFilePath);
        JsonReader jsonReader = Json.createReader(is);
        JsonObject json = jsonReader.readObject();
        jsonReader.close();
        is.close();
        return json;
    }
    
    @Override
    public void saveDraft(Draft draftToSave) throws IOException { 
        // BUILD THE FILE PATH
        String draftName = "" + draftToSave.getName();
        String jsonFilePath = PATH_DRAFTS + SLASH + draftName + JSON_EXT;
        
        // INIT THE WRITER
        OutputStream os = new FileOutputStream(jsonFilePath);
        JsonWriter jsonWriter = Json.createWriter(os);  
        
        // ARRAY FOR FANTASY TEAMS
        JsonArray fantasyTeamsJsonArray = makeFantasyTeamsJsonArray(draftToSave.getFantasyTeams());
        
        // ARRAY FOR HITTERS
        JsonArray hittersJsonArray = makeHittersJsonArray(draftToSave.getHitters());
        
        // ARRAY FOR PITCHERS
        JsonArray pitchersJsonArray = makePitchersJsonArray(draftToSave.getPitchers());
        
        // ARRAY FOR AVAILABLE PLAYERS
        JsonArray availablePlayersJsonArray = makeAvailablePlayersJsonArray(draftToSave.getAvailablePlayers());
        
        
        // NOW BUILD THE DRAFT USING EVERYTHING WE'VE ALREADY MADE
        JsonObject draftJsonObject = Json.createObjectBuilder()
                                    .add(JSON_DRAFT_NAME, draftToSave.getName())
                                    .add(JSON_TEAMS, fantasyTeamsJsonArray)
                                    .add(JSON_HITTERS, hittersJsonArray)
                                    .add(JSON_PITCHERS, pitchersJsonArray)
                                    .add(JSON_PLAYERS, availablePlayersJsonArray)
                                    .build();
        
        // AND SAVE EVERYTHING AT ONCE
        jsonWriter.writeObject(draftJsonObject);
    }

    @Override
    public void loadDraft(Draft draftToLoad, String draftFilePath) throws IOException {
        // LOAD THE JSON FILE WITH ALL THE DATA
        JsonObject json = loadJSONFile(draftFilePath);
        
        // NOW LOAD THE COURSE
        draftToLoad.setName(json.getString(JSON_DRAFT_NAME));
        
        JsonArray jsonTeams = json.getJsonArray("Teams");
        ObservableList<Team> teams = FXCollections.observableArrayList();
        for (int i=0;i<jsonTeams.size();i++) {
            JsonObject jsonTeam = jsonTeams.getJsonObject(i);
            teams.add(loadSavedTeam(jsonTeam));
        }
        draftToLoad.setFantasyTeams(teams);
        // error loading available players in loadSavedPlayersMethod
        draftToLoad.setAvailablePlayers(loadSavedPlayers(json.getJsonArray(JSON_PLAYERS)));
        
    }

    private ObservableList<Player> loadSavedPlayers(JsonArray jsonPlayers) {
        ObservableList<Player> players = FXCollections.observableArrayList();
        for (int i=0;i<jsonPlayers.size();i++) {
            JsonObject jsonPlayer = jsonPlayers.getJsonObject(i);
            if (jsonPlayers.getJsonObject(i).getString("positions").contains("P")) {
              Pitcher p = new Pitcher();
              initPlayer(p, jsonPlayer);
              //error being thrown on this line
              //p.setInningsPitched((double)jsonPlayer.getInt("inningsPitched"));
              p.setEarnedRuns(jsonPlayer.getInt("earnedRuns"));
              p.setWins(jsonPlayer.getInt("wins"));
              p.setSaves(jsonPlayer.getInt("saves"));
              p.setHits(jsonPlayer.getInt("hits"));
              p.setWalks(jsonPlayer.getInt("walks"));
              p.setStrikeouts(jsonPlayer.getInt("strikeouts"));
              
              players.add(p);
            } 
            else {
              Hitter h = new Hitter();
              initPlayer(h, jsonPlayer);
              h.setAtBats(jsonPlayer.getInt("atBats"));
              h.setRuns(jsonPlayer.getInt("runs"));
              h.setHits(jsonPlayer.getInt("hits"));
              h.setHomeruns(jsonPlayer.getInt("homeruns"));
              h.setRbis(jsonPlayer.getInt("rbis"));
              h.setStolenBases(jsonPlayer.getInt("stolenBases"));
              
              players.add(h);
            }
        }
        return players;
    }
   
    private Team loadSavedTeam(JsonObject jsonTeam) {
        Team t = new Team();
        t.setPoints(jsonTeam.getInt("points"));
        t.setOwner(jsonTeam.getString("owner"));
        t.setName(jsonTeam.getString("name"));
        t.setMoney(jsonTeam.getInt("money"));
        t.setRoster(loadSavedPlayers(jsonTeam.getJsonArray("roster")));
        t.setTaxiSquad(loadSavedPlayers(jsonTeam.getJsonArray("taxiSquad")));
        
        return t;
    }
    
    @Override
    public void savePlayers(List<Object> subjects, String filePath) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    // LOADS AN ARRAY OF A SPECIFIC NAME FROM A JSON FILE AND
    // RETURNS IT AS AN ArrayList FULL OF THE DATA FOUND
    private ArrayList<String> loadArrayFromJSONFile(String jsonFilePath, String arrayName) throws IOException {
        JsonObject json = loadJSONFile(jsonFilePath);
        ArrayList<String> items = new ArrayList();
        JsonArray jsonArray = json.getJsonArray(arrayName);
        for (JsonValue jsV : jsonArray) {
            items.add(jsV.toString());
        }
        return items;
    }

    @Override
    public ObservableList<Hitter> loadHitters(String filePath) throws IOException {
        ArrayList<String> hittersArray = loadArrayFromJSONFile(filePath, JSON_HITTERS);
        ObservableList<Hitter> hitters = FXCollections.observableArrayList();
        for (String s : hittersArray) {
            // GET RID OF ALL THE QUOTE CHARACTERS
            s = s.replaceAll("\"", "");
            Hitter h = stringToHitter(s);
            hitters.add(h);
        }
        return hitters;
    }

    @Override
    public ObservableList<Pitcher> loadPitchers(String filePath) throws IOException {
        ArrayList<String> pitchersArray = loadArrayFromJSONFile(filePath, JSON_PITCHERS);
        ObservableList<Pitcher> pitchers = FXCollections.observableArrayList();
        for (String s : pitchersArray) {
            // GET RID OF ALL THE QUOTE CHARACTERS
            s = s.replaceAll("\"", "");
            Pitcher p = stringToPitcher(s);
            p.setPositions("P");
            pitchers.add(p);
        }
        return pitchers;
    }
 
    // TAKE AS INPUT A STRING AND RETURN A PITCHER
    public Pitcher stringToPitcher(String s) {
        Pitcher p = new Pitcher();
        p.setProTeam(s.substring(s.indexOf(':')+1, s.indexOf(',')));
        s = s.substring(s.indexOf(',')+1);
        p.setLastName(s.substring(s.indexOf(':')+1, s.indexOf(',')));
        s = s.substring(s.indexOf(',')+1);
        p.setFirstName(s.substring(s.indexOf(':')+1, s.indexOf(',')));
        s = s.substring(s.indexOf(',')+1);
        p.setInningsPitched(Double.parseDouble(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        p.setEarnedRuns(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        //p.setWins(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        p.setRunsWins(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        //p.setSaves(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        p.setHrSave(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        p.setHits(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        p.setWalks(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        //p.setStrikeouts(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        p.setRbiK(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        p.setNotes(s.substring(s.indexOf(':')+1, s.indexOf(',')));
        s = s.substring(s.indexOf(',')+1);
        p.setBirthYear(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        p.setBirthNation(s.substring(s.indexOf(':')+1, s.indexOf('}')));
        p.setPositions("P");
        p.computeERA();
        p.computeWHIP();
        
        return p;
    }
    
    // TAKE AS INPUT A STRING AND RETURN A HITTER
    public Hitter stringToHitter(String s) {
        Hitter h = new Hitter();
        h.setProTeam(s.substring(s.indexOf(':')+1, s.indexOf(',')));
        s = s.substring(s.indexOf(',')+1);
        h.setLastName(s.substring(s.indexOf(':')+1, s.indexOf(',')));
        s = s.substring(s.indexOf(',')+1);
        h.setFirstName(s.substring(s.indexOf(':')+1, s.indexOf(',')));
        s = s.substring(s.indexOf(',')+1);
        h.setPositions(s.substring(s.indexOf(':')+1, s.indexOf(',')));
        s = s.substring(s.indexOf(',')+1);
        h.setAtBats(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        //h.setRuns(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        h.setRunsWins(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        h.setHits(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        //h.setHomeruns(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        h.setHrSave(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        //h.setRbis(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        h.setRbiK(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        //h.setStolenBases(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        h.setSbERA(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        h.setNotes(s.substring(s.indexOf(':')+1, s.indexOf(',')));
        s = s.substring(s.indexOf(',')+1);
        h.setBirthYear(Integer.parseInt(s.substring(s.indexOf(':')+1, s.indexOf(','))));
        s = s.substring(s.indexOf(',')+1);
        h.setBirthNation(s.substring(s.indexOf(':')+1, s.indexOf('}')));
        h.computeBattingAverage();
        h.adjustPositions();
        
        return h;
    }

    // MAKE A JSON ARRAY FOR TEAMS IN A DRAFT
    private JsonArray makeFantasyTeamsJsonArray(ObservableList<Team> fantasyTeams) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (Team t : fantasyTeams) {
            jsb.add(makeTeamJsonObject(t));
        }
        JsonArray jA = jsb.build();
        return jA;
    }

    // MAKE A JSON ARRAY OF HITTERS
    private JsonArray makeHittersJsonArray(ObservableList<Hitter> hitters) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (Hitter h : hitters) {
           jsb.add(makeHitterJsonObject(h));
        }
        JsonArray jA = jsb.build();
        return jA; 
    }

    // MAKE A JSON ARRAY OF PITCHERS
    private JsonArray makePitchersJsonArray(ObservableList<Pitcher> pitchers) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (Pitcher p : pitchers) {
           jsb.add(makePitcherJsonObject(p));
        }
        JsonArray jA = jsb.build();
        return jA; 
    }

    // MAKE A JSON ARRAY FOR A DRAFTS AVAILABLE PLAYERS
    private JsonArray makeAvailablePlayersJsonArray(ObservableList<Player> availablePlayers) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (Player p : availablePlayers) {
           jsb.add(makePlayerJsonObject(p));
        }
        JsonArray jA = jsb.build();
        return jA; 
    }
    
    // MAKE A JSON ARRAY FOR A TEAMS ROSTER
    private JsonArray makeRosterJsonArray(ObservableList<Player> roster) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (Player p : roster) {
           jsb.add(makePlayerJsonObject(p));
        }
        JsonArray jA = jsb.build();
        return jA; 
    }

    // MAKE A JSON OBJECT FOR A TEAM
    private JsonObject makeTeamJsonObject(Team t) {
        JsonObject jso = Json.createObjectBuilder().add(JSON_TEAM_NAME, t.getName())
                                                    .add(JSON_TEAM_OWNER, t.getOwner())
                                                    .add("points", t.getPoints())
                                                    .add(JSON_MONEY, t.getMoney())
                                                    .add(JSON_PITCHERS, makePitchersJsonArray(t.getPitchers()))
                                                    .add(JSON_HITTERS, makeHittersJsonArray(t.getHitters()))
                                                    .add(JSON_ROSTER, makeRosterJsonArray(t.getRoster()))
                                                    .add(JSON_TAXI_SQUAD, makeRosterJsonArray(t.getTaxiSquad()))
                                                    .build();
        return jso;
    }

    // MAKE A JSON OBJECT FOR A HITTER
    private JsonObject makeHitterJsonObject(Hitter h) {
        JsonObject jso = Json.createObjectBuilder().add("proTeam", h.getProTeam())
                                                    .add(JSON_FIRST_NAME_VAR, h.getFirstName())
                                                    .add(JSON_LAST_NAME_VAR, h.getLastName())
                                                    .add("pick", h.getPick())
                                                    .add("estimatedValue", h.getEstimatedValue())
                                                    .add("positions", h.getPositions())
                                                    .add("positionInTeam", h.getPositionInTeam())
                                                    .add("notes", h.getNotes())
                                                    .add("birthYear", h.getBirthYear())
                                                    .add("birthNation", h.getBirthNation())
                                                    .add("estimatedValue", h.getEstimatedValue())
                                                    .add("runsWins", h.getRunsWins())
                                                    .add("hrSave", h.getHrSave())
                                                    .add("rbiK", h.getRbiK())
                                                    .add("sbERA", h.getSbERA())
                                                    .add("baWHIP", h.getBaWHIP())
                                                    .add("fantasyTeam", h.getFantasyTeam())
                                                    .add("contract", h.getContract())
                                                    .add("salary", h.getSalary())
                                                    .add("positionCandidates", makePositionsJsonArray(h.getPositionCandidates()))
                                                    .add("atBats", h.getAtBats())
                                                    .add("runs", h.getRuns())
                                                    .add("hits", h.getHits())
                                                    .add("homeruns", h.getHomeruns())
                                                    .add("rbis", h.getRbis())
                                                    .add("stolenBases", h.getStolenBases())
                                                    .build();
        return jso;
    }

    // MAKE A JSON OBJECT FOR A PITCHER
    private JsonObject makePitcherJsonObject(Pitcher p) {
        JsonObject jso = Json.createObjectBuilder().add("proTeam", p.getProTeam())
                                                    .add(JSON_FIRST_NAME_VAR, p.getFirstName())
                                                    .add(JSON_LAST_NAME_VAR, p.getLastName())
                                                    .add("pick", p.getPick())
                                                    .add("estimatedValue", p.getEstimatedValue())
                                                    .add("positions", p.getPositions())
                                                    .add("positionInTeam", p.getPositionInTeam())
                                                    .add("notes", p.getNotes())
                                                    .add("birthYear", p.getBirthYear())
                                                    .add("birthNation", p.getBirthNation())
                                                    .add("estimatedValue", p.getEstimatedValue())
                                                    .add("runsWins", p.getRunsWins())
                                                    .add("hrSave", p.getHrSave())
                                                    .add("rbiK", p.getRbiK())
                                                    .add("sbERA", p.getSbERA())
                                                    .add("baWHIP", p.getBaWHIP())
                                                    .add("fantasyTeam", p.getFantasyTeam())
                                                    .add("contract", p.getContract())
                                                    .add("salary", p.getSalary())
                                                    .add("positionCandidates", makePositionsJsonArray(p.getPositionCandidates()))
                                                    .add("inningsPitched", p.getInningsPitched())
                                                    .add("earnedRuns", p.getEarnedRuns())
                                                    .add("wins", p.getWins())
                                                    .add("saves", p.getSaves())
                                                    .add("hits", p.getHits())
                                                    .add("walks", p.getWalks())
                                                    .add("strikeouts", p.getStrikeouts())
                                                    .build();
        return jso;
    }

    // MAKE A JSON OBJECT FOR A PLAYER
    private JsonObject makePlayerJsonObject(Player p) {
        if (p.getPositions().contains("P"))
           return makePitcherJsonObject((Pitcher)p);
        else return makeHitterJsonObject((Hitter)p);
        /*JsonObject jso = Json.createObjectBuilder().add("proTeam", p.getProTeam())
                                                    .add(JSON_FIRST_NAME_VAR, p.getFirstName())
                                                    .add(JSON_LAST_NAME_VAR, p.getLastName())
                                                    .add("positions", p.getPositions())
                                                    .add("positionInTeam", p.getPositionInTeam())
                                                    .add("notes", p.getNotes())
                                                    .add("birthYear", p.getBirthYear())
                                                    .add("birthNation", p.getBirthNation())
                                                    .add("estimatedValue", p.getEstimatedValue())
                                                    .add("runsWins", p.getRunsWins())
                                                    .add("hrSave", p.getHrSave())
                                                    .add("rbiK", p.getRbiK())
                                                    .add("sbERA", p.getSbERA())
                                                    .add("baWHIP", p.getBaWHIP())
                                                    .add("fantasyTeam", p.getFantasyTeam())
                                                    .add("contract", p.getContract())
                                                    .add("salary", p.getSalary())
                                                    .add("positionCandidates", makePositionsJsonArray(p.getPositionCandidates()))
                                                    .build();
        return jso;*/
    }

    // MAKE A JSON ARRAY FOR POSITION CANDIDATES
    private JsonArray makePositionsJsonArray(ObservableList<String> candidates) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (String s : candidates) {
           jsb.add(s);
        }
        JsonArray jA = jsb.build();
        return jA; 
    }
    // and load it
    private ObservableList<String> loadPositionsList(JsonArray array) {
        ObservableList<String> list = FXCollections.observableArrayList();
        for (int i=0;i<array.size();i++) {
            list.add(array.getString(i));
        }
        return list;
    }

    private void initPlayer(Player p, JsonObject json) {
        p.setProTeam(json.getString("proTeam"));
        p.setFirstName(json.getString(JSON_FIRST_NAME_VAR));
        p.setLastName(json.getString(JSON_LAST_NAME_VAR));
        p.setPick(json.getInt("pick"));
        p.setEstimatedValue(json.getInt("estimatedValue"));
        p.setPositions(json.getString("positions"));
        p.setPositionInTeam(json.getString("positionInTeam"));
        p.setNotes(json.getString("notes"));
        p.setBirthYear(json.getInt("birthYear"));
        p.setBirthNation(json.getString("birthNation"));
        p.setEstimatedValue(json.getInt("estimatedValue"));
        p.setRunsWins(json.getInt("runsWins"));
        p.setHrSave(json.getInt("hrSave"));
        p.setRbiK(json.getInt("rbiK"));
        p.setSbERA(json.getInt("sbERA"));
        p.setBaWHIP(json.getInt("baWHIP"));
        p.setFantasyTeam(json.getString("fantasyTeam"));
        p.setContract(json.getString("contract"));
        p.setSalary(json.getInt("salary"));
        p.setPositionCandidates(loadPositionsList(json.getJsonArray("positionCandidates")));
    }
}
