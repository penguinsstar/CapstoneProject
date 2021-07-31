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
#include "Calculate_DBP_data.h"
#include "Calculate_DBP_initialize.h"
#include "Calculate_PTT.h"
#include <math.h>
#include <stdbool.h>
#include <jni.h>

/* Function Definitions */
JNIEXPORT jdoubleArray JNICALL
Java_com_example_chironsolutions_MainActivity_00024calculations_calibrate(JNIEnv *env, jobject thiz,
                                                                          jdoubleArray ecg,
                                                                          jdoubleArray ppg,
                                                                          jdoubleArray real_dbp,
                                                                          jdoubleArray real_sbp) {
  double SBP0 = 0;
  double DBP0 = 0;
  double PTT0 = 0;
  double* ecgPointer  = (*env)->GetDoubleArrayElements(env, ecg, 0);
  double* ppgPointer  = (*env)->GetDoubleArrayElements(env, ppg, 0);
  double* real_dbpPointer  = (*env)->GetDoubleArrayElements(env, real_dbp, 0);
  double* real_sbpPointer  = (*env)->GetDoubleArrayElements(env, real_sbp, 0);

  Calibrate(ecgPointer, ppgPointer, real_dbpPointer, real_sbpPointer, &SBP0, &DBP0, &PTT0);

  double calibrationValues[3];
  calibrationValues[0] = SBP0;
  calibrationValues[1] = DBP0;
  calibrationValues[2] = PTT0;

  jdoubleArray values = (*env)->NewDoubleArray(env, 3);
  (*env)->SetDoubleArrayRegion(env, values , 0, 3, calibrationValues);

  return values;
}

void Calibrate(const double ECG[5000], const double PPG[5000],
               const double RealDBP[5], const double RealSBP[5], double *SBP0,
               double *DBP0, double *PTT0)
{
  double PTT[5];
  int c;
  if (!isInitialized_Calculate_DBP) {
    Calculate_DBP_initialize();
  }
  /*  CalibrationMode = 0 : mPTP */
  /*  CalibrationMode = 1 : fPTP */
  for (c = 0; c < 5; c++) {
    /* Getting 5 median PTT values from 30s intervals */
    PTT[c] = Calculate_PTT(*(double(*)[1000]) & ECG[1000 * c],
                           *(double(*)[1000]) & PPG[1000 * c]);
  }
  /* Average of the 5 ensembled PTTs and cuff based BP (PTT BAR) */
  *PTT0 = ((((PTT[0] + PTT[1]) + PTT[2]) + PTT[3]) + PTT[4]) / 5.0;
  *SBP0 =
      ((((RealSBP[0] + RealSBP[1]) + RealSBP[2]) + RealSBP[3]) + RealSBP[4]) /
      5.0;
  *DBP0 =
      ((((RealDBP[0] + RealDBP[1]) + RealDBP[2]) + RealDBP[3]) + RealDBP[4]) /
      5.0;
  /*     %% Calculating various parameters required for the model (mPTP) */
}

/* End of code generation (Calibrate.c) */
