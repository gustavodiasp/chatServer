import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection implements Runnable {

    private ChatServer chatServer;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String userName;

    public ClientConnection(Socket clientSocket, ChatServer chatServer) throws IOException {
        this.clientSocket = clientSocket;
        this.chatServer = chatServer;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public PrintWriter getOut() {
        return out;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public void run() {
        out.println("What's your USER NAME: ");
        try {
            this.userName = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                String message = in.readLine();


                if (message.equals("/userList")) {
                    chatServer.getUserList(this);
                    continue;
                }

                chatServer.broadcast(message, this);
//Todo When window close it should close clientSocket and execute quit
                if (message.equals("/quit")) {
                    System.out.println(getUserName() + " disconnected");
                    clientSocket.close();
                    chatServer.remove(this);
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String getIn(String fromServer) {
        out.println(fromServer);
        return fromServer;
    }
}
