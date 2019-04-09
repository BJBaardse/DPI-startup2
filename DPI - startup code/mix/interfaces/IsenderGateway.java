package interfaces;

import messaging.requestreply.RequestReply;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;
import model.brandstoffen.BrandstofUpdate;
import model.brandstoffen.Pomporder;
import model.brandstoffen.TankstationOrder;
import model.loan.LoanRequest;

public interface IsenderGateway {
//    void messageSomeOne(RequestReply reply, String correlation, String Queue);
//    void messageSomeOne(LoanRequest request, String Queue);
//    void messageSomeOne(BankInterestRequest request, String correlation,String Queue);
//    void messageSomeOne(BankInterestReply request, String correlation, String Queue);

    void MessageAllTankstations(BrandstofUpdate update);
    void MessagePomps(BrandstofUpdate update, String personalID);
    void SendPompOrder(Pomporder order, String Queue);
    void UnlockPomp(int ID, String Queue);
    void unlockstation(String correlation, String Queue);
    String SendTankstationOrder(TankstationOrder order, String Queue);
}
