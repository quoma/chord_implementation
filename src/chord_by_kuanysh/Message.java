package chord_by_kuanysh;

import java.io.*;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int FIND_SUCCESSOR_REQUEST = 1;
	public static final int FIND_SUCCESSOR_RESPONSE= 2;
	public int type;
	public int data;
	public Node sender;
	
	public Message(int command, int data, Node sender) {
		this.type = command;
		this.data = data;
		
		this.sender = sender;
	}
	
	public static Message fromString(String serialized) {
		byte[] bytes = MyBase64.getDecoder().decode(serialized);
		ByteArrayInputStream bytesStream = new ByteArrayInputStream(bytes);
		Message result = null;
		try {
			ObjectInputStream objectStream = new ObjectInputStream(bytesStream);
			result = (Message) objectStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
		
	public String toString() {
		String result = null;
		ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();		
		try {
			ObjectOutputStream objectStream = new ObjectOutputStream(bytesStream); 
			objectStream.writeObject(this);
			result = MyBase64.getEncoder().encodeToString(bytesStream.toByteArray());
			objectStream.flush();
			objectStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
