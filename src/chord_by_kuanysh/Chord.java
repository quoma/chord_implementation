package chord_by_kuanysh;

import java.security.*;
import java.util.Scanner;
import java.net.*;

import com.rabbitmq.examples.perf.Broker;


public class Chord {
	static Scanner in = new Scanner(System.in);
	
	static String hostAddress;
	static int hostIdentifier;
    
	static String scsAddress;
	static int scsIdentifier;
	
	public static void main2(String[] args) {
    	echo("Starting Chord...");
    	promptHostAddress();
    	promptHostIdentifier();
    	String reference = promptReference();

		if (reference == null) {
			// new network
			scsAddress = hostAddress;
			scsIdentifier = hostIdentifier;
		} else {
			// temp
			scsAddress = reference.split("@")[1];
			scsIdentifier = hash(reference);
		}
				
		// broker for listening incoming messages
//		MessageBroker broker = new MessageBroker(hostAddress, hostIdentifier, true);
//		Thread thread = new Thread(broker);
//		thread.start();
		
		CommandLine cli = new CommandLine();
		Thread cli_thread = new Thread(cli);
		cli_thread.start();
		echo("You can type commands now...");
	}
    
	public static int findSuccessor(int n) {
		if (hostIdentifier == scsIdentifier) return scsIdentifier;
    	boolean is_tail = hostIdentifier > scsIdentifier;
    	boolean in_range = is_tail ? (n > hostIdentifier || n <= scsIdentifier) 
    			: (n > hostIdentifier && n <= scsIdentifier);
    	if (in_range) {
    		return scsIdentifier;
    	}
    	return findSuccessorRemote(n); 
	}
	
	public static int findSuccessorRemote(int n) {
//		MessageBroker broker = new MessageBroker(scsAddress, scsIdentifier, false);
//		broker.publish("lookup " + n);
//		int result = Integer.parseInt(broker.consume());
//		broker.close();
		return 0;
	}
	
	public static String promptReference() {
    	echo("Please, type any known node's address (e.g. user@127.0.0.1), "
    			+ "or press <Enter> to create a new network:");
    	String line = in.nextLine(); 
		return (line.length() > 0) ? line : null;
	}
    
    public static void promptHostAddress() {
    	hostAddress = null;
    	try {
			hostAddress = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {}
    	if (hostAddress == null) {
    		echo("Unable to detect your IP-address. Please, enter it manually:");
    		hostAddress = in.nextLine();
    	} else {
    		echo("Your detected IP-address is " + hostAddress);
    		echo("Press <Enter> or type a different address to use:");
    		String line = in.nextLine();
    		if (line.length() > 0) hostAddress = line;
    	}
    }
    
    public static void promptHostIdentifier() {
    	echo("Please, enter username (this allows running multiple nodes on one machine):");
    	String userName = "";
    	while (userName.length() == 0) {
    		userName = in.nextLine();
    	}
    	hostIdentifier = hash(userName + "@" + hostAddress);
    	echo("Your Chord identifier is N" + hostIdentifier);
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
    
    static void receivedMessage(String message) {
    	Chord.echo("received:" + message);
//		String[] line = message.split(" ");
//		if (line[0].equals("lookup")) {
//			int result = Chord.findSuccessor(Integer.parseInt(line[1]));
//			Chord.echo("I found: " + result);
//		}
    }
    static void send(String message) {
//		MessageBroker broker = new MessageBroker(scsAddress, scsIdentifier, false);
//		broker.publish(message);
//		broker.close();
    }
    
    public static void echo(String message){
    	System.out.println(message);
    }

}
