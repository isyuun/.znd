/*
 *  BSLMid.h
 *
 *  Copyright (c) 2002-2006 bismark. All rights reserved.
 *
 */

/*!
	@file BSLMid.h
	@brief
	MIDI関連定義（暫定）
*/

#ifndef __INCBSLMidh
#define __INCBSLMidh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"

/* defines */

typedef enum {
	BSL_MID_PORT_DISABLED = -1,
	BSL_MID_PORT_A = 0, /* port A */
	BSL_MID_PORT_B, /* port B */
	BSL_MID_PORT_C, /* port C */
} BSL_MID_PORT;

typedef enum {
	BSL_MID_STATUS_NULL = 0x00, /* default */
	BSL_MID_STATUS_NOTE_OFF = 0x80, /* note off */
	BSL_MID_STATUS_NOTE_ON = 0x90, /* note on */
	BSL_MID_STATUS_POLY_PRESS = 0xA0, /* polyphonic key pressure */
	BSL_MID_STATUS_CONTROL = 0xB0, /* control change */
	BSL_MID_STATUS_PROGRAM = 0xC0, /* program change */
	BSL_MID_STATUS_CH_PRESS = 0xD0, /* channel pressure */
	BSL_MID_STATUS_PITCH_BEND = 0xE0, /* pitch bend */
	BSL_MID_STATUS_SYX = 0xF0, /* system exclusive */
	BSL_MID_STATUS_MTC, /* midi time code */
	BSL_MID_STATUS_SONG_POSITION, /* song position pointer */
	BSL_MID_STATUS_SONG_SELECT, /* song select */
	BSL_MID_STATUS_PORT = 0xF5, /* port select */
	BSL_MID_STATUS_TUNE_REQUEST, /* tune request */
	BSL_MID_STATUS_SYX_END, /* end of system exclusive */
	BSL_MID_STATUS_MIDI_CLOCK, /* midi clock */
	BSL_MID_STATUS_START = 0xFA, /* start */
	BSL_MID_STATUS_STOP, /* stop */
	BSL_MID_STATUS_CONTINUE, /* continue */
	BSL_MID_STATUS_ACTIVE_SENSE = 0xFE, /* active sensing */
	BSL_MID_STATUS_RESET, /* system reset */
} BSL_MID_STATUS;

