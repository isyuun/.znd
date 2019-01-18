/*
 *  BSLWavRender.c
 *
 *  Copyright (c) 2004 bismark. All rights reserved.
 *
 */

/*!
	@file BSLWavRender.c
	@brief
	Wave Rendering制御オブジェクト
*/

/* includes */

#include "BSL.h"
#include "BSLDebug.h"
#include "BSLError.h"
#include "BSLWav.h"
#include "BSLWavRender.h"

/* defines */

/* typedefs */

/* globals */

/* locals */

/* forward declarations */


/*---------------------------------------------------------------------------*/
static long _getBlockSize (BSLWavRender *obj)
{
	return (obj->lBlockSize);
}

/*---------------------------------------------------------------------------*/
static void _setBuffer (BSLWavRender *obj, BSLWavIOData *data)
{
	obj->pBuffer = data;
}

/*---------------------------------------------------------------------------*/
static BSLWavIOData *_getBuffer (BSLWavRender *obj)
{
	return (obj->pBuffer);
}

/*---------------------------------------------------------------------------*/
static void _setUser (BSLWavRender *obj, void *user)
{
	obj->pUser = user;
}

/*---------------------------------------------------------------------------*/
static void *_getUser (BSLWavRender *obj)
{
	return (obj->pUser);
}

#pragma mark -
/*---------------------------------------------------------------------------*/
BSLErr bslWavRenderSetBlockSize
	(
	BSLWavRender *obj,
	long blockSize
	)
{
	obj->lBlockSize = blockSize;

	return BSL_OK;
}

#pragma mark -
/*---------------------------------------------------------------------------*/
void bslWavRender
	(
	BSLWavRender *obj,
	BSL_WAV_RENDER render,
	void *user
	)
{
	memset (obj, 0, sizeof (BSLWavRender));

	obj->render = render;
	obj->setBlockSize = bslWavRenderSetBlockSize;
	obj->getBlockSize = _getBlockSize;
	obj->setBuffer = _setBuffer;
	obj->getBuffer = _getBuffer;
	obj->setUser = _setUser;
	obj->getUser = _getUser;

	obj->lBlockSize = BSL_WAV_BLOCK_SIZE;

	obj->pUser = user;
}

