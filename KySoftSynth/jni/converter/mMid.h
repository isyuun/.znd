/* mMid.h - midi specification defines */

/* */

/* 
modification history
--------------------
*/

/* 
DESCRIPTION 
*/

#ifndef __INCmMidih
#define __INCmMidih

/* defines */

enum {
	kMidPortA = 0, // port A
	kMidPortB, // port B
	kMidPortC, // port C

	// status
	kMidStatusNull = 0x00, // default 
	kMidStatusNoteOff = 0x80, // note off
	kMidStatusNoteOn = 0x90, // note on
	kMidStatusPolyPress = 0xA0, // polyphonic key pressure
	kMidStatusControl = 0xB0, // control change
	kMidStatusProgram = 0xC0, // program change
	kMidStatusChPress = 0xD0, // channel pressure
	kMidStatusPitchBend = 0xE0, // pitch bend
	kMidStatusSyx = 0xF0, // system exclusive
	kMidStatusMtc, // midi time code
	kMidStatusSongPosition, // song position pointer
	kMidStatusSongSelect, // song select
	kMidStatusPort = 0xF5, // port select
	kMidStatusTuneRequest, // tune request
	kMidStatusSyxEnd, // end of system exclusive
	kMidStatusMclk, // midi clock
	kMidStatusStart = 0xFA, // start
	kMidStatusStop, // stop
	kMidStatusContinue, // continue
	kMidStatusActiveSense = 0xFE, // active sensing
	kMidStatusReset, // system reset

