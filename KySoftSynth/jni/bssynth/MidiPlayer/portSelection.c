/*
 *  portSelection.c
 *  bssynth
 *
 *  Created by hideo on 11/09/09.
 *  Copyright 2011 bismark. All rights reserved.
 *
 */

#include "bsmpPlayer.h"
#include "portSelection.h"

#ifdef BSSYNTH_KY
#include "bsmp_ky.h"
#endif /* BSSYNTH_KY */

#pragma mark -

BSL_MID_PORT portSelectionGetTrackPortUsingMetaEventPort
	(
	BSLMidParserTrack *obj,
	int index
	)
{
	BSLErr err = BSL_OK;
	BSL_MID_PORT port = BSL_MID_PORT_A;

	if (err == BSL_OK) {
		err = obj->rewind (obj);
	}

	if (err == BSL_OK) {
		while (!obj->isFinished (obj)) {
			BSLMidEvent event;
			if ((err = obj->readEvent (obj, &event)) != BSL_OK) {
				break;
			}

			if (event.byStatus == BSL_MID_META_TYPE_END) {
				break;
			}
			else if (event.byStatus == BSL_MID_META_TYPE_PORT) {
				if (event.nSize == 1) {
					if (event.pData[0] == 0x00) {
						port = BSL_MID_PORT_A;
					}
					else if (event.pData[0] == 0x01) {
						port = BSL_MID_PORT_B;
					}
#if (defined BSSYNTH_KY) && (defined BSSYNTH_KY_RHYTHM_CHANGE)
					else if (BSMP_KY_RHYTHM_PORT <= event.pData[0] && event.pData[0] < (BSMP_KY_RHYTHM_PORT + BSMP_KY_RHYTHM_TYPES)) {
						port = event.pData[0];
					}
#endif
					else {
						port = BSL_MID_PORT_DISABLED;
					}
					break;
				}
			}

			if ((err = obj->incEvent (obj, &event)) != BSL_OK) {
				break;
			}
		}
	}

	return port;
}

BSL_MID_PORT portSelectionGetTrackPortDK
	(
	BSLMidParserTrack *obj,
	int index
	)
{
	BSLErr err = BSL_OK;
	BSL_MID_PORT port = BSL_MID_PORT_A;

	if (err == BSL_OK) {
		err = obj->rewind (obj);
	}

	if (err == BSL_OK) {
		while (!obj->isFinished (obj)) {
			BSLMidEvent event;
			if ((err = obj->readEvent (obj, &event)) != BSL_OK) {
				break;
			}

			if (event.byStatus == BSL_MID_META_TYPE_END) {
				break;
			}
			else if (event.byStatus == 0x09) {
				if (event.nSize == 11) {
					if (event.pData[10] == 0x41) {
						port = BSL_MID_PORT_A;
						break;
					}
					else if (event.pData[10] == 0x42) {
						port = BSL_MID_PORT_B;
						break;
					}
				}
			}

			if ((err = obj->incEvent (obj, &event)) != BSL_OK) {
				break;
			}
		}
	}

	return port;
}