enum {
	/* control changes */
	BSL_MID_CNUM_BANK_SEL_M = 0, /* 0 = 00H bank select msb */
	BSL_MID_CNUM_MODULATION, /* 1 = 01H modulation depth */
	BSL_MID_CNUM_BREATH_TYPE, /* 2 = 02H breath type */
	BSL_MID_CNUM_UNDEFINED_3, /* 3 = 03H */
	BSL_MID_CNUM_FOOT_TYPE, /* 4 = 04H foot type */
	BSL_MID_CNUM_PORTA_TIME, /* 5 = 05H portamento time */
	BSL_MID_CNUM_DATA_ENT_M, /* 6 = 06H data entry msb */
	BSL_MID_CNUM_VOLUME, /* 7 = 07H volume */
	BSL_MID_CNUM_BALANCE, /* 8 = 08H balance control */
	BSL_MID_CNUM_UNDEFINED_9, /* 9 = 09H */
	BSL_MID_CNUM_PANPOT, /* 10 = 0AH panpot */
	BSL_MID_CNUM_EXPRESSION, /* 11 = 0BH expression */
	BSL_MID_CNUM_EFT_CTRL1, /* 12 = 0CH effect control 1 */
	BSL_MID_CNUM_EFT_CTRL2, /* 13 = 0DH effect control 2 */
	BSL_MID_CNUM_UNDEFINED_14, /* 14 = 0EH */
	BSL_MID_CNUM_UNDEFINED_15, /* 15 = 0FH */
	BSL_MID_CNUM_GENERAL_PURPOSE_1M, /* 16 = 10H general purpose 1 msb */
	BSL_MID_CNUM_GENERAL_PURPOSE_2M, /* 17 = 11H general purpose 2 msb */
	BSL_MID_CNUM_GENERAL_PURPOSE_3M, /* 18 = 12H general purpose 3 msb */
	BSL_MID_CNUM_GENERAL_PURPOSE_4M, /* 19 = 13H general purpose 4 msb */
	BSL_MID_CNUM_UNDEFINED_20, /* 20 = 14H */
	BSL_MID_CNUM_UNDEFINED_21, /* 21 = 15H */
	BSL_MID_CNUM_UNDEFINED_22, /* 22 = 16H */
	BSL_MID_CNUM_UNDEFINED_23, /* 23 = 17H */
	BSL_MID_CNUM_UNDEFINED_24, /* 24 = 18H */
	BSL_MID_CNUM_UNDEFINED_25, /* 25 = 19H */
	BSL_MID_CNUM_UNDEFINED_26, /* 26 = 1AH */
	BSL_MID_CNUM_UNDEFINED_27, /* 27 = 1BH */
	BSL_MID_CNUM_UNDEFINED_28, /* 28 = 1CH */
	BSL_MID_CNUM_UNDEFINED_29, /* 29 = 1DH */
	BSL_MID_CNUM_UNDEFINED_30, /* 30 = 1EH */
	BSL_MID_CNUM_UNDEFINED_31, /* 31 = 1FH */
	BSL_MID_CNUM_BANK_SEL_L, /* 32 = 20H bank select lsb */
	BSL_MID_CNUM_UNDEFINED_33, /* 33 = 21H */
	BSL_MID_CNUM_UNDEFINED_34, /* 34 = 22H */
	BSL_MID_CNUM_UNDEFINED_35, /* 35 = 23H */
	BSL_MID_CNUM_UNDEFINED_36, /* 36 = 24H */
	BSL_MID_CNUM_UNDEFINED_37, /* 37 = 25H */
	BSL_MID_CNUM_DATA_ENT_L, /* 38 = 26H data entry lsb */
	BSL_MID_CNUM_UNDEFINED_39, /* 39 = 27H */
	BSL_MID_CNUM_UNDEFINED_40, /* 40 = 28H */
	BSL_MID_CNUM_UNDEFINED_41, /* 41 = 29H */
	BSL_MID_CNUM_UNDEFINED_42, /* 42 = 2AH */
	BSL_MID_CNUM_UNDEFINED_43, /* 43 = 2BH */
	BSL_MID_CNUM_UNDEFINED_44, /* 44 = 2CH */
	BSL_MID_CNUM_UNDEFINED_45, /* 45 = 2DH */
	BSL_MID_CNUM_UNDEFINED_46, /* 46 = 2EH */
	BSL_MID_CNUM_UNDEFINED_47, /* 47 = 2FH */
	BSL_MID_CNUM_GENERAL_PURPOSE_1L, /* 48 = 30H general purpose 1 lsb */
	BSL_MID_CNUM_GENERAL_PURPOSE_2L, /* 49 = 31H general purpose 2 lsb */
	BSL_MID_CNUM_GENERAL_PURPOSE_3L, /* 50 = 32H general purpose 3 lsb */
	BSL_MID_CNUM_GENERAL_PURPOSE_4L, /* 51 = 33H general purpose 4 lsb */
	BSL_MID_CNUM_UNDEFINED_52, /* 52 = 34H */
	BSL_MID_CNUM_UNDEFINED_53, /* 53 = 35H */
	BSL_MID_CNUM_UNDEFINED_54, /* 54 = 36H */
	BSL_MID_CNUM_UNDEFINED_55, /* 55 = 37H */
	BSL_MID_CNUM_UNDEFINED_56, /* 56 = 38H */
	BSL_MID_CNUM_UNDEFINED_57, /* 57 = 39H */
	BSL_MID_CNUM_UNDEFINED_58, /* 58 = 3AH */
	BSL_MID_CNUM_UNDEFINED_59, /* 59 = 3BH */
	BSL_MID_CNUM_UNDEFINED_60, /* 60 = 3CH */
	BSL_MID_CNUM_UNDEFINED_61, /* 61 = 3DH */
	BSL_MID_CNUM_UNDEFINED_62, /* 62 = 3EH */
	BSL_MID_CNUM_UNDEFINED_63, /* 63 = 3FH */
	BSL_MID_CNUM_HOLD_1, /* 64 = 40H hold 1 */
	BSL_MID_CNUM_PORETAMENTO_SW, /* 65 = 41H portamento switch */
	BSL_MID_CNUM_SOSTENUTO, /* 66 = 42H sostenuto */
	BSL_MID_CNUM_SOFT_PEDAL, /* 67 = 43H soft pedal */
	BSL_MID_CNUM_LEGATO_FOOT_SW, /* 68 = 44H legato foot switch */
	BSL_MID_CNUM_HOLD_2, /* 69 = 45H hold 2 */
	BSL_MID_CNUM_SOUND_CTRL_1, /* 70 = 46H sound variation */
	BSL_MID_CNUM_SOUND_CTRL_2, /* 71 = 47H timbre/harmony indensity */
	BSL_MID_CNUM_SOUND_CTRL_3, /* 72 = 48H release time */
	BSL_MID_CNUM_SOUND_CTRL_4, /* 73 = 49H attack time */
	BSL_MID_CNUM_SOUND_CTRL_5, /* 74 = 4AH brightness */
	BSL_MID_CNUM_SOUND_CTRL_6, /* 75 = 4BH decay time */
	BSL_MID_CNUM_SOUND_CTRL_7, /* 76 = 4CH vibrato rate */
	BSL_MID_CNUM_SOUND_CTRL_8, /* 77 = 4DH vibrato depth */
	BSL_MID_CNUM_SOUND_CTRL_9, /* 78 = 4EH vibrato delay */
	BSL_MID_CNUM_SOUND_CTRL_10, /* 79 = 4FH */
	BSL_MID_CNUM_GENERAL_PURPOSE_5, /* 80 = 50H general purpose 5 */
	BSL_MID_CNUM_GENERAL_PURPOSE_6, /* 81 = 51H general purpose 6 */
	BSL_MID_CNUM_GENERAL_PURPOSE_7, /* 82 = 52H general purpose 7 */
	BSL_MID_CNUM_GENERAL_PURPOSE_8, /* 83 = 53H general purpose 8 */
	BSL_MID_CNUM_PORTAMENTO_CTRL, /* 84 = 54H portamento control */
	BSL_MID_CNUM_UNDEFINED_85, /* 85 = 55H */
	BSL_MID_CNUM_UNDEFINED_86, /* 86 = 56H */
	BSL_MID_CNUM_UNDEFINED_87, /* 87 = 57H */
	BSL_MID_CNUM_UNDEFINED_88, /* 88 = 58H */
	BSL_MID_CNUM_UNDEFINED_89, /* 89 = 59H */
	BSL_MID_CNUM_UNDEFINED_90, /* 90 = 5AH */
	BSL_MID_CNUM_EFFECT_1, /* 91 = 5BH general effect 1 (rev) */
	BSL_MID_CNUM_EFFECT_2, /* 92 = 5CH general effect 2 (trem) */
	BSL_MID_CNUM_EFFECT_3, /* 93 = 5DH general effect 3 (cho) */
	BSL_MID_CNUM_EFFECT_4, /* 94 = 5EH general effect 4 (cel) */
	BSL_MID_CNUM_EFFECT_5, /* 95 = 5FH general effect 5 (pha) */
	BSL_MID_CNUM_INC, /* 96 = 60H data increment */
	BSL_MID_CNUM_DEC, /* 97 = 61H data decrement */
	BSL_MID_CNUM_NRPN_L, /* 98 = 62H nrpn lsb */
	BSL_MID_CNUM_NRPN_M, /* 99 = 63H nrpn msb */
	BSL_MID_CNUM_RPN_L, /* 100 = 64H rpn lsb */
	BSL_MID_CNUM_RPN_M, /* 101 = 65H rpn msb */
	BSL_MID_CNUM_UNDEFINED_102, /*  102 = 66H */
	BSL_MID_CNUM_UNDEFINED_103, /*  103 = 67H */
	BSL_MID_CNUM_UNDEFINED_104, /*  104 = 68H */
	BSL_MID_CNUM_UNDEFINED_105, /*  105 = 69H */
	BSL_MID_CNUM_UNDEFINED_106, /*  106 = 6AH */
	BSL_MID_CNUM_UNDEFINED_107, /*  107 = 6BH */
	BSL_MID_CNUM_UNDEFINED_108, /*  108 = 6CH */
	BSL_MID_CNUM_UNDEFINED_109, /*  109 = 6DH */
	BSL_MID_CNUM_UNDEFINED_110, /*  110 = 6EH */
	BSL_MID_CNUM_UNDEFINED_111, /*  111 = 6FH */
	BSL_MID_CNUM_UNDEFINED_112, /*  112 = 70H */
	BSL_MID_CNUM_UNDEFINED_113, /*  113 = 71H */
	BSL_MID_CNUM_UNDEFINED_114, /*  114 = 72H */
	BSL_MID_CNUM_UNDEFINED_115, /*  115 = 73H */
	BSL_MID_CNUM_UNDEFINED_116, /*  116 = 74H */
	BSL_MID_CNUM_UNDEFINED_117, /*  117 = 75H */
	BSL_MID_CNUM_UNDEFINED_118, /*  118 = 76H */
	BSL_MID_CNUM_UNDEFINED_119, /*  119 = 77H */

