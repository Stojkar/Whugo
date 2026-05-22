package cz.stojkar.whugo.controllers;

import cz.stojkar.whugo.GameController;
import cz.stojkar.whugo.model.GameState;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class HugoDumController {

    @FXML private ImageView npcStarec;

    // ── NPC interakce ─────────────────────────────────────────────────────────

    @FXML
    public void onNpcClicked(MouseEvent event) {
        GameController.getInstance().loadLocation("hugo_chat");
    }

    @FXML
    public void onNpcEntered(MouseEvent event) {
        npcStarec.setCursor(Cursor.HAND);
        npcStarec.setOpacity(0.85);
    }

    @FXML
    public void onNpcExited(MouseEvent event) {
        npcStarec.setCursor(Cursor.DEFAULT);
        npcStarec.setOpacity(1.0);
    }

    // ── Navigace ─────────────────────────────────────────────────────────────

    @FXML
    public void goToUlice4() {
        GameController.getInstance().loadLocation("ulice4");
    }

    /**
     * Šipka do zahrady – NPC hráče zastaví hláškou,
     * ale po 2 sekundách ho stejně pustí dál.
     * Pokud NPC už mluvil, pustí rovnou.
     */
    @FXML
    public void goToHugoZahrada() {
        if (!GameState.getInstance().getInventory().isEmpty()) {
            GameController.getInstance().showDialogue("Stařec: „S tímhle předmětem tě dál nepustím! Vrať se!“");
        } else {
            GameController.getInstance().loadLocation("hugo_zahrada");
        }
    }

    // ── Šipky hover ──────────────────────────────────────────────────────────

    @FXML
    public void onMouseEntered(MouseEvent event) {
        ((ImageView) event.getSource()).setCursor(Cursor.HAND);
    }

    @FXML
    public void onMouseExited(MouseEvent event) {
        ((ImageView) event.getSource()).setCursor(Cursor.DEFAULT);
    }
}
