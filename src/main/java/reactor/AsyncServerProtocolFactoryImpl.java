package reactor;

import bgu.spl.SPL3_server.ServerProtocol;
import bgu.spl.SPL3_server.ServerProtocolFactory;

public class AsyncServerProtocolFactoryImpl implements ServerProtocolFactory {

	public ServerProtocol create() {
		
		return new AsyncServerProtocolImpl();
	}

	

}
