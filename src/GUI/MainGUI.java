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
import model.YaoqiangXMLParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

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

    public void generatePiADL(Event e) throws ParserConfigurationException {
        if (tfFilePath.getText().equals("") || !tfFilePath.getText().matches(".*[.]bpmn")) {
            txtResult.setText("Forneça um arquivo .bpmn válido");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar arquivo .piadl");
        File chosen = fileChooser.showSaveDialog(primaryStage);
        if (chosen == null || chosen.getPath().equals("")) {
            txtResult.setText("Forneça um caminho válido para salvar o arquivo");
            return;
        }
//        if (chosen.getName().matches(".*[.].*") && !chosen.getName().matches(".*[.]piadl")) {
//            txtResult.setText("Forneça um arquivo com extensão .piadl ou informe apenas o nome do arquivo");
//            return;
//        }
        System.out.println(chosen.getName());
        try {
            String res = getPiADLFromBPMN(tfFilePath.getText(), getFileNameFromPath(tfFilePath.getText()));
            //System.out.println(res);

            FileWriter arq = new FileWriter(chosen.getPath().matches(".*[.]piadl")? chosen.getPath() : chosen.getPath() + ".piadl");
            PrintWriter gravarArq = new PrintWriter(arq);
            gravarArq.println(res);
            arq.close();
            txtResult.setText("Arquivo criado com sucesso");



        } catch (IOException | SAXException exception) {
            exception.printStackTrace();
            txtResult.setText("Arquivo .bpmn não encontrado");
        }


        txtResult.setText("Arquivo salvo com sucesso");
    }

    private String getFileNameFromPath(String path) {
        String[] s = path.split("/");
        return s[s.length-1].split("[.]")[0];
    }

    private String getPiADLFromBPMN(String filePath, String fileName) throws IOException, SAXException, ParserConfigurationException {
        try {
            YaoqiangXMLParser parser = new YaoqiangXMLParser();
            parser.parseBPMN(filePath);
            return parser.generatePiADL(fileName);
        } catch (FileNotFoundException exception) {
            //throw new FileNotFoundException();
            return "Não foi possível gerar pi-ADL: não foi possível encontrar arquivo .bpmn informado";
        }

    }
}
