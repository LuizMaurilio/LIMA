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
    private static String idAtual;
    public FolhasRepositorio(SQLiteDatabase conexao){
        this.conexao = conexao;
    }

    public void inserir(ActCalculos calc){
        ContentValues contentValues = new ContentValues();
        idAtual = calc.getIdImg();
        for(int i = 0; i < calc.getListaFolhas().size(); i++ ){
            contentValues.put("COMPRIMENTO",calc.getListaFolhas().get(i).getComprimento());
            contentValues.put("AREA",calc.getListaFolhas().get(i).getArea());
            contentValues.put("LARGURA",calc.getListaFolhas().get(i).getLargura());
            contentValues.put("PERIMETRO",calc.getListaFolhas().get(i).getPerimetro());
            contentValues.put("id_Imagem", calc.getIdImg());
            contentValues.put("largcomp", calc.getListaFolhas().get(i).getLargcomp());
            contentValues.put("num_Folha", calc.getListaFolhas().get(i).getNum_Folha());
            conexao.insertOrThrow("FOLHA",null,contentValues);
        }
        ContentValues content = new ContentValues();
        content.put("id_Imagem", calc.getIdImg());
        content.put("nome", calc.getNome());
        content.put("especie", calc.getEspecie());
        content.put("tratamento", calc.getTratamento());
        content.put("repeticao", " ");
        content.put("area_Quad", calc.getArea_Quad());
        content.put("larg_Media", calc.getLarg_Media());
        content.put("larg_Desvio", calc.getLarg_Desvio());
        content.put("comp_Media", calc.getComp_Medio());
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

    public void alterar(String idImg, String nome){
        ContentValues contentValues = new ContentValues();
        contentValues.put("nome",nome);
        String[] parametros  = new String[1];
        parametros[0] = idImg;
        conexao.update("IMAGEM",contentValues,"id_Imagem = ? ", parametros);
    }

    public ActCalculos consultar(){ // FAZER UMA FUNÇÃO QUE SELECIONA APENAS AS FOLHAS COM O MESMO ID
        ActCalculos calc = new ActCalculos();
        List<Folha> folhas = new ArrayList<>();
        String sql = "SELECT CODIGO,id_Imagem,num_Folha,AREA,COMPRIMENTO,LARGURA,largcomp,PERIMETRO " +  "FROM FOLHA" + " WHERE id_Imagem = '" + idAtual+ "' ;";
        String sql2 = "SELECT id_Imagem, especie, area_Quad, larg_Media, larg_Desvio, comp_Desvio, comp_Media, per_Desvio, per_Media, area_Media, area_Desvio, sumareas, tratamento, repeticao, nome " + "FROM IMAGEM" + " WHERE id_Imagem = '" + idAtual + "';";
        @SuppressLint("Recycle") Cursor resultado = conexao.rawQuery(sql,null);
        @SuppressLint("Recycle") Cursor resultado2 = conexao.rawQuery(sql2, null);
        if (resultado.getCount() > 0){
            Log.d("RESULTADO1 >0", " OK");
            resultado.moveToFirst();
            do{
                Folha folha = new Folha();
                folha.setCod(resultado.getInt(resultado.getColumnIndexOrThrow("CODIGO")));
                folha.setIdImg(resultado.getString(resultado.getColumnIndexOrThrow("id_Imagem")));
                folha.setArea(resultado.getDouble(resultado.getColumnIndexOrThrow("AREA")));
                folha.setComprimento(resultado.getDouble(resultado.getColumnIndexOrThrow("COMPRIMENTO")));
                folha.setLargura(resultado.getDouble(resultado.getColumnIndexOrThrow("LARGURA")));
                folha.setNum_Folha(resultado.getInt(resultado.getColumnIndexOrThrow("num_Folha")));
                folha.setPerimetro(resultado.getDouble(resultado.getColumnIndexOrThrow("PERIMETRO")));
                folha.setLargcomp(resultado.getDouble(resultado.getColumnIndexOrThrow("largcomp")));
                folhas.add(folha);
                Log.d("folha adicionada", " OK");
            }while(resultado.moveToNext());
        }
        if (resultado2.getCount()>0) {
            resultado2.moveToFirst();
            calc.setListaFolhas(folhas);
            Log.d("LISTA ADICIONADA", " OK");
            calc.setIdImg(resultado2.getString(resultado2.getColumnIndexOrThrow("id_Imagem")));
            calc.setEspecie(resultado2.getString(resultado2.getColumnIndexOrThrow("especie")));
            calc.setArea_Quad(resultado2.getFloat(resultado2.getColumnIndexOrThrow("area_Quad")));
            calc.setLarg_Media(resultado2.getDouble(resultado2.getColumnIndexOrThrow("larg_Media")));
            calc.setLarg_Desvio(resultado2.getDouble(resultado2.getColumnIndexOrThrow("larg_Desvio")));
            calc.setComp_Medio(resultado2.getDouble(resultado2.getColumnIndexOrThrow("comp_Media")));
            calc.setComp_Medio(resultado2.getDouble(resultado2.getColumnIndexOrThrow("comp_Desvio")));
            calc.setPer_Media(resultado2.getDouble(resultado2.getColumnIndexOrThrow("per_Media")));
            calc.setPer_Desvio(resultado2.getDouble(resultado2.getColumnIndexOrThrow("per_Desvio")));
            calc.setArea_Media(resultado2.getDouble(resultado2.getColumnIndexOrThrow("area_Media")));
            calc.setArea_Desvio(resultado2.getDouble(resultado2.getColumnIndexOrThrow("area_Desvio")));
            calc.setSumareas(resultado2.getDouble(resultado2.getColumnIndexOrThrow("sumareas")));
            calc.setTratamento(resultado2.getString(resultado2.getColumnIndexOrThrow("tratamento")));
            calc.setRepeticao(resultado2.getString(resultado2.getColumnIndexOrThrow("repeticao")));
            calc.setNome(resultado2.getString(resultado2.getColumnIndexOrThrow("nome")));
        }
        return calc;
    }
}