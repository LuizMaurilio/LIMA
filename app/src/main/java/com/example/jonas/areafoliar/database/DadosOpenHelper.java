package com.example.jonas.areafoliar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DadosOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_ALTER_FOLHA_1 = "ALTER TABLE FOLHA ADD COLUMN TIPO INTEGER NOT NULL;";
    private static final String DATABASE_ALTER_FOLHA_2 = "ALTER TABLE FOLHA ADD COLUMN PERIMETRO VARCHAR (200) NOT NULL DEFAULT ('');";


    public DadosOpenHelper(Context context) {
        super(context, "Dados", null, 16);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ScriptDLL.getCreateTableImagem());
        db.execSQL(ScriptDLL.getCreateTableFolha());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            db.execSQL(DATABASE_ALTER_FOLHA_1);
            db.execSQL(DATABASE_ALTER_FOLHA_2);
        }else if(oldVersion == 2){
            db.execSQL(DATABASE_ALTER_FOLHA_2);
        }
    }
}
