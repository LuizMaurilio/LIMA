package com.example.jonas.areafoliar.repositorio;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jonas.areafoliar.ActCalculos;
import com.example.jonas.areafoliar.Folha;

import java.util.ArrayList;
import java.util.List;


public class FolhasRepositorio {
    private SQLiteDatabase conexao;
    private String idAtual;
    public FolhasRepositorio(SQLiteDatabase conexao){
        this.conexao = conexao;
    }

    public void inserir(ActCalculos calc){
        ContentValues contentValues = new ContentValues();
        idAtual = calc.getNome();
        for(int i = 0; i < calc.getListaFolhas().size(); i++ ){
            contentValues.put("ALTURA",calc.getListaFolhas().get(i).getAltura());
            contentValues.put("AREA",calc.getListaFolhas().get(i).getArea());
            contentValues.put("LARGURA",calc.getListaFolhas().get(i).getLargura());
            contentValues.put("PERIMETRO",calc.getListaFolhas().get(i).getPerimetro());
            contentValues.put("id_Imagem", calc.getNome());
            contentValues.put("largcomp", calc.getListaFolhas().get(i).getLargcomp());
            contentValues.put("num_Folha", calc.getListaFolhas().get(i).getNum_Folha());
            conexao.insertOrThrow("FOLHA",null,contentValues);
        }
        ContentValues content = new ContentValues();
        content.put("id_Imagem", calc.getNome());
        content.put("nome", "placeholder");
        content.put("especie", calc.getEspecie());
        content.put("tratamento", calc.getTratamento());
        content.put("repeticao", " ");
        content.put("area_Quad", calc.getArea_Quad());
        content.put("larg_Media", calc.getLarg_Media());
        content.put("larg_Desvio", calc.getLarg_Desvio());
        content.put("comp_Medio", calc.getComp_Medio());
        content.put("comp_Desvio", calc.getComp_Desvio());
        content.put("area_Media", calc.getArea_Media());
        content.put("area_Desvio", calc.getArea_Desvio());
        content.put("sumareas", calc.getSumareas());
        content.put("per_Media", calc.getPer_Media());
        content.put("per_Desvio", calc.getPer_Desvio());

        conexao.insertOrThrow("IMAGEM", null, content);
    }

    public void excluir(int codigo){
        String[] parametros  = new String[1];
        parametros[0] = String.valueOf(codigo);
        conexao.delete("FOLHA","CODIGO = ? ",parametros);
    }

    public void alterar(int codigo, String nome){
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_Imagem",nome);
        String[] parametros  = new String[1];
        parametros[0] = String.valueOf(codigo);
        conexao.update("FOLHA",contentValues,"CODIGO = ? ",parametros);
    }

    public ActCalculos consultar(){ // FAZER UMA FUNÇÃO QUE SELECIONA APENAS AS FOLHAS COM O MESMO ID
        ActCalculos calc = new ActCalculos();
        List<Folha> folhas = calc.getListaFolhas();
        String sql = "SELECT CODIGO,id_Imagem,num_Folha,AREA,ALTURA,LARGURA,largcomp,PERIMETRO " +  "FROM FOLHA" + " WHERE id_Imagem ='" + idAtual+ "' ;";
        String sql2 = "SELECT id_Imagem, especie, area_Quad, larg_Media " + "FROM IMAGEM;";
        @SuppressLint("Recycle") Cursor resultado = conexao.rawQuery(sql,null);
        @SuppressLint("Recycle") Cursor resultado2 = conexao.rawQuery(sql2, null);
        if (resultado.getCount() > 0){
            resultado.moveToFirst();
            do{
                Folha folha = new Folha();
                folha.setCodigo(resultado.getInt(resultado.getColumnIndexOrThrow("CODIGO")));
                folha.setNome(resultado.getString(resultado.getColumnIndexOrThrow("id_Imagem")));
                folha.setArea(resultado.getString(resultado.getColumnIndexOrThrow("AREA")));
                folha.setAltura(resultado.getString(resultado.getColumnIndexOrThrow("ALTURA")));
                folha.setLargura(resultado.getString(resultado.getColumnIndexOrThrow("LARGURA")));
                folha.setNum_Folha(resultado.getInt(resultado.getColumnIndexOrThrow("num_Folha")));
                folha.setPerimetro(resultado.getString(resultado.getColumnIndexOrThrow("PERIMETRO")));
                folha.setLargcomp(resultado.getString(resultado.getColumnIndexOrThrow("largcomp")));
                folhas.add(folha);
            }while(resultado.moveToNext());
        }
        resultado2.moveToFirst();
        calc.setListaFolhas(folhas);
        calc.setNome(resultado2.getString(resultado2.getColumnIndexOrThrow("id_Imagem")));
        calc.setEspecie(resultado2.getString(resultado2.getColumnIndexOrThrow("especie")));
        calc.setArea_Quad(resultado2.getFloat(resultado2.getColumnIndexOrThrow("area_Quad")));
        calc.setLarg_Media(resultado2.getDouble(resultado2.getColumnIndexOrThrow("larg_Media")));
        return calc;
    }

//    public ActCalculos getValues(){
//        ActCalculos calc = new ActCalculos();
//
//        calc.setNome(data_completa + " - Nome do teste");
//        calc.setSumareas(mA);
//        calc.setComp_Medio(mC);
//        calc.setLarg_Media(mL);
//        calc.setPer_Media(mP);
//        calc.setArea_Quad(areaQuadrado);
//        return calc;
//    }
}