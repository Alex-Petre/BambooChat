package Client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientChat {

    private final String EXIT_PATTERN = "***&^!@#***";
    private final String GET_USERS_PATTERN = "***@#$%^(&^%$@***";

    private PrintWriter writer;
    private Socket socket;
    private String hostname;
    private int port;

    public guiClient gui;
    
    public ClientChat(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public ClientChat(guiClient gui , String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.gui = gui;
    }

    public void execute() {
        try {
            socket = new Socket(hostname, port);
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            ReadThread readThread = new ReadThread(socket, this);
            readThread.start();
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void getUsers(){      
        writer.println(GET_USERS_PATTERN);
    }
    
    public boolean closeSocket() {
        boolean result = false;

        try {
            sendMessage(EXIT_PATTERN);
            socket.close();
            result = true;
        } catch (IOException ex) {
            result = false;
        }

        return result;
    }
}