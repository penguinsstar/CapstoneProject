/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * Calculate_DBP_internal_types.h
 *
 * Code generation for function 'Calculate_DBP'
 *
 */

#ifndef CALCULATE_DBP_INTERNAL_TYPES_H
#define CALCULATE_DBP_INTERNAL_TYPES_H

/* Include files */
#include "Calculate_DBP_types.h"
#include "rtwtypes.h"

/* Type Definitions */
#ifndef typedef_dsp_IIRFilter_0
#define typedef_dsp_IIRFilter_0
typedef struct {
  int S0_isInitialized;
  double W0_states[2];
  double P0_Numerator[3];
  double P1_Denominator[3];
  double P2_InitialStates;
} dsp_IIRFilter_0;
#endif /* typedef_dsp_IIRFilter_0 */

#ifndef typedef_dspcodegen_IIRFilter
#define typedef_dspcodegen_IIRFilter
typedef struct {
  boolean_T matlabCodegenIsDeleted;
  int isInitialized;
  boolean_T isSetupComplete;
  dsp_IIRFilter_0 cSFunObject;
} dspcodegen_IIRFilter;
#endif /* typedef_dspcodegen_IIRFilter */

#endif
/* End of code generation (Calculate_DBP_internal_types.h) */
