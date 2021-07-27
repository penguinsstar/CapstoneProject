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
  double a_tmp;
  /*  CalibrationMode = 0 : mPTP */
  /*  CalibrationMode = 1 : fPTP */
  /*  BP estimation with mPTP parameters only */
  a_tmp = PTT0 / Calculate_PTT(ECG, PPG);
  return ((SBP0 + 2.0 * DBP0) / 3.0 + 2.0 / b_gamma * log(a_tmp)) -
         (SBP0 - DBP0) / 3.0 * (a_tmp * a_tmp);
}

/* End of code generation (Calculate_DBP.c) */
