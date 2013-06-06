package router;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import util.Disk;
import util.PropertiesFile;

public class Router {

    public static void main(String[] args) {
    	//Initialize properties
    	PropertiesFile.getProperties();

        ServerSocket clientSocket = null;
        int clientPort = Integer.parseInt(PropertiesFile.getProperty(PropertiesFile.PORT_ATM, "34002"));
        try {
        	clientSocket = new ServerSocket(clientPort);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + clientPort);
            System.exit(-1);
        }

        ServerSocket serverSocket = null;
    	int bankPort = Integer.parseInt(PropertiesFile.getProperty(PropertiesFile.PORT_BANK, "34001"));
    	try {
            serverSocket = new ServerSocket(bankPort);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + bankPort);
            System.exit(1);
        }

        Socket bankSocket = null;
        try {
            bankSocket = serverSocket.accept();
            serverSocket.close();
            System.out.println("Bank connected to the router.");
        } catch (IOException e) {
            System.err.println("Bank Socket Accept() failed.");
            System.exit(1);
        }

        Socket atmSocket = null;
        try {
            atmSocket = clientSocket.accept();
            clientSocket.close();
            System.out.println("ATM connected to the router.");
        } catch (IOException e) {
            System.err.println("ATM Socket Accept() failed.");
            System.exit(1);
        }

        byte buf[];
        int len;

        InputStream atmSocketInputStream = null;
        OutputStream atmSocketOutputStream = null;
        InputStream bankSocketInputStream = null;
        OutputStream bankSocketOutputStream = null;

        try {
            atmSocketInputStream = atmSocket.getInputStream();
            atmSocketOutputStream = atmSocket.getOutputStream();
            bankSocketInputStream = bankSocket.getInputStream();
            bankSocketOutputStream = bankSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (atmSocket.isClosed() || bankSocket.isClosed()) {
                    System.exit(0);
                }

                if ((len = bankSocketInputStream.available()) > 0) {
                	buf = new byte[len];
                    bankSocketInputStream.read(buf,0,len);
                    atmSocketOutputStream.write(buf,0,len);
                    System.out.println("sent " + len + " bytes from Bank to ATM");
                }

                if ((len = atmSocketInputStream.available()) > 0) {
                	buf = new byte[len];
                    atmSocketInputStream.read(buf,0,len);

                    // First attempt at cracking the communications
                    Disk.save(buf, "transferredSerializedObject.file");

                    bankSocketOutputStream.write(buf,0,len);
                    bankSocketOutputStream.flush();
                    System.out.println("sent " + len + " bytes from ATM to Bank");
                }
            } catch (IOException e) {
                // Connection may have been closed
                System.out.println("The ATM or Bank connection to the router may have been closed");
            }
        }
    }
}
