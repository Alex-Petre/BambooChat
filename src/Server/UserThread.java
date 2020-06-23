package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserThread extends Thread {
    private final String EXIT_PATTERN = "***&^!@#***";
    private final String GET_USERS_PATTERN = "***@#$%^(&^%$@***";

    private Socket socket;
    private ServerChat server;
    private PrintWriter writer;

    public UserThread(Socket socket, ServerChat server) {
        this.socket = socket;        
        this.server = server;
    }

    @Override
    public void run() {
        try {           
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            String name = reader.readLine();
            
            server.addUser(name);
            server.broadcast("User " + name + " has connected");

            String clientMessage;                       
                        
            while(true) {
                clientMessage = reader.readLine();

                if(clientMessage.contentEquals(EXIT_PATTERN)){
                    break;
                }
                
                if(clientMessage.contentEquals(GET_USERS_PATTERN)){
                    String responseWithUsers = server.getUsersName();                                                            
                    this.sendMessage(responseWithUsers);                    
                    continue;
                }
                
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime timeNow = LocalDateTime.now();

                String serverMessage = dateTimeFormatter.format(timeNow) + " " + name + ": " + clientMessage;
                server.broadcast(serverMessage);
            }
            
            socket.close();
            server.removeUser(name, this);
            
            server.broadcast("User " + name + " has disconnected");

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}
