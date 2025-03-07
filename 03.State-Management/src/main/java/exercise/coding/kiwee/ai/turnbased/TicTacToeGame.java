package exercise.coding.kiwee.ai.turnbased;

import java.util.Arrays;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TicTacToeGame extends Application {
	private char[] board = new char[9];
	private char currentPlayer; // Set by START message
	private Label statusLabel;
	private Button[] buttons = new Button[9];
	private NetworkHandler networkHandler;
	private boolean isHost;
	private String playerName;
	private String opponentName;
	private boolean isConnected = false;
	private Button hostButton;
	private Button joinButton;

	@Override
	public void start(Stage primaryStage) {
		Arrays.fill(board, ' ');

		VBox root = new VBox(10);
		GridPane grid = new GridPane();
		statusLabel = new Label("Enter your name, IP, and port to connect or host");

		for (int i = 0; i < 9; i++) {
			final int pos = i;
			buttons[i] = new Button(" ");
			buttons[i].setMinSize(50, 50);
			buttons[i].setDisable(true); // Disable buttons initially
			buttons[i].setOnAction(e -> makeMove(pos));
			grid.add(buttons[i], i % 3, i / 3);
		}

		TextField nameField = new TextField("Player");
		TextField ipField = new TextField("localhost");
		TextField portField = new TextField("5000");
		hostButton = new Button("Host Game");
		joinButton = new Button("Join Game");

		hostButton.setOnAction(e -> startHost(nameField.getText(), portField.getText()));
		joinButton.setOnAction(e -> startClient(nameField.getText(), ipField.getText(), portField.getText()));

		root.getChildren().addAll(statusLabel, grid, nameField, ipField, portField, hostButton, joinButton);

		Scene scene = new Scene(root, 300, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Tic-Tac-Toe");
		primaryStage.show();
	}

	private void startHost(String name, String portStr) {
		try {
			int port = Integer.parseInt(portStr);
			playerName = name.isEmpty() ? "Host" : name;
			isHost = true;
			networkHandler = new NetworkHandler(this, true, null, port);
			updateStatus(playerName + " hosting on port " + port + "... Waiting for player");
		} catch (NumberFormatException e) {
			updateStatus("Invalid port number");
		}
	}

	private void startClient(String name, String ip, String portStr) {
		try {
			int port = Integer.parseInt(portStr);
			playerName = name.isEmpty() ? "Client" : name;
			isHost = false;
			networkHandler = new NetworkHandler(this, false, ip, port);
			updateStatus(playerName + " connecting to " + ip + ":" + port);
		} catch (NumberFormatException e) {
			updateStatus("Invalid port number");
		}
	}

	private void makeMove(int position) {
		if (board[position] == ' ' && isMyTurn() && isConnected) {
			board[position] = currentPlayer;
			buttons[position].setText(String.valueOf(currentPlayer));
			networkHandler.sendMove(position, currentPlayer);
			checkGameState();
			switchPlayer();
		}
	}

	public void receiveMove(GameMessage message) {
		Platform.runLater(() -> {
			board[message.getPosition()] = message.getPlayer();
			buttons[message.getPosition()].setText(String.valueOf(message.getPlayer()));
			currentPlayer = (message.getPlayer() == 'X') ? 'O' : 'X';
			checkGameState();
			updateStatus(isMyTurn() ? playerName + "'s turn" : opponentName + "'s turn");
		});
	}

	public void setOpponentName(String name) {
		this.opponentName = name;
		isConnected = true;
		enableBoard();
		// Don’t set turn status here; wait for START
		updateStatus("Connected to " + opponentName + "...");
	}

	public void setStartingPlayer(char startingPlayer) {
		this.currentPlayer = startingPlayer;
		String startingName = (startingPlayer == 'X' && isHost) || (startingPlayer == 'O' && !isHost) ? playerName
				: opponentName;
		Platform.runLater(() -> {
			updateStatus(startingName + "'s turn");
		});
	}

	public void gameOver(String winner) {
		Platform.runLater(() -> {
			if ("Nobody".equals(winner)) {
				statusLabel.setText("Draw!");
			} else {
				statusLabel.setText(winner + " wins!");
			}
			disableBoard();
		});
	}

	private void switchPlayer() {
		currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
	}

	private boolean isMyTurn() {
		return (isHost && currentPlayer == 'X') || (!isHost && currentPlayer == 'O');
	}

	private void checkGameState() {
		int[][] wins = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, { 0, 4, 8 },
				{ 2, 4, 6 } };
		for (int[] win : wins) {
			if (board[win[0]] != ' ' && board[win[0]] == board[win[1]] && board[win[1]] == board[win[2]]) {
				String winner = (board[win[0]] == 'X' && isHost) || (board[win[0]] == 'O' && !isHost) ? playerName
						: opponentName;
				networkHandler.sendGameOver(winner);
				gameOver(winner);
				return;
			}
		}

		boolean isFull = true;
		for (char c : board) {
			if (c == ' ') {
				isFull = false;
				break;
			}
		}
		if (isFull) {
			networkHandler.sendGameOver("Nobody");
			gameOver("Nobody");
		}
	}

	private void disableBoard() {
		for (Button button : buttons) {
			button.setDisable(true);
		}
	}

	private void enableBoard() {
		for (Button button : buttons) {
			button.setDisable(false);
		}
	}

	public char getCurrentPlayer() {
		return currentPlayer;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void updateStatus(String message) {
		Platform.runLater(() -> statusLabel.setText(message));
	}

	public void disableConnectionButtons() {
		Platform.runLater(() -> {
			hostButton.setDisable(true);
			joinButton.setDisable(true);
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