	/*  mode messages  */
	BSL_MID_CNUM_ALL_SND_OFF, /* 120 = 78H all sound off */
	BSL_MID_CNUM_RESET_ALL_CTRL, /* 121 = 79H reset all controlers */
	BSL_MID_CNUM_LOCAL, /* 122 = 7AH local control */
	BSL_MID_CNUM_ALL_NOTE_OFF, /* 123 = 7BH all note off */
	BSL_MID_CNUM_ALL_OMNI_OFF, /* 124 = 7CH omni mode off */
	BSL_MID_CNUM_ALL_OMNI_ON, /* 125 = 7DH omni mode on */
	BSL_MID_CNUM_MONO, /* 126 = 7EH mono */
	BSL_MID_CNUM_POLY, /* 127 = 7FH poly */

	/* rpn */
	BSL_MID_RMSB_PITCH_BEND_SENSE = 0x00, /* pitch bend sensitivity */
	BSL_MID_RLSB_PITCH_BEND_SENSE = 0x00,
	BSL_MID_RNUM_PITCH_BEND_SENSE = ((BSL_MID_RMSB_PITCH_BEND_SENSE << 8) + BSL_MID_RLSB_PITCH_BEND_SENSE),

	BSL_MID_RMSB_MASTER_FINE_TUNE = 0x00, /* master fine tuning */
	BSL_MID_RLSB_MASTER_FINE_TUNE = 0x01,
	BSL_MID_RNUM_MASTER_FINE_TUNE = ((BSL_MID_RMSB_MASTER_FINE_TUNE << 8) + BSL_MID_RLSB_MASTER_FINE_TUNE),

