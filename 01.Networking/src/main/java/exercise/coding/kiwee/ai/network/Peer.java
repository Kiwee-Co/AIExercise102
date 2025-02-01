package exercise.coding.kiwee.ai.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Peer {
	private static final int TIMEOUT = 60*60;
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final int MAX_WIDTH = 60;
	private static final String SEPARATOR = "-".repeat(MAX_WIDTH);

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter your name: ");
		String name = scanner.nextLine();

		int myPort = getValidPort(scanner, "Enter your listening port (1025-65535): ");
		Set<String> localIps = getNetworkInfo(myPort);

		String peerIp;
		int peerPort;
		boolean validConnection;

		do {
			System.out.println("\n🔗 Where would you like to connect to?");
			String connectionInput = getConnectionInput(scanner);

			if (!connectionInput.isEmpty()) {
				String[] parts = connectionInput.split(":");
				peerIp = parts[0];
				peerPort = Integer.parseInt(parts[1]);
			} else {
				peerIp = getPeerIp(scanner);
				peerPort = getValidPort(scanner, "Enter peer's port: ");
			}

			validConnection = validateConnection(myPort, peerIp, peerPort, localIps);
		} while (!validConnection);

		connectToPeer(name, myPort, peerIp, peerPort);
		scanner.close();
	}

	private static int getValidPort(Scanner scanner, String prompt) {
		while (true) {
			System.out.print(prompt);
			try {
				int port = scanner.nextInt();
				scanner.nextLine(); // Consume newline
				if (port > 1024 && port <= 65535) {
					return port;
				}
				System.out.println("Invalid port! Must be between 1025-65535");
			} catch (InputMismatchException e) {
				System.out.println("Invalid number format!");
				scanner.nextLine(); // Clear invalid input
			}
		}
	}

	private static Set<String> getNetworkInfo(int port) {
		Set<String> ips = new HashSet<>();
		try {
			System.out.println("\n🏠 Your connection information:");
			System.out.println("Port: " + port);

			ips = Collections.list(NetworkInterface.getNetworkInterfaces()).stream().filter(ni -> {
				try {
					return !ni.isLoopback() && ni.isUp();
				} catch (SocketException e) {
					return false;
				}
			}).flatMap(ni -> Collections.list(ni.getInetAddresses()).stream())
					.filter(addr -> addr instanceof Inet4Address).map(InetAddress::getHostAddress)
					.peek(ip -> System.out.println("  " + ip + ":" + port)).collect(Collectors.toSet());

			if (ips.isEmpty()) {
				System.out.println("No network interfaces found!");
			}
		} catch (SocketException e) {
			System.out.println("Could not retrieve network information");
		}
		return ips;
	}

	private static boolean validateConnection(int myPort, String peerIp, int peerPort, Set<String> localIps) {
		if ((localIps.contains(peerIp) || peerIp.equalsIgnoreCase("localhost")) && peerPort == myPort) {
			System.out.println("Error: Cannot connect to yourself!");
			return false;
		}
		return true;
	}

	private static String getConnectionInput(Scanner scanner) {
		while (true) {
			System.out.print("Enter peer as [IP:PORT] or leave blank for manual entry: ");
			String input = scanner.nextLine().trim();

			if (input.isEmpty())
				return "";

			if (!input.contains(":")) {
				System.out.println("Invalid format. Use IP:PORT (e.g., 192.168.1.10:12345)");
				continue;
			}

			try {
				String[] parts = input.split(":");
				if (parts.length != 2)
					throw new IllegalArgumentException();
				Integer.parseInt(parts[1]);
				return input;
			} catch (Exception e) {
				System.out.println("Invalid format. Use IP:PORT (e.g., 192.168.1.10:12345)");
			}
		}
	}

	private static String getPeerIp(Scanner scanner) {
		System.out.print("Enter peer's IP address: ");
		return scanner.nextLine().trim();
	}

	private static void connectToPeer(String name, int myPort, String peerIp, int peerPort) {
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(peerIp, peerPort), TIMEOUT);
			System.out.println("Connected to peer. Starting communication...");
			communicate(socket, name);
		} catch (IOException e) {
			try (ServerSocket serverSocket = new ServerSocket(myPort)) {
				System.out.println("\nWaiting for incoming connection on port " + myPort + "...");
				Socket socket = serverSocket.accept();
				System.out.println("Peer connected. Starting communication...");
				communicate(socket, name);
			} catch (IOException ex) {
				System.out.println("Connection failed: " + ex.getMessage());
			}
		}
	}

	private static void communicate(Socket socket, String name) {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			out.println(name);
			String otherName = in.readLine();

			System.out.println("\n" + SEPARATOR);
			System.out.println("💬 Connected with: " + otherName);
			System.out.println(SEPARATOR + "\n");

			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(() -> {
				try {
					String message;
					while ((message = in.readLine()) != null) {
						if (message.equalsIgnoreCase("/exit"))
							break;
						printFormattedMessage(otherName, message);
						System.out.print("➤ ");
					}
				} catch (IOException e) {
					System.out.println("Connection closed");
				} finally {
					executor.shutdown();
					closeSocket(socket);
				}
				return null;
			});

			Scanner scanner = new Scanner(System.in);
			String input;
			while (true) {
				System.out.print("> ");
				input = scanner.nextLine();
				out.println(input);
				if (input.equalsIgnoreCase("/exit"))
					break;
				printFormattedMessage("Me", input);
			}
			scanner.close();
			executor.shutdownNow();

		} catch (IOException e) {
			System.out.println("Communication error: " + e.getMessage());
		} finally {
			closeSocket(socket);
		}
	}

	private static void printFormattedMessage(String sender, String message) {
		String time = LocalTime.now().format(TIME_FORMAT);
		String header = String.format("[%s] %s:", time, sender);
		String formatted = wrapText(header, message);
		System.out.println("\r" + formatted);
	}

	private static String wrapText(String header, String message) {
		StringBuilder result = new StringBuilder();
		String indent = " ".repeat(header.length() + 2);
		List<String> words = new ArrayList<>(Arrays.asList(message.split(" ")));

		result.append(header).append("\n");
		StringBuilder line = new StringBuilder(indent);

		for (Iterator<String> it = words.iterator(); it.hasNext();) {
			String word = it.next();
			if (line.length() + word.length() > MAX_WIDTH) {
				result.append(line).append("\n");
				line = new StringBuilder(indent);
			}
			line.append(word);
			if (it.hasNext())
				line.append(" ");
		}

		result.append(line);
		return result.toString();
	}

	private static void closeSocket(Socket socket) {
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			System.out.println("Error closing socket: " + e.getMessage());
		}
	}
}
