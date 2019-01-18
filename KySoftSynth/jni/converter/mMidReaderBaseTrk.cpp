/* mMidReaderBaseTrk.cpp - */

/* */

/* 
modification history
--------------------
*/

/*
DESCRIPTION
*/

/* includes */

#include "mDefine.h"
#include "mError.h"
#include "mDebug.h"
#include "mMidDef.h"
#include "mMid.h"
#include "mMidReaderBaseTrk.h"
#include "mMidReaderBase.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forword declarations */

/*******************************************************************************
*
* MMidReaderBaseTrk::MMidReaderBaseTrk - 
*
* RETURNS:
* void
*/
MMidReaderBaseTrk::MMidReaderBaseTrk
	(
	MMidReaderBase *parent
	)
{
	pParent = parent;
	initialize ();
}

/*******************************************************************************
*
* MMidReaderBaseTrk::~MMidReaderBaseTrk - 
*
* RETURNS:
* void
*/
MMidReaderBaseTrk::~MMidReaderBaseTrk ()
{
}

#pragma mark -

MMidReaderBase *MMidReaderBaseTrk::parent (void)
{
	return pParent;
}

void MMidReaderBaseTrk::parentSet (MMidReaderBase *parent)
{
  pParent = parent;
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderBaseTrk::initialize - 
*
* RETURNS:
* error code
*/
int MMidReaderBaseTrk::initialize (void)
{
	iOrgTrk = 0;

	dwReadtick = (DWORD) kMidSmfReadtickMax;

	byPort = kMidPortNull;
	byCh = kMidChNull;
#ifdef M_MID_INCLUDE_OPTSW
	byOptSw = kMidOptionSwNull;
#endif // M_MID_INCLUDE_OPTSW

	bIsFinished = false;

	_tcscpy (cName, M_MID_TRACKNAME_DEF);

	dwChunkSize = 0UL;

	return (OK);
}

/*******************************************************************************
*
* MMidReaderBaseTrk::ready - 
*
* RETURNS:
* error code
*/
int MMidReaderBaseTrk::ready
	(
	int trk, // original track number
	DWORD chunkSize
	)
{
	orgTrkNumberSet (trk);
	dwChunkSize = chunkSize;

	return (OK);
}

/*******************************************************************************
*
* MMidReaderBaseTrk::rewind - 
*
* RETURNS:
* error code
*/
int MMidReaderBaseTrk::rewind (void)
{
	int err = OK;

	bIsFinished = false;

	return (err);
}

/*******************************************************************************
*
* MMidReaderBaseTrk::close - 
*
* RETURNS:
* void
*/
void MMidReaderBaseTrk::close (void)
{
	bIsFinished = true;
}

/*******************************************************************************
*
* MMidReaderBaseTrk::isFinished - 
*
* RETURNS:
* BOOL
*/
BOOL MMidReaderBaseTrk::isFinished (void)
{
	return bIsFinished;
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderBaseTrk::readtickGet - 
*
* RETURNS:
* DWORD
*/
DWORD MMidReaderBaseTrk::readtickGet (void)
{
	return dwReadtick;
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderBaseTrk::portGet - 
*
* RETURNS:
* BYTE
*/
BYTE MMidReaderBaseTrk::portGet (void)
{
	return byPort;
}

/*******************************************************************************
*
* MMidReaderBaseTrk::portSet - 
*
* RETURNS:
* void
*/
void MMidReaderBaseTrk::portSet
	(
	BYTE port
	)
{
	byPort = port;
}

/*******************************************************************************
*
* MMidReaderBaseTrk::channelGet - 
*
* RETURNS:
* BYTE
*/
BYTE MMidReaderBaseTrk::channelGet (void)
{
	return byCh;
}

/*******************************************************************************
*
* MMidReaderBaseTrk::channelSet - 
*
* RETURNS:
* void
*/
void MMidReaderBaseTrk::channelSet
	(
	BYTE ch
	)
{
	byCh = ch;
}

#ifdef M_MID_INCLUDE_OPTSW
/*******************************************************************************
*
* MMidReaderBaseTrk::optionSwitchGet - 
*
* RETURNS:
* BYTE
*/
BYTE MMidReaderBaseTrk::optionSwitchGet (void)
{
	return byOptSw;
}

/*******************************************************************************
*
* MMidReaderBaseTrk::optionSwitchSet - 
*
* RETURNS:
* void
*/
void MMidReaderBaseTrk::optionSwitchSet
	(
	BYTE optSw
	)
{
	byOptSw = optSw;
}

#endif // M_MID_INCLUDE_OPTSW

/*******************************************************************************
*
* MMidReaderBaseTrk::orgTrkNumberSet - 
*
* RETURNS:
* void
*/
void MMidReaderBaseTrk::orgTrkNumberSet
	(
	int trk
	)
{
	iOrgTrk = trk;
}

/*******************************************************************************
*
* MMidReaderBaseTrk::orgTrkNumberGet - 
*
* RETURNS:
* int
*/
int MMidReaderBaseTrk::orgTrkNumberGet (void)
{
	return iOrgTrk;
}

#pragma mark -
/*******************************************************************************
*
* MMidReaderBaseTrk::nameSet -
*
* RETURNS: 
* void
*/
void MMidReaderBaseTrk::nameSet
	(
	LPCTSTR str
	)
{
	_tcsncpy_(cName, str, kNameLength - 1);
}

/*******************************************************************************
*
* MMidReaderBaseTrk::nameGet -
*
* RETURNS: 
* void
*/
void MMidReaderBaseTrk::nameGet
	(
	LPTSTR str,
	int length
	)
{
	if (str) {
		_tcsncpy_(str, cName, length);
	}
#ifdef BSL_DEBUG
	else {
		mPrintf (_TEXT("%s"), cName);
	}
#endif // BSL_DEBUG
}

/*******************************************************************************
 *
 * MMidReaderBaseTrk::nameGet -
 *
 * RETURNS:
 * void
 */
LPCTSTR MMidReaderBaseTrk::nameGet (void)
{
  return cName;
}
