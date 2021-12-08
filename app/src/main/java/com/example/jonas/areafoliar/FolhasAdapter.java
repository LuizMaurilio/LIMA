package com.example.jonas.areafoliar;

import android.app.Dialog;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jonas.areafoliar.database.DadosOpenHelper;
import com.example.jonas.areafoliar.repositorio.FolhasRepositorio;

import java.util.List;

public class FolhasAdapter extends RecyclerView.Adapter<FolhasAdapter.ViewHolderFolhas> {
    private List<Folha> folhas;

    FolhasAdapter(List<Folha> folhas) {
        this.folhas = folhas;
    }

    @NonNull
    @Override
    public FolhasAdapter.ViewHolderFolhas onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.linha_config_dados, viewGroup, false);
        return new ViewHolderFolhas(view, viewGroup.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull FolhasAdapter.ViewHolderFolhas viewHolder, int i) {
        if (folhas != null && (folhas.size() > 0)) {
            Folha folha = folhas.get(i);
            viewHolder.edtNome.setText(folha.getNum_Folha()+"");
            viewHolder.edtArea.setText(folha.getArea()+"");
            viewHolder.edtAltura.setText(folha.getComprimento()+"");
            viewHolder.edtLargura.setText(folha.getLargura()+"");
            viewHolder.edtPerimetro.setText(folha.getPerimetro()+"");
        }
    }

    @Override
    public int getItemCount() {
        return folhas.size();
    }

    class ViewHolderFolhas extends RecyclerView.ViewHolder {
        private EditText edtNome, edtArea, edtAltura, edtLargura,edtPerimetro;
        private FolhasRepositorio folhasRepositorio;
        private SQLiteDatabase conexao;
        private Context contextoApp;


        ViewHolderFolhas(@NonNull final View itemView, final Context context) {
            super(itemView);
            edtNome = itemView.findViewById(R.id.edtNome);
            edtArea = itemView.findViewById(R.id.edtArea);
            edtAltura = itemView.findViewById(R.id.edtAltura);
            edtLargura = itemView.findViewById(R.id.edtLargura);
            edtPerimetro = itemView.findViewById(R.id.edtPerimetro);
            contextoApp = context;
            criarConexao();
            folhasRepositorio = new FolhasRepositorio(conexao);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (folhas.size() > 0) {
                        final Folha folha;
                        folha = folhas.get(getLayoutPosition());

                        final Dialog dialog = new Dialog(v.getContext());

                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_alterarfolha); // seu layout
                        dialog.setCancelable(true);

                        Button cancelar = dialog.findViewById(R.id.excBtn);
                        Button confirmar = dialog.findViewById(R.id.confBtn);
                        final EditText input = dialog.findViewById(R.id.edtNomeAlterarFolha);
                        input.setText(folha.getNum_Folha()+"");
                        input.setSelection(input.getText().length());

                        cancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    //Falta recalcular a área da média quando excluir e também atualizar o list com os dados novos!!!!!!
                                    folhasRepositorio.excluir(folha.getIdImg());
                                    dialog.dismiss(); // fecha o dialog
                                } catch (SQLException ignored) {

                                }
                            }
                        });
                        confirmar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                folhasRepositorio.alterar(folha.getIdImg(), input.getText().toString());
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }
            });
        }

        void criarConexao() {
            try {
                DadosOpenHelper dadosOpenHelper = new DadosOpenHelper(contextoApp);
                conexao = dadosOpenHelper.getWritableDatabase();
                folhasRepositorio = new FolhasRepositorio(conexao);
            } catch (SQLException ex) {
                Toast.makeText(contextoApp, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}
