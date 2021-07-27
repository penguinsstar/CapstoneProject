/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * eml_int_forloop_overflow_check.c
 *
 * Code generation for function 'eml_int_forloop_overflow_check'
 *
 */

/* Include files */
#include "eml_int_forloop_overflow_check.h"
#include "Calculate_DBP_internal_types.h"
#include <stdio.h>

/* Function Declarations */
static void c_rtErrorWithMessageID(const int b, const char *c,
                                   const char *aFcnName, int aLineNum);

/* Function Definitions */
static void c_rtErrorWithMessageID(const int b, const char *c,
                                   const char *aFcnName, int aLineNum)
{
  fprintf(stderr,
          "The loop variable of class %.*s might overflow on the last "
          "iteration of the for loop. This could lead to an infinite loop.",
          b, c);
  fprintf(stderr, "\n");
  fprintf(stderr, "Error in %s (line %d)", aFcnName, aLineNum);
  fprintf(stderr, "\n");
  fflush(stderr);
  abort();
}

void check_forloop_overflow_error(void)
{
  static rtRunTimeErrorInfo c_emlrtRTEI = {
      88,                             /* lineNo */
      9,                              /* colNo */
      "check_forloop_overflow_error", /* fName */
      "C:\\Program "
      "Files\\MATLAB\\R2021a\\toolbox\\eml\\lib\\matlab\\eml\\eml_int_forloop_"
      "overflow_check.m" /* pName */
  };
  c_rtErrorWithMessageID(5, "int32", c_emlrtRTEI.fName, c_emlrtRTEI.lineNo);
}

/* End of code generation (eml_int_forloop_overflow_check.c) */
