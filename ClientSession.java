import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

public class ClientSession {
    private int port;
    private String hostname;
    private Socket centralSocket;
    private Socket controlSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private ArrayList<ArrayList<String>> filesFound = new ArrayList<>();


    public ClientSession(String username, String hostname) throws UnknownHostException {
        try {
            //connects to central server
            this.centralSocket = new Socket(hostname, 1200);
            this.port = this.centralSocket.getLocalPort();
            String connMessage = "newuser: " + username + " " + this.port + "\n";
            System.out.println("Registering with central: " + connMessage);
            DataOutputStream centralDOS = new DataOutputStream(this.centralSocket.getOutputStream());
            centralDOS.writeBytes(connMessage);
            new Thread(
                this.server = new Server(this.centralSocket.getLocalAddress().getHostAddress(), this.port, username)
            ).start();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            if (e instanceof UnknownHostException) {
                throw new UnknownHostException(e.getMessage());
            }
            e.printStackTrace();
            System.out.println("Wow that sucked");
        }
    }
    
    //closes client session
    public void close() {
        if (this.controlSocket != null) {
            try {
                this.port += 2;
                String closeCommand = this.port + " close \n";
                
                
                this.out.writeBytes(closeCommand);
                this.controlSocket.close();
                this.controlSocket = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }try{
        DataOutputStream centralDOS = new DataOutputStream(this.centralSocket.getOutputStream());
        centralDOS.writeBytes("close" + this.port+"\n");
        }catch(IOException d){
            d.printStackTrace();
        }
    }

    public boolean isConnected() {
        return this.controlSocket != null;
    }

    public Socket getSocket() {
        return this.controlSocket;
    }

    public DataOutputStream getOutputStream() {
        return this.out;
    }

    public DataInputStream getInputStream() {
        return this.in;
    }
}
