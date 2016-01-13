package reactor;

import ThreadPerClientServer.*;
import bgu.spl.SPL3_server.AsyncServerProtocol;


public class AsyncServerProtocolImpl extends ServerProtocolImpl implements AsyncServerProtocol<String> {

    private boolean shouldClose=false;
    private boolean isTerminated=false;


    @Override
    public void processMessage(String msg,ProtocolCallback callback){
        if(msg.equals(Quit)) {
            shouldClose = true;
        }
        super.processMessage(msg,callback);
    }

    @Override
    public boolean isEnd(String msg) {
        return msg.equals(Quit)||shouldClose||isTerminated;
    }

    @Override
    public boolean shouldClose() {
        return shouldClose;
    }

    @Override
    public void connectionTerminated() {
        isTerminated=true;
    }

}
