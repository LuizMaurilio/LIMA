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
    //private int cont = 1;
    private String data_completa;
    private List<MatOfPoint> square = new ArrayList<>();
    private List<MatOfPoint> leaves = new ArrayList<>();
    private List<MatOfPoint> leavesPCA = new ArrayList<>();

    private static CustomProgressBar progressBar = new CustomProgressBar();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }
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
                findObjects(result);
                surfaceCalc();
                if (square.size() <= 0 || square.size() > 1 || leaves.size() <= 0) {
                    //Toast.makeText(getApplicationContext(), "An error occurred while analyzing the image. Please try again.", Toast.LENGTH_LONG).show();
                } else {
                    //Converte o Mat em bitmap para salvar na tela
                    Utils.matToBitmap(ImageMat, bitmap);
                    //Cria objeto de ByteArray
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //Converte o bitmap para JPEG
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    //Salva o bitmap enquanto o app é utilzado
                    BitmapHelper.getInstance().setBitmap(bitmap);
                    //Abre a tela para mostrar o resultado
                    Intent it = new Intent(this, ActCamera.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    List<Folha> dados = folhaRepositorio.consultar();
                    int codigo = dados.get(dados.size() - 1).getCodigo();
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
            if (approx[i].toArray().length == 4 && Math.abs(Imgproc.contourArea(approx[i])) > 1000 && Math.abs(Imgproc.contourArea(approx[i])) < 10000000000.0) {
                double maxCosine = 0;

                for (int j = 2; j < 5; j++) {
                    double cosine = Math.abs(angle(approx[i].toArray()[j % 4], approx[i].toArray()[j - 2], approx[i].toArray()[j - 1]));
                    maxCosine = Math.max(maxCosine, cosine);
                }

                if (maxCosine < 0.3) {
                    square.add(contours.get(i));
                    Imgproc.drawContours(ImageMat, contours, i, new Scalar(0, 255, 0), 3);
                } else {
                    leaves.add(contours.get(i));
                    MatOfPoint contourPCA = pca(contours, i);
                    leavesPCA.add(contourPCA);
                    List<Point> pts = contourPCA.toList();
                    Imgproc.drawContours(ImageMat, contours, i, new Scalar(255, 0, 0), 3);
                }
            } else if (Math.abs(Imgproc.contourArea(approx[i])) > 1000 && Math.abs(Imgproc.contourArea(approx[i])) < 10000000000.0) {
                leaves.add(contours.get(i));
                MatOfPoint contourPCA =  pca(contours, i);
                leavesPCA.add(contourPCA);
                List<Point> pts = contourPCA.toList();
                Imgproc.drawContours(ImageMat, contours, i, new Scalar(255, 0, 0), 3);
            }
        }
    }

    void surfaceCalc() {
        if(square.size() <= 0){
            Toast.makeText(getApplicationContext(), "The square could not be found. Please try again.", Toast.LENGTH_LONG).show();
        }else if(leaves.size() <= 0){
            Toast.makeText(getApplicationContext(), "No leaf can be found. Please try again.", Toast.LENGTH_LONG).show();
        }else{
            List<MatOfPoint> result = new ArrayList<>();
            SharedPreferences sharedPreferences = getSharedPreferences("valorLadoPref", Context.MODE_PRIVATE);
            float areaQuadrado = sharedPreferences.getInt("lado", 5) * sharedPreferences.getInt("lado", 5);

            //---------------------Variaveis auxiliares calculos-----------------------

            double pixelsWidSquare, pixelsLenSquare;
            double pixelsPerSquare;
            //double sum = 0.0;
            double mL = 0.0, mC = 0.0, mA = 0.0, mP = 0.0;
            double dL = 0.0, dC = 0.0, dA = 0.0, dP = 0.0;
            double[] L = new double[leaves.size()];
            double[] C = new double[leaves.size()];
            double[] A = new double[leaves.size()];
            double[] P = new double[leaves.size()];
            //double[] LC = new double[leaves.size()];

            //-------------------SQUARE----------------------

            pixelsPerSquare = Imgproc.arcLength(new MatOfPoint2f(square.get(0).toArray()), true);

            pixelsWidSquare = pixelsPerSquare/4;//Math.sqrt((Math.pow((square.get(0).toArray()[2].x - square.get(0).toArray()[1].x), 2) + Math.pow((square.get(0).toArray()[2].y - square.get(0).toArray()[1].y), 2)));
            pixelsLenSquare = pixelsWidSquare;//Math.sqrt((Math.pow((square.get(0).toArray()[1].x - square.get(0).toArray()[0].x), 2) + Math.pow((square.get(0).toArray()[1].y - square.get(0).toArray()[0].y), 2)));

            double pixelsAreaSquare = Imgproc.contourArea(square.get(0));
            double realPerSquare = Math.sqrt(areaQuadrado) * 4;

            //final Point p = square.get(0).toArray()[0];
            //int n = square.get(0).toArray().length;
            Scalar color = new Scalar(0, 255, 0);
            Imgproc.polylines(ImageMat, result, true, color, 1, 10, Imgproc.LINE_AA);

            //---------------------LEAFS-----------------------

            Rect[] boundRect = new Rect[leavesPCA.size()];
            double realSideSquare = Math.sqrt(areaQuadrado);
            for (int i = 0; i < leavesPCA.size(); i++) {
                Folha folha = new Folha();
                folha.setData(data_completa);
                folha.setTipo(0);
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

                double aux = boundRect[i].width;//Math.sqrt((Math.pow((boundRect[i].tl().x - boundRect[i].tl().x), 2) + Math.pow((boundRect[i].br().y - boundRect[i].tl().y), 2)));
                aux = (aux * realSideSquare) / pixelsWidSquare;

                double aux2 = boundRect[i].height;//Math.sqrt((Math.pow((boundRect[i].tl().x - boundRect[i].br().x), 2) + Math.pow((boundRect[i].tl().y - boundRect[i].tl().y), 2)));
                aux2 = (aux2 * realSideSquare) / pixelsLenSquare;

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
                double auxArea = ((Imgproc.contourArea(leaves.get(i)) * areaQuadrado) / pixelsAreaSquare);
                folha.setArea(auxArea + "");
                //sum += auxArea;
                //result.append("\nArea: "); result.append(QString::number(auxArea));
                mA += auxArea;
                A[i] = auxArea;
                //_____________Calculo Perimetro_____________
                double auxPer = ((Imgproc.arcLength(new MatOfPoint2f(leaves.get(i).toArray()), true) * realPerSquare) / pixelsPerSquare);
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
                folhaRepositorio.inserir(folha);
            }
            Folha folhaMedia = new Folha();
            folhaMedia.setNome(data_completa + " - Nome do teste");
            folhaMedia.setArea(mA + "");
            folhaMedia.setAltura(mC + "");
            folhaMedia.setLargura(mL + "");
            folhaMedia.setData(data_completa);
            folhaMedia.setPerimetro(mP + "");
            folhaMedia.setTipo(1);
            folhaRepositorio.inserir(folhaMedia);
        }
    }
}


