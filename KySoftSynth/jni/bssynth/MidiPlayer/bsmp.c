/* bsmp.cpp - */

/* Copyright (c) 2002-2017 bismark LLC */

/*
modification history
--------------------
*/

/* 
DESCRIPTION 
*/

/* includes */

#include "BSL.h"
#include "BSLMem.h"

#include "bsmpPlayer.h"

#if BSL_IOS
	#include <CoreFoundation/CFBundle.h>
#elif BSL_WINDOWS
/*	#define BSMP_EXPORTS 1  / * automatically set by VC */
#else /* BSL_WINDOWS */
	#define BSMP_EXPORTS 1
#endif /* BSL_WINDOWS */
#include "SynthEngine/bsse.h"
#include "bsmp.h"

#if BSL_ANDROID
#include <assert.h>
#include <jni.h>
#include <string.h>
#include <android/log.h>

#include "BSLWavOutOpenSLES.h"
#endif /* BSL_ANDROID */

/* defines */

#define BSMP_VERSION _TEXT(BSSYNTH_VERSION)

#if BSL_WINDOWS
	#define _DLS_IN_RESOURCE
	#include "resource.h"
	#include "resource_dls.h"
#endif

#define _CHECK_PARAMETER_ \
	LOCAL_HANDLE *local = (LOCAL_HANDLE *) handle; \
	if (!handle) { \
		return BSMP_ERR_INVALID_HANDLE; \
	} \
	if (local->size != sizeof (LOCAL_HANDLE)) { \
		return BSMP_ERR_INVALID_HANDLE; \
	} \

/* typedefs */

typedef struct
{
	int size;
	BSMPPlayer player;
#ifdef _DLS_IN_RESOURCE
	char *dls;
#endif /* _DLS_IN_RESOURCE */
} LOCAL_HANDLE;

/* locals */

#if BSL_WINDOWS
HANDLE s_hInstance;
static HWND s_hWnd = NULL;
#endif /* BSL_WINDOWS */

#ifdef BSL_WAV_OUT_VOLUME
	static int s_nVolume = BSMP_VOLUME_MAX;
#endif /* BSL_WAV_OUT_VOLUME */

/* function declarations */

static BSMP_ERR _ctrl (BSMP_HANDLE handle, BSMP_CTRL ctrl, void *data, int size);
static BSMP_ERR _error (BSLErr err);
static void _version (BSMP_HANDLE handle, LPTSTR engine, int engineSize, LPTSTR player, int playerSize);


#ifndef BSMP_STATIC
#if BSL_WINDOWS
/*---------------------------------------------------------------------------*/
BOOL WINAPI DllMain
	(
	HANDLE hInstDLL,
	DWORD dwReason,
	LPVOID pReserved
	)
{
	s_hInstance = hInstDLL;
	return TRUE;
}
#endif /* BSL_WINDOWS */
#endif /* BSMP_STATIC */

