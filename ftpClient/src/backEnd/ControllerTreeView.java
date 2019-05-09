package frontEnd;

import backEnd.FtpClientNormalThread;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerTreeView implements javafx.fxml.Initializable {

    @FXML
    private ListView<String> logger;


    synchronized public void updateLog(String message)
    {


        String timeStamp = new SimpleDateFormat("hh:mm:ss").format(new Date());

        message ="[" +  timeStamp + "]  " + message;

        logger.getItems().add(message);


    }

    public final boolean DEBUGGER = true;
    public Button buttonSelectFile;
    @FXML
    private Button buttonEnterDirName;
    @FXML
    private TextField textFieldDirName;
    @FXML
    private Button buttonMakedir;
    @FXML
    private Button buttonReset;
    @FXML
    private ChoiceBox choiceBox;
    @FXML
    private TextField textFieldUpload;
    @FXML
    private Button buttonUpload;
    @FXML
    private TreeView<String> treeView;
    @FXML
    private Button buttonDownload;
    @FXML
    private TextField textFieldSelectedFile;
    @FXML
    private Button buttonDelete;
    @FXML
    private TextField textFieldEnterCommand;
    @FXML
    private Button buttonSend;


    private Main main;
    private BlockingQueue queue;
    private static ConcurrentHashMap<String,Vector<String>> treeViewMap;
    private ControllerTreeView controller;
    Image icon =new Image(getClass().getResourceAsStream("/frontEnd/img/folder.png"));
    TreeItem<String> root;

    private Desktop desktop = Desktop.getDesktop();


     private FileChooser fileChooser = new FileChooser();

    @FXML
    private Button buttonSelect;// = new Button("Open a Picture...");


    Stage stage;

    //private String[] folder={"Documents","Images","Songs","Videos"};

    Vector<String> folder;/** new Vector<String>(){{
        add("Documents");
        add("Images");
        add("Songs");
        add("Videos");

    }};**/



    public ControllerTreeView()
    {
        folder=FtpClientNormalThread.folder;
        root=new TreeItem<>("Server Directory", new ImageView(icon));


    }
    @FXML
    public void buttonResetAction(ActionEvent event) {

        try {
            queue.put("refreshGui");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }
    @FXML
    public void sendButtonAction(ActionEvent actionEvent)
    {

        try {
            queue.put(textFieldEnterCommand.getText());


        } catch (InterruptedException e) {
            System.out.println("Blocking Queue cant put Data" + e);
        }
    }

    @FXML
    public void deleteButtonAction(ActionEvent event)
    {

        String selectedfileName=textFieldSelectedFile.getText();

        for (int i=0;i<folder.size();i++)
        {

            List<String> temp=treeViewMap.get(folder.get(i));
            if(temp.contains(selectedfileName))
            {

                try {
                    queue.put("cd" );
                    queue.put("cd "+ folder.get(i));
                    queue.put("delete "+ selectedfileName);
                    queue.put("refreshGui");

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }



            }

        }




    }
    @FXML
    public void downloadButtonAction(ActionEvent event)
    {
        String selectedfileName=textFieldSelectedFile.getText();

        for (int i=0;i<folder.size();i++)
        {

            List<String> temp=treeViewMap.get(folder.get(i));
            if(temp.contains(selectedfileName))
            {
                try {

                    queue.put("cd" );
                    queue.put("cd "+ folder.get(i));
                    queue.put("get "+ selectedfileName);

                    break;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

        }


        }



    public void init(Main main,Stage stage,BlockingQueue queue,ConcurrentHashMap<String,Vector<String>> treeMap) {
        System.out.println("Inside Init");
       // this.treeViewMap=treeViewMap;
        this.main=main;
        this.queue=queue;
        treeViewMap=treeMap;
        this.stage=stage;

        fileChooser.setTitle("Select A file From This Directory :");
        fileChooser.setInitialDirectory(
                new File(String.valueOf(Paths.get(System.getProperty("user.dir"))))
        );
        if(!DEBUGGER)
        {
             buttonSend.setDisable(true);
            textFieldEnterCommand.setDisable(true);
        }

    }



    @FXML
    public void mouseClick(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount()==1) {
            TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();

           try
           {
               textFieldSelectedFile.setText(item.getValue());
           }
           catch (Exception e){}
            System.out.println(" clicked: "+textFieldSelectedFile.getText());
        }
    }
    public void resetFileExplorer(boolean isFirstTime) {
        List<TreeItem<String>> filesNode=new ArrayList<>();

        if(!isFirstTime)root.getChildren().clear();

        int i=-1;

        for (String s :
                treeViewMap.get("Root")) {
                ++i;


                if (folder.contains(s)) {
                    TreeItem<String> temp=new TreeItem<>(s,new ImageView((icon)));
                    filesNode.add(temp);
                    root.getChildren().add(filesNode.get(filesNode.indexOf(temp)));

                    for (String s1 :
                            treeViewMap.get(folder.get(folder.indexOf(s)))) {
                        filesNode.get(filesNode.indexOf(temp)).getChildren().add(new TreeItem<>(s1, new ImageView(icon)));
                    }
                    filesNode.get(filesNode.indexOf(temp)).setExpanded(true);
                }

            }

        treeView.setRoot(root);
        root.setExpanded(true);

        try {
            queue.put("cd");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //choiceBox.getItems().clear();
        if(DEBUGGER)System.out.println("Folder Picker :"+folder);



        for (String s:
             folder) {
            if(!choiceBox.getItems().contains(s))
            {
            choiceBox.getItems().add(s);
            }

        }
    }

    private void openFile(File file) {
        try {
           // desktop.open(file);

            textFieldUpload.setText(file.toString());

        } catch (Exception ex) {
            //TODO:Figure out what it does
            Logger.getLogger(
                    FileChooser.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }
    @FXML
    public void selectFileButtonAction(ActionEvent event)
    {
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openFile(file);
        }

    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {



        Platform.runLater(new Runnable() {
            @Override
            public void run() {

            resetFileExplorer(true);
            //root.setExpanded(true);

            }
        });

    }
    @FXML
    public void uploadButtonAction(ActionEvent event) {

        try {
            if(choiceBox.getValue().equals("null")) {
                System.out.println(choiceBox.getValue());
            }

        }catch (Exception e)
        {
            main.invalid(ErrorCode.UPLOAD_ERROR);
        }
          try {
            queue.put("cd");
              System.out.println(choiceBox.getValue());
            queue.put("cd " +choiceBox.getValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String [] split=(textFieldUpload.getText()).split("\\\\");

        StringBuffer filePath=new StringBuffer("");

        for (int i=0;i<split.length-1;i++)
        {
            filePath.append(split[i]);
          if(i!=split.length-2)filePath.append("\\");
        }

        if(filePath.toString().equals(FtpClientNormalThread.clientPath.toString())) {

            try {
                queue.put("put "+split[split.length-1]);
                queue.put("cd");
                queue.put("refreshGui");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else
            {
                main.invalid(ErrorCode.UPLOAD_ERROR);
            }

    }
    @FXML
    public void buttonMakeDirAction(ActionEvent event) {
        buttonEnterDirName.setVisible(true);
        textFieldDirName.setVisible(true);
        textFieldDirName.setDisable(false);
        buttonEnterDirName.setDisable(false);
    }
    @FXML
    public void buttonEnterDirNameAction(ActionEvent event) {

        System.out.println(textFieldDirName.getText());


        try {
            queue.put("cd");
            queue.put("mkdir "+textFieldDirName.getText());


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            textFieldDirName.setDisable(true);
            buttonEnterDirName.setDisable(true);
            textFieldDirName.setVisible(false);
            buttonEnterDirName.setVisible(false);


            try {
                queue.put("refreshGui");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

    @FXML
    public void buttonLogoutAction(ActionEvent event) {
        try {
            queue.put("bye");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
