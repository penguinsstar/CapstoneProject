/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * Calculate_DBP_internal_types.h
 *
 * Code generation for function 'Calculate_DBP'
 *
 */

#ifndef CALCULATE_DBP_INTERNAL_TYPES_H
#define CALCULATE_DBP_INTERNAL_TYPES_H

/* Include files */
#include "Calculate_DBP_types.h"
#include "rtwtypes.h"

/* Type Definitions */
#ifndef typedef_rtRunTimeErrorInfo
#define typedef_rtRunTimeErrorInfo
typedef struct {
  int lineNo;
  int colNo;
  const char *fName;
  const char *pName;
} rtRunTimeErrorInfo;
#endif /* typedef_rtRunTimeErrorInfo */

#endif
/* End of code generation (Calculate_DBP_internal_types.h) */
