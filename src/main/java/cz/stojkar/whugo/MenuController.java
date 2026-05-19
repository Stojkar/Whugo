package cz.stojkar.whugo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML
    protected void MenuPlayButtonClick(ActionEvent event) throws IOException {
        cz.stojkar.whugo.model.GameState.resetInstance();

        // 1. Načtení nového FXML souboru
        FXMLLoader loader = new FXMLLoader(getClass().getResource("intro.fxml"));
        Parent gameViewRoot = loader.load();

        // 2. Vytvoření nové scény s načteným obsahem
        Scene gameScene = new Scene(gameViewRoot);

        // 3. Získání aktuálního okna (Stage) skrze událost kliknutí
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // 4. Nastavení nové scény do okna
        stage.setScene(gameScene);
        stage.show();
    }

    @FXML
    protected void onExitButtonClick() {
        javafx.application.Platform.exit();
    }
}
