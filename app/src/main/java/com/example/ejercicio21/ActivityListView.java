package com.example.ejercicio21;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ejercicio21.tabla.video;
import com.example.ejercicio21.transacciones.transacciones;

import java.sql.SQLException;
import java.util.ArrayList;

public class ActivityListView extends AppCompatActivity {

    ListView lista;
    ArrayList<String> listaInformacion;
    ArrayList<video>listavideo;
    SQLiteConexion conexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        conexion = new SQLiteConexion(getApplicationContext(), "DBActual",null,1);
        lista = (ListView) findViewById(R.id.listview);

        try {
            consultarLista();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ArrayAdapter adaptador = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaInformacion);
        lista.setAdapter(adaptador);
    }

    private void consultarLista() throws SQLException {
        SQLiteDatabase db = conexion.getReadableDatabase();
        video video = null;
        listavideo = new ArrayList<video>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + transacciones.video, null);
        while(cursor.moveToNext()){

            video = new video();

            video.getVideo();
            listavideo.add(video);
        }
        obtenerLista();
    }

    private void obtenerLista() {
        listaInformacion = new ArrayList<String>();
        for (int i = 0; i < listavideo.size(); i++){
            listaInformacion.add(String.valueOf(listavideo.get(i).getVideo()));
        }
    }
}