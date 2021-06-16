package com.example.chironsolutions;

import java.util.Date;

public class UserDataModel {

    private int id;
    private double PPG;
    private double ECG;
    private double DBP;
    private Date date;

    public UserDataModel(int id, double PPG, double ECG, double DBP, Date date) {
        this.id = id;
        this.PPG = PPG;
        this.ECG = ECG;
        this.DBP = DBP;
        this.date = date;
    }

    public UserDataModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPPG() {
        return PPG;
    }

    public void setPPG(double PPG) {
        this.PPG = PPG;
    }

    public double getECG() {
        return ECG;
    }

    public void setECG(double ECG) {
        this.ECG = ECG;
    }

    public double getDBP() {
        return DBP;
    }

    public void setDBP(double DBP) {
        this.DBP = DBP;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
