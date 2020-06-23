package Client;

import java.io.*;
import java.net.*;

public class ReadThread extends Thread {
    private final String GET_USERS_PATTERN = "***@#$%^(&^%$@***";

    private BufferedReader reader;
    private Socket socket;
    private ClientChat client;

    public ReadThread(Socket socket, ClientChat client) {
        this.socket = socket;
        this.client = client;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("An error occurred while trying to take input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
        
         while (true) {
            try {
                String response = reader.readLine();             
                
                if (response.contains(GET_USERS_PATTERN)){
                    response = response.substring(GET_USERS_PATTERN.length());
                    String[] users = response.split(","); 
                    client.gui.updateUsers(users);
                    continue;
                }
                client.gui.setMsgArea(response);                
            } catch (SocketException sx) {
                // socket is closed, so it's ok
                break;
            } catch (IOException ex) {
                System.out.println("Server read error: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }
}
