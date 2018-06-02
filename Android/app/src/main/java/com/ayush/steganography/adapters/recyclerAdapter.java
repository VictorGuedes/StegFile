package com.ayush.steganography.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ayush.steganography.R;
import com.ayush.steganography.models.Anexo;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vitu on 30/05/2018.
 */

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.ViewHolder> implements Filterable {
    private List<Anexo> anexos;
    private List<Anexo> anexosFiltro;
    private Context context;

    public recyclerAdapter(List<Anexo> anexos, Context context){
        this.anexos = anexos;
        this.anexosFiltro = anexos;
        this.context = context;
    }

    @Override
    public recyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.anexo_recycler_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(recyclerAdapter.ViewHolder holder, int position) {
        holder.nome.setText(anexosFiltro.get(position).getNomeArquivo());
        holder.link.setText(anexosFiltro.get(position).getLink());
    }

    @Override
    public int getItemCount() {
        return anexosFiltro.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    anexosFiltro = anexos;
                } else {
                    List<Anexo> filteredList = new ArrayList<>();
                    for (Anexo row : anexos) {
                        if (row.getLink().contains(charString)) {
                            filteredList.add(row);
                        }
                    }
                    anexosFiltro = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = anexosFiltro;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                anexosFiltro = (ArrayList<Anexo>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nome;
        private TextView link;


        public ViewHolder(View itemView) {
            super(itemView);
            nome = (TextView) itemView.findViewById(R.id.nome_arquivo);
            link = (TextView) itemView.findViewById(R.id.link_arquivo);
        }
    }


}
