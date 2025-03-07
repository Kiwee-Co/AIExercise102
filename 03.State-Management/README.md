# Tic-Tac-Toe Multiplayer Game

A JavaFX-based, networked Tic-Tac-Toe game where two players can compete over a local network. The game features a graphical user interface, randomized starting player, and real-time synchronization of moves and game outcomes.

## Features
- **Multiplayer**: Host or join a game over a network.
- **Randomized Start**: The host randomly decides whether the host ('X') or client ('O') starts.
- **Real-Time Updates**: Moves and game results (win/draw) are synced between players.
- **User-Friendly UI**: Enter your name, IP, and port to play; buttons disable appropriately during connection attempts.
- **Error Handling**: Graceful handling of connection failures (e.g., port in use) with retry logic.

## Prerequisites
- **Java 21**: The game uses JavaFX, which is included in JDK 21.
- **Maven**: For dependency management and building the project.
- **Jackson**: For JSON serialization/deserialization (included via Maven).

## Setup
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/tictactoe-multiplayer.git
   cd tictactoe-multiplayer
   ```
2. **Install Dependencies**: Ensure you have Maven installed, then run:
   ```bash
   mvn clean install
   ```
3. **Project Structure**:
  - `src/main/java/exercise/coding/kiwee/ai/turnbased/`
    - `TicTacToeGame.java`: Main game logic and UI.
    - `NetworkHandler.java`: Network communication handling.
    - `GameMessage.java`: Message structure for network communication.

## Usage
**Compile and Run**: Use Maven to run the application:
   ```bash
   mvn javafx:run
   ```
