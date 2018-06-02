package com.ayush.steganography;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextEncodingCallback;
import com.ayush.imagesteganographylibrary.Text.ImageSteganography;
import com.ayush.imagesteganographylibrary.Text.TextEncoding;
import com.ayush.steganography.endpoints.AnexoEndpoint;
import com.ayush.steganography.models.Anexo;
import com.ayush.steganography.models.Foto;
import com.ayush.steganography.models.Resposta;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Encode extends AppCompatActivity implements TextEncodingCallback {

    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "Encode Class";

    private Uri filepath;

    //Bitmaps
    private Bitmap original_image;
    private Bitmap encoded_image;

    ImageView imageView;
    EditText message, secret_key, nomeArquivo;
    Button encode_button;
    private String link;

    //Objects needed for encoding
    TextEncoding textEncoding;
    ImageSteganography imageSteganography, result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);

        imageView = (ImageView) findViewById(R.id.imageview);
        message = (EditText) findViewById(R.id.message);
        secret_key = (EditText) findViewById(R.id.secret_key);
        encode_button = (Button) findViewById(R.id.encode_button);
        nomeArquivo = (EditText )findViewById(R.id.nomeArquivo);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageChooser();
            }
        });


        //Encode Button
        encode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filepath != null){
                    if (message.getText() != null ){

                        //ImageSteganography Object instantiation
                        imageSteganography = new ImageSteganography(message.getText().toString(),
                                secret_key.getText().toString(),
                                original_image);
                        //TextEncoding object Instantiation
                        textEncoding = new TextEncoding(Encode.this, Encode.this);
                        //Executing the encoding
                        textEncoding.execute(imageSteganography);
                    }
                }
            }
        });
    }

    public void uploadImage() throws IOException {
        final ProgressDialog progressDialog = new ProgressDialog(Encode.this);
        progressDialog.setTitle("Enviando para servidor");
        progressDialog.setMessage("espere um pouco...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AnexoEndpoint.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AnexoEndpoint retrofitInterface = retrofit.create(AnexoEndpoint.class);

        File file = new File(getCacheDir(), nomeArquivo.getText().toString());
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = encoded_image;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image",nomeArquivo.getText().toString(), requestFile);
        link = UUID.randomUUID().toString();
        Call<Resposta> call = retrofitInterface.uploadImage(body, link);
        call.enqueue(new Callback<Resposta>() {
            @Override
            public void onResponse(@NonNull Call<Resposta> call, @NonNull Response<Resposta> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                }else {
                    //Log.i("Erro Medio", "onFailure: "+ response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Resposta> call, Throwable t) {
                //Toast.makeText(Encode.this,"Eroo: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                //Log.i("Erro Total", "onFailure: "+t.getLocalizedMessage());
                progressDialog.dismiss();
                startActivity(new Intent(getApplicationContext(), PrincipalActivity.class));
            }
        });
    }

    public void salvarImagem(){

        try {
            uploadImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String name = link;
        File file = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        //File rootdir = new File(file, name);
        //rootdir.mkdir();

        if (encoded_image != null){

            String name_image = name + "_encoded" + ".png";
            File encoded_file = new File(file, name_image);
            try {
                encoded_file.createNewFile();
                FileOutputStream fout_encoded_image = new FileOutputStream(encoded_file);
                encoded_image.compress(Bitmap.CompressFormat.PNG, 100, fout_encoded_image);
                fout_encoded_image.flush();
                fout_encoded_image.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Foto foto = new Foto(
                    name_image,
                    link,
                    encoded_file.getPath(),
                    secret_key.getText().toString());
            foto.save();
        }
    }

    public void ImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image set to imageView
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null){

            filepath = data.getData();
            try{
                original_image = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(original_image);
            }
            catch (IOException e){
                Log.d(TAG, "Error : " + e);
            }
        }
    }

    @Override
    public void onStartTextEncoding() {
        //Whatever you want to do at the start of text encoding
    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {
        this.result = result;
        if (result != null && result.isEncoded()){
            encoded_image = result.getEncoded_image();
            //imageView.setImageBitmap(encoded_image);
            salvarImagem();

        }
    }
}
