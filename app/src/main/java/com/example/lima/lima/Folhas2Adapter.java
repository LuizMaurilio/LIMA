package com.example.lima.lima;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Folhas2Adapter extends ExpandableRecyclerViewAdapter<HistoricoViewHolder, FolhaViewHolder> {
    Folhas2Adapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public HistoricoViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_recyclerview_historico,parent,false);
        return new HistoricoViewHolder(v);
    }

    @Override
    public FolhaViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_recyclerview_folha,parent,false);
        return new FolhaViewHolder(v,parent.getContext());
    }

    @Override
    public void onBindChildViewHolder(FolhaViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Folha folha = (Folha) group.getItems().get(childIndex);
        holder.bind(folha);
    }

    @Override
    public void onBindGroupViewHolder(HistoricoViewHolder holder, int flatPosition, ExpandableGroup group) {
        final Historico historico = (Historico) group;
        holder.bind(historico);
    }
}
