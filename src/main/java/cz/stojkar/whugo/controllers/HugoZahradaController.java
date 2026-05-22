package cz.stojkar.whugo.controllers;

import cz.stojkar.whugo.GameController;
import cz.stojkar.whugo.model.GameState;
import cz.stojkar.whugo.model.Item;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;

public class HugoZahradaController {

    @FXML private ImageView zebrikNaStene;

    @FXML
    public void initialize() {
        if (GameState.getInstance().getFlag("zebrik_na_stene")) {
            zebrikNaStene.setVisible(true);
        }
    }

    @FXML
    public void onMouseEntered(MouseEvent event) {
        ((ImageView)event.getSource()).setCursor(Cursor.HAND);
    }
    
    @FXML
    public void onMouseExited(MouseEvent event) {
        ((ImageView)event.getSource()).setCursor(Cursor.DEFAULT);
    }

    @FXML
    public void onOknoEntered(MouseEvent event) {
        javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) event.getSource();
        rect.setCursor(Cursor.HAND);
        rect.setFill(javafx.scene.paint.Color.web("rgba(255,255,255,0.2)")); // Lehce bílé podsvícení
    }

    @FXML
    public void onOknoExited(MouseEvent event) {
        javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) event.getSource();
        rect.setCursor(Cursor.DEFAULT);
        rect.setFill(javafx.scene.paint.Color.TRANSPARENT);
    }

    @FXML
    public void goToHugoDum() {
        GameController.getInstance().loadLocation("hugo_dum");
    }

    @FXML
    public void goToRybnik() {
        GameController.getInstance().loadLocation("rybnik");
    }

    @FXML
    public void onDragOverOkno(DragEvent event) {
        if (event.getDragboard().hasString() && event.getDragboard().getString().equals("zebrik")) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    @FXML
    public void onDragDroppedOkno(DragEvent event) {
        if (event.getDragboard().hasString() && event.getDragboard().getString().equals("zebrik")) {
            // Označíme, že je žebřík zafixovaný
            cz.stojkar.whugo.SoundManager.getInstance().playLadder();
            GameState.getInstance().setFlag("zebrik_na_stene", true);
            
            // Odstraníme žebřík z inventáře hráče
            if (!GameState.getInstance().getInventory().isEmpty()) {
                Item zebrik = GameState.getInstance().getInventory().get(0);
                GameState.getInstance().removeItem(zebrik);
            }
            
            // Zobrazíme ho ve scéně
            zebrikNaStene.setVisible(true);
            GameController.getInstance().updateInventoryUI();
            
            event.setDropCompleted(true);
        }
        event.consume();
    }

    @FXML
    public void goToVnitrekDomu(MouseEvent event) {
        boolean hasZebrik = false;
        if (!GameState.getInstance().getInventory().isEmpty()) {
            Item item = GameState.getInstance().getInventory().get(0);
            if (item.getId().equals("zebrik")) {
                hasZebrik = true;
            }
        }

        if (hasZebrik) {
            cz.stojkar.whugo.SoundManager.getInstance().playLadder();
            GameState.getInstance().setFlag("zebrik_na_stene", true);
            
            // Odstraníme žebřík z inventáře
            Item zebrik = GameState.getInstance().getInventory().get(0);
            GameState.getInstance().removeItem(zebrik);
            
            zebrikNaStene.setVisible(true);
            GameController.getInstance().updateInventoryUI();
            GameController.getInstance().loadLocation("vnitrek_domu");
        } else if (GameState.getInstance().getFlag("zebrik_na_stene")) {
            GameController.getInstance().loadLocation("vnitrek_domu");
        } else {
            GameController.getInstance().showDialogue("Okno je moc vysoko, nedosáhnu tam.");
        }
    }

    // ── Kůlna interakce ───────────────────────────────────────────────────────

    @FXML
    public void onKulnaEntered(MouseEvent event) {
        javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) event.getSource();
        rect.setCursor(Cursor.HAND);
        rect.setFill(javafx.scene.paint.Color.web("rgba(255,255,255,0.2)")); // Lehce bílé podsvícení
    }

    @FXML
    public void onKulnaExited(MouseEvent event) {
        javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) event.getSource();
        rect.setCursor(Cursor.DEFAULT);
        rect.setFill(javafx.scene.paint.Color.TRANSPARENT);
    }

    @FXML
    public void onDragOverKulna(DragEvent event) {
        if (event.getDragboard().hasString() && event.getDragboard().getString().equals("klic")) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    @FXML
    public void onDragDroppedKulna(DragEvent event) {
        if (event.getDragboard().hasString() && event.getDragboard().getString().equals("klic")) {
            cz.stojkar.whugo.SoundManager.getInstance().playPickup(); // Přehrání zvuku
            GameState.getInstance().setFlag("kulna_odemcena", true);
            
            // Odstraníme klíč z inventáře hráče
            if (!GameState.getInstance().getInventory().isEmpty()) {
                Item klic = GameState.getInstance().getInventory().get(0);
                GameState.getInstance().removeItem(klic);
            }
            
            GameController.getInstance().updateInventoryUI();
            event.setDropCompleted(true);
            
            // Přejdeme do kumbalu
            GameController.getInstance().loadLocation("kumbal");
        }
        event.consume();
    }

    @FXML
    public void goToKulna(MouseEvent event) {
        boolean hasKlic = false;
        if (!GameState.getInstance().getInventory().isEmpty()) {
            Item item = GameState.getInstance().getInventory().get(0);
            if (item.getId().equals("klic")) {
                hasKlic = true;
            }
        }

        if (hasKlic) {
            cz.stojkar.whugo.SoundManager.getInstance().playPickup();
            GameState.getInstance().setFlag("kulna_odemcena", true);
            
            // Odstraníme klíč z inventáře
            Item klic = GameState.getInstance().getInventory().get(0);
            GameState.getInstance().removeItem(klic);
            
            GameController.getInstance().updateInventoryUI();
            GameController.getInstance().loadLocation("kumbal");
        } else if (GameState.getInstance().getFlag("kulna_odemcena")) {
            GameController.getInstance().loadLocation("kumbal");
        } else {
            GameController.getInstance().showDialogue("Kůlna je zamčená, potřebuji klíč.");
        }
    }
}
