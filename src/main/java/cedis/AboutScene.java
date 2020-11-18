package cedis;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class AboutScene {
    public static Image BREAD_IMAGE = new Image(Layout.class.getClass().getResourceAsStream("/pay/icons8_bread_loaf_64.png"));
    public static Image CHIPS_IMAGE = new Image(Layout.class.getClass().getResourceAsStream("/pay/icons8_chips_64.png"));
    public static Image SODA_IMAGE = new Image(Layout.class.getClass().getResourceAsStream("/pay/icons8_soda_64.png"));
    public static Image NOODLE_IMAGE = new Image(Layout.class.getClass().getResourceAsStream("/pay/icons8_noodles_64.png"));
    public static Image ZHIFUBAO_IMAGE = new Image(Layout.class.getClass().getResourceAsStream("/pay/zhifubao.jpg"));
    public static Image WEIXIN_IMAGE = new Image(Layout.class.getClass().getResourceAsStream("/pay/weixin.png"));

    public static void showAbout() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle("About");
        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, 400, 200);

        ImageView imageView = new ImageView(TabCreator.APP_IMAGE);
        AnchorPane.setTopAnchor(imageView, 14.0);
        AnchorPane.setLeftAnchor(imageView, 14.0);
        imageView.setFitWidth(64);
        imageView.setFitHeight(64);

        Label redisGui = new Label("Redis GUI");
        redisGui.setFont(Font.font("System", 23));
        AnchorPane.setTopAnchor(redisGui, 31.0);
        AnchorPane.setLeftAnchor(redisGui, 105.0);

        Label about = new Label("Redis GUI is a free Redis graphical operating program");
        about.setFont(Font.font("System", 14));
        AnchorPane.setTopAnchor(about, 100.0);
        AnchorPane.setLeftAnchor(about, 20.0);

        Label version = new Label("Version: " + Layout.APP_VERSION);
        version.setFont(Font.font("System", 14));
        AnchorPane.setTopAnchor(version, 130.0);
        AnchorPane.setLeftAnchor(version, 161.0);

        Label foot = new Label("Author: chenjing Emali: 1402752916@qq.com");
        foot.setFont(Font.font("System", 12));
        AnchorPane.setTopAnchor(foot, 162.0);
        AnchorPane.setLeftAnchor(foot, 69.0);

        root.getChildren().addAll(imageView, redisGui, about, version, foot);


        stage.setScene(scene);
        stage.getIcons().addAll(Layout.ICONS);
        stage.setResizable(false);
        stage.showAndWait();
    }

    public static void showSupport() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle("Encourage developer");
        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, 600, 270);

        Text text = new Text();
        text.setText("大家好，我是开发者，感谢你的支持。这是一个捐赠页面，如果有能力的朋友，欢迎请我喝杯饮料、来份外卖，当作对我的支持。");
        text.setWrappingWidth(180);
        AnchorPane.setTopAnchor(text, 13.0);
        AnchorPane.setLeftAnchor(text, 14.0);

        ImageView breadImage = new ImageView(BREAD_IMAGE);
        breadImage.setFitWidth(32);
        breadImage.setFitHeight(32);
        ImageView chipsImage = new ImageView(CHIPS_IMAGE);
        chipsImage.setFitWidth(32);
        chipsImage.setFitHeight(32);
        ImageView sodaImage = new ImageView(SODA_IMAGE);
        sodaImage.setFitWidth(32);
        sodaImage.setFitHeight(32);
        ImageView noodleImage = new ImageView(NOODLE_IMAGE);
        noodleImage.setFitWidth(32);
        noodleImage.setFitHeight(32);
        Label bread = new Label("一个面包 RBM 5", breadImage);
        Label chips = new Label("一份薯条 RBM 7", chipsImage);
        Label soda = new Label("一杯饮料 RBM 10", sodaImage);
        Label noodle = new Label("一碗小面 RBM 15", noodleImage);
        AnchorPane.setTopAnchor(bread, 93.0);
        AnchorPane.setLeftAnchor(bread, 14.0);

        AnchorPane.setTopAnchor(chips, 141.0);
        AnchorPane.setLeftAnchor(chips, 14.0);

        AnchorPane.setTopAnchor(soda, 187.0);
        AnchorPane.setLeftAnchor(soda, 14.0);

        AnchorPane.setTopAnchor(noodle, 226.0);
        AnchorPane.setLeftAnchor(noodle, 14.0);

        ImageView zhifubao = new ImageView(ZHIFUBAO_IMAGE);
        zhifubao.setFitWidth(171);
        zhifubao.setFitHeight(248);
        ImageView weixin = new ImageView(WEIXIN_IMAGE);
        weixin.setFitWidth(181);
        weixin.setFitHeight(248);
        AnchorPane.setTopAnchor(zhifubao, 10.0);
        AnchorPane.setLeftAnchor(zhifubao, 217.0);
        AnchorPane.setTopAnchor(weixin, 10.0);
        AnchorPane.setLeftAnchor(weixin, 405.0);

        root.getChildren().addAll(text, bread, chips, soda, noodle, zhifubao, weixin);
        stage.setScene(scene);
        stage.getIcons().addAll(Layout.ICONS);
        stage.setResizable(false);
        stage.showAndWait();
    }
}
