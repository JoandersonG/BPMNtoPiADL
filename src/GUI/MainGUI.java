package GUI;

import deadlockTest.TestDeadlock;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.YaoqiangXMLParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class MainGUI extends Application {

    public Text txtResultDeadlockTest;
    public TextField tfFilePathDeadlockTest;
    public TextField tfFilePathPiADL;
    public Text errorPiADL;
    public Text resultPiADL;
    public Text errorDeadlockTest;
    private Stage primaryStage;
    @FXML
    public ToggleButton testDeadlockMenuButton;
    @FXML
    private ToggleButton aboutMenuButton;
    @FXML
    private ToggleButton piADLMenuButton;
    @FXML
    public AnchorPane apPiADL;
    @FXML
    public AnchorPane apDeadlockTest;
    private String savingPiADLPath = null;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/GUI/MainGUI.fxml"));
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Teste de Deadlock");
        Scene scene = new Scene(root, 681, 455);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
        this.primaryStage.setResizable(false);
    }

    public void chooseFile(Event e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione o arquivo .bpmn");
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

    public void TestDeadlockView(ActionEvent actionEvent) {
        testDeadlockMenuButton.setSelected(true);
        testDeadlockMenuHandleOnClick(actionEvent);
    }

    public void piADLMenuButtonHandleOnClick(ActionEvent actionEvent) {
        if (!testDeadlockMenuButton.isSelected() && !aboutMenuButton.isSelected()) {
            piADLMenuButton.setSelected(true);
        }
        testDeadlockMenuButton.setSelected(false);
        aboutMenuButton.setSelected(false);

        apPiADL.setVisible(true);
        apDeadlockTest.setVisible(false);
    }

    public void testDeadlockMenuHandleOnClick(ActionEvent actionEvent) {
        if (!piADLMenuButton.isSelected() && !aboutMenuButton.isSelected()) {
            testDeadlockMenuButton.setSelected(true);
        }
        piADLMenuButton.setSelected(false);
        aboutMenuButton.setSelected(false);

        apPiADL.setVisible(false);
        apDeadlockTest.setVisible(true);
    }

    public void aboutMenuHandleOnClick(ActionEvent actionEvent) {
        if (!testDeadlockMenuButton.isSelected() && !piADLMenuButton.isSelected()) {
            aboutMenuButton.setSelected(true);
        }
        testDeadlockMenuButton.setSelected(false);
        piADLMenuButton.setSelected(false);
    }

    public void chooseFileDeadlockTest(ActionEvent actionEvent) {
        chooseFile(tfFilePathDeadlockTest, "Selecione o arquivo compile do seu programa gerado pelo Eclipse");
    }

    public void chooseFilePiADL(ActionEvent actionEvent) {
        chooseFile(tfFilePathPiADL, "Selecione o arquivo .bpmn");
    }

    public void selectSavingPathPiADLOnClick(ActionEvent actionEvent) {
        errorPiADL.setVisible(false);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar arquivo .piadl");
        File chosen = fileChooser.showSaveDialog(primaryStage);
        if (chosen != null && !chosen.getPath().equals("")) {
            savingPiADLPath = chosen.getPath();
        }
    }

    public void TestDeadlock(ActionEvent actionEvent) {
        errorDeadlockTest.setVisible(false);
        if (tfFilePathDeadlockTest == null || tfFilePathDeadlockTest.getText().equals("")) {
            errorDeadlockTest.setText("Forneça um caminho para o arquivo compile, localizado dentro do projeto criado no Eclipse");
            errorDeadlockTest.setVisible(true);
            return;
        }
        String workingDirectory = tfFilePathDeadlockTest.getText().substring(0, tfFilePathDeadlockTest.getText().lastIndexOf("/"));
        String result = TestDeadlock.performDeadlockTest(workingDirectory);
        if (result.equals("-1")) {
            errorDeadlockTest.setText("Erro: não foi possível obter retorno de teste de deadlock");
            errorDeadlockTest.setVisible(true);
            return;
        }
        txtResultDeadlockTest.setText(result);
    }
}
