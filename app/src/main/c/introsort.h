/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * introsort.h
 *
 * Code generation for function 'introsort'
 *
 */

#ifndef INTROSORT_H
#define INTROSORT_H

/* Include files */
#include "rtwtypes.h"
#include "omp.h"
#include <stddef.h>
#include <stdlib.h>

#ifdef __cplusplus
extern "C" {
#endif

/* Function Declarations */
void introsort(int x[22], const int cmp_workspace_a[22],
               const int cmp_workspace_b[22]);

#ifdef __cplusplus
}
#endif

#endif
/* End of code generation (introsort.h) */
