/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * filtfilt.c
 *
 * Code generation for function 'filtfilt'
 *
 */

/* Include files */
#include "filtfilt.h"
#include "Calculate_DBP_emxutil.h"
#include "Calculate_DBP_types.h"
#include "filter.h"
#include "introsort.h"
#include "cs.h"
#include "makeCXSparseMatrix.h"
#include "solve_from_lu.h"
#include "solve_from_qr.h"
#include <string.h>

/* Function Definitions */
void filtfilt(const double x[1000], double y_data[], int *y_size)
{
  static const double a[9] = {1.0,
                              -7.72320578489849,
                              26.10796599243734,
                              -50.455523228091693,
                              60.970758589387245,
                              -47.174893742460888,
                              22.82319495276451,
                              -6.3124823148159628,
                              0.76418553701778247};
  static const double b[9] = {
      0.86382016209025225, -6.9048986521773594, 24.153000875315268,
      -48.289038215903119, 60.35423166135125,   -48.289038215903112,
      24.153000875315268,  -6.9048986521773594, 0.86382016209025225};
  static const int cidxInt[22] = {1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 4,
                                  5, 6, 7, 8, 2, 3, 4, 5, 6, 7, 8};
  static const int ridxInt[22] = {1, 2, 3, 4, 5, 6, 7, 8, 2, 3, 4,
                                  5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7};
  cs_di *b_cxA;
  cs_di *cxA;
  cs_din *N;
  cs_din *b_N;
  cs_dis *S;
  cs_dis *b_S;
  emxArray_int32_T *y_colidx;
  emxArray_int32_T *y_rowidx;
  emxArray_real_T *y_d;
  double b_ytemp_data[1048];
  double c_ytemp_data[1048];
  double ytemp_data[1048];
  double yf[22];
  double a2_data[9];
  double b2_data[9];
  double b_a2_data[9];
  double b_b2_data[9];
  double rhs[8];
  double rhs_data[8];
  double d;
  double d1;
  double d2;
  double tol;
  double val;
  int sortedIndices[22];
  int b_c;
  int b_k;
  int c;
  int c_k;
  int cptr;
  int currRowIdx;
  int exitg1;
  int i;
  int i1;
  int i10;
  int i11;
  int i12;
  int i13;
  int i2;
  int i3;
  int i4;
  int i5;
  int i6;
  int i7;
  int i8;
  int i9;
  int idx;
  int k;
  int ridx;
  int ytemp_size;
  signed char b_cidxInt[22];
  signed char b_ridxInt[22];
  memcpy(&b2_data[0], &b[0], 9U * sizeof(double));
  memcpy(&a2_data[0], &a[0], 9U * sizeof(double));
  yf[0] = a2_data[1] + 1.0;
  for (i = 0; i < 7; i++) {
    yf[i + 1] = a2_data[i + 2];
    yf[i + 8] = 1.0;
    yf[i + 15] = -1.0;
  }
  for (i1 = 0; i1 < 8; i1++) {
    rhs[i1] = b2_data[i1 + 1] - b2_data[0] * a2_data[i1 + 1];
  }
  emxInit_real_T(&y_d, 1);
  emxInit_int32_T(&y_colidx, 1);
  emxInit_int32_T(&y_rowidx, 1);
  for (k = 0; k < 22; k++) {
    sortedIndices[k] = k + 1;
  }
  introsort(sortedIndices, cidxInt, ridxInt);
  i2 = y_d->size[0];
  y_d->size[0] = 22;
  emxEnsureCapacity_real_T(y_d, i2);
  i3 = y_colidx->size[0];
  y_colidx->size[0] = 9;
  emxEnsureCapacity_int32_T(y_colidx, i3);
  y_colidx->data[0] = 1;
  i4 = y_rowidx->size[0];
  y_rowidx->size[0] = 22;
  emxEnsureCapacity_int32_T(y_rowidx, i4);
  for (b_k = 0; b_k < 22; b_k++) {
    i5 = sortedIndices[b_k];
    b_cidxInt[b_k] = (signed char)cidxInt[i5 - 1];
    b_ridxInt[b_k] = (signed char)ridxInt[i5 - 1];
    y_d->data[b_k] = 0.0;
    y_rowidx->data[b_k] = 0;
  }
  cptr = 0;
  for (c = 0; c < 8; c++) {
    while ((cptr + 1 <= 22) && (b_cidxInt[cptr] == c + 1)) {
      y_rowidx->data[cptr] = b_ridxInt[cptr];
      cptr++;
    }
    y_colidx->data[c + 1] = cptr + 1;
  }
  for (c_k = 0; c_k < 22; c_k++) {
    y_d->data[c_k] = yf[sortedIndices[c_k] - 1];
  }
  idx = 1;
  for (b_c = 0; b_c < 8; b_c++) {
    ridx = y_colidx->data[b_c];
    y_colidx->data[b_c] = idx;
    do {
      exitg1 = 0;
      i6 = y_colidx->data[b_c + 1];
      if (ridx < i6) {
        val = 0.0;
        currRowIdx = y_rowidx->data[ridx - 1];
        while ((ridx < i6) && (y_rowidx->data[ridx - 1] == currRowIdx)) {
          val += y_d->data[ridx - 1];
          ridx++;
        }
        if (val != 0.0) {
          y_d->data[idx - 1] = val;
          y_rowidx->data[idx - 1] = currRowIdx;
          idx++;
        }
      } else {
        exitg1 = 1;
      }
    } while (exitg1 == 0);
  }
  y_colidx->data[8] = idx;
  cxA = makeCXSparseMatrix(y_colidx->data[8] - 1, 8, 8, &y_colidx->data[0],
                           &y_rowidx->data[0], &y_d->data[0]);
  S = cs_di_sqr(2, cxA, 0);
  N = cs_di_lu(cxA, S, 1);
  cs_di_spfree(cxA);
  if (N == NULL) {
    cs_di_sfree(S);
    cs_di_nfree(N);
    b_cxA = makeCXSparseMatrix(y_colidx->data[8] - 1, 8, 8, &y_colidx->data[0],
                               &y_rowidx->data[0], &y_d->data[0]);
    b_S = cs_di_sqr(2, b_cxA, 1);
    b_N = cs_di_qr(b_cxA, b_S);
    cs_di_spfree(b_cxA);
    qr_rank_di(b_N, &tol);
    solve_from_qr_di(b_N, b_S, (double *)&rhs[0], 8, 8);
    cs_di_sfree(b_S);
    cs_di_nfree(b_N);
  } else {
    solve_from_lu_di(N, S, (double *)&rhs[0], 8);
    cs_di_sfree(S);
    cs_di_nfree(N);
  }
  emxFree_int32_T(&y_rowidx);
  emxFree_int32_T(&y_colidx);
  emxFree_real_T(&y_d);
  d = 2.0 * x[0];
  d1 = 2.0 * x[999];
  for (i7 = 0; i7 < 24; i7++) {
    ytemp_data[i7] = d - x[24 - i7];
  }
  memcpy(&ytemp_data[24], &x[0], 1000U * sizeof(double));
  for (i8 = 0; i8 < 24; i8++) {
    ytemp_data[i8 + 1024] = d1 - x[998 - i8];
  }
  memcpy(&b_b2_data[0], &b2_data[0], 9U * sizeof(double));
  memcpy(&b_a2_data[0], &a2_data[0], 9U * sizeof(double));
  memcpy(&b_ytemp_data[0], &ytemp_data[0], 1048U * sizeof(double));
  for (i9 = 0; i9 < 8; i9++) {
    rhs_data[i9] = rhs[i9] * ytemp_data[0];
  }
  filter(b_b2_data, b_a2_data, b_ytemp_data, rhs_data, ytemp_data, &ytemp_size);
  for (i10 = 0; i10 < 1048; i10++) {
    c_ytemp_data[i10] = ytemp_data[1047 - i10];
  }
  for (i11 = 0; i11 < 1048; i11++) {
    d2 = c_ytemp_data[i11];
    ytemp_data[i11] = d2;
    b_ytemp_data[i11] = d2;
  }
  for (i12 = 0; i12 < 8; i12++) {
    rhs_data[i12] = rhs[i12] * ytemp_data[0];
  }
  filter(b2_data, a2_data, b_ytemp_data, rhs_data, ytemp_data, &ytemp_size);
  *y_size = 1000;
  for (i13 = 0; i13 < 1000; i13++) {
    y_data[i13] = ytemp_data[1023 - i13];
  }
}

/* End of code generation (filtfilt.c) */
