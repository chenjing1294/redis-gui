package cedis;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FavoriteConnection {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 40;
    private static final String STORAGE = "cedis.db";
    private TextField name;
    private TextField host;
    private TextField port;
    private TextField pwd;
    private VBox vBox;
    private Set<Connection> connections = new LinkedHashSet<>();
    private TabCreator tabCreator;

    public void setName(TextField name) {
        this.name = name;
    }

    public void setHost(TextField host) {
        this.host = host;
    }

    public void setPort(TextField port) {
        this.port = port;
    }

    public void setPwd(TextField pwd) {
        this.pwd = pwd;
    }

    public void setvBox(VBox vBox) {
        this.vBox = vBox;
    }

    public void setTabCreator(TabCreator tabCreator) {
        this.tabCreator = tabCreator;
    }

    /**
     * 获取我收藏的连接
     */
    public List<JFXButton> getFavorites() {
        Path path = Paths.get(System.getProperty("user.home") + "/" + STORAGE);
        File file;
        try {
            if (!Files.exists(path)) {
                file = Files.createFile(path).toFile();
            } else {
                file = new File(System.getProperty("user.home") + "/" + STORAGE);
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                connections = (Set<Connection>) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        List<JFXButton> buttons = new ArrayList<>();
        for (Connection c : connections) {
            JFXButton b = new JFXButton(c.getName());
            b.setUserData(c);
            b.setMinWidth(BUTTON_WIDTH);
            b.setPrefWidth(BUTTON_WIDTH);
            b.setMaxWidth(BUTTON_WIDTH);
            b.setPrefHeight(BUTTON_HEIGHT);
            b.setAlignment(Pos.BASELINE_LEFT);
            b.setGraphicTextGap(10.0);
            b.setFont(Font.font("System", 13));
            b.getStyleClass().add("btns");
            MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.DATABASE);
            icon.setSize("15");
            b.setGraphic(icon);
            b.setOnMouseClicked(event -> {
                JFXButton button = (JFXButton) event.getSource();
                Connection connection = (Connection) button.getUserData();
                if (event.getClickCount() == 1) {
                    name.setText(connection.getName());
                    host.setText(connection.getHost());
                    port.setText(String.valueOf(connection.getPort()));
                    pwd.setText(connection.getPassword());
                }
                if (event.getClickCount() == 2) {
                    Tab viewTab = tabCreator.createViewTab(connection);
                    tabCreator.addViewTab(viewTab);
                }
            });
            buttons.add(b);
        }
        return buttons;
    }

    public void addToFavorites(Connection connection) {
        if (connection.getPassword() == null || connection.getPassword().length() == 0)
            connection.setPassword(null);
        if (connections.contains(connection))
            return;
        connections.add(connection);
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.home") + "/" + STORAGE));
            out.writeObject(connections);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (vBox != null) {
            vBox.getChildren().remove(1, vBox.getChildren().size());
            vBox.getChildren().addAll(getFavorites());
        }
    }

    public void remove() {
        String name = this.name.getText();
        for (Connection c : connections) {
            if (c.getName().equals(name)) {
                connections.remove(c);
                break;
            }
        }

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.home") + "/" + STORAGE));
            out.writeObject(connections);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (vBox != null) {
            vBox.getChildren().remove(1, vBox.getChildren().size());
            vBox.getChildren().addAll(getFavorites());
        }
    }

    public static class Connection implements Serializable {
        private String name;
        private String host;
        private int port;
        private String password;

        public Connection(String name, String host, int port, String password) {
            this.name = name;
            this.host = host;
            this.port = port;
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return "Connection{" +
                    "name='" + name + '\'' +
                    ", host='" + host + '\'' +
                    ", port=" + port +
                    ", password='" + password + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Connection that = (Connection) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

}