#pragma mark -
/*---------------------------------------------------------------------------*/
static BSMP_ERR __initialize
	(
	BSMP_HANDLE *handle,
	BSMP_CALLBACK callback,
	void *user,
	LPCTSTR libraryPath,
	char *libraryAddress,
	unsigned long librarySize,
	void *target,
	const unsigned char *key
	)
{
	BSMP_ERR err = BSMP_OK;
	LOCAL_HANDLE *local = NULL;

#ifdef BSL_DEBUG
	bslTrace (_TEXT("bsmp: init\n"));
#endif /* BSL_DEBUG */


#if 0 /* checked in engine */
	/* protection */
	switch (bssynthProtectionLoadKey2 (key)) {
	case BSSYNTH_PROTECTION_RESULT_OK:
#ifdef BSL_IOS
		if (bssynthProtectionGetTerm () == 0L) {
			/* メインバンドルを取得 */
			CFBundleRef mainBundle = CFBundleGetMainBundle ();
			if (mainBundle) {
				/* メインバンドルからURLを取得 */
				CFURLRef urlRef = CFBundleCopyExecutableURL (mainBundle);
				if (urlRef) {
					/* ファイル名のとこだけとりだす */
					CFStringRef strRef = CFURLCopyLastPathComponent (urlRef);
					if (strRef) {
						char appname[128];
						CFStringGetCString (strRef, appname, 128, CFStringGetSystemEncoding ());
						if (_tcscmp (appname, bssynthProtectionGetUser ()) != 0) {
							printf ("bssynth: application name <%s> does not match to key <%s>\n", appname, bssynthProtectionGetUser ());
							err = BSMP_ERR_PROTECTION;
						}
						CFRelease (strRef);
					}
					else {
						printf ("bssynth: can not get application path component\n");
						err = BSMP_ERR_PROTECTION;
					}
					CFRelease (urlRef);
				}
				else {
					printf ("bssynth: can not get application executable url\n");
					err = BSMP_ERR_PROTECTION;
				}
				CFRelease (mainBundle);
			}
			else {
				printf ("bssynth: can not get application main bundle\n");
				err = BSMP_ERR_PROTECTION;
			}
		}
#endif /* BSL_IOS */
		break;
	case BSSYNTH_PROTECTION_RESULT_EXPIRED:
	case BSSYNTH_PROTECTION_RESULT_ERR:
	default:
		err = BSMP_ERR_PROTECTION;
		break;
	}
#endif

	if (err == BSMP_OK) {
		local = (LOCAL_HANDLE *) bslMemAlloc (sizeof (LOCAL_HANDLE));
		if (local == NULL) {
			err = BSMP_ERR_MEMORY;
		}
		else {
			memset (local, 0, sizeof (LOCAL_HANDLE));
			local->size = sizeof (LOCAL_HANDLE);
		}
	}

#ifdef _DLS_IN_RESOURCE
	if (err == BSMP_OK && (!libraryPath || _tcslen (libraryPath) == 0) && !libraryAddress) {
		HRSRC hrsrc = NULL;
		HGLOBAL hglobal = NULL;
		LPBYTE data = NULL;
		DWORD rsize;

		if (err == BSMP_OK) {
			if ((hrsrc = FindResource ((HMODULE) s_hInstance, MAKEINTRESOURCE(IDR_DLS1), _TEXT("DLS"))) == NULL) {
/*					MessageBox (NULL, _TEXT("FindResource"), _TEXT("error"), MB_OK); */
				err = BSMP_ERR_RESOURCE;
			}
		}
		if (err == BSMP_OK) {
			if ((rsize = SizeofResource ((HMODULE) s_hInstance, hrsrc)) == 0UL) {
/*					MessageBox (NULL, _TEXT("SizeofResource"), _TEXT("error"), MB_OK); */
				err = BSMP_ERR_RESOURCE;
			}
		}
		if (err == BSMP_OK) {
			if ((hglobal = LoadResource ((HMODULE) s_hInstance, hrsrc)) == NULL) {
/*					MessageBox (NULL, _TEXT("LoadResource"), _TEXT("error"), MB_OK); */
				err = BSMP_ERR_RESOURCE;
			}
		}
		if (err == BSMP_OK) {
			if ((data = (LPBYTE) LockResource (hglobal)) == NULL) {
/*					MessageBox (NULL, _TEXT("LockResource"), _TEXT("error"), MB_OK); */
				err = BSMP_ERR_RESOURCE;
			}
		}
		if (err == BSMP_OK) {
			local->dls = (char *) bslMemAlloc (sizeof (char) * rsize + 4); /* why +4 required? */
			if (local->dls == NULL) {
				err = BSMP_ERR_MEMORY;
			}
		}
		if (err == BSMP_OK) {
			memcpy (local->dls, data, rsize);
			libraryAddress = local->dls;
			librarySize = rsize;
		}
	}
#endif /* _DLS_IN_RESOURCE */

	if (err == BSMP_OK) {
		bsmpPlayer (&local->player, libraryPath, libraryAddress, librarySize);
		local->player.setCallback (&local->player, callback, user);
		err = local->player.initialize (&local->player, key, target);
	}

	if (err == BSMP_OK) {
#if BSL_WINDOWS
		s_hWnd = (HWND) target;
#endif /* BSL_WINDOWS */
		local->player.bsmpHandle = *handle = (BSMP_HANDLE) local;
#ifdef BSL_WAV_OUT_VOLUME
		_ctrl (*handle, BSMP_CTRL_GET_MASTER_VOLUME, &s_nVolume, sizeof (int));
#endif /* BSL_WAV_OUT_VOLUME */

		{
			char engine[64];
			char player[64];
#if BSL_ANDROID
			char buf[256];
			_version ((BSMP_HANDLE) local, engine, 64, player, 64);
			sprintf (buf, "Engine Library [%s] + MIDI Player Library [%s]\n", engine, player);
			__android_log_print (3, "<bssynth>", "%s", buf);
#else
			_version ((BSMP_HANDLE) local, engine, 64, player, 64);
			printf ("<bssynth> Engine Library [%s] + MIDI Player Library [%s]\n", engine, player);
#endif
		}
	}
	else {
		if (local) {
#ifdef _DLS_IN_RESOURCE
			if (local->dls) bslMemFree (local->dls);
#endif
			bslMemFree (local);
		}
	}

	return err;
}

/*---------------------------------------------------------------------------*/
static BSMP_ERR _initialize
	(
	BSMP_HANDLE *handle,
	BSMP_CALLBACK callback,
	void *user,
	void *target,
	const unsigned char *key
	)
{
	return __initialize (handle, callback, user, NULL, NULL, 0UL, target, key);
}

/*---------------------------------------------------------------------------*/
static BSMP_ERR _initializeWithSoundLib
	(
	BSMP_HANDLE *handle,
	BSMP_CALLBACK callback,
	void *user,
	LPCTSTR libraryPath,
	void *target,
	const unsigned char *key
	)
{
	return __initialize (handle, callback, user, libraryPath, NULL, 0UL, target, key);
}

