module cz.stojkar.whugo {
    requires javafx.controls;
    requires javafx.fxml;


    opens cz.stojkar.whugo to javafx.fxml;
    exports cz.stojkar.whugo;
}