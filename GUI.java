import javax.swing.*;
import java.awt.*;

class GUI extends JFrame {
    JPanel panel = new JPanel();
    
    public GUI() {
        super("Poker");
        this.panel.setBackground(new Color(12, 107, 17));
        this.panel.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        Deck deck = new Deck();
        PlayerPane player1 = new PlayerPane(true, deck.draw(), deck.draw());
        panel.add(player1);
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 3;
        cons.gridy = 5;
        cons.anchor = GridBagConstraints.PAGE_END;
        cons.gridwidth = 2;
        this.panel.add(player1, cons);

        PlayerPane player2 = new PlayerPane(false, deck.draw(), deck.draw());
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 0;
        cons.gridy = 0;
        this.panel.add(player2, cons);
        
        this.add(this.panel);
        this.setBounds(200, 200, 640, 480);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

}