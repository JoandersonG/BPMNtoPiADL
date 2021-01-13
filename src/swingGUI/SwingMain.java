package swingGUI;

import deadlockTest.TestDeadlock;
import model.YaoqiangXMLParser;
import org.xml.sax.SAXException;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class SwingMain {
    private JPanel mainPanel;
    private JButton generatePiADLButton;
    private JButton testDeadlockButton;
    private JButton aboutApplicationButton;
    private JTextField tfBPMNPath;
    private JButton btSelectFilePiADL;
    private JLabel resultPiADL;
    private JButton selectPlaceSavePiADL;
    private JButton btGeneratePiADL;
    private JLabel labelInstructionsGeneratePiADL;
    private JLabel errorPiADL;
    private JLabel lPiADL;
    private JLabel lDeadlock;
    private JButton btTestDeadlockPiADL;
    private JTextField tfDeadlockPath;
    private JButton btSelectFileDeadlock;
    private JLabel resultDeadlock;
    private JLabel errorDeadlock;
    private JButton btTestDeadlock;
    private JLabel lAboutApplication;

    private String savingPiADLPath;
    private YaoqiangXMLParser parser;

    SwingMain() {
        String paragraph = "Com o arquivo .piadl, inicie o Eclipse, que deve possuir o plugin para o pi-ADL arquivo" +
                " (link), então insira e  salve o arquivo .piadl. Em sequida, pressione o botão abaixo para testar a " +
                        "ocorrência de deadlocks.";
        labelInstructionsGeneratePiADL.setText("<html><p style=\"width:500px \">" + paragraph + "</p></html>");
        paragraph = "Este é um prorgama criado pra realizar duas tarefas distintas:" +
                "<br>" +
                "<br>" +
                "1. A primeira tarefa que o programa resolve é a geração de código na linguagem pi-ADL a partir " +
                "de arquivos .bpmn gerados na plataforma, chamada Yaoqiang, de design de modelos do diagrama de" +
                " coreografia BPMN 2.0. O programa utiliza o mapeamento de BPMN para pi-ADL utilizando o trabalho de " +
                "mestrado de Leila de Carvalho Costa do Programa de Pós-Graduação em Ciência da Computação da " +
                "Universidade Federal da Bahia." +
                "<br>" +
                "<br>" +
                "2. A segunda tarefa que o programa resolve é o teste de ocorrência de deadlocks, incluindo o " +
                "nome de um elemento na área que ocorreu o deadlock no modelo original BPMN." +
                "<br>" +
                "<br>" +
                "Autor: Joanderson Gonçalves santos" +
                "<br>" +
                "contato: joandersongsantos@yahoo.com" +
                "<br>" +
                "Tutora: Leila de Carvalho Costa" +
                "<br>" +
                "Orientadora: profa. Dra Rita Suzana pitangueira Maciel" +
                "<br>" +
                "Departamento de Ciência da Computação" +
                "<br>" +
                "Instituto de Matemática" +
                "<br>" +
                "Universidade Federal da Bahia";
        lAboutApplication.setText("<html><p style=\"width:500px \">" + paragraph + "</p></html>");


        btSelectFilePiADL.addActionListener(actionEvent ->
                chooseFile(tfBPMNPath, "Selecione um arquivo .bpmn")
        );
        selectPlaceSavePiADL.addActionListener(actionEvent -> {
            errorPiADL.setVisible(false);
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvar arquivo .piadl");
            fileChooser.showSaveDialog(mainPanel);
            File chosen = fileChooser.getSelectedFile();
            if (chosen != null && !chosen.getPath().equals("")) {
                savingPiADLPath = chosen.getPath();
            }
        });
        errorPiADL.setVisible(false);
        setDeadlockVisibility(false);
        setPiADLVisibility(true);
        setAboutVisibility(false);
        btGeneratePiADL.addActionListener(actionEvent -> generatePiADL());
        generatePiADLButton.addActionListener(actionEvent -> {
            setDeadlockVisibility(false);
            setAboutVisibility(false);
            setPiADLVisibility(true);
        });
        testDeadlockButton.addActionListener(actionEvent -> {
            setPiADLVisibility(false);
            setAboutVisibility(false);
            setDeadlockVisibility(true);
        });

        aboutApplicationButton.addActionListener(actionEvent -> {
            setPiADLVisibility(false);
            setDeadlockVisibility(false);
            setAboutVisibility(true);
        });
        btSelectFileDeadlock.addActionListener(actionEvent ->
                chooseFile(tfDeadlockPath, "Selecione o arquivo compile do seu programa gerado pelo Eclipse")
        );
        btTestDeadlock.addActionListener(actionEvent -> testDeadlock());

        btTestDeadlockPiADL.addActionListener(actionEvent -> {
            setAboutVisibility(false);
            setPiADLVisibility(false);
            setDeadlockVisibility(true);
        });
    }

    private void setAboutVisibility(boolean b) {
        lAboutApplication.setVisible(b);
    }

    private void setDeadlockVisibility(boolean b) {
        lDeadlock.setVisible(b);
        tfDeadlockPath.setVisible(b);
        btSelectFileDeadlock.setVisible(b);
        labelInstructionsGeneratePiADL.setVisible(b);
        resultDeadlock.setVisible(b);
        errorDeadlock.setVisible(false);
        btTestDeadlock.setVisible(b);
    }

    private void setPiADLVisibility(boolean b) {
        lPiADL.setVisible(b);
        tfBPMNPath.setVisible(b);
        btSelectFilePiADL.setVisible(b);
        labelInstructionsGeneratePiADL.setVisible(b);
        btGeneratePiADL.setVisible(b);
        resultPiADL.setVisible(b);
        errorPiADL.setVisible(false);
        selectPlaceSavePiADL.setVisible(b);
        btTestDeadlockPiADL.setVisible(b);

    }

    private void generatePiADL() {
        errorPiADL.setVisible(false);
        if (tfBPMNPath.getText().equals("") || !tfBPMNPath.getText().matches(".*[.]bpmn")) {
            errorPiADL.setText("Forneça um arquivo .bpmn");
            errorPiADL.setVisible(true);
            return;
        }
        if (savingPiADLPath == null || savingPiADLPath.equals("")) {
            errorPiADL.setText("Forneça um caminho válido para salvar o arquivo");
            errorPiADL.setVisible(true);
            return;
        }
        try {
            String res = getPiADLFromBPMN(tfBPMNPath.getText(), getFileNameFromPath(tfBPMNPath.getText()));
            FileWriter arq = new FileWriter(savingPiADLPath.matches(".*[.]piadl")? savingPiADLPath : savingPiADLPath + ".piadl");
            PrintWriter gravarArq = new PrintWriter(arq);
            gravarArq.println(res);
            arq.close();
            resultPiADL.setText("Arquivo salvo com sucesso");
        } catch (IOException | SAXException | ParserConfigurationException exception) {
            exception.printStackTrace();
            errorPiADL.setText("Arquivo .bpmn não encontrado");
            errorPiADL.setVisible(true);
        }
    }

    private String getFileNameFromPath(String path) {
        String[] s = path.split("/");
        return s[s.length-1].split("[.]")[0];
    }

    private String getPiADLFromBPMN(String filePath, String fileName) throws IOException, SAXException, ParserConfigurationException {
        try {
            parser = new YaoqiangXMLParser();
            parser.parseBPMN(filePath);
            return parser.generatePiADL(fileName);
        } catch (FileNotFoundException exception) {
            return "Não foi possível gerar pi-ADL: não foi possível encontrar arquivo .bpmn informado";
        }

    }

    public void testDeadlock() {
        errorDeadlock.setVisible(false);
        if (
                tfDeadlockPath == null
                        || tfDeadlockPath.getText().equals("")
                        || !tfDeadlockPath.getText().matches(".*/compile")
        ){
            errorDeadlock.setText("Forneça um caminho para o arquivo compile, localizado dentro do projeto criado no Eclipse");
            errorDeadlock.setVisible(true);
            return;
        }
        String workingDirectory = tfDeadlockPath.getText().substring(0, tfDeadlockPath.getText().lastIndexOf("/"));
        String result = TestDeadlock.performDeadlockTest(workingDirectory);
        if (result.equals("-1")) {
            errorDeadlock.setText("Erro: não foi possível obter retorno de teste de deadlock");
            errorDeadlock.setVisible(true);
            return;
        }
        if (result.matches("deadlock.*") && parser != null) {
            //get the element where the deadlock occurred
            String[] split = result.split(" ");
            String element = split[split.length - 1];
            String originalName = parser.getComponentsOriginalName(element);
            if (originalName != null) {
                result = result.replaceFirst(element, originalName);
            }
        }
        resultDeadlock.setText(result);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Geração de pi-ADL e teste de deadlock");
        frame.setContentPane((new SwingMain()).mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(830,400);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void chooseFile(JTextField textField, String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.showOpenDialog(mainPanel);
        File chosen = fileChooser.getSelectedFile();
        if (chosen == null) {
            return;
        }
        textField.setText(chosen.getPath());
    }
}
