package ThreadPerClientServer;

import bgu.spl.SPL3_server.ServerProtocol;
import bgu.spl.SPL3_server.ServerProtocolFactory;

/**
 * Created by nadav on 09/01/16.
 */
public class ServerProtocolFactoryImpl implements ServerProtocolFactory {

    @Override
    public ServerProtocol create() {
        return new ServerProtocolImpl();
    }
}
