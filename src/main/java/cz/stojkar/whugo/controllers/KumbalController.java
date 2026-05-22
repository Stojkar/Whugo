package cz.stojkar.whugo.controllers;

import cz.stojkar.whugo.GameController;
import cz.stojkar.whugo.SoundManager;
import cz.stojkar.whugo.model.GameState;
import cz.stojkar.whugo.model.Item;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class KumbalController {

    @FXML private Rectangle kytkaZona;

    @FXML
    public void onKytkaEntered(MouseEvent event) {
        kytkaZona.setCursor(Cursor.HAND);
        kytkaZona.setFill(javafx.scene.paint.Color.web("rgba(0,255,0,0.1)")); // Lehce zelené podsvícení
    }

    @FXML
    public void onKytkaExited(MouseEvent event) {
        kytkaZona.setCursor(Cursor.DEFAULT);
        kytkaZona.setFill(javafx.scene.paint.Color.TRANSPARENT);
    }

    @FXML
    public void onKytkaClicked(MouseEvent event) {
        if (!GameState.getInstance().getInventory().isEmpty()) {
            GameController.getInstance().showDialogue("Mám plné ruce.");
            return;
        }
        SoundManager.getInstance().playPickup();
        
        // Vytvoříme kytku jako předmět a dáme ji do inventáře
        Item kytka = new Item("kytka", "Květina", "cz/stojkar/whugo/images/kytka.png");
        GameState.getInstance().addItem(kytka);
        
        // Uzamkneme kůlnu
        GameState.getInstance().setFlag("kulna_odemcena", false);

        // Zpět do Hugo zahrady
        GameController.getInstance().loadLocation("hugo_zahrada");
        GameController.getInstance().showDialogue("Sebral jsem kouzelnou květinu. Teď můžu odjet autem!");
        GameController.getInstance().updateInventoryUI();
    }

    @FXML
    public void onMouseEntered(MouseEvent event) {
        if (event.getSource() instanceof javafx.scene.Node) {
            ((javafx.scene.Node) event.getSource()).setCursor(Cursor.HAND);
        }
    }
    
    @FXML
    public void onMouseExited(MouseEvent event) {
        if (event.getSource() instanceof javafx.scene.Node) {
            ((javafx.scene.Node) event.getSource()).setCursor(Cursor.DEFAULT);
        }
    }

    @FXML
    public void goToHugoZahrada() {
        GameState.getInstance().setFlag("kulna_odemcena", false);
        GameController.getInstance().loadLocation("hugo_zahrada");
    }
}
