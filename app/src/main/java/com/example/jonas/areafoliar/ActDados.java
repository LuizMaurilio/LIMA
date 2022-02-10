package com.example.jonas.areafoliar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.jonas.areafoliar.database.DadosOpenHelper;
import com.example.jonas.areafoliar.repositorio.FolhasRepositorio;

import org.opencv.core.Mat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActDados extends AppCompatActivity{
    private RecyclerView listDados;
    private SQLiteDatabase conexao;
    private FolhasRepositorio folhasRepositorio;
    private String diaAtual,mesAtual,nomeMesAtual,diaFolha,mesFolha;
    public static List<Folha> calculos;
    private ArrayList<Folha> maisAntigo = new ArrayList<>();
    private ArrayList<Folha> mesPresente = new ArrayList<>();
    private ArrayList<Folha> mesPassado = new ArrayList<>();
    private ArrayList<Folha> maisRecentes = new ArrayList<>();
    private Folhas2Adapter folhas2Adapter;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dados);
        listDados = findViewById(R.id.listDados);
        criarConexao();
        listDados.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listDados.setLayoutManager(linearLayoutManager);
        folhasRepositorio = new FolhasRepositorio(conexao);
        calculos = folhasRepositorio.consultaHist();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        Date data = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();
        String data_completa = dateFormat.format(data_atual);
        diaAtual = data_completa.substring(0,2);
        mesAtual = data_completa.substring(3,5);
        switch (mesAtual) {
            case "01":
                //nomeMesAtual = "Janeiro";
                nomeMesAtual = "January";
                break;
            case "02":
                //nomeMesAtual = "Fevereiro";
                nomeMesAtual = "February";
                break;
            case "03":
                //nomeMesAtual = "Março";
                nomeMesAtual = "March";
                break;
            case "04":
                //nomeMesAtual = "Abril";
                nomeMesAtual = "April";
                break;
            case "05":
                //nomeMesAtual = "Maio";
                nomeMesAtual = "May";
                break;
            case "06":
                //nomeMesAtual = "Junho";
                nomeMesAtual = "June";
                break;
            case "07":
                //nomeMesAtual = "Julho";
                nomeMesAtual = "July";
                break;
            case "08":
                //nomeMesAtual = "Agosto";
                nomeMesAtual = "August";
                break;
            case "09":
                //nomeMesAtual = "Setembro";
                nomeMesAtual = "September";
                break;
            case "10":
                //nomeMesAtual = "Outubro";
                nomeMesAtual = "October";
                break;
            case "11":
                //nomeMesAtual = "Novembro";
                nomeMesAtual = "November";
                break;
            default:
                //nomeMesAtual = "Dezembro";
                nomeMesAtual = "December";
                break;
        }
        histSections(calculos);
        folhas2Adapter = new Folhas2Adapter(addHistoricos());
        listDados.setAdapter(folhas2Adapter);
    }

    public ArrayList<Historico> addHistoricos(){
        ArrayList<Historico> historicos = new ArrayList<>();
        Historico recente = new Historico("Today",maisRecentes);
        historicos.add(recente);
        Historico presente = new Historico(nomeMesAtual,mesPresente);
        historicos.add(presente);
        Historico passado = new Historico("Last month",mesPassado);
        historicos.add(passado);
        Historico antigos = new Historico("Older",maisAntigo);
        historicos.add(antigos);
        return historicos;
    }

    public void histSections(List<Folha> calculos){
        for(int i = 0; i < calculos.size(); i ++){
            diaFolha = calculos.get(i).getIdImg().substring(0,2);
            mesFolha = calculos.get(i).getIdImg().substring(3,5);
            if(diaFolha.equals(diaAtual) && mesFolha.equals(mesAtual)){
                maisRecentes.add(calculos.get(i));
            }else if(mesFolha.equals(mesAtual)){
                mesPresente.add(calculos.get(i));
            }else if(Integer.parseInt(mesFolha.trim()) == (Integer.parseInt((mesAtual.trim())) - 1)){
                mesPassado.add(calculos.get(i));
            }else{
                maisAntigo.add(calculos.get(i));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        calculos = folhasRepositorio.consultaHist();
        maisRecentes.clear();
        mesPresente.clear();
        mesPassado.clear();
        maisAntigo.clear();
        histSections(calculos);
        folhas2Adapter = new Folhas2Adapter(addHistoricos());
        listDados.setAdapter(folhas2Adapter);
    }

    public void criarConexao(){
        try{
            DadosOpenHelper dadosOpenHelper = new DadosOpenHelper(this);
            conexao = dadosOpenHelper.getWritableDatabase();
            folhasRepositorio = new FolhasRepositorio(conexao);
            //Toast.makeText(getApplicationContext(), "Conexão criada com sucesso", Toast.LENGTH_SHORT).show();
        }catch (SQLException ex){
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.act_dados, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_dados) {
            /*final AlertDialog.Builder builderDialog = new AlertDialog.Builder(this);
            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_dados_info, null);
            builderDialog.setView(customLayout);
            final AlertDialog dialog = builderDialog.create();
            dialog.show();*/

            final Dialog dialog = new Dialog(this);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_dados_info); // seu layout
            dialog.setCancelable(false);

            Button fechar = dialog.findViewById(R.id.fecharBtn);
            fechar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss(); // fecha o dialog
                    } catch (SQLException ignored) {

                    }
                }
            });
            dialog.show();
        }else if(id == R.id.action_home){
            Intent it3 = new Intent(this, ActMain.class);
            it3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            it3.putExtra("EXIT", true);
            startActivity(it3);
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onBackPressed() {
        Intent it2 = new Intent(this, ActCamera.class);
        startActivity(it2);
    }*/
}
