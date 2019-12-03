package gui;


import javax.swing.*;
import java.awt.*;
import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;

import server.Player;

public class CardCellRenderer implements TableCellRenderer {
    private static final long serialVersionUID = 1L;
    private CardPanel panel;

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

            PlayersTableModel currentModel = (PlayersTableModel) table.getModel();
            int index = row;
            // if (index < 0) { index = currentModel.players.size() - 1; }
            boolean currentTurn = currentModel.players.get(index).isTurn();
            if (column != 2) {
                JLabel label = new JLabel(value.toString());
                if (currentTurn) {
                    label.setOpaque(true);
                    label.setBackground(table.getSelectionBackground());
                } else {
                    label.setBackground(table.getBackground());
                }
                return label;
            }
            
            this.panel = currentModel.players.get(index).getPanel();
            if (currentTurn) {
                this.panel.setBackground(table.getSelectionBackground());
            } else {
                this.panel.setBackground(table.getBackground());
            }
            return this.panel;
    }
    
}