/*---------------------------------------------------------------------------*/
static BSMP_ERR _initializeWithSoundLibMemory
	(
	BSMP_HANDLE *handle,
	BSMP_CALLBACK callback,
	void *user,
	char *libraryAddress,
	unsigned long librarySize,
	void *target,
	const unsigned char *key
	)
{
	return __initialize (handle, callback, user, NULL, libraryAddress, librarySize, target, key);
}

/*---------------------------------------------------------------------------*/
static BSMP_ERR __exit
	(
	BSMP_HANDLE handle
	)
{
	_CHECK_PARAMETER_

#ifdef BSL_DEBUG
	bslTrace (_TEXT("bsmp: exit\n"));
#endif /* BSL_DEBUG */

	local->player.setCallback (&local->player, 0, 0);

	bsmpPlayerExit (&local->player);

#ifdef _DLS_IN_RESOURCE
	if (local->dls != NULL) {
		bslMemFree (local->dls);
		local->dls = NULL;
	}
#endif /* _DLS_IN_RESOURCE */
	bslMemFree (local);

	return BSMP_OK;
}

/*---------------------------------------------------------------------------*/
static BSMP_ERR _error
	(
	BSLErr err /* internal error code */
	)
{
	switch (err) {
	case BSL_OK:
		return BSMP_OK;

	/* library errors */
	case BSL_ERR_FILE_OPEN:
	case BSL_ERR_FILE_CREATE:
	case BSL_ERR_FILE_SIZE:
	case BSL_ERR_FILE_READ:
	case BSL_ERR_FILE_WRITE:
	case BSL_ERR_FILE_SEEK:
	case BSL_ERR_FILE_NOT_READY:
	case BSL_ERR_FILE_UNKNOWN_STRUCTURE:
	case BSL_ERR_FILE_RIFF_HEADER:
	case BSL_ERR_FILE_UNSUPPORTED_FORMAT:
		return BSMP_ERR_FILE;
	case BSL_ERR_MEM_ALLOC:
	case BSL_ERR_MEM_HEAP_CREATE:
	case BSL_ERR_MEM_HEAP_DESTROY:
		return BSMP_ERR_MEMORY;
	case BSL_ERR_PARAM_WRONG:
	case BSL_ERR_PARAM_RANGE:
		return BSMP_ERR_PARAM;
	case BSL_ERR_WAV_OUT_OPEN:
	case BSL_ERR_WAV_OUT_CLOSE:
	case BSL_ERR_WAV_OUT_START:
	case BSL_ERR_WAV_OUT_STOP:
	case BSL_ERR_WAV_OUT_RESTART:
	case BSL_ERR_WAV_OUT_RESET:
	case BSL_ERR_WAV_OUT_PAUSE:
#if BSL_WINDOWS
	case BSL_ERR_WAV_OUT_PREPARE_HEADER:
	case BSL_ERR_WAV_OUT_UNPREPARE_HEADER:
#endif /* BSL_WINDOWS */
	case BSL_ERR_WAV_OUT_PROCESS:
	case BSL_ERR_WAV_OUT_DATA:
	case BSL_ERR_WAV_SET_SAMPLE_RATE:
	case BSL_ERR_WAV_SET_BITS_PER_SAMPLE:
	case BSL_ERR_WAV_SET_BIT_RATE:
	case BSL_ERR_WAV_SET_BLOCK_SIZE:
	case BSL_ERR_WAV_SET_CHANNELS:
	case BSL_ERR_WAV_SET_BIGENDIAN:
	case BSL_ERR_WAV_SET_UNSIGNED:
	case BSL_ERR_WAV_SET_BUFFERS:
	case BSL_ERR_WAV_SET_CODEC:
	case BSL_ERR_WAV_DEC:
	case BSL_ERR_WAV_ENC:
		return BSMP_ERR_AUDIO_DRIVER;
	case BSL_ERR_MODULE:
		return BSMP_ERR_MODULE;

	default:
		return BSMP_ERR_UNDEFINED;
	}
}

#pragma mark -
/*---------------------------------------------------------------------------*/
static int _getNumDrivers
	(
	BSMP_HANDLE handle
	)
{
	LOCAL_HANDLE *local = (LOCAL_HANDLE *) handle;

	if (!handle) {
		return 0;
	}

	if (local->size != sizeof (LOCAL_HANDLE)) {
		return 0;
	}

	return local->player.getNumDrivers (&local->player);
}

/*---------------------------------------------------------------------------*/
static int _getNumDevice
	(
	BSMP_HANDLE handle,
	LPCTSTR driver
	)
{
	LOCAL_HANDLE *local = (LOCAL_HANDLE *) handle;

	if (!handle) {
		return 0;
	}

	if (local->size != sizeof (LOCAL_HANDLE)) {
		return 0;
	}

	return local->player.getNumDevices (&local->player, driver);
}

/*---------------------------------------------------------------------------*/
static LPCTSTR _getDriverName
	(
	BSMP_HANDLE handle, 
	int index
	)
{
	LOCAL_HANDLE *local = (LOCAL_HANDLE *) handle;

	if (!handle) {
		return NULL;
	}

	if (local->size != sizeof (LOCAL_HANDLE)) {
		return NULL;
	}

	return local->player.getDriverName (&local->player, index);
}

