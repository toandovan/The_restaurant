package com.example.myapp.Model;

public class Order {
    private int ID;
    private String ProductId;
    private String ProductName;
    private String Quantity;
    private String Price;
    private String Discount;
    private String Image;

    public  Order(){

    }



    public Order(String productId,String prouctName,String quantity,String price,String discount,String image){
        ProductId=productId;
        ProductName=prouctName;
        Quantity=quantity;
        Price=price;
        Discount=discount;
        Image=image;
    }

    public Order(int ID,String productId,String prouctName,String quantity,String price,String discount,String image){
        this.ID=ID;
        ProductId=productId;
        ProductName=prouctName;
        Quantity=quantity;
        Price=price;
        Discount=discount;
        Image=image;
    }

    public  int getID()
    {
        return ID;
    }
    public void setID(int ID)
    {

        this.ID=ID;
    }

    public String getProductId() {
        return ProductId;
    }

    public String getProductName() {
        return ProductName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public String getPrice() {
        return Price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
