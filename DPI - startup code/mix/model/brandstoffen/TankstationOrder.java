package model.brandstoffen;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.ArrayList;

public class TankstationOrder implements Serializable {
    public String getPompid() {
        return pompid;
    }

    public void setPompid(String pompid) {
        this.pompid = pompid;
    }

    private String pompid;

    private Pomporder pomporder;

    public Pomporder getPomporder() {
        return pomporder;
    }

    public void setPomporder(Pomporder pomporder) {
        this.pomporder = pomporder;
        gevuld = true;
    }

    public ArrayList<TankstationItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<TankstationItem> items) {
        this.items = items;
        gevuld = true;
    }

    private ArrayList<TankstationItem> items;

    public boolean isGevuld() {
        return gevuld;
    }

    private boolean gevuld;

    public TankstationOrder(String pompid){

    }
    public TankstationOrder(Pomporder order){
        pomporder = order;
        gevuld = true;
    }
    public void addItems(TankstationItem item){
        if(items == null){ items = new ArrayList<>();}
        gevuld=true;
        items.add(item);
    }
    @Override
    public String toString() {
        if(gevuld == false){
            return "0 items ingevoerd";
        }
        else if(pomporder == null){
            int prijs=0;
            for (int i = 0; i < items.size(); i++) {
                prijs = prijs + items.get(i).getPrijs();
            }
            return "items: " + items.size() + " | Totaal: " + prijs;
        }
        else if(items == null){
            return "items: 1 | Totaal: " + pomporder.getPrijs();
        }
        else{
            int prijs=0;
            for (int i = 0; i < items.size(); i++) {
                prijs = prijs + items.get(i).getPrijs();

            }
            prijs = prijs+ pomporder.getPrijs();
            int itemcount = items.size()+1;
            return "items: " + itemcount + " | Totaal: " + prijs;
        }
    }
}
