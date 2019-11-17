import java.io.*;
import java.net.*;
import java.util.*;

class Server implements Runnable {
    public int port;
    public String hostname;
    public String userName = "";

    public Server(String hostname, int port, String userName) {
        this.hostname = hostname;
        this.port = port;
        this.userName = userName;
    }

    public void run() {
        try{
            ServerSocket welcomeSocket = new ServerSocket(this.port, 0, InetAddress.getLoopbackAddress());
            System.out.println("Listening at " + welcomeSocket.getInetAddress() + ":" + port);
            while(true) {
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Victim at: " + connectionSocket.getPort());
                new Thread(new ClientHandler(connectionSocket, userName)).start();
            }
        } catch(Exception e) {
             e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private String userName = "";

    public ClientHandler(Socket socket, String userName) {
        this.socket = socket;
        this.userName = userName;
    }

    @Override
    public void run() {
        String fromClient;
        String clientCommand;
        String frstln;
        int port = this.socket.getPort();

        while (true) {
            try {
                DataOutputStream  outToClient = new DataOutputStream(this.socket.getOutputStream());
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

                fromClient = inFromClient.readLine();
                System.out.println(fromClient);

                StringTokenizer tokens = new StringTokenizer(fromClient);
                frstln = tokens.nextToken();
                port = Integer.parseInt(frstln);
                clientCommand = tokens.nextToken();

                if (clientCommand.equals("close")) {
                    System.out.println("Connection ended with victim " + this.socket.getPort());
                    this.socket.close();
                    break;
                }  else {
                    Socket dataSocket = new Socket(this.socket.getInetAddress(), port);
                    DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
                    dataSocket.close();
                    System.out.println("Data Socket closed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("There was a problem, " +
                    "so we are closing the connection to victim " + this.socket.getPort());
                try {
                    this.socket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }

}