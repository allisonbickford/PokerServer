package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

public class ClientSession {
    private int port;
    private String address;
    private Socket centralSocket;
    private Socket controlSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;


    public ClientSession(String username, String centralAddress) throws UnknownHostException {
        try {
            //connects to central server
            this.centralSocket = new Socket(centralAddress, 1200);
            this.address = InetAddress.getLocalHost().getHostAddress();
            this.port = this.centralSocket.getLocalPort();
            String connMessage = String.format("newuser: %s %s:%d \n", username, this.address, this.port);
            System.out.println("Registering with central: " + connMessage);
            DataOutputStream centralDOS = new DataOutputStream(this.centralSocket.getOutputStream());
            centralDOS.writeBytes(connMessage);
            new Thread(
                this.server = new Server(this.port, username)
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


    public void startGame() {
        try {
            DataOutputStream centralDOS = new DataOutputStream(this.centralSocket.getOutputStream());
            centralDOS.writeUTF(String.format("start %s:%d", this.address, this.port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PlayersObservable getObservable() {
        return this.server.getObservable();
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
        } try {
            DataOutputStream centralDOS = new DataOutputStream(this.centralSocket.getOutputStream());
            centralDOS.writeBytes("close " + this.address + ":" + this.port + "\n");
        } catch(IOException d){
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
