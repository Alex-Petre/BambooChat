package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ServerChat {    
    private final int PORT;
    private final String GET_USERS_PATTERN = "***@#$%^(&^%$@***";
    private Set<UserThread> userThreads = new HashSet<>();
    private Set<String> users = new HashSet<>();

    public ServerChat(int port) {
        this.PORT = port;
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server is listening on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerChat server = new ServerChat(8981);
        server.execute();
    }

    public void broadcast(String message) {
        for (UserThread user : userThreads) {
            user.sendMessage(message);
        }
    }

    public void updateUsersOnline(){
        broadcast(getUsersName());
    }
    
    public void addUser(String user) {
        users.add(user);
        updateUsersOnline();
    }
    
    public void removeUser(String user, UserThread userThread){
        users.remove(user);
        userThreads.remove(userThread);
        updateUsersOnline();
    }
    
   public String getUsersName() {
       return GET_USERS_PATTERN + "," + String.join(",", users);        
    }
 
    public boolean usersExist() {
        return !this.users.isEmpty();
    }
}
