/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * Calibrate.h
 *
 * Code generation for function 'Calibrate'
 *
 */

#ifndef CALIBRATE_H
#define CALIBRATE_H

/* Include files */
#include "rtwtypes.h"
#include <stddef.h>
#include <stdlib.h>

#ifdef __cplusplus
extern "C" {
#endif

/* Function Declarations */
extern void Calibrate(const double ECG[10000], const double PPG[10000],
                      const double RealDBP[10], const double RealSBP[5],
                      double b_gamma, boolean_T CalibrationMode, double *SBP0,
                      double *DBP0, double *PTT0, double *fPTT0, double *fDBP0);

#ifdef __cplusplus
}
#endif

#endif
/* End of code generation (Calibrate.h) */