/*---------------------------------------------------------------------------*/
static LPCTSTR _getDeviceName
	(
	BSMP_HANDLE handle, 
	LPCTSTR driver, 
	int index
	)
{
	LOCAL_HANDLE *local = (LOCAL_HANDLE *) handle;

	if (!handle) {
		return NULL;
	}

	if (local->size != sizeof (LOCAL_HANDLE)) {
		return NULL;
	}

	return local->player.getDeviceName (&local->player, driver, index);
}

/*---------------------------------------------------------------------------*/
static BSMP_ERR _open
	(
	BSMP_HANDLE handle,
	LPCTSTR driver,
	LPCTSTR device
	)
{
	_CHECK_PARAMETER_

#ifdef BSL_DEBUG
	bslTrace (_TEXT("bsmp: open\n"));
#endif /* BSL_DEBUG */

	return _error (local->player.open (&local->player, driver, device));
}

/*---------------------------------------------------------------------------*/
static BSMP_ERR _close
	(
	BSMP_HANDLE handle
	)
{
	_CHECK_PARAMETER_

#ifdef BSL_DEBUG
	bslTrace (_TEXT("bsmp: close\n"));
#endif /* BSL_DEBUG */

	return _error (local->player.close (&local->player));
}

#pragma mark -
/*---------------------------------------------------------------------------*/
static BSMP_ERR _start
	(
	BSMP_HANDLE handle
	)
{
	_CHECK_PARAMETER_

#ifdef BSL_DEBUG
	bslTrace (_TEXT("\n"));
	bslTrace (_TEXT("bsmp: start\n"));
#endif /* BSL_DEBUG */

	return _error (local->player.start (&local->player));
}

/*---------------------------------------------------------------------------*/
static BSMP_ERR _stop
	(
	BSMP_HANDLE handle
	)
{
	_CHECK_PARAMETER_

#ifdef BSL_DEBUG
	bslTrace (_TEXT("bsmp: stop\n"));
#endif /* BSL_DEBUG */

	return _error (local->player.stop (&local->player));
}

/*---------------------------------------------------------------------------*/
static BSMP_ERR _seek
	(
	BSMP_HANDLE handle,
	unsigned long tick
	)
{
	_CHECK_PARAMETER_

#ifdef BSL_DEBUG
	bslTrace (_TEXT("bsmp: seek (%lu[tick])\n"), tick);
#endif /* BSL_DEBUG */

#if __LP64__
	return _error (local->player.seek (&local->player, (unsigned int) tick));
#else
	return _error (local->player.seek (&local->player, tick));
#endif
}

/*---------------------------------------------------------------------------*/
static BSMP_ERR _seekWithTime
(
 BSMP_HANDLE handle,
 float time
 )
{
	_CHECK_PARAMETER_
	
#ifdef BSL_DEBUG
	bslTrace (_TEXT("bsmp: seek (%.2f[sec])\n"), time);
#endif /* BSL_DEBUG */
	
	return _error (local->player.seekWithTime (&local->player, time));
}

/*---------------------------------------------------------------------------*/
static int _isPlaying
	(
	BSMP_HANDLE handle
	)
{
	LOCAL_HANDLE *local = (LOCAL_HANDLE *) handle;

	if (!handle) {
		return 0;
	}

	if (local->size != sizeof (LOCAL_HANDLE)) {
		return 0;
	}

	return (int) local->player.m_wavOut.isPlaying (&local->player.m_wavOut);
}

#pragma mark -
/*---------------------------------------------------------------------------*/
static BSMP_ERR _bounce
	(
	BSMP_HANDLE handle,
	LPCTSTR path, 
	BSMP_WAVE_FILE type, 
	int (*updater) (int percent, void *updateParam), 
	void *updateParam
	)
{
#ifdef BSMP_EXPORT_WAVE
	LOCAL_HANDLE *local = (LOCAL_HANDLE *) handle;
	BSLErr err;

	if (!handle) {
		return BSMP_ERR_INVALID_HANDLE;
	}

	if (local->size != sizeof (LOCAL_HANDLE)) {
		return BSMP_ERR_INVALID_HANDLE;
	}

	if (path) {
		err = local->player.bounce (&local->player, path, type, updater, updateParam);
	}
	else {
		err = local->player.bounce (&local->player, NULL, type, updater, updateParam);
	}

	return _error (err);	
#else /* BSMP_EXPORT_WAVE */
	return BSMP_ERR_NOT_SUPPORTED;
#endif /* BSMP_EXPORT_WAVE */
}

#pragma mark -
/*---------------------------------------------------------------------------*/
static BSMP_ERR _setFile 
	(
	BSMP_HANDLE handle,
	LPCTSTR path
	)
{
	LOCAL_HANDLE *local = (LOCAL_HANDLE *) handle;
	BSLErr err = BSL_OK;

	if (!handle) {
		return BSMP_ERR_INVALID_HANDLE;
	}

	if (local->size != sizeof (LOCAL_HANDLE)) {
		return BSMP_ERR_INVALID_HANDLE;
	}

	if (err == BSL_OK) {
		err = local->player.set (&local->player, path);
	}

	return _error (err);	
}

