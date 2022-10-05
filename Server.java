import java.io.*;
import java.net.*;

/**
 * Server.java
 *
 * This server class handles different client threads.
 *
 * @author Henry Merchant, lab sec 13180-L19
 *
 * @version April 30, 2022
 *
 */

public class Server {

    public static void main(String[] args) {
        ServerSocket server = null;

        try {
            //server is listening
            server = new ServerSocket(6969);
            server.setReuseAddress(true);
            System.out.println("running...");
            //running infinite loop for getting client requests
            while (true) {
                //socket object to receive incoming client requests
                Socket client = server.accept();
                //Displaying that new client is connected to server
                System.out.println("New client connected: " + client.getInetAddress().getHostAddress());
                //create a new thread object
                ClientHandler clientSock = new ClientHandler(client);
                //This thread will handle the client separately
                new Thread(clientSock).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
