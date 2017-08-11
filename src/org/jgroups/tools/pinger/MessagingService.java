package org.jgroups.tools.pinger;

import java.util.List;

import org.cometd.annotation.Configure;
import org.cometd.annotation.Service;
import org.cometd.annotation.Session;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.LocalSession;

@Service()
public class MessagingService implements BroadCaster.Listener {

    @Session
    private LocalSession sender;
    
    private static final MainLogger _logger = new MainLogger(MessagingService.class.getName());
    
    private BroadCaster bc;
    private MessageListener ml;
  
    public MessagingService() {
    	_logger.startUpLogger();
    	_logger.info("Initializing jGroupsPinger - FrontEnd");
    	try {
			bc = (BroadCaster) Class.forName(Settings.getBroadCasterClassName()).newInstance();
			this.ml = new MessageListener(bc);
		} catch (InstantiationException e) {
			_logger.error("error initializing jGroupsPingerEngine", e);
		} catch (IllegalAccessException e) {
			_logger.error("error initializing jGroupsPingerEngine", e);
		} catch (ClassNotFoundException e) {
			_logger.error("error initializing jGroupsPingerEngine", e);
		}
	}
    
    @Configure("/*")
    protected void configureMessaging(ConfigurableServerChannel channel) {
    	this.ml = new MessageListener(bc);
    	channel.addListener(ml);
    }
    
    public void onUpdates(List<BroadCaster.Update> updates) {
        for (BroadCaster.Update update : updates) {        
        	broadcastMessage(update);
        }
    }
    
    private void broadcastMessage(BroadCaster.Update update) {
    	// Here I can handle with the message and do Whatever I Want.
    }
    
    public synchronized BroadCaster getBC() {
		return bc;
	}

}