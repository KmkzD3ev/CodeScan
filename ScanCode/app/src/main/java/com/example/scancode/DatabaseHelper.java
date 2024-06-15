package com.example.scancode;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// Banco de Dados local
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BarcodeDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "barcodes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CODE = "codbarra";
    private static final String COLUMN_TIMESTAMP = "datahora";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CODE + " TEXT,"
                + COLUMN_TIMESTAMP + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertBarcode(String codbarra, String datahora) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CODE, codbarra);
        values.put(COLUMN_TIMESTAMP, datahora);

        long id = db.insert(TABLE_NAME, null, values);
        db.close();

        if (id == -1) {
            Log.e("DATABASE", "Falha ao inserir o código de barras no banco de dados.");
        } else {
            Log.i("DATABASE", "Código de barras inserido com sucesso no banco de dados. ID: " + id);
        }

        return id;
    }

    public Cursor getAllBarcodes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }


}
