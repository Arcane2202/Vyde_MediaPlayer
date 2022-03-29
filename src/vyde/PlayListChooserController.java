    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vyde;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author User
 */
public class PlayListChooserController implements Initializable {

    final ObservableList playListFiles = FXCollections.observableArrayList();
    final ObjectProperty<String> selectedMedia = new SimpleObjectProperty<>();
    final ObjectProperty<String> deletedMedia = new SimpleObjectProperty<>();
    static FXMLDocumentController kon;
    @FXML
    ListView<?> PlayList;

    @FXML
    private Button doneB;

    @FXML
    private Button cancelB;

    @FXML
    void cancelC(ActionEvent event) {
        Stage stage = (Stage) cancelB.getScene().getWindow();
        stage.close();
        FXMLLoader loll = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent Root;
        try {
            Root = loll.load();
            Scene scene = new Scene(Root);
            scene.getStylesheets().add("/CSS/styling.css");
            stage.setTitle("Vyde Media Player");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(PlayListChooserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void doneC(ActionEvent event) throws SQLException, IOException {
        if (PlayList.getSelectionModel().getSelectedItem() != null) {
            if (null != playListFiles || !playListFiles.isEmpty()) {
                FXMLDocumentController obj = new FXMLDocumentController();
                obj.flag = true;
                
                //deletedMedia.setValue((String) PlayList.getSelectionModel().getSelectedItem());
                String st = PlayList.getSelectionModel().getSelectedItem().toString();
                //PlayList.setItems(playListFiles);
                Stage stage = (Stage) cancelB.getScene().getWindow();
                stage.close();
                String query = "SELECT MEDIAF FROM VYDE.PLAYLISTS WHERE TITLE=?";
                PreparedStatement pstate;
                Connection con;
                try {
                    FXMLLoader loll = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
                    Parent Root = loll.load();
                    kon = (FXMLDocumentController) loll.getController();
                    con = DriverManager.getConnection("jdbc:derby://localhost:1527/PLAYLISTS", "Vyde", "vyde");
                    pstate = con.prepareStatement(query);
                    pstate.setString(1, st);
                    ResultSet rs = pstate.executeQuery();
                    while (rs.next()) {
                        kon.listOfFiles.add(rs.getString("MEDIAF"));
                    }
                    Scene scene = new Scene(Root);
                    stage.setTitle("Vyde Media Player");
                    stage.setScene(scene);
                    scene.getStylesheets().add("/CSS/styling.css");
                    stage.show();
                    kon.play();
                } catch (SQLException ex) {
                    Logger.getLogger(PlayListChooserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        String query = "SELECT TITTLEDIFF FROM VYDE.PLAYLISTS WHERE TITTLEDIFF != 'null'";
        PreparedStatement pstate;
        Connection con;
        try {
            con = DriverManager.getConnection("jdbc:derby://localhost:1527/PLAYLISTS", "Vyde", "vyde");
            pstate = con.prepareStatement(query);
            ResultSet rs = pstate.executeQuery();
            while (rs.next()) {
                playListFiles.add(rs.getString("TITTLEDIFF"));
                PlayList.setItems(playListFiles);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlayListChooserController.class.getName()).log(Level.SEVERE, null, ex);
        }
        PlayList.setOnMouseClicked((click) -> {
            if (click.getClickCount() == 2) {
                if (PlayList.getSelectionModel().getSelectedItem() != null) {
                    selectedMedia.setValue((String) PlayList.getSelectionModel().getSelectedItem());
                }
            }
        });
    }
}
