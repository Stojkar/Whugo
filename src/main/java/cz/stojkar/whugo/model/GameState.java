package cz.stojkar.whugo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    private static GameState instance;
    private String currentLocationId;
    private List<Item> inventory;
    private Map<String, List<Item>> droppedItems;
    
    // Globální příznaky (např. zebrik_na_dome = true)
    private Map<String, Boolean> flags;

    private GameState() {
        this.inventory = new ArrayList<>();
        this.droppedItems = new HashMap<>();
        this.flags = new HashMap<>();
        
        // Výchozí předměty ležící v lokacích
        Item zebrik = new Item("zebrik", "Žebřík", "cz/stojkar/whugo/images/zebrik.png");
        dropItemInLocation(zebrik, "tvuj_dum");

        Item klic = new Item("klic", "Klíč", "cz/stojkar/whugo/images/klic.png");
        dropItemInLocation(klic, "hospoda");
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    public String getCurrentLocationId() {
        return currentLocationId;
    }

    public void setCurrentLocationId(String currentLocationId) {
        this.currentLocationId = currentLocationId;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void addItem(Item item) {
        this.inventory.clear(); // Hra podporuje pouze 1 aktivní předmět v ruce
        this.inventory.add(item);
    }

    public void removeItem(Item item) {
        this.inventory.remove(item);
    }

    public void dropItemInLocation(Item item, String locationId) {
        droppedItems.computeIfAbsent(locationId, k -> new ArrayList<>()).add(item);
    }

    public List<Item> getDroppedItems(String locationId) {
        return droppedItems.getOrDefault(locationId, new ArrayList<>());
    }

    public void removeDroppedItem(Item item, String locationId) {
        if (droppedItems.containsKey(locationId)) {
            droppedItems.get(locationId).remove(item);
        }
    }

    public void setFlag(String key, boolean value) {
        flags.put(key, value);
    }

    public boolean getFlag(String key) {
        return flags.getOrDefault(key, false);
    }
}
