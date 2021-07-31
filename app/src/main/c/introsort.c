/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * introsort.c
 *
 * Code generation for function 'introsort'
 *
 */

/* Include files */
#include "introsort.h"
#include "insertionsort.h"

/* Function Definitions */
void introsort(int x[22], const int cmp_workspace_a[22],
               const int cmp_workspace_b[22])
{
  insertionsort(x, cmp_workspace_a, cmp_workspace_b);
}

/* End of code generation (introsort.c) */
