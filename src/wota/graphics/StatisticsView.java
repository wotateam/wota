/**
 * 
 */
package wota.graphics;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.util.Vector;

import wota.gamemaster.StatisticsLogger;
import wota.gameobjects.*;
import wota.gameobjects.GameWorld.Player;

/**
 *  View class for the statistics.
 *  Draws an JTable and greps information from GameWorld
 */
public class StatisticsView implements Runnable{
	
	public JFrame frame;
	private GameWorld gameWorld;
	private StatisticsLogger logger;
	private StatisticsTableModel statisticsTableModel;
	
	public StatisticsView(GameWorld gameWorld, StatisticsLogger logger) {
		this.gameWorld = gameWorld;
		this.logger    = logger;
	}
	
	public void run() {
		frame = new JFrame("Wota Statistics");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());

		statisticsTableModel = new StatisticsTableModel(logger);
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

		private StatisticsLogger  logger;
		String[]                  columnNames;
		Class<?>[]                columnClasses;

		public StatisticsTableModel(StatisticsLogger logger) {
			this.logger = logger;
			columnNames = new String[] {
							"Player", "", "Ants", "created ants", "lost ants",
							"collected food"
			};

		}
		
		@Override
		public int getRowCount() {
			return gameWorld.getPlayers().size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			System.out.println(rowIndex + " " + columnIndex);
			Player player = gameWorld.getPlayers().get(rowIndex);
			switch(columnIndex) {
			case 0: 
				return player.name;
			case 1:
				return GameView.playerColors[player.getId()];
			case 2:
				return player.antObjects.size();
			case 3:
				return logger.createdAnts()[rowIndex];
			case 4:
				return logger.diedAnts()[rowIndex];
			case 5:
				return logger.collectedFood()[rowIndex];
			default:
				return null;
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return getValueAt(0, columnIndex).getClass();
		}
		
		
	}

	/** call this when the table should grep the information */
	public void refresh() {
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
