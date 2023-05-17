package com.example.lima.lima;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashScreenActivityInicial extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash_screen_inicial);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override public void run() {
                mostrarTelaInicial();
            }
        }, 2000);
    }

    private void mostrarTelaInicial() {
        Intent intent = new Intent(this, ActMain.class);
        startActivity(intent);
        finish();
    }
}
