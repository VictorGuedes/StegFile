package com.ayush.steganography;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ayush.steganography.adapters.FotoAdapter;
import com.ayush.steganography.models.Foto;

import java.util.List;

public class GridPhotosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_photos);

        RecyclerView mRecyclerView = findViewById(R.id.id_recyclerView_grid);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        List<Foto> fotos = Foto.listAll(Foto.class);

        FotoAdapter myAdapter = new FotoAdapter(this, fotos);
        mRecyclerView.setAdapter(myAdapter);

    }
}
