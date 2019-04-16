package application;

import controllers.receiverGateway;
import controllers.senderGateway;
import interfaces.IsenderGateway;
import model.brandstoffen.Brandstof;
import model.brandstoffen.BrandstofUpdate;
import model.brandstoffen.Pomporder;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

public class Pomp2 implements Observer {
    private int Pompnr;
    private String tankstation = "EersteStation";
    private JRadioButton rbBenzine;
    private JRadioButton rbDiesel;
    private JRadioButton rbGas;
    private JButton sendButton;
    private JLabel labBenzine;
    private JLabel labDiesel;
    private JLabel labGas;
    private JPanel panelPomp;
    private JSpinner spinLiter;
    private IsenderGateway sendergateway = new senderGateway();
    private receiverGateway receivergateway;
    private BrandstofUpdate brandstoffen;

    public static void main(String[] args) {
        System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");
        JFrame frame = new JFrame("Pomp");
        frame.setContentPane(new Pomp2().panelPomp);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
    public Pomp2(){
        receivergateway = new receiverGateway("Pomp2");
        receivergateway.addObserver(this::update);
        Pompnr = 2;
        brandstoffen = new BrandstofUpdate(new Brandstof("Benzine", 160), new Brandstof("Diesel", 141), new Brandstof("Gas", 91));
        refreshBranstoffen(brandstoffen);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendButton.setEnabled(false);
                sendPompAction();
            }
        });
    }
    private void sendPompAction(){
        Brandstof sending = null;
        if(rbBenzine.isSelected()) {
            sending = brandstoffen.getBenzine();
        }
        else if(rbDiesel.isSelected()){
            sending = brandstoffen.getDiesel();
        }
        else if(rbGas.isSelected()){
            sending = brandstoffen.getGas();
        }
        Pomporder order = new Pomporder(Pompnr, sending, (Integer) spinLiter.getValue());
        sendergateway.SendPompOrder(order, tankstation);
        System.out.println("Send the order");
    }
    private void refreshBranstoffen(BrandstofUpdate update){
        brandstoffen = update;
        labBenzine.setText(String.valueOf(update.getBenzine().prijs));
        labDiesel.setText(String.valueOf(update.getDiesel().prijs));
        labGas.setText(String.valueOf(update.getGas().prijs));

    }
    @Override
    public void update(Observable o, Object msg) {
        try {
            if (((ObjectMessage) msg).getObject() instanceof BrandstofUpdate) {
                BrandstofUpdate update = null;

                    update = (BrandstofUpdate) ((ObjectMessage) msg).getObject();
                refreshBranstoffen(update);
            }
            else if(((ObjectMessage) msg).getObject() instanceof Integer){
                if((Integer)((ObjectMessage)msg).getObject() == Pompnr){
                    sendButton.setEnabled(true);
                }
            }


        }
        catch (JMSException e) {
        e.printStackTrace();
    }
    }
}
