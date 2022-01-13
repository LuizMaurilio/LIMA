package com.example.jonas.areafoliar.database;

public class ScriptDLL {
    public static String getCreateTableFolha(){
        StringBuilder sql = new StringBuilder();
        sql.append("  CREATE TABLE IF NOT EXISTS FOLHA( ");
        sql.append("  CODIGO   INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,");
        sql.append(" id_Imagem VARCHAR (20) REFERENCES IMAGEM (id_Imagem) NOT NULL,");
        sql.append(" num_Folha INTEGER      NOT NULL,");
        sql.append("  AREA REAL (3, 4), ");
        sql.append("  COMPRIMENTO    REAL (3, 4),");
        sql.append("  LARGURA REAL (3, 4),");
        sql.append(" largcomp REAL (3, 4),");
        sql.append("  PERIMETRO REAL (3, 4));");

        return sql.toString();
    }

    public static String getCreateTableImagem(){
        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE IF NOT EXISTS IMAGEM ( ");
        sql.append(" id_Imagem   VARCHAR (20)  PRIMARY KEY DESC NOT NULL UNIQUE,"); //MUDAR PARA TIPO DATA
        sql.append("  nome        VARCHAR (300) NOT NULL, "); // EDITAR O NOME, DEFAULT NOME DA IMAGEM
        sql.append(" especie     STRING (200), ");
        sql.append(" tratamento  STRING (200),");
        sql.append(" repeticao   STRING (4),");
        sql.append(" area_Quad   REAL (2, 0),");
        sql.append(" larg_Media  REAL (3, 4),");
        sql.append(" larg_Desvio REAL (3, 4),");
        sql.append(" comp_Media  REAL (3, 4),");
        sql.append(" comp_Desvio REAL (3, 4),");
        sql.append(" area_Media  REAL (3, 4),");
        sql.append(" area_Desvio REAL (3, 4),");
        sql.append(" largcomp_Desvio REAL (3, 4),");
        sql.append(" largcomp_Media REAL (3, 4),");
        sql.append(" sumareas    REAL (3, 4),");
        sql.append(" per_Media   REAL (3, 4),");
        sql.append(" per_Desvio  REAL (3, 4));");
        return sql.toString();
    }
}
