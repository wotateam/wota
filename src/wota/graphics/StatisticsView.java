/**
 * 
 */
package wota.graphics;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.util.Vector;

import wota.gameobjects.*;
import wota.gameobjects.GameWorld.Player;

/**
 *  View class for the statistics.
 *  Draws an JTable and greps information from GameWorld
 */
public class StatisticsView implements Runnable{
	
	public JFrame frame;
	private GameWorld gameWorld;
	private StatisticsTableModel statisticsTableModel;
	
	public StatisticsView(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
	}
	
	public void run() {
		frame = new JFrame("Wota Statistics");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());

		statisticsTableModel = new StatisticsTableModel();
		JTable table = new JTable(statisticsTableModel);
		table.setDefaultRenderer(Color.class, new ColorRenderer());
		
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
		
		JScrollPane scrollPane = new JScrollPane(table);
		frame.add(scrollPane);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * The StatisticTableModel gets passed to the JTable.
	 * It implements basic methods like getValueAt() which 
	 * are used by the JTable to obtain the cell values.
	 */
	public class StatisticsTableModel extends AbstractTableModel {

		Vector<Vector<Object> >   rowdata;
		Vector<String>            columnNames;

		public StatisticsTableModel() {
			rowdata = new Vector<Vector<Object> >();
			columnNames = new Vector<String>();
			columnNames.add("Player");
			columnNames.add("");
			columnNames.add("#Ants");
			
			refresh();
		}
		
		public void refresh() {
			rowdata.clear();

			for (int playerId=0; playerId<gameWorld.getPlayers().size(); playerId++) {
				Vector<Object> playerData = new Vector<Object>();
				Player player = gameWorld.getPlayers().get(playerId);
				playerData.add(player.name);
				playerData.add(GameView.playerColors[player.getId()]);
				playerData.add(player.antObjects.size());
				
				rowdata.add(playerData);
			}
		}
		
		@Override
		public int getRowCount() {
			return rowdata.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.size();
		}

		@Override
		public String getColumnName(int column) {
			return columnNames.get(column);
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			//return Color.black;
			return rowdata.get(rowIndex).get(columnIndex);
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return rowdata.get(0).get(columnIndex).getClass();
		}
		
	}

	/** call this when the table should grep the information */
	public void refresh() {
		statisticsTableModel.refresh();
		statisticsTableModel.fireTableDataChanged(); // tells the JTable to update graphics 
	}
	
	public class ColorRenderer extends JLabel
							   implements TableCellRenderer {

		ColorRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(
								            JTable table, Object color,
										    boolean isSelected, boolean hasFocus,
										    int row, int column) {
			Color newColor = (Color)color;
			setBackground(newColor);
			return this;
		}
	}
}
