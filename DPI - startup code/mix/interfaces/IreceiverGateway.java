package interfaces;

import javax.jms.MessageListener;

public interface IreceiverGateway {
    Object[] setListener(MessageListener ml);
}
