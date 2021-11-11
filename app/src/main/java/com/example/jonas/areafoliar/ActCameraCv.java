package com.example.jonas.areafoliar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
                //findObjects(result);
               // surfaceCalc();
                //Realiza a cnoversão de Mat para Bitmap
                bitmap = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(ImageMat, bitmap);
                //ContentValues contentValues = new ContentValues();
                //Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                //Salva a imagem

                SharedPreferences sharedPreferences = getSharedPreferences("valorLadoPref", Context.MODE_PRIVATE);
                float areaQuadrado = sharedPreferences.getInt("area", 1);

                ActCalculos calc = new ActCalculos();
                calc.findObjects(result, ImageMat);
                calc.surfaceCalc(areaQuadrado, ImageMat);

                folhaRepositorio.inserir(calc);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                if (calc.getSquare().size() <= 0 || calc.getSquare().size() > 1 || calc.getLeaves().size() <= 0) {
                    //Toast.makeText(getApplicationContext(), "An error occurred while analyzing the image. Please try again.", Toast.LENGTH_LONG).show();
                } else {
                    BitmapHelper.getInstance().setBitmap(bitmap);
                    //Abre a tela para mostrar o resultado
                    Intent it = new Intent(this, ActSaidaImagem.class); //OUTPUT IMAGE
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    List<Folha> dados = calc.getListaFolhas();
                    int codigo = dados.get(dados.size() - 1).getCodigo();
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

    static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    static Point GetPointAfterRotate(Point inputpoint, Point center, double angle) {
        Point preturn = new Point();
        preturn.x = (inputpoint.x - center.x) * Math.cos(-1 * angle) - (inputpoint.y - center.y) * Math.sin(-1 * angle) + center.x;
        preturn.y = (inputpoint.x - center.x) * Math.sin(-1 * angle) + (inputpoint.y - center.y) * Math.cos(-1 * angle) + center.y;
        return preturn;
    }

    private static double getOrientation(MatOfPoint ptsMat, Point center) {
        List<Point> pts = ptsMat.toList();
        // Construct a buffer used by the pca analysis
        int sz = pts.size();
        Mat dataPts = new Mat(sz, 2, CvType.CV_64F);
        double[] dataPtsData = new double[(int) (dataPts.total() * dataPts.channels())];
        for (int i = 0; i < dataPts.rows(); i++) {
            dataPtsData[i * dataPts.cols()] = pts.get(i).x;
            dataPtsData[i * dataPts.cols() + 1] = pts.get(i).y;
        }
        dataPts.put(0, 0, dataPtsData);
        // Perform PCA analysis
        Mat mean = new Mat();
        Mat eigenvectors = new Mat();
        Core.PCACompute(dataPts, mean, eigenvectors);
        double[] meanData = new double[(int) (mean.total() * mean.channels())];
        mean.get(0, 0, meanData);
        // Store the center of the object
        center.x = meanData[0];
        center.y = meanData[1];
        // Store eigenvectors
        double[] eigenvectorsData = new double[(int) (eigenvectors.total() * eigenvectors.channels())];
        eigenvectors.get(1, 0, eigenvectorsData);
        return Math.atan2(-eigenvectorsData[1], -eigenvectorsData[0]); // orientation in radians;
    }

    private static MatOfPoint pca(List<MatOfPoint> contours, int i) {
        Point pos = new Point();
        double dOrient;
        dOrient = getOrientation(contours.get(i), pos);
        ArrayList<Point> pointsOrdered = new ArrayList<>();

        for (int j = 0; j < contours.get(i).toList().size(); j++) {
            Point p = GetPointAfterRotate(contours.get(i).toList().get(j), pos, dOrient);
            pointsOrdered.add(p);
        }
        MatOfPoint contourPCA = new MatOfPoint();
        contourPCA.fromList(pointsOrdered);
        return contourPCA;
    }

    void findObjects(Mat image) {
        // Mat gray = new Mat();
        Mat thresh = new Mat();
        Mat hierarchy = new Mat();

        List<MatOfPoint> contours = new ArrayList<>();

        //MatOfPoint2f[] approx = new MatOfPoint2f[contours.size()];

        //Imgproc.cvtColor(image, gray, COLOR_BGR2GRAY);

        Imgproc.threshold(image, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint2f[] contoursPoly = new MatOfPoint2f[contours.size()];
        //Imgproc.findContours(thresh, contours, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint2f[] approx = new MatOfPoint2f[contours.size()];
        for (int i = 0; i < contours.size(); i++) {
            approx[i] = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), approx[i], Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true) * 0.02, true);
            //approxPolyDP(contours[i], approx, arcLength(contours[i], true)*0.02, true);
            //if(approx.size() == 4 && fabs(contourArea(approx)) > 10000 && fabs(contourArea(approx)) < 999999999 && isContourConvex(approx) ){ NÃO CONSEGUI COLOCAR O isContourConvex
            if (approx[i].toArray().length == 4 && Math.abs(Imgproc.contourArea(approx[i])) > 10000 && Math.abs(Imgproc.contourArea(approx[i])) < 999999999) {
                double maxCosine = 0;

                for (int j = 2; j < 5; j++) {
                    double cosine = Math.abs(angle(approx[i].toArray()[j % 4], approx[i].toArray()[j - 2], approx[i].toArray()[j - 1]));
                    maxCosine = Math.max(maxCosine, cosine);
                }

                if (maxCosine < 0.3) {
                    square.add(approx[i]);
                    Imgproc.drawContours(ImageMat, contours, i, new Scalar(0, 255, 0), 3);
                } else {
                    leaves.add(contours.get(i));
                    MatOfPoint contourPCA = pca(contours, i);
                    leavesPCA.add(contourPCA);
                    List<Point> pts = contourPCA.toList();
                    Imgproc.drawContours(ImageMat, contours, i, new Scalar(255, 0, 0), 3);
                }
            } else if (Math.abs(Imgproc.contourArea(approx[i])) > 1000 && Math.abs(Imgproc.contourArea(approx[i])) < 999999999) {
                leaves.add(contours.get(i));
                MatOfPoint contourPCA = pca(contours, i);
                leavesPCA.add(contourPCA);
                List<Point> pts = contourPCA.toList();
                Imgproc.drawContours(ImageMat, contours, i, new Scalar(255, 0, 0), 3);
            }
        }
    }

    void surfaceCalc() {
        if (square.size() <= 0 || square.size() > 1) {
            Toast.makeText(getApplicationContext(), "The square could not be found. Please try again.", Toast.LENGTH_LONG).show();
        } else if (leaves.size() <= 0) {
            Toast.makeText(getApplicationContext(), "No leaf can be found. Please try again.", Toast.LENGTH_LONG).show();
        } else {
            List<MatOfPoint> result = new ArrayList<>();
            SharedPreferences sharedPreferences = getSharedPreferences("valorLadoPref", Context.MODE_PRIVATE);
            float areaQuadrado = sharedPreferences.getInt("lado", 5) * sharedPreferences.getInt("lado", 5);

            //---------------------Variaveis auxiliares calculos-----------------------

            double largSquare, compSquare;
            //double sum = 0.0;
            double mL = 0.0, mC = 0.0, mA = 0.0, mP = 0.0;
            double dL = 0.0, dC = 0.0, dA = 0.0, dP = 0.0;
            double[] L = new double[leaves.size()];
            double[] C = new double[leaves.size()];
            double[] A = new double[leaves.size()];
            double[] P = new double[leaves.size()];
            //double[] LC = new double[leaves.size()];

            //-------------------SQUARE----------------------

            largSquare = Math.sqrt((Math.pow((square.get(0).toArray()[2].x - square.get(0).toArray()[1].x), 2) + Math.pow((square.get(0).toArray()[2].y - square.get(0).toArray()[1].y), 2)));
            compSquare = Math.sqrt((Math.pow((square.get(0).toArray()[1].x - square.get(0).toArray()[0].x), 2) + Math.pow((square.get(0).toArray()[1].y - square.get(0).toArray()[0].y), 2)));

            double contourSquare = Imgproc.contourArea(square.get(0));
            double perSquare = Math.sqrt(areaQuadrado) * 4;

            //final Point p = square.get(0).toArray()[0];
            //int n = square.get(0).toArray().length;
            Scalar color = new Scalar(0, 255, 0);
            Imgproc.polylines(ImageMat, result, true, color, 1, 10, Imgproc.LINE_AA);

            //---------------------LEAFS-----------------------

            Rect[] boundRect = new Rect[leavesPCA.size()];

            for (int i = 0; i < leavesPCA.size(); i++) {
                Folha folha = new Folha();
                folha.setData(data_completa);
                //folha.setTipo(0);
                //final Point p = leaves.get(i).toArray()[0];
                //int n = leaves.get(i).toArray().length;
                Scalar color2 = new Scalar(0, 0, 255);
                Imgproc.polylines(ImageMat, result, true, color2, 1, 10, Imgproc.LINE_AA);
                Scalar color3 = new Scalar(255, 0, 0);
                //leaves[i].at(leaves[i].capacity()/2);
                //double x = boundRect[i].x + 0.5 * boundRect[i].width;
                double x = leaves.get(i).toArray()[0].x + 0.5 * leaves.get(i).width();
                //double y = boundRect[i].y + 0.5 * boundRect[i].height;
                double y = leaves.get(i).toArray()[0].y + 0.5 * leaves.get(i).height();
                Imgproc.putText(ImageMat, (i + 1) + "", new Point(x, y), Core.FONT_HERSHEY_SIMPLEX, 5, color3,
                        12
                );
                //result.append("\nLeaf: ");
                //result.append(QString::number (i + 1));
                folha.setNome(data_completa + " " + (i + 1));
                //result.append("\n\n");*/

                //_____________Calculo Largura e Comprimento_____________
                boundRect[i] = Imgproc.boundingRect(leavesPCA.get(i));

                double aux = Math.sqrt((Math.pow((boundRect[i].tl().x - boundRect[i].tl().x), 2) + Math.pow((boundRect[i].br().y - boundRect[i].tl().y), 2)));
                aux = (aux * Math.sqrt((areaQuadrado))) / largSquare;

                double aux2 = Math.sqrt((Math.pow((boundRect[i].tl().x - boundRect[i].br().x), 2) + Math.pow((boundRect[i].tl().y - boundRect[i].tl().y), 2)));
                aux2 = (aux2 * Math.sqrt((areaQuadrado))) / compSquare;

                if (aux2 > aux) {
                    mL += aux;
                    mC += aux2;
                    //result.append("\nWidth: "); result.append(QString::number(aux));
                    folha.setLargura(aux + "");
                    L[i] = aux;
                    //result.append("\nLength: "); result.append(QString::number(aux2));
                    folha.setAltura(aux2 + "");
                    C[i] = aux2;
                    //result.append("\nWidth/Length: "); result.append(QString::number(aux/aux2));
                    //LC[i] = aux / aux2;
                } else {
                    mL += aux2;
                    mC += aux;
                    //result.append("\nWidth: "); result.append(QString::number(aux2));
                    folha.setLargura(aux2 + "");
                    L[i] = aux2;
                    //result.append("\nLength: "); result.append(QString::number(aux));
                    C[i] = aux;
                    folha.setAltura(aux + "");
                    //result.append("\nWidth/Length: "); result.append(QString::number(aux2/aux));
                    //LC[i] = aux2 / aux;
                }
                //_____________Calculo Area_____________
                double auxArea = ((Imgproc.contourArea(leavesPCA.get(i)) * areaQuadrado) / contourSquare);
                folha.setArea(auxArea + "");
                //sum += auxArea;
                //result.append("\nArea: "); result.append(QString::number(auxArea));
                mA += auxArea;
                A[i] = auxArea;
                //_____________Calculo Perimetro_____________
                double auxPer = ((Imgproc.arcLength(new MatOfPoint2f(leavesPCA.get(i).toArray()), true) * perSquare) / Imgproc.arcLength(new MatOfPoint2f(square.get(0)), true));
                //result.append("\nPerimeter: "); result.append(QString::number(auxPer));
                folha.setPerimetro(auxPer + "");
                mP += auxPer;
                P[i] = auxPer;
                //result.append("\n\n");
                //_____________Result sum areas_____________
                //result.append("\nSum areas: "); result.append(QString::number(sum));
                //result.append("\n\n");

                //_____________Calculo Media e Desvio_____________

                //_____________Media_____________
                mL = mL / leavesPCA.size();
                //result.append("\nAverage width: "); result.append(QString::number(mL));
                mC = mC / leavesPCA.size();
                //result.append("\nAverage lenght: "); result.append(QString::number(mC));
                mA = mA / leavesPCA.size();
                //result.append("\nAverage area: "); result.append(QString::number(mA));
                mP = mP / leavesPCA.size();
                //result.append("\nAverage perimeter: "); result.append(QString::number(mP));
                //result.append("\n\n");
                //_____________Desvio_____________
                for (int j = 0; j < leavesPCA.size(); j++) {
                    dL += Math.pow(L[i] - mL, 2);
                }
                dL = Math.sqrt(dL / leavesPCA.size());
                //result.append("\nWidth deviation: "); result.append(QString::number(dL));
                for (int k = 0; k < leavesPCA.size(); k++) {
                    dC += Math.pow(C[i] - mC, 2);
                }
                dC = Math.sqrt(dC / leavesPCA.size());
                //result.append("\nLenght deviation: "); result.append(QString::number(dC));
                for (int l = 0; l < leavesPCA.size(); l++) {
                    dA += Math.pow(A[i] - mA, 2);
                }
                dA = Math.sqrt(dA / leavesPCA.size());
                //result.append("\nArea deviation: "); result.append(QString::number(dA));
                for (int k = 0; k < leavesPCA.size(); k++) {
                    dP += Math.pow(P[i] - mP, 2);
                }
                dP = Math.sqrt(dP / leavesPCA.size());
                //result.append("\nPerimeter deviation: "); result.append(QString::number(dP));
                //result.append("\n\n");
                //folhaRepositorio.inserir(folha);
            }
            Folha folhaMedia = new Folha();
            folhaMedia.setNome(data_completa + " - Nome do teste");
            folhaMedia.setArea(mA + "");
            folhaMedia.setAltura(mC + "");
            folhaMedia.setLargura(mL + "");
            folhaMedia.setData(data_completa);
            folhaMedia.setPerimetro(mP + "");
            //folhaMedia.setTipo(1);
            //folhaRepositorio.inserir(folhaMedia);
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

                SharedPreferences sharedPreferences = getSharedPreferences("valorLadoPref", Context.MODE_PRIVATE);
                float areaQuadrado = sharedPreferences.getInt("area", 1);

                ActCalculos calc = new ActCalculos();
                calc.findObjects(result, ImageMat);
                calc.surfaceCalc(areaQuadrado, ImageMat);

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

    public void atualizarBanco(List<Folha> ListaFolhas){
        for(int i = 0; i < ListaFolhas.size(); i++ ){
            //folhaRepositorio.inserir(ListaFolhas.get(i));
            Log.d("Inserção", "Concluída");
            Log.d("valores", "ValoresFOlha: " + ListaFolhas.get(i).getArea());
        }
    }
}