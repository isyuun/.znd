/*
 *  BSLFileSave.h
 *
 *  Copyright (c) 2004-2005 bismark. All rights reserved.
 *
 */

/*!
	@file BSLFileSave.h
	@brief
	�t�@�C���o�͐���I�u�W�F�N�g
*/

#ifndef __INCBSLFileSaveh
#define __INCBSLFileSaveh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSLFile.h"

/* defines */

/*! �t�@�C���^�C�v */
typedef enum  {
	/*! �t�@�C�� */
	BSL_FILE_SAVE_TYPE_FILE,
	/*! ������ */
	BSL_FILE_SAVE_TYPE_MEM,
	/*! �_�~�[ */
	BSL_FILE_SAVE_TYPE_DUMMY
} BSL_FILE_SAVE_TYPE;

/* typedefs */

/*!
	�t�@�C���o�͏������s����{����I�u�W�F�N�g
	�e�����Ώۖ��Ɉȉ��̌p���I�u�W�F�N�g�����݂���B
	- BSLFileSave.c / BSLFileSave.h : �X�g���[��I/O�^
	- BSLFileSaveMem.c / BSLFileMemSave.h : ������I/O�^
	- �g�p�菇
		-# bslFileSave()�ɂ�萧��I�u�W�F�N�g���\�z
		-# �R�[���o�b�N�֐� (BSL_FILE_SAVE)��p�ӂ���
		-# BSLFileSave::save()�����s
		-# bslFileSaveExit()�ɂ�萧��I�u�W�F�N�g��j��
*/
typedef struct BSLFileSave
{
#ifndef BSL_FILE_NO_SYSTEM
	/*! �w��̃p�X�ɏo�͑Ώۂ�ݒ肷��*/
	BSLErr (*setDestination) (struct BSLFileSave *obj, LPCTSTR filename);
#endif /* BSL_FILE_NO_SYSTEM */

	/*! �w��̃������ɏo�͑Ώۂ�ݒ肷�� */
	BSLErr (*setDestinationMem) (struct BSLFileSave *obj, char *data, DWORD size);

	/*! �t�@�C����ۑ����� */
	BSLErr (*save) (struct BSLFileSave *obj, BSLErr (*save) (struct BSLFileSave *obj, void *user, void *userParam), void *user, void *userParam);

	/*! �f�[�^�̏����o�����s�� */
	BSLErr (*write) (struct BSLFileSave *obj, void *data, unsigned long size);
	/*! �f�[�^�̓ǂݏo�����s�� */
	BSLErr (*read) (struct BSLFileSave *obj, void *data, unsigned long size);
	/*! �w�肳�ꂽ�ʒu�Ƀt�@�C�� �|�C���^���ړ����� */
	BSLErr (*seek) (struct BSLFileSave *obj, long offset, BSL_FILE_SEEK mode);
	/*! �t�@�C���|�C���^�̌��݈ʒu���擾���� */
	unsigned long (*getPos) (struct BSLFileSave *obj);
	/*! �o�̓T�C�Y[byte]���擾���� */
	unsigned long (*getLength) (struct BSLFileSave *obj);
	/*! �t�@�C���^�C�v���擾���� */
	BSL_FILE_SAVE_TYPE (*getType) (struct BSLFileSave *obj);


	union {
#ifndef BSL_FILE_NO_SYSTEM
		struct {
			TCHAR cPath[_MAX_PATH];
	#if BSL_WINDOWS
			HANDLE fp;
	#else /* BSL_WINDOWS */
			FILE * fp;
	#endif /* BSL_WINDOWS */
			BOOL bOpened;
			unsigned long ulPosition;
		} disk;
#endif /* BSL_FILE_NO_SYSTEM */
		struct {
			char *pData;
			char *pPosition;
			unsigned long ulDataSize;
		} mem;
		struct {
			unsigned long ulPassedPos;
			unsigned long ulActivePos;
		} dummy;
	} member;

	unsigned long ulFileSize;
	BSL_FILE_SAVE_TYPE nType;

} BSLFileSave;

/*! �t�@�C�������o���������s���R�[���o�b�N�֐��^ */
typedef BSLErr (*BSL_FILE_SAVE) (BSLFileSave *obj, void *user, void *userParam);

/* globals */

/* locals */

/* function declarations */

void bslFileSave (BSLFileSave *obj);
void bslFileSaveExit (BSLFileSave *obj);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLFileSaveh */
