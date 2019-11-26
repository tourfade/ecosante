package com.kamitsoft.ecosante.client.user.subscription.order;

public class IOrder {
    public Invoice invoice = new Invoice();
    public Store store = new Store();
    public Action actions = new Action();
    public CustomData custom_data;
}
