/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * filter.c
 *
 * Code generation for function 'filter'
 *
 */

/* Include files */
#include "filter.h"
#include <string.h>

/* Function Definitions */
void filter(double b_data[], double a_data[], const double x_data[],
            const double zi_data[], double y_data[], int *y_size)
{
  double a1;
  double as;
  int b_j;
  int b_k;
  int b_y_tmp;
  int c_k;
  int j;
  int k;
  int naxpy;
  int y_tmp;
  a1 = a_data[0];
  if ((a_data[0] != 0.0) && (a_data[0] != 1.0)) {
    for (k = 0; k < 9; k++) {
      b_data[k] /= a1;
    }
    for (b_k = 0; b_k < 8; b_k++) {
      a_data[b_k + 1] /= a1;
    }
    a_data[0] = 1.0;
  }
  *y_size = 1048;
  memcpy(&y_data[0], &zi_data[0], 8U * sizeof(double));
  memset(&y_data[8], 0, 1040U * sizeof(double));
  for (c_k = 0; c_k < 1048; c_k++) {
    if (1048 - c_k < 9) {
      naxpy = 1047 - c_k;
    } else {
      naxpy = 8;
    }
    for (j = 0; j <= naxpy; j++) {
      y_tmp = c_k + j;
      y_data[y_tmp] += x_data[c_k] * b_data[j];
    }
    if (1047 - c_k < 8) {
      naxpy = 1046 - c_k;
    } else {
      naxpy = 7;
    }
    as = -y_data[c_k];
    for (b_j = 0; b_j <= naxpy; b_j++) {
      b_y_tmp = (c_k + b_j) + 1;
      y_data[b_y_tmp] += as * a_data[b_j + 1];
    }
  }
}

/* End of code generation (filter.c) */
