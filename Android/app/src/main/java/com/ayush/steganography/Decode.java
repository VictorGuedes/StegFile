package com.ayush.steganography;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextDecodingCallback;
import com.ayush.imagesteganographylibrary.Text.ImageSteganography;
import com.ayush.imagesteganographylibrary.Text.TextDecoding;
import com.ayush.steganography.endpoints.AnexoEndpoint;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Decode extends AppCompatActivity implements TextDecodingCallback {

    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "Decode Class";

    private Uri filepath;

    //Bitmap
    private Bitmap original_image;

    //Initializing the UI components
    TextView textView;
    ImageView imageView;
    EditText secret_key;
    Button decode_button;

    //ImageSteganography object
    ImageSteganography result;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);
        Bundle extra = getIntent().getExtras();
        if(extra != null) {
            id = extra.getString("idAnexo");

        }
        //Instantiation of UI components
        textView = (TextView) findViewById(R.id.whether_decoded);
        imageView = (ImageView) findViewById(R.id.imageview);
        secret_key = (EditText) findViewById(R.id.secret_key);
        decode_button = (Button) findViewById(R.id.decode_button);

        //Decode Button
        decode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filepath != null){

                    //Making the ImageSteganography object
                    ImageSteganography imageSteganography = new ImageSteganography(secret_key.getText().toString(),
                            original_image);

                    //Making the TextDecoding object
                    TextDecoding textDecoding = new TextDecoding(Decode.this, Decode.this);

                    //Execute Task
                    textDecoding.execute(imageSteganography);
                }
            }
        });

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AnexoEndpoint.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AnexoEndpoint downloadService = retrofit.create(AnexoEndpoint.class);
        Call<ResponseBody> call = downloadService.downloadImage(id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Download", "server contacted and has file " + response.body());

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String header = response.headers().get("Content-Disposition");
                                String fileName = header.replace("attachment; filename=", "foto.png");

                                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile(), fileName);
                                BufferedSink sink = Okio.buffer(Okio.sink(file));
                                sink.writeAll(response.body().source());
                                sink.close();

                                original_image = BitmapFactory.decodeFile(file.getPath());
                                filepath = Uri.parse(file.getPath());

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(original_image);

                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();

                    //boolean writtenToDisk = writeResponseBodyToDisk(response.body());
                    //Log.d("Download", "file download was a success? " + writtenToDisk);
                } else {
                    Log.d("Download", "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Download", "error");
            }
        });
    }


    @Override
    public void onStartTextEncoding() {
        //Whatever you want to do by the start of textDecoding
    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {

        this.result = result;

        if (result != null){
            if (!result.isDecoded())
                textView.setText("Nenhuma mensagem encontrada.");
            else{
                if (!result.isSecretKeyWrong()){
                    textView.setText("Mensagem escondida: " + result.getMessage());
                }
                else {
                    textView.setText("Não é a chave para essa imagem.");
                }
            }
        }
        else {
            textView.setText("Selecione a Imagem.");
        }


    }
}
