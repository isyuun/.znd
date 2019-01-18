/*
 *  BSL.h
 *
 *  Copyright (c) 2004-2009 bismark. All rights reserved.
 *
 */

/*!
	@file BSL.h
	@brief
	BSL��{�w�b�_
*/

/*! \mainpage
	-# �ړI<BR>
		BSL�͉��L��ړI�Ƃ���ėp���C�u�����ł���B
		- �J�����Y�̎Г����L�ɂ��A�v���P�[�V�����A�~�h���E�G�A���̊J�������̌���
		- ����ˑ��̃R�[�h�\�z�ɂ��A�v���P�[�V�����A�~�h���E�G�A���̈ڐA������
		- �e��f�X�N�g�b�vOS
		- �e��g�ݍ��݊�
		����ɏ]���A�ȉ��̊�{�p���Ɋ�Â��ă��C�u�����J�����s���B
		- C����ɂ��R�[�f�B���O
		- ���W���[���A�I�u�W�F�N�g�I�\�z
		- ���W���[���A�\�[�X���̃e�X�g�c�[���̐����ɂ��A�M�������؎�i�Ȃ�тɃT���v���̒�
		- �h�L�������g�̐��� (Doxygen�ɂ��)
	-# �p�r
	-# ���W���[��<BR>
		�{���C�u�����͉��L�Ɏ������W���[���ɕ��ނ��č\�z�����B
		- Debug: �f�o�b�O�p���[�e�B���e�B
		- Eft: �I�[�f�B�I�G�t�F�N�g����
		- Enc: �Í�/����������
		- Error: �G���[�Ǘ�
		- File: �t�@�C�����o��
		- Lyr: �J���I�P�̎������i�\���A�t�@�C���p�[�X/���C�g�j
		- Math: ���l���Z
		- Mem: �������Ǘ�
		- Mid: MIDI�i���o�́AMIDI�V�[�P���X�t�@�C���p�[�X/���C�g�j
		- Syn: �\�t�g�E�G�A�V���Z�T�C�U
		- Text: �e�L�X�g����
		- Wav: Wave�i���o�́A�t�@�C���p�[�X/���C�g�A�R�[�f�b�N�j
		- Wtbl: Wave Table�i�t�@�C���p�[�X/���C�g�j
	-# ����I�u�W�F�N�g<BR>
		�{���C�u������C����ɂč\�z������̂ł��邪�A
		���C�u�����̃I�u�W�F�N�g�w�� �I���W��}�邽�߁A����I�u�W�F�N�g���`����B
		����I�u�W�F�N�g�Ƃ́A��{�v�f�ɑ΂��鐧����s�����\�b�h�p�֐��|�C���^�A
		��{�v�f�Ɋւ������ێ����郁���o�[�ϐ��A���܂Ƃ߂�C�\���̂ɂĒ�`�����B
		���\�b�h�p�֐��|�C���^��u�������邱�Ƃŋ[���I�Ȑ���I�u�W�F�N�g�̌p���A
		���\�b�h�p�֐��̉��z�֐��I�g�p���s�����Ƃ�ړI�Ƃ�����̂ł���B
		- ��{�\��
		���L�Ɋ�{�\���������B
\code
typedef struct BSLObj
{
	BSLErr(*method0) (struct BSLObj *obj, int method_parameter0);
	int parameter0;
} BSLObj;
\endcode
		���L�Ɏ�����������B
		- BSLObj.h: ��{�I�u�W�F�N�g
\code
typedef struct BSLObj
{
	int (*sample) (struct BSLObj *obj);
	int parameter
} BSLObj;

void bslObj (BSLObj *obj);
void bslObjExit (BSLObj *obj);
\endcode
		����I�u�W�F�N�g�͍Œ���ȉ��̍\�����Ƃ�B
			-# ���\�b�h�Ȃ�тɃ����o���`����\���̒�` typedef struct BSLXxx�B
			-# �\�z�֐��F����I�u�W�F�N�g���쐬���� void bslXxx (BSLXxx *obj, ...) �`���̊֐��B�p�����ꂽ�I�u�W�F�N�g���Ɍʂ̍\�z�֐����p�ӂ����B
			-# �j���֐��F����I�u�W�F�N�g�̔j����錾���� void bslXxxExit (BSLXxx *obj)�̌`���̊֐��B�p�����ꂽ�S�I�u�W�F�N�g�ŋ��ʂ̔j�����@���Ƃ�B
		- BSLObj.c: ��{�I�u�W�F�N�g
\code
static void sample(BSLObj *obj)
{
	printf (�gparam is %d\n�h, obj->parameter);
}

void bslObj (BSLObj *obj)
{
	parameter = 0;
	obj->sample = sample;
}

void bslObjExit (BSLObj *obj)
{
}
\endcode
		- BSLObj2.h: �p���I�u�W�F�N�g
\code
void BSLObj2 (BSLObj *obj);
\endcode
		- BSLObj2.c: �p���I�u�W�F�N�g
\code
static void sample(BSLObj *obj)
{
	printf (�gparam is %d\n�h, obj->parameter + 1);
}

void CrObj2 (BSLObject *obj)
{
	parameter = 0;
	obj->sample = sample;
}
\endcode
		- main.c: �T���v��
\code
int main ()
{
	int i;
	BSLObj obj[3];
	
	BSLObj (&obj[0]);
	BSLObj2 (&obj[1]);
	BSLObj (&obj[2]);
	
	{
		BSLObj *o = obj;
		for (i = 0; i < 3; i++) {
			o->sample (o);
			o++;
		}
	}
}
\endcode
		����I�u�W�F�N�g�̗̍p�͓����ɃA�v���P�[�V�����̉��������߂邱�Ƃ�ړI�Ƃ�����̂ł���B
		�]���āA����I�u�W�F�N�g���L�q����\���̂̓��e�Ɋւ��Ă͂��ꂼ��A�N�Z�X�p�̊֐���\�ߒ�`���A
		�A�v���P�[�V�������\���̂̓��e�֒��ڃA�N�Z�X���邱�Ƃ�h�����Ƃ��]�܂����B
	-# �R�[�f�B���O
		-# ����<BR>
			C����
		-# �R���p�C��<BR>
			ANSI C�����R���p�C���̑Ή����\�ł��邱�ƁB�ȉ������t�@�����X���Ƃ���
			- Windows
				- Microsoft Visual Studio.net 2003
				- Microsoft Visual C Version 6 + SP6
			- Windows CE
				- Microsoft VEmbedded Visual C Version 4
				- Microsoft VEmbedded Visual C Version 3
			- Mac OS X
				- Apple Xcode 1.5
			- Linux
				- GNU gcc version 3.x
		-# �����<BR>
		�@��{�͊���ˑ��Ƃ���B�ȉ������t�@�����X���Ƃ���B
			- Windows XP SP2 / 2000 SP4 / Me / 98SE
			- Windows CE 3.x (Windows Mobile 2002), 4.x (Windows Mobile 2003)
			- Mac OS X 10.3
			- RedHat Linux 9
	-# �R�[�f�B���O�K��
		-# �t�@�C����
			- BSL[ModuleName (�擪�̂ݑ啶��)][�C�ӕ����� (�擪�̂ݑ啶��)][�c].c
				- ex) BSLWavSample.c
			- BSL[ModuleName (�擪�̂ݑ啶��)][�C�ӕ����� (�擪�̂ݑ啶��)] [�c].h
				- ex) BSLWavSample.h
		-# ��`���ideine / typedef�j
			-# �{���C�u�����O���֌��J������`
				- BSL_[Module Name (�啶��)]_[�C�ӕ����� (�啶��)] [�c]
					- ex) BSL_WAV_SAMPLE
			-# ����J��`
				- _[�C�ӕ����� (�啶��)]_ [�C�ӕ����� (�啶��)] [�c]
					- ex) _MAX_SAMPLE
			-# ����I�u�W�F�N�g��`
				- BSL[ModuleName (�擪�̂ݑ啶��)][�C�ӕ����� (�擪�̂ݑ啶��)]
					- ex) BSLSample
		-# �֐���
			-# �{���C�u�����O���֌��J�����֐�
				- bsl[Module Name (�擪�̂ݑ啶��)] [�C�ӓ��������� (�擪�̂ݑ啶��)][�C�ӕ����� (�擪�̂ݑ啶��)] [�c]
					- ex) bslWavReadSample ()
			-# �{���C�u���������ւ̂݌��J�����֐�
				- _[Module Name (������)] [�C�ӓ��������� (�擪�̂ݑ啶��)][�C�ӕ����� (�擪�̂ݑ啶��)] [�c]
					- ex) _bslWavReadSample ()
					- ��Ɍp�����ꂽ����I�u�W�F�N�g����{�܂��͐e�̐���I�u�W�F�N�g�̃��\�b�h���g�p����ꍇ�ɗp����B
			-# ����J�֐�
				- _[�C�ӓ��������� (������)] [�C�ӕ����� (�擪�̂ݑ啶��)]�c
					- ex) _getData ()
					- static�錾���s������
	-# ���W���[�����ʍ���
		-# �t�@�C��
			- BSL.h: �{���C�u�����̊�{��`���s��
			- BSLConf.h: �^�[�Q�b�g�ˑ���`���s���B���BSL.h���include�����B
		-# �v���b�g�t�H�[����`
			�Ή��v���b�g�t�H�[���ɏ]���ABSLConf.h�ɂĉ��L��define��r���I�ɒ�`����B
			- Windows
				- BSL_WINDOWS = 1
			- Windows CE
				- BSL_WINDOWS = 1, BSL_WINDOWSCE = 1
			- Mac OS X
				- BSL_MAC = 1
			- Linux
				- BSL_LINUX = 1
*/

