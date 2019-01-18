/*
 *  BSLWavRender.h
 *
 *  Copyright (c) 2004 bismark. All rights reserved.
 *
 */

/*!
	@file BSLMath.h
	@brief
	数値演算
*/

#ifndef __INCBSLMathh
#define __INCBSLMathh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include <math.h>

/* defines */

#if BSL_MAC || BSL_WINDOWS /* || BSL_WINDOWSCE  */
	/*! math.hにfloat用関数がない場合に定義する */
	#define BSL_MATH_NO_FLOAT_FUNCTIONS
#endif

#if BSL_MAC || BSL_WINDOWS || BSL_LINUX || BSL_ANDROID
	/*! math.hにlog2 ()関数がない場合に定義する */
	#define BSL_MATH_NO_LOG2
#endif

/*! log2 ()関数用マクロ */
#ifdef BSL_MATH_NO_LOG2
	#define log2f_(value) (log10f_(value) / log10f_(2.f))
#else
	#define log2f_(value) (log2f (value))
#endif

#ifdef BSL_MATH_NO_FLOAT_FUNCTIONS
	#define sinf_(value) ((float) sin ((double) value))
	#define cosf_(value) ((float) cos ((double) value))
	#define log10f_(value) ((float) log10 ((double) value))
	#define powf_(v1,v2) ((float) pow ((double) v1, (double) v2))
	#define sqrtf_(value) ((float) sqrt ((double) value))
	#define fabsf_(value) ((float) fabs ((double) value))
	#define atan2f_(y,x) ((float) atan2 ((double) y, (double) x))
	#define fmodf_(x,y) ((float) fmod ((double) x, (double) y))
#else /* BSL_MATH_NO_FLOAT_FUNCTIONS */
	#define sinf_(value) (sinf (value))
	#define cosf_(value) (cosf (value))
	#define log10f_(value) (log10f (value))
	#define powf_(v1, v2) (powf (v1, v2))
	#define sqrtf_(value) (sqrtf (value))
	#define fabsf_(value) (fabsf (value))
	#define atan2f_(y,x) (atan2f(y,x))
	#define fmodf_(x,y) (fmodf(x,y))
#endif /* BSL_MATH_NO_FLOAT_FUNCTIONS */

#ifdef BSL_MATH_NO_FLOAT_FUNCTIONS
	#undef BSL_MATH_NO_FLOAT_FUNCTIONS
#endif /* BSL_MATH_NO_FLOAT_FUNCTIONS */

#ifdef BSL_MATH_NO_LOG2
	#undef BSL_MATH_NO_LOG2
#endif /* BSL_MATH_NO_LOG2 */

/*! 円周率 */
#define BSL_MATH_PI 3.14159265359f

/* typedefs */

/* function decralations */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLMathh */
