package cz.stojkar.whugo.controllers;

import cz.stojkar.whugo.GameController;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;

public class Ulice1Controller {

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
    public void goToHospoda() {
        GameController.getInstance().loadLocation("hospoda");
    }
}
