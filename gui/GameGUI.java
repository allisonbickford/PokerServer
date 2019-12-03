package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;

import server.ClientSession;
import server.Server;
import server.Player;
import game.*;

public class GameGUI extends JPanel {
    private Deck deck;
    private BoardCards boardCardPanel;
    private JTable playersTable;
    private JLabel potLabel;
    private PlayerPane myPanel;

    public GameGUI(ClientSession clientSession) {
        PlayersTableModel ptm = new PlayersTableModel(Server.getGame().getPlayers());
        this.playersTable = new JTable(ptm);
        this.potLabel = new JLabel("Pot: $0");
        this.boardCardPanel = new BoardCards(3); // start with all cards face down
        ArrayList<Player> players = Server.getGamePlayers();

        this.setBackground(new Color(12, 107, 17));
        this.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        playersTable.setCellSelectionEnabled(false);
        playersTable.setDefaultRenderer(CardCellRenderer.class, new CardCellRenderer());
        playersTable.setPreferredScrollableViewportSize(new Dimension(600, 300));
        playersTable.setRowHeight(75);
        JScrollPane playersPane = new JScrollPane(this.playersTable);
        playersPane.setPreferredSize(new Dimension(600, 300));
        
        cons.gridx = 0;
        cons.gridy = 0;
        cons.gridheight = 3;
        cons.gridwidth = 3;
        this.add(playersPane, cons);
        
        String name = "";
        for (Player player: players) {
            if(player.getHostName().contains(clientSession.getHostName())) {
                Card[] hold = player.getCards();
                myPanel = new PlayerPane(hold[0],hold[1], clientSession);
                name = player.getName();
                if (!player.isTurn()) {
                    myPanel.stop();
                } else {
                    myPanel.actionAfterBet();
                    myPanel.play();
                }
                player.showCards();
                break;
            }
        }
        JLabel myLabel = new JLabel("Me: " + name);
        cons.gridx = 0;
        cons.gridy = 4;
        cons.gridheight = 1;
        cons.gridwidth = 1;
        cons.anchor = GridBagConstraints.PAGE_START;
        myLabel.setForeground(new Color(255, 255, 255));
        this.add(myLabel, cons);
        
        cons.gridx = 1;
        cons.gridy = 7;
        cons.gridwidth = 2;
        cons.anchor = GridBagConstraints.PAGE_END;
        this.potLabel.setForeground(new Color(255, 255, 255));
        this.add(potLabel, cons);
        
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 0;
        cons.gridy = 9;
        cons.anchor = GridBagConstraints.PAGE_END;
        cons.gridwidth = 3;
        this.add(myPanel, cons);

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 0;
        cons.gridy = 5;
        cons.anchor = GridBagConstraints.PAGE_END;
        cons.gridwidth = 10;
        cons.gridheight = 2;
        this.add(this.boardCardPanel, cons);
        this.setPreferredSize(new Dimension(640, 700));
        this.setVisible(true);
    }

    public PlayerPane getMyPanel() {
        return this.myPanel;
    }

    public void stopPlay() {
        this.myPanel.stop();
    }

    public void resumePlay() {
        this.myPanel.play();
    }

    public void changePlayerButtons(String lastAction) {
        if (lastAction.contains("Bet") || lastAction.contains("Raise") || lastAction.contains("Call")) {
            this.myPanel.actionAfterBet();
        } else {
            this.myPanel.actionAfterCheck();
        }
    }

    public void actionAfterBet() {
        this.myPanel.actionAfterBet();
    }

    public void setButtonsToBeginningOfRound() {
        this.myPanel.actionAfterCheck();
    }

    public void setPotLabel(int amount) {
        this.potLabel.setText("Pot: $" + amount);
    }

    public void flop(Card[] cards) {
        this.boardCardPanel.flop(cards[0], cards[1], cards[2]);
    }

    public void turn(Card card) {
        this.boardCardPanel.turn(card);
    }

    public void river(Card card) {
        this.boardCardPanel.river(card);
    }

    public void updateTable() {
        ((PlayersTableModel) this.playersTable.getModel()).fireTableDataChanged();
    }

    public void fireUpdate() {
        this.invalidate();
        this.revalidate();
    }
}
