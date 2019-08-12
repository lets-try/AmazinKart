package com.amazingKart;

public class AmazingKart {
    public static void main(String[] args) {
        AmazingKartService amazingKartService = new AmazingKartService(args[0]);
        amazingKartService.listProducts();
    }
}
