package exercise.coding.kiwee.ai.turnbased;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NetworkHandler {
	private TicTacToeGame game;
	private ServerSocket serverSocket;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private ObjectMapper mapper = new ObjectMapper();
	private static final int MAX_RETRIES = 3;
	private static final int RETRY_DELAY = 2000;
	private final int port;

	public NetworkHandler(TicTacToeGame game, boolean isHost, String ip, int port) {
		this.game = game;
		this.port = port;
		new Thread(() -> {
			try {
				if (isHost) {
					setupHost();
				} else {
					setupClient(ip);
				}
				listenForMessages();
			} catch (IOException e) {
				game.updateStatus("Connection error: " + e.getMessage());
			}
		}).start();
	}

	private void setupHost() throws IOException {
		try {
			serverSocket = new ServerSocket(port);
			game.disableConnectionButtons();
			game.updateStatus(game.getPlayerName() + " hosting on port " + port + "... Waiting for player");
			socket = serverSocket.accept();
			setupStreams();
			sendName(game.getPlayerName());
			Random rand = new Random();
			char startingPlayer = rand.nextBoolean() ? 'X' : 'O';
			sendStart(startingPlayer);
			game.setStartingPlayer(startingPlayer);
		} catch (IOException e) {
			game.updateStatus("Failed to host on port " + port + ": " + e.getMessage());
			throw e;
		}
	}

	private void setupClient(String ip) throws IOException {
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				socket = new Socket(ip, port);
				setupStreams();
				game.disableConnectionButtons();
				sendName(game.getPlayerName());
				return;
			} catch (IOException e) {
				if (i == MAX_RETRIES - 1) {
					game.updateStatus("Failed to connect to " + ip + ":" + port + " after retries");
					throw e;
				}
				try {
					Thread.sleep(RETRY_DELAY);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	private void setupStreams() throws IOException {
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void sendMove(int position, char player) {
		GameMessage msg = new GameMessage("MOVE", player, position, null);
		sendMessageWithRetry(msg);
	}

	public void sendGameOver(String winner) {
		GameMessage msg = new GameMessage("GAME_OVER", ' ', -1, winner);
		sendMessageWithRetry(msg);
		// Ensure message is flushed
		if (out != null) {
			out.flush();
		}
	}

	private void sendName(String name) {
		GameMessage msg = new GameMessage("NAME", ' ', -1, name);
		sendMessageWithRetry(msg);
	}

	private void sendStart(char startingPlayer) {
		GameMessage msg = new GameMessage("START", startingPlayer, -1, null);
		sendMessageWithRetry(msg);
	}

	private void sendMessageWithRetry(GameMessage msg) {
		if (out == null) {
			game.updateStatus("Cannot send message: Connection not established");
			return;
		}
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				String json = mapper.writeValueAsString(msg);
				out.println(json);
				out.flush(); // Ensure message is sent immediately
				return;
			} catch (Exception e) {
				if (i == MAX_RETRIES - 1) {
					game.updateStatus("Failed to send message after retries: " + e.getMessage());
				}
				try {
					Thread.sleep(RETRY_DELAY);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	private void listenForMessages() throws IOException {
		String line;
		while ((line = in.readLine()) != null) {
			try {
				GameMessage msg = mapper.readValue(line, GameMessage.class);
				if ("MOVE".equals(msg.getType())) {
					game.receiveMove(msg);
				} else if ("NAME".equals(msg.getType())) {
					game.setOpponentName(msg.getMessage());
				} else if ("START".equals(msg.getType())) {
					game.setStartingPlayer(msg.getPlayer());
				} else if ("GAME_OVER".equals(msg.getType())) {
					game.gameOver(msg.getMessage());
				}
			} catch (Exception e) {
				game.updateStatus("Error processing message: " + e.getMessage());
			}
		}
	}
}
