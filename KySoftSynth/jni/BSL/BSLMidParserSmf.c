/*
 *  BSLMidParserSmf.c
 *
 *  Copyright (c) 2002-2012 bismark. All rights reserved.
 *
 */

/*!
	@file BSLMidParserSmf.c
	@brief
	MIDI File入力制御オブジェクト(SMF)
*/

/* includes */

#include "BSL.h"
#include "BSLFile.h"
#include "BSLMid.h"
#include "BSLMidParserTrackSmf.h"
#include "BSLMidParserSmf.h"

/* defines */

#define MTrk_MARKER (BSL_FILE_MARKER ('M', 'T', 'r', 'k'))
#define _SMPTE_DIVISION 0x8000 /* SMPTE formated division  */

/* typedefs */

/* globals */

/* locals */

/* forward declarations */


/*---------------------------------------------------------------------------*/
BSLErr _bslMidParserSmfParse
	(
	BSLMidParser *obj, 
	BSLFile *file
	)
{
	DWORD header[2];
	WORD data;
	int i;
	BSLErr err = BSL_OK;

#ifdef BSL_DEBUG
	bslTrace (_TEXT("\n"));
	bslTrace (_TEXT("MID : ---------------------------------------\n"));
	bslTrace (_TEXT("MID : standard midi file loaded\n"));
#endif /* BSL_DEBUG */

	/* clear smf parameters */
	obj->nPlayedTracks = 0;
	obj->dwStartReadtick = 0UL;

	for (i = 0; i < BSL_MID_TRACKS; i++) {
		obj->tTrack[i].initialize (&obj->tTrack[i]);
	}

	if (err == BSL_OK) {
		obj->pFile = file;
	}

	if (err == BSL_OK) {
		err = file->seek (file, 0, BSL_FILE_SEEK_START);
	}

	/* read header chunk */
	if (err == BSL_OK) {
		err = file->read (file, &header, 8);
#ifndef BSL_BIGENDIAN
		bslFileSwap (&header[1], 4);
#endif /* BSL_BIGENDIAN */
	}

	/* check 'MThd' */
	if (err == BSL_OK) {
		if (header[0] != MThd_MARKER) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-MID: can't read smf header\n"));
#endif /* BSL_DEBUG */
			err = BSL_ERR_FILE_UNKNOWN_STRUCTURE;
		}
	}

	/* check chunksize */
	if (err == BSL_OK) {
		if (header[1] < 6UL) {
			err = BSL_ERR_FILE_UNKNOWN_STRUCTURE;
		}
	}

	/* read format */
	if (err == BSL_OK) {
		err = file->read (file, &data, 2);
#ifndef BSL_BIGENDIAN
		bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
	}

	/* check format */
	if (err == BSL_OK) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("MID: smf format %d\n"), data);
#endif /* BSL_DEBUG */
		switch (data) {
		case 0:
			obj->nFormat = BSL_MID_FILE_FORMAT_SMF_0;
			break;
		case 1:
			obj->nFormat = BSL_MID_FILE_FORMAT_SMF_1;
			break;
		case 2:
			obj->nFormat = BSL_MID_FILE_FORMAT_SMF_2;
			err = BSL_ERR_FILE_UNSUPPORTED_FORMAT;
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-MID: unknown smf format\n"));
#endif /* BSL_DEBUG */
			break;
		default:
			err = BSL_ERR_FILE_UNSUPPORTED_FORMAT;
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-MID: unknown smf format\n"));
#endif /* BSL_DEBUG */
			break;
		}
	}

	/* read number of tracks */
	if (err == BSL_OK) {
		err = file->read (file, &data, 2);
#ifndef BSL_BIGENDIAN
		bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
	}

	/* check tracks */
	if (err == BSL_OK) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("MID: smf tracks %u\n"), data);
#endif /* BSL_DEBUG */
		if (0 == data) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-MID: 0 tracks\n"));
#endif /* BSL_DEBUG */
			err = BSL_ERR_FILE_UNSUPPORTED_FORMAT;
		}
		else if (1 < data && obj->nFormat == BSL_MID_FILE_FORMAT_SMF_0) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("WARNING-MID: multiple tracks in format 0\n"));
#endif /* BSL_DEBUG */
		}
		else if (BSL_MID_TRACKS < data) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("WARNING-MID: some smf tracks are igonored.\n"));
