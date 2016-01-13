package reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;


import bgu.spl.SPL3_server.AsyncServerProtocol;
import tokenizer.*;

/**
 * This class supplies some data to the protocol, which then processes the data,
 * possibly returning a reply. This class is implemented as an executor task.
 *
 */
public class ProtocolTask implements Runnable {

	private final AsyncServerProtocol _protocol;
	private final MessageTokenizer _tokenizer;
	private final ConnectionHandlerReactor _handler;

	public ProtocolTask(final AsyncServerProtocol protocol, final MessageTokenizer tokenizer, final ConnectionHandlerReactor h) {
		this._protocol = protocol;
		this._tokenizer = tokenizer;
		this._handler = h;
	}

	// we synchronize on ourselves, in case we are executed by several threads
	// from the thread pool.
	public synchronized void run() {
		// go over all complete messages and process them.
		while (_tokenizer.hasMessage()) {
			String msg = ((StringMessage)_tokenizer.nextMessage()).getMessage();
			{
				msg=msg.trim();
				if (_protocol.getName()!=null){
					System.out.println("Received \"" + msg + "\" from "+_protocol.getName());
				}
				else System.out.println("Received \"" + msg + "\" from client");
				String command;
				if(msg.contains(" "))
					command=msg.substring(0,msg.indexOf(" "));
				else
					command=msg;

				_protocol.processMessage(msg,(response)->{
					if (response != null) {
						try {
							ByteBuffer bytes = _tokenizer.getBytesForMessage(new StringMessage((String)response));
							this._handler.addOutData(bytes);
						} catch (CharacterCodingException e) { e.printStackTrace(); }
					}});

				if (_protocol.isEnd(msg))
				{
					break;
				}
			}


		}
	}


	public void addBytes(ByteBuffer b) {
		_tokenizer.addBytes(b);
	}
}