#ifndef __INCBSLh
#define __INCBSLh

#ifdef __cplusplus
extern "C" {
#endif

/* pre-defines */

#ifndef TRUE
	#define TRUE 1
#endif
#ifndef FALSE
	#define FALSE 0
#endif

/* includes */

#ifdef _DEBUG
	#define BSL_DEBUG
#endif /* _DEBUG */

#include "BSLConf.h" /* target configration */

#if BSL_WINDOWS
	#define WIN32_LEAN_AND_MEAN
	#include <windows.h>
#endif /* BSL_WINDOWS */

#if BSL_MAC
	#ifdef __BIG_ENDIAN__ /* only PowerPC */
		#define BSL_BIGENDIAN
	#endif /* __BIG_ENDIAN__ */
#endif

#if BSL_MAC || BSL_IOS
	#include <objc/objc.h>
#endif

#if __LP64__ || BSL_LINUX
#include <stdint.h>
#endif
  
#include "BSLText.h"
#include "BSLDebug.h"
#include "BSLError.h"

/* defines */

/*! BSL���C�u�����̃o�[�W���������� */
#define BSL_VERSION _TEXT("20090521_crysnth_v2.1.1")

#if BSL_WINDOWS
	#pragma warning(disable:4068) /* disable visual studio warning against to "#pragma -" */
#endif /* BSL_WINDOWS */

#if (defined __GNUC__)
	#define INLINE __inline__
#elif BSL_WINDOWS
	#define INLINE __inline
#else
	#define INLINE
#endif

/* typedefs */

#ifndef OBJC_BOOL_DEFINED
typedef int BOOL;
#endif
#if __LP64__
  typedef unsigned int DWORD;
#else
  typedef unsigned long DWORD;
#endif
typedef unsigned short WORD;
typedef unsigned char  BYTE;

/* globals */

/* loclas */

/* function declarations */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLh */

