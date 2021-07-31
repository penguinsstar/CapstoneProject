/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * insertionsort.c
 *
 * Code generation for function 'insertionsort'
 *
 */

/* Include files */
#include "insertionsort.h"

/* Function Definitions */
void insertionsort(int x[22], const int cmp_workspace_a[22],
                   const int cmp_workspace_b[22])
{
  int idx;
  int k;
  int xc;
  boolean_T exitg1;
  boolean_T varargout_1;
  for (k = 0; k < 21; k++) {
    xc = x[k + 1] - 1;
    idx = k;
    exitg1 = false;
    while ((!exitg1) && (idx + 1 >= 1)) {
      if (cmp_workspace_a[xc] < cmp_workspace_a[x[idx] - 1]) {
        varargout_1 = true;
      } else if (cmp_workspace_a[xc] == cmp_workspace_a[x[idx] - 1]) {
        varargout_1 = (cmp_workspace_b[xc] < cmp_workspace_b[x[idx] - 1]);
      } else {
        varargout_1 = false;
      }
      if (varargout_1) {
        x[idx + 1] = x[idx];
        idx--;
      } else {
        exitg1 = true;
      }
    }
    x[idx + 1] = xc + 1;
  }
}

/* End of code generation (insertionsort.c) */
