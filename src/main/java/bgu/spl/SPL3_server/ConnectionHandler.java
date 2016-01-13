package bgu.spl.SPL3_server;

import ThreadPerClientServer.ProtocolCallback;

import java.util.Map;


public interface ConnectionHandler extends Runnable {
    void close();
}
