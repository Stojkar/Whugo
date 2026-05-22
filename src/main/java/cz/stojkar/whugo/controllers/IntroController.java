package cz.stojkar.whugo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class IntroController {

    @FXML
    private MediaView introMediaView;

    @FXML
    public void initialize() {
        try {
            // Cesta k videu (musí existovat ve složce resources nebo projektu)
            java.net.URL resource = getClass().getResource("/cz/stojkar/whugo/videos/intro.mp4");
            if (resource == null) {
                throw new IllegalArgumentException("Video intro.mp4 nenalezeno.");
            }
            String videoPath = resource.toExternalForm();
            Media media = new Media(videoPath);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            introMediaView.setMediaPlayer(mediaPlayer);

            mediaPlayer.setOnEndOfMedia(this::skipIntro);
            
            // Přizpůsobení velikosti okna
            introMediaView.setFitWidth(1280);
            introMediaView.setFitHeight(720);

            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Video intro.mp4 nebylo nalezeno nebo nelze přehrát. Přeskakuji do hry.");
            // Pokud video neexistuje, automaticky přeskočíme po 1 vteřině (aby se scéna stihla načíst)
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
            pause.setOnFinished(event -> skipIntro());
            pause.play();
        }
    }

    @FXML
    public void onSkipClicked(MouseEvent event) {
        if (introMediaView.getMediaPlayer() != null) {
            introMediaView.getMediaPlayer().stop();
        }
        skipIntroHelper(event);
    }

    private void skipIntro() {
        // Tady potřebujeme získat Stage. Protože setOnEndOfMedia neběží z eventu myši,
        // musíme si Stage vzít ze samotného MediaView
        if (introMediaView.getScene() != null) {
            Stage stage = (Stage) introMediaView.getScene().getWindow();
            goToGame(stage);
        }
    }

    private void skipIntroHelper(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        goToGame(stage);
    }

    private void goToGame(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cz/stojkar/whugo/game-view.fxml"));
            Parent gameRoot = loader.load();
            Scene gameScene = new Scene(gameRoot);
            stage.setScene(gameScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
