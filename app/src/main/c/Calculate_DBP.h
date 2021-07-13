/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * Calculate_DBP.h
 *
 * Code generation for function 'Calculate_DBP'
 *
 */

#ifndef CALCULATE_DBP_H
#define CALCULATE_DBP_H

/* Include files */
#include "rtwtypes.h"
#include <stddef.h>
#include <stdlib.h>

#ifdef __cplusplus
extern "C" {
#endif

/* Function Declarations */
extern double Calculate_DBP(double SBP0, double DBP0, double PTT0, double fPTT0,
                            double fDBP0, const double ECG[1000],
                            const double PPG[1000], double b_gamma,
                            boolean_T CalibrationMode);

#ifdef __cplusplus
}
#endif

#endif
/* End of code generation (Calculate_DBP.h) */
