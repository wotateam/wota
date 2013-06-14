/**
 * 
 */
package wota.graphics;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class StatisticsView implements Runnable{

	@Override
	public void run() {

		JFrame f = new JFrame("Wota Statistics");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLayout(new FlowLayout());
		
		String[] columnNames = {"Name",
                "Mitglied der geilsten WG der Welt?"
                };
		
		Object[][] data = {
			    {"Daniel", new Boolean(true)},
			    {"Simon", new Boolean(true)},
			    {"Peter", new Boolean(false)},
			    {"Pascal", new Boolean(true)}
		};

		
		JTable table = new JTable(data, columnNames);
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
		
		JScrollPane scrollPane = new JScrollPane(table);
		f.add(scrollPane);
		
		f.pack();
		f.setVisible(true);
	}
}
