// GameNetwork.java
package exercise.coding.kiwee.ai.latency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;

public class GameNetwork {
    private GameUI ui;
    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ExecutorService executor = Executors.newFixedThreadPool(2);
    private boolean isHost;
    private String playerName;
    private String opponentName;
    private volatile boolean gameEnded;

    public GameNetwork(GameUI ui) {
        this.ui = ui;
        this.gameEnded = false;
    }

    public void startServer(int port, String name, double initialCountdown) {
        isHost = true;
        this.playerName = name;
        executor.submit(() -> {
            try {
                serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();
                var gameStartTime = System.currentTimeMillis();
                setupStreams();
                var clientMessage = in.readLine();
                if (clientMessage != null && clientMessage.startsWith("CLIENT:")) {
                    opponentName = clientMessage.split(":")[1];
                    System.out.println("Host " + playerName + " received client name: " + opponentName);
                } else {
                    opponentName = "Player2";
                    System.err.println("Host " + playerName + " didn’t receive client name, defaulting to " + opponentName);
                }
                out.println("SETUP:" + gameStartTime + ":" + initialCountdown + ":" + playerName);
                ui.updateStatus("Connected! Starting game as " + playerName);
                ui.updateStatusBar("Connected! Game starting...");
                ui.enableControls(false);
                ui.startCountdown(gameStartTime);
                listenForMessages();
            } catch (IOException e) {
                ui.updateStatus("Error: " + e.getMessage());
                ui.updateStatusBar("Error: Failed to host game");
            }
        });
    }

    public void startClient(String ip, int port, String name) {
        isHost = false;
        this.playerName = name;
        executor.submit(() -> {
            try {
                socket = new Socket(ip, port);
                setupStreams();
                out.println("CLIENT:" + playerName);
                ui.updateStatus("Connected! Waiting for host as " + playerName);
                ui.updateStatusBar("Connected! Waiting for host's first ping...");
                ui.enableControls(false);
                listenForMessages();
            } catch (IOException e) {
                ui.updateStatus("Error: " + e.getMessage());
                ui.updateStatusBar("Error: Failed to connect");
            }
        });
    }

    private void setupStreams() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void listenForMessages() {
        executor.submit(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("SETUP:")) {
                        var parts = message.split(":");
                        var startTime = Long.parseLong(parts[1]);
                        var countdown = Double.parseDouble(parts[2]);
                        opponentName = parts[3];
                        ui.setTimeRemaining(startTime, -1, countdown);
                        Platform.runLater(() -> {
                            ui.updateStatus("Game started! Waiting for " + opponentName + "'s ping");
                        });
                        System.out.println("Client " + playerName + " set opponentName to " + opponentName);
                    } else if (message.startsWith("PING:")) {
                        var parts = message.split(":");
                        var verifyNumber = Integer.parseInt(parts[1]);
                        var startTime = Long.parseLong(parts[2]);
                        var countdown = Double.parseDouble(parts[3]);
                        var senderName = parts[4];
                        if (isHost) {
                            opponentName = senderName;
                            System.out.println("Host " + playerName + " confirmed opponentName as " + opponentName);
                        }
                        ui.setTimeRemaining(startTime, verifyNumber, countdown);
                        Platform.runLater(() -> {
                            ui.updateStatus("Received ping from " + opponentName);
                        });
                    } else if (message.startsWith("GAMEOVER:")) {
                        var winnerMessage = message.split(":")[1];
                        gameEnded = true;
                        ui.updateStatus(winnerMessage);
                        ui.updateStatusBar("Game over: " + winnerMessage);
                        System.out.println(playerName + " received GAMEOVER: " + winnerMessage);
                        Thread.sleep(500);
                        cleanup();
                        break;
                    }
                }
            } catch (IOException e) {
                if (!gameEnded) {
                    ui.updateStatus("Error: " + e.getMessage());
                    ui.updateStatusBar("Error: Connection issue - " + e.getMessage());
                }
                cleanup();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void sendPing(String message) {
        out.println("PING:" + message);
    }

    public void gameOver(String message) {
        gameEnded = true;
        out.println("GAMEOVER:" + message);
        ui.updateStatus(message);
        ui.updateStatusBar("Game over: " + message);
        System.out.println(playerName + " sent GAMEOVER: " + message);
        executor.submit(() -> {
            try {
                Thread.sleep(500);
                cleanup();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public String getPrivateIP() {
        try {
            var interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : Collections.list(interfaces)) {
                var addresses = ni.getInetAddresses();
                for (InetAddress addr : Collections.list(addresses)) {
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
            return "localhost";
        } catch (SocketException e) {
            return "localhost";
        }
    }

    public void reset() {
        cleanup();
        executor = Executors.newFixedThreadPool(2);
        opponentName = null;
        gameEnded = false;
    }

    private void cleanup() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            if (!gameEnded) {
                System.err.println("Cleanup error: " + e.getMessage());
            }
        }
    }

    public String getOpponentName() {
        if (opponentName == null || opponentName.isEmpty()) {
            System.err.println("Warning: opponentName is null or empty for " + playerName + ", defaulting to 'Unknown'");
            return "Unknown";
        }
        return opponentName;
    }
}