package com.example.jonas.areafoliar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


public class ActConfigGeral extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    //private CheckBox checkbox3,checkbox4,checkbox5;
    private EditText editTextNumberDados;
    private Button buttonDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_config_geral);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("valorLadoPref", Context.MODE_PRIVATE);
        int valor = sharedPreferences.getInt("area", 5);
        editTextNumberDados = (EditText)findViewById(R.id.editTextNumberDados);
        buttonDados = (Button)findViewById(R.id.buttonDados);
        buttonDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validaCampos();
            }
        });

        /*checkbox3 = findViewById(R.id.checkbox3);
        checkbox4 = findViewById(R.id.checkbox4);
        checkbox5 = findViewById(R.id.checkbox5);
        if(valor == 3){
            checkbox3.setChecked(true);
            checkbox4.setChecked(false);
            checkbox5.setChecked(false);
        }else if(valor == 4){
            checkbox3.setChecked(false);
            checkbox4.setChecked(true);
            checkbox5.setChecked(false);
        }else{
            checkbox3.setChecked(false);
            checkbox4.setChecked(false);
            checkbox5.setChecked(true);
        }*/
    }

    /*public void validaCheckBox(int check){
        sharedPreferences = getSharedPreferences("valorLadoPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        checkbox3 = findViewById(R.id.checkbox3);
        checkbox4 = findViewById(R.id.checkbox4);
        checkbox5 = findViewById(R.id.checkbox5);
        if(check == 3){
            checkbox3.setChecked(true);
            checkbox4.setChecked(false);
            checkbox5.setChecked(false);
            editor.putInt("lado",3);
            //editor.commit();
            editor.apply();
        }else if(check == 4){
            checkbox3.setChecked(false);
            checkbox4.setChecked(true);
            checkbox5.setChecked(false);
            editor.putInt("lado",4);
            //editor.commit();
            editor.apply();
        }else{
            checkbox3.setChecked(false);
            checkbox4.setChecked(false);
            checkbox5.setChecked(true);
            editor.putInt("lado",5);
            //editor.commit();
            editor.apply();
        }

        /*sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        String result = sharedPreferences.getString(getString(R.string.pref_text), "");

        if(result.equals("3")){
            checkbox3.setChecked(true);
        }else if(result.equals("4")){
            checkbox4.setChecked(true);
        }else{
            checkbox5.setChecked(true);
            editor = sharedPreferences.edit();
            editor.putString(getString(R.string.pref_text), "5");
            editor.apply();
        }*/
    //}

    public void validaCampos(){
        sharedPreferences = getSharedPreferences("valorLadoPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String valor = editTextNumberDados.getText().toString();
        if (isCampoVazio(valor)) editTextNumberDados.requestFocus();
        else {
            int area = Integer.parseInt(String.valueOf(editTextNumberDados.getText()));
            editor.putInt("area", area);
            editor.apply();
            finish();
        }
    }

    private boolean isCampoVazio(String a){
        boolean resultado = (TextUtils.isEmpty(a) || a.trim().isEmpty());
        return resultado;
    }


    /*public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.checkbox3:
                if (checked) {
                    validaCheckBox(3);
                }
                break;
            case R.id.checkbox4:
                if (checked) {
                    validaCheckBox(4);
                }
                break;
            case R.id.checkbox5:
                if (checked) {
                    validaCheckBox(5);
                }
                break;
        }
        /*sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.checkbox3:
                if (checked) {
                    editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.pref_text), "3");
                    editor.apply();
                }
                break;
            case R.id.checkbox4:
                if (checked) {
                    editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.pref_text), "4");
                    editor.apply();
                }
                break;
            case R.id.checkbox5:
                if (checked) {
                    editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.pref_text), "3");
                    editor.apply();
                }
                break;
        }*/
    //}

}
