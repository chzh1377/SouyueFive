package com.zhongsou.souyue.module;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class AdDetail extends ResponseObject {

    private String logo = "";
    private String name = "";
    private String brand = "";
    private List<Product> product = new ArrayList<Product>();
    private AdContact contact = new AdContact();
    private String description = "";

    public String logo() {
        return logo;
    }

    public void logo_$eq(String logo) {
        this.logo = logo;
    }

    public String name() {
        return name;
    }

    public void name_$eq(String name) {
        this.name = name;
    }

    public String brand() {
        return brand;
    }

    public void brand_$eq(String brand) {
        this.brand = brand;
    }

    public List<Product> product() {
        return product;
    }

    public void product_$eq(List<Product> product) {
        this.product = product;
    }

    public AdContact contact() {
        return contact;
    }

    public void contact_$eq(AdContact contact) {
        this.contact = contact;
    }

    public String description() {
        return description;
    }

    public void description_$eq(String description) {
        this.description = description;
    }

}
