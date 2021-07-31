/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * SystemCore.c
 *
 * Code generation for function 'SystemCore'
 *
 */

/* Include files */
#include "SystemCore.h"
#include "Calculate_DBP_internal_types.h"

/* Function Definitions */
void SystemCore_step(dspcodegen_IIRFilter *obj, const double varargin_1[1000],
                     double varargout_1[1000])
{
  dsp_IIRFilter_0 *b_obj;
  double b_numAccum;
  double d;
  double d1;
  double denAccum;
  double numAccum;
  int i;
  if (obj->isInitialized != 1) {
    obj->isSetupComplete = false;
    obj->isInitialized = 1;
    obj->isSetupComplete = true;
    /* System object Initialization function: dsp.IIRFilter */
    obj->cSFunObject.W0_states[0] = obj->cSFunObject.P2_InitialStates;
    obj->cSFunObject.W0_states[1] = obj->cSFunObject.P2_InitialStates;
  }
  b_obj = &obj->cSFunObject;
  /* System object Outputs function: dsp.IIRFilter */
  for (i = 0; i < 1000; i++) {
    numAccum = b_obj->W0_states[0];
    d = varargin_1[i];
    d1 = numAccum + b_obj->P0_Numerator[0] * d;
    varargout_1[i] = d1;
    b_numAccum = b_obj->W0_states[1];
    denAccum = b_numAccum + b_obj->P0_Numerator[1] * d;
    denAccum -= b_obj->P1_Denominator[1] * d1;
    b_obj->W0_states[0] = denAccum;
    denAccum = b_obj->P0_Numerator[2] * d;
    denAccum -= b_obj->P1_Denominator[2] * d1;
    b_obj->W0_states[1] = denAccum;
  }
}

/* End of code generation (SystemCore.c) */
