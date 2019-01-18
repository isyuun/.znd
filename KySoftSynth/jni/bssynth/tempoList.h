//
//  tempoList.h
//  bssynth
//
//  Created by hideo on 2018/02/08.
//

#ifndef tempoList_h
#define tempoList_h

#ifdef __cplusplus
extern "C" {
#endif

#include "BSL.h"

#define DEFAULT_TEMPO (500000UL)

/* typedefs */

struct BSSYNTH_TEMPO {
    DWORD dwTick;
    DWORD dwTempo;
    struct BSSYNTH_TEMPO *next;
};

/* function decralations */

void tempoList_add (struct BSSYNTH_TEMPO **list, DWORD tempo, DWORD tick);
void tempoList_remove (struct BSSYNTH_TEMPO **list);

void tempoList_show (struct BSSYNTH_TEMPO *list);

float tempoList_tick2Time (struct BSSYNTH_TEMPO *list, int division, DWORD tick);
DWORD tempoList_time2Tick (struct BSSYNTH_TEMPO *list, int division, float time);

#ifdef __cplusplus
}
#endif

#endif /* tempoList_h */
