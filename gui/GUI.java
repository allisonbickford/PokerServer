package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.*;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;

import server.ClientSession;
import server.Player;
import server.PlayersObservable;
import game.*;

public class GUI extends JFrame implements ActionListener, Observer {
    JPanel gamePanel = new JPanel();
    JPanel regPanel = new JPanel();
    JPanel lobbyPanel = new JPanel();
    JPanel playerPanel = new JPanel();
    private ClientSession clientSession;
    private JLabel cHostName, cPort, username,regText;
    private JTextField cHostN_field,cPort_field,username_field;
    private JButton connect;
    private JTable playersTable;
    JFrame frame;
    PlayersObservable observable = null;
    PlayersTableModel ptm;
    private Card firstCard, secondCard;
    private ImageIcon firstCardImg, secondCardImg;
    private JLabel firstCardSlot, secondCardSlot;
    private JTextField pot;
    JButton checkBtn, betBtn, foldBtn;
    private boolean check=false,fold=false,call=false;
    private int bet = 0;
    //   private ClientSession cs;
    private Player cPlayer;
    JScrollPane playersPane;
    ArrayList<Player> playerInfo;
    public GUI() {
        super("Poker");
        initializeRegGUI();
    }
    public void initializePlayerPanel(Card first, Card second){
        this.firstCard = first;
        this.secondCard = second;
        playerPanel.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        this.checkBtn = new JButton("Check");
        this.betBtn = new JButton("Bet");
        this.foldBtn = new JButton("Fold");

        this.pot = new JTextField("POT:");

        ArrayList<Player> playersList = this.observable.getPlayers();
        for (Player player: playersList) {
            if (player.getHostName().contains(this.clientSession.getHostName())) {
                cPlayer = player;
            }
        }

        // image shows number/suit
        this.firstCardImg = new ImageIcon(getCardImage(firstCard));
        this.secondCardImg = new ImageIcon(getCardImage(secondCard));

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 0;
        cons.gridy = 0;
        playerPanel.add(this.checkBtn, cons);

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 1;
        cons.gridy = 0;
        playerPanel.add(this.betBtn, cons);

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 2;
        cons.gridy = 0;
        playerPanel.add(this.foldBtn);

        this.firstCardSlot = new JLabel(this.firstCardImg);
        this.secondCardSlot = new JLabel(this.secondCardImg);
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 0;
        cons.gridy = 1;
        cons.gridwidth = 2;
        playerPanel.add(this.firstCardSlot, cons);

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 1;
        cons.gridy = 1;
        cons.gridwidth = 2;

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 3;
        cons.gridy = 1;
        cons.gridwidth = 2;
        playerPanel.add(this.pot);
        playerPanel.add(this.secondCardSlot, cons);
        checkBtn.addActionListener(this);
        betBtn.addActionListener(this);
        foldBtn.addActionListener(this);
        playerPanel.setBackground(new Color(12, 107, 17));
    }
    public void initializeGameGUI() {
        frame.getContentPane().removeAll();
        this.gamePanel.setBackground(new Color(12, 107, 17));
        this.gamePanel.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        Deck deck = new Deck();
        playerInfo = this.observable.getPlayers();
        
        Card first = deck.draw();
        Card second = deck.draw();

        initializePlayerPanel(first, second); // TODO: don't draw for everyone, central deck?
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 0;
        cons.gridy = 3;
        cons.anchor = GridBagConstraints.PAGE_END;
        cons.gridwidth = 3;
        this.gamePanel.add(playerPanel, cons);

        ptm = new PlayersTableModel(playerInfo, observable.getTurn().getHostName());
        this.playersTable = new JTable(ptm);
        this.playersTable.setCellSelectionEnabled(false);
        playersTable.setPreferredScrollableViewportSize(playersTable.getPreferredSize());
        playersTable.setDefaultRenderer(CardCellRenderer.class, new CardCellRenderer());
        playersTable.setRowHeight(75);
        playersPane = new JScrollPane(this.playersTable);
        playersPane.setPreferredSize(new Dimension(600, 300));

        cons.gridy = 0;
        cons.anchor = GridBagConstraints.PAGE_START;
        this.gamePanel.add(playersPane, cons);
        
        this.gamePanel.setPreferredSize(new Dimension(640, 480));
        frame.add(this.gamePanel);
        frame.setBounds(200, 200, 640, 480);
        frame.setBackground(new Color(12, 107, 17));
        frame.pack();
        frame.setVisible(true);
    }

