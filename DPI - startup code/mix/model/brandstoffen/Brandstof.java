package model.brandstoffen;

import java.io.Serializable;

public class Brandstof implements Serializable {
    public String naam;
    public int prijs;

    public Brandstof(String naam, int prijs){
        this.naam = naam;
        this.prijs = prijs;
    }

    @Override
    public String toString() {
        return "\u00a3" + prijs;
    }
}
