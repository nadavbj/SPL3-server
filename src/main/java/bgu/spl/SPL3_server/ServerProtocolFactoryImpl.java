package bgu.spl.SPL3_server;

public class ServerProtocolFactoryImpl implements ServerProtocolFactory  {

	public ServerProtocol create() {
		
		return new ServerProtocolImpl();
	}

	

}
