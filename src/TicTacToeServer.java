import java.util.Scanner;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
* The TicTacToeServer program implements a Tic-Tac-Toe Server.
* This process handles the requests from the clients and relay to the GameManager.
* This process also interprets messages from GameManager and sends to clients.
* Each TicTacToeServer is only associated with one GameManager.
* That is, there can only be one game and no more than 2 clients at any time.
* Upon disconnection of 2 existing clients, the GameManager will be reset and a new game is initialized for new clients.
*
* @author Vincent Tong
* @version 1.0
* @since 2024-12-05
*/

public class TicTacToeServer {
	
	private ServerSocket serverSocket;
	private GameManagerForMultiThreads GameManager;
	private PrintWriter OutputO;
	private PrintWriter OutputX;
	private Set<PrintWriter> Outputs = new HashSet<>();
	private boolean isPlayerExited;
	
	/**
	 * Create a new Tic-Tac-Toe server according to the given / assigned port.
	 * @param serverSocket
	 */
	public TicTacToeServer(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	/**
	 * Entry point of the TicTacToeServer to start the server.
	 */
	public void start() {
		while (true) {
			try {
				System.out.println("======== NEW GAME ========");
				GameManager = new GameManagerForMultiThreads(this);
				isPlayerExited = false;
				
				Socket Socket1 = serverSocket.accept();
				System.out.println("Connected to client 1");
				Thread Thread1 = new Thread(new Handler(Socket1));
				Thread1.start();
				
				Socket Socket2 = serverSocket.accept();
				System.out.println("Connected to client 2");
				Thread Thread2 = new Thread(new Handler(Socket2));
				Thread2.start();
				
				try {
					Thread1.join();
					Thread2.join();
				} catch (InterruptedException e) {
				    e.printStackTrace();
				}
				System.out.println("======== GAME END ========");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class Handler implements Runnable {
		private Socket socket;
		private Scanner Input;
		private PrintWriter Output;
		private String Role;

		public Handler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			System.out.println("Connected: " + socket);
			try {
				Input = new Scanner(socket.getInputStream());
				Output = new PrintWriter(socket.getOutputStream(), true);
				Outputs.add(Output);
				Role = "";

				while (Input.hasNextLine() && !isPlayerExited) {
					var Command = Input.nextLine().trim();

					System.out.println("Server Received: " + Command);

					if (Command.startsWith("READY")) {
						Role = GameManager.getRole(Role);
						if (Role.equals("O")) {
							OutputO = Output;
						}
						else if (Role.equals("X")) {
							OutputX = Output;
						}
						GameManager.tryStartNewGame();
						
					} else if (Command.startsWith("MOVE")) {
						
						String[] Arguments = Command.split(" ");
						int x = Integer.parseInt(Arguments[1]);
				        int y = Integer.parseInt(Arguments[2]);
				        GameManager.move(Role, x, y);
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				// client disconnected
				for (PrintWriter i : Outputs) {
					i.println("EXIT");
					System.out.println("Server Broadcasted: EXIT");
				}
				if (Output != null) {
					Outputs.remove(Output);
				}
				isPlayerExited = true;
			}
		}
	}
	
	/**
	 * Send messages to clients to notify the start of the game.
	 */
	public void notifyGameStarted() {
		OutputO.println("START AND MOVE");
		OutputX.println("START AND WAIT");
		System.out.println("Server Broadcasted: START");
	}
	
	/**
	 * Tell the clients about the change of the grids and instructs clients to continue the game.
	 * There is a move but there is no winner or draw yet.
	 * @param Role Who has just moved (O or X)
	 * @param x X coordinate of the grid that has just been occupied
	 * @param y Y coordinate of the grid that has just been occupied
	 */
	public void displayMoveAndGameContinue(String Role, int x, int y) {
		OutputO.println("MOVED " + Role + " " + x + " " + y);
		OutputX.println("MOVED " + Role + " " + x + " " + y);
		System.out.println("Server Broadcasted: MOVED " + Role + " " + x + " " + y);
		if (Role.equals("O")) {
			OutputO.println("WAIT");
			OutputX.println("MOVE");
			System.out.println("X's Turn Now");
		}
		else if (Role.equals("X")) {
			OutputO.println("MOVE");
			OutputX.println("WAIT");
			System.out.println("O's Turn Now");
		}
	}
	
	/**
	 * Tell the clients about the change of the grids and instructs clients to stop the game.
	 * There is a move and there is a winner or a draw.
	 * @param Winner Who wins the game (O, X or Draw)
	 * @param Role Who has just moved (O or X)
	 * @param x X coordinate of the grid that has just been occupied
	 * @param y Y coordinate of the grid that has just been occupied
	 * @param NumOfWinO Number of wins of O
	 * @param NumOfWinX Number of wins of X
	 * @param NumOfDraw Number of draws
	 */
	public void displayMoveAndGameOver(String Winner, String Role, int x, int y, int NumOfWinO, int NumOfWinX, int NumOfDraw) {
		OutputO.println("MOVED " + Role + " " + x + " " + y);
		OutputX.println("MOVED " + Role + " " + x + " " + y);
		System.out.println("Server Broadcasted: MOVED " + Role + " " + x + " " + y);
		System.out.println("Game Over");
		if (Role.equals("O")) {
			OutputO.println("WAIT");
			OutputX.println("MOVE");
		}
		else if (Role.equals("X")) {
			OutputO.println("MOVE");
			OutputX.println("WAIT");
		}
		if (Winner.equals("O")) {
			OutputO.println("WIN " + NumOfWinO + " " + NumOfWinX + " " + NumOfDraw);
			OutputX.println("LOSE " + NumOfWinO + " " + NumOfWinX + " " + NumOfDraw);
			System.out.println("O Win");
		}
		else if (Winner.equals("X")) {
			OutputO.println("LOSE " + NumOfWinO + " " + NumOfWinX + " " + NumOfDraw);
			OutputX.println("WIN " + NumOfWinO + " " + NumOfWinX + " " + NumOfDraw);
			System.out.println("X Win");
		}
		else if (Winner.equals("DRAW")) {
			OutputO.println("DRAW " + NumOfWinO + " " + NumOfWinX + " " + NumOfDraw);
			OutputX.println("DRAW " + NumOfWinO + " " + NumOfWinX + " " + NumOfDraw);
			System.out.println("Draw");
		}
		System.out.println("Player 1 Wins:" + NumOfWinO);
		System.out.println("Player 2 Wins:" + NumOfWinX);
		System.out.println("Draws:" + NumOfDraw);
	}
}
