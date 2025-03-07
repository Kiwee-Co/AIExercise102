package exercise.coding.kiwee.ai.turnbased;

//GameMessage.java
public class GameMessage {
	private String type;
	private char player;
	private int position;
	private String message;

	public GameMessage() {
	} // Required for Jackson

	public GameMessage(String type, char player, int position, String message) {
		this.type = type;
		this.player = player;
		this.position = position;
		this.message = message;
	}

	// Getters and setters
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public char getPlayer() {
		return player;
	}

	public void setPlayer(char player) {
		this.player = player;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}