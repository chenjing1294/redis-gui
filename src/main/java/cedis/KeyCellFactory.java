package cedis;

import com.jfoenix.controls.JFXListCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;

public class KeyCellFactory implements Callback<ListView<Key>, ListCell<Key>> {
    public final static Clipboard clipboard = Clipboard.getSystemClipboard();
    private static final String MORE_SCAN = "__SCAN_MORE_9411126374";
    private final static int LABEL_IMAGE_WIDTH = 20;
    private final static int LABEL_IMAGE_HEIGHT = 20;
    private final static int KEY_LABEL_HEIGHT = 20;
    private final FavoriteConnection.Connection connection;
    private final RedisOperator redisOperator;
    private final ListView<Key> listView;
    private final ListView<String> fieldListView;
    private final ComboBox<Integer> dbComboBox;
    private final Label errLabel;
    private final TextField searchTextField;
    private final TextArea textArea;
    private final Image hashImage = new Image(getClass().getResourceAsStream("/key/hash.png"));
    private final Image stringImage = new Image(getClass().getResourceAsStream("/key/string.png"));
    private final Image listImage = new Image(getClass().getResourceAsStream("/key/list.png"));
    private final Image setImage = new Image(getClass().getResourceAsStream("/key/set.png"));
    private final Image zsetImage = new Image(getClass().getResourceAsStream("/key/zset.png"));
    private final Image badImage = new Image(getClass().getResourceAsStream("/key/bad.png"));
    private Key item;

    public KeyCellFactory(FavoriteConnection.Connection connection,
                          RedisOperator redisOperator,
                          ListView<Key> listView,
                          ComboBox<Integer> dbComboBox,
                          TextField searchTextField,
                          Label errLabel,
                          TextArea textArea,
                          ListView<String> fieldListView) {
        this.connection = connection;
        this.redisOperator = redisOperator;
        this.listView = listView;
        this.dbComboBox = dbComboBox;
        this.searchTextField = searchTextField;
        this.errLabel = errLabel;
        this.fieldListView = fieldListView;
        this.textArea = textArea;
    }

    private ImageView getImageView(Key key) {
        ImageView imageView;
        switch (key.getType()) {
            case SET:
                imageView = new ImageView(setImage);
                break;
            case HASH:
                imageView = new ImageView(hashImage);
                break;
            case STRING:
                imageView = new ImageView(stringImage);
                break;
            case LIST:
                imageView = new ImageView(listImage);
                break;
            case ZSET:
                imageView = new ImageView(zsetImage);
                break;
            default:
                imageView = new ImageView(badImage);
        }
        return imageView;
    }

