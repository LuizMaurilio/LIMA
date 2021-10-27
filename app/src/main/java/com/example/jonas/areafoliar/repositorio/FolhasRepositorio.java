package com.example.jonas.areafoliar.repositorio;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.jonas.areafoliar.Folha;

import java.util.ArrayList;
import java.util.List;


public class FolhasRepositorio {
    private SQLiteDatabase conexao;

    public FolhasRepositorio(SQLiteDatabase conexao){
        this.conexao = conexao;
    }

    public void inserir(Folha folha){
        ContentValues contentValues = new ContentValues();
        contentValues.put("NOME",folha.getNome());
        contentValues.put("AREA",folha.getArea());
        contentValues.put("ALTURA",folha.getAltura());
        contentValues.put("LARGURA",folha.getLargura());
        contentValues.put("DATA",folha.getData());
        contentValues.put("TIPO",folha.getTipo());
        contentValues.put("PERIMETRO",folha.getPerimetro());
        conexao.insertOrThrow("FOLHA",null,contentValues);
    }

    public void inserirImagem(){

    }

    public void excluir(int codigo){
        String[] parametros  = new String[1];
        parametros[0] = String.valueOf(codigo);
        conexao.delete("FOLHA","CODIGO = ? ",parametros);
    }

    public void alterar(int codigo, String nome){
        ContentValues contentValues = new ContentValues();
        contentValues.put("NOME",nome);
        String[] parametros  = new String[1];
        parametros[0] = String.valueOf(codigo);
        conexao.update("FOLHA",contentValues,"CODIGO = ? ",parametros);
    }

    public List<Folha> consultar(){
        List<Folha> folhas = new ArrayList<>();
        String sql = "SELECT CODIGO,NOME,AREA,ALTURA,LARGURA,DATA,TIPO,PERIMETRO " +
                "FROM FOLHA";
        @SuppressLint("Recycle") Cursor resultado = conexao.rawQuery(sql,null);
        if (resultado.getCount() > 0){
            resultado.moveToFirst();
            do{
                Folha folha = new Folha();
                folha.setCodigo(resultado.getInt(resultado.getColumnIndexOrThrow("CODIGO")));
                folha.setNome(resultado.getString(resultado.getColumnIndexOrThrow("NOME")));
                folha.setArea(resultado.getString(resultado.getColumnIndexOrThrow("AREA")));
                folha.setAltura(resultado.getString(resultado.getColumnIndexOrThrow("ALTURA")));
                folha.setLargura(resultado.getString(resultado.getColumnIndexOrThrow("LARGURA")));
                folha.setData(resultado.getString(resultado.getColumnIndexOrThrow("DATA")));
                folha.setTipo(resultado.getInt(resultado.getColumnIndexOrThrow("TIPO")));
                folha.setPerimetro(resultado.getString(resultado.getColumnIndexOrThrow("PERIMETRO")));
                folhas.add(folha);
            }while(resultado.moveToNext());
        }
        return folhas;
    }

    public void consultarImagens(){

    }
}