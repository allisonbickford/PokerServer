package gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import server.ClientSession;
import server.Player;


/**********************************************************************
Graphical user interface used to represent a game lobby. Once a client
registers their name and connects to the central server, their name
will appear in a lobby list along with any other players that are
connected to the central server.

@author Allison Bickford
@author R.J. Hamilton
@author Johnathon Kileen
@author Michelle Vu
@version December 2019
 **********************************************************************/
public class GUI extends JFrame {
    private GameGUI gameGUI;
    private JPanel lobbyPanel = new JPanel();
    private ClientSession clientSession;

    public GUI(ClientSession session) {
        super("Poker");
        this.clientSession = session;
        initializeLobbyGUI();
        this.pack();
        this.setBounds(200, 200, 640, 480);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void initializeGameGUI() {
        this.getContentPane().removeAll();
        this.gameGUI = new GameGUI(this.clientSession);
        this.add(this.gameGUI);
        this.pack();
        this.revalidate();
        this.repaint();
    }

    private void initializeLobbyGUI() {
        this.lobbyPanel.setBackground(new Color(12, 107, 17));
        this.setTitle("Poker Lobby");
        
        JButton startButton = new JButton("Start Game!");
        startButton.addActionListener(e -> {
            this.clientSession.startGame();
        });
        this.lobbyPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        this.lobbyPanel.setPreferredSize(new Dimension(640, 480));
        JPanel playerLobby = playerLobby(new ArrayList<Player>());
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        this.lobbyPanel.add(playerLobby, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.PAGE_END;
        this.lobbyPanel.add(startButton, gbc);

        this.add(this.lobbyPanel);
        this.pack();
        this.revalidate();
        this.repaint();
    }

    private JPanel playerLobby(ArrayList<Player> players) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255, 80));
        panel.setPreferredSize(new Dimension(200, 200));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Players:"));
        for (Player player: players) {
            if (player.getHostName().contains(this.clientSession.getHostName())) {
                panel.add(new JLabel(player.getName() + " <-- You!"));
            } else {
                panel.add(new JLabel(player.getName()));
            }
        }
        return panel;
    }

    public void updatePlayerLobby(ArrayList<Player> updatedPlayers) {
        this.lobbyPanel.remove(0);
        JPanel playerLobby = playerLobby(updatedPlayers);
        this.lobbyPanel.add(playerLobby, 0);
        this.invalidate();
        this.validate();
        this.repaint();
    }

    /**
     * @return the gameGUI
     */
    public GameGUI getGameGUI() {
        return gameGUI;
    }
    
    public void showWinnerDialog(String name) {
        JOptionPane.showMessageDialog(this, "Winner of the round was " + name);
    }
}