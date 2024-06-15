package com.example.scancode.ApiService;

//Modelo de Dados
public class BarcodeData {
    private String codbarra;
    private String datahora;

    public BarcodeData(String codbarra, String datahora) {
        this.codbarra = codbarra;
        this.datahora = datahora;
    }

    public String getCodbarra() {
        return codbarra;
    }

    public String getDatahora() {
        return datahora;
    }
}


