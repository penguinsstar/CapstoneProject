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

/* Function Definitions */
double Calculate_DBP(double SBP0, double DBP0, double PTT0, double fPTT0,
                     double fDBP0, const double ECG[1000],
                     const double PPG[1000], double b_gamma,
                     boolean_T CalibrationMode)
{
  double DBP;
  double PTTcurrent;
  /*  CalibrationMode = 0 : mPTP */
  /*  CalibrationMode = 1 : fPTP */
  PTTcurrent = Calculate_PTT(ECG, PPG);
  if (!CalibrationMode) {
    /*  BP estimation with mPTP parameters only */
    PTTcurrent = PTT0 / PTTcurrent;
    DBP = ((SBP0 + 2.0 * DBP0) / 3.0 + 2.0 / b_gamma * log(PTTcurrent)) -
          (SBP0 - DBP0) / 3.0 * (PTTcurrent * PTTcurrent);
  } else {
    /*  BP estimation with fPTP parameters */
    /*  TODO: remove one of these 2 after testing */
    PTTcurrent = fPTT0 / PTTcurrent;
    DBP = ((SBP0 + 2.0 * fDBP0) / 3.0 + 2.0 / b_gamma * log(PTTcurrent)) -
          (SBP0 - fDBP0) / 3.0 * (PTTcurrent * PTTcurrent);
  }
  return DBP;
}

/* End of code generation (Calculate_DBP.c) */
