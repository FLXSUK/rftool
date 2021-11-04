package uk.co.flxs.rftool;


public class OrderItem
{
    private String sku;
    private String name;
    private int qty;

    public OrderItem(String sku, String name, int qty) {
        this.sku = sku;
        this.name = name;
        this.qty = qty;
    }
    
    public void increment(int increaseBy)
    {
        this.qty = this.qty + increaseBy;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public int getQty() {
        return qty;
    }
    
    public String[] asRow() {
        String[] row = new String[] {sku, name, Integer.toString(qty)};
        return row;
    }
}
