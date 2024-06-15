package com.example.scancode.ApiService;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

// Interface que define os métodos da API
public interface ApiService {
    @FormUrlEncoded

    @POST("testeApp.php")

        // Método que envia os dados do código de barras para o servidor
        // Retorna um Call que representa a requisição à API
    Call<ServerResponse> sendBarcodeData(

            // O valor do parâmetro será associado ao campo "codbarra"
            @Field("codbarra") String codbarra,

            // O valor do parâmetro será associado ao campo "datahora"
            @Field("datahora") String datahora
    );
}
