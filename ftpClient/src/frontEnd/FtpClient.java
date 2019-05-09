package backEnd;

import frontEnd.ErrorCode;
import frontEnd.Main;
import javafx.application.Platform;

import java.net.InetAddress;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class FtpClient {

    public static final boolean DEBUGGER = true;///set 'true' for commands debug

    public static int port;

    public static String hostname;

    private Set<Path> transferSet;

	
	public FtpClient() {
		transferSet = new HashSet<Path>();

	}
	
	public synchronized boolean transfer(Path path) {
		return !transferSet.contains(path);
	}
	
	public synchronized void transferIN(Path path) {

	    ///Adds a new command to the transfer set
	    transferSet.add(path);


	}



	public synchronized void transferOUT(Path path) {
		try {

			transferSet.remove(path);

		} catch(Exception e) {}
	}
	
	public synchronized boolean quit() {///returns true if no file-transfer is on process
		return transferSet.isEmpty();
	}


    public static void backEnd(String[] loginData, Main main) {

        System.out.println("Started Backend Main");
        if (loginData.length != 5) {
            main.invalid(ErrorCode.MISC);
            System.out.println("error: Invalid number of arguments");
            return;
        }

        //hostname
        try {
            InetAddress.getByName(loginData[0]);
            hostname = loginData[0];
        } catch (Exception e) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    main.invalid(ErrorCode.NETWORKERROR);

                }
            });
            System.out.println("hostname does not resolve to an IP address");
            return;
        }



        try {
            port = Integer.parseInt(loginData[1]);
            if ( port < 1 || port > 65535 ) throw new Exception();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid port ");
            main.invalid(ErrorCode.NETWORKERROR);
            return;
        } catch (Exception e) {
            System.out.println("error: Invalid port range");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    main.invalid(ErrorCode.NETWORKERROR);
                }
            });
            return;
        }



        try {
            //shared memory object
            FtpClient ftpClient = new FtpClient();

            System.out.println("Before COnnection");
            //initial starting thread
            ( new Thread(new FtpClientNormalThread(ftpClient, loginData, main)) ).start();
        } catch (Exception e) {
            System.out.println("unexpectedly");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    main.invalid(ErrorCode.NETWORKERROR);
                }
            });

        }
    }


}