/*---------------------------------------------------------------------------*/
static BSMP_ERR _setFileMemory 
	(
	BSMP_HANDLE handle,
	char *address,
	unsigned long size
	)
{
	LOCAL_HANDLE *local = (LOCAL_HANDLE *) handle;
	BSLErr err = BSL_OK;

	if (!handle) {
		return BSMP_ERR_INVALID_HANDLE;
	}

	if (local->size != sizeof (LOCAL_HANDLE)) {
		return BSMP_ERR_INVALID_HANDLE;
	}

	if (err == BSL_OK) {
		err = local->player.setMemory (&local->player, address, size);
	}

	return _error (err);	
}

/*---------------------------------------------------------------------------*/
static BSMP_ERR _getFileMemory
	(
	BSMP_HANDLE handle,
	char **address,
	unsigned long *size
	)
{
	_CHECK_PARAMETER_

	{
#ifdef BSL_MID_INCLUDE_FILE_TYPE_MCOMP
		BSLMidParser *midParser = &local->player.m_midParser;
		if (midParser->getType (midParser) == BSL_MID_FILE_TYPE_MCOMP) {
			*address = midParser->getDecoded (midParser);
			*size = midParser->getDecodedSize (midParser);
		}
		else
#endif /* BSL_MID_INCLUDE_FILE_TYPE_MCOMP */
		{
			BSLFile *file = &local->player.m_file;
			*address = file->getAddress (file);
			*size = file->getLength (file);
		}
	}

	return BSMP_OK;
}

/*---------------------------------------------------------------------------*/
static BSMP_ERR _getFileInfo 
	(
	BSMP_HANDLE handle,
	int *format,
	unsigned short *division,
	unsigned long *totaltick,
	unsigned long *totaltime
	)
{
	_CHECK_PARAMETER_

	if (format) {
		BSLMidParser *smf = &local->player.m_midParser;
		*format = (int) smf->getFormat (smf);
	}

	if (division) {
		BSLMidParser *smf = &local->player.m_midParser;
		*division = (WORD) smf->getDivision (smf);
	}

	if (totaltick) {
		*totaltick = local->player.getTotalTick (&local->player);
	}

	if (totaltime) {
		*totaltime = local->player.getTotalTime (&local->player);
	}

	return BSMP_OK;
}

#pragma mark -
/*---------------------------------------------------------------------------*/
static void _version
	(
	BSMP_HANDLE handle, 
	LPTSTR engine,
	int engineSize,
	LPTSTR player,
	int playerSize
	)
{
	TCHAR ver[128];
	LOCAL_HANDLE *local = (LOCAL_HANDLE *) handle;

	if (!handle) {
		return;
	}

	if (local->size != sizeof (LOCAL_HANDLE)) {
		return;
	}

	/* bsse */
	if (engine) {
		local->player.bsseFunc->version (local->player.bsseHandle, engine, engineSize);
	}

	/* bsmp */
#if (_MSC_VER >= 1400) && (!defined _WIN32_WCE)
	_tcscpy_s (ver, sizeof (ver) / sizeof (TCHAR), BSMP_VERSION);
#else
	_tcscpy (ver, BSMP_VERSION);
#endif

#if (_MSC_VER >= 1400) && (!defined _WIN32_WCE)
#define _ADD_OPTION_(str) _tcscat_s (ver, 128, _TEXT(str))
#else
#define _ADD_OPTION_(str) _tcscat (ver, _TEXT(str))
#endif

#ifdef BSMP_AVGM
	_ADD_OPTION_("/AVGM");
#endif /* BSMP_AVGM */

#ifdef BSMP_INCLUDE_WAVE
	_ADD_OPTION_("/WAVE");
#endif /* BSMP_INCLUDE_WAVE */

#ifdef BSSYNTH_GS
	#ifdef BSSYNTH_88
	_ADD_OPTION_("/88");
	#else
	_ADD_OPTION_("/GS");
	#endif
#endif /* BSSYNTH_GS */

#ifdef BSSYNTH_SEGA
	_ADD_OPTION_("/SEGA");
#endif /* BSSYNTH_SEGA */

#ifdef BSSYNTH_SHE
	_ADD_OPTION_("/SHE");
#endif /* BSSYNTH_SHE */

#ifdef BSSYNTH_KT
	_ADD_OPTION_("/KT");
#endif /* BSSYNTH_KT */

#ifdef BSSYNTH_BS
	_ADD_OPTION_("/bs");
#endif /* BSSYNTH_BS */

#ifdef BSSYNTH_KY
	_ADD_OPTION_("/KR");
#endif /* BSSYNTH_KY */

#if __LP64__
	_ADD_OPTION_("/64bit");
#endif

#ifdef BSSYNTH_FLOAT
	_ADD_OPTION_("/Hi-Res");
#endif /* BSSYNTH_FLOAT */
	
	if (player) {
		_tcsncpy_(player, ver, playerSize - 1);
	}
}

