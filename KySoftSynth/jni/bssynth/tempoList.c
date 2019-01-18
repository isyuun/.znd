//
//  tempoList.c
//  bssynth
//
//  Created by hideo on 2018/02/08.
//

#include "tempoList.h"
#include "BSLMem.h"

void tempoList_add (struct BSSYNTH_TEMPO **list, DWORD tempo, DWORD tick)
{
    struct BSSYNTH_TEMPO *t = (struct BSSYNTH_TEMPO *) bslMemAlloc (sizeof (struct BSSYNTH_TEMPO));
    if (t) {
        t->dwTempo = tempo;
        t->dwTick = tick;
        t->next = NULL;
        
        if (*list) {
            struct BSSYNTH_TEMPO *lastTempo = *list;
            while (lastTempo->next) {
                lastTempo = lastTempo->next;
            }
            lastTempo->next = t;
        }
        else {
            *list = t;
        }
    }
}

void tempoList_remove (struct BSSYNTH_TEMPO **list)
{
    if (*list) {
        struct BSSYNTH_TEMPO *p = *list;
        struct BSSYNTH_TEMPO *p2;
        while (p != NULL) {     /* éüÉ|ÉCÉìÉ^Ç™NULLÇ‹Ç≈èàóù */
            p2 = p->next;
            bslMemFree (p);
            p = p2;
        }
        *list = NULL;
    }
}

void tempoList_show (struct BSSYNTH_TEMPO *list)
{
#ifdef BSL_DEBUG
    struct BSSYNTH_TEMPO *tempo = list;
    while (tempo) {
        bslTrace (_TEXT("tempoList: %lu - %lu[usec/Beat]\n"), tempo->dwTick, tempo->dwTempo);
        tempo = tempo->next;
    }
#endif /* BSL_DEBUG */
}

float tempoList_tick2Time (struct BSSYNTH_TEMPO *list, int division, DWORD tick)
{
    float _time = 0.f;
    DWORD _tick = 0UL;
    DWORD _tempo = DEFAULT_TEMPO;
    
    struct BSSYNTH_TEMPO *tempo = list;
    while (tempo) {
        if (tick < tempo->dwTick) break;
        
        _time += (float) _tempo * (float) (tempo->dwTick - _tick) / ((float) division * 1000000.f);
        _tick = tempo->dwTick;
        _tempo = tempo->dwTempo;
        
        tempo = tempo->next;
    }
    
    _time += (float) _tempo * (float) (tick - _tick) / ((float) division * 1000000.f);
    return _time;
}

DWORD tempoList_time2Tick (struct BSSYNTH_TEMPO *list, int division, float time)
{
    DWORD tick = 0UL;
    float _time = 0.f;
    DWORD _tick = 0UL;
    DWORD _tempo = DEFAULT_TEMPO;
    
    struct BSSYNTH_TEMPO *tempo = list;
    while (tempo) {
        float _time2 = _time + (float) _tempo * (float) (tempo->dwTick - _tick) / ((float) division * 1000000.f);
        if (time <= _time2) {
            tick = _tick + (DWORD) ((time - _time) * ((float) division * 1000000.f) / (float) _tempo);
            return tick;
        }
        
        _time = _time2;
        _tick = tempo->dwTick;
        _tempo = tempo->dwTempo;
        
        tempo = tempo->next;
    }
    
    tick = _tick + (DWORD) ((time - _time) * ((float) division * 1000000.f) / (float) _tempo);
    return tick;
}