    @Override
    public ListCell<Key> call(ListView<Key> param) {
        param.setOnEditStart(event -> item = param.getItems().get(event.getIndex()));
        ListCell<Key> listCell = new JFXListCell<Key>() {
            @Override
            public void commitEdit(Key newValue) {
                super.commitEdit(newValue);
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                if (item.getName().equals(MORE_SCAN))
                    return;
                Label label = new Label();
                label.setText(item.getName());
                label.setAlignment(Pos.CENTER_LEFT);
                ImageView imageView = getImageView(item);
                imageView.setFitWidth(LABEL_IMAGE_WIDTH);
                imageView.setFitHeight(LABEL_IMAGE_HEIGHT);
                label.setGraphic(imageView);
                setGraphic(label);
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if (item.getName().equals(MORE_SCAN))
                    return;
                HBox hBox = new HBox(5);
                TextField textField = new TextField();
                textField.setFont(Font.font("Courier New"));
                textField.setPrefHeight(KEY_LABEL_HEIGHT);
                textField.setText(item.getName());
                HBox.setHgrow(textField, Priority.ALWAYS);
                ImageView imageView = getImageView(item);
                imageView.setFitWidth(LABEL_IMAGE_WIDTH);
                imageView.setFitHeight(LABEL_IMAGE_HEIGHT);
                hBox.getChildren().addAll(imageView, textField);
                textField.setOnKeyPressed(event -> {
                    // 如果按下ENTER键则提交修改
                    if (event.getCode().getName().equals(KeyCode.ENTER.getName())) {
                        if (textField.getText().length() > 0 && !textField.getText().equals(item.getName())) {
                            String newKeyName = textField.getText();
                            int db = dbComboBox.getSelectionModel().getSelectedItem();
                            int res = redisOperator.rename(item, newKeyName, connection, db);
                            if (res == 0) {
                                errLabel.setText("Key existed");
                                errLabel.setTextFill(Paint.valueOf("#ff0000"));
                            } else
                                commitEdit(new Key(newKeyName, item.getType()));
                        }
                    }
                });
                setGraphic(hBox);
            }

            @Override
            protected void updateItem(Key item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    HBox hBox = new HBox(5);
                    if (item.getName().equals(MORE_SCAN)) {
                        Label label = new Label("Scan more...");
                        label.setFont(Font.font("Courier New"));
                        label.setAlignment(Pos.CENTER_LEFT);
                        label.prefHeight(KEY_LABEL_HEIGHT);
                        label.setUnderline(true);
                        label.setOnMouseClicked(event -> {
                            List<Key> dbContent = redisOperator.getDbContent(0, connection, false);
                            ObservableList<Key> items = listView.getItems();
                            int size = items.size();
                            Key lastKey = items.get(size - 1);
                            items.remove(lastKey);
                            if (dbContent != null && dbContent.size() > 0) {
                                listView.getItems().addAll(dbContent);
                                items.add(lastKey);
                            }
                        });
                        hBox.getChildren().addAll(label);
                    } else {
                        Label label = new Label();
                        label.setFont(Font.font("Courier New"));
                        label.setText(item.getName());
                        label.setPrefHeight(KEY_LABEL_HEIGHT);
                        label.setAlignment(Pos.CENTER_LEFT);
                        HBox.setHgrow(label, Priority.ALWAYS);
                        ImageView imageView = getImageView(item);
                        imageView.setFitWidth(LABEL_IMAGE_WIDTH);
                        imageView.setFitHeight(LABEL_IMAGE_HEIGHT);
                        hBox.getChildren().addAll(imageView, label);
                    }
                    setGraphic(hBox);
                    setText(null);
                } else {
                    setText(null);
                    setGraphic(null);
                }
            }
        };

        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem copyMenuItem = new MenuItem("Copy");
        MenuItem addMenuItem = new MenuItem("Add");
        contextMenu.getItems().addAll(copyMenuItem, addMenuItem, deleteMenuItem);
        deleteMenuItem.setOnAction(event -> {
            Key key = listView.getSelectionModel().getSelectedItem();
            redisOperator.delete(connection, dbComboBox.getSelectionModel().getSelectedItem(), key);
            listView.getItems().remove(key);
            errLabel.setText("Delete successfully");
        });
        copyMenuItem.setOnAction(event -> {
            ClipboardContent clipboardContent = new ClipboardContent();
            Key key = listView.getSelectionModel().getSelectedItem();
            clipboardContent.putString(key.getName());
            clipboard.setContent(clipboardContent);
            errLabel.setText("Copy successfully");
        });
        addMenuItem.setOnAction(event -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add new key");
            BorderPane root = new BorderPane();
            Scene scene = new Scene(root, 400, 200);
            scene.getStylesheets().add("/css/newconnection.css");

            AnchorPane center = new AnchorPane();
            root.setCenter(center);

            GridPane gridPane = new GridPane();
            gridPane.setVgap(10);
            center.getChildren().add(gridPane);
            AnchorPane.setTopAnchor(gridPane, 52.0);
            AnchorPane.setLeftAnchor(gridPane, 50.0);
            AnchorPane.setRightAnchor(gridPane, 50.0);
            AnchorPane.setBottomAnchor(gridPane, 35.0);
            ColumnConstraints col1 = new ColumnConstraints(100);
            ColumnConstraints col2 = new ColumnConstraints(200);
            gridPane.getColumnConstraints().addAll(col1, col2);
            Label label1 = new Label("数据类型: ");
            label1.setAlignment(Pos.CENTER_LEFT);
            label1.setFont(Font.font("System", FontWeight.BOLD, 12));
            Label label2 = new Label("Key name: ");
            label2.setAlignment(Pos.CENTER_LEFT);
            label2.setFont(Font.font("System", FontWeight.BOLD, 12));
            gridPane.add(label1, 0, 0);
            gridPane.add(label2, 0, 1);

            String[] types = new String[]{"HASH", "STRING", "LIST", "SET", "ZSET"};
            ComboBox<String> stringComboBox = new ComboBox<>(FXCollections.observableArrayList(types));
            stringComboBox.getSelectionModel().select(0);
            GridPane.setHgrow(stringComboBox, Priority.ALWAYS);
            stringComboBox.setPrefWidth(200);
            gridPane.add(stringComboBox, 1, 0);
            TextField textField = new TextField();
            GridPane.setHgrow(textField, Priority.ALWAYS);
            gridPane.add(textField, 1, 1);

            AnchorPane left = new AnchorPane();
            Button button1 = new Button("Add");
            button1.getStyleClass().add("quick-connect-button");
            AnchorPane.setBottomAnchor(button1, 15.0);
            AnchorPane.setRightAnchor(button1, 24.0);
            Button button2 = new Button("Cancel");
            button2.getStyleClass().add("quick-connect-button");
            AnchorPane.setBottomAnchor(button2, 15.0);
            AnchorPane.setRightAnchor(button2, 88.0);
            left.getChildren().addAll(button1, button2);

            button1.setOnAction(event1 -> {
                String type = stringComboBox.getSelectionModel().getSelectedItem();
                String key = textField.getText();
                int res = 1;
                int db = dbComboBox.getSelectionModel().getSelectedItem();
                if (key.length() > 0) {
                    res = redisOperator.add(type, connection, db, key, "0", "New Member");
                    if (res == 0) {
                        errLabel.setTextFill(Paint.valueOf("#ff0000"));
                        errLabel.setText("Key existed");
                    } else {
                        errLabel.setText("");
                        stage.close();
                        //搜索刚添加的key
                        searchTextField.setText(key);
                        List<Key> search = redisOperator.search(key, connection, db);
                        listView.getItems().clear();
                        fieldListView.getItems().clear();
                        textArea.setText("");
                        listView.getItems().addAll(search);
                    }
                }
            });

            button2.setOnAction(event1 -> {
                stage.close();
            });

            root.setBottom(left);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        });
        listCell.setOnContextMenuRequested(event -> contextMenu.show(listCell, event.getScreenX(), event.getScreenY()));
        return listCell;
    }
}