/*---------------------------------------------------------------------------*/
static BSMP_ERR _ctrl
	(
	BSMP_HANDLE handle, 
	BSMP_CTRL ctrl, 
	void *data,
	int size
	)
{
	BSLErr err = BSL_OK;
	BSMPPlayer *player = NULL;
	LOCAL_HANDLE *local = (LOCAL_HANDLE *) handle;

	if (!handle) {
		return BSMP_ERR_INVALID_HANDLE;
	}

	if (local->size != sizeof (LOCAL_HANDLE)) {
		return BSMP_ERR_INVALID_HANDLE;
	}
	player = &local->player;

	switch (ctrl) {
	case BSMP_CTRL_SET_MASTER_VOLUME:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		{
			int volume = *(int *) data;
#ifdef BSL_WAV_OUT_VOLUME
			DWORD value;

			if (volume < BSMP_VOLUME_MIN) {
				volume = BSMP_VOLUME_MIN;
			}
			else if (volume > BSMP_VOLUME_MAX) {
				volume = BSMP_VOLUME_MAX;
			}

			value 
				= (DWORD) BSL_WAV_OUT_VOLUME_MAX 
				* (DWORD) volume 
				/ (DWORD) (BSMP_VOLUME_MAX - BSMP_VOLUME_MIN);

			player->m_wavOut.setVolume (&player->m_wavOut, value);
			s_nVolume = volume;
#else /* BSL_WAV_OUT_VOLUME */
	#ifdef BSSYNTH_KT
			if (volume < BSMP_KT_VOLUME_MIN) volume = BSMP_KT_VOLUME_MIN;
			else if (BSMP_KT_VOLUME_MAX < volume) volume = BSMP_KT_VOLUME_MAX;
	#else /* BSSYNTH_KT */
			if (volume < BSMP_VOLUME_MIN) volume = BSMP_VOLUME_MIN;
			else if (BSMP_VOLUME_MAX < volume) volume = BSMP_VOLUME_MAX;
	#endif /* BSSYNTH_KT */
			player->setVolume (player, volume);
#endif /* BSL_WAV_OUT_VOLUME */
			return BSMP_OK;
		}

	case BSMP_CTRL_GET_MASTER_VOLUME:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
#ifdef BSL_WAV_OUT_VOLUME
		{
			DWORD vol = player->m_wavOut.getVolume (&player->m_wavOut);
			s_nVolume = (int) (0.5f + (float) vol * (float) (BSMP_VOLUME_MAX - BSMP_VOLUME_MIN) / (float) BSL_WAV_OUT_VOLUME_MAX);
		}
		*(int *) data = s_nVolume;
#else /* BSL_WAV_OUT_VOLUME */
		*(int *) data = player->iVolume;
#endif /* BSL_WAV_OUT_VOLUME */
		return BSMP_OK;

	case BSMP_CTRL_SET_MASTER_KEY:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		player->setKey (player, *(int *) data);
		return BSMP_OK;

	case BSMP_CTRL_GET_MASTER_KEY:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		*(int *) data = player->iKey;
		return BSMP_OK;

	case BSMP_CTRL_SET_MASTER_TUNE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		player->setTune (player, *(int *) data);
		return BSMP_OK;

	case BSMP_CTRL_GET_MASTER_TUNE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		*(int *) data = player->nTune;
		return BSMP_OK;

	case BSMP_CTRL_SET_SPEED:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		player->setSpeed (player, *(int *) data);
		return BSMP_OK;

	case BSMP_CTRL_GET_SPEED:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		*(int *) data = player->iSpeed;
		return BSMP_OK;

	case BSMP_CTRL_SET_GUIDE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		player->setGuide (player, *(int *) data);
		return BSMP_OK;

	case BSMP_CTRL_GET_GUIDE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		*(int *) data = player->iGuide;
		return BSMP_OK;

	case BSMP_CTRL_SET_GUIDE_MAIN_CH:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		player->setGuideMainCh (player, *(int *) data);
		return BSMP_OK;

	case BSMP_CTRL_GET_GUIDE_MAIN_CH:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		*(int *) data = player->iGuideMainCh;
		return BSMP_OK;

	case BSMP_CTRL_SET_GUIDE_SUB_CH:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		player->setGuideSubCh (player, *(int *) data);
		return BSMP_OK;

	case BSMP_CTRL_GET_GUIDE_SUB_CH:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		*(int *) data = player->iGuideSubCh;
		return BSMP_OK;

	case BSMP_CTRL_SET_REVERB:
#ifdef BSL_MID_INCLUDE_EFFECT1
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		player->setEffect1Switch (player, *(int *) data);
		return BSMP_OK;
#else /* BSL_MID_INCLUDE_EFFECT1 */
		return BSMP_ERR_NOT_SUPPORTED;
#endif /* BSL_MID_INCLUDE_EFFECT1 */

	case BSMP_CTRL_GET_REVERB:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
#ifdef BSL_MID_INCLUDE_EFFECT1
		*(int *) data = (int) player->getEffect1Switch (player);
		return BSMP_OK;
#else /* BSL_MID_INCLUDE_EFFECT1 */
		*(int *) data = FALSE;
		return BSMP_ERR_NOT_SUPPORTED;
#endif /* BSL_MID_INCLUDE_EFFECT1 */

	case BSMP_CTRL_GET_REVERB_AVAILABLE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
#ifdef BSL_MID_INCLUDE_EFFECT1
		*(int *) data = player->effect1Available (player);
#else /* BSL_MID_INCLUDE_EFFECT1 */
		*(int *) data = FALSE;
#endif /* BSL_MID_INCLUDE_EFFECT1 */
		return BSMP_OK;

	case BSMP_CTRL_SET_CHORUS:
#ifdef BSL_MID_INCLUDE_EFFECT3
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		player->setEffect3Switch (player, *(int *) data);
		return BSMP_OK;
#else /* BSL_MID_INCLUDE_EFFECT3 */
		return BSMP_ERR_NOT_SUPPORTED;
#endif /* BSL_MID_INCLUDE_EFFECT3 */

	case BSMP_CTRL_GET_CHORUS:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
#ifdef BSL_MID_INCLUDE_EFFECT3
		*(int *) data = player->getEffect3Switch (player);
		return BSMP_OK;
#else /* BSL_MID_INCLUDE_EFFECT3 */
		*(int *) data = FALSE;
		return BSMP_ERR_NOT_SUPPORTED;
#endif /* BSL_MID_INCLUDE_EFFECT3 */

	case BSMP_CTRL_GET_CHORUS_AVAILABLE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
#ifdef BSL_MID_INCLUDE_EFFECT3
		*(int *) data = player->effect3Available (player);
#else /* BSL_MID_INCLUDE_EFFECT3 */
		*(int *) data = FALSE;
#endif /* BSL_MID_INCLUDE_EFFECT3 */
		return BSMP_OK;

	case BSMP_CTRL_SET_DELAY:
#ifdef BSL_MID_INCLUDE_EFFECT4
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		player->setEffect4Switch (player, *(BOOL *) data);
		return BSMP_OK;
#else /* BSL_MID_INCLUDE_EFFECT4 */
		return BSMP_ERR_NOT_SUPPORTED;
#endif /* BSL_MID_INCLUDE_EFFECT4 */

	case BSMP_CTRL_GET_DELAY:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
#ifdef BSL_MID_INCLUDE_EFFECT4
		*(int *) data = player->getEffect4Switch (player);
		return BSMP_OK;
#else /* BSL_MID_INCLUDE_EFFECT4 */
		*(int *) data = FALSE;
		return BSMP_ERR_NOT_SUPPORTED;
#endif /* BSL_MID_INCLUDE_EFFECT4 */

	case BSMP_CTRL_GET_DELAY_AVAILABLE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
#ifdef BSL_MID_INCLUDE_EFFECT4
		*(int *) data = player->effect4Available (player);
#else /* BSL_MID_INCLUDE_EFFECT4 */
		*(int *) data = FALSE;
#endif /* BSL_MID_INCLUDE_EFFECT4 */
		return BSMP_OK;

	case BSMP_CTRL_SET_SAMPLE_RATE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		err = player->setSampleRate (player, *(int *) data);
		return _error (err);

	case BSMP_CTRL_GET_SAMPLE_RATE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		*(int *) data = player->m_wavOut.getSampleRate (&player->m_wavOut);
		return BSMP_OK;

	case BSMP_CTRL_SET_BLOCK_SIZE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (long)) return BSMP_ERR_PARAM;
		err = local->player.setBlockSize (&local->player, *(long *) data);
		return (_error (err));

	case BSMP_CTRL_GET_BLOCK_SIZE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (long)) return BSMP_ERR_PARAM;
		*(long *) data = local->player.m_wavOut.getBlockSize (&local->player.m_wavOut);
		return BSMP_OK;

	case BSMP_CTRL_SET_CHANNELS:
		return BSMP_ERR_NOT_SUPPORTED;

	case BSMP_CTRL_GET_CHANNELS:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		*(int *) data = player->m_wavOut.getChannels (&player->m_wavOut);
		return BSMP_OK;

	case BSMP_CTRL_SET_POLY:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		err = player->setPoly (player, *(int *) data);
		return _error (err);	

	case BSMP_CTRL_GET_POLY:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		*(int *) data = player->getPoly (player);
		return BSMP_OK;

