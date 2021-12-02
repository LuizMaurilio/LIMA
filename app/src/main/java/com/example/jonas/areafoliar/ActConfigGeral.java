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
import android.widget.RadioGroup;
import android.widget.Toast;


public class ActConfigGeral extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    //private CheckBox checkbox3,checkbox4,checkbox5;
    private EditText editTextNumberDados, editTextSpecies, editTextTreatment;
    private Button buttonDados;
    private CheckBox c1,c2,c3,c4,c5,c6,c7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_config_geral);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        int valor = sharedPreferences.getInt("area", 1);
        editTextNumberDados = (EditText)findViewById(R.id.editTextNumberDados);
        editTextNumberDados.setText(String.valueOf(valor));
        editTextTreatment = (EditText)findViewById(R.id.editTextTreatment);
        editTextTreatment.setText(sharedPreferences.getString("treatment", null));
        editTextSpecies = (EditText)findViewById(R.id.editTextSpecies);
        editTextSpecies.setText(sharedPreferences.getString("species", null));

        c1 = findViewById(R.id.calcArea);
        c2 = findViewById(R.id.calcSomaArea);
        c3 = findViewById(R.id.calcWidth);
        c4 = findViewById(R.id.calcLength);
        c5 = findViewById(R.id.calcWidthDLength);
        c6 = findViewById(R.id.calcPerimeter);
        c7 = findViewById(R.id.calcAvgDev);

        Boolean prec1 = sharedPreferences.getBoolean("calcArea", false);
        if(!prec1) {
            c1.setChecked(false);
            c2.setEnabled(false);
        }
        else {
            c1.setChecked(true);
            c2.setEnabled(true);
        }

        Boolean prec2 = sharedPreferences.getBoolean("calcSomaArea", false);
        if(!prec2) c2.setChecked(false);
        else if(prec1) c2.setChecked(true);

        Boolean prec3 = sharedPreferences.getBoolean("calcWidth", false);
        if(!prec3) {
            c3.setChecked(false);
            c5.setEnabled(false);
        }
        else c3.setChecked(true);

        Boolean prec4 = sharedPreferences.getBoolean("calcLength", false);
        if(!prec4) {
            c4.setChecked(false);
            c5.setEnabled(false);
        }
        else c4.setChecked(true);
        if(prec3 && prec4) c5.setEnabled(true);

        Boolean prec5 = sharedPreferences.getBoolean("calcWidthDLength", false);
        if(!prec5) c5.setChecked(false);
        else if(prec3 && prec4) c5.setChecked(true);

        Boolean prec6 = sharedPreferences.getBoolean("calcPerimeter", false);
        if(!prec6) c6.setChecked(false);
        else c6.setChecked(true);

        Boolean prec7 = sharedPreferences.getBoolean("calcAvgDev", false);
        if(!prec7) c7.setChecked(false);
        else if(prec6) c7.setChecked(true);

        if(prec1 || prec3 || prec4 || prec6) c7.setEnabled(true);
        else c7.setEnabled(false);

        buttonDados = (Button)findViewById(R.id.buttonDados);
        buttonDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validaCampos();
            }
        });
    }

    public void validaCampos(){
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String valor = editTextNumberDados.getText().toString();
        if (isCampoVazio(valor)) editTextNumberDados.requestFocus();
        else {
            int area = Integer.parseInt(String.valueOf(editTextNumberDados.getText()));
            editor.putInt("area", area);
            editor.putString("species", String.valueOf(editTextSpecies.getText()));
            editor.putString("treatment", String.valueOf(editTextTreatment.getText()));
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
    //função para fazer a verificação de checkbox.
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        CheckBox c2 = findViewById(R.id.calcSomaArea);
        CheckBox c1 = findViewById(R.id.calcArea);
        CheckBox c3 = findViewById(R.id.calcLength);
        CheckBox c4 = findViewById(R.id.calcWidth);
        CheckBox c5 = findViewById(R.id.calcWidthDLength);
        CheckBox c6 = findViewById(R.id.calcAvgDev);
        CheckBox c7 = findViewById(R.id.calcPerimeter);
        switch (view.getId()){
            case R.id.calcArea:

            case R.id.calcPerimeter:

            case R.id.calcLength:

            case R.id.calcWidth:
                if(c1.isChecked()) {
                    c2.setEnabled(true);
                }
                else {
                    c2.setEnabled(false);
                    c2.setChecked(false);
                    Toast.makeText(getApplicationContext(), "To calculate the Sum of the Areas please select the Area Checkbox", Toast.LENGTH_SHORT).show();
                }

                if(c3.isChecked() && c4.isChecked()) {
                    c5.setEnabled(true);
                }
                else {
                    c5.setEnabled(false);
                    c5.setChecked(false);
                    Toast.makeText(getApplicationContext(), "To calculate Width/Length please select both the Width and Length CheckBoxes", Toast.LENGTH_SHORT).show();
                }

                if(c1.isChecked() || c3.isChecked() || c4.isChecked() || c7.isChecked()) c6.setEnabled(true);
                else {
                    c6.setEnabled(false);
                    c6.setChecked(false);
                }
                break;

        }
    }

}
