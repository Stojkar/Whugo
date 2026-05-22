package cz.stojkar.whugo.controllers;

import cz.stojkar.whugo.GameController;
import cz.stojkar.whugo.model.GameState;
import cz.stojkar.whugo.model.Item;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class TvujDumController {

    @FXML private Rectangle autoZona;

    @FXML
    public void onMouseEntered(MouseEvent event) {
        ((ImageView)event.getSource()).setCursor(Cursor.HAND);
    }
    
    @FXML
    public void onMouseExited(MouseEvent event) {
        ((ImageView)event.getSource()).setCursor(Cursor.DEFAULT);
    }

    @FXML
    public void goToUlice2() {
        GameController.getInstance().loadLocation("ulice2");
    }

    @FXML
    public void onAutoEntered(MouseEvent event) {
        autoZona.setCursor(Cursor.HAND);
        autoZona.setFill(javafx.scene.paint.Color.web("rgba(255,255,0,0.1)")); // Lehce žluté podsvícení
    }

    @FXML
    public void onAutoExited(MouseEvent event) {
        autoZona.setCursor(Cursor.DEFAULT);
        autoZona.setFill(javafx.scene.paint.Color.TRANSPARENT);
    }

    @FXML
    public void onAutoClicked(MouseEvent event) {
        boolean hasKytka = false;
        if (!GameState.getInstance().getInventory().isEmpty()) {
            Item item = GameState.getInstance().getInventory().get(0);
            if (item.getId().equals("kytka")) {
                hasKytka = true;
            }
        }
        
        if (hasKytka) {
            GameController.getInstance().showDialogue("Nasedl jsem do auta a s květinou odjíždím...");
            
            javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
            delay.setOnFinished(e -> {
                try {
                    Stage stage = (Stage) autoZona.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/cz/stojkar/whugo/menu-view.fxml"));
                    Scene scene = new Scene(loader.load(), 1280, 720);
                    stage.setScene(scene);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            delay.play();
        } else {
            GameController.getInstance().showDialogue("Nemám důvod, proč odjet.");
        }
    }
}
