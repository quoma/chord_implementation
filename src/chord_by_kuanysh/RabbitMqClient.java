package chord_by_kuanysh;

import java.io.IOException;

import com.rabbitmq.client.*;

public class RabbitMqClient {
		
	public static QueueingConsumerThread consumerThread;

			
	public static void publish(String message, String host, String queue) {
		try {
			ConnectionFactory factory = new ConnectionFactory();
		    factory.setHost(host);
		    Connection connection = factory.newConnection();
		    Channel channel = connection.createChannel();
	
		    channel.queueDeclare(queue, false, false, false, null);
		    channel.basicPublish("", queue, null, message.getBytes());
		    
		    channel.close();
		    connection.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static String nextMessage = "";
	
	public static String nextMessage() {
        synchronized(consumerThread){
            try{
                consumerThread.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            } 
        }
        return nextMessage;
	}
		
//	public static String consume(String host, String queue) {
//		String res = "";
//		try {
//			res = RabbitMqClient.get();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return res;
//		lastMessage = "";
//		synchronized (lastMessage) {
//			try {
////				isWaiting = true;
//				while (lastMessage.equals("")) {
//					Thread.sleep(1000);
//					System.out.print(".");
//				}
//				System.out.println("woke up");
//				
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
//		}
//		String result = lastMessage;
//		lastMessage = "";
//		return lastMessage;

//		String result = null;
//		try {
//			ConnectionFactory factory = new ConnectionFactory();
//		    factory.setHost(host);
//		    Connection connection = factory.newConnection();
//		    Channel channel = connection.createChannel();
//	
//		    channel.queueDeclare(queue, false, false, false, null);	    
//		    QueueingConsumer consumer = new QueueingConsumer(channel);
//		    channel.basicConsume(queue, true, consumer);
//		    
//	    	QueueingConsumer.Delivery delivery = consumer.nextDelivery();
//			result = new String(delivery.getBody());
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		return result;
//	}
	
	public static void startConsuming(String host, String queue, Notifiable caller, Node current) {
		try {
			ConnectionFactory factory = new ConnectionFactory();
		    factory.setHost(host);
		    Connection connection = factory.newConnection();
		    Channel channel = connection.createChannel();
	
		    channel.queueDeclare(queue, false, false, false, null);	    
		    QueueingConsumer consumer = new QueueingConsumer(channel);
		    channel.basicConsume(queue, true, consumer);
		    
		    consumerThread = new QueueingConsumerThread(consumer, caller, current);
		    Thread thread = new Thread(consumerThread);
		    thread.start();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
}

class QueueingConsumerThread implements Runnable{
	private Notifiable messageHandler;
	private QueueingConsumer consumer;
	private Node currentNode;
	
	public QueueingConsumerThread(QueueingConsumer consumer, Notifiable handler, Node current) {
		this.consumer = consumer;
		this.messageHandler = handler;
		this.currentNode = current;
	}
	
	public void run() {
    	QueueingConsumer.Delivery delivery;
	    while (true) {
			try {
				
				delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
//				Main.pln("Received some message");
				
//				messageHandler.handleMessage(message);
		        synchronized (this) {
		        	RabbitMqClient.nextMessage = message;
//		        	while (currentNode.requests.size() != 0);
		        	currentNode.requests.add(Message.fromString(message));
		            notifyAll();
//		            Main.pln("Updated next message");
		        }
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
	    }
	}
}

interface Notifiable {
	public void handleMessage(Message m);
}