#ifdef BSMP_INCLUDE_CALLBACK_QUEUE
	case BSMP_CTRL_SET_CALLBACK_DELAY:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (long)) return BSMP_ERR_PARAM;
		player->setCallbackDelay (player, *(long *) data);
		return BSMP_OK;

	case BSMP_CTRL_GET_CALLBACK_DELAY:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (long)) return BSMP_ERR_PARAM;
		*(long *) data = player->getCallbackDelay (player);
		return BSMP_OK;
#endif /* BSMP_INCLUDE_CALLBACK_QUEUE */

#if (1 < BSSYNTH_MID_PORTS)
	case BSMP_CTRL_SET_PORT_SELECTION_METHOD:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		player->portSelector.nPortSelectionMethod = *(int *) data;
		return BSMP_OK;

	case BSMP_CTRL_GET_PORT_SELECTION_METHOD:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		*(int *) data = player->portSelector.nPortSelectionMethod;
		return BSMP_OK;
#endif

#ifdef BSMP_INCLUDE_WAVE
	case BSMP_CTRL_SET_WAVE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (BSMP_WAVE)) return BSMP_ERR_PARAM;
		{
			BSMP_WAVE *wave = (BSMP_WAVE *) data;
			err = player->tWaveManager.addWave (&player->tWaveManager, wave->index, wave->type, wave->path, wave->triggerMode, (BOOL) wave->allowKeyControl);
			return _error (err);
		}
