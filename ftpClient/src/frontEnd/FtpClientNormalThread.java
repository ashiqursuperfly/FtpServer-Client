
package backEnd;

import frontEnd.ControllerTreeView;
import frontEnd.ErrorCode;
import frontEnd.Main;
import javafx.application.Platform;
import shared.FileData;
import shared.InfoData;
import shared.LsData;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class FtpClientNormalThread implements Runnable {
    private static final int BUFFER_SIZE = 1000000;
    private FtpClient ftpClient;
    private String hostname;
    private String userName;
    private String passWord;


    public int counter = 0;
    //Shared Resources
    private BlockingQueue queue = new ArrayBlockingQueue(100);
    private ConcurrentHashMap<String, Vector<String>> treeViewMap = new ConcurrentHashMap<>();

    private Main main;
    //private LoginController loginController;
    private String[] loginData;


    public static Vector<String> folder = new Vector<String>();/* {{
        add("Documents");
        add("Images");
        add("Songs");
        add("Videos");

    }};*/
    private boolean logLock=false;


    private int port;

    private Socket socket;
    private Path currentPath;///initiateStream er Moddhei server theke 'pwd' command diye initialise kora hoi
    private Path serverRootPath;
    public static Path clientPath;
    private Vector<String> tokens;


    //Stream
    //for string task/output with server



    //for data task/output with server
     ObjectInputStream objectInputStream;///get command erpor server theke asha data backEnd e read kore
     ObjectOutputStream objectOutputStream;//put command theke data server e write kore

    OutputStream outputStream;
    DataInputStream dataInputStream;
    /**
     * basically FtpClientNormalThread Thread er Constructor e
     * 1: ftpClient ta ,hostName,nport initialise hoi,
     * 2: Ekta socket diye oi ftpClient take server(hostName er server) e connect kore
     * 3: oi socket er task,output streams gula diye FtpClientNormalThread thread er nijer stream gula initialise kore
     **/
    public FtpClientNormalThread(FtpClient ftpClient, String[] loginData, Main main) {



        this.ftpClient = ftpClient;
        this.hostname = loginData[0];
        this.port = Integer.parseInt(loginData[1]);
        this.userName = loginData[2];
        this.passWord = loginData[3];

        this.main = main;

        //Connect to server
        InetAddress ip = null;

        try {


            ip = InetAddress.getByName(hostname);

            socket = new Socket();
            socket.connect(new InetSocketAddress(ip.getHostAddress(), port), 1000);



            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream=new ObjectInputStream(socket.getInputStream());
            //Data(Reading Data as Bytes from server)
            //objectInputStream = new ObjectInputStream(dataInputStream);







        } catch (SocketTimeoutException ste) {
            System.out.println("error: host could not be reached");
            try {
                showErrorMessage(ErrorCode.NETWORKERROR);

            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (ConnectException ce) {
            System.out.println("error: no running FTP at remote host");
            try {
                showErrorMessage(ErrorCode.NETWORKERROR);

            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            System.out.println(e);
        }


        //Set current working directory
        clientPath = Paths.get(System.getProperty("user.dir"));
        System.out.println("ClientPath:" + clientPath);
        System.out.println("Connected to: " + ip);

        System.out.println("After Connection Before Initiate Stream");
        //Streams
        initiateStream();

        System.out.println("After Initiate Stream");


    }
    public void initialiseTreeViewMap() throws Exception {


        logLock=false;
        counter++;

        cd(new Vector<String>() {{
            add("cd");

        }});
        objectOutputStream.writeObject("ls");

        Object o2=objectInputStream.readObject();

        folder.clear();

        if(o2 instanceof LsData) {
            LsData lsData = (LsData) o2;
            for(String s: lsData.getData())
            {
                if(!s.contains(".") &&  !s.equals("BinaryContent") &&!s.equals("src") && !s.equals("out") && !s.equals(".idea") && !s.equals(".git") && !s.equals("lib") && !s.equals("Server.iml") )
                    folder.add(s);
            }
        }


        treeViewMap.clear();

        if (FtpClient.DEBUGGER) System.out.println("Iniside Initialise treeView " + counter);
        Vector<String> rootDirList = new Vector<String>();

        cd(new Vector<String>() {{
            add("cd");

        }});
        objectOutputStream.writeObject("ls");

        Object o=objectInputStream.readObject();
        if(o instanceof LsData) {
            LsData lsData = (LsData) o;



            if(!lsData.getData().isEmpty()) {
                for (String ls_line :
                        lsData.getData()) {

                    if(folder.contains(ls_line))
                    {
                        rootDirList.add(ls_line);
                    }
                   /* for (String s:
                         folder) {

                        if(ls_line.equals(s))
                        {
                            rootDirList.add(ls_line);
                        }
                    }
                    */
                    //if (ls_line.equals("Documents") || ls_line.equals("Images") || ls_line.equals("Songs") || ls_line.equals("Videos"))
                    //  rootDirList.add(ls_line);
                }
            }
            if (FtpClient.DEBUGGER) System.out.println("Server Directory:" + rootDirList);
            treeViewMap.put("Root", rootDirList);
        }
        else
        {
            showErrorMessage(ErrorCode.OBJECT_READ_ERROR);
            return;
        }
        Vector<String> cdCommands = new Vector<>();
        cdCommands.add("cd");

        Vector<String>[] filesDirList = new Vector[folder.size()];

        int i = -1;
        for (String s :
                treeViewMap.get("Root")) {
            ++i;
            filesDirList[i] = new Vector<>();

            if (folder.contains(s) ) {
                cdCommands.add(s);

                cd(cdCommands);
                ///TODO: some how skip the ls if 's' is a File.




                objectOutputStream.writeObject("ls");

                Object o3 = objectInputStream.readObject();
                if (o3 instanceof LsData) {
                    LsData lsData = (LsData) o3;


                    //String ls_line;
                    for (String ls_line :
                            lsData.getData()) {

                        if (!(ls_line.equals("")))
                            filesDirList[i].add(ls_line);
                    }
                    if (FtpClient.DEBUGGER) System.out.println("Server Directory:" + rootDirList);
                    treeViewMap.put(folder.get(folder.indexOf(s)), filesDirList[i]);
                    //treeViewMap.put(folder.get(i), filesDirList[i]);
                } else {
                    showErrorMessage(ErrorCode.OBJECT_READ_ERROR);
                    return;
                }


                cdCommands.remove(1);
                cd(cdCommands);
            }
            //

            //


        }


        if (FtpClient.DEBUGGER) {
            System.out.println("rootDir" + rootDirList + " ");
            int x = 0;
            for (List<String> list : filesDirList) {
                System.out.println("filesDir [" + x + "] :" + list);
                ++x;
            }

        }

        logLock=true;

    }

    ///Initiating streams and setting serverPath
    public void initiateStream() {
        try {

            objectOutputStream.writeObject("pwd");

            Object o=objectInputStream.readObject();

            if(o instanceof InfoData)
            {
                InfoData temp=(InfoData)o;
                if(!(temp.getInformation().equals("")))
                {
                    currentPath = Paths.get(temp.getInformation());
                    serverRootPath=Paths.get(temp.getInformation());
                    if (FtpClient.DEBUGGER) System.out.println("Inside InitiateStream() -->");
                }
            }

        } catch (Exception e) {
            if (FtpClient.DEBUGGER) System.out.println("stream initiation error");
        }
    }

    public void get() throws Exception {

        if (tokens.size() != 2) {

            showErrorMessage(ErrorCode.MISC);
            return;
        }

        if (!ftpClient.transfer(currentPath.resolve(tokens.get(1)))) {
            System.out.println("error: file already transfering");
            return;
        }

        if (FtpClient.DEBUGGER)
            System.out.println("Before sending get to server");
        //send command

        objectOutputStream.writeObject("get " + currentPath.resolve(tokens.get(1)));

        ftpClient.transferIN(currentPath.resolve(tokens.get(1)));

        File file = new File(tokens.get(1));
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);

        System.out.println("Opened file");

        long bytesWriten = 0;


        while(true) {

            Object o = objectInputStream.readUnshared();


            System.out.println("read object");


            if(o instanceof FileData)
            {

                FileData fileData = (FileData) o;


                if(!fileData.getErrorMessage().equals(""))
                {
                    System.out.println(fileData.getErrorMessage());
                    updateLog(fileData.getErrorMessage());
                    break;
                }



                else if(fileData.isEOF() || fileData.getBytesRead() == -1){
                    fileOutputStream.close();
                    System.out.println("EOF reached");
                    break;
                }
                else {
                    System.out.println("reading data");
                    Integer a = fileData.getBytesRead();
                    Boolean b = fileData.isEOF();
                    System.out.println(a.toString() + b.toString());

                    fileOutputStream.write(fileData.getDataArray(), 0, fileData.getBytesRead());
                    bytesWriten += fileData.getBytesRead();
                }


            }
            else
            {
                showErrorMessage(ErrorCode.DOWNLOAD_ERROR);
                updateLog("Download Failed" + tokens.get(1));
                fileOutputStream.close();
                Files.deleteIfExists(file.toPath());
                break;
            }



        }

        System.out.println("Total bytes recieved "  + bytesWriten);
        System.out.println("Closing file, done recieving");
        updateLog("Completed Download " + tokens.get(1));

        ftpClient.transferOUT(currentPath.resolve(tokens.get(1)));

       /* ftpClient.transferIN(serverPath.resolve(tokens.get(1)));
        updateLog("get " + serverPath.resolve(tokens.get(1)));

        Object o = objectInputStream.readObject();

        if (!(o instanceof FileData)) {
            showErrorMessage(ErrorCode.DOWNLOAD_ERROR);
            updateLog("Download Failed" + tokens.get(1));
            return;
        }

        else {
            FileData temp = (FileData) o;
            if (!(temp.getErrorMessage().equals(""))) {
                updateLog(temp.getErrorMessage());
                return;
            } else {
                FileOutputStream f = new FileOutputStream(new File(tokens.get(1)));
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(f);
                bufferedOutputStream.write(temp.getDataArray());
                bufferedOutputStream.close();
            }

            if (FtpClient.DEBUGGER)
                System.out.println("After closing file");
            updateLog(tokens.get(1) + " Downloaded Succesfully !");*/




       /* //error messages(if any server er kach theke)
        String get_line;
        if (!(get_line = serverReader.readLine()).equals("")) {
            System.out.println(get_line);
            updateLog(get_line);
            return;
        }

        if (FtpClient.DEBUGGER)
            System.out.println("Before sending filesize");

        //get file size
        String filesizeString = serverReader.readLine();

        updateLog("Selected File Size: "+filesizeString);

        long fileSize = Long.parseLong(filesizeString);

        //2688724045736783411
        String sizeString = "2688724045736783411" + "";
        if ((fileSize < 0) || (fileSize >= Long.parseLong(sizeString))) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    main.invalid(ErrorCode.MISC);
                }
            });

            return;
        }

        if (FtpClient.DEBUGGER)
            System.out.println("Before sending file to server");


        System.out.println("filesize :" + fileSize);


        //receive the file

        int count = 0;
        byte[] buffer = new byte[10000];
        long bytesReceived = 0;
        while (bytesReceived < fileSize) {
            count = objectInputStream.read(buffer);
            f.write(buffer, 0, count);
            bytesReceived += count;
            System.out.println("Bytes recieved" + bytesReceived);
        }


        f.close();
*/

       }


    public void put() throws Exception {
        String errorMessage;
        byte[] byteDataArray2 = new byte[100];

        if (tokens.size() != 2) {

            showErrorMessage(ErrorCode.MISC);

            return;
        }
        System.out.println("Put Tokens :" + tokens);

        if(serverRootPath.toString().equals(currentPath.toString()))
        {
            showErrorMessage(ErrorCode.UPLOAD_ERROR);
            updateLog("Please Select a Directory");
            return;
        }

        if (!ftpClient.transfer(clientPath.resolve(tokens.get(1)))) {
            System.out.println("error:This file is already being transferred");
            return;
        }


        if (Files.notExists(clientPath.resolve(tokens.get(1)))) {
            System.out.println("put: " + tokens.get(1) + ": No such file or directory");
            return;
        }

        else if (Files.isDirectory(clientPath.resolve(tokens.get(1)))) {
            System.out.println("put: " + tokens.get(1) + ": Is a directory");
            return;
        }

        objectOutputStream.writeObject("put " + currentPath.resolve(tokens.get(1)) + "\n");

        ftpClient.transferIN(clientPath.resolve(tokens.get(1)));
        File file = new File(clientPath.resolve(tokens.get(1)).toString());

        try (BufferedInputStream bufferedInputStreamForFile = new BufferedInputStream(new FileInputStream(file));)
        {


            int bytesRead = 0;
            System.out.println("before begging of while loop");
            int count = 0;
            long bytesWritten = 0;
            FileData fileData = new FileData(byteDataArray2, "",  tokens.get(1), 0, false   );

            byte[] byteDataArray = new byte[BUFFER_SIZE];


            while(true)
            {    count++;




                bytesRead = bufferedInputStreamForFile.read(byteDataArray);
                if(FtpClient.DEBUGGER)
                    System.out.println("while loop first line count after reading from stream" + count);


                fileData.setDataArray(byteDataArray);
                fileData.setBytesRead(bytesRead);



                fileData.setBytesRead(bytesRead);
                fileData.setDataArray(byteDataArray);

                bytesWritten += fileData.getBytesRead();

                if(FtpClient.DEBUGGER)
                    System.out.println("while loop instance" + count);

                if(bytesRead == -1)
                {    if(FtpClient.DEBUGGER)
                    System.out.println("eof reached");

                    fileData.setEOF(true);
                    Integer a = fileData.getBytesRead();
                    System.out.println(a);
                    objectOutputStream.reset();
                    objectOutputStream.writeUnshared(fileData);

                    objectOutputStream.flush();
                    break;
                }
                else
                {    if(FtpClient.DEBUGGER)
                    System.out.println("writing object time" + count);

                    objectOutputStream.reset();
                    objectOutputStream.writeUnshared(fileData);
                    Integer a = fileData.getBytesRead();
                    System.out.println(a);

                    objectOutputStream.flush();
                    if(FtpClient.DEBUGGER)
                        System.out.println("After writing object" + count);
                }




            }






            System.out.println("Total bytes recieved " + bytesWritten );



        } catch (Exception e) {
            if(FtpClient.DEBUGGER)
                System.out.println("Exception faced in get");
            errorMessage = "get: " + clientPath.resolve(tokens.get(1)).getFileName() + ": Could not transfer file" ;
            FileData fileDataErr = new FileData(byteDataArray2, errorMessage,  tokens.get(1), 0, true );
            objectOutputStream.writeObject(fileDataErr);
        }





        updateLog(tokens.get(1)+": Uploaded Successfully");

        ftpClient.transferOUT(clientPath.resolve(tokens.get(1)));


    }

	public void delete() throws Exception {
		//only two arguments
		if (tokens.size() != 2) {

          showErrorMessage(ErrorCode.MISC);
			return;
		}


		//send command
		objectOutputStream.writeObject("delete " + tokens.get(1));
        updateLog("delete " + tokens.get(1));


        Object o=objectInputStream.readObject();
		if(o instanceof InfoData)
		{
		    InfoData temp =(InfoData)o;
           if(!temp.getInformation().equals("")) {
               System.out.println("Delete_line:" + temp.getInformation());
               updateLog(temp.getInformation());
           }
        }
        else
        {
            showErrorMessage(ErrorCode.OBJECT_READ_ERROR);

        }



    }

	public void ls() throws Exception {
		//only one argument
		if (tokens.size() != 1) {

            showErrorMessage(ErrorCode.MISC);

            return;
		}

		//send command
		objectOutputStream.writeObject("ls");
        updateLog("ls");


        Object o=objectInputStream.readObject();

        if(o instanceof LsData)
        {
            LsData temp=(LsData)o;
            if(!temp.getData().isEmpty())
            {
                System.out.println(temp.getData());
                for (String ls:
                     temp.getData()) {

                    updateLog(ls);
                }
            }

        }
	}

	public void cd(Vector<String> tokens) throws Exception {
		//up to two arguments
		if (tokens.size() > 2) {

            showErrorMessage(ErrorCode.MISC);

            return;
		}



		//send command
		if (tokens.size() == 1) //allow "cd" goes back to home directory
			objectOutputStream.writeObject("cd");
		else
			objectOutputStream.writeObject("cd " + tokens.get(1));

        Object o=objectInputStream.readObject();

        if(o instanceof InfoData)
        {
            InfoData temp=(InfoData)o;
            String cd_line=temp.getInformation();

            if (!cd_line.equals("")) {
                System.out.println(cd_line);

                updateLog(cd_line);
            }

        }
        else
        {

            showErrorMessage(ErrorCode.OBJECT_READ_ERROR);

        }



		objectOutputStream.writeObject("pwd");


        Object o2=objectInputStream.readObject();

        if(o2 instanceof InfoData)
        {
            InfoData temp=(InfoData)o2;
            //messages
            String pwd_line=temp.getInformation();

            if (!pwd_line.equals("")) {
                System.out.println(pwd_line);
                currentPath = Paths.get(pwd_line);
                updateLog(pwd_line);
            }

        }
        else
        {
            showErrorMessage(ErrorCode.OBJECT_READ_ERROR);
        }


	}
	public void refreshGui() throws Exception {
        initialiseTreeViewMap();
        ControllerTreeView controllerTreeView=main.getControllerTreeView();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(controllerTreeView!=null)controllerTreeView.resetFileExplorer(false);
            }
        });

    }
    public void updateLog(String msg)
    {
        if(msg.equals("")){return;}
        if(!logLock)return;
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                ControllerTreeView controllerTreeView=main.getControllerTreeView();

                if(controllerTreeView!=null)controllerTreeView.updateLog(msg);
            }
        });
    }

	public void mkdir() throws Exception {
		//only two arguments
		if (tokens.size() != 2) {

            showErrorMessage(ErrorCode.MISC);
			return;
		}




		objectOutputStream.writeObject("mkdir " + tokens.get(1));


        Object o=objectInputStream.readObject();

        if(o instanceof InfoData)
        {
            InfoData temp=(InfoData)o;
            if(!temp.getInformation().equals("")) {
                updateLog(temp.getInformation());
                System.out.println(temp.getInformation());

            }
            else {
                folder.add(tokens.get(1));
            }
        }
        else
        {
           showErrorMessage(ErrorCode.OBJECT_READ_ERROR);
        }




    }

	public void pwd() throws Exception {

		if (tokens.size() != 1) {

		showErrorMessage(ErrorCode.MISC);
			return ;
		}



		//send command
		objectOutputStream.writeObject("pwd");


		//message
		Object o=objectInputStream.readObject();

		if(o instanceof InfoData)
		{
		    InfoData temp=(InfoData)o;
            if(!temp.getInformation().equals("")) {
                updateLog("Current Dir :" + currentPath);
                System.out.println("Current Dir :" + temp.getInformation() + " should be same as " + "ServerPath :" + currentPath);
            }
        }
        else
            {
                showErrorMessage(ErrorCode.OBJECT_READ_ERROR);
            }



	}
    public void showErrorMessage(ErrorCode errorCode)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                main.invalid(errorCode);
            }
        });
    }
	public void bye() throws Exception {
		//only one argument
		if (tokens.size() != 1) {

		showErrorMessage(ErrorCode.MISC);
			return;
		}

		if (!ftpClient.quit()) {
			System.out.println("error: Transfers in progress");
			return;
		}
		updateLog("Good Bye! Have A Nice Day");

		//send command
		objectOutputStream.writeObject("bye");


		Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    main.showLoginPage();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }





	public void help() {


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                main.help();
            }
        });

	}

	public void task() {



		try {



			//TODO:Send The username Password to server using outputStream
            //dataOutputStream.writeObject("true" + "\n");
 			objectOutputStream.writeObject(userName);
			objectOutputStream.writeObject(passWord);

			Object o=objectInputStream.readObject();
			String temp="";
			if(o instanceof String)
			{
                temp=(String)o;

            }
			if(temp.equals("false"))
			{
                System.out.println("Client Found:"+temp);

                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        //Update UI here
                        try {
                            main.showClientPage(false,queue,treeViewMap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
				return;
			}
			else if(temp.equals("true"))
			{
                initialiseTreeViewMap();

                System.out.println("Client Found:"+temp);

                Platform.runLater(new Runnable() {

                    @Override public void run() {
						try {
                            main.showClientPage(true,queue,treeViewMap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }


			//keyboard task
			Scanner input = new Scanner(System.in);
			String command="" ;
            updateLog("File Transfer Tool : Created By Tasin Ishmam and Ashiqur Rahman");
			updateLog("Welcome : "+ userName);
			updateLog("Connected to : "+hostname);
			updateLog("For Instructions Send 'help' ");
            updateLog("Initialising Server Directory ...");



            do {
				//get task



				command = String.valueOf(queue.take());

                updateLog("Command Taken :"+command);


				tokens = new Vector<String>();
				Scanner tokenize = new Scanner(command);

				if (tokenize.hasNext())
					tokens.add(tokenize.next());

				if (tokenize.hasNext())
					tokens.add(command.substring(tokens.get(0).length()).trim());

				for (String s:
						tokens) {
					System.out.println(s);
				}
				tokenize.close();

				if (tokens.isEmpty()) continue;


				switch(tokens.get(0)) {
					case "get": 		get(); 			break;
					case "put": 		put(); 			break;
					case "delete": 		delete(); 		break;
					case "ls": 			ls(); 	        break;
					case "cd": 			cd(tokens); 	break;
					case "mkdir": 		mkdir(); 		break;
					case "pwd": 		pwd(); 			break;
					case "bye": 		bye(); 		break;
					case "help": 		help(); 		break;
                    case "refreshGui":  refreshGui();   break;
					default:
						System.out.println("unrecognized command '" + tokens.get(0) + "'");
						System.out.println("Try `help' for more information.");
				}


				if (FtpClient.DEBUGGER) System.out.println(tokens);


				//allows for blank enter

			} while (!command.equalsIgnoreCase("bye"));
			input.close();

		} catch (Exception e) {
			System.out.println(e);
			if (FtpClient.DEBUGGER) e.printStackTrace();
		}
	}
	
	public void run() {
		task();
	}
}
