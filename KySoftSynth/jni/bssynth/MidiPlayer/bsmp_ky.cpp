/*
 *  bsmp_ky.c
 *  bssynth
 *
 *  Created by hideo on 15/12/10.
 *  Copyright (c) 2015 - 2016 bismark. All rights reserved.
 *
 */

#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)

// #define DEBUG_CONVERT

#include <string>
#include <sstream>

#include "bsmp_ky.h"
#include "mError.h"
#include "mMidEditorSmf.h"

#define KY_DIVISION 120
#define KY_FUTURE_TICK 0xFFFFFFFFUL

static int format[] = {kMidFormatSmf1};
static int division[] = {KY_DIVISION};

#pragma mark -

template <typename T>
std::string to_string_(T value)
{
  std::ostringstream os ;
  os << value ;
  return os.str() ;
}

#pragma mark -

class Rhythm : public MMidReaderSmf
{
  public:
    Rhythm ();
    int addToTrack (MMidEditorSmfTrk *track, DWORD offsetTick, DWORD fromTick = 0UL, int keyShift = 0, bool tucking = false);

    int pattern;
    bool isSlowSong;
    DWORD length;
  
  protected:
    virtual int eventGet (MMidEvent *event);
  
  private:
    MMidEditorSmfTrk *track;
    DWORD offsetTick;
    DWORD fromTick;
    int keyShift;
    bool tucking;
};

Rhythm::Rhythm () {
  pattern = BSMP_KY_RHYTHM_PATTERN_DEFAULT;
  isSlowSong = false;
  length = 0;
}

int Rhythm::addToTrack (MMidEditorSmfTrk *track, DWORD offsetTick, DWORD fromTick, int keyShift, bool tucking)
{
  this->track = track;
  this->offsetTick = offsetTick;
  this->fromTick = fromTick;
  this->keyShift = keyShift;
  this->tucking = tucking;

  if (isSlowSong) {
    switch (pattern) {
      case BSMP_KY_RHYTHM_PATTERN_FILL1:
      case BSMP_KY_RHYTHM_PATTERN_FILL2:
        /*
         |1111|1111|1111|1111| -> |xxxx|xxxx|2222|2222|
         |xxxx|1111|1111|1111| -> |xxxx|xxxx|2222|2222|
         |xxxx|xxxx|1111|1111| -> |xxxx|xxxx|2222|2222|
         |xxxx|xxxx|xxxx|1111| -> |xxxx|xxxx|2222|2222|
         */
        this->offsetTick += (length / 2);
        this->fromTick = 0;
        break;
      case BSMP_KY_RHYTHM_PATTERN_ENDING:
        break;
    }
  }
  
  rewind ();
  if (eventSort (0UL) != kMidEventProcFinished) {
    return ERR;
  }

  if (isSlowSong) {
    switch (pattern) {
      case BSMP_KY_RHYTHM_PATTERN_DEFAULT:
        DWORD length = this->length / 2;
        this->offsetTick += length;
        if (length <= this->fromTick) {
          this->fromTick -= length;
        }
        else {
          this->fromTick = 0;
        }
        rewind ();
        if (eventSort (0UL) != kMidEventProcFinished) {
          return ERR;
        }
        break;
    }
  }

  return OK;
}