	// control changes
	kMidCnumBankSelM = 0, // 0 = 00H bank select msb
	kMidCnumModulation, // 1 = 01H modulation depth
	kMidCnumBreathType, // 2 = 02H breath type
	kMidCnumUndefined3, // 3 = 03H
	kMidCnumFootType, // 4 = 04H foot type
	kMidCnumPortaTime, // 5 = 05H portamento time
	kMidCnumDataEntM, // 6 = 06H data entry msb
	kMidCnumVolume, // 7 = 07H volume
	kMidCnumBalance, // 8 = 08H balance control
	kMidCnumUndefined9, // 9 = 09H
	kMidCnumPanpot, // 10 = 0AH panpot
	kMidCnumExpression, // 11 = 0BH expression
	kMidCnumEftCtrl1, // 12 = 0CH effect control 1
	kMidCnumEftCtrl2, // 13 = 0DH effect control 2
	kMidCnumUndefined14, // 14 = 0EH
	kMidCnumUndefined15, // 15 = 0FH
	kMidCnumGPurpose1M, // 16 = 10H general purpose 1 msb
	kMidCnumGPurpose2M, // 17 = 11H general purpose 2 msb
	kMidCnumGPurpose3M, // 18 = 12H general purpose 3 msb
	kMidCnumGPurpose4M, // 19 = 13H general purpose 4 msb
	kMidCnumUndefined20, // 20 = 14H
	kMidCnumUndefined21, // 21 = 15H
	kMidCnumUndefined22, // 22 = 16H
	kMidCnumUndefined23, // 23 = 17H
	kMidCnumUndefined24, // 24 = 18H
	kMidCnumUndefined25, // 25 = 19H
	kMidCnumUndefined26, // 26 = 1AH
	kMidCnumUndefined27, // 27 = 1BH
	kMidCnumUndefined28, // 28 = 1CH
	kMidCnumUndefined29, // 29 = 1DH
	kMidCnumUndefined30, // 30 = 1EH
	kMidCnumUndefined31, // 31 = 1FH
	kMidCnumBankSelL, // 32 = 20H bank select lsb
	kMidCnumUndefined33, // 33 = 21H
	kMidCnumUndefined34, // 34 = 22H
	kMidCnumUndefined35, // 35 = 23H
	kMidCnumUndefined36, // 36 = 24H
	kMidCnumUndefined37, // 37 = 25H
	kMidCnumDataEntL, // 38 = 26H data entry lsb
	kMidCnumUndefined39, // 39 = 27H
	kMidCnumUndefined40, // 40 = 28H
	kMidCnumUndefined41, // 41 = 29H
	kMidCnumUndefined42, // 42 = 2AH
	kMidCnumUndefined43, // 43 = 2BH
	kMidCnumUndefined44, // 44 = 2CH
	kMidCnumUndefined45, // 45 = 2DH
	kMidCnumUndefined46, // 46 = 2EH
	kMidCnumUndefined47, // 47 = 2FH
	kMidCnumGPurpose1L, // 48 = 30H general purpose 1 lsb
	kMidCnumGPurpose2L, // 49 = 31H general purpose 2 lsb
	kMidCnumGPurpose3L, // 50 = 32H general purpose 3 lsb
	kMidCnumGPurpose4L, // 51 = 33H general purpose 4 lsb
	kMidCnumUndefined52, // 52 = 34H
	kMidCnumUndefined53, // 53 = 35H
	kMidCnumUndefined54, // 54 = 36H
	kMidCnumUndefined55, // 55 = 37H
	kMidCnumUndefined56, // 56 = 38H
	kMidCnumUndefined57, // 57 = 39H
	kMidCnumUndefined58, // 58 = 3AH
	kMidCnumUndefined59, // 59 = 3BH
	kMidCnumUndefined60, // 60 = 3CH
	kMidCnumUndefined61, // 61 = 3DH
	kMidCnumUndefined62, // 62 = 3EH
	kMidCnumUndefined63, // 63 = 3FH
	kMidCnumHold1, // 64 = 40H hold 1
	kMidCnumPortamentoSw, // 65 = 41H portamento switch
	kMidCnumSostenuto, // 66 = 42H sostenuto
	kMidCnumSoftPedal, // 67 = 43H soft pedal
	kMidCnumLegatoFootSw, // 68 = 44H legato foot switch
	kMidCnumHold2, // 69 = 45H hold 2
	kMidCnumSoundCtrl1, // 70 = 46H sound variation
	kMidCnumSoundCtrl2, // 71 = 47H timbre/harmony indensity
	kMidCnumSoundCtrl3, // 72 = 48H release time
	kMidCnumSoundCtrl4, // 73 = 49H attack time
	kMidCnumSoundCtrl5, // 74 = 4AH brightness
	kMidCnumSoundCtrl6, // 75 = 4BH decay time
	kMidCnumSoundCtrl7, // 76 = 4CH vibrato rate
	kMidCnumSoundCtrl8, // 77 = 4DH vibrato depth
	kMidCnumSoundCtrl9, // 78 = 4EH vibrato delay
	kMidCnumSoundCtrl10, // 79 = 4FH
	kMidCnumGPurpose5, // 80 = 50H general purpose 5
	kMidCnumGPurpose6, // 81 = 51H general purpose 6
	kMidCnumGPurpose7, // 82 = 52H general purpose 7
	kMidCnumGPurpose8, // 83 = 53H general purpose 8
	kMidCnumPortamentoCtrl, // 84 = 54H portamento control
	kMidCnumUndefined85, // 85 = 55H
	kMidCnumUndefined86, // 86 = 56H
	kMidCnumUndefined87, // 87 = 57H
	kMidCnumUndefined88, // 88 = 58H
	kMidCnumUndefined89, // 89 = 59H
	kMidCnumUndefined90, // 90 = 5AH
	kMidCnumEffect1, // 91 = 5BH general effect 1 (rev)
	kMidCnumEffect2, // 92 = 5CH general effect 2 (trem)
	kMidCnumEffect3, // 93 = 5DH general effect 3 (cho)
	kMidCnumEffect4, // 94 = 5EH general effect 4 (cel)
	kMidCnumEffect5, // 95 = 5FH general effect 5 (pha)
	kMidCnumInc, // 96 = 60H data increment
	kMidCnumDec, // 97 = 61H data decrement
	kMidCnumNrpnL, // 98 = 62H nrpn lsb
	kMidCnumNrpnM, // 99 = 63H nrpn msb
	kMidCnumRpnL, // 100 = 64H rpn lsb
	kMidCnumRpnM, // 101 = 65H rpn msb
	kMidCnumUndefined102, //  102 = 66H
	kMidCnumUndefined103, //  103 = 67H
	kMidCnumUndefined104, //  104 = 68H
	kMidCnumUndefined105, //  105 = 69H
	kMidCnumUndefined106, //  106 = 6AH
	kMidCnumUndefined107, //  107 = 6BH
	kMidCnumUndefined108, //  108 = 6CH
	kMidCnumUndefined109, //  109 = 6DH
	kMidCnumUndefined110, //  110 = 6EH
	kMidCnumUndefined111, //  111 = 6FH
	kMidCnumUndefined112, //  112 = 70H
	kMidCnumUndefined113, //  113 = 71H
	kMidCnumUndefined114, //  114 = 72H
	kMidCnumUndefined115, //  115 = 73H
	kMidCnumUndefined116, //  116 = 74H
	kMidCnumUndefined117, //  117 = 75H
	kMidCnumUndefined118, //  118 = 76H
	kMidCnumUndefined119, //  119 = 77H

