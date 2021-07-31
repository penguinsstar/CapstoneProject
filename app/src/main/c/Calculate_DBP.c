/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * Calculate_DBP.c
 *
 * Code generation for function 'Calculate_DBP'
 *
 */

/* Include files */
#include "Calculate_DBP.h"
#include "Calculate_DBP_data.h"
#include "Calculate_DBP_initialize.h"
#include "Calculate_PTT.h"
#include <math.h>
#include <stdbool.h>
#include <jni.h>

/* Function Definitions */
JNIEXPORT jdouble JNICALL
Java_com_example_chironsolutions_MainActivity_00024calculations_calculate_1DBP(JNIEnv *env,
                                                                               jobject thiz,
                                                                               jdouble sbp0,
                                                                               jdouble dbp0,
                                                                               jdouble ptt0,
                                                                               jdoubleArray ecg,
                                                                               jdoubleArray ppg,
                                                                               jdouble gamma)
{
    double* ecgPointer  = (*env)->GetDoubleArrayElements(env, ecg, 0);
    double* ppgPointer  = (*env)->GetDoubleArrayElements(env, ppg, 0);
  return Calculate_DBP(sbp0, dbp0, ptt0, ecgPointer, ppgPointer, gamma);
}

double Calculate_DBP(double SBP0, double DBP0, double PTT0,
                     const double ECG[1000], const double PPG[1000],
                     double b_gamma)
{
  double PTTcurrent;
  double a;
  if (!isInitialized_Calculate_DBP) {
    Calculate_DBP_initialize();
  }
  /*  CalibrationMode = 0 : mPTP */
  /*  CalibrationMode = 1 : fPTP */
  PTTcurrent = Calculate_PTT(ECG, PPG);
  /*  BP estimation with mPTP parameters only */
  a = PTT0 / PTTcurrent;
  return ((SBP0 + 2.0 * DBP0) / 3.0 + 2.0 / b_gamma * log(PTT0 / PTTcurrent)) -
         (SBP0 - DBP0) / 3.0 * (a * a);
}

/* End of code generation (Calculate_DBP.c) */
