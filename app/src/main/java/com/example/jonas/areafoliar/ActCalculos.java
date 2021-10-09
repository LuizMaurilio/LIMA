package com.example.jonas.areafoliar;

import android.annotation.SuppressLint;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.jonas.areafoliar.database.DadosOpenHelper;
import com.example.jonas.areafoliar.repositorio.FolhasRepositorio;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActCalculos extends AppCompatActivity{
    //private Mat ImageMat;
    public Bitmap bitmap;

    private FolhasRepositorio folhaRepositorio;

    private String data_completa;
    private List<MatOfPoint> square = new ArrayList<>();
    private List<MatOfPoint> leaves = new ArrayList<>();
    private List<MatOfPoint> leavesPCA = new ArrayList<>();
    private List<Folha> ListaFolhas = new ArrayList<>();

    private String nome;
    private String area;
    private String altura;
    private String largura;
    private String tipo;
    private String perimetro;
    private String data;


    //private Context context;

//    protected void onCreate(){
//        criarConexao();
//    }
//    public ActCalculos(){
//        criarConexao();
//    }

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.i("OpenCv", "OpenCV loaded failed");
        } else {
            Log.i("OpenCv", "OpenCV loaded successfully");
        }
    }


    public void criarConexao() {
        try {
            DadosOpenHelper dadosOpenHelper = new DadosOpenHelper(this);
            SQLiteDatabase conexao = dadosOpenHelper.getWritableDatabase();
            folhaRepositorio = new FolhasRepositorio(conexao);
            //Toast.makeText(getApplicationContext(), "Conexão criada com sucesso!", Toast.LENGTH_SHORT).show();
        } catch (SQLException ex) {
            //Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
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

    void findObjects(Mat image, Mat ImageMat) {
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
        Log.d("yourTag", "This is my message55555");
        for (int i = 0; i < contours.size(); i++) {
            approx[i] = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), approx[i], Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true) * 0.02, true);
            //approxPolyDP(contours[i], approx, arcLength(contours[i], true)*0.02, true);
            //if(approx.size() == 4 && fabs(contourArea(approx)) > 10000 && fabs(contourArea(approx)) < 999999999 && isContourConvex(approx) ){ NÃO CONSEGUI COLOCAR O isContourConvex
            Log.d("yourTag", "This is my m222222225");
            if (approx[i].toArray().length == 4 && Math.abs(Imgproc.contourArea(approx[i])) > 1000 && Math.abs(Imgproc.contourArea(approx[i])) < 10000000000.0) {
                double maxCosine = 0;
                Log.d("yourTag", "This is my m888888888885");
                for (int j = 2; j < 5; j++) {
                    double cosine = Math.abs(angle(approx[i].toArray()[j % 4], approx[i].toArray()[j - 2], approx[i].toArray()[j - 1]));
                    maxCosine = Math.max(maxCosine, cosine);
                    Log.d("yourTag", "This is my 1671671451755");
                }
                if (maxCosine < 0.3) {
                    Log.d("yourTag", "This is my 8989898989");
                    getSquare().add(contours.get(i));
                    Imgproc.drawContours(ImageMat, contours, i, new Scalar(0, 255, 0), 3);
                    Log.d("yourTag", "contour drawn");
                } else {
                    Log.d("yourTag", "This is my 7777777777");
                    getLeaves().add(contours.get(i));
                    MatOfPoint contourPCA = pca(contours, i);
                    getLeavesPCA().add(contourPCA);
                    List<Point> pts = contourPCA.toList();
                    Imgproc.drawContours(ImageMat, contours, i, new Scalar(255, 0, 0), 3);
                }
            } else if (Math.abs(Imgproc.contourArea(approx[i])) > 1000 && Math.abs(Imgproc.contourArea(approx[i])) < 10000000000.0) {
                Log.d("yourTag", "This is my 787878787878787");
                getLeaves().add(contours.get(i));
                MatOfPoint contourPCA =  pca(contours, i);
                getLeavesPCA().add(contourPCA);
                List<Point> pts = contourPCA.toList();
                Imgproc.drawContours(ImageMat, contours, i, new Scalar(255, 0, 0), 3);
            }
        }
        Log.d("myTag", "This is my message10000");
    }

    void surfaceCalc(float areaQuadrado, Mat ImageMat) {
        //Log.d("yourTag", "This is my 651654784165");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        Date dataCalc = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dataCalc);
        Date data_atual = cal.getTime();
        data_completa = dateFormat.format(data_atual);
        if(getSquare().size() <= 0){
            //Toast.makeText(getApplicationContext(), "The square could not be found. Please try again.", Toast.LENGTH_LONG).show();
        }else if(getLeaves().size() <= 0){
            //Toast.makeText(getApplicationContext(), "No leaf can be found. Please try again.", Toast.LENGTH_LONG).show();
        }else{
            List<MatOfPoint> result = new ArrayList<>();
            //SharedPreferences sharedPreferences = getSharedPreferences("valorLadoPref", Context.MODE_PRIVATE);
            //float areaQuadrado = sharedPreferences.getInt("area", 5);

            //---------------------Variaveis auxiliares calculos-----------------------

            double pixelsWidSquare, pixelsLenSquare;
            double pixelsPerSquare;
            //double sum = 0.0;
            double mL = 0.0, mC = 0.0, mA = 0.0, mP = 0.0;
            double dL = 0.0, dC = 0.0, dA = 0.0, dP = 0.0;
            double[] L = new double[getLeaves().size()];
            double[] C = new double[getLeaves().size()];
            double[] A = new double[getLeaves().size()];
            double[] P = new double[getLeaves().size()];
            //double[] LC = new double[leaves.size()];

            //-------------------SQUARE----------------------

            pixelsPerSquare = Imgproc.arcLength(new MatOfPoint2f(getSquare().get(0).toArray()), true);

            pixelsWidSquare = pixelsPerSquare/4;//Math.sqrt((Math.pow((square.get(0).toArray()[2].x - square.get(0).toArray()[1].x), 2) + Math.pow((square.get(0).toArray()[2].y - square.get(0).toArray()[1].y), 2)));
            pixelsLenSquare = pixelsWidSquare;//Math.sqrt((Math.pow((square.get(0).toArray()[1].x - square.get(0).toArray()[0].x), 2) + Math.pow((square.get(0).toArray()[1].y - square.get(0).toArray()[0].y), 2)));

            double pixelsAreaSquare = Imgproc.contourArea(getSquare().get(0));
            double realPerSquare = Math.sqrt(areaQuadrado) * 4;

            //final Point p = square.get(0).toArray()[0];
            //int n = square.get(0).toArray().length;
            Scalar color = new Scalar(0, 255, 0);
            Imgproc.polylines(ImageMat, result, true, color, 1, 10, Imgproc.LINE_AA);

            //---------------------LEAFS-----------------------

            Rect[] boundRect = new Rect[getLeavesPCA().size()];
            double realSideSquare = Math.sqrt(areaQuadrado);
            for (int i = 0; i < getLeavesPCA().size(); i++) {
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
                double x = getLeaves().get(i).toArray()[0].x + 0.5 * getLeaves().get(i).width();
                //double y = boundRect[i].y + 0.5 * boundRect[i].height;
                double y = getLeaves().get(i).toArray()[0].y + 0.5 * getLeaves().get(i).height();
                Imgproc.putText(ImageMat, (i + 1) + "", new Point(x, y), Core.FONT_HERSHEY_SIMPLEX, 5, color3,
                        12
                );
                //result.append("\nLeaf: ");
                //result.append(QString::number (i + 1));
                folha.setNome(data_completa + " " + (i + 1));
                //setNome(data_completa + " " + (i + 1));
                //result.append("\n\n");*/

                //_____________Calculo Largura e Comprimento_____________
                boundRect[i] = Imgproc.boundingRect(getLeavesPCA().get(i));

                double aux = boundRect[i].width;//Math.sqrt((Math.pow((boundRect[i].tl().x - boundRect[i].tl().x), 2) + Math.pow((boundRect[i].br().y - boundRect[i].tl().y), 2)));
                aux = (aux * realSideSquare) / pixelsWidSquare;

                double aux2 = boundRect[i].height;//Math.sqrt((Math.pow((boundRect[i].tl().x - boundRect[i].br().x), 2) + Math.pow((boundRect[i].tl().y - boundRect[i].tl().y), 2)));
                aux2 = (aux2 * realSideSquare) / pixelsLenSquare;

                if (aux2 > aux) {
                    mL += aux;
                    mC += aux2;
                    //result.append("\nWidth: "); result.append(QString::number(aux));
                    folha.setLargura(aux + "");
                    //setLargura(aux + "");
                    L[i] = aux;
                    //result.append("\nLength: "); result.append(QString::number(aux2));
                    folha.setAltura(aux2 + "");
                    //setAltura(aux2 + "");
                    C[i] = aux2;
                    //result.append("\nWidth/Length: "); result.append(QString::number(aux/aux2));
                    //LC[i] = aux / aux2;
                } else {
                    mL += aux2;
                    mC += aux;
                    //result.append("\nWidth: "); result.append(QString::number(aux2));
                    folha.setLargura(aux2 + "");
                    //setLargura(aux2 + "");
                    L[i] = aux2;
                    //result.append("\nLength: "); result.append(QString::number(aux));
                    C[i] = aux;
                    folha.setAltura(aux + "");
                    //setAltura(aux + "");
                    //result.append("\nWidth/Length: "); result.append(QString::number(aux2/aux));
                    //LC[i] = aux2 / aux;
                }
                //_____________Calculo Area_____________
                double auxArea = ((Imgproc.contourArea(getLeaves().get(i)) * areaQuadrado) / pixelsAreaSquare);
                folha.setArea(auxArea + "");
                //setArea(auxArea + "");
                //sum += auxArea;
                //result.append("\nArea: "); result.append(QString::number(auxArea));
                mA += auxArea;
                A[i] = auxArea;
                //_____________Calculo Perimetro_____________
                double auxPer = ((Imgproc.arcLength(new MatOfPoint2f(getLeaves().get(i).toArray()), true) * realPerSquare) / pixelsPerSquare);
                //result.append("\nPerimeter: "); result.append(QString::number(auxPer));
                folha.setPerimetro(auxPer + "");
                //setPerimetro(auxPer + "");
                mP += auxPer;
                P[i] = auxPer;
                //result.append("\n\n");
                //_____________Result sum areas_____________
                //result.append("\nSum areas: "); result.append(QString::number(sum));
                //result.append("\n\n");

                //_____________Calculo Media e Desvio_____________

                //_____________Media_____________
                mL = mL / getLeavesPCA().size();
                //result.append("\nAverage width: "); result.append(QString::number(mL));
                mC = mC / getLeavesPCA().size();
                //result.append("\nAverage lenght: "); result.append(QString::number(mC));
                mA = mA / getLeavesPCA().size();
                //result.append("\nAverage area: "); result.append(QString::number(mA));
                mP = mP / getLeavesPCA().size();
                //result.append("\nAverage perimeter: "); result.append(QString::number(mP));
                //result.append("\n\n");
                //_____________Desvio_____________
                for (int j = 0; j < getLeavesPCA().size(); j++) {
                    dL += Math.pow(L[i] - mL, 2);
                }
                dL = Math.sqrt(dL / getLeavesPCA().size());
                //result.append("\nWidth deviation: "); result.append(QString::number(dL));
                for (int k = 0; k < getLeavesPCA().size(); k++) {
                    dC += Math.pow(C[i] - mC, 2);
                }
                dC = Math.sqrt(dC / getLeavesPCA().size());
                //result.append("\nLenght deviation: "); result.append(QString::number(dC));
                for (int l = 0; l < getLeavesPCA().size(); l++) {
                    dA += Math.pow(A[i] - mA, 2);
                }
                dA = Math.sqrt(dA / getLeavesPCA().size());
                //result.append("\nArea deviation: "); result.append(QString::number(dA));
                for (int k = 0; k < getLeavesPCA().size(); k++) {
                    dP += Math.pow(P[i] - mP, 2);
                }
                dP = Math.sqrt(dP / getLeavesPCA().size());
                //result.append("\nPerimeter deviation: "); result.append(QString::number(dP));
                //result.append("\n\n");
//                folhaRepositorio.inserir(folha);
                getListaFolhas().add(folha);
            }
//            Folha folhaMedia = new Folha();
//            folhaMedia.setNome(data_completa + " - Nome do teste");
//            folhaMedia.setArea(mA + "");
//            folhaMedia.setAltura(mC + "");
//            folhaMedia.setLargura(mL + "");
//            folhaMedia.setData(data_completa);
//            folhaMedia.setPerimetro(mP + "");
//            folhaMedia.setTipo(1);
//            folhaRepositorio.inserir(folhaMedia);
        }
        Log.d("yourTag", "This is my message55555");}


