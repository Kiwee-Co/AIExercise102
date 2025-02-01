package exercise.coding.kiwee.ai.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Server {
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final ConcurrentHashMap<Socket, String> clientNames = new ConcurrentHashMap<>();
	private static BufferedWriter logWriter;
	private static final DateTimeFormatter LOG_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) {
		try (Scanner scanner = new Scanner(System.in);) {
			int serverPort = getValidPort(scanner, "Enter server port (1025-65535): ");
			showConnectionInfo(serverPort);
			setupLogger();
			startServer(serverPort);
		}
	}

	private static void setupLogger() {
		try {
			String logFileName = "server_log_"
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".log";
			logWriter = new BufferedWriter(new FileWriter(logFileName, true));
			logMessage("SERVER_START", "Server logging initialized");
		} catch (IOException e) {
			System.out.println("Failed to initialize logger: " + e.getMessage());
		}
	}

	private static synchronized void logMessage(String type, String message) {
		String timestamp = LocalDateTime.now().format(LOG_TIMESTAMP);
		String logEntry = String.format("[%s] [%s] %s", timestamp, type, message);

		try {
			logWriter.write(logEntry + "\n");
			logWriter.flush();
			System.out.println(logEntry);
		} catch (IOException e) {
			System.out.println("Logging failed: " + e.getMessage());
		}
	}

	private static void showConnectionInfo(int port) {
		try {
			System.out.println("\n🔌 Server connection information:");
			System.out.println("   Port: " + port);

			String ips = Collections.list(NetworkInterface.getNetworkInterfaces()).stream().filter(ni -> {
				try {
					return ni.isUp() && !ni.isLoopback();
				} catch (SocketException e) {
					return false;
				}
			}).flatMap(ni -> Collections.list(ni.getInetAddresses()).stream())
					.filter(addr -> addr instanceof Inet4Address)
					.map(addr -> "   " + addr.getHostAddress() + ":" + port).collect(Collectors.joining("\n"));

			System.out.println(ips.isEmpty() ? "   No network interfaces found!" : ips);
		} catch (SocketException e) {
			System.out.println("Error showing connection info: " + e.getMessage());
		}
	}

	private static void startServer(int port) {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("\n🚀 Server started on port " + port);
			ExecutorService executor = Executors.newCachedThreadPool();

			while (true) {
				Socket clientSocket = serverSocket.accept();
				executor.submit(() -> handleClient(clientSocket));
			}
		} catch (IOException ex) {
			logMessage("ERROR", "Server failure: " + ex.getMessage());
		} finally {
			try {
				if (logWriter != null)
					logWriter.close();
			} catch (IOException e) {
				System.out.println("Error closing logger: " + e.getMessage());
			}
		}
	}

	private static void handleClient(Socket clientSocket) {
		String clientIp = clientSocket.getInetAddress().getHostAddress();
		int clientPort = clientSocket.getPort();

		try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
			// Step 1: Receive and register client name
			String clientName = in.readLine();
			if (clientName == null || clientName.trim().isEmpty()) {
				logMessage("ERROR", "Invalid name from " + clientIp + ":" + clientPort);
				return;
			}
			clientNames.put(clientSocket, clientName);
			logMessage("JOIN", String.format("%s (%s:%d) joined", clientName, clientIp, clientPort));
			broadcastMessage(clientName + " has joined the chat", clientSocket);

			// Step 2: Handle messages
			String message;
			while ((message = in.readLine()) != null) {
				if (message.equalsIgnoreCase("/exit"))
					break;
				logMessage("MESSAGE", String.format("%s (%s:%d): %s", clientName, clientIp, clientPort, message));
				broadcastMessage(formatChatMessage(clientName, message), clientSocket);
			}

			logMessage("LEAVE", String.format("%s (%s:%d) left", clientName, clientIp, clientPort));
			broadcastMessage(clientName + " has left the chat", clientSocket);
		} catch (IOException e) {
			logMessage("ERROR", "Client error: " + e.getMessage());
		} finally {
			clientNames.remove(clientSocket);
			closeSocket(clientSocket);
		}
	}

	private static String formatChatMessage(String name, String message) {
		return String.format("[%s] %s: %s", LocalDateTime.now().format(TIME_FORMAT), name, message);
	}

	private static void broadcastMessage(String message, Socket excludeSocket) {
		clientNames.forEach((socket, name) -> {
			if (socket != excludeSocket && !socket.isClosed()) {
				try {
					PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
					writer.println(message);
				} catch (IOException e) {
					System.out.println("Error broadcasting to client: " + e.getMessage());
				}
			}
		});
	}

	private static void closeSocket(Socket socket) {
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			logMessage("ERROR", "Socket close error: " + e.getMessage());
		}
	}

	private static int getValidPort(Scanner scanner, String prompt) {
		while (true) {
			System.out.print(prompt);
			try {
				int port = scanner.nextInt();
				scanner.nextLine();
				if (port > 1024 && port <= 65535)
					return port;
				System.out.println("Invalid port! Must be between 1025-65535");
			} catch (InputMismatchException e) {
				System.out.println("Invalid number format!");
				scanner.nextLine();
			}
		}
	}
}