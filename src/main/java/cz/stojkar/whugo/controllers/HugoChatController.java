package cz.stojkar.whugo.controllers;

import cz.stojkar.whugo.GameController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HugoChatController {

    @FXML private ScrollPane chatScrollPane;
    @FXML private VBox chatBox;
    @FXML private TextField messageInput;
    @FXML private Button sendButton;
    @FXML private ImageView arrowLeft;

    private String selectedModel = "llama3"; // Výchozí fallback model
    private final List<Message> conversationHistory = new ArrayList<>();
    private HBox thinkingBubble;

    // Reprezentace zprávy v historii chatu
    public static class Message {
        private final String role;
        private final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public String getContent() { return content; }
    }

    @FXML
    public void initialize() {
        // Nastavení systémového promptu pro definici osobnosti Huga
        conversationHistory.add(new Message("system", 
            "Jsi Hugo, mrzutý, starší, ale moudrý muž sedící na lavičce v útulné vesničce v kreslené 2D adventuře. " +
            "Mluv výhradně česky. Odpovídej stručně (maximálně 1 až 3 krátké věty). " +
            "Hráč si k tobě přisedl na lavičku a povídá si s tebou. Buď trochu tajemný, občas si mírně postěžuj na dnešní mládež, " +
            "která jen kouká do mobilů, nebo na to, jak tě bolí záda. Působ jako postava ze staré adventury."
        ));

        // Počáteční replika od Huga v rozhraní a v historii
        String uvodniReplika = "No tak si přisedni, mladíku. Co mi chceš vyprávět? Dnešní mládež stejně neumí nic než zírat do těch svítících krabiček... Ale když už jsi tu, povídej.";
        conversationHistory.add(new Message("assistant", uvodniReplika));
        addMessageToChat("assistant", "Hugo: " + uvodniReplika);

        // Pokus o automatickou detekci běžících modelů na Ollamě
        detectOllamaModel();
    }

    /**
     * Zkusí zjistit dostupné modely v Ollamě a vybrat ten první běžící.
     */
    private void detectOllamaModel() {
        CompletableFuture.runAsync(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:11434/api/tags"))
                        .GET()
                        .timeout(Duration.ofSeconds(3))
                        .build();
                
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JSONObject json = new JSONObject(response.body());
                    JSONArray models = json.optJSONArray("models");
                    if (models != null && !models.isEmpty()) {
                        String firstModel = models.getJSONObject(0).getString("name");
                        selectedModel = firstModel;
                        System.out.println("Detekován Ollama model: " + selectedModel);
                    }
                }
            } catch (Exception e) {
                System.out.println("Nepodařilo se detekovat Ollama modely, použije se výchozí: " + selectedModel + ". Chyba: " + e.getMessage());
            }
        });
    }

    /**
     * Zavolá se při odeslání zprávy (kliknutí na tlačítko nebo stisknutí Enter).
     */
    @FXML
    public void onSendPressed() {
        String text = messageInput.getText().trim();
        if (text.isEmpty()) {
            return;
        }

        // Vyčištění vstupu a zablokování prvků během přemýšlení
        messageInput.clear();
        messageInput.setDisable(true);
        sendButton.setDisable(true);

        // Zobrazení zprávy hráče na pravé straně
        addMessageToChat("user", "Já: " + text);
        conversationHistory.add(new Message("user", text));

        // Zobrazení indikátoru přemýšlení
        showThinking();

        // Asynchronní volání API Ollamy
        CompletableFuture.runAsync(() -> {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("model", selectedModel);
                requestBody.put("stream", false);

                JSONArray messagesArray = new JSONArray();
                for (Message msg : conversationHistory) {
                    JSONObject msgObj = new JSONObject();
                    msgObj.put("role", msg.getRole());
                    msgObj.put("content", msg.getContent());
                    messagesArray.put(msgObj);
                }
                requestBody.put("messages", messagesArray);

                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:11434/api/chat"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), java.nio.charset.StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    JSONObject responseJson = new JSONObject(response.body());
                    JSONObject messageObj = responseJson.getJSONObject("message");
                    String reply = messageObj.getString("content").trim();

                    // Uložit do historie
                    conversationHistory.add(new Message("assistant", reply));

                    Platform.runLater(() -> {
                        hideThinking();
                        addMessageToChat("assistant", "Hugo: " + reply);
                        enableInput();

                        // Pokud odpověď obsahuje "(výhra)" nebo "(vyhra)", přejdeme do kumbálu po malé prodlevě
                        String replyLower = reply.toLowerCase();
                        if (replyLower.contains("(výhra)") || replyLower.contains("(vyhra)")) {
                            javafx.animation.PauseTransition transitionDelay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2.5));
                            transitionDelay.setOnFinished(ev -> {
                                GameController.getInstance().loadLocation("kumbal");
                            });
                            transitionDelay.play();
                        }
                    });
                } else {
                    throw new Exception("Chyba API. Kód odpovědi: " + response.statusCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    hideThinking();
                    addMessageToChat("assistant", "Systém: *Nepodařilo se spojit s Ollamou. Ujistěte se, že Ollama běží na http://localhost:11434 a máte nainstalovaný a spuštěný libovolný LLM model.*");
                    enableInput();
                });
            }
        });
    }

    private void enableInput() {
        messageInput.setDisable(false);
        sendButton.setDisable(false);
        messageInput.requestFocus();
    }

    private void addMessageToChat(String role, String text) {
        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setMaxWidth(650.0);
        bubble.setFont(javafx.scene.text.Font.font("Arial", 14));

        if (role.equals("user")) {
            // Modrá bublina hráče
            bubble.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 10 15 10 15;");
        } else {
            // Tmavě šedá bublina Huga / Systému
            bubble.setStyle("-fx-background-color: #374151; -fx-text-fill: #f3f4f6; -fx-background-radius: 15; -fx-padding: 10 15 10 15;");
        }

        HBox container = new HBox(bubble);
        if (role.equals("user")) {
            container.setAlignment(Pos.CENTER_RIGHT);
            container.setPadding(new Insets(0, 10, 0, 50));
        } else {
            container.setAlignment(Pos.CENTER_LEFT);
            container.setPadding(new Insets(0, 50, 0, 10));
        }

        chatBox.getChildren().add(container);
        scrollChatToBottom();
    }

    private void showThinking() {
        Label bubble = new Label("Hugo přemýšlí...");
        bubble.setFont(javafx.scene.text.Font.font("Arial", 14));
        bubble.setStyle("-fx-background-color: #374151; -fx-text-fill: #9ca3af; -fx-background-radius: 15; -fx-padding: 10 15 10 15; -fx-font-style: italic;");

        thinkingBubble = new HBox(bubble);
        thinkingBubble.setAlignment(Pos.CENTER_LEFT);
        thinkingBubble.setPadding(new Insets(0, 50, 0, 10));

        chatBox.getChildren().add(thinkingBubble);
        scrollChatToBottom();
    }

    private void hideThinking() {
        if (thinkingBubble != null) {
            chatBox.getChildren().remove(thinkingBubble);
            thinkingBubble = null;
        }
    }

    private void scrollChatToBottom() {
        Platform.runLater(() -> {
            chatScrollPane.layout();
            chatScrollPane.setVvalue(1.0);
        });
    }

    // ── Šipka zpět a hover efekty ─────────────────────────────────────────────

    @FXML
    public void goToHugoDum() {
        GameController.getInstance().loadLocation("hugo_dum");
    }

    @FXML
    public void onMouseEntered(MouseEvent event) {
        ((ImageView) event.getSource()).setCursor(Cursor.HAND);
    }

    @FXML
    public void onMouseExited(MouseEvent event) {
        ((ImageView) event.getSource()).setCursor(Cursor.DEFAULT);
    }
}
