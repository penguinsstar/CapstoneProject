package com.example.chironsolutions;

import java.util.Date;

public class UserDataModel {

    private int id;
    private double PPG;
    private double ECG;
    private double DBP;
    private double SBP;
    private long date;

    public UserDataModel(int id, double PPG, double ECG, double DBP, double SBP, long date) {
        this.id = id;
        this.PPG = PPG;
        this.ECG = ECG;
        this.DBP = DBP;
        this.SBP = SBP;
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

    public double getSBP() {
        return SBP;
    }

    public void setSBP(double SBP) { this.SBP = SBP; }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
