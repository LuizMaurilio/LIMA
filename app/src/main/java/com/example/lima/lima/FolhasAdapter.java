package com.example.lima.lima;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lima.lima.database.DadosOpenHelper;
import com.example.lima.lima.repositorio.FolhasRepositorio;

import java.util.List;

public class FolhasAdapter extends RecyclerView.Adapter<FolhasAdapter.ViewHolderFolhas> {
    private List<Folha> folhas;
    private SharedPreferences sharedPreferences;
    private ConstraintLayout constraint;

    FolhasAdapter(List<Folha> folhas, SharedPreferences sharedPreferences) {
        this.folhas = folhas;
        this.sharedPreferences = sharedPreferences;
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
            if (sharedPreferences.getBoolean("calcWidth", false)) viewHolder.edtLargura.setText(folha.getLargura()+"");
            if (sharedPreferences.getBoolean("calcLength", false)) viewHolder.edtAltura.setText(folha.getComprimento()+"");
            if (sharedPreferences.getBoolean("calcArea", false)) viewHolder.edtArea.setText(folha.getArea()+"");
            if (sharedPreferences.getBoolean("calcPerimeter", false)) viewHolder.edtPerimetro.setText(folha.getPerimetro()+"");
            if (sharedPreferences.getBoolean("calcWidthDLength", false)) viewHolder.edtLarguraComprimento.setText(folha.getLargcomp()+"");
        }
    }

    @Override
    public int getItemCount() {
        return folhas.size();
    }

    class ViewHolderFolhas extends RecyclerView.ViewHolder {
        private EditText edtNome, edtArea, edtAltura, edtLargura,edtPerimetro, edtLarguraComprimento;
        private FolhasRepositorio folhasRepositorio;
        private SQLiteDatabase conexao;
        private Context contextoApp;
        private TextView txtArea, txtAltura, txtLargura, txtPerimetro, txtLarguraComprimento;

        ViewHolderFolhas(@NonNull final View itemView, final Context context) {
            super(itemView);
            constraint = itemView.findViewById(R.id.parent_constraint);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraint);

            edtNome = itemView.findViewById(R.id.edtNome);
            edtArea = itemView.findViewById(R.id.edtArea);
            edtAltura = itemView.findViewById(R.id.edtAltura);
            edtLargura = itemView.findViewById(R.id.edtLargura);
            edtPerimetro = itemView.findViewById(R.id.edtPerimetro);
            edtLarguraComprimento = itemView.findViewById(R.id.edtLarguraComprimento);

            txtArea = itemView.findViewById(R.id.textView2);
            txtAltura = itemView.findViewById(R.id.textView3);
            txtLargura = itemView.findViewById(R.id.textView4);
            txtPerimetro = itemView.findViewById(R.id.textView5);
            txtLarguraComprimento = itemView.findViewById(R.id.textView9);

            Boolean notCheckedArea = !sharedPreferences.getBoolean("calcArea", false);
            Boolean notCheckedAltura = !sharedPreferences.getBoolean("calcLength", false);
            Boolean notCheckedLargura = !sharedPreferences.getBoolean("calcWidth", false);
            Boolean notCheckedPerimetro = !sharedPreferences.getBoolean("calcPerimeter", false);
            Boolean notCheckedLargComp = !sharedPreferences.getBoolean("calcWidthDLength", false);


            if (notCheckedArea) {
                constraintSet.connect(R.id.edtAltura,ConstraintSet.TOP,R.id.edtNome,ConstraintSet.BOTTOM,16);
                constraintSet.connect(R.id.textView3,ConstraintSet.TOP,R.id.edtNome,ConstraintSet.BOTTOM,28);
            }

            if (notCheckedAltura) {
                if (notCheckedArea){
                    constraintSet.connect(R.id.edtLargura,ConstraintSet.TOP,R.id.edtNome,ConstraintSet.BOTTOM,16);
                    constraintSet.connect(R.id.textView4,ConstraintSet.TOP,R.id.edtNome,ConstraintSet.BOTTOM,28);
                }
                else {
                    constraintSet.connect(R.id.edtLargura,ConstraintSet.TOP,R.id.edtArea,ConstraintSet.BOTTOM,16);
                    constraintSet.connect(R.id.textView4,ConstraintSet.TOP,R.id.edtArea,ConstraintSet.BOTTOM,28);
                }
            }

            if (notCheckedLargura) {
                if (notCheckedAltura){
                    if (notCheckedArea){
                        constraintSet.connect(R.id.edtPerimetro,ConstraintSet.TOP,R.id.edtNome,ConstraintSet.BOTTOM,16);
                        constraintSet.connect(R.id.textView5,ConstraintSet.TOP,R.id.edtNome,ConstraintSet.BOTTOM,28);
                    }
                    else {
                        constraintSet.connect(R.id.edtPerimetro,ConstraintSet.TOP,R.id.edtArea,ConstraintSet.BOTTOM,16);
                        constraintSet.connect(R.id.textView5,ConstraintSet.TOP,R.id.edtArea,ConstraintSet.BOTTOM,28);
                    }
                }
                else {
                    constraintSet.connect(R.id.edtPerimetro,ConstraintSet.TOP,R.id.edtAltura,ConstraintSet.BOTTOM,16);
                    constraintSet.connect(R.id.textView5,ConstraintSet.TOP,R.id.edtAltura,ConstraintSet.BOTTOM,28);
                }
            }

            if (notCheckedPerimetro) {
                if(notCheckedLargura){
                    if (notCheckedAltura){
                        if (notCheckedArea){
                            constraintSet.connect(R.id.edtLarguraComprimento,ConstraintSet.TOP,R.id.edtNome,ConstraintSet.BOTTOM,16);
                            constraintSet.connect(R.id.textView9,ConstraintSet.TOP,R.id.edtNome,ConstraintSet.BOTTOM,28);
                        }
                        else {
                            constraintSet.connect(R.id.edtLarguraComprimento,ConstraintSet.TOP,R.id.edtArea,ConstraintSet.BOTTOM,16);
                            constraintSet.connect(R.id.textView9,ConstraintSet.TOP,R.id.edtArea,ConstraintSet.BOTTOM,28);
                        }
                    }
                    else {
                        constraintSet.connect(R.id.edtLarguraComprimento,ConstraintSet.TOP,R.id.edtAltura,ConstraintSet.BOTTOM,16);
                        constraintSet.connect(R.id.textView9,ConstraintSet.TOP,R.id.edtAltura,ConstraintSet.BOTTOM,28);
                    }
                }
                else {
                    constraintSet.connect(R.id.edtLarguraComprimento,ConstraintSet.TOP,R.id.edtLargura,ConstraintSet.BOTTOM,16);
                    constraintSet.connect(R.id.textView9,ConstraintSet.TOP,R.id.edtLargura,ConstraintSet.BOTTOM,28);
                }
            }

            constraintSet.applyTo(constraint);

            if (notCheckedArea) {
                edtArea.setVisibility(itemView.INVISIBLE);
                txtArea.setVisibility(itemView.INVISIBLE);
            }
            if (notCheckedAltura) {
                edtAltura.setVisibility(itemView.INVISIBLE);
                txtAltura.setVisibility(itemView.INVISIBLE);
            }
            if (notCheckedLargura) {
                edtLargura.setVisibility(itemView.INVISIBLE);
                txtLargura.setVisibility(itemView.INVISIBLE);
            }
            if (notCheckedPerimetro) {
                edtPerimetro.setVisibility(itemView.INVISIBLE);
                txtPerimetro.setVisibility(itemView.INVISIBLE);
            }
            if (notCheckedLargComp) {
                edtLarguraComprimento.setVisibility(itemView.INVISIBLE);
                txtLarguraComprimento.setVisibility(itemView.INVISIBLE);
            }

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
