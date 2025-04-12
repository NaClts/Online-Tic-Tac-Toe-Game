import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JOptionPane;

/**
* The TicTacToeClientGUI program implements a GUI application of an Online Tic-Tac-Toe Game Client.
* This application will connect to the server (at localhost).
* User will play a Tic-Tac-Toe game with another player, which has also connected to the server.
* User actions will trigger the application to send commands to the server.
* Commands from server will be handled and displayed in this application.
* Game logic and session management is mainly handled by the server.
* To start and initialize the GUI, one should call the go() method.
*
* @author Vincent Tong
* @version 1.0
* @since 2024-12-05
*/

public class TicTacToeClientGUI {
	
	private Socket socket;
	private Scanner in;
	private PrintWriter out;
	
	private JFrame MainFrame = new JFrame("Tic Tac Toe");;
	
	private JTextField NameTextField = new JTextField(16);
	private JButton SubmitName = new JButton("Submit");
	
	private JLabel MessageLabel = new JLabel("Enter your player name...");
	
	private JLabel Player1WinNum = new JLabel("0");
	private JLabel Player2WinNum = new JLabel("0");
	private JLabel DrawNum = new JLabel("0");
	
	private JPanel WestPanel;
	
	private Grid[][] Grids = new Grid[3][3];
	private boolean AllowGridInput = false;
	
