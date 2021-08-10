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
#include "Calculate_DBP_data.h"
#include "Calculate_DBP_emxutil.h"
#include "Calculate_DBP_initialize.h"
#include "Calculate_DBP_types.h"
#include "Data_Processing.h"
#include "diff.h"
#include "mean.h"
#include "minOrMax.h"
#include <math.h>
#include <string.h>
#include <jni.h>

/* Function Definitions */
JNIEXPORT jdouble JNICALL
Java_com_example_chironsolutions_MainActivity_00024calculations_calculate_1PTT(JNIEnv *env,
                                                                               jobject thiz,
                                                                               jdoubleArray ecg,
                                                                               jdoubleArray ppg)
{
  double* ecgPointer  = (*env)->GetDoubleArrayElements(env, ecg, 0);
  double* ppgPointer  = (*env)->GetDoubleArrayElements(env, ppg, 0);
  return Calculate_PTT(ecgPointer, ppgPointer);
}

double Calculate_PTT(const double ECG[1000], const double PPG[1000])
{
  static short ECG_peaks_initial[998001];
  emxArray_real_T *ECG_ensembles;
  emxArray_real_T *PPG_ensembles;
  emxArray_real_T *b_ECG_peaks_initial;
  emxArray_real_T *b_r;
  emxArray_real_T *r1;
  emxArray_real_T *x;
  double b_ECG[1000];
  double b_PPG[1000];
  double PTT;
  double a__1;
  double a__2;
  double bsum;
  double d;
  double d1;
  double ex;
  double l_limit;
  double r;
  double u_limit;
  double window_size;
  double y;
  int b_i;
  int b_iindx;
  int b_k;
  int b_loop_ub;
  int c_i;
  int c_k;
  int c_loop_ub;
  int d_loop_ub;
  int e_loop_ub;
  int firstBlockLength;
  int hi;
  int i;
  int i1;
  int i10;
  int i11;
  int i12;
  int i2;
  int i3;
  int i4;
  int i5;
  int i6;
  int i7;
  int i8;
  int i9;
  int ib;
  int iindx;
  int j;
  int k;
  int l_limit_tmp;
  int lastBlockLength;
  int loop_ub;
  int nblocks;
  int xblockoffset;
  if (!isInitialized_Calculate_DBP) {
    Calculate_DBP_initialize();
  }
  memcpy(&b_ECG[0], &ECG[0], 1000U * sizeof(double));
  memcpy(&b_PPG[0], &PPG[0], 1000U * sizeof(double));
  Data_Processing(b_ECG, b_PPG);
  j = 0;
  memset(&ECG_peaks_initial[0], 0, 998001U * sizeof(short));
  for (i = 0; i < 998; i++) {
    d = b_ECG[i + 1];
    if ((d > b_ECG[i]) && (d >= b_ECG[i + 2])) {
      ex = b_ECG[0];
      for (k = 0; k < 999; k++) {
        d1 = b_ECG[k + 1];
        if (ex < d1) {
          ex = d1;
        }
      }
      if (d > 0.45 * ex) {
        ECG_peaks_initial[j] = (short)(i + 2);
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
  b_i = b_ECG_peaks_initial->size[0] * b_ECG_peaks_initial->size[1];
  b_ECG_peaks_initial->size[0] = 1;
  b_ECG_peaks_initial->size[1] = loop_ub;
  emxEnsureCapacity_real_T(b_ECG_peaks_initial, b_i);
  for (i1 = 0; i1 < loop_ub; i1++) {
    b_ECG_peaks_initial->data[i1] = ECG_peaks_initial[i1];
  }
  emxInit_real_T(&x, 2);
  diff(b_ECG_peaks_initial, x);
  emxFree_real_T(&b_ECG_peaks_initial);
  if (x->size[1] == 0) {
    y = 0.0;
  } else {
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
    y = x->data[0];
    for (b_k = 2; b_k <= firstBlockLength; b_k++) {
      y += x->data[b_k - 1];
    }
    for (ib = 2; ib <= nblocks; ib++) {
      xblockoffset = (ib - 1) << 10;
      bsum = x->data[xblockoffset];
      if (ib == nblocks) {
        hi = lastBlockLength;
      } else {
        hi = 1024;
      }
      for (c_k = 2; c_k <= hi; c_k++) {
        bsum += x->data[(xblockoffset + c_k) - 1];
      }
      y += bsum;
    }
  }
  window_size = floor(y / (double)x->size[1]);
  /*  window size defined as the average distance between peaks */
  emxFree_real_T(&x);
  if (window_size == 0.0) {
    r = 0.0;
  } else {
    r = fmod(window_size, 2.0);
    if (r == 0.0) {
      r = 0.0;
    } else if (window_size < 0.0) {
      r += 2.0;
    }
  }
  if (r != 0.0) {
    window_size--;
  }
  emxInit_real_T(&ECG_ensembles, 2);
  i2 = ECG_ensembles->size[0] * ECG_ensembles->size[1];
  ECG_ensembles->size[0] = (int)window_size;
  ECG_ensembles->size[1] = loop_ub - 1;
  emxEnsureCapacity_real_T(ECG_ensembles, i2);
  b_loop_ub = (int)window_size * (loop_ub - 1);
  for (i3 = 0; i3 < b_loop_ub; i3++) {
    ECG_ensembles->data[i3] = 0.0;
  }
  emxInit_real_T(&PPG_ensembles, 2);
  i4 = PPG_ensembles->size[0] * PPG_ensembles->size[1];
  PPG_ensembles->size[0] = (int)window_size;
  PPG_ensembles->size[1] = loop_ub - 1;
  emxEnsureCapacity_real_T(PPG_ensembles, i4);
  c_loop_ub = (int)window_size * (loop_ub - 1);
  for (i5 = 0; i5 < c_loop_ub; i5++) {
    PPG_ensembles->data[i5] = 0.0;
  }
  /*  split ECG waveform around peaks */
  i6 = loop_ub - 2;
  for (c_i = 0; c_i <= i6; c_i++) {
    l_limit_tmp = ECG_peaks_initial[c_i + 1];
    l_limit = fmax(1.0, (double)l_limit_tmp - window_size / 2.0);
    /* ensure minimum index = 1 */
    if (l_limit == 1.0) {
      u_limit = window_size;
    } else {
      u_limit = fmin(1000.0, ((double)l_limit_tmp + window_size / 2.0) - 1.0);
      /* ensure maximum index = length(ECG) */
      if (u_limit == 1000.0) {
        l_limit = (1000.0 - window_size) + 1.0;
      }
    }
    if (l_limit > u_limit) {
      i7 = 0;
      i8 = 0;
    } else {
      i7 = (int)l_limit - 1;
      i8 = (int)u_limit;
    }
    d_loop_ub = i8 - i7;
    for (i9 = 0; i9 < d_loop_ub; i9++) {
      ECG_ensembles->data[i9 + ECG_ensembles->size[0] * c_i] = b_ECG[i7 + i9];
    }
    if (l_limit > u_limit) {
      i10 = 0;
      i11 = 0;
    } else {
      i10 = (int)l_limit - 1;
      i11 = (int)u_limit;
    }
    e_loop_ub = i11 - i10;
    for (i12 = 0; i12 < e_loop_ub; i12++) {
      PPG_ensembles->data[i12 + PPG_ensembles->size[0] * c_i] =
          b_PPG[i10 + i12];
    }
  }
  emxInit_real_T(&b_r, 1);
  emxInit_real_T(&r1, 1);
  /*  calculate ensemble average for ECG and PPG signals */
  mean(PPG_ensembles, r1);
  maximum(r1, &a__1, &iindx);
  mean(ECG_ensembles, b_r);
  maximum(b_r, &a__2, &b_iindx);
  PTT = ((double)iindx - (double)b_iindx) / 100.0;
  emxFree_real_T(&r1);
  emxFree_real_T(&b_r);
  emxFree_real_T(&PPG_ensembles);
  emxFree_real_T(&ECG_ensembles);
  return PTT;
}

/* End of code generation (Calculate_PTT.c) */
