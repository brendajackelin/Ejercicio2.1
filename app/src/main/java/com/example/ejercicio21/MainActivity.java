package com.example.ejercicio21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.ejercicio21.transacciones.transacciones;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int PETICION_ACCESO_CAM = 100;
    VideoView videoView;
    private Button btnguardar, btngrabar, btnlista;
    private static final int GALLERY_INTENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnguardar = (Button)findViewById(R.id.btnguardar);
        btngrabar = (Button)findViewById(R.id.btngrabar);
        videoView = (VideoView)findViewById(R.id.videoView);
        btnlista = (Button)findViewById(R.id.btnlista);

        btngrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisos();
            }
        });

        btnlista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lista = new Intent(getApplicationContext(), ActivityListView.class);
                startActivity(lista);
            }
        });
    }

    private void permisos()
    {

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED  &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PETICION_ACCESO_CAM);
        }
        else
        {
            dispatchTakeVideoIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PETICION_ACCESO_CAM)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakeVideoIntent();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Se necesitan permisos de acceso a la camara", Toast.LENGTH_LONG).show();
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        /*CAPTURAR VIDEO*/
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            videoView.setMediaController((new MediaController(this)));
            videoView.setVideoURI(videoUri); /*PASAR EL VIDEO AL VIDEOVIEW*/
            videoView.requestFocus(); //Mostrar video
            videoView.start();    // Reproducir el video
            Toast.makeText(getApplicationContext(), "Su video ha sido guardado en el storage exitosamente", Toast.LENGTH_LONG).show();
        }

        SQLiteConexion conexion = new SQLiteConexion(this, transacciones.NameDataBase,  null, 1);

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*GUARDAR VIDEO EN LA BD*/
                if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    SQLiteDatabase db = conexion.getWritableDatabase();
                    ContentValues valores = new ContentValues();
                    valores.put(transacciones.tablavideo, transacciones.video.equals(uri.getLastPathSegment()));

                    Long resultado = db.insert(transacciones.tablavideo,transacciones.video, valores);
                    Toast.makeText(getApplicationContext(), "Su video ha sido guardado con exito" +" "+ "Numero de registro: "+resultado, Toast.LENGTH_LONG).show();
                    db.close();
                }
            }
        });


    }
}