int Rhythm::eventGet (MMidEvent *event)
{
  if (event->byStatus < 0x80)
    return kMidEventProcOk;

  if (0x80 <= event->byStatus && event->byStatus < 0xF0) {
    switch (event->byStatus & 0x0F) {
      case 0x01:
      case 0x09:
      case 0x0A:
        break;
      default:
        return kMidEventProcOk;
    }
  }
  
  if (isSlowSong) {
    if ((event->dwTick / 2) < fromTick)
      return kMidEventProcOk;
  }
  else {
    if (event->dwTick < fromTick)
      return kMidEventProcOk;
  }
  
  DWORD tick;
  if (tucking) {
    tick = offsetTick;
  }
  else {
    if (isSlowSong) {
      tick = offsetTick + event->dwTick / 2;
    }
    else {
      tick = offsetTick + event->dwTick;
    }
  }
  MMidEvent *newEvent;
  track->eventAdd (tick, event->iSize, &newEvent);
  newEvent->byStatus = event->byStatus;
  memcpy (newEvent->pData, event->pData, event->iSize);

  switch (event->byStatus & 0xF0) {
    case 0x80:
    case 0x90:
      switch (event->byStatus & 0x0F) {
        case 0x09:
        case 0x0A:
          break;
        default:
          // key shift
          newEvent->pData[0] += keyShift;
          break;
      }
      break;
  }
  
  
  return kMidEventProcOk;
}

#pragma mark -

class Analyzer : public MMidEditorSmf
{
public:
  Analyzer ();
  void checkSongType (MMidEditorSmf *smf);
  BSMP_ERR loadMaterials (LPCTSTR patternsFolder);
  BSMP_ERR loadMaterials (BSMP_KY_RHYTHM_CHANGE_MEMORY data[BSMP_KY_RHYTHM_TYPES][BSMP_KY_RHYTHM_PATTERNS], BSMP_KY_RHYTHM_CHANGE_MEMORY ctrl[BSMP_KY_RHYTHM_TYPES][BSMP_KY_RHYTHM_PATTERNS]);

  int copyTrack (MMidEditorSmf *smf, int index);
  int copyTrack (MMidEditorSmf *smf, const char *trackName);
  int analyze (MMidEditorSmfTrk *track, int rhythmType);
  
protected:
  virtual int eventGet (MMidEvent *event);
  
private:
  Rhythm rhythm_data[BSMP_KY_RHYTHM_TYPES][BSMP_KY_RHYTHM_PATTERNS];
  Rhythm rhythm_ctrl[BSMP_KY_RHYTHM_TYPES][BSMP_KY_RHYTHM_PATTERNS];
  
  MMidEditorSmfTrk *targetTrack;
  DWORD startTick; // tick where starting standard (4 bars) phrase
  DWORD lastTick;
  int lastKey;
  int rhythmType;
  bool isSlowSong;
};

Analyzer::Analyzer () {
  formatSet (1);
  divisionSet (KY_DIVISION);
  isSlowSong = false;
}

void Analyzer::checkSongType (MMidEditorSmf *smf)
{
  for (int t = 0; t < smf->numberOfTracks(); t++) {
    MMidEditorSmfTrk *track = (MMidEditorSmfTrk *) smf->trackGet(t);
    if (!strcmp (track->nameGet (), "$SW")) {
      isSlowSong = true;
      break;
    }
  }

  for (int type = 0; type < BSMP_KY_RHYTHM_TYPES; type++) {
    for (int pattern = 0; pattern < BSMP_KY_RHYTHM_PATTERNS; pattern++) {
      switch (pattern) {
        case BSMP_KY_RHYTHM_PATTERN_DEFAULT:
          rhythm_data[type][pattern].length = KY_DIVISION * 4 * 4;
          break;
        case BSMP_KY_RHYTHM_PATTERN_FILL1:
        case BSMP_KY_RHYTHM_PATTERN_FILL2:
          rhythm_data[type][pattern].length = KY_DIVISION * 4 * 1;
          break;
        case BSMP_KY_RHYTHM_PATTERN_ENDING:
          rhythm_data[type][pattern].length = KY_DIVISION * 4 * 4;
          break;
      }
      rhythm_data[type][pattern].pattern = pattern;
      rhythm_data[type][pattern].isSlowSong = this->isSlowSong;
    }
  }
}

