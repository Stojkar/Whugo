package cz.stojkar.whugo.controllers;

import cz.stojkar.whugo.GameController;
import cz.stojkar.whugo.SoundManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class HospodaController {

    // ── Zadní místnost ───────────────────────────────────────────────────────

    @FXML
    public void onZadniMistnostClicked(MouseEvent event) {
        SoundManager.getInstance().playKrknuti();
    }

    // ── Navigace a šipky ─────────────────────────────────────────────────────

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
    public void goToUlice1() {
        GameController.getInstance().loadLocation("ulice1");
    }
}
