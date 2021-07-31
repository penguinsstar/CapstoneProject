/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * diff.c
 *
 * Code generation for function 'diff'
 *
 */

/* Include files */
#include "diff.h"
#include "Calculate_DBP_emxutil.h"
#include "Calculate_DBP_types.h"

/* Function Definitions */
void diff(const emxArray_real_T *x, emxArray_real_T *y)
{
  double d;
  double tmp1;
  double work_data;
  int b_y;
  int dimSize;
  int i;
  int m;
  int u0;
  dimSize = x->size[1];
  if (x->size[1] == 0) {
    y->size[0] = 1;
    y->size[1] = 0;
  } else {
    u0 = x->size[1] - 1;
    if (u0 < 1) {
      b_y = u0;
    } else {
      b_y = 1;
    }
    if (b_y < 1) {
      y->size[0] = 1;
      y->size[1] = 0;
    } else {
      i = y->size[0] * y->size[1];
      y->size[0] = 1;
      y->size[1] = x->size[1] - 1;
      emxEnsureCapacity_real_T(y, i);
      if (x->size[1] - 1 != 0) {
        work_data = x->data[0];
        for (m = 2; m <= dimSize; m++) {
          tmp1 = x->data[m - 1];
          d = tmp1;
          tmp1 -= work_data;
          work_data = d;
          y->data[m - 2] = tmp1;
        }
      }
    }
  }
}

/* End of code generation (diff.c) */