BSL_MID_PORT portSelectionGetTrackPortUga
	(
	BSSYNTH_PORT_SELECTOR *selector,
	BSLMidParserTrack *obj,
	int index,
  int *guideMainCh,
  int *guideSubCh
	)
{
	BSLErr err = BSL_OK;
	BSL_MID_PORT port = BSL_MID_PORT_A;

	if (err == BSL_OK) {
		err = obj->rewind (obj);
	}

	if (err == BSL_OK) {
		TCHAR name[256];
		memset (name, 0, sizeof (TCHAR) * 256);

		while (!obj->isFinished (obj)) {
			BSLMidEvent event;
			if ((err = obj->readEvent (obj, &event)) != BSL_OK) {
				break;
			}

			if (event.byStatus == BSL_MID_META_TYPE_END) {
				break;
			}
			else if (event.byStatus == BSL_MID_META_TYPE_NAME) {
	#ifdef BSL_UNICODE
		#if BSL_WINDOWS
				MultiByteToWideChar (CP_ACP, 0, (char *) event.pData, event.nSize, name, 256);
		#else /* BSL_WINDOWS */
			#error not supported
		#endif /* BSL_WINDOWS */
	#else /* BSL_UNICODE */
				memcpy (name, (char *) event.pData, 256 < event.nSize ? 256 : event.nSize);
	#endif /* BSL_UNICODE */
	#ifdef BSL_DEBUG
/*				bslTrace (_TEXT("bsmp: track name = %s¥n"), name); */
	#endif /* BSL_DEBUG */
			}

			if ((err = obj->incEvent (obj, &event)) != BSL_OK) {
				break;
			}
		}

		if (2 <= _tcslen (name)) {
			BYTE TrackType = 0x50; /* nop */
			int ch = 0;
			
			port = BSL_MID_PORT_A;

			if (((_TEXT('0') <= name[0] && name[0] <= _TEXT('9')) || (_TEXT('A') <= name[0] && name[0] <= _TEXT('F')))
				&& ((_TEXT('0') <= name[1] && name[1] <= _TEXT('9')) || (_TEXT('A') <= name[1] && name[1] <= _TEXT('F')))) {
				if (name[0] < _TEXT('A')) TrackType = ((name[0] - _TEXT('0')) << 4);
				else TrackType = ((0xA + name[0] - _TEXT('A')) << 4);
				if (name[1] < _TEXT('A')) TrackType += (name[1] - _TEXT('0'));
				else TrackType += (0xA + name[1] - _TEXT('A'));
			}

			if (3 <= _tcslen (name) && _TEXT('0') <= name[2] && name[2] <= _TEXT('1')) {
				port = (BSL_MID_PORT) ((int) BSL_MID_PORT_A + (name[2] - _TEXT('0')));
			}
			else port = BSL_MID_PORT_DISABLED;

			if (4 <= _tcslen (name) && ((_TEXT('0') <= name[3] && name[3] <= _TEXT('9')) || (_TEXT('A') <= name[3] && name[3] <= _TEXT('F')))) {
				if (name[3] < _TEXT('A')) ch = (name[3] - _TEXT('0'));
				else ch = (0xA + name[3] - _TEXT('A'));
			}

			switch (TrackType) {
			case 0x00: /* tempo */
				break;
			case 0x01: /* exclusive */
				break;
			case 0x12: /* key control disabled */
				if (BSL_MID_PORT_A <= port && port <= BSL_MID_PORT_B) {
					selector->bNoTranspose[port][ch] = TRUE;
					selector->byUgaTrackType[port][ch] = TrackType;
				}
				break;
			case 0x13: /* melody 1*/
				if (BSL_MID_PORT_A <= port && port <= BSL_MID_PORT_B) {
          if (guideMainCh) {
            *guideMainCh = port * 16 + ch;
          }
					selector->byUgaTrackType[port][ch] = TrackType;
				}
				break;
			case 0x14: /* melody 2 */
				if (BSL_MID_PORT_A <= port && port <= BSL_MID_PORT_B) {
          if (guideSubCh) {
            *guideSubCh = port * 16 + ch;
          }
					selector->byUgaTrackType[port][ch] = TrackType;
				}
				break;
			case 0x11: /* rhythm */
			case 0x15: /* bass */
			case 0x16: /* backing */
			case 0x10: /* other */
				if (BSL_MID_PORT_A <= port && port <= BSL_MID_PORT_B) {
					selector->byUgaTrackType[port][ch] = TrackType;
				}
				break;
			case 0xEF: /* empty */
			case 0x50: /* off */
			default:
				port = BSL_MID_PORT_DISABLED;
				break;
			}
		}
		else {
			port = BSL_MID_PORT_DISABLED;
		}
	}

	return port;
}

BSL_MID_PORT portSelectionGetTrackPortSongoku
	(
	BSSYNTH_PORT_SELECTOR *selector,
	BSLMidParserTrack *obj,
	int index
	)
{
	BSLErr err = BSL_OK;
	BSL_MID_PORT port = BSL_MID_PORT_A;

	if (err == BSL_OK) {
		err = obj->rewind (obj);
	}

	if (err == BSL_OK) {
		TCHAR name[256];
		memset (name, 0, sizeof (TCHAR) * 256);

		while (!obj->isFinished (obj)) {
			BSLMidEvent event;
			if ((err = obj->readEvent (obj, &event)) != BSL_OK) {
				break;
			}

			if (event.byStatus == BSL_MID_META_TYPE_END) {
				break;
			}
			else if (event.byStatus == BSL_MID_META_TYPE_NAME) {
	#ifdef BSL_UNICODE
		#if BSL_WINDOWS
				MultiByteToWideChar (CP_ACP, 0, (char *) event.pData, event.nSize, name, 256);
		#else /* BSL_WINDOWS */
			#error not supported
		#endif /* BSL_WINDOWS */
	#else /* BSL_UNICODE */
				memcpy (name, (char *) event.pData, 256 < event.nSize ? 256 : event.nSize);
	#endif /* BSL_UNICODE */
	#ifdef BSL_DEBUG
/*				bslTrace (_TEXT("bsmp: track name = %s¥n"), name); */
	#endif /* BSL_DEBUG */
			}

			if ((err = obj->incEvent (obj, &event)) != BSL_OK) {
				break;
			}
		}

		/* A01 */
		if (3 <= _tcslen (name) && name[0] == _TEXT('A') && _TEXT('0') <= name[1] && name[1] <= _TEXT('9') && _TEXT('0') <= name[2] && name[2] <= _TEXT('9')) {
			port = BSL_MID_PORT_A;
			if (4 <= _tcslen (name) && name[3] == _TEXT('R')) {
				int ch = (name[1] - _TEXT('0')) * 10 + (name[2] - _TEXT('0')) - 1;
				if (0 <= ch && ch < 16) {
					selector->bNoTranspose[0][ch] = TRUE;
				}
			}
		}
		/* B01 */
		else if (3 <= _tcslen (name) && name[0] == _TEXT('B') && _TEXT('0') <= name[1] && name[1] <= _TEXT('9') && _TEXT('0') <= name[2] && name[2] <= _TEXT('9')) {
			port = BSL_MID_PORT_B;
			if (4 <= _tcslen (name) && name[3] == _TEXT('R')) {
				int ch = (name[1] - _TEXT('0')) * 10 + (name[2] - _TEXT('0')) - 1;
				if (0 <= ch && ch < 16) {
					selector->bNoTranspose[1][ch] = TRUE;
				}
			}
		}
		else {
			if (index == 0) port = BSL_MID_PORT_A;
			else port = BSL_MID_PORT_DISABLED;
		}
	}

	return port;
}

