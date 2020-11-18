package cedis;

import com.jfoenix.controls.JFXTabPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Layout extends Application {
    public static final String APP_NAME = "Redis GUI";
    public static final String APP_VERSION = "1.0";
    public final static Image[] ICONS = new Image[]{
            new Image(Layout.class.getClass().getResourceAsStream("/icons/icons8_redis_16.png")),
            new Image(Layout.class.getClass().getResourceAsStream("/icons/icons8_redis_32.png")),
            new Image(Layout.class.getClass().getResourceAsStream("/icons/icons8_redis_48.png")),
            new Image(Layout.class.getClass().getResourceAsStream("/icons/icons8_redis_64.png"))
    };
    public final Image bkImage = new Image(getClass().getResourceAsStream("/th6.jpg"));
    private TabCreator tabCreator;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        FavoriteConnection favoriteConnection = new FavoriteConnection();
        RedisOperator redisOperator = new RedisOperator();
        tabCreator = new TabCreator(favoriteConnection, redisOperator);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(APP_NAME);
        stage.setOnCloseRequest(event -> {
            Platform.exit();
        });
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add("/css/newconnection.css");

        //设置菜单栏
        MenuBar menuBar = new MenuBar();
        root.setTop(menuBar);
        Menu fileMenu = new Menu("File");
        Menu helpMenu = new Menu("Help");
        MenuItem newConnectionMenuItem = new MenuItem(TabCreator.NEW_CONNECTION_NAME);
        MenuItem closeMenuItem = new MenuItem("Close");
        closeMenuItem.setOnAction(event -> Platform.exit());
        fileMenu.getItems().addAll(newConnectionMenuItem, closeMenuItem);
        MenuItem about = new MenuItem("About");
        MenuItem support = new MenuItem("Encourage developer");

        about.setOnAction(event -> {
            AboutScene.showAbout();
        });

        support.setOnAction(event -> {
            AboutScene.showSupport();
        });


        helpMenu.getItems().addAll(about, support);
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        AnchorPane anchorPane = new AnchorPane();
        ImageView imageView = new ImageView(bkImage);
        AnchorPane.setTopAnchor(imageView, 0.0);
        AnchorPane.setLeftAnchor(imageView, 0.0);
        AnchorPane.setRightAnchor(imageView, 0.0);
        AnchorPane.setBottomAnchor(imageView, 0.0);
        //设置TabPane
        TabPane tabPane = new JFXTabPane();
        tabPane.setVisible(false);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        tabPane.setSide(Side.TOP);
        AnchorPane.setTopAnchor(tabPane, 0.0);
        AnchorPane.setLeftAnchor(tabPane, 0.0);
        AnchorPane.setRightAnchor(tabPane, 0.0);
        AnchorPane.setBottomAnchor(tabPane, 0.0);
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (tabPane.getTabs().size() == 0) {
                imageView.setVisible(true);
                imageView.fitWidthProperty().unbind();
                imageView.fitHeightProperty().unbind();
                imageView.setFitWidth(bkImage.getWidth());
                imageView.setFitHeight(bkImage.getHeight());
            } else {
                imageView.setVisible(false);
                imageView.fitWidthProperty().bind(scene.widthProperty());
                //25为菜单栏的高度
                imageView.fitHeightProperty().bind(scene.heightProperty().subtract(25));
            }
        });

        tabCreator.setTabPane(tabPane);
        newConnectionMenuItem.setOnAction(event -> {
            tabPane.setVisible(true);
            imageView.setVisible(false);
            ObservableList<Tab> tabs = tabPane.getTabs();
            for (Tab t : tabs) {
                if (t.getText().equals(TabCreator.NEW_CONNECTION_NAME)) {
                    tabPane.getSelectionModel().select(t);
                    return;
                }
            }
            Tab connectionTab = tabCreator.createConnectionTab();
            tabPane.getTabs().add(connectionTab);
            tabPane.getSelectionModel().select(connectionTab);
        });

        anchorPane.getChildren().addAll(tabPane, imageView);
        root.setCenter(anchorPane);

        stage.setScene(scene);
        stage.getIcons().addAll(ICONS);
        stage.show();
    }


}