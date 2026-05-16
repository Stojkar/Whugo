package cz.stojkar.whugo;

import javafx.scene.media.AudioClip;

/**
 * Správce zvukových efektů hry.
 * Singleton – získej instanci přes SoundManager.getInstance().
 */
public class SoundManager {

    private static SoundManager instance;

    private final AudioClip arrowSound;
    private final AudioClip pickupSound;
    private final AudioClip ladderSound;
    private final AudioClip krknutiSound;
    private final AudioClip mluveniSound;

    private SoundManager() {
        arrowSound   = loadClip("/cz/stojkar/whugo/sounds/sipka.mp3");
        pickupSound  = loadClip("/cz/stojkar/whugo/sounds/sebrat.mp3");
        ladderSound  = loadClip("/cz/stojkar/whugo/sounds/zebrik.mp3");
        krknutiSound = loadClip("/cz/stojkar/whugo/sounds/krknuti.mp3");
        mluveniSound = loadClip("/cz/stojkar/whugo/sounds/mluveni.mp3");
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /** Zvuk při kliknutí na navigační šipku */
    public void playArrow() {
        play(arrowSound);
    }

    /** Zvuk při zvednutí předmětu ze země */
    public void playPickup() {
        play(pickupSound);
    }

    /** Zvuk při umístění žebříku k domu */
    public void playLadder() {
        play(ladderSound);
    }

    /** Zvuk krknuti – NPC po 5 sekundách v hospodě */
    public void playKrknuti() {
        play(krknutiSound);
    }

    /** Zvuk mluvení – NPC po kliknutí hráčem */
    public void playMluveni() {
        play(mluveniSound);
    }

    // ── Privátní helpers ──────────────────────────────────────────────────────

    private AudioClip loadClip(String resourcePath) {
        try {
            java.net.URL url = getClass().getResource(resourcePath);
            if (url == null) {
                System.err.println("Zvukový soubor nenalezen: " + resourcePath);
                return null;
            }
            return new AudioClip(url.toExternalForm());
        } catch (Exception e) {
            System.err.println("Nelze načíst zvuk: " + resourcePath);
            return null;
        }
    }

    private void play(AudioClip clip) {
        if (clip != null) {
            clip.play();
        }
    }
}
