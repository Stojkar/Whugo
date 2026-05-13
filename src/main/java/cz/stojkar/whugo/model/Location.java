package cz.stojkar.whugo.model;

import java.util.ArrayList;
import java.util.List;

public class Location {
    private String id;
    private String imagePath;
    private List<Hotspot> hotspots;

    public Location(String id, String imagePath) {
        this.id = id;
        this.imagePath = imagePath;
        this.hotspots = new ArrayList<>();
    }

    public void addHotspot(Hotspot hotspot) {
        this.hotspots.add(hotspot);
    }
    
    public void removeHotspot(Hotspot hotspot) {
        this.hotspots.remove(hotspot);
    }

    public String getId() { return id; }
    public String getImagePath() { return imagePath; }
    public List<Hotspot> getHotspots() { return hotspots; }
}
