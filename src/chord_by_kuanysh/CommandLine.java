package chord_by_kuanysh;

public class CommandLine implements Runnable{
	public void run() {
		while (true) {
			String line = Chord.in.nextLine();
			Chord.send(line);
		}
	}
	
}
