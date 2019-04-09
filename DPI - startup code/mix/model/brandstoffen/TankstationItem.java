package model.brandstoffen;

import java.io.Serializable;

public class TankstationItem implements Serializable {
    public String getNaam() {
        return naam;
    }

    public int getPrijs() {
        return Prijs;
    }

    private String naam;
    private int Prijs;

    public TankstationItem(String nm, int pr){
        naam = nm;
        Prijs = pr;
    }

    @Override
    public String toString() {
        return "1x " + naam;
    }
}
