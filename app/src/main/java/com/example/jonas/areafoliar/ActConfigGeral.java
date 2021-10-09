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
    private CheckBox c1,c2,c3,c4,c5,c6,c7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_config_geral);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("valorLadoPref", Context.MODE_PRIVATE);
        int valor = sharedPreferences.getInt("area", 1);
        editTextNumberDados = (EditText)findViewById(R.id.editTextNumberDados);
        editTextNumberDados.setText(String.valueOf(valor));

        c1 = findViewById(R.id.calcArea);
        c2 = findViewById(R.id.calcSomaArea);
        c3 = findViewById(R.id.calcWidth);
        c4 = findViewById(R.id.calcLength);
        c5 = findViewById(R.id.calcWidthDLength);
        c6 = findViewById(R.id.calcPerimeter);
        c7 = findViewById(R.id.calcAvgDev);

        Boolean prec1 = sharedPreferences.getBoolean("calcArea", false);
        if(!prec1) c1.setChecked(false);
        else c1.setChecked(true);
        Boolean prec2 = sharedPreferences.getBoolean("calcSomaArea", false);
        if(!prec2) c2.setChecked(false);
        else c2.setChecked(true);
        Boolean prec3 = sharedPreferences.getBoolean("calcWidth", false);
        if(!prec3) c3.setChecked(false);
        else c3.setChecked(true);
        Boolean prec4 = sharedPreferences.getBoolean("calcLength", false);
        if(!prec4) c4.setChecked(false);
        else c4.setChecked(true);
        Boolean prec5 = sharedPreferences.getBoolean("calcWidthDLength", false);
        if(!prec5) c5.setChecked(false);
        else c5.setChecked(true);
        Boolean prec6 = sharedPreferences.getBoolean("calcPerimeter", false);
        if(!prec6) c6.setChecked(false);
        else c6.setChecked(true);
        Boolean prec7 = sharedPreferences.getBoolean("calcAvgDev", false);
        if(!prec7) c7.setChecked(false);
        else c7.setChecked(true);

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
            if (c1.isChecked()) editor.putBoolean("calcArea", true);
            else editor.putBoolean("calcArea", false);
            if (c2.isChecked()) editor.putBoolean("calcSomaArea", true);
            else editor.putBoolean("calcSomaArea", false);
            if (c3.isChecked()) editor.putBoolean("calcWidth", true);
            else editor.putBoolean("calcWidth", false);
            if (c4.isChecked()) editor.putBoolean("calcLength", true);
            else editor.putBoolean("calcLength", false);
            if (c5.isChecked()) editor.putBoolean("calcWidthDLength", true);
            else editor.putBoolean("calcWidthDLength", false);
            if (c6.isChecked()) editor.putBoolean("calcPerimeter", true);
            else editor.putBoolean("calcPerimeter", false);
            if (c7.isChecked()) editor.putBoolean("calcAvgDev", true);
            else editor.putBoolean("calcAvgDev", false);
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
