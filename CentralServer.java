import java.io.*;
import java.net.*;
import java.util.*;

import java.util.List;
import java.util.regex.*;

class CentralServer {
    private static Map<String, Map<String, String>> word = new LinkedHashMap<>();
    private static int clientNumber = 1;
    public static void main(String argv[]) throws Exception {

        int port = 1200;
        ServerSocket welcomeSocket = new ServerSocket(port, 0, InetAddress.getLoopbackAddress());
        System.out.println("Central Server running at " + welcomeSocket.getInetAddress() + ":" + port);
        while(true) {
            Socket connectionSocket = null;
            try {
                connectionSocket = welcomeSocket.accept();
                DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                System.out.println("Victim at: " + connectionSocket.getPort());
                new Thread(new SubServerHandler(connectionSocket, outToClient, inFromClient)).start();
            } catch(Exception e) {
                connectionSocket.close();
                e.printStackTrace();
            }
        }
    }
    /*
    //adds to hashmap from client
    public static void createword(String user,String host, String addr,String conn,String port){
        Map<String,String> client = new LinkedHashMap<>();
        client.put("username",user);
        client.put("hostname",host);
        client.put("address",addr);
        client.put("connection",conn);
        client.put("port",port);
        word.put("client_" + String.valueOf(CentralServer.clientNumber), client);
        
    }
    */
    
    //records who is in the database currently
    private static void sendToFile() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream("dbFiles.txt"));
        if (!word.isEmpty()){
            word.entrySet().forEach(entry->{
                pw.println((entry.getKey() + "List of Entries: \n"));
                entry.getValue().entrySet().forEach(subEntry->{
                    pw.println((subEntry.getKey() + " = " + subEntry.getValue()));
                });
            });
        } else {
            pw.println(("no files"));
        }
        pw.close();
    }
    
    
    //adds files to our hash
    public static void addFilesToword(String files) {
        String[] split = files.split("\0");
        Pattern pattern = Pattern.compile("(.*\\.\\w{1,}) (.*)");
        Map<String, String> cData = new LinkedHashMap<>();
        for (String file : split) {
            Matcher matcher = pattern.matcher(file);
            String s1 = "";
            String s2 = "";
            while(matcher.find()){
                s1 = matcher.group(1);
                s2 = matcher.group(2);
            }
            if (!s1.equals("") && !s2.equals("")){
                cData.put(s1, s2);
            }
        }
        cData.entrySet().forEach(entry->{
            word.get("client_" + String.valueOf(CentralServer.clientNumber)).put(entry.getKey(), entry.getValue());
        });
    
        CentralServer.clientNumber++;
        try {
            CentralServer.sendToFile();
        } catch(FileNotFoundException f) {

        }

    }
    
  /*
    //deregisters user when disconnect
    public static void deregister(String port){
        ArrayList<String> pNum = new ArrayList<String>();
        word.entrySet().forEach(entry->{
            entry.getValue().entrySet().forEach(subEntry->{
                if (entry.getValue().containsValue(port)){
                    pNum.add(entry.getKey());
                }
            });
        });
        if (pNum.size() > 0){
            word.remove(pNum.get(0));
        }
        try{
            CentralServer.sendToFile();
        }catch(FileNotFoundException f){

        }
    }
    */
/*
    //reformats the string, splitting by null
    private static String reformat(String fileString){
        String[] clients = fileString.split(", client");
        String reformattedFiles = "";
        String splitter = "\0";
        Pattern checkMatch = Pattern.compile(".*(\\d{1,})=\\[(.*)\\]");
        for (String client : clients) {
            Matcher matcher = checkMatch.matcher(client);
            String s1 = "";
            String s2 = "";
            while(matcher.find()){
                s1 = matcher.group(1);
                s2 = matcher.group(2);
            }
            String[] splitComma = s2.split(",");
            String newFile = String.join("", splitComma);
            reformattedFiles += "client_" + s1 + " " + newFile + splitter;
        }
        return reformattedFiles;
    }
*/
}

class SubServerHandler implements Runnable {

    private Socket socket;
    private DataOutputStream outToClient;
    private BufferedReader inFromClient;

    public SubServerHandler(Socket socket, DataOutputStream out, BufferedReader in) {
        this.socket = socket;
        this.outToClient = out;
        this.inFromClient = in;
    }

    //runs the central server
    @Override
    public void run() {
        String fromClient = "";
        String clientCommand;
        byte[] data;
        String frstln;
        String[] commands;
        int port = this.socket.getPort();
        String username="",hostname,address,connection;
        while (true) {
            try {

                try {
                    fromClient = inFromClient.readLine();
                } catch(IOException io) {
                    System.out.println("client disconnected");
                    break;
                }

                System.out.println("Received command: " + fromClient);
                commands = fromClient.split(" ");
                clientCommand = (commands[0]);
                //dereg users
                if (clientCommand.equals("close")) {
                    int portg = Integer.parseInt(commands[1]);
                    System.out.println("Connection ended with victim " + this.socket.getPort());
                 //   CentralServer.deregister(String.valueOf(portg));
                    outToClient.close();
                    this.socket.close();
                    break;
                } else {
                    //new connection, add information to the hashmap to store
                    if (clientCommand.startsWith("newuser:")) {
                        username = commands[1];
                        port= Integer.parseInt(commands[2]);
                     //   CentralServer.createword(username,hostname,address,connection,String.valueOf(port));
                        System.out.println("connected to "+ username);
                    }
                    //recieve the file to add info
                    if(clientCommand.startsWith("fileSend")){

                        String fileString = fromClient.replaceAll(
                            "fileSend " + commands[1] + " " + commands[2] + " ", 
                            "");
                        CentralServer.addFilesToword(fileString);
                    }
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

