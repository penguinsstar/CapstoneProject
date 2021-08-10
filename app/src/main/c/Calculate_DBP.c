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
#include <math.h>
#include <jni.h>

/* Function Definitions */
JNIEXPORT jdouble JNICALL
Java_com_example_chironsolutions_MainActivity_00024calculations_calculate_1DBP(JNIEnv *env,
                                                                               jobject thiz,
                                                                               jdouble sbp0,
                                                                               jdouble dbp0,
                                                                               jdouble ptt0,
                                                                               jdouble pttCurrent,
                                                                               jdouble gamma)
{

  return Calculate_DBP(sbp0, dbp0, ptt0, pttCurrent, gamma);
}

double Calculate_DBP(double SBP0, double DBP0, double PTT0, double PTTcurrent,
                     double b_gamma)
{
  double a_tmp;
  if (!isInitialized_Calculate_DBP) {
    Calculate_DBP_initialize();
  }
  /*  BP estimation with mPTP parameters only */
  a_tmp = PTT0 / PTTcurrent;
  return ((SBP0 + 2.0 * DBP0) / 3.0 + 2.0 / b_gamma * log(a_tmp)) -
         (SBP0 - DBP0) / 3.0 * (a_tmp * a_tmp);
}

/* End of code generation (Calculate_DBP.c) */