//    public String getNome() {
//        return nome;
//    }
//
//    public void setNome(String nome) {
//        this.nome = nome;
//    }
//
//    public String getArea() {
//        return area;
//    }
//
//    public void setArea(String area) {
//        this.area = area;
//    }
//
//    public String getAltura() {
//        return altura;
//    }
//
//    public void setAltura(String altura) {
//        this.altura = altura;
//    }
//
//    public String getLargura() {
//        return largura;
//    }
//
//    public void setLargura(String largura) {
//        this.largura = largura;
//    }
//
//    public String getTipo() {
//        return tipo;
//    }
//
//    public void setTipo(String tipo) {
//        this.tipo = tipo;
//    }
//
//    public String getPerimetro() {
//        return perimetro;
//    }
//
//    public void setPerimetro(String perimetro) {
//        this.perimetro = perimetro;
//    }
//
//    public String getData() {
//        return data;
//    }
//
//    public void setData(String data) {
//        this.data = data;
//    }

    public List<MatOfPoint> getSquare() {
        return square;
    }

    public void setSquare(List<MatOfPoint> square) {
        this.square = square;
    }

    public List<MatOfPoint> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<MatOfPoint> leaves) {
        this.leaves = leaves;
    }

    public List<MatOfPoint> getLeavesPCA() {
        return leavesPCA;
    }

    public void setLeavesPCA(List<MatOfPoint> leavesPCA) {
        this.leavesPCA = leavesPCA;
    }

    public List<Folha> getListaFolhas() {
        return ListaFolhas;
    }

    public void setListaFolhas(List<Folha> listaFolhas) {
        ListaFolhas = listaFolhas;
    }
}