BSMP_ERR Analyzer::loadMaterials (LPCTSTR patternsFolder)
{
  int err = OK;
  
  for (int type = 0; type < BSMP_KY_RHYTHM_TYPES && err == OK; type++) {
    for (int pattern = 0; pattern < BSMP_KY_RHYTHM_PATTERNS && err == OK; pattern++) {
      if (err == OK) {
        std::string path = std::string(patternsFolder) + std::string("nds") + to_string_(type + 1) + std::string("-") + to_string_(pattern + 1) + std::string(".mid");
        err = rhythm_data[type][pattern].fileOpen(path.c_str());
      }
      if (err == OK) {
        err = rhythm_data[type][pattern].ready(sizeof(format) / sizeof(int), format, sizeof(division) / sizeof(int), division, NULL);
      }
      
      if (err == OK) {
        std::string path = std::string(patternsFolder) + std::string("nds") + to_string_(type + 1) + std::string("-") + to_string_(pattern + 1) + std::string("c.mid");
        err = rhythm_ctrl[type][pattern].fileOpen(path.c_str());
      }
      if (err == OK) {
        err = rhythm_ctrl[type][pattern].ready(sizeof(format) / sizeof(int), format, sizeof(division) / sizeof(int), division, NULL);
      }
    }
  }
  
  return err == OK? BSMP_OK : BSMP_ERR_UNDEFINED;
}

BSMP_ERR Analyzer::loadMaterials (BSMP_KY_RHYTHM_CHANGE_MEMORY data[BSMP_KY_RHYTHM_TYPES][BSMP_KY_RHYTHM_PATTERNS], BSMP_KY_RHYTHM_CHANGE_MEMORY ctrl[BSMP_KY_RHYTHM_TYPES][BSMP_KY_RHYTHM_PATTERNS])
{
  int err = OK;
  
  for (int type = 0; type < BSMP_KY_RHYTHM_TYPES && err == OK; type++) {
    for (int pattern = 0; pattern < BSMP_KY_RHYTHM_PATTERNS && err == OK; pattern++) {
      if (err == OK) {
        err = rhythm_data[type][pattern].memoryOpen (data[type][pattern].address, (DWORD) data[type][pattern].size);
      }
      if (err == OK) {
        err = rhythm_data[type][pattern].ready(sizeof(format) / sizeof(int), format, sizeof(division) / sizeof(int), division, NULL);
      }
      
      if (err == OK) {
        err = rhythm_data[type][pattern].memoryOpen (ctrl[type][pattern].address, (DWORD) ctrl[type][pattern].size);
      }
      if (err == OK) {
        err = rhythm_ctrl[type][pattern].ready(sizeof(format) / sizeof(int), format, sizeof(division) / sizeof(int), division, NULL);
      }
    }
  }
  
  return err == OK? BSMP_OK : BSMP_ERR_UNDEFINED;
}

int Analyzer::copyTrack (MMidEditorSmf *smf, int index)
{
  if (0 <= index && index < smf->numberOfTracks()) {
    MMidEditorSmfTrk *track = (MMidEditorSmfTrk *) smf->trackGet(index);
    MMidEditorSmfTrk *newTrack = trackAdd();
    if (newTrack) {
      newTrack->nameSet(track->nameGet());
      newTrack->orgTrkNumberSet(iPlayedTracks);
      iPlayedTracks++;
      return newTrack->eventLoadAll (track);
    }
  }
  
  return ERR;
}

int Analyzer::copyTrack (MMidEditorSmf *smf, const char *trackName)
{
  for (int t = 0; t < smf->numberOfTracks(); t++) {
    MMidEditorSmfTrk *track = (MMidEditorSmfTrk *) smf->trackGet(t);
    if (!strcmp (track->nameGet (), trackName)) {
      MMidEditorSmfTrk *newTrack = trackAdd();
      if (newTrack) {
        newTrack->nameSet(track->nameGet());
        newTrack->orgTrkNumberSet(iPlayedTracks);
        iPlayedTracks++;
        return newTrack->eventLoadAll (track);
      }
    }
  }

  return ERR;
}

