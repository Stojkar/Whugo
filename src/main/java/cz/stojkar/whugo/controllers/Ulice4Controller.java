package cz.stojkar.whugo.controllers;

import cz.stojkar.whugo.GameController;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;

public class Ulice4Controller {

    @FXML
    public void onMouseEntered(MouseEvent event) {
        ((ImageView)event.getSource()).setCursor(Cursor.HAND);
    }
    
    @FXML
    public void onMouseExited(MouseEvent event) {
        ((ImageView)event.getSource()).setCursor(Cursor.DEFAULT);
    }

    @FXML
    public void goToUlice3() {
        GameController.getInstance().loadLocation("ulice3");
    }

    @FXML
    public void goToUlice5() {
        GameController.getInstance().loadLocation("ulice5");
    }

    @FXML
    public void goToHugoDum() {
        GameController.getInstance().loadLocation("hugo_dum");
    }
}
