package gui;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelEvent;
import java.util.ArrayList;

import server.Player;
import gui.CardCellRenderer;

public class PlayersTableModel extends AbstractTableModel implements TableModelListener {
    public static final String[] columnHeaders = {"Player Name", "Money", "Cards", "Last Action"};
    public ArrayList<Player> players = new ArrayList<>();

    /**
     * Default version UID
     */
    private static final long serialVersionUID = 1L;

    public PlayersTableModel(ArrayList<Player> players) {
        this.players = players;
        this.addTableModelListener(this);
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
                // if player has role, show <Player> (Role), else show <Player>
                return player.getRole() != "" ? player.getName() + " (" + player.getRole() + ")" : player.getName();
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

    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();

        for (int i = 0; i < this.players.size(); i++) {
            // System.out.println(this.players.get(i).getLastAction());
            this.setValueAt(this.players.get(i).getLastAction(), row, 3);
        }

    }
}
