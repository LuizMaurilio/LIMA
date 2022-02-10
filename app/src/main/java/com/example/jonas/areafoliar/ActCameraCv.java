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
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.jonas.areafoliar.database.DadosOpenHelper;
import com.example.jonas.areafoliar.helper.BitmapHelper;
import com.example.jonas.areafoliar.repositorio.FolhasRepositorio;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ActCameraCv extends AppCompatActivity implements CvCameraViewListener2, View.OnClickListener {
    private static final String TAG = "MYAPP::OPENCV";
    private CameraBridgeViewBase mOpenCvCameraView;
    //private double altQuad, largQuad;
    private Mat ImageMat;
    public Bitmap bitmap;
    int total = 0;
    //double altura = 0,largura = 0,areaQuadradoPx = 0,areaFolhaCm = 0;
    private FolhasRepositorio folhaRepositorio;
    //private int cont = 1;
    private String data_completa;
    private List<MatOfPoint2f> square = new ArrayList<>();
    private List<MatOfPoint> leaves = new ArrayList<>();
    private List<MatOfPoint> leavesPCA = new ArrayList<>();

    BaseLoaderCallback mCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == BaseLoaderCallback.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
                mOpenCvCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.i("OpenCv", "OpenCV loaded failed");
        } else {
            Log.i("OpenCv", "OpenCV loaded successfully");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.act_camera_cv);
        (findViewById(R.id.camera)).setOnClickListener(this);
        (findViewById(R.id.gallery)).setOnClickListener(this);
        mOpenCvCameraView = findViewById(R.id.HelloOpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        criarConexao();
    }

    public void criarConexao() {
        try {
            DadosOpenHelper dadosOpenHelper = new DadosOpenHelper(this);
            SQLiteDatabase conexao = dadosOpenHelper.getWritableDatabase();
            folhaRepositorio = new FolhasRepositorio(conexao);
            //Toast.makeText(getApplicationContext(), "Conexão criada com sucesso", Toast.LENGTH_SHORT).show();
        } catch (SQLException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent it2 = new Intent(this, ActMain.class);
        startActivity(it2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                Mat result = new Mat(ImageMat.size(), ImageMat.type());
                Imgproc.cvtColor(ImageMat, result, Imgproc.COLOR_RGB2GRAY);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
                Date dataCalc = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(dataCalc);
                Date data_atual = cal.getTime();
                data_completa = dateFormat.format(data_atual);
                //Realiza a cnoversão de Mat para Bitmap
                bitmap = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(ImageMat, bitmap);

                //ContentValues contentValues = new ContentValues();
                //Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                //Salva a imagem

                SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                float areaQuadrado = sharedPreferences.getInt("area", 1);

                ActCalculos calc = new ActCalculos();
                calc.findObjects(result, ImageMat);

                if (calc.getSquare().size() <= 0 || calc.getSquare().size() > 1 || calc.getLeaves().size() <= 0) {
                    Toast.makeText(getApplicationContext(), "An error occurred while analyzing the image. Please try again.", Toast.LENGTH_LONG).show();
                    Log.d("yourTag", "An error occurred while analyzing the image. Please try again.");
                } else {
                    calc.surfaceCalc(areaQuadrado, ImageMat, "Camera", sharedPreferences.getString("treatment", null), sharedPreferences.getString("species", null), sharedPreferences.getString("repetition", null));
                    folhaRepositorio.inserir(calc);
                    Utils.matToBitmap(ImageMat, bitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    BitmapHelper.getInstance().setBitmap(bitmap);
                    //Abre a tela para mostrar o resultado
                    Intent it = new Intent(this, ActSaidaImagem.class); //OUTPUT IMAGE
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    List<Folha> dados = calc.getListaFolhas();
                    int codigo = dados.get(dados.size() - 1).getCod();
                    it.putExtra("CODIGO",codigo);
                    //Inicia a intent
                    startActivity(it);
                    // Show progress bar
                    //progressBar.show(this,"Loading...");
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
                break;

            case R.id.gallery:
                Intent intentPegaFoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentPegaFoto, 1);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
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

                SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                float areaQuadrado = sharedPreferences.getInt("area", 1);

                ActCalculos calc = new ActCalculos();
                calc.findObjects(result, ImageMat);
                calc.surfaceCalc(areaQuadrado, ImageMat, nome, sharedPreferences.getString("treatment", null), sharedPreferences.getString("species", null), sharedPreferences.getString("repetition", null));

                folhaRepositorio.inserir(calc);
//                findObjects(result);
//                surfaceCalc();
                //Converte o Mat em bitmap para salvar na tela
                Utils.matToBitmap(ImageMat, bitmap);
                //Cria objeto de ByteArray
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                if (square.size() <= 0 || square.size() > 1 || leaves.size() <= 0) {
                if (calc.getSquare().size() <= 0 || calc.getSquare().size() > 1 || calc.getLeaves().size() <= 0) {
                    //Toast.makeText(getApplicationContext(), "An error occurred while analyzing the image. Please try again.", Toast.LENGTH_LONG).show();
                } else {
                    //Converte o bitmap para JPEG
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    BitmapHelper.getInstance().setBitmap(bitmap);
                    //Abre a tela para mostrar o resultado
                    Intent it = new Intent(this, ActSaidaImagem.class);
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Inicia a intent
                    startActivity(it);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCallBack.onManagerConnected(BaseLoaderCallback.SUCCESS);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //Converte a imagem fornecida pela camera em tons de cinza
        ImageMat = inputFrame.rgba();
        //Cria um matriz
        //result = new Mat();
        //Converte o Mat em tons de cinza
        //Imgproc.threshold(ImageMat, result, 80, 255, Imgproc.THRESH_BINARY_INV);
        //Imgproc.adaptiveThreshold(src,result,255, Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY_INV,19,5);
        return ImageMat;
    }
}