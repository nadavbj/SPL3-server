package ThreadPerClientServer;


	
	import java.io.*;
	import java.net.*;

	

/*
	class EchoProtocol implements ServerProtocol {
			
		public EchoProtocol() { }
		
		public String processMessage(String msg)
		{
			return msg;
		}
		
		public boolean isEnd(String msg)
		{
			return msg.equals("bye");
		}
	}

	class EchoProtocolFactory implements ServerProtocolFactory {
		public ServerProtocol create(){
			return new EchoProtocol();
		}
	}

	*/
	
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