int Analyzer::analyze (MMidEditorSmfTrk *track, int rhythmType)
{
  int err = OK;

  if (err == OK) {
    this->targetTrack = track;
    this->rhythmType = rhythmType;
    rewind ();
    err = rhythm_ctrl[rhythmType][BSMP_KY_RHYTHM_PATTERN_DEFAULT].addToTrack (track, 0, 0, 0, true);
  }

  if (err == OK) {
    this->startTick = KY_FUTURE_TICK;
    this->lastTick = 0UL;
    if (eventSort (0UL) != kMidEventProcFinished) {
      err = ERR;
    }
  }
  
  return err;
}

int Analyzer::eventGet (MMidEvent *event)
{
  MMidEditorSmfTrk *track = (MMidEditorSmfTrk *) trackGet(event->iOrgTrk);
  const DWORD length_default = (KY_DIVISION * 4 * 4);
  const DWORD length_fillin = (KY_DIVISION * 4);

#ifdef DEBUG_CONVERT
  const char *note[] = {"C ", "C#", "D ", "D#", "E ", "F ", "F#", "G ", "G#", "A ", "A#", "B "};
  static int beatsInMesure = 4;
  static DWORD baseTickForTransport = 0UL;
  static int baseMesureForTransport = 0;
  int measure = baseMesureForTransport + (event->dwTick - baseTickForTransport) / (KY_DIVISION * beatsInMesure);
  int beat = ((event->dwTick - baseTickForTransport) % (KY_DIVISION * beatsInMesure)) / KY_DIVISION;
  int tick = event->dwTick % KY_DIVISION;
  char transport[20];
  sprintf (transport, "%03d:%d:%03d (%06d)", measure + 1, beat + 1, tick, (int) event->dwTick);

  if (event->byStatus == BSL_MID_META_TYPE_TIME_SIGNATURE) {
    printf ("%d [---]: %s - Time Signature %d/%d\n", event->iOrgTrk, transport, event->pData[0], 1 << event->pData[1]);
    baseMesureForTransport = measure;
    baseTickForTransport = event->dwTick;
    beatsInMesure = (int) event->pData[0];
  }
#endif // DEBUG_CONVERT
  
  if (!strcmp (track->nameGet(), "$BS")) {
    if ((event->byStatus & 0xF0) == 0x90 && event->pData[1] != 0x00) {
#ifdef DEBUG_CONVERT
      printf ("%d [%s]: %s - %02X %02X - %s", event->iOrgTrk, track->nameGet(), transport, event->byStatus, event->pData[0], note[event->pData[0] % 12]);
#endif // DEBUG_CONVERT

      if (startTick == KY_FUTURE_TICK) {
        startTick = event->dwTick;
        lastTick = event->dwTick;
#ifdef DEBUG_CONVERT
        printf ("Start\n");
#endif // DEBUG_CONVERT
        return kMidEventProcOk;
      }
      
#ifdef DEBUG_CONVERT
      if ((startTick + length_default) <= event->dwTick) printf ("\n");
#endif // DEBUG_CONVERT
      while ((startTick + length_default) <= event->dwTick) {
        startTick += length_default;
        if (startTick < event->dwTick) {
#ifdef DEBUG_CONVERT
          printf ("      - %06d 000000 %06d\n", (int) startTick, (int) length_default);
#endif // DEBUG_CONVERT
          targetTrack->eventDeleteInRange(startTick, KY_FUTURE_TICK);
          rhythm_data[rhythmType][BSMP_KY_RHYTHM_PATTERN_DEFAULT].addToTrack (targetTrack, startTick, 0UL, lastKey);
          lastTick = event->dwTick;
        }
      }
      
      lastKey = event->pData[0] % 12 - 12;
      targetTrack->eventDeleteInRange(event->dwTick, KY_FUTURE_TICK);
      rhythm_data[rhythmType][BSMP_KY_RHYTHM_PATTERN_DEFAULT].addToTrack(targetTrack, startTick, event->dwTick - startTick, lastKey);
      lastTick = event->dwTick;
#ifdef DEBUG_CONVERT
      printf ("      - %06d %06d %06d\n", (int) startTick, (int)  (event->dwTick - startTick), (int) (length_default - event->dwTick + startTick));
#endif // DEBUG_CONVERT
    }
  }
  else if (!strcmp (track->nameGet(), "$FS")) {
    if ((event->byStatus & 0xF0) == 0x90 && event->pData[1] != 0x00 && 0UL < event->dwTick) {
#ifdef DEBUG_CONVERT
      printf ("%d [%s]: %s - %02X %02X - Fill in", event->iOrgTrk, track->nameGet(), transport, event->byStatus, event->pData[0]);
#endif // DEBUG_CONVERT
      
      int pattern = -1;
      switch (event->pData[0]) {
        case 0x11:
          pattern = BSMP_KY_RHYTHM_PATTERN_FILL1;
          break;
        case 0x1D:
          pattern = BSMP_KY_RHYTHM_PATTERN_FILL2;
          break;
      }
      
      if (0 <= pattern) {
#ifdef DEBUG_CONVERT
        if ((startTick + length_default) <= event->dwTick) printf ("\n");
#endif // DEBUG_CONVERT
        while ((startTick + length_default) <= event->dwTick) {
          startTick += length_default;
          if (startTick <= event->dwTick) {
#ifdef DEBUG_CONVERT
            printf ("      - %06d 000000 %06d\n", (int) startTick, (int) length_default);
#endif // DEBUG_CONVERT
            rhythm_data[rhythmType][BSMP_KY_RHYTHM_PATTERN_DEFAULT].addToTrack (targetTrack, startTick, 0UL, lastKey);
            lastTick = event->dwTick;
          }
        }
        
        DWORD offsetTick = (event->dwTick - startTick) / length_fillin * length_fillin + startTick;
        targetTrack->eventDeleteInRange (event->dwTick, length_fillin + offsetTick);
        rhythm_data[rhythmType][pattern].addToTrack(targetTrack, offsetTick, event->dwTick - offsetTick, lastKey);
        //      lastTick = event->dwTick;
#ifdef DEBUG_CONVERT
        printf ("      - %06d %06d\n", (int) offsetTick, (int)  (event->dwTick - offsetTick));
#endif // DEBUG_CONVERT
      }
#ifdef DEBUG_CONVERT
      else {
        printf ("\n");
      }
#endif // DEBUG_CONVERT
    }
  }
  else if (!strcmp (track->nameGet(), "$VR")) {
    if ((event->byStatus & 0xF0) == 0xC0) {
#ifdef DEBUG_CONVERT
      printf ("%d [%s]: %s - %02X %02X - Sync", event->iOrgTrk, track->nameGet(), transport, event->byStatus, event->pData[0]);
#endif // DEBUG_CONVERT
      startTick = event->dwTick;
      targetTrack->eventDeleteInRange(event->dwTick, KY_FUTURE_TICK);
      rhythm_data[rhythmType][BSMP_KY_RHYTHM_PATTERN_DEFAULT].addToTrack(targetTrack, startTick, 0UL, lastKey);
      lastTick = event->dwTick;
#ifdef DEBUG_CONVERT
      printf ("      - %06d %06d\n", (int) lastTick, (int)  (event->dwTick - lastTick));
#endif // DEBUG_CONVERT
    }
  }
  else if (!strcmp (track->nameGet(), "$RS")) {
    if ((event->byStatus & 0xF0) == 0x90 && event->pData[0] == 0x1A) {
#ifdef DEBUG_CONVERT
      printf ("%d [%s]: %s - %02X %02X - Ending\n", event->iOrgTrk, track->nameGet(), transport, event->byStatus, event->pData[0]);
#endif // DEBUG_CONVERT
      targetTrack->eventDeleteInRange(event->dwTick, KY_FUTURE_TICK);
      rhythm_data[rhythmType][BSMP_KY_RHYTHM_PATTERN_ENDING].addToTrack(targetTrack, event->dwTick, 0UL, lastKey);
      return kMidEventProcFinished;
    }
  }
  
  return kMidEventProcOk;
}

