package com.ayush.steganography.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayush.steganography.R;
import com.ayush.steganography.models.Foto;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by vitu on 02/06/2018.
 */

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.ViewHolder> {

    private Context mContext;
    private List<Foto> fotos;

    public FotoAdapter(Context mContext, List<Foto> fotos) {
        this.mContext = mContext;
        this.fotos = fotos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.foto_recycler_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Bitmap bitmap = BitmapFactory.decodeFile(fotos.get(position).getCaminhoArquivo());
        holder.imageView.setImageBitmap(bitmap);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.detalhes_foto);
                dialog.setTitle("Detalhes Foto...");
                TextView textLink = (TextView) dialog.findViewById(R.id.textView2_link_detalhe);
                TextView textChave = (TextView) dialog.findViewById(R.id.textchave_detalhe);
                ImageView imageDetalhe = (ImageView) dialog.findViewById(R.id.imageView);

                textLink.setText(fotos.get(position).getLink());
                textChave.setText(fotos.get(position).getChave());
                imageDetalhe.setImageBitmap(bitmap);

                dialog.setCancelable(true);
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return fotos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.row_foto);
        }
    }

}
