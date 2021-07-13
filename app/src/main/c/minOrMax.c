/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * minOrMax.c
 *
 * Code generation for function 'minOrMax'
 *
 */

/* Include files */
#include "minOrMax.h"
#include "Calculate_DBP_types.h"

/* Function Definitions */
void maximum(const emxArray_real_T *x, double *ex, int *idx)
{
  double d;
  int k;
  int last;
  last = x->size[0];
  if (x->size[0] <= 2) {
    if (x->size[0] == 1) {
      *ex = x->data[0];
      *idx = 1;
    } else if (x->data[0] < x->data[x->size[0] - 1]) {
      *ex = x->data[x->size[0] - 1];
      *idx = x->size[0];
    } else {
      *ex = x->data[0];
      *idx = 1;
    }
  } else {
    *ex = x->data[0];
    *idx = 1;
    for (k = 2; k <= last; k++) {
      d = x->data[k - 1];
      if (*ex < d) {
        *ex = d;
        *idx = k;
      }
    }
  }
}

/* End of code generation (minOrMax.c) */
