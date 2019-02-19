package loanbroker;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import controllers.senderGateway;
import interfaces.IsenderGateway;
import messaging.requestreply.RequestReply;
import model.bank.*;
import model.loan.LoanReply;
import model.loan.LoanRequest;
import sun.management.snmp.jvminstr.JvmClassLoadingImpl;


public class LoanBrokerFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<JListLine>();
	private JList<JListLine> list;
	private List<RequestReply<LoanRequest, String>> waitingForReply;
	private IsenderGateway sendergateway;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");
					LoanBrokerFrame frame = new LoanBrokerFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoanBrokerFrame() {

		receiveMessages_request();
		receiveMessage_reply();
		sendergateway = new senderGateway();
		setTitle("Loan Broker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
		gbl_contentPane.rowHeights = new int[]{233, 23, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 7;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		waitingForReply = new ArrayList<>();
		list = new JList<JListLine>(listModel);
		scrollPane.setViewportView(list);		
	}

	private void receiveMessage_reply(){
		Connection connection; // to connect to the JMS
		Session session; // session for creating consumers

		Destination receiveDestination; //reference to a queue/topic destination
		MessageConsumer consumer = null; // for receiving messages

		try {
			Properties props = new Properties();
			props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
					"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

			// connect to the Destination called “myFirstChannel”
			// queue or topic: “queue.myFirstDestination” or
			// “topic.myFirstDestination”
			props.put(("queue.ReplyToBroker"), " ReplyToBroker");

			Context jndiContext = new InitialContext(props);
			ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
					.lookup("ConnectionFactory");
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// connect to the receiver destination
			receiveDestination = (Destination) jndiContext.lookup("ReplyToBroker");
			consumer = session.createConsumer(receiveDestination);

			connection.start(); // this is needed to start receiving messages
		} catch (NamingException | JMSException e) {
			e.printStackTrace();
		}
		try {
			consumer.setMessageListener(msg -> {
				try {
					String correlation = msg.getJMSCorrelationID();
					System.out.println("Received message");
					for (int i = 0; i < waitingForReply.size(); i++) {
						System.out.println("Checking message: " + i);
						System.out.println("Is this ok? " + waitingForReply.get(i).getReply() + " | " + correlation);
						if(waitingForReply.get(i).getReply().equals(correlation)){
							System.out.println("++ Adding message: " + i);
							BankInterestReply bankinterestreply = (BankInterestReply)((ObjectMessage)msg).getObject();
							add(waitingForReply.get(i).getRequest(),bankinterestreply);


							LoanReply loanreply = new LoanReply(bankinterestreply.getInterest(), bankinterestreply.getQuoteId());
							System.out.println("Sending:" + waitingForReply.get(i).getRequest() + "AND" + loanreply + "AND" + correlation);
							sendergateway.messageSomeOne(new RequestReply<>(waitingForReply.get(i).getRequest(), loanreply), correlation, "ReplyToClient");

							break;
						}
					}
				} catch (JMSException e) {
					e.printStackTrace();
				}
				System.out.println("received message: " + (msg));
			});

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	 private void receiveMessages_request(){
		 Connection connection; // to connect to the JMS
		 Session session; // session for creating consumers

		 Destination receiveDestination; //reference to a queue/topic destination
		 MessageConsumer consumer = null; // for receiving messages

		 try {
			 Properties props = new Properties();
			 props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
					 "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			 props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

			 // connect to the Destination called “myFirstChannel”
			 // queue or topic: “queue.myFirstDestination” or
			 // “topic.myFirstDestination”
			 props.put(("queue.myFirstDestination"), " myFirstDestination");

			 Context jndiContext = new InitialContext(props);
			 ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
					 .lookup("ConnectionFactory");
			 connection = connectionFactory.createConnection();
			 session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			 // connect to the receiver destination
			 receiveDestination = (Destination) jndiContext.lookup("myFirstDestination");
			 consumer = session.createConsumer(receiveDestination);

			 connection.start(); // this is needed to start receiving messages
			} catch (NamingException | JMSException e) {
			 e.printStackTrace();
		 }
		 try {
			 consumer.setMessageListener(msg -> {
				 try {
				 	LoanRequest loanrequest = (LoanRequest)((ObjectMessage)msg).getObject();
					BankInterestRequest bankInterestRequest = new BankInterestRequest(loanrequest.getAmount(),loanrequest.getTime());
				 	add(loanrequest);
				 	String messageid = msg.getJMSMessageID();
					 waitingForReply.add(new RequestReply<>(loanrequest,messageid));
					 sendergateway.messageSomeOne(bankInterestRequest, messageid, "toBankFrameQueue");
				 } catch (JMSException e) {
					 e.printStackTrace();
				 }
				 System.out.println("received message: " + (msg));
			 });

		 } catch (JMSException e) {
			 e.printStackTrace();
		 }

	 }
	 private JListLine getRequestReply(BankInterestRequest request){
		 for (int i = 0; i < listModel.getSize(); i++){
			 JListLine rr =listModel.get(i);
			 if (rr.getBankRequest() == request){
				 return rr;
			 }
		 }
		 return null;
	 }
	 private JListLine getRequestReply(LoanRequest request){    
	     
	     for (int i = 0; i < listModel.getSize(); i++){
	    	 JListLine rr =listModel.get(i);
	    	 if (rr.getLoanRequest() == request){
	    		 return rr;
	    	 }
	     }
	     
	     return null;
	   }
	
	public void add(LoanRequest loanRequest){		
		listModel.addElement(new JListLine(loanRequest));		
	}
	

	public void add(LoanRequest loanRequest,BankInterestRequest bankRequest){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankRequest != null){
			rr.setBankRequest(bankRequest);
            list.repaint();
		}		
	}
	
	public void add(LoanRequest loanRequest, BankInterestReply bankReply){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankReply != null){
			rr.setBankReply(bankReply);
            list.repaint();
		}		
	}


}
