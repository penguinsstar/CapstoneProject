/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * Calibrate.c
 *
 * Code generation for function 'Calibrate'
 *
 */

/* Include files */
#include "Calibrate.h"
#include "Calculate_PTT.h"
#include <math.h>
#include <stdbool.h>
#include <jni.h>

/* Function Definitions */
JNIEXPORT jobject JNICALL
Java_com_example_chironsolutions_MainActivity_00024calculations_calibrate(JNIEnv *env, jobject thiz,
                                                                          jdoubleArray ecg,
                                                                          jdoubleArray ppg,
                                                                          jdoubleArray real_dbp,
                                                                          jdoubleArray real_sbp,
                                                                          jboolean gamma,
                                                                          jboolean calibration_mode) {
    double SBP0;
    double DBP0;
    double PTT0;
    double fPTT0;
    double fDBP0;
    Calibrate(ecg, ppg, real_dbp, real_sbp, gamma, calibration_mode, &SBP0, &DBP0, &PTT0, &fPTT0, &fDBP0);
    double calibrationValues[5];
    calibrationValues[1] = SBP0;
    calibrationValues[2] = DBP0;
    calibrationValues[3] = PTT0;
    calibrationValues[4] = fPTT0;
    calibrationValues[5] = fDBP0;
    return calibrationValues;
}

void Calibrate(const double ECG[10000], const double PPG[10000],
               const double RealDBP[10], const double RealSBP[5],
               double b_gamma, boolean_T CalibrationMode,
               double *SBP0, double *DBP0, double *PTT0, double *fPTT0, double *fDBP0)
{
  double EstDBP[5];
  double PTTcurrent[5];
  double y[5];
  double a_tmp;
  double alphaPTT_tmp_tmp;
  double b_y;
  int c;
  int i;
  /*  CalibrationMode = 0 : mPTP */
  /*  CalibrationMode = 1 : fPTP */
  for (c = 0; c < 5; c++) {
    /* Getting 5 median PTT values from 30s intervals */
    EstDBP[c] = Calculate_PTT(*(double(*)[1000]) & ECG[1000 * c],
                              *(double(*)[1000]) & PPG[1000 * c]);
  }
  /* Average of the 5 ensembled PTTs and cuff based BP (PTT BAR) */
  *PTT0 =
      ((((EstDBP[0] + EstDBP[1]) + EstDBP[2]) + EstDBP[3]) + EstDBP[4]) / 5.0;
  *SBP0 =
      ((((RealSBP[0] + RealSBP[1]) + RealSBP[2]) + RealSBP[3]) + RealSBP[4]) /
      5.0;
  *DBP0 =
      ((((RealDBP[0] + RealDBP[1]) + RealDBP[2]) + RealDBP[3]) + RealDBP[4]) /
      5.0;
  /*     %% Calculating various parameters required for the model (mPTP) */
  *fPTT0 = 0.0;
  *fDBP0 = 0.0;
  if (CalibrationMode) {
    /*         %% fPTP calibration */
    /* Calculating Penalty Factor (alpha) */
    for (c = 0; c < 5; c++) {
      a_tmp = EstDBP[c] - *PTT0;
      EstDBP[c] = a_tmp;
      y[c] = fabs(a_tmp);
    }
    *PTT0 *= 1.0 -
             ((((EstDBP[0] + EstDBP[1]) + EstDBP[2]) + EstDBP[3]) + EstDBP[4]) /
                 (5.0 * ((((y[0] + y[1]) + y[2]) + y[3]) + y[4]));
    /* Calculating the adjusted DBP initial value */
    /*  need to collect cuff BP and estimate BP values using mPTP calibration */
    for (c = 0; c < 5; c++) {
      i = 1000 * (c + 5);
      a_tmp = Calculate_PTT(*(double(*)[1000]) & ECG[i],
                            *(double(*)[1000]) & PPG[i]);
      PTTcurrent[c] = a_tmp;
      /* base values already calculated from the callibration */
      a_tmp = *PTT0 / a_tmp;
      a_tmp = (((*SBP0 + 2.0 * *DBP0) / 3.0 + 2.0 / b_gamma * log(a_tmp)) -
               (*SBP0 - *DBP0) / 3.0 * (a_tmp * a_tmp)) -
              RealDBP[c + 5];
      EstDBP[c] = a_tmp;
      y[c] = fabs(a_tmp);
    }
    b_y = (((y[0] + y[1]) + y[2]) + y[3]) + y[4];
    alphaPTT_tmp_tmp =
        ((((PTTcurrent[0] + PTTcurrent[1]) + PTTcurrent[2]) + PTTcurrent[3]) +
         PTTcurrent[4]) /
        5.0;
    for (c = 0; c < 5; c++) {
      a_tmp = PTTcurrent[c] - alphaPTT_tmp_tmp;
      PTTcurrent[c] = a_tmp;
      y[c] = fabs(a_tmp);
    }
    *fPTT0 =
        alphaPTT_tmp_tmp *
        (1.0 -
         ((((PTTcurrent[0] + PTTcurrent[1]) + PTTcurrent[2]) + PTTcurrent[3]) +
          PTTcurrent[4]) /
             (5.0 * ((((y[0] + y[1]) + y[2]) + y[3]) + y[4])));
    *fDBP0 =
        *DBP0 * (1.0 - ((((EstDBP[0] + EstDBP[1]) + EstDBP[2]) + EstDBP[3]) +
                        EstDBP[4]) /
                           (5.0 * b_y));
  }
}

/* End of code generation (Calibrate.c) */
