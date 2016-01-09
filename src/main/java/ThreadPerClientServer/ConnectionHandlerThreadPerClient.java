package ThreadPerClientServer;

import bgu.spl.SPL3_server.ServerProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ConnectionHandlerThreadPerClient  implements bgu.spl.SPL3_server.ConnectionHandler {

	private BufferedReader in;
	private PrintWriter out;
	private Socket clientSocket;
	private ServerProtocol protocol;
	private Map<String, ProtocolCallback> responsesCallBacks = new HashMap();

	public ConnectionHandlerThreadPerClient(Socket acceptedSocket, ServerProtocol p)
	{
		in = null;
		out = null;
		clientSocket = acceptedSocket;
		protocol = p;
		protocol.setConnection(this);
		System.out.println("Accepted connection from client!");
		System.out.println("The client is from: " + acceptedSocket.getInetAddress() + ":" + acceptedSocket.getPort());
	}

	public void run()
	{
		try {
			initialize();
		}
		catch (IOException e) {
			System.out.println("Error in initializing I/O");
		}

		try {
			process();
		}
		catch (IOException e) {
			System.out.println("Error in I/O");
		}

		System.out.println("Connection closed - bye bye...");
		close();
	}

	public void process() throws IOException
	{
		String msg;

		while ((msg = in.readLine()) != null )
		{
			msg=msg.trim();
			System.out.println("Received \"" + msg + "\" from client");
			String command;
			if(msg.contains(" "))
				command=msg.substring(0,msg.indexOf(" "));
			else
				command=msg;
			if(responsesCallBacks.containsKey(command)){
				responsesCallBacks.get(command).sendMessage(msg.substring(msg.indexOf(" ")));
				responsesCallBacks.remove(command);
			}
			else
			{
				protocol.processMessage(msg,(response)-> out.println(response));

				if (protocol.isEnd(msg))
				{
					break;
				}
			}


		}

	}

	/*
	Sends the message and activate the callback when the client response recived
	 */
	public void sendMessage(String message, String responseCommannd, ProtocolCallback<String> callback){
		out.println(message);
		if(responseCommannd!=null)
			responsesCallBacks.put(responseCommannd,callback);
	}

	@Override
	public Map<String, ProtocolCallback> getResponsesCallBacks() {
		return responsesCallBacks;
	}

	// Starts listening
	public void initialize() throws IOException
	{
		// Initialize I/O
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));
		out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-8"), true);
		System.out.println("I/O initialized");
	}

	// Closes the connection
	public void close()
	{
		try {
			if (in != null)
			{
				in.close();
			}
			if (out != null)
			{
				out.close();
			}

			clientSocket.close();
		}
		catch (IOException e)
		{
			System.out.println("Exception in closing I/O");
		}
	}

}
