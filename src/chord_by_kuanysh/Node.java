package chord_by_kuanysh;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;

public class Node implements Serializable, Notifiable, Runnable{
	private static final long serialVersionUID = 1L;
	public String host;
	public String name;
	public int id;
	public Node successor;
	
	LinkedList<Message> requests;
	
	public Node(String host, String name) {
		this.host = host;
		this.name = name;
		this.id = hash(name + "@" + host);
		requests = new LinkedList<Message>();
	}
	
	public String getRabbitQueue() {
		return "chord-node-" + this.id;
	}
	
	public void start() {
		RabbitMqClient.startConsuming(this.host, this.getRabbitQueue(), this, this);
		Thread t = new Thread(this);
		t.start();
	}
	
	public void handleMessage(Message m) {
		if (m.type == Message.FIND_SUCCESSOR_REQUEST) {
			Main.pln("was asked about " + m.data);
			int answer = this.findSuccessor(m.data);
			Message response = new Message(Message.FIND_SUCCESSOR_RESPONSE, answer, this);
			RabbitMqClient.publish(response.toString(), m.sender.host, m.sender.getRabbitQueue());
//			Main.pln("sent answer to " + m.sender.name + " " + m.sender.host + ", " + m.sender.getRabbitQueue());
		}
//		if (m.type == Message.FIND_SUCCESSOR_RESPONSE) {
//			RabbitMqClient.nextMessage = message;
//		}
	}

	
    public static int hash(String input) {
    	int result = -1;
    	try {
	    	MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
	    	byte[] digest = messageDigest.digest(input.getBytes());
	    	// will get 8 bits (the last byte)
	    	result = (int) (digest[digest.length - 1] + Math.abs(Byte.MIN_VALUE));
    	} catch (NoSuchAlgorithmException e) {}
    	return result;
    }
    
	public int findSuccessor(int key) {
		if (this.id == successor.id) return successor.id;
    	boolean is_tail = this.id > successor.id;
    	boolean in_range = is_tail ? (key > this.id || key <= successor.id) 
    			: (key > this.id && key <= successor.id);
    	if (in_range) {
    		return successor.id;
    	}
    	
    	// ask successor
		Message request = new Message(Message.FIND_SUCCESSOR_REQUEST, key, this);
		RabbitMqClient.publish(request.toString(), successor.host, successor.getRabbitQueue());
		Main.pln("don't know, asked successor");

		String response = RabbitMqClient.nextMessage();
		Message result = Message.fromString(response);
//		Main.pln("got the answer");
    	return result.data; 
	}

	public void run() {
		while (true) {
//			System.out.println("waiting for request");
			synchronized (RabbitMqClient.consumerThread) {
				try {
					RabbitMqClient.consumerThread.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				LinkedList<Message> cl = (LinkedList<Message>) requests.clone();
				requests.clear();
				ListIterator<Message> i = cl.listIterator();
				while (i.hasNext()) {
					Message request = i.next();
					handleMessage(request);
//					System.out.println("request:" + request.data);
				}
			}
		}
	}

}
