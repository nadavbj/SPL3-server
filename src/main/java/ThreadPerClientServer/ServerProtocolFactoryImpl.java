package ThreadPerClientServer;

import bgu.spl.SPL3_server.ServerProtocol;
import bgu.spl.SPL3_server.ServerProtocolFactory;


public class ServerProtocolFactoryImpl implements ServerProtocolFactory {

    @Override
    public ServerProtocol create() {
        return new ServerProtocolImpl();
    }
}
