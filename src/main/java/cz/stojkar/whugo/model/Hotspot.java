package cz.stojkar.whugo.model;

import javafx.scene.shape.Polygon;

public class Hotspot {
    private Polygon shape;
    private ActionType actionType;
    private String targetId; // Location ID or Item ID
    private String lookText; // Text to display for LOOK_AT

    public Hotspot(Polygon shape, ActionType actionType, String targetId) {
        this.shape = shape;
        this.actionType = actionType;
        this.targetId = targetId;
    }

    public Hotspot setLookText(String text) {
        this.lookText = text;
        return this;
    }

    public Polygon getShape() { return shape; }
    public ActionType getActionType() { return actionType; }
    public String getTargetId() { return targetId; }
    public String getLookText() { return lookText; }
}
