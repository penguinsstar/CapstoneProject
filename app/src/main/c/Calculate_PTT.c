/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * Calculate_PTT.c
 *
 * Code generation for function 'Calculate_PTT'
 *
 */

/* Include files */
#include "Calculate_PTT.h"
#include "Calculate_DBP_emxutil.h"
#include "Calculate_DBP_types.h"
#include "diff.h"
#include "mean.h"
#include "minOrMax.h"
#include <math.h>
#include <string.h>

/* Function Definitions */
double Calculate_PTT(const double ECG[1000], const double PPG[1000])
{
  static short ECG_peaks_initial[998001];
  emxArray_real_T *ECG_ensembles;
  emxArray_real_T *PPG_ensembles;
  emxArray_real_T *b_ECG_peaks_initial;
  emxArray_real_T *r;
  emxArray_real_T *x;
  double bsum;
  double l_limit;
  double ptt;
  double window_size;
  int hi;
  int ib;
  int j;
  int k;
  int lastBlockLength;
  int loop_ub;
  int nblocks;
  j = 0;
  memset(&ECG_peaks_initial[0], 0, 998001U * sizeof(short));
  for (lastBlockLength = 0; lastBlockLength < 998; lastBlockLength++) {
    l_limit = ECG[lastBlockLength + 1];
    if ((l_limit > ECG[lastBlockLength]) &&
        (l_limit >= ECG[lastBlockLength + 2])) {
      bsum = ECG[0];
      for (k = 0; k < 999; k++) {
        window_size = ECG[k + 1];
        if (bsum < window_size) {
          bsum = window_size;
        }
      }
      if (l_limit > 0.45 * bsum) {
        ECG_peaks_initial[j] = (short)(lastBlockLength + 2);
        j++;
      }
    }
  }
  emxInit_real_T(&b_ECG_peaks_initial, 2);
  if (1 > j) {
    loop_ub = 0;
  } else {
    loop_ub = j;
  }
  /*  split waveforms into single beats */
  nblocks = b_ECG_peaks_initial->size[0] * b_ECG_peaks_initial->size[1];
  b_ECG_peaks_initial->size[0] = 1;
  b_ECG_peaks_initial->size[1] = loop_ub;
  emxEnsureCapacity_real_T(b_ECG_peaks_initial, nblocks);
  for (nblocks = 0; nblocks < loop_ub; nblocks++) {
    b_ECG_peaks_initial->data[nblocks] = ECG_peaks_initial[nblocks];
  }
  emxInit_real_T(&x, 2);
  diff(b_ECG_peaks_initial, x);
  emxFree_real_T(&b_ECG_peaks_initial);
  if (x->size[1] == 0) {
    l_limit = 0.0;
  } else {
    if (x->size[1] <= 1024) {
      j = x->size[1];
      lastBlockLength = 0;
      nblocks = 1;
    } else {
      j = 1024;
      nblocks = x->size[1] / 1024;
      lastBlockLength = x->size[1] - (nblocks << 10);
      if (lastBlockLength > 0) {
        nblocks++;
      } else {
        lastBlockLength = 1024;
      }
    }
    l_limit = x->data[0];
    for (k = 2; k <= j; k++) {
      l_limit += x->data[k - 1];
    }
    for (ib = 2; ib <= nblocks; ib++) {
      j = (ib - 1) << 10;
      bsum = x->data[j];
      if (ib == nblocks) {
        hi = lastBlockLength;
      } else {
        hi = 1024;
      }
      for (k = 2; k <= hi; k++) {
        bsum += x->data[(j + k) - 1];
      }
      l_limit += bsum;
    }
  }
  window_size = floor(l_limit / (double)x->size[1]);
  /*  window size defined as the average distance between peaks */
  emxFree_real_T(&x);
  if (window_size == 0.0) {
    l_limit = 0.0;
  } else {
    l_limit = fmod(window_size, 2.0);
    if (l_limit == 0.0) {
      l_limit = 0.0;
    } else if (window_size < 0.0) {
      l_limit += 2.0;
    }
  }
  if (l_limit != 0.0) {
    window_size--;
  }
  emxInit_real_T(&ECG_ensembles, 2);
  nblocks = ECG_ensembles->size[0] * ECG_ensembles->size[1];
  ECG_ensembles->size[0] = (int)window_size;
  ECG_ensembles->size[1] = loop_ub - 1;
  emxEnsureCapacity_real_T(ECG_ensembles, nblocks);
  hi = (int)window_size * (loop_ub - 1);
  for (nblocks = 0; nblocks < hi; nblocks++) {
    ECG_ensembles->data[nblocks] = 0.0;
  }
  emxInit_real_T(&PPG_ensembles, 2);
  nblocks = PPG_ensembles->size[0] * PPG_ensembles->size[1];
  PPG_ensembles->size[0] = (int)window_size;
  PPG_ensembles->size[1] = loop_ub - 1;
  emxEnsureCapacity_real_T(PPG_ensembles, nblocks);
  hi = (int)window_size * (loop_ub - 1);
  for (nblocks = 0; nblocks < hi; nblocks++) {
    PPG_ensembles->data[nblocks] = 0.0;
  }
  /*  split ECG waveform around peaks */
  nblocks = loop_ub - 2;
  for (lastBlockLength = 0; lastBlockLength <= nblocks; lastBlockLength++) {
    j = ECG_peaks_initial[lastBlockLength + 1];
    l_limit = fmax(1.0, (double)j - window_size / 2.0);
    /* ensure minimum index = 1 */
    if (l_limit == 1.0) {
      bsum = window_size;
    } else {
      bsum = fmin(1000.0, ((double)j + window_size / 2.0) - 1.0);
      /* ensure maximum index = length(ECG) */
      if (bsum == 1000.0) {
        l_limit = (1000.0 - window_size) + 1.0;
      }
    }
    if (l_limit > bsum) {
      j = 0;
      hi = 0;
    } else {
      j = (int)l_limit - 1;
      hi = (int)bsum;
    }
    loop_ub = hi - j;
    for (hi = 0; hi < loop_ub; hi++) {
      ECG_ensembles->data[hi + ECG_ensembles->size[0] * lastBlockLength] =
          ECG[j + hi];
    }
    if (l_limit > bsum) {
      j = 0;
      hi = 0;
    } else {
      j = (int)l_limit - 1;
      hi = (int)bsum;
    }
    loop_ub = hi - j;
    for (hi = 0; hi < loop_ub; hi++) {
      PPG_ensembles->data[hi + PPG_ensembles->size[0] * lastBlockLength] =
          PPG[j + hi];
    }
  }
  emxInit_real_T(&r, 1);
  /*  calculate ensemble average for ECG and PPG signals */
  mean(PPG_ensembles, r);
  maximum(r, &bsum, &j);
  mean(ECG_ensembles, r);
  maximum(r, &bsum, &hi);
  ptt = ((double)j - (double)hi) / 100.0;
  emxFree_real_T(&r);
  emxFree_real_T(&PPG_ensembles);
  emxFree_real_T(&ECG_ensembles);
  return ptt;
}

/* End of code generation (Calculate_PTT.c) */
