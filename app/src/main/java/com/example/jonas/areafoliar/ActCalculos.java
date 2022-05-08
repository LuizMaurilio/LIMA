package com.example.jonas.areafoliar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActCalculos implements Parcelable{
    //private Mat ImageMat;
    public Bitmap bitmap;

    private String data_completa;
    private List<MatOfPoint> square = new ArrayList<>();
    private List<MatOfPoint> leaves = new ArrayList<>();
    private List<MatOfPoint> leavesPCA = new ArrayList<>();
    private ArrayList<Folha> ListaFolhas = new ArrayList<>();

    private String nome;
    private String idImg;
    private Double sumareas;
    private Float area_Quad;
    private Double larg_Media;
    private Double larg_Desvio;
    private Double comp_Medio;
    private Double comp_Desvio;
    private Double area_Media;
    private Double area_Desvio;
    private Double per_Media;
    private Double per_Desvio;
    private Double largComp_Desvio = 20000.00;
    private Double largComp_Media;
    private String especie;
    private String tratamento = "TESTE";
    private Integer repeticao = 10;

    public ActCalculos(String idImg, Integer repeticao, String tratamento, String especie, Double largComp_Media, Double largComp_Desvio, Double per_Desvio,
                       Double per_Media, Double area_Desvio, Double area_Media, Double comp_Desvio, Double comp_Medio, Double larg_Desvio, Double larg_Media,
                       Double sumareas, String nome, ArrayList<Folha> ListaFolhas){
        this.idImg = idImg;
        this.repeticao = repeticao;
        this.tratamento = tratamento;
        this.especie = especie;
        this.sumareas = sumareas;
        this.larg_Desvio = larg_Desvio;
        this.larg_Media = larg_Media;
        this.comp_Desvio = comp_Desvio;
        this.comp_Medio = comp_Medio;
        this.largComp_Desvio = largComp_Desvio;
        this.largComp_Media = largComp_Media;
        this.area_Desvio = area_Desvio;
        this.area_Media = area_Media;
        this.per_Desvio = per_Desvio;
        this.per_Media = per_Media;
    }

    public ActCalculos(){ }

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.i("OpenCv", "OpenCV loaded failed");
        } else {
            Log.i("OpenCv", "OpenCV loaded successfully");
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //parcel.writeList(getListaFolhas());
        parcel.writeString(getNome());
        parcel.writeString(getIdImg());
        parcel.writeDouble(getSumareas());
        parcel.writeDouble(getArea_Quad());
        parcel.writeDouble(getLarg_Media());
        parcel.writeDouble(getLarg_Desvio());
        parcel.writeDouble(getComp_Medio());
        parcel.writeDouble(getComp_Desvio());
        parcel.writeDouble(getArea_Media());
        parcel.writeDouble(getArea_Desvio());
        parcel.writeDouble(getPer_Media());
        parcel.writeDouble(getPer_Desvio());
        parcel.writeDouble(getLarg_Desvio());
        parcel.writeDouble(getLargComp_Media());
        parcel.writeString(getEspecie());
        parcel.writeString(getTratamento());
        parcel.writeInt(getRepeticao());
    }

    public ActCalculos(Parcel in) {
//        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
//        data_completa = in.readString();
        //ListaFolhas = in.createTypedArrayList(Folha.CREATOR);
        nome = in.readString();
        idImg = in.readString();
        sumareas = in.readDouble();
        area_Quad = in.readFloat();
        larg_Media = in.readDouble();
        larg_Desvio = in.readDouble();
        comp_Medio = in.readDouble();
        comp_Desvio = in.readDouble();
        area_Media = in.readDouble();
        area_Desvio = in.readDouble();
        per_Media = in.readDouble();
        per_Desvio = in.readDouble();
        largComp_Desvio = in.readDouble();
        largComp_Media = in.readDouble();
        especie = in.readString();
        tratamento = in.readString();
        repeticao = in.readInt();
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
                    getSquare().add(contours.get(i));
                    Imgproc.drawContours(ImageMat, contours, i, new Scalar(0, 255, 0), 3);
                } else {
                    getLeaves().add(contours.get(i));
                    MatOfPoint contourPCA = pca(contours, i);
                    getLeavesPCA().add(contourPCA);
                    List<Point> pts = contourPCA.toList();
                    Imgproc.drawContours(ImageMat, contours, i, new Scalar(255, 0, 0), 3);
                }
            } else if (Math.abs(Imgproc.contourArea(approx[i])) > 1000 && Math.abs(Imgproc.contourArea(approx[i])) < 10000000000.0) {
                getLeaves().add(contours.get(i));
                MatOfPoint contourPCA =  pca(contours, i);
                getLeavesPCA().add(contourPCA);
                List<Point> pts = contourPCA.toList();
                Imgproc.drawContours(ImageMat, contours, i, new Scalar(255, 0, 0), 3);
            }
        }
    }

    void surfaceCalc(float areaQuadrado, Mat ImageMat, String name, String treatment, String species, Integer repetition) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        Date dataCalc = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dataCalc);
        Date data_atual = cal.getTime();
        data_completa = dateFormat.format(data_atual);

            List<MatOfPoint> result = new ArrayList<>();
            //SharedPreferences sharedPreferences = getSharedPreferences("valorLadoPref", Context.MODE_PRIVATE);
            //float areaQuadrado = sharedPreferences.getInt("area", 5);

            //---------------------Variaveis auxiliares calculos-----------------------

            double pixelsWidSquare, pixelsLenSquare;
            double pixelsPerSquare;
            //double sum = 0.0;
            double mL = 0.0, mC = 0.0, mA = 0.0, mP = 0.0, mLC = 0.0;
            double dL = 0.0, dC = 0.0, dA = 0.0, dP = 0.0, dLC = 0.0;
            double[] L = new double[getLeaves().size()];
            double[] C = new double[getLeaves().size()];
            double[] A = new double[getLeaves().size()];
            double[] P = new double[getLeaves().size()];
            double[] LC = new double[getLeaves().size()];
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
                folha.setIdImg(data_completa);
                folha.setNum_Folha(i+1);
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

                //_____________Calculo Largura e Comprimento_____________
                boundRect[i] = Imgproc.boundingRect(getLeavesPCA().get(i));

                double aux = boundRect[i].width;//Math.sqrt((Math.pow((boundRect[i].tl().x - boundRect[i].tl().x), 2) + Math.pow((boundRect[i].br().y - boundRect[i].tl().y), 2)));
                aux = (aux * realSideSquare) / pixelsWidSquare;

                double aux2 = boundRect[i].height;//Math.sqrt((Math.pow((boundRect[i].tl().x - boundRect[i].br().x), 2) + Math.pow((boundRect[i].tl().y - boundRect[i].tl().y), 2)));
                aux2 = (aux2 * realSideSquare) / pixelsLenSquare;

                if (aux2 > aux) {
                    aux2 = Math.round(aux2*100.0)/100.0;
                    aux = Math.round(aux*100.0)/100.0;
                    mL += aux;
                    mC += aux2;
                    folha.setLargura(aux);
                    L[i] = aux;
                    folha.setComprimento(aux2);
                    C[i] = aux2;

                    Double largcomp = aux/aux2;
                    largcomp = Math.round(largcomp*100.0)/100.0;
                    folha.setLargcomp(largcomp);
                    mLC += largcomp;
                    LC[i] = largcomp;
                } else {
                    aux2 = Math.round(aux2*100.0)/100.0;
                    aux = Math.round(aux*100.0)/100.0;
                    mL += aux2;
                    mC += aux;
                    folha.setLargura(aux2);
                    L[i] = aux2;
                    C[i] = aux;
                    folha.setComprimento(aux);

                    Double largcomp = aux2/aux;
                    largcomp = Math.round(largcomp*100.0)/100.0;
                    folha.setLargcomp(largcomp);
                    mLC += largcomp;
                    LC[i] = largcomp;
                }
                //_____________Calculo Area_____________
                double auxArea = ((Imgproc.contourArea(getLeaves().get(i)) * areaQuadrado) / pixelsAreaSquare);
                auxArea = Math.round(auxArea*10.0)/10.0;
                folha.setArea(auxArea);
                //setArea(auxArea + "");
                //sum += auxArea;
                //result.append("\nArea: "); result.append(QString::number(auxArea));
                mA += auxArea;
                A[i] = auxArea;
                //_____________Calculo Perimetro_____________
                double auxPer = ((Imgproc.arcLength(new MatOfPoint2f(getLeaves().get(i).toArray()), true) * realPerSquare) / pixelsPerSquare);
                //result.append("\nPerimeter: "); result.append(QString::number(auxPer));
                auxPer = Math.round(auxPer*100.0)/100.0;
                folha.setPerimetro(auxPer);
                //setPerimetro(auxPer + "");
                mP += auxPer;
                P[i] = auxPer;
                //result.append("\n\n");
                //_____________Result sum areas_____________
                //result.append("\nSum areas: "); result.append(QString::number(sum));
                //result.append("\n\n");
                getListaFolhas().add(folha);
            }

            mL = mL / getLeavesPCA().size();
            //result.append("\nAverage width: "); result.append(QString::number(mL));
            mC = mC / getLeavesPCA().size();
            //result.append("\nAverage lenght: "); result.append(QString::number(mC));
            mLC = mLC / getLeavesPCA().size();
            mA = mA / getLeavesPCA().size();
            //result.append("\nAverage area: "); result.append(QString::number(mA));
            mP = mP / getLeavesPCA().size();
            //result.append("\nAverage perimeter: "); result.append(QString::number(mP));
            //result.append("\n\n");
            //_____________Desvio_____________
            for (int j = 0; j < getLeavesPCA().size(); j++) {
                dL += Math.pow(L[j] - mL, 2);
            }
            dL = Math.sqrt(dL / getLeavesPCA().size());
            //result.append("\nWidth deviation: "); result.append(QString::number(dL));
            for (int k = 0; k < getLeavesPCA().size(); k++) {
                dC += Math.pow(C[k] - mC, 2);
            }
            dC = Math.sqrt(dC / getLeavesPCA().size());
            //result.append("\nLenght deviation: "); result.append(QString::number(dC));
            for (int l = 0; l < getLeavesPCA().size(); l++) {
                dA += Math.pow(A[l] - mA, 2);
            }
            dA = Math.sqrt(dA / getLeavesPCA().size());
            //result.append("\nArea deviation: "); result.append(QString::number(dA));
            for (int k = 0; k < getLeavesPCA().size(); k++) {
                dP += Math.pow(P[k] - mP, 2);
            }
            dP = Math.sqrt(dP / getLeavesPCA().size());

            for (int k = 0; k < getLeavesPCA().size(); k++) {
                dLC += Math.pow(LC[k] - mLC, 2);
            }
            dLC = Math.sqrt(dLC / getLeavesPCA().size());

            setIdImg(data_completa);
            setNome(name);
            setSumareas(Math.round(mA*100.0)/100.0);
            setComp_Medio(Math.round(mC*100.0)/100.0);
            setComp_Desvio(Math.round(dC*100.0)/100.0);
            setLarg_Media(Math.round(mL*100.0)/100.0);
            setLarg_Desvio(Math.round(dL*100.0)/100.0);
            setPer_Media(Math.round(mP*100.0)/100.0);
            setPer_Desvio(Math.round(dP*100.0)/100.0);
            setLargComp_Desvio(Math.round(dLC*100.0)/100.0);
            setLargComp_Media(Math.round(mLC*100.0)/100.0);
            setArea_Quad(areaQuadrado);
            setRepeticao(repetition);
            setTratamento(treatment);
            setEspecie(species);
            setArea_Desvio(Math.round(dA*100.0)/100.0);
            setArea_Media(Math.round(mA*100.0)/100.0);
    }


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

    public ArrayList<Folha> getListaFolhas() {
        return ListaFolhas;
    }

    public void setListaFolhas(ArrayList<Folha> listaFolhas) {
        ListaFolhas = listaFolhas;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getSumareas() {
        return sumareas;
    }

    public void setSumareas(Double sumareas) {
        this.sumareas = sumareas;
    }

    public Float getArea_Quad() {
        return area_Quad;
    }

    public void setArea_Quad(Float area_Quad) {
        this.area_Quad = area_Quad;
    }

    public Double getLarg_Media() {
        return larg_Media;
    }

    public void setLarg_Media(Double larg_Media) {
        this.larg_Media = larg_Media;
    }

    public Double getLarg_Desvio() {
        return larg_Desvio;
    }

    public void setLarg_Desvio(Double larg_Desvio) {
        this.larg_Desvio = larg_Desvio;
    }

    public Double getComp_Medio() {
        return comp_Medio;
    }

    public void setComp_Medio(Double comp_Medio) {
        this.comp_Medio = comp_Medio;
    }

    public Double getComp_Desvio() {
        return comp_Desvio;
    }

    public void setComp_Desvio(Double comp_Desvio) {
        this.comp_Desvio = comp_Desvio;
    }

    public Double getArea_Media() {
        return area_Media;
    }

    public void setArea_Media(Double area_Media) {
        this.area_Media = area_Media;
    }

    public Double getArea_Desvio() {
        return area_Desvio;
    }

    public void setArea_Desvio(Double area_Desvio) {
        this.area_Desvio = area_Desvio;
    }

    public Double getPer_Media() {
        return per_Media;
    }

    public void setPer_Media(Double per_Media) {
        this.per_Media = per_Media;
    }

    public Double getPer_Desvio() {
        return per_Desvio;
    }

    public void setPer_Desvio(Double per_Desvio) {
        this.per_Desvio = per_Desvio;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getTratamento() {
        return tratamento;
    }

    public void setTratamento(String tratamento) {
        this.tratamento = tratamento;
    }

    public Integer getRepeticao() {
        return repeticao;
    }

    public void setRepeticao(Integer repeticao) {
        this.repeticao = repeticao;
    }

    public String getIdImg() {
        return idImg;
    }

    public void setIdImg(String idImg) {
        this.idImg = idImg;
    }

    public Double getLargComp_Desvio() {
        return largComp_Desvio;
    }

    public void setLargComp_Desvio(Double largComp_Desvio) {
        this.largComp_Desvio = largComp_Desvio;
    }

    public Double getLargComp_Media() {
        return largComp_Media;
    }

    public void setLargComp_Media(Double largComp_Media) {
        this.largComp_Media = largComp_Media;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ActCalculos> CREATOR = new Creator<ActCalculos>() {
        @Override
        public ActCalculos createFromParcel(Parcel in) {
            return new ActCalculos(in);
        }

        @Override
        public ActCalculos[] newArray(int size) {
            return new ActCalculos[size];
        }
    };
}


