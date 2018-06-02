package com.ayush.steganography.models;

/**
 * Created by vitu on 30/05/2018.
 */

public class Anexo {

    private String idAnexo;
    private String NomeArquivo;
    private String Link;

    public Anexo(){}

    public String getIdAnexo() {
        return idAnexo;
    }

    public void setIdAnexo(String idAnexo) {
        this.idAnexo = idAnexo;
    }

    public String getNomeArquivo() {
        return NomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        NomeArquivo = nomeArquivo;
    }

    public String getLink() {
        return Link;
    }
}
