package com.example.scancode;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.scancode.ApiService.ApiService;
import com.example.scancode.ApiService.RetrofitClient;
import com.example.scancode.ApiService.ServerResponse;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Classe principal que estende de AppCompatActivity
public class MainActivity extends AppCompatActivity {


    // Declaração de variáveis
    private TextView tvBarcodeResult;
    private String barcodeResult;
    private DatabaseHelper databaseHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define a cor da barra de status
        getWindow().setStatusBarColor(getResources().getColor(R.color.color_status_bar));

        // Inicializa os botões e o TextView
        Button btnScan = findViewById(R.id.btn_scan);
        tvBarcodeResult = findViewById(R.id.tv_barcode_result);
        Button btnSend = findViewById(R.id.btn_send);

        // Inicializa o helper do banco de dados
        databaseHelper = new DatabaseHelper(this);

        // Define o que acontece quando o botão de scan é clicado
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(MainActivity.this).initiateScan();
            }
        });

        // Define o que acontece quando o botão de enviar é clicado
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToServer();
            }
        });

        // Exibe todos os códigos de barras salvos no banco de dados
        displayAllBarcodes();

    }

    // Este método é chamado quando a atividade recebe um resultado de outra atividade
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                tvBarcodeResult.setText("Cancelled");
                Toast.makeText(MainActivity.this, "Leitura do código de barras cancelada", Toast.LENGTH_SHORT).show();
            } else {
                barcodeResult = result.getContents();
                tvBarcodeResult.setText(barcodeResult);
                saveBarcodeToDatabase(barcodeResult);
            }
        }
    }

    // Este método salva o código de barras no banco de dados
    private void saveBarcodeToDatabase(String barcode) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        long id = databaseHelper.insertBarcode(barcode, timestamp);

        if (id != -1) {      // Código de barras salvo com sucesso
            Toast.makeText(MainActivity.this, "Código de barras salvo com sucesso!", Toast.LENGTH_SHORT).show();
            Log.i("DATABASE", "Código de barras salvo com sucesso. ID: " + id);

        } else {    // Falha ao salvar o código de barras
            Toast.makeText(MainActivity.this, "Falha ao salvar o código de barras.", Toast.LENGTH_SHORT).show();
            Log.e("DATABASE", "Falha ao salvar o código de barras.");
        }
    }

    // Metodo de Envio de Dados ao Servidor via Api usando Retrofitt2
    private void sendDataToServer() {
        if (barcodeResult != null) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            Call<ServerResponse> call = apiService.sendBarcodeData(barcodeResult, timestamp);

            call.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    if (response.isSuccessful()) {
                        ServerResponse serverResponse = response.body();
                        if (serverResponse != null) {

                            Toast.makeText(MainActivity.this, "Dados enviados com sucesso!", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MainActivity.this, "Resposta inesperada do servidor: " , Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Erro no Servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Falha ao enviar dados: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("API_CALL", "Erro na chamada da API", t);
                }
            });
        } else {    // Nenhum código de barras para enviar

        }
    }

    // Este método exibe todos os códigos de barras salvos no banco de dados
    @SuppressLint("Range")
    private void displayAllBarcodes() {
        Cursor cursor = databaseHelper.getAllBarcodes();
        if (cursor.moveToFirst()) {
            do {
                String barcode = cursor.getString(cursor.getColumnIndex("codbarra"));
                String timestamp = cursor.getString(cursor.getColumnIndex("datahora"));


                Log.d("Database Content", "Barcode: " + barcode + ", Timestamp: " + timestamp);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

}