#pragma mark -

int portNumberWithTrack (MMidReaderBaseTrk *track)
{
  int port = 0;
  
  track->rewind ();
  
  while (1) {
    MMidEvent *event;
    int err = OK;
    if ((err = track->eventRead (&event)) != OK) {
      break;
    }
    
    if (event->byStatus == kMidSmfMetaTypeEnd) {
      break;
    }
    else if (event->byStatus == BSL_MID_META_TYPE_PORT) {
      port = event->pData[0];
    }
    
    track->eventInc (event);
  }
  
  track->rewind ();
  return port;
}

int channelWithTrack (MMidReaderBaseTrk *track)
{
  int channel = -1;
  
  track->rewind ();
  
  while (1) {
    MMidEvent *event;
    int err = OK;
    if ((err = track->eventRead (&event)) != OK) {
      break;
    }
    
    if (event->byStatus == kMidSmfMetaTypeEnd) {
      break;
    }
    else if (0x80 <= event->byStatus && event->byStatus < 0xF0) {
      int ch = (int) event->byStatus & 0x0F;
      if (channel < 0) {
        channel = ch;
      }
      else {
        if (channel != ch) {
          return -1;
        }
      }
    }
    
    track->eventInc (event);
  }
  
  track->rewind ();
  return channel;
}

