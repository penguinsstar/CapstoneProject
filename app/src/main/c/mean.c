/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * mean.c
 *
 * Code generation for function 'mean'
 *
 */

/* Include files */
#include "mean.h"
#include "Calculate_DBP_emxutil.h"
#include "Calculate_DBP_types.h"

/* Function Definitions */
void mean(const emxArray_real_T *x, emxArray_real_T *y)
{
  emxArray_real_T *bsum;
  int b_k;
  int b_loop_ub;
  int b_xj;
  int bvstride;
  int c_xj;
  int d_xj;
  int e_xj;
  int firstBlockLength;
  int hi;
  int i;
  int i1;
  int i2;
  int i3;
  int i4;
  int ib;
  int k;
  int lastBlockLength;
  int loop_ub;
  int nblocks;
  int vstride;
  int xblockoffset;
  int xj;
  int xoffset;
  if ((x->size[0] == 0) || (x->size[1] == 0)) {
    i = y->size[0];
    y->size[0] = x->size[0];
    emxEnsureCapacity_real_T(y, i);
    loop_ub = x->size[0];
    for (i2 = 0; i2 < loop_ub; i2++) {
      y->data[i2] = 0.0;
    }
  } else {
    emxInit_real_T(&bsum, 1);
    vstride = x->size[0] - 1;
    bvstride = x->size[0] << 10;
    i1 = y->size[0];
    y->size[0] = x->size[0];
    emxEnsureCapacity_real_T(y, i1);
    i3 = bsum->size[0];
    bsum->size[0] = x->size[0];
    emxEnsureCapacity_real_T(bsum, i3);
    if (x->size[1] <= 1024) {
      firstBlockLength = x->size[1];
      lastBlockLength = 0;
      nblocks = 1;
    } else {
      firstBlockLength = 1024;
      nblocks = x->size[1] / 1024;
      lastBlockLength = x->size[1] - (nblocks << 10);
      if (lastBlockLength > 0) {
        nblocks++;
      } else {
        lastBlockLength = 1024;
      }
    }
    for (xj = 0; xj <= vstride; xj++) {
      y->data[xj] = x->data[xj];
      bsum->data[xj] = 0.0;
    }
    for (k = 2; k <= firstBlockLength; k++) {
      xoffset = (k - 1) * (vstride + 1);
      for (b_xj = 0; b_xj <= vstride; b_xj++) {
        y->data[b_xj] += x->data[xoffset + b_xj];
      }
    }
    for (ib = 2; ib <= nblocks; ib++) {
      xblockoffset = (ib - 1) * bvstride;
      for (c_xj = 0; c_xj <= vstride; c_xj++) {
        bsum->data[c_xj] = x->data[xblockoffset + c_xj];
      }
      if (ib == nblocks) {
        hi = lastBlockLength;
      } else {
        hi = 1024;
      }
      for (b_k = 2; b_k <= hi; b_k++) {
        xoffset = xblockoffset + (b_k - 1) * (vstride + 1);
        for (e_xj = 0; e_xj <= vstride; e_xj++) {
          bsum->data[e_xj] += x->data[xoffset + e_xj];
        }
      }
      for (d_xj = 0; d_xj <= vstride; d_xj++) {
        y->data[d_xj] += bsum->data[d_xj];
      }
    }
    emxFree_real_T(&bsum);
  }
  b_loop_ub = y->size[0];
  for (i4 = 0; i4 < b_loop_ub; i4++) {
    y->data[i4] /= (double)x->size[1];
  }
}

/* End of code generation (mean.c) */
