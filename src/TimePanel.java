import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.*;

/**
* This is a JPanel displaying the current time.
* In other words, it is a subclass of JPanel.
* Time is displayed as `HH:mm:ss` and updated every second.
*
* @author Vincent Tong
* @version 1.0
* @since 2024-11-26
*/

public class TimePanel extends JPanel {
	private JLabel TimeLabel;
	private SimpleDateFormat TimeFormat;
	
	/**
	 * No argument constructor of TimePanel.
	 * Logics and components will be initialized.
	 * No extra configuration is needed.
	 */
	public TimePanel() {
		add(new JLabel("Current Time: "));
		
		TimeFormat = new SimpleDateFormat("HH:mm:ss");
		TimeLabel = new JLabel();
		TimeLabel.setText(TimeFormat.format(new Date()));
		add(TimeLabel);
		
		Timer ScheduledTask = new Timer(1000, new UpdateTime());
		ScheduledTask.start();
	}
	
	private class UpdateTime implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			TimeLabel.setText(TimeFormat.format(new Date()));
		}
	}
}