static BSMP_ERR addRhythmChange
  (
   LPCTSTR src,
   LPCTSTR dst,
   Analyzer *analyzer
   )
{
  MMidEditorSmf smf;
  MMidEditorSmfTrk *newTracks[BSMP_KY_RHYTHM_TYPES];

#ifdef DEBUG_CONVERT
  bslTrace ("-------- bsmpKYAddRhythmChange\n");
#endif // DEBUG_CONVERT
  
  int err = OK;
  
  if (err == OK) {
    err = smf.fileOpen(src);
  }
  
  if (err == OK) {
    err = smf.ready(sizeof(format) / sizeof(int), format, sizeof(division) / sizeof(int), division, NULL);
  }

#ifdef DEBUG_CONVERT
  if (err == OK) {
    for (int t = 0; t < smf.numberOfTracks(); t++) {
      MMidEditorSmfTrk *track = (MMidEditorSmfTrk *) smf.trackGet(t);
      int port = portNumberWithTrack(track);
      int channel = channelWithTrack (track);
      bslTrace ("track[%02d] port = %d ch = %d [%s]\n", t, port, channel, track->nameGet());
    }
  }
#endif // DEBUG_CONVERT

  if (err == OK) {
    analyzer->checkSongType (&smf);
  }
  
  // add new tracks for rhythm pattern
  if (err == OK) {
    for (int type = 0; type < BSMP_KY_RHYTHM_TYPES && err == OK; type++) {
      MMidEditorSmfTrk *track = smf.trackAdd();
      if (track) {
        MMidEvent *event;

        err = track->eventAdd(0UL, 3, &event);
        if (err == OK) {
          event->byStatus = 0x03;
          char trackName[4];
          sprintf(trackName, "$R%d", type + 1);
          memcpy (event->pData, trackName, 3);
        }

        err = track->eventAdd(0UL, 1, &event);
        if (err == OK) {
          event->byStatus = BSL_MID_META_TYPE_PORT;
          event->pData[0] = BSMP_KY_RHYTHM_PORT + type;
        }
        
        newTracks[type] = track;
      }
    }
  }

  if (err == OK) {
    err = analyzer->copyTrack (&smf, 0);
  }
  
  if (err == OK) {
    err = analyzer->copyTrack (&smf, "$BS");
  }
  
  if (err == OK) {
    err = analyzer->copyTrack (&smf, "$RS");
  }
  
  if (err == OK) {
    err = analyzer->copyTrack (&smf, "$FS");
  }
  
  if (err == OK) {
    /* err = */ analyzer->copyTrack (&smf, "$VR"); /* not mandatory */
  }
  
  if (err == OK) {
    for (int type = 0; type < BSMP_KY_RHYTHM_TYPES && err == OK; type++) {
      err = analyzer->analyze (newTracks[type], type);
    }
  }
  
  if (err == OK) {
    smf.useRunningStatus(false);
    err = smf.fileSave(dst);
  }

  if (err == OK) {
    return BSMP_OK;
  }
  else {
    switch (err) {
      case kErrFileOpen:
      case kErrFileCreate:
      case kErrFileSize:
      case kErrFileSizeOver:
      case kErrFileRead:
      case kErrFileReadEOF:
      case kErrFileWrite:
      case kErrFileSeek:
      case kErrFileNotReady:
      case kErrFileUnknownStructure:
      case kErrFileRiffHeader:
      case kErrFileUnsupportedFormat:
        return BSMP_ERR_FILE;
        
      case kErrMemAlloc:
      case kErrMemFree:
      case kErrMemHandle:
      case kErrMemHeapCreate:
      case kErrMemHeapDestroy:
        return BSMP_ERR_MEMORY;
        
      case kErrParamWrong:
      case kErrParamRange:
      case kErrParamChecksum:
        return BSMP_ERR_PARAM;
        
      case kErrSmfHeaderChunk:
      case kErrSmfHeaderChunkSize:
      case kErrSmfFormat:
      case kErrSmfTracks0:
      case kErrSmfTracks:
      case kErrSmfDivisionFormat:
      case kErrSmfDivision:
      case kErrSmfTrackChunk:
      case kErrSmfNoEOT:
      case kErrSmfVarLenRead:
      case kErrSmfNoStatus:
      case kErrSmfUndefinedStatus:
      case kErrSmfSysexSize:
      case kErrSmfSysexSize2:
      case kErrSmfSysexData:
      case kErrSmfSysexData2:
      case kErrSmfChannelMsgData:
      case kErrSmfChannel:
      case kErrSmfMetaSize:
      case kErrSmfMetaData:
      case kErrSmfMetaType:
      case kErrSmfNoTrackName:
      case kErrSmfNon1stTrackName:
      case kErrSmfNoData:
      case kErrSmfMemoryEmpty:
      case kErrSmfTrackSelectFail:
        return BSMP_ERR_DATA;
    }

    return BSMP_ERR_UNDEFINED;
  }
}

