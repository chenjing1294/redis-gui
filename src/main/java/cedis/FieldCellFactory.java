package cedis;

import com.jfoenix.controls.JFXListCell;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

public class FieldCellFactory implements Callback<ListView<String>, ListCell<String>> {
    private final static int KEY_LABEL_HEIGHT = 20;
    private final FavoriteConnection.Connection connection;
    private final RedisOperator redisOperator;
    private final ListView<Key> keyListView;
    private final ListView<String> fieldListView;
    private final ComboBox<Integer> dbComboBox;
    private final TextField searchTextField;
    private final Label errLabel;
    private final TextArea textArea;
    private String item;

    public FieldCellFactory(FavoriteConnection.Connection connection,
                            RedisOperator redisOperator,
                            ListView<Key> keyListView,
                            ComboBox<Integer> dbComboBox,
                            TextField searchTextField,
                            Label errLabel,
                            TextArea textArea,
                            ListView<String> fieldListView) {
        this.connection = connection;
        this.redisOperator = redisOperator;
        this.keyListView = keyListView;
        this.dbComboBox = dbComboBox;
        this.errLabel = errLabel;
        this.fieldListView = fieldListView;
        this.searchTextField = searchTextField;
        this.textArea = textArea;
    }

    @Override
    public ListCell<String> call(ListView<String> param) {
        param.setOnEditStart(event -> item = param.getItems().get(event.getIndex()));
        ListCell<String> listCell = new JFXListCell<String>() {
            @Override
            public void commitEdit(String newValue) {
                super.commitEdit(newValue);
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(item);
                setGraphic(null);
            }

            @Override
            public void startEdit() {
                super.startEdit();
                Key key = keyListView.getSelectionModel().getSelectedItem();
                switch (key.getType()) {
                    case SET:
                    case STRING:
                    case LIST:
                        break;
                    case ZSET:
                        break;
                    case HASH:
                        TextField textField = new TextField();
                        textField.setFont(Font.font("Courier New"));
                        textField.setPrefHeight(KEY_LABEL_HEIGHT);
                        textField.setText(item);
                        textField.setOnKeyPressed(event -> {
                            // 如果按下ENTER键则提交修改
                            if (event.getCode().getName().equals(KeyCode.ENTER.getName())) {
                                if (textField.getText().length() > 0 && !textField.getText().equals(item)) {
                                    String newField = textField.getText();
                                    int db = dbComboBox.getSelectionModel().getSelectedItem();
                                    Key k = keyListView.getSelectionModel().getSelectedItem();
                                    if (k.getType() == Key.Type.HASH) {
                                        int res = redisOperator.rename(k, newField, item, connection, db);
                                        if (res == 0) {
                                            errLabel.setTextFill(Paint.valueOf("#ff0000"));
                                            errLabel.setText("命名冲突");
                                        } else {
                                            errLabel.setTextFill(Paint.valueOf("#ffffff"));
                                            errLabel.setText("操作成功");
                                            commitEdit(newField);
                                        }
                                    }
                                }
                            }
                        });
                        setGraphic(textField);
                        setText(null);
                }
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    setGraphic(null);
                    setText(item);
                    setFont(Font.font("Courier New"));
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
            Key key = keyListView.getSelectionModel().getSelectedItem();
            String field = fieldListView.getSelectionModel().getSelectedItem();
            redisOperator.delete(connection, dbComboBox.getSelectionModel().getSelectedItem(), key, field);
            fieldListView.getItems().remove(field);
            errLabel.setText("Delete successfully");
        });
        copyMenuItem.setOnAction(event -> {
            ClipboardContent clipboardContent = new ClipboardContent();
            String field = fieldListView.getSelectionModel().getSelectedItem();
            clipboardContent.putString(field);
            KeyCellFactory.clipboard.setContent(clipboardContent);
            errLabel.setText("Copy successfully");
        });
        addMenuItem.setOnAction(event -> {
            Key key = keyListView.getSelectionModel().getSelectedItem();
            if (key == null)
                return;
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
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
            ToggleGroup toggleGroup = new ToggleGroup();
            final Label label3 = new Label();
            final TextField zsetTextField = new TextField();
            switch (key.getType()) {
                case HASH:
                    stage.setTitle("HASH: Add new key");
                    stringComboBox.getSelectionModel().select(0);
                    break;
                case LIST:
                    stage.setTitle("LIST: Add new item");
                    stringComboBox.getSelectionModel().select(2);
                    label2.setText("Value:");
                    label3.setText("Insert to:");
                    label3.setAlignment(Pos.CENTER_LEFT);
                    label3.setFont(Font.font("System", FontWeight.BOLD, 12));
                    gridPane.add(label3, 0, 2);
                    HBox hBox = new HBox(10);
                    RadioButton head = new RadioButton("head");
                    head.setUserData(1);
                    RadioButton tail = new RadioButton("tail");
                    tail.setUserData(0);
                    head.setToggleGroup(toggleGroup);
                    tail.setToggleGroup(toggleGroup);
                    hBox.getChildren().addAll(head, tail);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    gridPane.add(hBox, 1, 2);
                    break;
                case SET:
                    stage.setTitle("SET: Add new item");
                    label2.setText("Value:");
                    stringComboBox.getSelectionModel().select(3);
                    break;
                case ZSET:
                    stage.setTitle("ZSET: Add new item");
                    label2.setText("Value:");
                    label3.setText("Score:");
                    label3.setAlignment(Pos.CENTER_LEFT);
                    label3.setFont(Font.font("System", FontWeight.BOLD, 12));
                    gridPane.add(label3, 0, 2);
                    GridPane.setHgrow(zsetTextField, Priority.ALWAYS);
                    gridPane.add(zsetTextField, 1, 2);
                    stringComboBox.getSelectionModel().select(4);
                    break;
            }
            stringComboBox.setDisable(true);
            GridPane.setHgrow(stringComboBox, Priority.ALWAYS);
            stringComboBox.setPrefWidth(200);
            gridPane.add(stringComboBox, 1, 0);
            TextField textField = new TextField();
            GridPane.setHgrow(textField, Priority.ALWAYS);
            gridPane.add(textField, 1, 1);

            AnchorPane left = new AnchorPane();
            Button button1 = new Button("Add");
            AnchorPane.setBottomAnchor(button1, 15.0);
            AnchorPane.setRightAnchor(button1, 24.0);
            Button button2 = new Button("Cancel");
            AnchorPane.setBottomAnchor(button2, 15.0);
            AnchorPane.setRightAnchor(button2, 88.0);
            left.getChildren().addAll(button1, button2);

            button1.setOnAction(event1 -> {
                String type = stringComboBox.getSelectionModel().getSelectedItem();
                int db = dbComboBox.getSelectionModel().getSelectedItem();
                String field;
                switch (type) {
                    case "HASH":
                        field = textField.getText();
                        int res = 1;
                        res = redisOperator.addField(type, connection, db, key.getName(), field, "New Member", null);
                        if (res == 0) {
                            errLabel.setText("Key existed");
                        } else {
                            errLabel.setText("");
                            stage.close();
                            //搜索刚添加的key
                            searchTextField.setText(">" + field);
                            List<String> search = redisOperator.search(field, key, connection, db);
                            fieldListView.getSelectionModel().clearSelection();
                            fieldListView.getItems().clear();
                            textArea.setText("");
                            fieldListView.getItems().addAll(search);
                        }
                        break;
                    case "LIST":
                        field = textField.getText();
                        redisOperator.addField(type, connection, db, key.getName(), null, field, (Integer) toggleGroup.getSelectedToggle().getUserData());
                        errLabel.setText("");
                        stage.close();
                        //搜索刚添加的key
                        searchTextField.setText(field);
                        fieldListView.getSelectionModel().clearSelection();
                        fieldListView.getItems().clear();
                        textArea.setText("");
                        fieldListView.getItems().addAll(redisOperator.search(field, key, connection, db));
                        break;
                    case "SET":
                        field = textField.getText();
                        redisOperator.addField(type, connection, db, key.getName(), null, field, null);
                        errLabel.setText("");
                        stage.close();
                        //搜索刚添加的key
                        searchTextField.setText(field);
                        fieldListView.getSelectionModel().clearSelection();
                        fieldListView.getItems().clear();
                        textArea.setText("");
                        fieldListView.getItems().addAll(redisOperator.search(field, key, connection, db));
                        break;
                    case "ZSET":
                        field = textField.getText();
                        String score = zsetTextField.getText();
                        redisOperator.addField(type, connection, db, key.getName(), field, score, null);
                        errLabel.setText("");
                        stage.close();
                        //搜索刚添加的key
                        searchTextField.setText(field);
                        fieldListView.getSelectionModel().clearSelection();
                        fieldListView.getItems().clear();
                        textArea.setText("");
                        fieldListView.getItems().addAll(redisOperator.search(field, key, connection, db));
                        break;
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
        listCell.getStyleClass().add("jfx-list-cell");
        return listCell;
    }
}
