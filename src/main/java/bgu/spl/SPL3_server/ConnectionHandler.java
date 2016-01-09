package bgu.spl.SPL3_server;

import ThreadPerClientServer.ProtocolCallback;

import java.util.Map;

/**
 * Created by nadav on 09/01/16.
 */
public interface ConnectionHandler extends Runnable {
    void sendMessage(String message, String responseCommannd, ProtocolCallback<String> callback);
    Map<String, ProtocolCallback> getResponsesCallBacks();
    void close();
}