    public void updatePlayer(){
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = 0;
        cons.anchor = GridBagConstraints.PAGE_START;
        ptm = new PlayersTableModel(playerInfo, observable.getTurn().getHostName());
        this.playersTable = new JTable(ptm);
        this.playersTable.setCellSelectionEnabled(false);
        playersTable.setPreferredScrollableViewportSize(playersTable.getPreferredSize());
        playersTable.setDefaultRenderer(CardCellRenderer.class, new CardCellRenderer());
        playersTable.setRowHeight(75);
        playersPane = new JScrollPane(this.playersTable);
        this.gamePanel.add(playersPane, cons);
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
    //  this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        } else if(pressed==checkBtn){
            if( this.checkBtn.getText().equals("Check")) {
                System.out.println("Check");
                check = true;

                cPlayer.setLastAction("Check");
                initializeGameGUI();
            }else{
                //call
                if(cPlayer.getMoney() >= observable.getPot()){
                    call=true;
                }
            }
            actionAfterCheck();
            updatePlayer();
        }else if(pressed ==betBtn){
            if(this.betBtn.getText().equals("Bet")) {
                System.out.println("Bet");
                try {
                    bet = Integer.parseInt(JOptionPane.showInputDialog("How much do you want to bet?"));
                    while (cPlayer.getMoney() < bet) {
                        bet = Integer.parseInt(JOptionPane.showInputDialog("You are too poor, bet again."));
                    }
                    cPlayer.removeMoney(bet); //should do that after round
                    observable.setPot(bet);
                    cPlayer.setLastAction("Bet: $" +bet);
                    clientSession.sendMessage("bet: "+cPlayer.getName()+" "+bet);
                    this.pot.setText("POT: "+ bet);
                    actionAfterBet();
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
                System.out.println("You have bet $" + bet);
                updatePlayer();
            }else{
                //raise
                System.out.println("Raise");
                int hold=0;
                try {
                    hold = Integer.parseInt(JOptionPane.showInputDialog("How much do you want to bet?"));
                    while ((cPlayer.getMoney() < bet)) {
                        hold = Integer.parseInt(JOptionPane.showInputDialog("Not enough"));
                    }
                    if(hold>observable.getPot()){
                        observable.setPot(hold);
                        this.pot.setText("POT: "+ hold);
                    }else{
                        hold = Integer.parseInt(JOptionPane.showInputDialog("Not enough"));
                    }
                    cPlayer.setLastAction("Raise: $" + hold);
                    cPlayer.removeMoney(hold);
                    updatePlayer();
                }catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }
        }else if(pressed ==foldBtn){
            System.out.println("Fold");
            cPlayer.setLastAction("Fold");
            updatePlayer();
            fold = true;
        }


    }

    private void initializeLobbyGUI() {
        this.lobbyPanel.setBackground(new Color(12, 107, 17));
        frame.getContentPane().removeAll();
        frame.setTitle("Poker Lobby " + this.clientSession.getHostName());
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
        if (arg instanceof ArrayList) {
            this.lobbyPanel.remove(0);
            JPanel playerLobby = playerLobby();
            this.lobbyPanel.add(playerLobby(), 0);
            frame.invalidate();
            frame.validate();
            frame.repaint();
        } else if (arg instanceof Player) {
            if (!this.gamePanel.isDisplayable()) {
                initializeGameGUI();
            }
            System.out.println("Currently " + this.observable.getTurn().getName() + "'s Turn");
        }
    }
    public boolean isCall() {
        return call;
    }
    public void setCall(boolean call){
        this.call = call;
    }
    public boolean isCheck() {
        return check;
    }

    public boolean isFold() {
        return fold;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public void setFold(boolean fold){
        this.fold = fold;
    }

    private Image getCardImage(Card card) {
        int cardNumber = card.getNumber();
        String cardLetter = card.getSuit().toString().substring(0,1);

        try {
            return ImageIO.read(new File("./CardImages/" + cardNumber + cardLetter + ".png")).getScaledInstance(75, 100, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            return null;
        }
    }

    private Image cardBack() {
        try {
            return ImageIO.read(new File("./CardImages/purple_back.png")).getScaledInstance(75, 100, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            return null;
        }
    }

    public void showCards() {
        this.firstCardImg = new ImageIcon(getCardImage(this.firstCard));
        this.secondCardImg = new ImageIcon(getCardImage(this.secondCard));
    }

    public void actionAfterBet() {
        this.checkBtn.setText("Call");
        this.betBtn.setText("Raise");
    }

    public void actionAfterCheck() {
        this.checkBtn.setText("Check");
        this.betBtn.setText("Bet");
    }

    public void play() {
        this.checkBtn.setEnabled(true);
        this.foldBtn.setEnabled(true);
        this.betBtn.setEnabled(true);
    }

    public void stop() {
        this.checkBtn.setEnabled(true);
        this.foldBtn.setEnabled(true);
        this.betBtn.setEnabled(true);
    }
}