	//  mode messages  
	kMidCnumAllSndOff, // 120 = 78H all sound off
	kMidCnumResetAllCtrl, // 121 = 79H reset all controlers
	kMidCnumLocal, // 122 = 7AH local control
	kMidCnumAllNoteOff, // 123 = 7BH all note off
	kMidCnumOmniOff, // 124 = 7CH omni mode off
	kMidCnumOmniOn, // 125 = 7DH omni mode on
	kMidCnumMono, // 126 = 7EH mono
	kMidCnumPoly, // 127 = 7FH poly

	// rpn
	kMidRnumPBSense = 0x0000, // pitch bend sensitivity
	kMidRmsbPBSense = 0x00,
	kMidRlsbPBSense = 0x00,
	kMidRnumMstFineTune = 0x0001, // master fine tuning
	kMidRmsbMstFineTune = 0x00,
	kMidRlsbMstFineTune = 0x01,
	kMidRnumMstCorsTune = 0x0002, // master corase tuning
	kMidRmsbMstCorsTune = 0x00,
	kMidRlsbMstCorsTune = 0x02,
	kMidRnumNull = 0x7F7F,
	kMidRmsbNull = 0x7F,
	kMidRlsbNull = 0x7F,

	// mode
	kMidMode1 = 0,
	kMidMode2,
	kMidMode3,
	kMidMode4,

	// bank
	kMidBankMelody = 0x79,
	kMidBankRhythm = 0x78,

	//  sysex maker IDs
	kMidSyxIdRoland = 0x41, //  maker = Roland
	kMidSyxIdKorg = 0x42, //  maker = KORG
	kMidSyxIdJvc = 0x48, //  maker = JVC
	kMidSyxIdOki = 0x5D, // maker = Oki
	kMidUnvSyxIdNonRealTime = 0x7E, //  universal non-realtime
	kMidUnvSyxIdRealTime = 0x7F, //  universal realtime

	kMidUnvSyxIdDevBroad = 0x7F,

	kMidUnvSyxIdS1DevCtrl = 0x04,
	kMidUnvSyxIdS1GenInfo = 0x06,
	kMidUnvSyxIdS1Gm = 0x09,

	kMidUnvSyxIdS2IdentyReq = 0x01,
	kMidUnvSyxIdS2IdentyRep = 0x02,

	kMidUnvSyxIdS2GmOn = 0x01,
	kMidUnvSyxIdS2GmOff = 0x02,
	kMidUnvSyxIdS2Gm2On = 0x03,

	kMidUnvSyxIdS2MstVol = 0x01,
	kMidUnvSyxIdS2MstBal = 0x02,
	kMidUnvSyxIdS2MstFTune = 0x03,
	kMidUnvSyxIdS2MstCTune = 0x04,

	// file types
	kMidFileTypeNone = -1,
	kMidFileTypeSmf,
	kMidFileTypeDummy,

	// file format
	kMidFormatSmf0 = 0x0000, 
	kMidFormatSmf1,
	kMidFormatSmf2,
};

/* typedefs */

typedef BYTE (*M_MID_CALLBACK_VALUECHANGE) (BYTE module, BYTE part, BYTE value, void *user);

#endif /* __INCmMidih */
