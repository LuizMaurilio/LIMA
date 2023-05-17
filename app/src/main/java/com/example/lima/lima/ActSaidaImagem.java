package com.example.lima.lima;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lima.lima.database.DadosOpenHelper;
import com.example.lima.lima.helper.BitmapHelper;
import com.example.lima.lima.repositorio.FolhasRepositorio;

import java.util.List;

public class ActSaidaImagem extends AppCompatActivity implements View.OnClickListener {

    ImageView imageViewFoto;
    private Bitmap foto;
    private int codigo;
    private int voltar;
    private FolhasRepositorio folhasRepositorio;
    //private SQLiteDatabase conexao;
    private Folha folha;
    public static List<Folha> dados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        setContentView(R.layout.act_saida_imagem);
        imageViewFoto = findViewById(R.id.imageViewFoto);
//        Bundle extras = new Bundle();
//        extras = getIntent().getExtras();
        //assert extras != null;
//        codigo = extras.getInt("CODIGO");
        foto = BitmapHelper.getInstance().getBitmap();
        Bitmap rotated = Bitmap.createBitmap(foto, 0, 0, foto.getWidth(), foto.getHeight(), matrix, true);
        imageViewFoto.setImageBitmap(rotated);
        voltar = 0;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        if(ActCameraCv.bitmap != null){
            foto = ActCameraCv.bitmap;
        }else if(ActMain.bitmap != null){
            foto = ActMain.bitmap;
        }/*else{
            foto = ActDados.bitmap;
        }*/


    }

    @Override
    protected void onDestroy() {
        //imageViewFoto.setImageDrawable(null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(voltar == 0){
            Toast.makeText(getApplicationContext(), "Click again to return to the home screen. You will not be able to return to this screen later.", Toast.LENGTH_LONG).show();
            voltar = 1;
        }else{
            Intent it2 = new Intent(this, ActMain.class);
            it2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(it2);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//                try {
//                        //Salva a imagem
//                        assert uri != null;
//                        getContentResolver().openOutputStream(uri);
//                        /* boolean compressed = ActCameraCv.bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream); */
//
//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        foto.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                        stream.close();
//                } catch (FileNotFoundException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                } catch (IOException e) {
//                        e.printStackTrace();
//                }
//                break;
            case R.id.confirmar:

                criarConexao();
                ActCalculos calc = getIntent().getExtras().getParcelable("calc");
//                Log.d("teste ", "outros" + calc.getIdImg());
//                Log.d("teste ", "id IMG" + calc.getNome());
//                Log.d("teste ", "tratamnto" + calc.getTratamento());
//                Log.d("teste ", "largcomp" + calc.getLargComp_Desvio());
//                Log.d("teste ", "repeticao " + calc.getRepeticao());
//                Log.d("teste ", "largcompmedia " + calc.getLargComp_Media());
//                Log.d("teste ", "compmedia " + calc.getComp_Medio());
//                Log.d("TESTE FOLHA ", "folha" + calc.getListaFolhas().get(0).getComprimento());
//                Log.d("TESTE FOLHA ", "folha" + calc.getListaFolhas().get(0).getLargura());
//                Log.d("TESTE FOLHA ", "folha" + calc.getListaFolhas().get(0).getIdImg());
//                Log.d("TESTE FOLHA ", "folha" + calc.getListaFolhas().get(0).getArea());
                folhasRepositorio.inserir(calc);

                Intent it = new Intent(this, ActConfigDados.class);
                it.putExtra("CODIGO", calc.getIdImg());
                startActivityForResult(it, 0);
                //Intent it1 = new Intent(this, ActDados.class);
                //startActivity(it1);
                finish();
                break;

            case R.id.cancelar:
                //voltar para a tela anterior
                Intent it2 = new Intent(this, ActMain.class);
                startActivity(it2);
                break;
        }

        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.act_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dados:
                Intent it1 = new Intent(this, ActDados.class);
                startActivity(it1);
                return true;
            case R.id.foto:
                Intent it2 = new Intent(this, ActCameraCv.class);
                startActivity(it2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void criarConexao() {
        try {
            DadosOpenHelper dadosOpenHelper = new DadosOpenHelper(this);
            SQLiteDatabase conexao = dadosOpenHelper.getWritableDatabase();
            folhasRepositorio = new FolhasRepositorio(conexao);
            //Toast.makeText(getApplicationContext(), "Conexão criada com sucesso!", Toast.LENGTH_SHORT).show();
        } catch (SQLException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
