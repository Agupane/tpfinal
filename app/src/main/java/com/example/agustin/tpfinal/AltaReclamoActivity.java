package com.example.agustin.tpfinal;

/**
 * Created by Agustin on 11/10/2016.
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class AltaReclamoActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnCancelar;
    private Button btnAgregar;
    private Button btnAgregarFoto;
    private EditText txtDescripcion;
    private EditText txtMail;
    private EditText txtTelefono;
    private LatLng ubicacion;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_RESULT = 2;
    static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView mImageView;
    private File photoFile;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        ubicacion = (LatLng) extras.get("coordenadas");
        setContentView(R.layout.activity_alta_reclamo);
        btnAgregar = (Button) findViewById(R.id.btnReclamar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnAgregarFoto = (Button) findViewById(R.id.btnAgregarFoto);
        mImageView = (ImageView) findViewById(R.id.imageViewVerFoto);
        txtDescripcion = (EditText) findViewById(R.id.etReclamoTexto);
        txtTelefono= (EditText) findViewById(R.id.etReclamoTelefono);
        txtMail= (EditText) findViewById(R.id.reclamoMail);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accionAgregarReclamo();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accionCancelar();
            }
        });
        btnAgregarFoto.setOnClickListener(this);
    }
    public void accionAgregarReclamo(){
        Reclamo reclamo = new Reclamo();
        reclamo.setEmail(txtMail.getText().toString());
        reclamo.setTelefono(txtTelefono.getText().toString());
        reclamo.setTitulo(txtDescripcion.getText().toString());
        reclamo.setCoordenadas(ubicacion.latitude,ubicacion.longitude);

        reclamo.setFoto(photoFile);
        photoFile=null;
        Intent resultado = new Intent();
        resultado.putExtra("reclamo",reclamo);
        setResult(Activity.RESULT_OK,resultado);
        finish();
    }
    public void accionCancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 1);
        //Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(it, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photoFile=obtenerFile(imageBitmap);
            mImageView.setImageBitmap(imageBitmap);
        }
        */
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                //Get image
                Bitmap newProfilePic = extras.getParcelable("data");
                photoFile = obtenerFile(newProfilePic);
                mImageView.setImageBitmap(newProfilePic);
            }
        }

    }
    private File obtenerFile(Bitmap image){
        //create a file to write bitmap data
        File f = new File(getApplicationContext().getCacheDir(), "filename");
        try {
            f.createNewFile();
        }
        catch(Exception e){

        }

        //Convert bitmap to byte array
        Bitmap bitmap = image;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }
        catch(Exception e){

        }
        return f;
    }
}