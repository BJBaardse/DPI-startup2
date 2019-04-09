package model.brandstoffen;

import java.io.Serializable;

public class Pomporder implements Serializable {
    private int Pomp;
    private Brandstof brandstof;
    private int volume;

    public int getPomp() {
        return Pomp;
    }

    public Brandstof getBrandstof() {
        return brandstof;
    }

    public int getVolume() {
        return volume;
    }

    public int getPrijs() {
        return prijs;
    }

    public boolean isPaid() {
        return paid;
    }

    private int prijs;
    public boolean paid;

    public Pomporder(int pomp, Brandstof Brand, int Vol){
        Pomp = pomp;
        brandstof = Brand;
        volume = Vol;
        prijs = brandstof.prijs * volume;
    }

    @Override
    public String toString() {
        if(!paid){
            return "Order at " + Pomp + " for " + volume + " liter " + brandstof.naam + " | " + prijs;
        }
        else{
            return "Order at " + Pomp + " for " + volume + " liter " + brandstof.naam + " | " + prijs + " \u2713";
        }
    }
}
