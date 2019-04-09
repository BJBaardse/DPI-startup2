package application;

import controllers.receiverGateway;
import controllers.senderGateway;
import interfaces.IsenderGateway;
import model.brandstoffen.Brandstof;
import model.brandstoffen.BrandstofUpdate;
import model.brandstoffen.TankstationOrder;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.swing.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class Shell implements Observer {
    SpinnerNumberModel model = new SpinnerNumberModel(0.0, -1000.0, 1000.0, 0.01);
    private IsenderGateway sendergateway;
    private JRadioButton rbBenzine;
    private JRadioButton rbDiesel;
    private JRadioButton rbGas;
    private JButton pushPrijsButton;
    private JPanel panel1;
    private JSpinner spinBenzine;
    private JSpinner spinDiesel;
    private JSpinner spinGas;
    private receiverGateway receiverGateway;
    private BrandstofUpdate prijzen = new BrandstofUpdate(new Brandstof("Benzine", 162), new Brandstof("Diesel", 150), new Brandstof("Gas", 90));
    //items

    public Shell() {
        pushPrijsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        sendergateway = new senderGateway();
        receiverGateway = new receiverGateway("toShell");
        receiverGateway.addObserver(this::update);
        spinBenzine.setValue(prijzen.getBenzine().prijs);
        spinDiesel.setValue(prijzen.getDiesel().prijs);
        spinGas.setValue(prijzen.getGas().prijs);
        pushPrijsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    pushprijzen();
            }
        });
        pushprijzen();
    }
    private void pushprijzen(){
        prijzen = new BrandstofUpdate(new Brandstof("Benzine", (int)spinBenzine.getValue()), new Brandstof("Diesel", (int)spinDiesel.getValue()), new Brandstof("Gas", (int)spinGas.getValue()));
        sendergateway.MessageAllTankstations(prijzen);
        System.out.println("Update send!");
    }

    public static void main(String[] args) {
        System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");
        JFrame frame = new JFrame("Shell");
        frame.setContentPane(new Shell().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


    }
    public boolean writeBonnetje(TankstationOrder order){
        DateFormat sdf = new SimpleDateFormat("dd MM yyyy _ HH mm ss");
        Date date = new Date();
        String mapname = sdf.format(date).toString() + " Tankbon.json";
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(mapname);
        try{
            mapper.writeValue(file, order);
            return true;
        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }
    public void finnishbonnetje(String lockedID, String queue){
        System.out.println("Unlocking:" + lockedID + " | " + queue);
        sendergateway.unlockstation(lockedID, queue);
    }
    @Override
    public void update(Observable o, Object arg) {
        try {
            if(((ObjectMessage)arg).getObject() instanceof TankstationOrder){
                TankstationOrder order = (TankstationOrder)((ObjectMessage)arg).getObject();
                writeBonnetje(order);
                finnishbonnetje(((ObjectMessage)arg).getJMSCorrelationID(), order.getPompid());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
