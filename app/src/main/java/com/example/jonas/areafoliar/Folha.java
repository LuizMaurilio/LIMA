package com.example.jonas.areafoliar;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Folha implements Serializable, Parcelable {

    private Double largcomp, perimetro, area, comprimento, largura;
    private int num_Folha;
    private int mImageResource;
    private int cod;
    private String idImg;

    public Folha(String idImg, Double area, Double comprimento, Double largura, int num, Double perimetro, Double largcomp, int cod) {
        this.idImg = idImg;
        this.area = area;
        this.comprimento = comprimento;
        this.largura = largura;
        this.num_Folha = num;
        this.perimetro = perimetro;
        this.largcomp = largcomp;
        this.cod = cod;
    }

    public Folha() {
        this.setCod(0);
    }

    private Folha(Parcel in) {
        idImg = in.readString();
        area = in.readDouble();
        comprimento = in.readDouble();
        largura = in.readDouble();
        perimetro = in.readDouble();
        num_Folha = in.readInt();
        largcomp = in.readDouble();
    }

    public static final Creator<Folha> CREATOR = new Creator<Folha>() {
        @Override
        public Folha createFromParcel(Parcel in) {
            return new Folha(in);
        }

        @Override
        public Folha[] newArray(int size) {
            return new Folha[size];
        }
    };

    public String getIdImg() {
        return idImg;
    }

    public void setIdImg(String idImg) {
        this.idImg = idImg;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Double getComprimento() {
        return comprimento;
    }

    public void setComprimento(Double comprimento) {
        this.comprimento = comprimento;
    }

    public Double getLargura() {
        return largura;
    }

    public void setLargura(Double largura) {
        this.largura = largura;
    }

    public int getNum_Folha() {
        return num_Folha;
    }

    public void setNum_Folha(int num) {
        this.num_Folha = num;
    }

    public Double getPerimetro() {
        return perimetro;
    }

    public void setPerimetro(Double perimetro) {
        this.perimetro = perimetro;
    }

    public int getmImageResource() {
        return mImageResource;
    }

    public void setmImageResource(int mImageResource) {
        this.mImageResource = mImageResource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idImg);
    }

    public Double getLargcomp() {
        return largcomp;
    }

    public void setLargcomp(Double largcomp) {
        this.largcomp = largcomp;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }
}
