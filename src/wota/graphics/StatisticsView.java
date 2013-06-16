/**
 * 
 */
package wota.graphics;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import java.awt.*;
import java.util.Map;
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
		table.setDefaultRenderer(Object.class, new CellRenderer());
		
	//	table.setPreferredScrollableViewportSize(new Dimension(500, 70));
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
		
		private StatisticsLogger          logger;
		private final Vector<String>      playerNames;
		private final String[]			  rowNames;
		
		public StatisticsTableModel(StatisticsLogger logger) {
			this.logger = logger;
			playerNames = new Vector<String>();
			for (Player player : gameWorld.getPlayers()) {
				playerNames.add(player.name);
			}
			rowNames = new String[] {
					"", "# Ants", "# Gatherer", "# Soldiers", "# Scouts",
					"# created ants", "# lost ants",
					"collected food"
			};
		}
		
		@Override
		public int getRowCount() {
			return rowNames.length;
		}

		@Override
		public int getColumnCount() {
			return playerNames.size() + 1;
		}

		@Override
		public String getColumnName(int column) {
			if (column == 0) {
				return "";
			}
			return playerNames.get(column - 1);
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return rowNames[rowIndex];
			}
			int playerId = columnIndex - 1;
			Player player = gameWorld.getPlayers().get(playerId);
			switch(rowIndex) {
			case 0:
				return GameView.playerColors[player.getId()];
			case 1:
				return player.antObjects.size();
			case 2:
				return player.numAnts(Caste.Gatherer);
			case 3:
				return player.numAnts(Caste.Soldier);
			case 4:
				return player.numAnts(Caste.Scout);
			case 5:
				return logger.createdAnts()[playerId];
			case 6:
				return logger.diedAnts()[playerId];
			case 7:
				return logger.collectedFood()[playerId];
			default:
				return null;
			}
		}
		
	}

	/** call this when the table should grep the information */
	public void refresh() {
		statisticsTableModel.fireTableDataChanged(); // tells the JTable to update graphics 
	}
	
	public class CellRenderer extends JLabel
							   implements TableCellRenderer {
		DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();

		CellRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(
								            JTable table, Object object,
										    boolean isSelected, boolean hasFocus,
										    int row, int column) {
			if (object instanceof Color) {
				setBackground((Color) object);
				return this;
			}
			else {
			return defaultTableCellRenderer.getTableCellRendererComponent(
					table, object, isSelected,hasFocus, row, column);
			}
		}
	}
}
