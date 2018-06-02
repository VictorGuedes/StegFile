package com.ayush.steganography.endpoints;

import com.ayush.steganography.models.Anexo;
import com.ayush.steganography.models.Resposta;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Created by vitu on 30/05/2018.
 */

public interface AnexoEndpoint {

    //Rede celular
    public static final String URL_BASE="http://192.168.43.249:49984/api/";

    //Rede de casa
    //public static final String URL_BASE="http://192.168.1.68:49984/api/";

    @GET("Anexos")
    Call<List<Anexo>> listarAnexos();

    @Multipart
    @POST("upload/{link}")
    Call<Resposta> uploadImage(@Part MultipartBody.Part image, @Path("link") String link);

    @Streaming
    @GET("Anexos/download/{link}")
    Call<ResponseBody> downloadImage(@Path("link") String link);
}
