package exercise.coding.kiwee.ai.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final int MAX_WIDTH = 60;

	public static void main(String[] args) {
		try (Scanner scanner = new Scanner(System.in);) {
			System.out.print("Enter your name: ");
			String name = scanner.nextLine().trim();

			if (name.isEmpty()) {
				System.out.println("Invalid name!");
				return;
			}

			String serverIp = getServerIp(scanner);
			int serverPort = getValidPort(scanner, "Enter server port: ");
			connectToServer(name, serverIp, serverPort);
		}

	}

	private static String getServerIp(Scanner scanner) {
		System.out.print("Enter server IP: ");
		return scanner.nextLine().trim();
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

	private static void connectToServer(String name, String serverIp, int serverPort) {
		try (Socket socket = new Socket(serverIp, serverPort);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

			// Step 1: Send name first
			out.println(name);

			// Step 2: Start message loop
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(() -> receiveMessages(in));

			Scanner inputScanner = new Scanner(System.in);
			while (true) {
				System.out.print("➤ ");
				String message = inputScanner.nextLine();
				if (message.trim().isEmpty())
					continue;
				out.println(message);
				if (message.equalsIgnoreCase("/exit"))
					break;
			}
			inputScanner.close();
			executor.shutdownNow();
		} catch (IOException e) {
			System.out.println("Connection error: " + e.getMessage());
		}
	}

	private static void receiveMessages(BufferedReader in) {
		try {
			String message;
			while ((message = in.readLine()) != null) {
				System.out.println("\r" + wrapText(message) + "\n➤ ");
			}
		} catch (IOException e) {
			System.out.println("\nDisconnected from server");
		}
	}

	private static String wrapText(String message) {
		String timeHeader = "[" + LocalTime.now().format(TIME_FORMAT) + "]";
		String indent = " ".repeat(timeHeader.length() + 2);
		StringBuilder result = new StringBuilder(timeHeader);

		int count = 0;
		for (String word : message.split(" ")) {
			if (count + word.length() > MAX_WIDTH) {
				result.append("\n").append(indent);
				count = 0;
			}
			result.append(" ").append(word);
			count += word.length() + 1;
		}
		return result.toString();
	}
}
