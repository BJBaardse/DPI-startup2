package model.brandstoffen;

import java.io.Serializable;

public class BrandstofUpdate implements Serializable {
    public Brandstof getBenzine() {
        return benzine;
    }

    public Brandstof getDiesel() {
        return diesel;
    }

    public Brandstof getGas() {
        return gas;
    }

    private Brandstof benzine;
    private Brandstof diesel;
    private Brandstof gas;

    public BrandstofUpdate(Brandstof benzine, Brandstof diesel, Brandstof gas){
        this.benzine = benzine;
        this.diesel = diesel;
        this.gas = gas;
    }



}