#endif /* BSL_DEBUG */
		}
		else {
			obj->nTracks = data;
		}
	}

	/* read division */
	if (err == BSL_OK) {
		err = file->read (file, &data, 2);
#ifndef BSL_BIGENDIAN
		bslFileSwap (&data, 2);
#endif /* BSL_BIGENDIAN */
	}

	/* check division */
	if (err == BSL_OK) {
#ifdef BSL_DEBUG
		bslTrace (_TEXT("MID: smf division %u\n"), data);
#endif /* BSL_DEBUG */
		if (data & _SMPTE_DIVISION) {
#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-MID: smf division is SMPTE format\n"));
#endif /* BSL_DEBUG */
			err = BSL_ERR_FILE_UNSUPPORTED_FORMAT;
		}
#ifdef BSL_MID_INCLUDE_MIDICLOCK
		else if ((data % 24) != 0) {
	#ifdef BSL_DEBUG
			bslTrace (_TEXT("ERROR-MID: this smf division (%hu[TPQN]) is not supported for generating midi clocls\n"), data);
	#endif /* BSL_DEBUG */
			err = BSL_ERR_FILE_UNSUPPORTED_FORMAT;
		}
#endif /* BSL_MID_INCLUDE_MIDICLOCK */
		else {
			obj->nDivision = data;
		}
	}

	if (err == BSL_OK ) {
		if (6UL < header[1]) {
			err = file->seek (file, (long) (header[1] - 6UL), BSL_FILE_SEEK_CURRENT);
		}
	}

	if (err == BSL_OK ) {
		int tracks = 0;
		while (err == BSL_OK && tracks < BSL_MID_TRACKS && tracks < obj->nTracks) {
			BSLMidParserTrack *trk = &obj->tTrack[tracks];
			DWORD header[2];
			unsigned long offset;
			
			/* read track chunk header */
			if ((err = file->read (file, &header, 8)) != BSL_OK) {
				break;
			}
#ifndef BSL_BIGENDIAN
			bslFileSwap (&header[1], 4);
#endif /* BSL_BIGENDIAN */

			if (header[0] != MTrk_MARKER) {
#ifdef BSL_DEBUG
				bslTrace (_TEXT("ERROR-MID: can't find MTrk header\n"));
#endif /* BSL_DEBUG */
				break;
			}

			offset = file->getPos (file);
			if ((err = trk->parse (trk, obj, offset, header[1])) != BSL_OK) {
				break;
			}
#ifdef BSL_MID_INCLUDE_TRACK_NAME
	#ifdef BSL_DEBUG
			if (trk->getName (trk)) {
				bslTrace (_TEXT("MID: smf track%02d - %s\n"), tracks, trk->getName (trk));
			}
			else {
				bslTrace (_TEXT("MID: smf track%02d - <untitled>\n"), tracks);
			}
	#endif /* BSL_DEBUG */
#endif /* BSL_MID_INCLUDE_TRACK_NAME */

			tracks++;

			/* seek to end of track */
			if ((err = file->seek (file, (long) (offset + header[1]), BSL_FILE_SEEK_START)) != BSL_OK) {
				break;
			}
		}

		if (0 < tracks) {
			err = BSL_OK; /* 途中エラーがあってもそれまで取り込めたトラックだけで進める */
			obj->nPlayedTracks = tracks;
		}
		else {
			err = BSL_ERR_FILE_UNKNOWN_STRUCTURE;
		}
	}

#ifdef BSL_DEBUG
	if (err == BSL_OK) {
		bslTrace (_TEXT("MID : %d tracks (%d tracks will be played)\n"), obj->getTracks (obj), obj->nPlayedTracks);
		bslTrace (_TEXT("\n"));
	}
#endif /* BSL_DEBUG */

	if (err == BSL_OK) {
		err = obj->rewind (obj, 0UL);
	}

	return err;
}

/*---------------------------------------------------------------------------*/
static BSLErr _rewind
	(
	BSLMidParser *obj,
	DWORD startReadtick
	)
{
	BSLErr err = BSL_OK;

	if (err == BSL_OK) {
		int i;
		for (i = 0; i < obj->nPlayedTracks; i++) {
			if ((err = obj->tTrack[i].rewind (&obj->tTrack[i])) != BSL_OK) {
				break;
			}
		}
	}

	if (err == BSL_OK) {
		err = _bslMidParserRewind (obj, startReadtick);
	}

	return err;
}

#pragma mark -
/*---------------------------------------------------------------------------*/
void bslMidParserSmf
	(
	BSLMidParser *obj
	)
{
	int i;

	bslMidParser (obj);

	obj->parse = _bslMidParserSmfParse;
	obj->rewind = _rewind;

	obj->nType = BSL_MID_FILE_TYPE_SMF;

	for (i = 0; i < BSL_MID_TRACKS; i++) {
		bslMidParserTrackSmf (&obj->tTrack[i]);
	}
}

