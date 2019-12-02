package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.*;
import game.*;
import server.*;
class PlayerPane extends JPanel implements ActionListener{
    private Card firstCard, secondCard;
    private ImageIcon firstCardImg, secondCardImg;
    private JLabel firstCardSlot, secondCardSlot;
    private JTextField pot;
    JButton checkBtn, betBtn, foldBtn;
    private boolean check=false,fold=false,call=false;
    private int bet = 0;
    private ClientSession cs;
    private Player cPlayer;
    private PlayersObservable observable = null;
    public PlayerPane(Card firstCard, Card secondCard, ClientSession cs) {
        this.firstCard = firstCard;
        this.secondCard = secondCard;
        this.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        this.checkBtn = new JButton("Check");
        this.betBtn = new JButton("Bet");
        this.foldBtn = new JButton("Fold");
        this.cs = cs;
        this.observable = this.cs.getObservable();
        this.pot = new JTextField("POT:");

        ArrayList<Player> playersList = this.observable.getPlayers();
        for (Player player: playersList) {
            if (player.getHostName().contains(this.cs.getHostName())) {
                cPlayer = player;
            }
        }

        // image shows number/suit
        this.firstCardImg = new ImageIcon(getCardImage(firstCard));
        this.secondCardImg = new ImageIcon(getCardImage(secondCard));

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 0;
        cons.gridy = 0;
        this.add(this.checkBtn, cons);

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 1;
        cons.gridy = 0;
        this.add(this.betBtn, cons);

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 2;
        cons.gridy = 0;
        this.add(this.foldBtn);

        this.firstCardSlot = new JLabel(this.firstCardImg);
        this.secondCardSlot = new JLabel(this.secondCardImg);
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 0;
        cons.gridy = 1;
        cons.gridwidth = 2;
        this.add(this.firstCardSlot, cons);

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 1;
        cons.gridy = 1;
        cons.gridwidth = 2;

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 3;
        cons.gridy = 1;
        cons.gridwidth = 2;
        this.add(this.pot);
        this.add(this.secondCardSlot, cons);
        checkBtn.addActionListener(this);
        betBtn.addActionListener(this);
        foldBtn.addActionListener(this);
        this.setBackground(new Color(12, 107, 17));
    }
    public void actionPerformed(ActionEvent e) {
        JComponent pressed = (JComponent) e.getSource();
        if(pressed==checkBtn){
            if( this.checkBtn.getText().equals("Check")) {
                System.out.println("Check");
                check = true;
                cPlayer.setLastAction("Check");
            }else{
                //call
                if(cPlayer.getMoney() >= observable.getPot()){
                    call=true;
                }
            }
            actionAfterCheck();
        }else if(pressed ==betBtn){
            if(this.betBtn.getText().equals("Bet")) {
                System.out.println("Bet");
                try {
                    bet = Integer.parseInt(JOptionPane.showInputDialog("How much do you want to bet?"));
                    while (cPlayer.getMoney() < bet) {
                        bet = Integer.parseInt(JOptionPane.showInputDialog("You are too poor, bet again."));
                    }
                    //   cPlayer.removeMoney(bet); should do that after round
                    observable.setPot(bet);
                    cPlayer.setLastAction("Bet: $" +bet);
                    this.pot.setText("POT: "+ bet);
                    actionAfterBet();
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
                System.out.println("You have bet $" + bet);
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
                }catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }
        }else if(pressed ==foldBtn){
            System.out.println("Fold");
            cPlayer.setLastAction("Fold");

            fold = true;
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