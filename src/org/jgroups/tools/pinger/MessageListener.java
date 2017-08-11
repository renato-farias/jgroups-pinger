package org.jgroups.tools.pinger;

import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerMessage.Mutable;
import org.cometd.bayeux.server.ServerSession;

public class MessageListener implements ServerChannel.MessageListener {
	
	private BroadCaster bc;
	
	static final MainLogger _logger = new MainLogger(MessageListener.class.getName());
	
	public MessageListener(BroadCaster bc) {
		this.bc = bc;
	}
	
	private void brodacast(ServerSession session, ServerMessage message) {
		String formatedMsg = null;
		try {
			formatedMsg = MessageTools.formatMessageFromFE(message, session);
		} catch (Exception e) {
			_logger.error("error sending message", e);
		}
		
		if (formatedMsg != null) {
			_logger.info("Sending message to broadcast: " + formatedMsg + " from: " + session.getId());
			bc.sendMessage(formatedMsg);
		}
	}

	public boolean onMessage(ServerSession session, ServerChannel channel, Mutable message) {
		if ( message.getId() == null ) {
			return true;
		}
		if ( message.getId().isEmpty() == false || message.get("id") != null ) {
			_logger.debug("Message sent from client: " + message.getJSON());
			brodacast(session, message);
			return false;
		}
		return true;
		
	}
}