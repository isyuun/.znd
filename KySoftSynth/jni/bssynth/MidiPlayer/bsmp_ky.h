/*
 *  bsmp_ky.h
 *  bssynth
 *
 *  Created by hideo on 15/12/10.
 *  Copyright (c) 2015 - 2016 bismark. All rights reserved.
 *
 */

#include "BSLMidParser.h"
#include "bsmpPlayer.h"

#ifdef BSSYNTH_KY

#ifdef BSSYNTH_KY_RHYTHM_CHANGE

#define BSMP_KY_RHYTHM_TYPES 4
#define BSMP_KY_RHYTHM_TYPE_OFF 0
#define BSMP_KY_RHYTHM_TYPE_1 1
#define BSMP_KY_RHYTHM_TYPE_2 2
#define BSMP_KY_RHYTHM_TYPE_3 3
#define BSMP_KY_RHYTHM_TYPE_4 4

#define BSMP_KY_RHYTHM_PATTERNS 4
#define BSMP_KY_RHYTHM_PATTERN_DEFAULT 0
#define BSMP_KY_RHYTHM_PATTERN_FILL1 1
#define BSMP_KY_RHYTHM_PATTERN_FILL2 2
#define BSMP_KY_RHYTHM_PATTERN_ENDING 3

#define BSMP_KY_RHYTHM_PORT 10

typedef struct {
  char *address;
  unsigned long size;
} BSMP_KY_RHYTHM_CHANGE_MEMORY;

BSMP_ERR bsmpKYAddRhythmChange (LPCTSTR src, LPCTSTR dst, LPCTSTR patternsFolder);
BSMP_ERR bsmpKYAddRhythmChangeMemory (LPCTSTR src, LPCTSTR dst, BSMP_KY_RHYTHM_CHANGE_MEMORY data[BSMP_KY_RHYTHM_TYPES][BSMP_KY_RHYTHM_PATTERNS], BSMP_KY_RHYTHM_CHANGE_MEMORY ctrl[BSMP_KY_RHYTHM_TYPES][BSMP_KY_RHYTHM_PATTERNS]);

#endif /* BSSYNTH_KY_RHYTHM_CHANGE */

#endif /* BSSYNTH_KY */
