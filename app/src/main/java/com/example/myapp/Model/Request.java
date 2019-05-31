package com.example.myapp.Model;

import java.util.List;

public class Request {
    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private List<Order> foods;//list of food order
    private  String paymentState;

    public Request(){

    }
    public Request(String phone,String name,String address,String total,String status,String paymentState,List<Order> foods){
        this.phone=phone;
        this.name=name;
        this.address=address;
        this.total=total;
        this.foods=foods;
        this.status=status;//default is 0,0  ,placed,l: shipping,2 shipped
        this.paymentState=paymentState;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public String getPaymentState() {
        return paymentState;
    }


}
