import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {


    private static final int DEFAULT_PORT = 9095;
    // To use connect nc localhost 9095 on command line.
    private ExecutorService cachedPoolThread = Executors.newCachedThreadPool();
    private ServerSocket serverSocket;
    private final List<ClientConnection> clientList = Collections.synchronizedList(new LinkedList<>());

    public ChatServer() {
        try {
            this.serverSocket = new ServerSocket(DEFAULT_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {

        while (true) {
            try {

                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                ClientConnection clientConnection = new ClientConnection(clientSocket, this);
                cachedPoolThread.submit(clientConnection);
                clientList.add(clientConnection);


            } catch (IOException e) {
                e.printStackTrace();

            }
        }

    }


    public void broadcast(String message, ClientConnection sender) throws IOException {
        for (ClientConnection clientConnection : clientList) {
            if (!(clientConnection == sender)) {
                if (message.equals("/quit")) {
                    message = "left";
                    clientConnection.getOut().println(sender.getUserName() + " " + message);

                } else {
                    clientConnection.getOut().println(sender.getUserName() + " says: " + message);
                }

            }
        }
    }

    public void remove(ClientConnection client) {
        for (ClientConnection clientConnection : clientList) {
            if (clientConnection == client) {
                clientList.remove(client);
            }
        }
    }

    public String getUserList(ClientConnection sender) {
        StringBuilder userList = new StringBuilder();
        for (ClientConnection clientConnection : clientList) {

            userList.append(clientConnection.getUserName() + "\r\n");

        }
        return sender.getIn(userList.toString());
    }
}

