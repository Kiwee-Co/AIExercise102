// GameUI.java
package exercise.coding.kiwee.ai.latency;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class GameUI {
    private VBox root;
    private Label statusLabel;
    private Label countdownLabel;
    private TextField ipField;
    private TextField portField;
    private TextField nameField;
    private TextField countdownField;
    private Label ipDisplayLabel;
    private Button hostButton;
    private Button connectButton;
    private Button pingBackButton;
    private Button confirmNameButton;
    private Button replayButton;
    private Label verifyNumberLabel;
    private TextField verifyNumberField;
    private Label statusBar;
    private GameNetwork network;
    private volatile long gameStartTime;
    private volatile double initialCountdown;
    private int randomVerifyNumber;
    private volatile boolean gameActive;
    private String playerName;

    public GameUI() {
        network = new GameNetwork(this);
        initializeUI();
    }

    private void initializeUI() {
        root = new VBox(15);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Name Entry Section
        var nameSection = new VBox(8);
        nameSection.setAlignment(Pos.CENTER);
        var namePromptLabel = new Label("Enter Your Name:");
        namePromptLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
        nameField = new TextField("Player");
        nameField.setPrefWidth(200);
        nameField.setStyle("-fx-font-size: 12px;");
        confirmNameButton = new Button("Confirm Name");
        confirmNameButton.setPrefWidth(120);
        confirmNameButton.setStyle("-fx-font-size: 12px;");
        nameSection.getChildren().addAll(namePromptLabel, nameField, confirmNameButton);

        // Connection panel
        var connectionGrid = new GridPane();
        connectionGrid.setHgap(10);
        connectionGrid.setVgap(10);
        connectionGrid.setAlignment(Pos.CENTER);

        var hostLabel = new Label("Host:");
        hostLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        ipField = new TextField("localhost");
        ipField.setPrefWidth(150);
        ipField.setStyle("-fx-font-size: 10px;");
        ipField.setDisable(true);

        var portLabel = new Label("Port:");
        portLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        portField = new TextField("5555");
        portField.setPrefWidth(80);
        portField.setStyle("-fx-font-size: 10px;");
        portField.setDisable(true);

        var countdownLabelText = new Label("Countdown (sec):");
        countdownLabelText.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        countdownField = new TextField("45");
        countdownField.setPrefWidth(80);
        countdownField.setStyle("-fx-font-size: 10px;");
        countdownField.setDisable(true);

        hostButton = new Button("Host Game");
        hostButton.setPrefWidth(100);
        hostButton.setStyle("-fx-font-size: 10px;");
        hostButton.setDisable(true);
        connectButton = new Button("Connect");
        connectButton.setPrefWidth(100);
        connectButton.setStyle("-fx-font-size: 10px;");
        connectButton.setDisable(true);

        connectionGrid.add(hostLabel, 0, 0);
        connectionGrid.add(ipField, 1, 0);
        connectionGrid.add(portLabel, 0, 1);
        connectionGrid.add(portField, 1, 1);
        connectionGrid.add(countdownLabelText, 0, 2);
        connectionGrid.add(countdownField, 1, 2);
        connectionGrid.add(hostButton, 2, 0);
        connectionGrid.add(connectButton, 2, 1);

        // IP Display
        ipDisplayLabel = new Label("Your IP Address: " + network.getPrivateIP());
        ipDisplayLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        // Game status area
        statusLabel = new Label("Enter your name to begin");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");

        countdownLabel = new Label(countdownField.getText() + ".000");
        countdownLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");

        // Verification Section
        var verifySection = new HBox(8);
        verifySection.setAlignment(Pos.CENTER);
        verifySection.setPadding(new Insets(8));

        verifyNumberLabel = new Label("Verify Number: -");
        verifyNumberLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
        verifyNumberLabel.setVisible(false);

        verifyNumberField = new TextField();
        verifyNumberField.setPromptText("Enter number");
        verifyNumberField.setPrefWidth(80);
        verifyNumberField.setStyle("-fx-font-size: 12px;");
        verifyNumberField.setDisable(true);
        verifyNumberField.setVisible(false);

        verifySection.getChildren().addAll(verifyNumberLabel, verifyNumberField);

        pingBackButton = new Button("Ping Back");
        pingBackButton.setPrefWidth(120);
        pingBackButton.setStyle("-fx-font-size: 12px;");
        pingBackButton.setDisable(true);

        replayButton = new Button("Replay");
        replayButton.setPrefWidth(120);
        replayButton.setStyle("-fx-font-size: 12px;");
        replayButton.setDisable(true);
        replayButton.setVisible(false);

        var statusBox = new VBox(15, statusLabel, countdownLabel, verifySection, pingBackButton, replayButton);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPadding(new Insets(10));
        statusBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        // Status bar
        statusBar = new Label("Ready - Please confirm your name");
        statusBar.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5; -fx-font-size: 10px; -fx-text-fill: #333333;");
        statusBar.setMaxWidth(Double.MAX_VALUE);
        statusBar.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(nameSection, connectionGrid, ipDisplayLabel, statusBox, statusBar);

        // Event handlers
        confirmNameButton.setOnAction(e -> confirmName());
        hostButton.setOnAction(e -> startHost());
        connectButton.setOnAction(e -> startClient());
        pingBackButton.setOnAction(e -> pingBack());
        replayButton.setOnAction(e -> replayGame());
    }

    private void confirmName() {
        if (nameField.getText().trim().isEmpty()) {
            updateStatus("Please enter a valid name");
            updateStatusBar("Error: Name cannot be empty");
            System.out.println("Name confirmation failed: Empty name");
        } else {
            playerName = nameField.getText();
            updateStatus("Name confirmed: " + playerName);
            updateStatusBar("Name confirmed - Ready to play");
            nameField.setDisable(true);
            confirmNameButton.setDisable(true);
            ipField.setDisable(false);
            portField.setDisable(false);
            countdownField.setDisable(false);
            hostButton.setDisable(false);
            connectButton.setDisable(false);
            System.out.println("Name confirmed: " + playerName);
        }
    }

    private void startHost() {
        try {
            initialCountdown = Double.parseDouble(countdownField.getText());
            network.startServer(Integer.parseInt(portField.getText()), playerName, initialCountdown);
            updateIPDisplay("Your IP Address: " + network.getPrivateIP() + ":" + portField.getText());
            updateStatus("Hosting on " + network.getPrivateIP() + ":" + portField.getText());
            updateStatusBar("Waiting for opponent to connect...");
            connectButton.setDisable(true);
            System.out.println("Host waiting for client...");
        } catch (NumberFormatException e) {
            updateStatus("Invalid countdown time");
            updateStatusBar("Error: Invalid countdown value");
            System.out.println("Host start failed: " + e.getMessage());
        }
    }

    private void startClient() {
        network.startClient(ipField.getText(), Integer.parseInt(portField.getText()), playerName);
        updateStatus("Connecting to " + ipField.getText() + ":" + portField.getText());
        updateStatusBar("Connecting to host...");
        hostButton.setDisable(true);
        System.out.println("Client started");
    }

    private void pingBack() {
        try {
            var enteredNumber = Integer.parseInt(verifyNumberField.getText());
            if (enteredNumber == randomVerifyNumber) {
                var currentTime = System.currentTimeMillis();
                var remainingTime = initialCountdown - (currentTime - gameStartTime) / 1000.0;
                if (remainingTime <= 0) {
                    network.gameOver(network.getOpponentName() + " wins!"); // Opponent wins if ping is too late
                    System.out.println(
                            "Ping attempted too late by " + playerName + ", declaring " + network.getOpponentName() + " as winner");
                    return;
                }
                var newVerifyNumber = generateVerifyNumber(remainingTime);
                network.sendPing(newVerifyNumber + ":" + gameStartTime + ":" + initialCountdown + ":" + playerName);
                pingBackButton.setDisable(true);
                verifyNumberField.setDisable(true);
                updateStatusBar("Ping sent! Waiting for opponent's ping...");
                System.out.println("Ping sent by " + playerName + ": " + newVerifyNumber + " with start time: " + gameStartTime
                        + ", countdown: " + initialCountdown);
            } else {
                updateStatusBar("Incorrect number! Please retry.");
                verifyNumberField.setText("");
            }
        } catch (NumberFormatException e) {
            updateStatusBar("Invalid input! Enter a number.");
            verifyNumberField.setText("");
        }
    }

    private int generateVerifyNumber(double remainingTime) {
        if (remainingTime <= 10.0) {
            return (int) (Math.random() * 900) + 100; // 100-999
        }
        if (remainingTime <= 30.0) {
            return (int)(Math.random() * 90) + 10; // 10-99
        } else {
            return (int)(Math.random() * 9) + 1; // 1-9
        }
    }

    private void replayGame() {
        initialCountdown = Double.parseDouble(countdownField.getText());
        countdownLabel.setText(String.format("%.3f", initialCountdown));
        pingBackButton.setDisable(true);
        verifyNumberLabel.setVisible(false);
        verifyNumberField.setVisible(false);
        replayButton.setVisible(false);
        replayButton.setDisable(true);
        updateStatus("Game reset - Ready to play as " + playerName);
        updateStatusBar("Game reset - Ready to play");
        ipField.setDisable(false);
        portField.setDisable(false);
        countdownField.setDisable(false);
        hostButton.setDisable(false);
        connectButton.setDisable(false);
        network.reset();
        gameActive = false;
        System.out.println("Replay initiated for " + playerName);
    }

    public void updateStatus(String status) {
        Platform.runLater(() -> statusLabel.setText(status));
    }

    public void updateIPDisplay(String ip) {
        Platform.runLater(() -> ipDisplayLabel.setText(ip));
    }

    public void updateStatusBar(String status) {
        Platform.runLater(() -> {
            statusBar.setText(status);
            if (status.contains("Game over")) {
                pingBackButton.setDisable(true);
                verifyNumberLabel.setVisible(false);
                verifyNumberField.setVisible(false);
                replayButton.setVisible(true);
                replayButton.setDisable(false);
                gameActive = false;
            }
        });
    }

    public void startCountdown(long startTime) {
        this.gameStartTime = startTime;
        gameActive = true;
        randomVerifyNumber = generateVerifyNumber(initialCountdown);
        Platform.runLater(() -> {
            verifyNumberLabel.setText("Verify Number: " + randomVerifyNumber);
            verifyNumberLabel.setVisible(true);
            verifyNumberField.setVisible(true);
            verifyNumberField.setDisable(false);
            verifyNumberField.setText("");
            pingBackButton.setDisable(false);
            updateStatusBar("Your turn! Enter the number and ping back!");
            System.out.println("startCountdown: Ping Back enabled for " + playerName + " with start time: " + gameStartTime);
        });

        new Thread(() -> {
            while (gameActive) {
                var now = System.currentTimeMillis();
                var remainingTime = initialCountdown - (now - gameStartTime) / 1000.0;

                Platform.runLater(() -> {
                    countdownLabel.setText(String.format("%.3f", Math.max(0, remainingTime)));
                    if (remainingTime <= 10.0) {
                        countdownLabel.setTextFill(Color.RED);
                    } else if (remainingTime <= 30.0) {
                        countdownLabel.setTextFill(Color.ORANGE);
                    } else {
                        countdownLabel.setTextFill(Color.BLACK);
                    }
                });

                if (remainingTime <= 0 && gameActive && !pingBackButton.isDisable()) {
                    network.gameOver(network.getOpponentName() + " wins!"); // Opponent wins if local turn expires
                    System.out.println("Countdown expired for " + playerName + " with Ping Back enabled, declaring "
                            + network.getOpponentName() + " as winner");
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setTimeRemaining(long startTime, int verifyNumber, double countdown) {
        this.gameStartTime = startTime;
        this.initialCountdown = countdown;
        gameActive = true;
        Platform.runLater(() -> {
            var now = System.currentTimeMillis();
            var remainingTime = initialCountdown - (now - gameStartTime) / 1000.0;
            countdownLabel.setText(String.format("%.3f", Math.max(0, remainingTime)));
            if (verifyNumber >= 0 && remainingTime > 0) {
                randomVerifyNumber = verifyNumber;
                verifyNumberLabel.setText("Verify Number: " + randomVerifyNumber);
                verifyNumberLabel.setVisible(true);
                verifyNumberField.setVisible(true);
                verifyNumberField.setDisable(false);
                verifyNumberField.setText("");
                pingBackButton.setDisable(false);
                updateStatusBar("Opponent pinged! Verify and ping back!");
            } else {
                updateStatusBar("Waiting for opponent's ping...");
            }
            System.out.println("setTimeRemaining: Called for " + playerName + " with verifyNumber: " + verifyNumber + ", start time: "
                    + gameStartTime + ", countdown: " + initialCountdown);
        });

        new Thread(() -> {
            while (gameActive) {
                var now = System.currentTimeMillis();
                var remainingTime = initialCountdown - (now - gameStartTime) / 1000.0;

                Platform.runLater(() -> {
                    countdownLabel.setText(String.format("%.3f", Math.max(0, remainingTime)));
                    if (remainingTime <= 10.0) {
                        countdownLabel.setTextFill(Color.RED);
                    } else if (remainingTime <= 30.0) {
                        countdownLabel.setTextFill(Color.ORANGE);
                    } else {
                        countdownLabel.setTextFill(Color.BLACK);
                    }
                });

                if (remainingTime <= 0 && gameActive && !pingBackButton.isDisable()) {
                    network.gameOver(network.getOpponentName() + " wins!"); // Opponent wins if local turn expires
                    System.out.println("Countdown expired for " + playerName + " with Ping Back enabled, declaring "
                            + network.getOpponentName() + " as winner");
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void enableControls(boolean enable) {
        Platform.runLater(() -> {
            hostButton.setDisable(!enable);
            connectButton.setDisable(!enable);
            pingBackButton.setDisable(true);
        });
    }

    public String getCountdownText() {
        return countdownLabel.getText();
    }

    public TextField getCountdownField() {
        return countdownField;
    }

    public VBox getRoot() {
        return root;
    }

    public String getPlayerName() {
        return playerName;
    }
}