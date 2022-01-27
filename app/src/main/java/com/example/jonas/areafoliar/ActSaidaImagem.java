package com.example.jonas.areafoliar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jonas.areafoliar.database.DadosOpenHelper;
import com.example.jonas.areafoliar.helper.BitmapHelper;
import com.example.jonas.areafoliar.repositorio.FolhasRepositorio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
    private ActCalculos calc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        setContentView(R.layout.act_saida_imagem);
        imageViewFoto = findViewById(R.id.imageViewFoto);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        codigo = bundle.getInt("CODIGO");
        foto = BitmapHelper.getInstance().getBitmap();
        Bitmap rotated = Bitmap.createBitmap(foto, 0, 0, foto.getWidth(), foto.getHeight(), matrix, true);
        imageViewFoto.setImageBitmap(rotated);
        voltar = 0;
        criarConexao();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        calc = folhasRepositorio.consultar(false, null);
        dados = calc.getListaFolhas();
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
            case R.id.download:

                ContentValues contentValues = new ContentValues();
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

                StringBuilder data = new StringBuilder();
                data.append("Image:, " + calc.getNome()+"\nSpecies:, " + calc.getEspecie()+ "\nTreatment:, " + calc.getTratamento()+"\nReplicate:, "+calc.getRepeticao()+"\nScale Pattern:, "+calc.getArea_Quad()+"\nNumber of Leaves:, "+calc.getListaFolhas().size());

                data.append("\nNumber of Leaf:, ");
                if(sharedPreferences.getBoolean("calcWidth", false)) data.append("Width,");
                if(sharedPreferences.getBoolean("calcLength", false)) data.append("Length,");
                if(sharedPreferences.getBoolean("calcWidthDLength", false)) data.append("Width/Length,");
                if(sharedPreferences.getBoolean("calcArea", false)) data.append("Area, ");
                if(sharedPreferences.getBoolean("calcPerimeter", false)) data.append("Perimeter, ");

                for(int i = 0; i < calc.getListaFolhas().size(); i++){
                    data.append("\n"+calc.getListaFolhas().get(i).getNum_Folha()+",");
                    if(sharedPreferences.getBoolean("calcWidth", false)) data.append(calc.getListaFolhas().get(i).getLargura()+",");
                    if(sharedPreferences.getBoolean("calcLength", false)) data.append(calc.getListaFolhas().get(i).getComprimento()+",");
                    if(sharedPreferences.getBoolean("calcWidthDLength", false)) data.append(calc.getListaFolhas().get(i).getLargcomp()+",");
                    if(sharedPreferences.getBoolean("calcArea", false)) data.append(calc.getListaFolhas().get(i).getArea()+",");
                    if(sharedPreferences.getBoolean("calcPerimeter", false)) data.append(calc.getListaFolhas().get(i).getPerimetro()+",");
                }

                if(sharedPreferences.getBoolean("calcAvgDev", false)) {
                    data.append("\nAverages:,");
                        if (sharedPreferences.getBoolean("calcWidth", false))
                            data.append(calc.getLarg_Media() + ",");
                        if (sharedPreferences.getBoolean("calcLength", false))
                            data.append(calc.getComp_Medio() + ",");
                        if(sharedPreferences.getBoolean("calcWidthDLength", false))
                            data.append(calc.getLargComp_Media()+ ",");
                        if (sharedPreferences.getBoolean("calcArea", false))
                            data.append(calc.getArea_Media() + ",");
                        if (sharedPreferences.getBoolean("calcPerimeter", false))
                            data.append(calc.getPer_Media() + ",");

                    data.append("\nDeviation:,");
                    if (sharedPreferences.getBoolean("calcWidth", false))
                        data.append(calc.getLarg_Desvio() + ",");
                    if (sharedPreferences.getBoolean("calcLength", false))
                        data.append(calc.getComp_Desvio() + ",");
                    if(sharedPreferences.getBoolean("calcWidthDLength", false))
                        data.append(calc.getLargComp_Desvio()+ ",");
                    if (sharedPreferences.getBoolean("calcArea", false))
                        data.append(calc.getArea_Desvio() + ",");
                    if (sharedPreferences.getBoolean("calcPerimeter", false))
                        data.append(calc.getPer_Desvio() + ",");
                }

                if(sharedPreferences.getBoolean("calcSomaArea", false)) {
                    data.append("\nSum Areas:, ");
                    data.append( calc.getSumareas()+", ");
                }

                Bundle bundle = getIntent().getExtras();
                assert bundle != null;
                int cod = bundle.getInt("CODIGO");

                for (int j = 0; j < dados.size(); j++) {
                    if (dados.get(j).getCod() == cod) {
                        folha = dados.get(j);
                    }
                }

                try {
                    //saving the file into device
                    FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);
                    out.write((data.toString()).getBytes());
                    out.close();
                    //TODO NO OUTLOOK FUNCIONA
                    //exporting
                    Context context = getApplicationContext();
                    File filelocation = new File(getFilesDir(), "data.csv");
                    Uri path = FileProvider.getUriForFile(context, "com.example.jonas.fileprovider", filelocation);
                    Intent fileIntent = new Intent(Intent.ACTION_SEND);
                    fileIntent.setType("text/csv");
                    fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
                    fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                    startActivity(Intent.createChooser(fileIntent, "Send mail"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                        //Salva a imagem
                        assert uri != null;
                        getContentResolver().openOutputStream(uri);
                        /* boolean compressed = ActCameraCv.bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream); */

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        foto.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        stream.close();
                } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                break;

                case R.id.dados_camera:
                        Intent it = new Intent(this, ActConfigDados.class);
                        it.putExtra("CODIGO", codigo);
                        startActivityForResult(it, 0);
                        //Intent it1 = new Intent(this, ActDados.class);
                        //startActivity(it1);
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
