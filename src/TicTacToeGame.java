/**
* A basic and simple single-round Tic-Tac-Toe game.
* Accepts only 1 input at any time.
*
* @author Vincent Tong
* @version 1.0
* @since 2024-12-05
*/

public class TicTacToeGame {
	
	private String[][] Grids = new String[3][3];
	private int NumberOfMove = 0;
	
	/**
	 * Creates a basic Tic-Tac-Toe game and initialize all grids to be empty.
	 */
	public TicTacToeGame() {
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				Grids[i][j] = "";
			}
		}
	}
	
	/**
	 * Make a move on the grids. Constraints will not be checked, but winner will be checked.
	 * @param Role Who is trying to move (X or O).
	 * @param x X coordinate of the requested grid.
	 * @param y Y coordinate of the requested grid.
	 * @return Winner (if any) after the move.
	 */
	public String move(String Role, int x, int y) {
		Grids[x][y] = Role;
		NumberOfMove++;
		return getWinner();
	}
	
	/**
	 * Check if the required grid is empty.
	 * @param x X coordinate of the required grid.
	 * @param y Y coordinate of the required grid.
	 * @return
	 */
	public boolean isGridEmpty(int x, int y) {
		if (Grids[x][y].equals("")) {
			return true;
		} else {
			return false;
		}
	}
	
	private String getWinner() {
		
		// Check columns
		for (int i=0; i<3; i++) {
			if ( (!Grids[i][0].equals("")) && Grids[i][0].equals(Grids[i][1]) && Grids[i][1].equals(Grids[i][2]) ) {
				return Grids[i][0];
			}	
		}
		
		// Check rows
		for (int i=0; i<3; i++) {
			if ( (!Grids[0][i].equals("")) && Grids[0][i].equals(Grids[1][i]) && Grids[1][i].equals(Grids[2][i]) ) {
				return Grids[0][i];
			}
		}
		
		// Check two diagonals
		if ( (!Grids[1][1].equals("")) && Grids[0][0].equals(Grids[1][1]) && Grids[1][1].equals(Grids[2][2]) ) {
			return Grids[1][1];
		}
		if ( (!Grids[1][1].equals("")) && Grids[0][2].equals(Grids[1][1]) && Grids[1][1].equals(Grids[2][0]) ) {
			return Grids[1][1];
		}
		
		if (NumberOfMove >= 9) {
			return "DRAW";
		}
		
		// No winner
		return "";
	}
	
}
