package GUI;

import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainGUI extends Application {

    private Stage primaryStage;
    @FXML
    private TextField tfFilePath;
    @FXML
    private Text txtResult;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/GUI/MainGUI.fxml"));
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Teste de Deadlock");
        Scene scene = new Scene(root, 681, 333);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
        this.primaryStage.setResizable(false);
    }

    public void chooseFile(Event e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File chosen = fileChooser.showOpenDialog(primaryStage);
        if (chosen == null) {
            return;
        }
        tfFilePath.setText(chosen.getPath());
    }

    public void testDeadlock(Event e) {
        txtResult.setText("Nenhum deadlock foi identificado");
    }
}
