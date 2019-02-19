package controllers;

import interfaces.IreceiverGateway;
import messaging.requestreply.RequestReply;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class receiverGateway implements IreceiverGateway {
    Connection connection; // to connect to the JMS
    Session session; // session for creating consumers

    Destination receiveDestination; //reference to a queue/topic destination
    MessageConsumer consumer = null; // for receiving messages

    public Object[] setListener(MessageListener ml){
//        Object array[] = new Object[2];
//        try {
//            Properties props = new Properties();
//            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
//                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
//            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
//
//            // connect to the Destination called “myFirstChannel”
//            // queue or topic: “queue.myFirstDestination” or
//            // “topic.myFirstDestination”
//            props.put(("queue.toBankFrameQueue"), " toBankFrameQueue");
//
//            Context jndiContext = new InitialContext(props);
//            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
//                    .lookup("ConnectionFactory");
//            connection = connectionFactory.createConnection();
//            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//
//            // connect to the receiver destination
//            receiveDestination = (Destination) jndiContext.lookup("toBankFrameQueue");
//            consumer = session.createConsumer(receiveDestination);
//
//            connection.start(); // this is needed to start receiving messages
//        } catch (NamingException | JMSException e) {
//            e.printStackTrace();
//        }
//        try {
//            consumer.setMessageListener(msg -> {
//                try {
//                    BankInterestRequest bankinterestrequest = (BankInterestRequest)((ObjectMessage)msg).getObject();
//                    String correlation = msg.getJMSCorrelationID();
//                    array[0] = new RequestReply<>(bankinterestrequest, correlation);
//                    array[1] = new RequestReply<>(bankinterestrequest,new BankInterestReply());
//                    waitingForReply.add(new RequestReply<>(bankinterestrequest, correlation));
//                    listModel.addElement(new RequestReply<>(bankinterestrequest,new BankInterestReply()));
//
//                } catch (JMSException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("received message: " + (msg));
//            });
//
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
//
        throw new UnsupportedOperationException();
    }
}
