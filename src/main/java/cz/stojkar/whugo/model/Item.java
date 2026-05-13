package cz.stojkar.whugo.model;

public class Item {
    private String id;
    private String name;
    private String imagePath;

    public Item(String id, String name, String imagePath) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImagePath() { return imagePath; }
}
