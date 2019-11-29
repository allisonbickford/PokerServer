package gui;

import javax.swing.*;
import java.awt.*;

import java.awt.event.*;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;

import server.ClientSession;
import server.PlayersObservable;
import game.*;

public class GUI extends JFrame implements ActionListener, Observer {
    JPanel panel = new JPanel();
    JPanel regPanel = new JPanel();
    JPanel lobbyPanel = new JPanel();
    private ClientSession clientSession;
    private JLabel cHostName, cPort, username,regText;
    private JTextField cHostN_field,cPort_field,username_field;
    private JButton connect;
    JFrame frame;
    PlayersObservable players = null;

    public GUI() {
        super("Poker");
       // this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeRegGUI();
        //this.setVisible(true);
    }

    public void initializeGameGUI() {
        frame.getContentPane().removeAll();
        this.panel.setBackground(new Color(12, 107, 17));
        this.panel.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        Deck deck = new Deck();
        ArrayList<Entry<String, String>> playerInfo = this.players.getPlayers();
        int length = playerInfo.size() / 2; // figure out how to layout a circle T_T
        int x = 0;
        int y = 0;
        boolean firstRow = true; 
        // TODO: figure out how to put current player's panel at a specific position
        for (int i = 0; i < playerInfo.size(); i++) {
            boolean isMyPanel = playerInfo.get(i).getKey().contains(this.clientSession.getHostName());
            PlayerPane tmp = new PlayerPane(isMyPanel, deck.draw(), deck.draw()); // TODO: don't draw for everyone, central deck?
            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = x;
            cons.gridy = y;
            cons.anchor = GridBagConstraints.PAGE_END;
            cons.gridwidth = 2;
            this.panel.add(tmp, cons);
            if (y >= length * 3) {
                x += 3;
            } else if (firstRow) {
                y += 3;
            }
            if (!firstRow) {
                y -= 3;
            }
        }
        
        this.panel.setPreferredSize(new Dimension(640, 480));
        frame.add(this.panel);
        frame.setBounds(200, 200, 640, 480);
        frame.setBackground(new Color(12, 107, 17));
        frame.pack();
    }

    public void initializeRegGUI() {
        //initialize fields
        cHostN_field = new JTextField("localhost", 20);
        cPort_field = new JTextField("1200", 5);
        username_field = new JTextField(20);
        //initialize labels
        cHostName = new JLabel("Central Server Hostname: ");
        cPort = new JLabel("Central Server Port Number: ");
        username = new JLabel("Username");
        regText = new JLabel("Register into a Central Server");
        //initialize buttons
        connect = new JButton("Connect");

        //this.regPanel.setBackground(new Color(12, 107, 17));
        this.regPanel.setLayout(new GridBagLayout());
        GridBagConstraints regCons = new GridBagConstraints();
        frame = new JFrame();
        frame.setLayout(new GridBagLayout());
    // frame.setBackground(new Color(12, 107, 17));
        regCons.fill = GridBagConstraints.HORIZONTAL;
        regCons.gridx = 1;
        regCons.gridy = 1;
        regCons.anchor = GridBagConstraints.PAGE_END;
        regCons.gridwidth = 1;
        this.regPanel.add(regText, regCons);

        regCons.gridx = 1;
        regCons.gridy=4;
        this.regPanel.add(cHostName, regCons);

        regCons.gridx = 1;
        regCons.gridy=5;
        this.regPanel.add(cHostN_field, regCons);

        regCons.gridx = 1;
        regCons.gridy = 7;
        this.regPanel.add(cPort, regCons);

        regCons.gridx = 1;
        regCons.gridy = 8;
        this.regPanel.add(cPort_field, regCons);

        regCons.gridx = 1;
        regCons.gridy = 10;
        this.regPanel.add(username, regCons);

        regCons.gridx = 1;
        regCons.gridy = 11;
        this.regPanel.add(username_field, regCons);

        regCons.gridx = 1;
        regCons.gridy = 13;
        this.regPanel.add(connect, regCons);

        this.add(this.regPanel);
        this.setBounds(200, 200, 640, 480);
    //    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //  this.setVisible(true);
        
        frame.add(this.regPanel,regCons);
        frame.setTitle("Project 3");
        frame.pack();
        frame.setSize(250, 250);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        connect.addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent e) {
        JComponent pressed = (JComponent) e.getSource();
        //if connect run gui and create a Host and central server
        if (pressed == this.connect) {
            if(this.connect.getText() == "Connect"){
                if (this.cHostN_field.getText().isEmpty() ||
                    this.cPort_field.getText().isEmpty() ||
                    this.username_field.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(
                        new JFrame(), 
                        "Please make sure you've filled in both hostnames and your username.",
                        "Invalid Inputs", JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    try {
                        this.clientSession = new ClientSession(
                            this.username_field.getText(),
                            this.cHostN_field.getText()
                        );
                        this.players = this.clientSession.getObservable();
                        this.players.addObserver(this);
                        this.cHostN_field.setEditable(false);
                        this.cPort_field.setEditable(false);
                        this.username_field.setEditable(false);
                        this.connect.setText("Disconnect");
                        initializeLobbyGUI();
                    } catch (UnknownHostException uhe) {
                        JOptionPane.showMessageDialog(
                            new JFrame(), 
                            "Please check your server hostname.",
                            "Invalid Host", JOptionPane.ERROR_MESSAGE
                        );
                    }
                    System.out.println("Connected");   
                }
            } else {
                this.clientSession.close();
                this.cHostN_field.setEditable(true);
                this.cPort_field.setEditable(true);
                this.username_field.setEditable(true);
                this.connect.setText("Connect");
            }
        }
    }

    private void initializeLobbyGUI() {
        this.lobbyPanel.setBackground(new Color(12, 107, 17));
        frame.getContentPane().removeAll();
        frame.setTitle("Poker Lobby");
        
        JButton startButton = new JButton("Start Game!");
        startButton.addActionListener(e -> {
            this.clientSession.startGame();
            initializeGameGUI();
        });
        this.lobbyPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        this.lobbyPanel.setPreferredSize(new Dimension(640, 480));
        JPanel playerLobby = playerLobby();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        this.lobbyPanel.add(playerLobby, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.PAGE_END;
        this.lobbyPanel.add(startButton, gbc);

        frame.add(this.lobbyPanel);
        frame.invalidate();
        frame.validate();
        frame.repaint();
        frame.pack();
    }

    private JPanel playerLobby() {
        ArrayList<Entry<String, String>> playersList = this.players.getPlayers();

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255, 80));
        panel.setPreferredSize(new Dimension(200, 200));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Players:"));
        for (Entry<String, String> player: playersList) {
            if (player.getKey().contains(this.clientSession.getHostName())) {
                panel.add(new JLabel(player.getValue() + " <-- You!"));
            } else {
                panel.add(new JLabel(player.getValue()));
            }
        }
        return panel;
    }
    
    @Override
    public void update(Observable o, Object arg) {
        this.players = ((PlayersObservable) o);
        if (arg instanceof ArrayList) {
            this.lobbyPanel.remove(0);
            JPanel playerLobby = playerLobby();
            this.lobbyPanel.add(playerLobby(), 0);
            frame.invalidate();
            frame.validate();
            frame.repaint();
        } else if (arg instanceof Entry) {
            if (!this.panel.isDisplayable()) {
                initializeGameGUI();
            }
            System.out.println("Currently " + this.players.getTurn().getValue() + "'s Turn");
        }
    }
}