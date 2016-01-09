package ThreadPerClientServer;

public class ServerProtocolFactoryImpl implements ServerProtocolFactory  {

	public ServerProtocol create() {
		
		return new ServerProtocolImpl();
	}

	

}
