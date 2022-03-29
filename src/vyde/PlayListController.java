/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vyde;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.io.File;
import java.net.MalformedURLException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author User
 */
public class PlayListController implements Initializable {

    private final ObservableList playListFiles = FXCollections.observableArrayList();
    private final ObjectProperty<String> selectedMedia = new SimpleObjectProperty<>();
    private final ObjectProperty<String> deletedMedia = new SimpleObjectProperty<>();

    @FXML
    private Button addButton;

    @FXML
    private Button doneButton;

    @FXML
    private TextField text0;

    @FXML
    private ListView<?> PlayList;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        PlayList.setOnMouseClicked((click) -> {
            if (click.getClickCount() == 2) {
                if (PlayList.getSelectionModel().getSelectedItem() != null) {
                    selectedMedia.setValue((String) PlayList.getSelectionModel().getSelectedItem());
                }
            }
        });
    }
    List<String> listOfFiles = new ArrayList<>();

    @FXML
    void addSongs(ActionEvent event) throws MalformedURLException {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(null);
        String st = file.toURI().toURL().toString();
        //chooser.getExtensionFilters().addAll(
        //new FileChooser.ExtensionFilter("Files",
        //PropertiesUtils.readFormats()));

        listOfFiles.add(st);
        //listOfFiles = FileUtils.convertListFiletoListPath(chooser.showOpenDialog(((Button) event.getSource()).getScene().getWindow()));
        listOfFiles.stream().forEach(System.out::println);
        listOfFiles.stream().forEach(playListFiles::add);
        listOfFiles.clear();
        playListFiles.stream().forEach(System.out::println);
        PlayList.setItems(playListFiles);
    }

    @FXML
    void removeSongs(ActionEvent event) {
        if (PlayList.getSelectionModel().getSelectedItem() != null) {
            if (null != playListFiles || !playListFiles.isEmpty()) {
                deletedMedia.setValue((String) PlayList.getSelectionModel().getSelectedItem());
                playListFiles.remove(PlayList.getSelectionModel().getSelectedItem());
                //PlayList.setItems(playListFiles);
            }
        }
    }

    @FXML
    void doneCreating(ActionEvent event) {
        if(playListFiles.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Playlist cannot be empty");
            return;
        }
        boolean flag = true, flag2 = false;
        for (Object lis : playListFiles) {
            String query = "INSERT INTO VYDE.PLAYLISTS (TITTLEDIFF, TITLE, MEDIAF) VALUES (?,?,?)";
            PreparedStatement pstate;
            String title = text0.getText();
            System.out.println(title);
            try {
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/PLAYLISTS", "Vyde", "vyde");
                pstate = con.prepareStatement(query);
                if(flag){
                  pstate.setString(1, title);
                  flag=false;
                }
                else {
                  pstate.setString(1, null);
                }
                pstate.setString(2, title);
                pstate.setString(3, lis.toString());
                if(pstate.executeUpdate()>0) flag2 = true;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Playlist with same name already exists");
                flag2=false;
                break;
            }
        }
        if(flag2) {
            JOptionPane.showMessageDialog(null, "Playlist successfully created");
            Stage stage = (Stage) doneButton.getScene().getWindow();
            stage.close();
        }
    }
}