#pragma mark - Public methods

BSMP_ERR bsmpKYAddRhythmChange
  (
  LPCTSTR src,
  LPCTSTR dst,
  LPCTSTR patternsFolder
  )
{
  BSMP_ERR err = BSMP_OK;
  Analyzer analyzer;
  
  if (err == BSMP_OK) {
    err = analyzer.loadMaterials (patternsFolder);
  }
  
  if (err == BSMP_OK) {
    err = addRhythmChange (src, dst, &analyzer);
  }
  
  return err;
}

BSMP_ERR bsmpKYAddRhythmChangeMemory
  (
  LPCTSTR src,
  LPCTSTR dst,
  BSMP_KY_RHYTHM_CHANGE_MEMORY data[BSMP_KY_RHYTHM_TYPES][BSMP_KY_RHYTHM_PATTERNS],
  BSMP_KY_RHYTHM_CHANGE_MEMORY ctrl[BSMP_KY_RHYTHM_TYPES][BSMP_KY_RHYTHM_PATTERNS]
  )
{
  BSMP_ERR err = BSMP_OK;
  Analyzer analyzer;
  
  if (err == BSMP_OK) {
    err = analyzer.loadMaterials (data, ctrl);
  }

  if (err == BSMP_OK) {
    err = addRhythmChange (src, dst, &analyzer);
  }

  return err;
}

#endif
