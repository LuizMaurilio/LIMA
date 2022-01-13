package com.example.jonas.areafoliar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jonas.areafoliar.database.DadosOpenHelper;
import com.example.jonas.areafoliar.repositorio.FolhasRepositorio;

import java.util.ArrayList;
import java.util.List;

public class ActConfigDados extends AppCompatActivity {
    private EditText edtNomeTeste;
    private RecyclerView listDados;
    private FolhasRepositorio folhasRepositorio;
    //private SQLiteDatabase conexao;
    private Folha folha;
    public static List<Folha> dados;
    private ActCalculos calc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_config_dados);
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listDados = findViewById(R.id.listConfigDados);
        criarConexao();
        listDados.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listDados.setLayoutManager(linearLayoutManager);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String cod = bundle.getString("Id_img");
        calc = folhasRepositorio.consultar(false, cod);
        dados = calc.getListaFolhas();
        edtNomeTeste = findViewById(R.id.edtNomeTeste);
        verificaParametro();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.act_config_dados, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*@Override
    public void onBackPressed() {
        Intent it2 = new Intent(this, ActDados.class);
        startActivity(it2);
    }*/

    private void confirmar() {
        try{
            folhasRepositorio.alterar(calc.getIdImg(),edtNomeTeste.getText().toString());
            Intent itDados = new Intent(this, ActDados.class);
            startActivityForResult(itDados, 0);
            finish();
        }catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            //dlg.setTitle("Erro");
            dlg.setTitle("Error");
            dlg.setMessage(ex.getMessage());
            //dlg.setNeutralButton("Confirmar",null);
            dlg.setNeutralButton("Confirm",null);
            dlg.show();
        }
    }

    private void verificaParametro() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        int cod = bundle.getInt("CODIGO");

        for(int j = 0; j < dados.size(); j ++){
            if(dados.get(j).getCod() == cod){
                folha = dados.get(j);
            }
        }

        ArrayList<Folha> folhasSelecionadas = new ArrayList<>();
        for(int i = 0; i < dados.size(); i ++){
//            if(dados.get(i).getData().equals(folha.getData())){
                folhasSelecionadas.add(dados.get(i));
           // }
//            else if(dados.get(i).getNum_Folha() == 1 && dados.get(i).getData().equals(folha.getData())){
//                edtNomeTeste.setText(dados.get(i).getNome());
//                edtNomeTeste.setSelection(edtNomeTeste.getText().length());
//            }
        }
        edtNomeTeste.setText(calc.getNome());
        edtNomeTeste.setSelection(edtNomeTeste.getText().length());

        FolhasAdapter folhasAdapter = new FolhasAdapter(folhasSelecionadas);
        listDados.setAdapter(folhasAdapter);
    }

    public void criarConexao() {
        try {
            DadosOpenHelper dadosOpenHelper = new DadosOpenHelper(this);
            SQLiteDatabase conexao = dadosOpenHelper.getWritableDatabase();
            folhasRepositorio = new FolhasRepositorio(conexao);
        } catch (SQLException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_ok:
                confirmar();
                break;
            case R.id.action_excluir:
                folhasRepositorio.excluir(calc.getIdImg());
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
