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
        content.put("repeticao", calc.getRepeticao());
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
        content.put("largcomp_Desvio", calc.getLargComp_Desvio());
        content.put("largcomp_Media", calc.getLargComp_Media());

        conexao.insertOrThrow("IMAGEM", null, content);
    }

    public void excluir(String idImg){
        String[] parametros  = new String[1];
        parametros[0] = idImg;
        conexao.delete("FOLHA","id_Imagem = ? ",parametros);
        conexao.delete("IMAGEM", "id_Imagem = ?", parametros);
    }

    public void alterar(String idImg, String nome){
        ContentValues contentValues = new ContentValues();
        contentValues.put("nome",nome);
        String[] parametros  = new String[1];
        parametros[0] = idImg;
        conexao.update("IMAGEM",contentValues,"id_Imagem = ? ", parametros);
    }
    //TODO revisar como isso funciona com o parcelable
    public ActCalculos consultar(String cod){
        if(cod != null) idAtual = cod;
        ActCalculos calc = new ActCalculos();
        ArrayList<Folha> folhas = new ArrayList<Folha>();
        String sql = "SELECT CODIGO,id_Imagem,num_Folha,AREA,COMPRIMENTO,LARGURA,largcomp,PERIMETRO " +  "FROM FOLHA" + " WHERE id_Imagem = '" + idAtual+ "' ;";
        String sql2 = "SELECT id_Imagem, especie, area_Quad, larg_Media, larg_Desvio, comp_Desvio, comp_Media, per_Desvio, per_Media, area_Media, area_Desvio, largcomp_Desvio, largcomp_Media, sumareas, tratamento, repeticao, nome " + "FROM IMAGEM" + " WHERE id_Imagem = '" + idAtual + "';";
        @SuppressLint("Recycle") Cursor resultado = conexao.rawQuery(sql, null);
        @SuppressLint("Recycle") Cursor resultado2 = conexao.rawQuery(sql2, null);
        if (resultado.getCount() > 0){
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
            }while(resultado.moveToNext());
            calc.setListaFolhas(folhas);
        }
        if (resultado2.getCount()>0) {
            resultado2.moveToFirst();
            calc.setIdImg(resultado2.getString(resultado2.getColumnIndexOrThrow("id_Imagem")));
            calc.setEspecie(resultado2.getString(resultado2.getColumnIndexOrThrow("especie")));
            calc.setArea_Quad(resultado2.getFloat(resultado2.getColumnIndexOrThrow("area_Quad")));
            calc.setLarg_Media(resultado2.getDouble(resultado2.getColumnIndexOrThrow("larg_Media")));
            calc.setLarg_Desvio(resultado2.getDouble(resultado2.getColumnIndexOrThrow("larg_Desvio")));
            calc.setComp_Medio(resultado2.getDouble(resultado2.getColumnIndexOrThrow("comp_Media")));
            calc.setComp_Desvio(resultado2.getDouble(resultado2.getColumnIndexOrThrow("comp_Desvio")));
            calc.setPer_Media(resultado2.getDouble(resultado2.getColumnIndexOrThrow("per_Media")));
            calc.setPer_Desvio(resultado2.getDouble(resultado2.getColumnIndexOrThrow("per_Desvio")));
            calc.setArea_Media(resultado2.getDouble(resultado2.getColumnIndexOrThrow("area_Media")));
            calc.setArea_Desvio(resultado2.getDouble(resultado2.getColumnIndexOrThrow("area_Desvio")));
            calc.setLargComp_Media(resultado2.getDouble(resultado2.getColumnIndexOrThrow("largcomp_Media")));
            calc.setLargComp_Desvio(resultado2.getDouble(resultado2.getColumnIndexOrThrow("largcomp_Desvio")));
            calc.setSumareas(resultado2.getDouble(resultado2.getColumnIndexOrThrow("sumareas")));
            calc.setTratamento(resultado2.getString(resultado2.getColumnIndexOrThrow("tratamento")));
            calc.setRepeticao(resultado2.getInt(resultado2.getColumnIndexOrThrow("repeticao")));
            calc.setNome(resultado2.getString(resultado2.getColumnIndexOrThrow("nome")));
        }
        return calc;
    }

    public List<Folha> consultaHist(){
        List<Folha> calculos = new ArrayList<>();
        @SuppressLint("Recycle") Cursor resultado;
        String sql = "SELECT id_Imagem, larg_Media, comp_Media, per_Media, area_Media, nome " +  "FROM IMAGEM;";
        resultado = conexao.rawQuery(sql,null);
        if (resultado.getCount()>0) {
            resultado.moveToFirst();
            do{
                Folha calc = new Folha();
                calc.setIdImg(resultado.getString(resultado.getColumnIndexOrThrow("id_Imagem")));
                calc.setLargura(resultado.getDouble(resultado.getColumnIndexOrThrow("larg_Media")));
                calc.setComprimento(resultado.getDouble(resultado.getColumnIndexOrThrow("comp_Media")));
                calc.setPerimetro(resultado.getDouble(resultado.getColumnIndexOrThrow("per_Media")));
                calc.setArea(resultado.getDouble(resultado.getColumnIndexOrThrow("area_Media")));
                calc.setNomeImagem(resultado.getString(resultado.getColumnIndexOrThrow("nome")));
                calculos.add(calc);
            } while(resultado.moveToNext());
        }
        return calculos;
    }
}