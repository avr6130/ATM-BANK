package original;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Router {

    final static int BUF_LENGTH = 4096;

    public static void main(String[] args) {
    	//Initialize properties
    	PropertiesFile.getProperties();
    	
        if (args.length < 2) {
            System.out.println("Usage: java Router <Bank port> <ATM port>");
            System.exit(1);
        }

        ServerSocket clientSocket = null;
        try {
            clientSocket = new ServerSocket(Integer.parseInt(args[1]));
        } catch (IOException e) {
            System.err.println("Could not listen on port: "+args[1]);
            System.exit(1);
        }

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        } catch (IOException e) {
            System.err.println("Could not listen on port: "+args[0]);
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
