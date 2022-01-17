package com.example.jonas.areafoliar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.jonas.areafoliar.database.DadosOpenHelper;
import com.example.jonas.areafoliar.helper.BitmapHelper;
import com.example.jonas.areafoliar.repositorio.FolhasRepositorio;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final int TELA_CAMERA = 1;
    //private double altQuad, largQuad;
    private Mat ImageMat;
    public Bitmap bitmap;
    //int total = 0;
    //double altura = 0, largura = 0, areaQuadradoPx = 0, areaFolhaCm = 0;
    private FolhasRepositorio folhaRepositorio;
    private String data_completa;
    static final int REQUEST_CODE = 123;

    private static CustomProgressBar progressBar = new CustomProgressBar();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    //TODO PERMISSÕES
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
//        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) +
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) +
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("TESTE PERMISSÕES", "TESTE");
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                    }, REQUEST_CODE);
        }
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
//                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
//                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
//                ActivityCompat.requestPermissions(
//                        this,
//                        new String[]{
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        }, REQUEST_CODE);
//                Log.d("TESTE PERMISSÕES", "TESTE123");
//                }
        //fix crash
        if(Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll()
                    .build();
            StrictMode.setThreadPolicy(policy);
        }
        (findViewById(R.id.camera_act_main)).setOnClickListener(this);
        (findViewById(R.id.galeria_act_main)).setOnClickListener(this);
        (findViewById(R.id.dados_salvos_act_main)).setOnClickListener(this);
        (findViewById(R.id.informacao_act_main)).setOnClickListener(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.addDrawerListener(toggle);
        //toggle.syncState();

        //NavigationView navigationView = findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
        criarConexao();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.act_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        /*int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent it = new Intent(this, ActCameraCv.class);
            startActivityForResult(it, TELA_CAMERA);
        } else if (id == R.id.nav_folhas) {
            Intent it = new Intent(this, ActDados.class);
            startActivityForResult(it, 0);
        } else if (id == R.id.nav_config) {
            Intent itConfig = new Intent(this, ActConfigGeral.class);
            startActivityForResult(itConfig, 0);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //if (requestCode == TELA_CAMERA && resultCode == 1) {
            /*Bundle params = data != null ? data.getExtras() : null;
            Bitmap imagem = (Bitmap) params.get("bitmap");
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotated = Bitmap.createBitmap(ActCameraCv.bitmap, 0, 0, ActCameraCv.bitmap.getWidth(), ActCameraCv.bitmap.getHeight(), matrix, true);
            imageViewFoto.setImageBitmap(rotated);*/
        //}
        if (requestCode == 2) {
            if (data == null) {
                //Toast.makeText(getApplicationContext(), "Escolha uma foto", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Choose a photo", Toast.LENGTH_LONG).show();
            } else {
                Uri imagemSelecionada = data.getData();
                String[] colunaArquivo = {MediaStore.Images.Media.DATA};
                assert imagemSelecionada != null;
                @SuppressLint("Recycle") Cursor c = getContentResolver().query(imagemSelecionada, colunaArquivo, null, null, null);
                assert c != null;
                c.moveToFirst();
                int columIndex = c.getColumnIndex(colunaArquivo[0]);
                String picPath = c.getString(columIndex);
                bitmap = BitmapFactory.decodeFile(picPath);
                ImageMat = new Mat();

                String nome = imagemSelecionada.getPath();
                //Cria um bitmap com a configuração ARGB_8888
                //Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                //Transforma o Bitmap em Mat
                Utils.bitmapToMat(bitmap, ImageMat);
                //Cria uma matriz com o tamanho e o tipo do Mat posterior
                Mat result = new Mat(ImageMat.size(), ImageMat.type());
                //Converte a imagem em tons de cinza
                Imgproc.cvtColor(ImageMat, result, Imgproc.COLOR_RGB2GRAY);
                //Cria as bounding boxes
                //result = createBoundingBoxes(result);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
                Date dataCalc = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(dataCalc);
                Date data_atual = cal.getTime();
                data_completa = dateFormat.format(data_atual);
//                findObjects(result);
//                surfaceCalc();

                SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                float areaQuadrado = sharedPreferences.getInt("area", 1);

                ActCalculos calc = new ActCalculos();
                calc.findObjects(result, ImageMat);
                calc.surfaceCalc(areaQuadrado, ImageMat, nome, sharedPreferences.getString("treatment", null), sharedPreferences.getString("species", null));

                //findObjects(result);
                //surfaceCalc();
//                Log.d("yourTag", "value: " + calc.getSquare().size());

//                atualizarBanco(calc.getListaFolhas());
                folhaRepositorio.inserir(calc);

                //if (square.size() <= 0 || square.size() > 1 || leaves.size() <= 0) {
                if (calc.getSquare().size() <= 0 || calc.getSquare().size() > 1 || calc.getLeaves().size() <= 0) {
                    Toast.makeText(getApplicationContext(), "An error occurred while analyzing the image. Please try again.", Toast.LENGTH_LONG).show();
                    Log.d("yourTag", "An error occurred while analyzing the image. Please try again.");
                } else {
                    //Log.d("yourTag", "ERROR");
                    //Converte o Mat em bitmap para salvar na tela
                    Utils.matToBitmap(ImageMat, bitmap);
                    //Cria objeto de ByteArray
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //Converte o bitmap para JPEG
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    //Salva o bitmap enquanto o app é utilzado
                    BitmapHelper.getInstance().setBitmap(bitmap);
                    //Abre a tela para mostrar o resultado
                    Intent it = new Intent(this, ActSaidaImagem.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ActCalculos auxCalc = folhaRepositorio.consultar(false, null);
                    List<Folha> dados = auxCalc.getListaFolhas();
                    int codigo = dados.get(dados.size() - 1).getCod();
                    it.putExtra("CODIGO",codigo);
                    // Show progress bar
                    //progressBar.show(this,"Loading...");
                    //Inicia a intent
                    startActivity(it);
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void criarConexao() {
        try {
            DadosOpenHelper dadosOpenHelper = new DadosOpenHelper(this);
            SQLiteDatabase conexao = dadosOpenHelper.getWritableDatabase();
            folhaRepositorio = new FolhasRepositorio(conexao);
            //Toast.makeText(getApplicationContext(), "Conexão criada com sucesso!", Toast.LENGTH_SHORT).show();
        } catch (SQLException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.i("OpenCv", "OpenCV loaded failed");
        } else {
            Log.i("OpenCv", "OpenCV loaded successfully");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_act_main:
                Intent it = new Intent(this, ActCameraCv.class);
                startActivityForResult(it, TELA_CAMERA);
                break;

            case R.id.galeria_act_main:
                Intent intentPegaFoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentPegaFoto, 2);
                break;

            case R.id.dados_salvos_act_main:
                Intent itDados = new Intent(this, ActDados.class);
                startActivityForResult(itDados, 0);
                break;

            case R.id.informacao_act_main:
                Intent itConfig = new Intent(this, ActConfigGeral.class);
                startActivityForResult(itConfig, 0);
                break;
        }
    }

    @Override
    protected void onResume() {

        if (getIntent().getBooleanExtra("EXIT", false)) {

            //Toast.makeText(getApplicationContext(), "Fechar tudo", Toast.LENGTH_LONG).show();
            //finish();
        }

        super.onResume();
    }
}