BSL_MID_PORT portSelectionGetTrackPortSega
	(
	BSSYNTH_PORT_SELECTOR *selector,
	BSLMidParserTrack *obj,
	int index
	)
{
	BSLErr err = BSL_OK;
	BSL_MID_PORT port = BSL_MID_PORT_A;

	if (err == BSL_OK) {
		err = obj->rewind (obj);
	}

	if (err == BSL_OK) {
		TCHAR name[256];
		memset (name, 0, sizeof (TCHAR) * 256);

		while (!obj->isFinished (obj)) {
			BSLMidEvent event;
			if ((err = obj->readEvent (obj, &event)) != BSL_OK) {
				break;
			}

			if (event.byStatus == BSL_MID_META_TYPE_END) {
				break;
			}
			else if (event.byStatus == BSL_MID_META_TYPE_NAME) {
	#ifdef BSL_UNICODE
		#if BSL_WINDOWS
				MultiByteToWideChar (CP_ACP, 0, (char *) event.pData, event.nSize, name, 256);
		#else /* BSL_WINDOWS */
			#error not supported
		#endif /* BSL_WINDOWS */
	#else /* BSL_UNICODE */
				memcpy (name, (char *) event.pData, 256 < event.nSize ? 256 : event.nSize);
	#endif /* BSL_UNICODE */
	#ifdef BSL_DEBUG
/*				bslTrace (_TEXT("bsmp: track name = %s¥n"), name); */
	#endif /* BSL_DEBUG */
			}

			if ((err = obj->incEvent (obj, &event)) != BSL_OK) {
				break;
			}
		}

		/* TRACKNAMEでポート判別する */
		if (!_tcsncmp (name, _TEXT("Orgn A Key"), 10)) {
			port = BSL_MID_PORT_A;
		}
		else if (!_tcsncmp (name, _TEXT("Orgn B Key"), 10)) {
			port = BSL_MID_PORT_B;
		}
		else if (!_tcsncmp (name, _TEXT("Orgn A Drm"), 10)) {
			port = BSL_MID_PORT_A;
		}
		else if (!_tcsncmp (name, _TEXT("Orgn B Drm"), 10)) {
			port = BSL_MID_PORT_B;
		}
		/* A01 */
		else if (3 <= _tcslen (name) && name[0] == _TEXT('A') && _TEXT('0') <= name[1] && name[1] <= _TEXT('9') && _TEXT('0') <= name[2] && name[2] <= _TEXT('9')) {
			port = BSL_MID_PORT_A;
			if (4 <= _tcslen (name) && name[3] == _TEXT('R')) {
				int ch = (name[1] - _TEXT('0')) * 10 + (name[2] - _TEXT('0')) - 1;
				if (0 <= ch && ch < 16) {
					selector->bNoTranspose[0][ch] = TRUE;
				}
			}
		}
		/* B01 */
		else if (3 <= _tcslen (name) && name[0] == _TEXT('B') && _TEXT('0') <= name[1] && name[1] <= _TEXT('9') && _TEXT('0') <= name[2] && name[2] <= _TEXT('9')) {
			port = BSL_MID_PORT_B;
			if (4 <= _tcslen (name) && name[3] == _TEXT('R')) {
				int ch = (name[1] - _TEXT('0')) * 10 + (name[2] - _TEXT('0')) - 1;
				if (0 <= ch && ch < 16) {
					selector->bNoTranspose[1][ch] = TRUE;
				}
			}
		}
		/* C01 */
		else if (3 <= _tcslen (name) && name[0] == _TEXT('C') && _TEXT('0') <= name[1] && name[1] <= _TEXT('9') && _TEXT('0') <= name[2] && name[2] <= _TEXT('9')) {
			port = BSL_MID_PORT_DISABLED;
		}
		else if (!_tcsncmp (name, _TEXT("A"), 1) || !_tcsncmp (name, _TEXT("E"), 1)) {
			port = BSL_MID_PORT_A;
		}
		else if (!_tcsncmp (name, _TEXT("B"), 1)) {
			port = BSL_MID_PORT_B;
		}
		else if (!_tcsncmp (name, _TEXT("Exclusive"), 9)) {
			port = BSL_MID_PORT_A;
		}
		else {
			if (index == 0) port = BSL_MID_PORT_A;
			else port = BSL_MID_PORT_DISABLED;
		}
	}

	return port;
}
