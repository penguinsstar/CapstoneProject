/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * normalize.c
 *
 * Code generation for function 'normalize'
 *
 */

/* Include files */
#include "normalize.h"
#include <math.h>

/* Function Definitions */
void normalize(const double a[1000], double n[1000])
{
  double b;
  double b_b;
  double d;
  double s;
  double scale;
  double t;
  double xbar;
  double y;
  double y_tmp;
  int b_k;
  int c_k;
  int counts;
  int k;
  y = a[0];
  counts = 1;
  xbar = a[0];
  for (k = 0; k < 999; k++) {
    y_tmp = a[k + 1];
    y += y_tmp;
    counts++;
    xbar += y_tmp;
  }
  b = y / (double)counts;
  xbar /= 1000.0;
  s = 0.0;
  scale = 3.3121686421112381E-170;
  for (b_k = 0; b_k < 1000; b_k++) {
    d = fabs(a[b_k] - xbar);
    if (d > scale) {
      t = scale / d;
      s = s * t * t + 1.0;
      scale = d;
    } else {
      t = d / scale;
      s += t * t;
    }
  }
  s = scale * sqrt(s);
  b_b = s / 31.606961258558215;
  for (c_k = 0; c_k < 1000; c_k++) {
    n[c_k] = (a[c_k] - b) / b_b;
  }
}

/* End of code generation (normalize.c) */