#endif /* BSMP_INCLUDE_WAVE */

#if BSL_ANDROID
	case BSMP_CTRL_GET_OPEN_SL_ENGINE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (SLObjectItf)) return BSMP_ERR_PARAM;
		if (player->m_wavOut.pInternalData != NULL) {
			BSLWavOutOpenSLES_Internal *intl = (BSLWavOutOpenSLES_Internal *) player->m_wavOut.pInternalData;
			*(SLObjectItf *) data = intl->engineObject;
			return BSMP_OK;
		}
		else return BSMP_ERR_UNDEFINED;
	case BSMP_CTRL_GET_OPEN_SL_ENGINE_INTERFACE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (SLEngineItf)) return BSMP_ERR_PARAM;
		if (player->m_wavOut.pInternalData != NULL) {
			BSLWavOutOpenSLES_Internal *intl = (BSLWavOutOpenSLES_Internal *) player->m_wavOut.pInternalData;
			*(SLEngineItf *) data = intl->engineEngine;
			return BSMP_OK;
		}
		else return BSMP_ERR_UNDEFINED;
#endif /* BSL_ANDROID */

#if (defined BSSYNTH_88)
	case BSMP_CTRL_SEGA_GET_ABSOLUTE_TIME_FOR_FIRST_EVENT:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (float)) return BSMP_ERR_PARAM;
		*(float *) data = player->fSegaAbsoluteTimeForFirstEvent;
		return BSMP_OK;
#endif

#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
	case BSMP_CTRL_KY_GET_RHYTHM_CHANGE_TYPE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		*(int *) data = player->KY_getRhythmChangeType (player);
		return BSMP_OK;
			
	case BSMP_CTRL_KY_SET_RHYTHM_CHANGE_TYPE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		player->KY_setRhythmChangeType (player, *(int *) data);
		return BSMP_OK;
#endif
			
	case BSMP_CTRL_GET_BSSE_FUNC:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (BSSE_FUNC *)) return BSMP_ERR_PARAM;
		*(BSSE_FUNC **) data = player->bsseFunc;
		return BSMP_OK;

	case BSMP_CTRL_GET_BSSE_HANDLE:
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (BSSE_HANDLE *)) return BSMP_ERR_PARAM;
		*(BSSE_HANDLE *) data = player->bsseHandle;
		return BSMP_OK;

	case BSMP_CTRL_GET_TICK: 
		if (!data) return BSMP_ERR_PARAM;
		if (size != sizeof (int)) return BSMP_ERR_PARAM;
		*(int *) data = player->dwWritetick;
		return BSMP_OK;

	default:
		if (10000 <= ctrl && ctrl < 20000) {
			return player->bsseCtrl (player, ctrl, data, size);
		}
#ifdef BSSYNTH_88
		else if (BSMP_CTRL_UGA_GET_TRACK_TYPE <= ctrl && ctrl < (BSMP_CTRL_UGA_GET_TRACK_TYPE + 100)) {
			int m = ((int) ctrl - BSMP_CTRL_UGA_GET_TRACK_TYPE) / BSL_MID_CHANNELS;
			int p = ((int) ctrl - BSMP_CTRL_UGA_GET_TRACK_TYPE) % BSL_MID_CHANNELS;

			if (!data) return BSMP_ERR_PARAM;
			if (size != sizeof (unsigned char)) return BSMP_ERR_PARAM;
			
			if (m < BSSYNTH_MID_PORTS && p < BSL_MID_CHANNELS) {
				*(unsigned char *) data = (unsigned char) player->portSelector.byUgaTrackType[m][p];
			}
		}
#endif /* BSSYNTH_88 */
		break;
	}

	return BSMP_ERR_PARAM;
}

#pragma mark -
/*---------------------------------------------------------------------------*/
BSMP_API BSMP_FUNC *bsmpLoad (void)
{
	static BSMP_FUNC bsmpFunc = {
		sizeof (BSMP_FUNC),

		_initialize,
		_initializeWithSoundLib,
		_initializeWithSoundLibMemory,
		__exit,

		_getNumDrivers,
		_getNumDevice,
		_getDriverName,
		_getDeviceName,
		NULL, /* showDeviceControlPanel */
		_open,
		_close,

		_start,
		_stop,
		_seek,
		_seekWithTime,
		_isPlaying,

		_bounce,

		_setFile,
		_setFileMemory,
		_getFileMemory,
		_getFileInfo,

		_ctrl,
		_version
	};
	return &bsmpFunc;
}
