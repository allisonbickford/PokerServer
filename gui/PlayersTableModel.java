package gui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

import server.Player;
import gui.CardCellRenderer;

public class PlayersTableModel extends AbstractTableModel {
    public static final String[] columnHeaders = {"Player Name", "Money", "Cards", "Last Action"};
    public ArrayList<Player> players = new ArrayList<>();
    public String turnHostName = "";

    /**
     * Default version UID
     */
    private static final long serialVersionUID = 1L;

    public PlayersTableModel(ArrayList<Player> players, String currentTurnName) {
        this.players = players;
        this.turnHostName = currentTurnName;
    }


    @Override
    public int getRowCount() {
        return players.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return CardCellRenderer.class;
    }

    @Override
    public String getColumnName(int column) {
        return PlayersTableModel.columnHeaders[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Player player = players.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return player.getName();
            case 1:
                return "$" + player.getMoney();
            case 2:
                return player.getPanel();
            case 3:
                return player.getLastAction();
            }
        return new String();
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return false;
    }
}