	/**
	 * This method initializes and starts the client GUI.
	 * It initializes the GUI components.
	 */
	public void go() {
		
		try {
			this.socket = new Socket("127.0.0.1", 52396);
			this.in = new Scanner(socket.getInputStream());
			this.out = new PrintWriter(socket.getOutputStream(), true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
			JPanel SouthPanel = new JPanel();
			SouthPanel.setLayout(new BoxLayout(SouthPanel, BoxLayout.Y_AXIS));
		
				JPanel NamePanel = new JPanel();
				NamePanel.add(new JLabel("Enter your name: "));
				NamePanel.add(NameTextField);
					
					SubmitName.addActionListener(new SubmitNameListener());
				
				NamePanel.add(SubmitName);
		
				JPanel CurrentTimePanel = new TimePanel();
		
			SouthPanel.add(NamePanel);
			SouthPanel.add(CurrentTimePanel);
		
		MainFrame.add(SouthPanel, BorderLayout.SOUTH);
		
			JPanel NorthPanel = new JPanel();
			NorthPanel.add(MessageLabel);
		
		MainFrame.add(NorthPanel, BorderLayout.NORTH);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		
			WestPanel = new JPanel();
			WestPanel.setLayout(new GridBagLayout());
//			WestPanel.setBackground(Color.LIGHT_GRAY);
			for (int i=0; i<3; i++) {
				for (int j=0; j<3; j++) {
					Grids[i][j] = new Grid(i, j);
					c.gridx = i;
					c.gridy = j;
					WestPanel.add(Grids[i][j], c);
				}
			}
		
		MainFrame.add(WestPanel, BorderLayout.CENTER);
		
			JPanel EastPanel = new JPanel();
			EastPanel.setLayout(new GridBagLayout());
			EastPanel.setBorder(BorderFactory.createTitledBorder("Score"));
		
			c.gridx = 0;
			c.gridy = 0;
			EastPanel.add(new JLabel("Player 1 Wins: "), c);
			
			c.gridx = 1;
			EastPanel.add(Player1WinNum, c);
			
			c.gridx = 0;
			c.gridy = 1;
			EastPanel.add(new JLabel("Player 2 Wins: "), c);
			
			c.gridx = 1;
			EastPanel.add(Player2WinNum, c);
			
			c.gridx = 0;
			c.gridy = 2;
			EastPanel.add(new JLabel("Draws: "), c);
			
			c.gridx = 1;
			EastPanel.add(DrawNum, c);
		
		MainFrame.add(EastPanel, BorderLayout.EAST);
		
			JMenuBar MainMenuBar = new JMenuBar(); 
				JMenu ControlMenu = new JMenu("Control");
					JMenuItem ControlExit = new JMenuItem("Exit");
					ControlExit.addActionListener(new ControlExitListener());
				ControlMenu.add(ControlExit);
			MainMenuBar.add(ControlMenu);
				JMenu HelpMenu = new JMenu("Help");
					JMenuItem HelpInstructions = new JMenuItem("Instructions");
					HelpInstructions.addActionListener(new HelpInstructionsListener());
				HelpMenu.add(HelpInstructions);
			MainMenuBar.add(HelpMenu);	
			
		MainFrame.setJMenuBar(MainMenuBar);
		
		MainFrame.setSize(512, 512);
		MainFrame.setVisible(true);
		
		// Creates a new Thread for reading server messages
		Thread handler = new ClinetHandler(socket);
		handler.start();
	}
	
	private class ClinetHandler extends Thread {
		private Socket socket;

		public ClinetHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				readFromServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void readFromServer() throws Exception {
			try {
				while (in.hasNextLine()) {
					var command = in.nextLine().trim();
					System.out.println("Client Received: " + command);
					out.flush();
					
					if (command.startsWith("START AND MOVE")) {
						AllowGridInput = true;
						MessageLabel.setText("Game started. Now is your turn.");
					}
					else if (command.startsWith("START AND WAIT")) {
						MessageLabel.setText("Game started. Wait for your opponent.");
					}
					else if (command.startsWith("MOVED")) {
						String[] Arguments = command.split(" ");
						String Owner = Arguments[1];
						int x = Integer.parseInt(Arguments[2]);
				        int y = Integer.parseInt(Arguments[3]);
				        Grids[x][y].setOwner(Owner);
					}
					else if (command.startsWith("MOVE")) {
						AllowGridInput = true;
						MessageLabel.setText("Your opponent has moved. Now is your turn.");
					}
					else if (command.startsWith("WAIT")) {
						AllowGridInput = false;
						MessageLabel.setText("Valid move, wait for your opponent.");
					}
					else if (command.startsWith("WIN") || command.startsWith("LOSE") || command.startsWith("DRAW")) {
						AllowGridInput = false;
						String[] Arguments = command.split(" ");
						String Result = Arguments[0];
						int NumOfWinO = Integer.parseInt(Arguments[1]);
				        int NumOfWinX = Integer.parseInt(Arguments[2]);
				        int NumOfDraw = Integer.parseInt(Arguments[3]);
				        GameOverHandler(Result, NumOfWinO, NumOfWinX, NumOfDraw);
					}
					else if (command.startsWith("EXIT")) {
						Object[] options = {"Yes"};
				        int result = JOptionPane.showOptionDialog(MainFrame, 
				        										  "Game Ends. One of the players left.", 
				                                                  "Game Over", 
				                                                  JOptionPane.DEFAULT_OPTION, 
				                                                  JOptionPane.INFORMATION_MESSAGE, 
				                                                  null, 
				                                                  options, 
				                                                  options[0]);
				        if (result == 0) {
				        	System.exit(0);
				        }
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				socket.close();
			}
		}
	}
	
	private void GameOverHandler(String Result, int NumOfWinO, int NumOfWinX, int NumOfDraw) {
		String GameOverMessage = "";
		
		if (Result.equals("WIN")) {
			GameOverMessage = "Congratulations. You wins! Do you want to play again?";
		} else if (Result.equals("LOSE")) {
			GameOverMessage = "You lose. Do you want to play again?";
		} else if (Result.equals("DRAW")) {
			GameOverMessage = "It's a draw! Play again?";
		}
		
        int result = JOptionPane.showConfirmDialog(MainFrame, 
        										  GameOverMessage, 
                                                  "Game Over", 
                                                  JOptionPane.YES_NO_OPTION, 
                                                  JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
        	MessageLabel.setText("Game started. Wait for your opponent.");
        	ResetGrid();
			out.println("READY");
			System.out.println("Client Sent: READY");
        }
        else if (result == JOptionPane.NO_OPTION) {
        	System.exit(0);
        }
        
        Player1WinNum.setText( String.valueOf( NumOfWinO ) );
        Player2WinNum.setText( String.valueOf( NumOfWinX ) );
        DrawNum.setText( String.valueOf( NumOfDraw ) );
		return;
	}
	
	private void ResetGrid() {
		// Reset Grid
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				Grids[i][j].setOwner("");
			}
		}
	}
	
	private class Grid extends JLabel implements MouseListener {
		private int x;
		private int y;
						
		public Grid(int x, int y) {
			this.x = x;
			this.y = y;
			setText("");
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
	        setPreferredSize(new Dimension(100, 100));
	        setHorizontalAlignment(JLabel.CENTER);
	        setFont(new Font("Arial", Font.BOLD, 96));
			addMouseListener(this);
		}
		
		public String getOwner() {
			return getText();
		}
		
		public void setOwner(String Owner) {
			setText(Owner);
			if ( Owner.equals("X") ) {
				setForeground(Color.GREEN);
			} else if ( Owner.equals("O") ) {
				setForeground(Color.RED);
			}
		}

		public void mouseClicked(MouseEvent event) {}

	    public void mousePressed(MouseEvent event) {
	    	if (AllowGridInput && getOwner().equals("")) {
	    		out.println("MOVE " + x + " " + y);
	    		System.out.println("Client Sent: MOVE " + x + " " + y);
	    	}
	    }
	    
	    public void mouseReleased(MouseEvent event) {}

	    public void mouseEntered(MouseEvent event) {}

	    public void mouseExited(MouseEvent event) {}
	}
	
	private class SubmitNameListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			NameTextField.setEditable(false);
			NameTextField.setFocusable(false);
			
			SubmitName.setEnabled(false);
			
			MainFrame.setTitle( "Tic Tac Toe - Player: " + NameTextField.getText() );
			MessageLabel.setText( "WELCOME " + NameTextField.getText().toUpperCase() );
			WestPanel.setBackground(Color.WHITE);
			
			out.println("READY");
			System.out.println("Client Sent: READY");
		}
	}
	
	private class ControlExitListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.exit(0);
		}
	}
	
	private class HelpInstructionsListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String InstructionsString = """
					Some information about the game:
					
					Criteria for a valid move:
					- The move is not occupied by any mark.
					- The move is made in the player's turn.
					- The move is made within the 3 x 3 board.
					
					The game would continue and switch among the opposite player until it reaches either one of the following conditions:
					- Player 1 wins.
					- Player 2 wins.
					- Draw.
					- One of the players leaves the game.
					""";
			Object[] options = {"Okay"};
	        int DummyResult = JOptionPane.showOptionDialog(MainFrame, 
	        										  InstructionsString, 
	                                                  "Game Information", 
	                                                  JOptionPane.DEFAULT_OPTION, 
	                                                  JOptionPane.INFORMATION_MESSAGE, 
	                                                  null, 
	                                                  options, 
	                                                  options[0]);
		}
	}
}