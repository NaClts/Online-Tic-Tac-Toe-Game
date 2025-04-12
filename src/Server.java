import java.io.IOException;
import java.net.ServerSocket;

/**
* The Server program acts as an entry point to the Tic-Tac-Toe Server.
*
* @author Vincent Tong
* @version 1.0
* @since 2024-12-05
*/

public class Server {

	/**
	 * This is the main method which creates a new Tic-Tac-Toe Server and starts it.
	 * @param args Unused.
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Server is Running...");
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				System.out.println("Server Stopped.");
			}
		}));

		try (var listener = new ServerSocket(52396)) {
			TicTacToeServer myServer = new TicTacToeServer(listener);
			myServer.start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
