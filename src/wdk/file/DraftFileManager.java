/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import wdk.data.Draft;
import wdk.data.Hitter;
import wdk.data.Pitcher;

/**
 * This interface provides an abstraction of what a file manager should do. Note
 * that file managers know how to read and write courses, instructors, and subjects,
 * but now how to export sites.
 * 
 * @author Guacamole
 */
public interface DraftFileManager {
    public void                      saveDraft(Draft draftToSave) throws IOException;
    public void                      loadDraft(Draft draftToLoad, String coursePath) throws IOException;
    public void                      savePlayers(List<Object> subjects, String filePath) throws IOException;
    public ObservableList<Hitter>    loadHitters(String filePath) throws IOException;
    public ObservableList<Pitcher>   loadPitchers(String filePath) throws IOException;
}