	BSL_MID_RMSB_MASTER_COARSE_TUNE = 0x00, /* master corase tuning */
	BSL_MID_RLSB_MASTER_COARSE_TUNE = 0x02,
	BSL_MID_RNUM_MASTER_COARSE_TUNE = ((BSL_MID_RMSB_MASTER_COARSE_TUNE << 8) + BSL_MID_RLSB_MASTER_COARSE_TUNE), 

	BSL_MID_RMSB_NULL = 0x7F,
	BSL_MID_RLSB_NULL = 0x7F,
	BSL_MID_RNUM_NULL = ((BSL_MID_RMSB_NULL << 8) + BSL_MID_RLSB_NULL),

	/* mode */
	BSL_MID_MODE_1 = 0,
	BSL_MID_MODE_2,
	BSL_MID_MODE_3,
	BSL_MID_MODE_4,

	/* bank */
	BSL_MID_BANK_MELODY = 0x79,
	BSL_MID_BANK_RYTHM = 0x78,

	/*  sysex maker IDs */
	BSL_MID_SYX_ID_ROLAND = 0x41, /*  maker = Roland */
	BSL_MID_SYX_ID_KORG = 0x42, /*  maker = KORG */
	BSL_MID_SYX_ID_YAMAHA = 0x43,
	BSL_MID_SYX_ID_JVC = 0x48, /*  maker = JVC */
	BSL_MID_SYX_ID_OKI = 0x5D, /* maker = Oki */
	BSL_MID_SYX_ID_UNV_NON_REAL_TIME = 0x7E, /*  universal non-realtime */
	BSL_MID_SYX_ID_UNV_REAL_TIME = 0x7F, /*  universal realtime */

	BSL_MID_SYX_ID_UNV_DEV_BROAD = 0x7F,

	BSL_MID_SYC_IDS1_DEV_CTRL = 0x04,
	BSL_MID_SYC_IDS1_GET_INFO = 0x06,
	BSL_MID_SYC_IDS1_GM = 0x09,

	BSL_MID_SYC_IDS2_IDENTY_REQ = 0x01,
	BSL_MID_SYC_IDS2_IDENTY_REPLY = 0x02,

	BSL_MID_SYC_IDS2_GM_ON = 0x01,
	BSL_MID_SYC_IDS2_GM_OFF = 0x02,
	BSL_MID_SYC_IDS2_GM2_ON = 0x03,

	BSL_MID_SYC_IDS2_MST_VOL = 0x01,
	BSL_MID_SYC_IDS2_MST_BAL = 0x02,
	BSL_MID_SYC_IDS2_MST_FINE_TUNE = 0x03,
	BSL_MID_SYC_IDS2_MST_COARSE_TUNE = 0x04,
	BSL_MID_SYC_IDS2_GLB_PRM_CTRL = 0x05,
};

#ifdef BSL_MID_INCLUDE_TRACK_OPTION
typedef enum
{
	BSL_MID_TRACK_OPTION_NORMAL = 0,
	BSL_MID_TRACK_OPTION_MAIN_MELODY,
	BSL_MID_TRACK_OPTION_SUB_MELODY,
	BSL_MID_TRACK_OPTION_SE,
} BSL_MID_TRACK_OPTION nOption;
#endif /* BSL_MID_INCLUDE_TRACK_OPTION */

/* typedefs */

typedef struct
{
	DWORD dwTick;
	BYTE byPort;
	BYTE byStatus;
	int nSize;
	BYTE *pData;
#ifdef BSL_MID_INCLUDE_TRACK_OPTION
	BSL_MID_TRACK_OPTION nOption;
#endif /* BSL_MID_INCLUDE_TRACK_OPTION */
} BSLMidEvent;

/* globals */

/* locals */

/* function declarations */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLMidh */
