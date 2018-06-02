package com.ayush.steganography.models;

import com.orm.SugarRecord;

/**
 * Created by vitu on 02/06/2018.
 */

public class Foto extends SugarRecord {

    private String nomeArquivo;
    private String link;
    private String caminhoArquivo;
    private String chave;

    public Foto(){}

    public Foto(String nomeArquivo, String link, String caminhoArquivo, String chave) {
        this.nomeArquivo = nomeArquivo;
        this.link = link;
        this.caminhoArquivo = caminhoArquivo;
        this.chave = chave;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }
}
