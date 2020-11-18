package cedis;

import cedis.util.A;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.weathericons.WeatherIcon;
import de.jensd.fx.glyphs.weathericons.WeatherIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TabCreator {
    public static final String NEW_CONNECTION_NAME = "Quick Connect";
    public static final String MORE_SCAN = "__SCAN_MORE_9411126374";
    public static final Image APP_IMAGE = new Image(TabCreator.class.getClass().getResourceAsStream("/icons/icons8_redis_48.png"));
    private final FavoriteConnection favoriteConnection;
    private final RedisOperator redisOperator;
    private TabPane tabPane;

    public TabCreator(FavoriteConnection favoriteConnection,
                      RedisOperator redisOperator) {
        this.favoriteConnection = favoriteConnection;
        this.favoriteConnection.setTabCreator(this);
        this.redisOperator = redisOperator;
    }

    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    /**
     * 创建连接Redis的tab页
     */
    public Tab createConnectionTab() {
        Tab tab = new Tab(NEW_CONNECTION_NAME);
        AnchorPane root = new AnchorPane();

        SplitPane splitPane = new SplitPane();
        AnchorPane.setTopAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        root.getChildren().addAll(splitPane);

        AnchorPane leftAnchor = new AnchorPane();
        AnchorPane rightAnchor = new AnchorPane();
        rightAnchor.getStyleClass().add("quick-connect-right-anchor");
        leftAnchor.setPrefWidth(200);
        leftAnchor.setMinWidth(200);
        leftAnchor.setMaxWidth(200);
        splitPane.getItems().addAll(leftAnchor, rightAnchor);

        VBox vBox = new VBox(0);
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);
        AnchorPane.setLeftAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
        leftAnchor.getChildren().addAll(vBox);

        TextField nameField = new TextField();
        TextField hostField = new TextField();
        TextField portField = new TextField();
        TextField passwordField = new TextField();
        favoriteConnection.setName(nameField);
        favoriteConnection.setHost(hostField);
        favoriteConnection.setPort(portField);
        favoriteConnection.setPwd(passwordField);
        favoriteConnection.setvBox(vBox);

        Label logLabel = new Label(Layout.APP_NAME, new ImageView(APP_IMAGE));
        logLabel.setPrefWidth(200);
        logLabel.setPrefHeight(50);
        logLabel.setFont(Font.font("System", FontWeight.BOLD, 17));
        logLabel.setGraphicTextGap(4);
        logLabel.setEffect(new Bloom());
        logLabel.setPadding(new Insets(0, 0, 0, 10));
        List<JFXButton> buttons = favoriteConnection.getFavorites();
        vBox.getChildren().addAll(logLabel);
        vBox.getChildren().addAll(buttons);


        AnchorPane rightSubAnchor = new AnchorPane();
        rightSubAnchor.getStyleClass().add("quick-connect-right-anchor");
        rightSubAnchor.setPrefWidth(322);
        rightSubAnchor.setPrefHeight(210);
        AnchorPane.setTopAnchor(rightSubAnchor, 167.0);
        AnchorPane.setLeftAnchor(rightSubAnchor, 188.0);
        WeatherIconView iconView = new WeatherIconView(WeatherIcon.LIGHTNING);
        iconView.setSize("20");
        Label quickConnectLabel = new Label("QUICK CONNECT", iconView);
        quickConnectLabel.setFont(Font.font("System", 16));
        AnchorPane.setTopAnchor(quickConnectLabel, 20.0);
        AnchorPane.setLeftAnchor(quickConnectLabel, 20.0);
        rightAnchor.getChildren().addAll(rightSubAnchor, quickConnectLabel);

        Label name = new Label("Name:");
        name.setFont(Font.font("System", FontWeight.BOLD, 18));
        AnchorPane.setTopAnchor(name, 2.0);
        AnchorPane.setLeftAnchor(name, 0.0);
        Label host = new Label("Host:");
        host.setFont(Font.font("System", FontWeight.BOLD, 18));
        AnchorPane.setTopAnchor(host, 43.0);
        AnchorPane.setLeftAnchor(host, 0.0);
        Label port = new Label("Port:");
        port.setFont(Font.font("System", FontWeight.BOLD, 18));
        AnchorPane.setTopAnchor(port, 84.0);
        AnchorPane.setLeftAnchor(port, 0.0);
        Label password = new Label("Password:");
        password.setFont(Font.font("System", FontWeight.BOLD, 18));
        AnchorPane.setTopAnchor(password, 121.0);
        AnchorPane.setLeftAnchor(password, 0.0);

        nameField.getStyleClass().add("quick-connect-right-field");
        nameField.setPrefWidth(163);
        AnchorPane.setTopAnchor(nameField, 0.0);
        AnchorPane.setRightAnchor(nameField, 0.0);
        hostField.getStyleClass().add("quick-connect-right-field");
        hostField.setPrefWidth(163);
        AnchorPane.setTopAnchor(hostField, 42.0);
        AnchorPane.setRightAnchor(hostField, 0.0);
        portField.getStyleClass().add("quick-connect-right-field");
        portField.setPrefWidth(163);
        AnchorPane.setTopAnchor(portField, 83.0);
        AnchorPane.setRightAnchor(portField, 0.0);
        passwordField.getStyleClass().add("quick-connect-right-field");
        passwordField.setPrefWidth(163);
        AnchorPane.setTopAnchor(passwordField, 120.0);
        AnchorPane.setRightAnchor(passwordField, 0.0);

        JFXButton addButton = new JFXButton("Add to Favorites");
        addButton.setFont(Font.font("System", 15));
        addButton.getStyleClass().add("quick-connect-button");
        AnchorPane.setBottomAnchor(addButton, 0.0);
        AnchorPane.setLeftAnchor(addButton, 91.0);
        addButton.setOnMouseClicked(event -> {
            try {
                FavoriteConnection.Connection connection = getConnection(nameField, hostField, portField, passwordField);
                favoriteConnection.addToFavorites(connection);
            } catch (NumberFormatException e) {
                portField.setText("请使用格式正确的端口号");
            }
        });

        JFXButton connectButton = new JFXButton("Connect");
        connectButton.setFont(Font.font("System", 15));
        connectButton.getStyleClass().add("quick-connect-button");
        AnchorPane.setBottomAnchor(connectButton, 0.0);
        AnchorPane.setRightAnchor(connectButton, 0.0);
        connectButton.setOnMouseClicked(event -> {
            try {
                FavoriteConnection.Connection connection = getConnection(nameField, hostField, portField, passwordField);
                Tab viewTab = this.createViewTab(connection);
                this.addViewTab(viewTab);
            } catch (NumberFormatException e) {
                portField.setText("请使用格式正确的端口号");
            }
        });

        JFXButton removeButton = new JFXButton("Remove");
        removeButton.setFont(Font.font("System", 15));
        removeButton.getStyleClass().add("quick-connect-button");
        AnchorPane.setBottomAnchor(removeButton, 0.0);
        AnchorPane.setLeftAnchor(removeButton, 0.0);
        removeButton.setOnMouseClicked(event -> {
            favoriteConnection.remove();
        });

        rightSubAnchor.getChildren().addAll(name, host, port, password, nameField, hostField, portField, passwordField,
                connectButton, addButton, removeButton);
        tab.setContent(root);
        tab.setOnClosed(event -> {
            Tab t = (Tab) event.getSource();
            tabPane.getTabs().remove(t);
        });
        return tab;
    }

    /**
     * 创建浏览redis内容的tab页
     */
    public Tab createViewTab(FavoriteConnection.Connection connection) {
        ObservableList<Tab> tabs = tabPane.getTabs();
        for (Tab t : tabs) {
            if (t.getText().equals(connection.getName()) && t.getUserData() != null) {
                return t;
            }
        }
        try {
            Tab tab = new Tab(connection.getName());
            tab.setUserData(connection);

            AnchorPane root = new AnchorPane();
            Label resLabel = new Label("Have a good day...");
            tab.setContent(root);
            TextField searchField = new TextField();
            searchField.setPromptText("Search here.....");
            AnchorPane.setTopAnchor(searchField, 0.0);
            AnchorPane.setLeftAnchor(searchField, 0.0);
            AnchorPane.setRightAnchor(searchField, 0.0);
            searchField.setPrefHeight(30);
            searchField.getStyleClass().add("search-field");

            SplitPane splitPane = new SplitPane();
            AnchorPane.setTopAnchor(splitPane, 30.0);
            AnchorPane.setBottomAnchor(splitPane, 30.0);
            AnchorPane.setLeftAnchor(splitPane, 0.0);
            AnchorPane.setRightAnchor(splitPane, 0.0);

            HBox hBox = new HBox(10);
            hBox.setPadding(new Insets(0, 10, 0, 10));
            hBox.setAlignment(Pos.CENTER_LEFT);
            AnchorPane.setLeftAnchor(hBox, 0.0);
            AnchorPane.setRightAnchor(hBox, 0.0);
            AnchorPane.setBottomAnchor(hBox, 0.0);
            hBox.setPrefHeight(30);

            root.getChildren().addAll(searchField, splitPane, hBox);

            AnchorPane keyAnchor = new AnchorPane();
            AnchorPane fieldAnchor = new AnchorPane();
            AnchorPane textAreaAnchor = new AnchorPane();
            splitPane.getItems().addAll(keyAnchor, fieldAnchor, textAreaAnchor);
            keyAnchor.setMinWidth(200);
            fieldAnchor.setMinWidth(200);
            textAreaAnchor.setMinWidth(450);

            ListView<Key> keyListView = new ListView<>();
            AnchorPane.setTopAnchor(keyListView, 0.0);
            AnchorPane.setBottomAnchor(keyListView, 0.0);
            AnchorPane.setLeftAnchor(keyListView, 0.0);
            AnchorPane.setRightAnchor(keyListView, 0.0);
            keyAnchor.getChildren().add(keyListView);

            ListView<String> fieldListView = new ListView<>();
            AnchorPane.setTopAnchor(fieldListView, 0.0);
            AnchorPane.setBottomAnchor(fieldListView, 0.0);
            AnchorPane.setLeftAnchor(fieldListView, 0.0);
            AnchorPane.setRightAnchor(fieldListView, 0.0);
            fieldAnchor.getChildren().add(fieldListView);

            VBox vBox = new VBox();
            AnchorPane.setTopAnchor(vBox, 0.0);
            AnchorPane.setBottomAnchor(vBox, 0.0);
            AnchorPane.setLeftAnchor(vBox, 0.0);
            AnchorPane.setRightAnchor(vBox, 0.0);

            textAreaAnchor.getChildren().add(vBox);

            JFXTextArea textArea = new JFXTextArea();
            textArea.setFont(Font.font("Courier New"));
            VBox.setVgrow(textArea, Priority.ALWAYS);

            HBox textAreaHBox = new HBox(10);
            textAreaHBox.setPrefHeight(30);
            textAreaHBox.setAlignment(Pos.CENTER_RIGHT);
            vBox.getChildren().addAll(textArea, textAreaHBox);

            JFXButton json = new JFXButton("Json");
            JFXButton save = new JFXButton("Save");
            json.getStyleClass().add("quick-connect-button");
            save.getStyleClass().add("quick-connect-button");
            textAreaHBox.getChildren().addAll(json, save);

            Label databaseLabel = new Label("DATABASE");
            databaseLabel.setPrefHeight(30);
            FontAwesomeIconView icon1 = new FontAwesomeIconView(FontAwesomeIcon.DATABASE);
            icon1.setSize("15");
            databaseLabel.setGraphic(icon1);

            ComboBox<Integer> databaseComboBox = new JFXComboBox<>();

            Label keysLabel = new Label("Keys: " + redisOperator.getDbSize(0, connection));
            keysLabel.setPrefHeight(30);
            FontAwesomeIconView icon2 = new FontAwesomeIconView(FontAwesomeIcon.KEY);
            icon2.setSize("15");
            keysLabel.setGraphic(icon2);

            Label membersLabel = new Label("Members: 15");
            membersLabel.setPrefHeight(30);
            FontAwesomeIconView icon3 = new FontAwesomeIconView(FontAwesomeIcon.SHOPPING_BASKET);
            icon3.setSize("15");
            membersLabel.setGraphic(icon3);

            membersLabel.setPrefHeight(30);
            FontAwesomeIconView icon4 = new FontAwesomeIconView(FontAwesomeIcon.COMMENTING);
            icon4.setSize("15");
            resLabel.setGraphic(icon4);


            hBox.getChildren().addAll(databaseLabel, databaseComboBox, keysLabel, membersLabel, resLabel);


            keyListView.setCellFactory(new KeyCellFactory(connection, redisOperator, keyListView, databaseComboBox, searchField, resLabel, textArea, fieldListView));
            fieldListView.setCellFactory(new FieldCellFactory(connection, redisOperator, keyListView, databaseComboBox, searchField, resLabel, textArea, fieldListView));
            fieldListView.setEditable(true);
            keyListView.setEditable(true);
            keyListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (newValue.getType() == Key.Type.STRING) {
                        Integer db = databaseComboBox.getSelectionModel().getSelectedItem();
                        String s = redisOperator.get(connection, db, newValue);
                        fieldListView.getItems().clear();
                        textArea.setText(s);
                    } else {
                        List<String> dbSubContent = redisOperator.getDbSubContent(databaseComboBox.getSelectionModel().getSelectedItem(), connection, newValue);
                        fieldListView.getItems().clear();
                        fieldListView.getItems().addAll(dbSubContent);
                        membersLabel.setText("Members: " + dbSubContent.size());
                        textArea.setText("");
                    }
                }
            });

            List<Key> keys = redisOperator.getDbContent(0, connection, false);
            keys.add(new Key(MORE_SCAN, Key.Type.UNKNOWN));
            keyListView.setItems(FXCollections.observableArrayList(keys));

            fieldListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Integer db = databaseComboBox.getSelectionModel().getSelectedItem();
                    Key key = keyListView.getSelectionModel().getSelectedItem();
                    if (key != null) {
                        String field = fieldListView.getSelectionModel().getSelectedItem();
                        if (field != null) {
                            switch (key.getType()) {
                                case HASH:
                                    String value = redisOperator.getValue(connection, db, key, field);
                                    textArea.setText(value);
                                    break;
                                case LIST:
                                case ZSET:
                                case SET:
                                    textArea.setText(field);
                                    break;
                            }
                        }
                    }
                }
            });

            fieldListView.setItems(FXCollections.observableArrayList());

            List<Integer> dbs = new ArrayList<>();
            int dbCount = redisOperator.getDbCount(connection);
            for (int i = 0; i < dbCount; i++) {
                dbs.add(i);
            }
            ObservableList<Integer> dbObservableList = FXCollections.observableArrayList(dbs);
            databaseComboBox.setItems(dbObservableList);
            databaseComboBox.setValue(dbObservableList.get(0));
            databaseComboBox.setOnAction(event -> {
                Integer db = databaseComboBox.getSelectionModel().getSelectedItem();
                long dbSize = redisOperator.getDbSize(db, connection);
                keysLabel.setText("Keys: " + dbSize);
                List<Key> ks = redisOperator.getDbContent(db, connection, true);
                ks.add(new Key(MORE_SCAN, Key.Type.UNKNOWN));
                keyListView.getItems().clear();
                keyListView.getItems().addAll(ks);
                fieldListView.getItems().clear();
            });

            json.setOnAction(event -> {
                String text = textArea.getText();
                if (!text.isEmpty()) {
                    try {
                        textArea.setText(A.prettyJSON(text));
                        resLabel.setText("Pretty good");
                        resLabel.setTextFill(Paint.valueOf("#000000"));
                    } catch (JsonProcessingException e) {
                        resLabel.setText("JSON format error");
                        resLabel.setTextFill(Paint.valueOf("#ff0000"));
                    }
                }
            });

            save.setOnAction(event -> {
                Key key = keyListView.getSelectionModel().getSelectedItem();
                String field = fieldListView.getSelectionModel().getSelectedItem();
                if (key != null && (field != null || key.getType().equals(Key.Type.STRING))) {
                    String value = textArea.getText();
                    int db = databaseComboBox.getSelectionModel().getSelectedItem();
                    switch (key.getType()) {
                        case HASH:
                            redisOperator.save(connection, db, key, field, value);
                            break;
                        case LIST:
                            Integer index = fieldListView.getItems().indexOf(field);
                            redisOperator.save(connection, db, key, index.toString(), value);
                            break;
                        case SET:
                            redisOperator.save(connection, db, key, field, value);
                            break;
                        case ZSET:
                            redisOperator.save(connection, db, key, field, value);
                            break;
                        case STRING:
                            redisOperator.save(connection, db, key, value);
                            break;
                    }
                    resLabel.setText("Save successfully");
                    resLabel.setTextFill(Paint.valueOf("#000000"));
                }
            });

            searchField.setOnKeyPressed(event -> {
                if (event.getCode().getName().equals(KeyCode.ENTER.getName())) {
                    String pattern = searchField.getText();
                    ObservableList<Key> items = keyListView.getItems();
                    Integer db = databaseComboBox.getSelectionModel().getSelectedItem();
                    if (!pattern.isEmpty()) {
                        if (pattern.startsWith(">")) {
                            Key key = keyListView.getSelectionModel().getSelectedItem();
                            if (key == null) {
                                fieldListView.getItems().clear();
                                return;
                            }
                            List<String> fields = redisOperator.getDbSubContent(db, connection, key);
                            List<String> result = fields.stream().filter(s -> {
                                Pattern compile = Pattern.compile(pattern.substring(1));
                                return compile.matcher(s).matches();
                            }).collect(Collectors.toList());
                            fieldListView.getItems().clear();
                            fieldListView.getItems().addAll(result);
                        } else {
                            fieldListView.getSelectionModel().clearSelection();
                            fieldListView.getItems().clear();
                            keyListView.getSelectionModel().clearSelection();
                            items.clear();
                            textArea.setText("");
                            List<Key> ks = redisOperator.search(pattern, connection, db);
                            items.addAll(ks);
                        }
                    } else {
                        keyListView.getSelectionModel().clearSelection();
                        items.clear();
                        fieldListView.getSelectionModel().clearSelection();
                        fieldListView.getItems().clear();
                        textArea.setText("");
                        List<Key> ks = redisOperator.getDbContent(db, connection, true);
                        ks.add(new Key(MORE_SCAN, Key.Type.UNKNOWN));
                        items.addAll(ks);
                        resLabel.setText("Flush successfully");
                    }
                }
            });

            tab.setOnClosed(event -> {
                Tab t = (Tab) event.getSource();
                Object userData = t.getUserData();
                if (userData != null) {
                    FavoriteConnection.Connection c = (FavoriteConnection.Connection) userData;
                    redisOperator.getCursorMap().remove(c);
                    JedisPool jedisPool = redisOperator.getJedisPoolMap().get(c);
                    jedisPool.close();
                    redisOperator.getJedisPoolMap().remove(c);
                }
                tabPane.getTabs().remove(t);
            });
            return tab;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Tab tab = new Tab(connection.getName());
            AnchorPane root = new AnchorPane();
            tab.setContent(root);

            Text text = new Text();
            text.setWrappingWidth(300);
            text.setText("Redis connection failed.");
            text.setFont(Font.font("System", 17));
            root.getChildren().addAll(text);
            AnchorPane.setTopAnchor(text, 10.0);
            AnchorPane.setLeftAnchor(text, 0.0);
            AnchorPane.setRightAnchor(text, 0.0);
            AnchorPane.setBottomAnchor(text, 0.0);
            return tab;
        }
    }

    public void addViewTab(Tab viewTab) {
        ObservableList<Tab> tabs = tabPane.getTabs();
        for (Tab t : tabs) {
            if (t.getText().equals(NEW_CONNECTION_NAME)) {
                tabs.remove(t);
                break;
            }
        }
        if (!tabs.contains(viewTab)) {
            tabs.add(viewTab);
        }
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(viewTab);
    }

    private FavoriteConnection.Connection getConnection(TextField name, TextField host, TextField port, TextField password) throws NumberFormatException {
        return new FavoriteConnection.Connection(
                name.getText(),
                host.getText(),
                Integer.valueOf(port.getText()),
                password.getText()
        );
    }
}
