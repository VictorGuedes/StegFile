package com.ayush.steganography;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.ayush.steganography.adapters.recyclerAdapter;
import com.ayush.steganography.endpoints.AnexoEndpoint;
import com.ayush.steganography.models.Anexo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PrincipalActivity extends AppCompatActivity {

    private List<Anexo> anexos;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private recyclerAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Encode.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.id_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        anexos = new ArrayList<>();
        adapter = new recyclerAdapter(anexos, this);
        recyclerView.setAdapter(adapter);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AnexoEndpoint.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AnexoEndpoint service = retrofit.create(AnexoEndpoint.class);
        final Call<List<Anexo>> requestPropostas = service.listarAnexos();
        carregarDados(requestPropostas, recyclerView);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutTeses);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                carregarDados(requestPropostas, recyclerView);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if(child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                    Intent intent = new Intent(getApplicationContext(), Decode.class);
                    intent.putExtra("idAnexo", anexos.get(position).getLink());
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_search){
            return true;

        }else if(id == R.id.local_user){
            startActivity(new Intent(this, GridPhotosActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void carregarDados(Call<List<Anexo>> requestPropostas, final RecyclerView recyclerView){
        requestPropostas.clone().enqueue(new Callback<List<Anexo>>() {
            @Override
            public void onResponse(Call<List<Anexo>> call, Response<List<Anexo>> response) {
                if(!response.isSuccessful()){
                    //COLOCAR IMAGEM TRISTE NO FUNDO DA TELA CASO NÃO TENHA NET
                    Log.i("TAG", "Erro " + response.code());
                    Toast.makeText(PrincipalActivity.this, "Sem internt :/", Toast.LENGTH_LONG).show();

                }else {
                    List<Anexo> teses = response.body();
                    anexos.clear();
                    anexos.addAll(teses);

                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(Call<List<Anexo>> call, Throwable t) {
                //COLOCAR IMAGEM TRISTE NO FUNDO DA TELA CASO NÃO TENHA NET
                Log.i("TAG_ERRO", t.getMessage());
                Toast.makeText(PrincipalActivity.this, "Sem internt :/", Toast.LENGTH_LONG).show();


            }
        });
    }

}
