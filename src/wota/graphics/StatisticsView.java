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
 * View class for the statistics. Draws an JTable and puts in
 * StatisticsView.StatisticsTableModel.
 */
public class StatisticsView implements Runnable {

	public JFrame frame;
	private GameWorld gameWorld;
	private StatisticsLogger logger;
	private StatisticsTableModel statisticsTableModel;

	public StatisticsView(GameWorld gameWorld, StatisticsLogger logger) {
		this.gameWorld = gameWorld;
		this.logger = logger;
	}

	public void run() {
		frame = new JFrame("Wota Statistics");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());

		statisticsTableModel = new StatisticsTableModel(logger);
		JTable table = new JTable(statisticsTableModel);
		table.setDefaultRenderer(Object.class, new CellRenderer());

		// table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);

		JScrollPane scrollPane = new JScrollPane(table);
		frame.add(scrollPane);

		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * The StatisticTableModel gets passed to the JTable. It implements basic
	 * methods like getValueAt() which are used by the JTable to obtain the cell
	 * values.
	 */
	@SuppressWarnings("synthetic-access")
	public class StatisticsTableModel extends AbstractTableModel {

		private StatisticsLogger logger;
		private final String[] playerNames;
		/**
		 * representing the table data, 1st index = row, 2nd = column, 0 = row
		 * name, 1.. players
		 */
		private Object[][] data;

		public StatisticsTableModel(StatisticsLogger logger) {
			this.logger = logger;
			String[] rowNames = new String[] { "", "# Ants", "# Gatherer",
					"# Soldiers", "# Scouts", "# created ants", "# lost ants",
					"collected food" };
			data = new Object[rowNames.length][gameWorld.getPlayers().size() + 1];
			for (int i = 0; i < rowNames.length; i++) {
				data[i][0] = rowNames[i];
			}

			playerNames = new String[gameWorld.getPlayers().size()];
			for (Player player : gameWorld.getPlayers()) {
				playerNames[player.id()] = player.name;
			}

		}

		public int getRowCount() {
			return data.length;
		}

		public int getColumnCount() {
			return data[0].length;
		}

		@Override
		public String getColumnName(int column) {
			if (column == 0) {
				return "";
			}
			return playerNames[column - 1];
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return data[rowIndex][columnIndex];
		}

		/** updates all of the data */
		public void refresh() {
			for (int column = 1; column < getColumnCount(); column++) {
				int playerId = column - 1;
				Player player = gameWorld.getPlayers().get(playerId);

				data[0][column] = GameView.playerColors[player.id()];
				data[1][column] = player.antObjects.size();
				data[2][column] = player.numAnts(Caste.Gatherer);
				data[3][column] = player.numAnts(Caste.Soldier);
				data[4][column] = player.numAnts(Caste.Scout);
				data[5][column] = logger.createdAnts()[playerId];
				data[6][column] = logger.diedAnts()[playerId];
				data[7][column] = logger.collectedFood()[playerId];
			}
		}

	}

	/** call this when the table should grab the information */
	public void refresh() {
		statisticsTableModel.refresh();
		statisticsTableModel.fireTableDataChanged(); // tells the JTable to
														// update graphics
	}

	public class CellRenderer extends JLabel implements TableCellRenderer {
		DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();

		CellRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object object, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (object instanceof Color) {
				setBackground((Color) object);
				return this;
			} else {
				return defaultTableCellRenderer.getTableCellRendererComponent(
						table, object, isSelected, hasFocus, row, column);
			}
		}
	}
}
