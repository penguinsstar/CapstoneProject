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
#include "omp.h"
#include <stddef.h>
#include <stdlib.h>

#ifdef __cplusplus
extern "C" {
#endif

/* Function Declarations */
extern void Calibrate(const double PTT[5], const double RealDBP[5],
                      const double RealSBP[5], double *SBP0, double *DBP0,
                      double *PTT0);

#ifdef __cplusplus
}
#endif

#endif
/* End of code generation (Calibrate.h) */
