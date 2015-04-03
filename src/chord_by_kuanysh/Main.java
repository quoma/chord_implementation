package chord_by_kuanysh;

import java.awt.Event;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Callable;

public class Main {
	static Scanner in = new Scanner(System.in);

	public static void p(String string) {
		System.out.print(string);
	}
	
	public static void pln(String string) {
		System.out.println(string);
	}

	public static void main(String[] args) {
		// get ips
		Enumeration<NetworkInterface> nets = null;
		try {
			nets = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		Vector<String> ips = new Vector<String>();
        for (NetworkInterface netint : Collections.list(nets)) {
        	Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            	ips.add(inetAddress.toString().substring(1));
            }
        }

        // select ip
        pln("Select your IP:");
        for (int i = 0; i < ips.size(); ++i) 
        	pln("[" + (i + 1) + "] " + ips.get(i));
        String currentHost = ips.get(Integer.parseInt(in.nextLine()) - 1);
		
		p("name of the node: ");
		String name = in.nextLine();
		Node current = new Node(currentHost, name);
		pln("Your id: " + current.id);
		
		p("known chord node (name@ip_address):");
		String scsFullName = in.nextLine();
		String[] scsData = scsFullName.split("@");
		Node successor = new Node(scsData[1], scsData[0]);
		current.successor = successor;
		
		current.start();
		
		while (true) {
			String line = in.nextLine();
			String[] cmd = line.split(" ");
			if (cmd.length == 2) {
				if (cmd[0].equals("find")) {
					int k = Integer.parseInt(cmd[1]);
					int result = current.findSuccessor(k);
					pln("successor is: " + result);					
				}
			}
		}
		
//		Message m = new Message(123, 234, new Node("bla bla", "nodifier"));
//		String s = m.toString();
//		pln(s);
//		Message k = Message.fromString(s);
//		pln(k.sender.name);
	}
}
