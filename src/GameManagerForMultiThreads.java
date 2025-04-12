/**
* The GameManagerForMultiThreads program implements a Tic-Tac-Toe Game Manager.
* Each manager is responsible for one game and two players.
* Most of the game logic is managed here.
* Multiple round of games is supported.
*
* @author Vincent Tong
* @version 1.0
* @since 2024-12-05
*/

public class GameManagerForMultiThreads {
	
	private TicTacToeServer ticTacToeServer;
	
	private TicTacToeGame Game;
	private boolean isOAssigned = false;
	private boolean isXAssigned = false;
	private boolean allowMovingO = false;
	private boolean allowMovingX = false;
	private boolean isGameRunning = false;
	private int NumOfWinO = 0;
	private int NumOfWinX = 0;
	private int NumOfDraw = 0;
	
	/**
	 * Create a game manager of Tic-Tac-Toe Game
	 * @param ticTacToeServer The TicTacToeServer that is interacting with the game manager. Used for sending back information to the clients.
	 */
	public GameManagerForMultiThreads(TicTacToeServer ticTacToeServer) {
		this.ticTacToeServer = ticTacToeServer;
	}
	
	/**
	 * Assign a role (O or X) to the caller.
	 * @param PerferredRole Caller's preferred role (O or X). Try to assign the preferred role first.
	 * @return Assigned role to the caller.
	 */
	public synchronized String getRole(String PerferredRole) {

		// Special Case: Try to assign X first if X is preferred
		// Failing to assign will fall back to `Default Action`
		if (PerferredRole.equals("X") && !isXAssigned) {
			isXAssigned = true;
			return "X";
		}	

		// Default Action: If caller has no preferred role or preferred role is O
		// Try to assign O first, then X
		if (!isOAssigned) {
			isOAssigned = true;
			return "O";
		}
		else if (!isXAssigned) {
			isXAssigned = true;
			return "X";
		}
		
		// All roles are occupied
		return "";
	}
	
	/**
	 * If all roles are assigned, the game will be started.
	 * Otherwise, it will do nothing.
	 */
	public synchronized void tryStartNewGame() {
		if (isOAssigned && isXAssigned && (!isGameRunning)) {
			Game = new TicTacToeGame();
			allowMovingO = true;
			allowMovingX = false;
			isGameRunning = true;
			ticTacToeServer.notifyGameStarted();
		}
	}
	
	/**
	 * Make a move on the grid. Constraints will be checked.
	 * @param Role Who is trying to move (X or O).
	 * @param x X coordinate of the requested grid.
	 * @param y Y coordinate of the requested grid.
	 */
	public synchronized void move(String Role, int x, int y) {
		if ( allowMoving(Role) && Game.isGridEmpty(x, y) ) {
			String Winner = Game.move(Role, x, y);
			if (Winner.equals("")) {
				ticTacToeServer.displayMoveAndGameContinue(Role, x, y);
				switchTurn();
			} else {
				GameOverHandler(Winner, Role, x, y);
			}
		}
	}
	
	private boolean allowMoving(String Role) {
		if (Role.equals("O")) {
			return allowMovingO;
		}
		else if (Role.equals("X")) {
			return allowMovingX;
		}
		return false;
	}
	
	private void switchTurn() {
		if (allowMovingO) {
			allowMovingO = false;
			allowMovingX = true;
		}
		else if (allowMovingX) {
			allowMovingX = false;
			allowMovingO = true;
		}
	}
	
	private void GameOverHandler(String Winner, String Role, int x, int y) {
		isOAssigned = false;
		isXAssigned = false;
		allowMovingO = false;
		allowMovingX = false;
		isGameRunning = false;
		if (Winner.equals("O")) {
			NumOfWinO++;
		}
		else if (Winner.equals("X")) {
			NumOfWinX++;
		}
		else if (Winner.equals("DRAW")) {
			NumOfDraw++;
		}
		ticTacToeServer.displayMoveAndGameOver(Winner, Role, x, y, NumOfWinO, NumOfWinX, NumOfDraw);
	}
}
