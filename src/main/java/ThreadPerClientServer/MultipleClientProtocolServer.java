package ThreadPerClientServer;


	
	import org.json.simple.parser.ParseException;

	import java.io.*;
	import java.net.*;


	
	class MultipleClientProtocolServer implements Runnable {
		private ServerSocket serverSocket;
		private int listenPort;
		private ServerProtocolFactoryImpl factory;
		
		
		public MultipleClientProtocolServer(int port, ServerProtocolFactoryImpl p)
		{
			serverSocket = null;
			listenPort = port;
			factory = p;
		}
		
		public void run()
		{
			try {
				serverSocket = new ServerSocket(listenPort);
				System.out.println("Listening...");
			}
			catch (IOException e) {
				System.out.println("Cannot listen on port " + listenPort);
			}
			
			while (true)
			{
				try {
					ConnectionHandler newConnection = new ConnectionHandler(serverSocket.accept(), factory.create());
	            new Thread(newConnection).start();
				}
				catch (IOException e)
				{
					System.out.println("Failed to accept on port " + listenPort);
				}
			}
		}
		

		// Closes the connection
		public void close() throws IOException
		{
			serverSocket.close();
		}
		
		
		
		
		
		
		public static void main(String[] args) throws IOException
		{
			// Get port
			int port = Integer.decode(args[0]).intValue();
			//Load questions from json
			try {
				ServerData.instance.loadQuestionsFromJson(args[1]);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			MultipleClientProtocolServer server = new MultipleClientProtocolServer(port, new ServerProtocolFactoryImpl());
			Thread serverThread = new Thread(server);
	      serverThread.start();
			try {
				serverThread.join();
			}
			catch (InterruptedException e)
			{
				System.out.println("Server stopped");
			}



		}
	}



