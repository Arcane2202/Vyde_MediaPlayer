/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vyde;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author User
 */
public class FXMLDocumentController implements Initializable {

    MediaPlayer player;

    boolean flag = false;

    List<String> listOfFiles = new ArrayList<>();
    int it = 0;
    
    @FXML
    private BorderPane BorderPane;
    
    @FXML
    private Button prevB;

    @FXML
    private Button nextB;

    @FXML
    private MenuItem playlistButton;

    @FXML
    private MenuItem playlistCreator;

    @FXML
    Button stopButton;

    @FXML
    private Slider seekerSlide;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Button muteButton;

    @FXML
    private Button playButton;

    @FXML
    private MediaView mediaView;

    @FXML
    private VBox vbox;

    @FXML
    private Button slowButton;

    @FXML
    private Button fastButton;

    @FXML
    private MenuBar MenuBar;
    
    
    @FXML
    private MenuItem exitB;
    
    String strg;

    @FXML
    void openSongMenu(ActionEvent event) {
        it=0;
        listOfFiles.clear();
        System.out.println("Open Executed");
        FileChooser fileChoose = new FileChooser();
        File file = fileChoose.showOpenDialog(null);
        try {
            listOfFiles.add(file.toURI().toURL().toString());
        } catch (MalformedURLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        play();
    }
    Media med;
    void play() {
        if (it == 0) {
            prevB.setDisable(true);
        }
        if (it == listOfFiles.size() - 1) {
            nextB.setDisable(true);
        }
        med = new Media(listOfFiles.get(it));
        if (player != null) {
            player.dispose();
        }
        player = new MediaPlayer(med);
        mediaView.setMediaPlayer(player);
        DoubleProperty widthProp = mediaView.fitWidthProperty();
        DoubleProperty HeightProp = mediaView.fitHeightProperty();
        widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width").subtract(10));
        HeightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height").subtract(100));
        mediaView.setPreserveRatio(true);
        player.setOnReady(() -> {
            seekerSlide.setMin(0);
            seekerSlide.setMax(player.getMedia().getDuration().toMinutes());
            seekerSlide.setValue(0);
        });
        player.currentTimeProperty().addListener(new ChangeListener<javafx.util.Duration>() {
            @Override
            public void changed(ObservableValue<? extends javafx.util.Duration> observable, javafx.util.Duration oldValue, javafx.util.Duration newValue) {
                javafx.util.Duration curr = player.getCurrentTime();
                seekerSlide.setValue(curr.toMinutes());
            }
        });

        seekerSlide.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (seekerSlide.isPressed()) {
                    double val = seekerSlide.getValue();
                    player.seek(new javafx.util.Duration(val * 60000));
                }
            }
        });

        volumeSlider.setValue(player.getVolume() * 100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                player.setVolume(volumeSlider.getValue() / 100);
            }
        });
        //playButton.setText("pause");
        player.play();
        stopButton.setDisable(true);
        if(it<listOfFiles.size()-1){
        player.setOnEndOfMedia(() -> {
            it++;play();
        });
        }
    }

    @FXML
    void playMedia(ActionEvent event) {
        MediaPlayer.Status stat = player.getStatus();
        if (stat != MediaPlayer.Status.PLAYING) {
            player.play();
            //playButton.setText("❚❚");
        } else {
            player.pause();
            //playButton.setText("▷");
        }
        stopButton.setDisable(false);
        if(it<listOfFiles.size()-1){
        player.setOnEndOfMedia(() -> {
            it++;play();
        });
        }
    }

    @FXML
    void rewind(ActionEvent event) {
        double currDuration = player.getCurrentTime().toSeconds();
        currDuration -= 10;
        player.seek(new javafx.util.Duration(currDuration * 1000));
        stopButton.setDisable(false);
        if(player.currentTimeProperty().equals(med.getDuration())&&it<listOfFiles.size()-1){it++;play();}
    }

    @FXML
    void fastForward(ActionEvent event) {
        double currDuration = player.getCurrentTime().toSeconds();
        currDuration += 10;
        player.seek(new javafx.util.Duration(currDuration * 1000));
        stopButton.setDisable(false);
        if(player.currentTimeProperty().equals(med.getDuration())&&it<listOfFiles.size()-1){it++;play();}
    }

    @FXML
    void slow(ActionEvent event) {
        player.setRate(player.getRate() - 0.25);
        fastButton.setDisable(false);
        if (player.getRate() > 1) {
            //fastButton.setText(Double.toString(player.getRate()) + "x");
        } else if (player.getRate() == 1) {
            //fastButton.setText(">>");
            //slowButton.setText("<<");
        } else {
            //slowButton.setText(Double.toString(player.getRate()) + "x");
        }
        if (player.getRate() == 0.25) {
            slowButton.setDisable(true);
        }
        stopButton.setDisable(false);
        if(player.currentTimeProperty().equals(med.getDuration())&&it<listOfFiles.size()-1){it++;play();}
    }

    @FXML
    void fast(ActionEvent event) {
        player.setRate(player.getRate() + 0.25);
        slowButton.setDisable(false);
        if (player.getRate() < 1) {
            //slowButton.setText(Double.toString(player.getRate()) + "x");
        } else if (player.getRate() == 1) {
            //fastButton.setText(">>");
            //slowButton.setText("<<");
        } else {
            //fastButton.setText(Double.toString(player.getRate()) + "x");
        }
        if (player.getRate() == 2) {
            //fastButton.setDisable(true);
        }
        stopButton.setDisable(false);
        if(player.currentTimeProperty().equals(med.getDuration())&&it<listOfFiles.size()-1){it++;play();}
    }

    @FXML
    void stop(ActionEvent event) {
        player.stop();
        stopButton.setDisable(true);
        //playButton.setText("Play");
        if(player.currentTimeProperty().equals(med.getDuration())&&it<listOfFiles.size()-1){it++;play();}
    }

    @FXML
    void mute(ActionEvent event) {
        if (player.isMute()) {
            player.setMute(false);
            muteButton.setText("mute");
        } else {
            player.setMute(true);
            muteButton.setText("unmute");
        }
        if(player.currentTimeProperty().equals(med.getDuration())&&it<listOfFiles.size()-1){it++;play();}
    }

    @FXML
    void createPlaylist(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("PlayList.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Creating a Playlist");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String st;

    @FXML
    void playPlaylist(ActionEvent event) {
        //player.pause();
        try {
            Stage stage1 = (Stage) stopButton.getScene().getWindow();
            stage1.close();
            Parent root = FXMLLoader.load(getClass().getResource("PlayListChooser.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Choose Playlist To Play");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
       }
    }

    @FXML
    void prevC(ActionEvent event) {
        it--;
        if (it == 0) {
            prevB.setDisable(true);
        }
        if (it < listOfFiles.size() - 1) {
            nextB.setDisable(false);
        }
        play();
        if(player.currentTimeProperty().equals(med.getDuration())&&it<listOfFiles.size()-1){it++;play();}
    }

    @FXML
    void nextC(ActionEvent event) {
        it++;
        if (it == listOfFiles.size() - 1) {
            nextB.setDisable(true);
        }
        if (it > 0) {
            prevB.setDisable(false);
        }
        play();
        if(player.currentTimeProperty().equals(med.getDuration())&&it<listOfFiles.size()-1){it++;play();}
    }

    @FXML
    void file(ActionEvent event) {
    }

    @FXML
    void option(ActionEvent event) {
    }

    @FXML
    void about(ActionEvent event) {
        try {
            Parent root;
            root = FXMLLoader.load(getClass().getResource("about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            scene.getStylesheets().add("/CSS/about.css");
            stage.setTitle("About");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void exitC(ActionEvent event) {
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*try {
            ImageView lol = new ImageView(new Image(new FileInputStream("src/images/Play.png")));
            lol.setFitHeight(50);
            lol.setFitWidth(100);
            playButton.setGraphic(lol);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }*/ 
    }
}
