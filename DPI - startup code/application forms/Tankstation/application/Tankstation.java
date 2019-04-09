package application;

import controllers.receiverGateway;
import controllers.senderGateway;
import interfaces.IsenderGateway;
import model.brandstoffen.*;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Tankstation implements Observer {
    private String personalID;
    private JPanel Tankstation;
    private JButton buttonPay;
    private JTextField tbNaam;
    private JSpinner spinItem;
    private JButton btnAdd;
    private JButton tankbeurtToevoegenAanOrderButton;
    private JLabel labbeurt;
    private JLabel labtotaal;
    private receiverGateway receivergateway;
    private receiverGateway privatereceiver;
    private IsenderGateway sendergateway = new senderGateway();
    private BrandstofUpdate brandstoffen;
    private ArrayList<Integer> tankpompen;
    private ArrayList<Pomporder> orderQueue;
    private TankstationOrder huidigeOrder;
    private String correlationLocked;
    public static void main(String[] args) {
        System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");
        JFrame frame = new JFrame("Tankstation");
        frame.setContentPane(new Tankstation().Tankstation);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    public Tankstation(){
        personalID = "EersteStation";
        orderQueue = new ArrayList<>();
        huidigeOrder = new TankstationOrder(personalID);
        labtotaal.setText(huidigeOrder.toString());
        brandstoffen = new BrandstofUpdate(new Brandstof("Benzine", 160), new Brandstof("Diesel", 141), new Brandstof("Gas", 91));
        receivergateway = new receiverGateway("toTankstations");
        privatereceiver = new receiverGateway(personalID);
        privatereceiver.addObserver(this::update);
        receivergateway.addObserver(this::update);
        tankpompen = new ArrayList();
        tankpompen.add(1);
        tankbeurtToevoegenAanOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                huidigeOrder.setPomporder(orderQueue.stream().findFirst().get());
                unlockPomp(orderQueue.stream().findFirst().get().getPomp());
                orderQueue.remove(orderQueue.stream().findFirst().get());

                refresh();
            }
        });
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                huidigeOrder.addItems(new TankstationItem(tbNaam.getText(), (Integer) spinItem.getValue()));
                refresh();
            }
        });
        buttonPay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            buttonPay.setEnabled(false);
            btnAdd.setEnabled(false);
            huidigeOrder.setPompid(personalID);
            correlationLocked = sendergateway.SendTankstationOrder(huidigeOrder, "toShell");
                System.out.println("Waiting for unlock for:" + correlationLocked + " | " + personalID);
            }
        });
    }
    private void unlockPomp(int ID){
        sendergateway.UnlockPomp(ID, personalID);
    }
    private void pushBrandstofPrijzen(BrandstofUpdate update){
        brandstoffen = update;
        //refresh prices
        System.out.println("Pushing the following:");
        System.out.println(update);
        sendergateway.MessagePomps(update, personalID);

    }
    private void unlock(String correlation){
        System.out.println("I'm getting unlocked");
        System.out.println("Lock " + correlation );
        System.out.println("Lock " + correlationLocked);
        if(correlation.equals(correlationLocked)){
            System.out.println("I've been unlocked");
            huidigeOrder = new TankstationOrder(this.personalID);
            buttonPay.setEnabled(true);
            refresh();
        }

    }
    @Override
    public void update(Observable o, Object msg) {
        try {
        if (((ObjectMessage) msg).getObject() instanceof BrandstofUpdate) {
            BrandstofUpdate update = null;
                update = (BrandstofUpdate)((ObjectMessage)msg).getObject();
                pushBrandstofPrijzen(update);
            }
        else if(((ObjectMessage) msg).getObject() instanceof Pomporder){
            System.out.println("Received pomporder");
            Pomporder order = (Pomporder) ((ObjectMessage)msg).getObject();
            orderQueue.add(order);
            refresh();
            //pomporders.addElement(((ObjectMessage) msg).getObject());
        }
        else if(((ObjectMessage)msg).getObject() instanceof Unlock){
            System.out.println("This is a unlock!");
                unlock(((ObjectMessage)msg).getJMSCorrelationID());
        }
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
        }
        private void refresh(){
        labtotaal.setText(huidigeOrder.toString());
        System.out.println("Refreshing");
        if(!orderQueue.isEmpty() && huidigeOrder.getPomporder() == null){
            labbeurt.setText(orderQueue.stream().findFirst().get().toString());
            tankbeurtToevoegenAanOrderButton.setEnabled(true);
        }
        else{
            tankbeurtToevoegenAanOrderButton.setEnabled(false);
        }
        }
//    private void refreshPrices(){
//
//    }
}
