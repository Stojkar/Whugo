package cz.stojkar.whugo;

import cz.stojkar.whugo.model.GameState;
import cz.stojkar.whugo.model.Item;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.SnapshotParameters;
import javafx.util.Duration;

import java.io.IOException;

public class GameController {

    private static GameController instance;

    @FXML
    private Pane scenePane; // Kontejner pro lokace
    @FXML
    private Label dialogueLabel;

    // Nové UI prvky
    @FXML
    private HBox inventoryBubble;
    @FXML
    private ImageView inventoryItemIcon;

    public GameController() {
        instance = this;
    }

    public static GameController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        updateInventoryUI();
        // Intro video nás přepne na tvuj_dum. Můžeme to zde nechat jako fallback.
        // Ale protože IntroController nás sem hází prázdné, musíme načíst lokaci.
        loadLocation("tvuj_dum");
    }

    public void loadLocation(String locationName) {
        SoundManager.getInstance().playArrow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cz/stojkar/whugo/" + locationName + ".fxml"));
            Node locationNode = loader.load();

            scenePane.getChildren().clear();
            scenePane.getChildren().add(locationNode);

            GameState.getInstance().setCurrentLocationId(locationName);
            updateHeroSprite(); // Zkusíme aktualizovat hrdinu, pokud v lokaci je
            renderDroppedItems(); // Vykreslíme věci, co hráč zahodil

        } catch (IOException e) {
            System.err.println("Nemohu načíst lokaci: " + locationName);
            e.printStackTrace();
        }
    }

    private void renderDroppedItems() {
        String currentLoc = GameState.getInstance().getCurrentLocationId();
        int offset = 0;
        for (Item item : GameState.getInstance().getDroppedItems(currentLoc)) {
            try {
                ImageView droppedIcon = new ImageView(
                        new Image(getClass().getResourceAsStream("/" + item.getImagePath())));
                droppedIcon.setPreserveRatio(true);

                // Speciální velikost a pozice pro žebřík v lokaci "hugo_zahrada" a "tvuj_dum"
                // (opřený o strom)
                if (item.getId().equals("zebrik")) {
                    droppedIcon.setFitWidth(200);
                    droppedIcon.setFitHeight(600);
                    if (currentLoc.equals("tvuj_dum")) {
                        droppedIcon.setLayoutX(40);
                        droppedIcon.setLayoutY(180);
                    } else {
                        droppedIcon.setLayoutX(860);
                        droppedIcon.setLayoutY(180);
                    }
                } else {
                    if (item.getId().equals("klic")) {
                        droppedIcon.setFitWidth(30);
                        droppedIcon.setFitHeight(30);
                        // Posuneme ho o něco níže na zem, protože je menší
                        droppedIcon.setLayoutY(500);
                    } else {
                        droppedIcon.setFitWidth(100);
                        droppedIcon.setFitHeight(100);
                        droppedIcon.setLayoutY(450);
                    }
                    // Umístíme předmět někam dolů na zem
                    droppedIcon.setLayoutX(300 + offset);
                    offset += 150;
                }

                droppedIcon.setCursor(Cursor.HAND);

                droppedIcon.setOnMouseClicked(e -> {
                    if (!GameState.getInstance().getInventory().isEmpty()) {
                        showDialogue("Mám plné ruce.");
                        return;
                    }
                    // Zvednutí předmětu
                    SoundManager.getInstance().playPickup();
                    GameState.getInstance().removeDroppedItem(item, currentLoc);
                    GameState.getInstance().addItem(item);
                    scenePane.getChildren().remove(droppedIcon);
                    showDialogue("Sebral jsem " + item.getName() + ".");
                    updateInventoryUI();
                });

                // Přidáme přímo do scény
                scenePane.getChildren().add(droppedIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void onSceneDragOver(DragEvent event) {
        if (event.getGestureSource() == inventoryItemIcon && event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
        event.consume();
    }

    @FXML
    public void onSceneDragDropped(DragEvent event) {
        if (event.getGestureSource() == inventoryItemIcon && event.getDragboard().hasString()) {
            if (!GameState.getInstance().getInventory().isEmpty()) {
                Item item = GameState.getInstance().getInventory().get(0);
                String currentLoc = GameState.getInstance().getCurrentLocationId();

                GameState.getInstance().removeItem(item);
                GameState.getInstance().dropItemInLocation(item, currentLoc);

                showDialogue("Položil jsem " + item.getName() + " na zem.");
                updateInventoryUI();
                loadLocation(currentLoc);

                event.setDropCompleted(true);
            }
        }
        event.consume();
    }

    @FXML
    public void onInventoryItemDragDetected(MouseEvent event) {
        if (!GameState.getInstance().getInventory().isEmpty()) {
            Item item = GameState.getInstance().getInventory().get(0);
            Dragboard db = inventoryItemIcon.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(item.getId()); // Přenášíme ID předmětu (např. "zebrik")
            db.setContent(content);

            // Zmenšení obřího obrázku pro tažení
            ImageView dragIcon = new ImageView(inventoryItemIcon.getImage());
            dragIcon.setFitWidth(100);
            dragIcon.setFitHeight(100);
            dragIcon.setPreserveRatio(true);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            Image dragImage = dragIcon.snapshot(params, null);

            db.setDragView(dragImage, 50, 50); // Vycentrování na myš

            event.consume();
        }
    }

    public void showDialogue(String text) {
        dialogueLabel.setText(text);
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> dialogueLabel.setText(""));
        delay.play();
    }

    public void updateInventoryUI() {
        // Pokud má hráč předmět, bublina se ukáže včetně něj
        if (!GameState.getInstance().getInventory().isEmpty()) {
            Item item = GameState.getInstance().getInventory().get(0); // Máme jen 1 slot
            inventoryBubble.setPrefWidth(240); // Roztáhneme bublinu (HBox se postará)
            try {
                inventoryItemIcon.setImage(new Image(getClass().getResourceAsStream("/" + item.getImagePath())));
                inventoryItemIcon.setVisible(true);
            } catch (Exception e) {
                System.err.println("Chybí ikona předmětu: " + item.getImagePath());
            }
        } else {
            inventoryBubble.setPrefWidth(130);
            inventoryItemIcon.setVisible(false);
        }

        updateHeroSprite();
    }

    public void updateHeroSprite() {
        // Získáme aktuální kontroler a pokud má metodu updateHero, zavoláme ji
        // Pro zjednodušení můžeme nechat logiku na kontrolerech samotných scén,
        // nebo to zkusit najít pomocí lookup("#heroSprite").
        Node heroNode = scenePane.lookup("#heroSprite");
        if (heroNode instanceof ImageView) {
            ImageView hero = (ImageView) heroNode;
            boolean holdsZebrik = false;
            if (!GameState.getInstance().getInventory().isEmpty()) {
                Item item = GameState.getInstance().getInventory().get(0);
                if (item.getId().equals("zebrik")) {
                    holdsZebrik = true;
                }
            }

            if (holdsZebrik) {
                // Hrdina s žebříkem
                hero.setImage(
                        new Image(getClass().getResourceAsStream("/cz/stojkar/whugo/images/hrdina_stoji_zebrik.png")));
            } else {
                // Hrdina bez předmětu (nebo s jiným předmětem, např. s klíčem)
                hero.setImage(new Image(getClass().getResourceAsStream("/cz/stojkar/whugo/images/hrdina_stoji.png")));
            }
        }
    }
}
