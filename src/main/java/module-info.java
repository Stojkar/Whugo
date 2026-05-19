module cz.stojkar.whugo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.net.http;
    requires org.json;

    opens cz.stojkar.whugo to javafx.fxml;
    opens cz.stojkar.whugo.controllers to javafx.fxml;
    exports cz.stojkar.whugo;
    exports cz.stojkar.whugo.controllers;
}