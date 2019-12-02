package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.awt.*;

import java.awt.event.*;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;

import server.ClientSession;
import server.Player;
import server.PlayersObservable;
import game.*;

public class GUI extends JFrame implements ActionListener, Observer  {
    JPanel gamePanel;
    JPanel regPanel = new JPanel();
    JPanel lobbyPanel = new JPanel();
    private ClientSession clientSession;
    private JLabel cHostName, cPort, username,regText;
    private JTextField cHostN_field,cPort_field,username_field;
    private JButton connect;
    private JTable playersTable;
    private JLabel potLabel;
    private PlayerPane myPanel;
    JFrame frame;
    PlayersObservable observable = null;
    private String myName = "";

    public GUI() {
        super("Poker");
        initializeRegGUI();
    }

    public void initializeGameGUI() throws InterruptedException{
        frame.getContentPane().removeAll();
        this.gamePanel = new JPanel();
        this.gamePanel.setBackground(new Color(12, 107, 17));
        this.gamePanel.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
     //   Deck deck = new Deck();
        ArrayList<Player> playerInfo = this.observable.getPlayers();

        //System.out.println("here1" + playerInfo);
/*
        for (Player player: playerInfo) {
            System.out.println(player.getRole());
            if (player.getRole().contains("D")&& player.getHostName().contains(this.clientSession.getHostName())) {
                System.out.println("here");
                Deck deck = new Deck();
                for (Player players : playerInfo) {
                    if (players.getHostName().contains(this.clientSession.getHostName())) {
                        continue;
                    }
                    first = deck.draw();
                    second = deck.draw();
                    this.clientSession.sendDeckMessage(first.rank(), first.suit(), second.rank(), second.suit(), players.getHostName());
                }
                Card dfirst = deck.draw();
                Card dsecond = deck.draw();
                for (int i = 0; i < playerInfo.size(); i++) {
                    if (playerInfo.get(i).getHostName().equals(this.clientSession.getHostName())) {
                        playerInfo.get(i).setCards(dfirst, dsecond);
                        break;
                    }
                }
            }
        }
        
        PlayersTableModel ptm = new PlayersTableModel(playerInfo);
        this.playersTable = new JTable(ptm);
        playersTable.setCellSelectionEnabled(false);
        playersTable.setDefaultRenderer(CardCellRenderer.class, new CardCellRenderer());
        playersTable.setPreferredScrollableViewportSize(new Dimension(600, 300));
        playersTable.setRowHeight(75);
        JScrollPane playersPane = new JScrollPane(this.playersTable);
        playersPane.setPreferredSize(new Dimension(600, 300));
        
        cons.gridy = 0;
        cons.gridheight = 3;
        cons.gridwidth = 3;
        this.gamePanel.add(playersPane, cons);
        
        cons.gridy = 4;
        cons.gridx = 0;
        cons.gridheight = 1;
        cons.gridwidth = 1;
        cons.anchor = GridBagConstraints.PAGE_START;
        this.gamePanel.add(new JLabel("Me: " + this.myName), cons);
        
        this.potLabel = new JLabel("Pot: $0");
        this.potLabel.setOpaque(true);
        this.potLabel.setBackground(new Color(255, 255, 255));
        cons.gridx = 2;
        cons.gridy = 4;
        cons.anchor = GridBagConstraints.PAGE_END;
        this.gamePanel.add(potLabel, cons);
        for (Player players: playerInfo) {
            if(players.getHostName().contains(this.clientSession.getHostName())) {
                Card[] hold = players.getCards();
                Thread.sleep(20);
                myPanel = new PlayerPane(hold[0],hold[1], this.clientSession);
            }
        }
         // TODO: don't draw for everyone, central deck?
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 0;
        cons.gridy = 5;
        cons.anchor = GridBagConstraints.PAGE_END;
        cons.gridwidth = 3;
        this.gamePanel.add(myPanel, cons);
        
        this.gamePanel.setPreferredSize(new Dimension(640, 480));
        frame.add(this.gamePanel);
        frame.setBounds(200, 200, 640, 480);
        frame.setBackground(new Color(12, 107, 17));
        frame.pack();
        frame.setVisible(true);

        for (int i = 0; i < playerInfo.size(); i++) {
            if (playerInfo.get(i).getHostName().equals(this.clientSession.getHostName())) {
                if (!playerInfo.get(i).isTurn()) {
                    myPanel.stop();
                } else {
                    myPanel.actionAfterBet();
                    myPanel.play();
                }
                this.observable.getPlayers().get(i).showCards();
                break;
            }
        }
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
                        this.myName = this.username_field.getText();
                        this.clientSession = new ClientSession(
                            this.username_field.getText(),
                            this.cHostN_field.getText()
                        );
                        this.observable = this.clientSession.getObservable();
                        this.observable.addObserver(this);
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
            try{
            initializeGameGUI();
            }catch(InterruptedException de){
                de.printStackTrace();
            }
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
        ArrayList<Player> playersList = this.observable.getPlayers();

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255, 80));
        panel.setPreferredSize(new Dimension(200, 200));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Players:"));
        for (Player player: playersList) {
            if (player.getHostName().contains(this.clientSession.getHostName())) {
                panel.add(new JLabel(player.getName() + " <-- You!"));
            } else {
                panel.add(new JLabel(player.getName()));
            }
        }
        return panel;
    }
    
    @Override
    public void update(Observable o, Object arg) {
        this.observable = ((PlayersObservable) o);
        if (arg.toString().equals("players")) { // players change
            this.lobbyPanel.remove(0);
            JPanel playerLobby = playerLobby();
            this.lobbyPanel.add(playerLobby(), 0);
            frame.invalidate();
            frame.validate();
            frame.repaint();
        } else if (arg.toString().equals("host")) { // game host changes
            if (this.gamePanel == null) {
                try{
                    ArrayList<Player> playerInfo = this.observable.getPlayers();
                    for (Player player: playerInfo) {
                        if (player.getRole().contains("BB")&& player.getHostName().contains(this.clientSession.getHostName())) {
                            Deck deck = new Deck();
                            for (Player players : playerInfo) {
                                if (players.getHostName().contains(this.clientSession.getHostName())) {
                                    continue;
                                }
                                Card first = deck.draw();
                                Card second = deck.draw();
                                this.clientSession.sendDeckMessage(first.rank(), first.suit(), second.rank(), second.suit(), players.getHostName());
                            }
                            Card dfirst = deck.draw();
                            Card dsecond = deck.draw();
                            for (int i = 0; i < playerInfo.size(); i++) {
                                if (playerInfo.get(i).getHostName().equals(this.clientSession.getHostName())) {
                                    playerInfo.get(i).setCards(dfirst, dsecond);
                                    break;
                                }
                            }
                        }
                    }
                    initializeGameGUI();
                }catch(InterruptedException ede){
                    ede.printStackTrace();
                }
            }
        } else if (arg.toString().equals("action")) { // turn changes

            int myIndex = 0;
            int endOfRoundIndex = 0;
            String turnHostName = "";
            for (int i = 0; i < this.observable.getPlayers().size(); i++) {
                if (this.observable.getPlayers().get(i).isTurn()) {
                    turnHostName = this.observable.getPlayers().get(i).getHostName();
                }
                if (this.observable.getPlayers().get(i).getHostName().equals(this.clientSession.getHostName())) {
                    myIndex = i;
                }
                if (this.observable.getPlayers().size() > 2 && this.observable.getPlayers().get(i).getRole().contains("SB")) {
                    endOfRoundIndex = i;
                } else if (this.observable.getPlayers().size() == 2 && this.observable.getPlayers().get(i).getRole().contains("D")) {
                    endOfRoundIndex = i;
                }
            }

            if (this.observable.getLastAction().getValue().toString().contains("Check")) {
                myPanel.actionAfterCheck();
            } else if (this.observable.getLastPlayerToBet().equals(turnHostName)) {
                myPanel.actionAfterCheck();
                this.observable.setLastPlayerToBet(this.observable.getPlayers().get(endOfRoundIndex).getHostName());
            } else if (this.observable.getLastAction().getValue().toString().contains("Bet") ||
                        this.observable.getLastAction().getValue().toString().contains("Call")) {
                myPanel.actionAfterBet();
            }
            if (this.observable.getPlayers().get(myIndex).isTurn()) {
                myPanel.play();
            } else {
                myPanel.stop();
            }
            ((PlayersTableModel) this.playersTable.getModel()).fireTableDataChanged();
        } else if (arg.toString().equals("pot")) { // pot changes
            potLabel.setText("Pot: $" + this.observable.getPot().toString());
        }
    }
}