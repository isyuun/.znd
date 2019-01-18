/*
 *  BSLFile.h
 *
 *  Copyright (c) 2004-2005 bismark. All rights reserved.
 *
 */

/*!
	@file BSLFile.h
	@brief
	�t�@�C�����͐���I�u�W�F�N�g
*/

#ifndef __INCBSLFileh
#define __INCBSLFileh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSL.h"
#include "BSLText.h"
#ifndef BSL_FILE_NO_SYSTEM
	#if !BSL_WINDOWS
		#include <stdio.h>
	#endif /* BSL_WINDOWS */
#endif /* BSL_FILE_NO_SYSTEM */

/* defines */

/*! 4�o�C�g�w�b�_�p�}�N�� */
#ifdef BSL_BIGENDIAN
	#define BSL_FILE_MARKER(a,b,c,d) (((a)<<24)|((b)<<16)|((c)<<8)|(d))
#else /* BSL_BIGENDIAN */
	#define BSL_FILE_MARKER(a,b,c,d) ((a)|((b)<<8)|((c)<<16)|((d)<<24))
#endif /* BSL_BIGENDIAN */

/*! �t�@�C���V�[�N�����p */
typedef enum {
	/*! �擪 */
	BSL_FILE_SEEK_START = 0,
	BSL_FILE_SEEK_CURRENT,
	BSL_FILE_SEEK_END
} BSL_FILE_SEEK;

/* typedefs */

/*!
	�t�@�C�����͏������s����{����I�u�W�F�N�g�B
	�e�����Ώۖ��Ɉȉ��̌p���I�u�W�F�N�g�����݂���B
	- BSLFile.c / BSLFile.h : �X�g���[��I/O�^
	- BSLFileMem.c / BSLFileMem.h : ������I/O�^
	- BSLFileCache.c / BSLFileCache.h : �X�g���[��I/O + ���[�h�L���b�V���^

	- �g�p�菇
		-# bslFile*()�ɂ�萧��I�u�W�F�N�g���\�z
		-# BSLFile::open()�ɂ��w��t�@�C�����A�܂���BSLFile::openMem()�ɂ��w��A�h���X���I�[�v��
			- �e���\�b�h�̌p��������bslFile*Open ()�ɂčs��
		-# BSLFile::read(), BSLFile::seek()�ɂ��C�ӓ��͏������s��
		-# BSLFile::close()�ɂ��w��t�@�C�����N���[�Y
		-# bslFileExit()�ɂ�萧��I�u�W�F�N�g��j��
*/
typedef struct BSLFile
{
#ifndef BSL_FILE_NO_SYSTEM
	/*!
		�t�@�C���p�X���I�[�v������B
		BSL_FILE_NO_SYSTEM������`�̏ꍇ�̂ݗL���B
	*/
	BSLErr (*open) (struct BSLFile *obj, LPCTSTR filename);
#endif /* BSL_FILE_NO_SYSTEM */
	/*! �������}�b�v���ꂽ�t�@�C���C���[�W���I�[�v������ */
	BSLErr (*openMem) (struct BSLFile *obj, char *data, unsigned long size);
	/*! �f�[�^�̓ǂݏo�����s�� */
	BSLErr (*read) (struct BSLFile *obj, void *data, unsigned long size);
	/*! �w�肳�ꂽ�ʒu�Ƀt�@�C�� �|�C���^���ړ����� */
	BSLErr (*seek) (struct BSLFile *obj, long offset, BSL_FILE_SEEK mode);
	/*! �t�@�C���|�C���^�̌��݈ʒu���擾���� */
	unsigned long (*getPos) (struct BSLFile *obj);
	/*! �t�@�C���T�C�Y[Byte]���擾���� */
	unsigned long (*getLength) (struct BSLFile *obj);
	/*! �t�@�C������� */
	BSLErr (*close) (struct BSLFile *obj);

	/*! �t�@�C�����I�[�v������Ă��邩*/
	BOOL (*isOpened) (struct BSLFile *obj);

	/*!
		������IO�A�h���X�̎擾���s���B������I/O�^�̂ݗL���B
	*/
	char *(*getAddress) (struct BSLFile *obj);

	/*!
		�I���������s�����\�b�h�B
		�p���I�u�W�F�N�g�Ǝ��̃������j�������K�v�ȏꍇ�ɂ͂����ōs���B
		�K�v�Ȃ��ꍇ�͖{���\�b�h�|�C���^��NULL�̂܂܂ŉB
		bslFileExit()���Ăяo�����B
	*/
	void (*finalize) (struct BSLFile *obj);

	unsigned long ulFileSize;
	BOOL bIsOpened;
	union {
#ifndef BSL_FILE_NO_SYSTEM
		struct {
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
			BOOL bAllocated;
			char *pPosition;
		} mem;
	} member;

	/*!
		�����f�[�^�p�|�C���^�Ƃ��Čp���I�u�W�F�N�g�ɂĎg�p����B
		�������擾�͌p���I�u�W�F�N�g�ɂčs�����ƁB
		�������J����bslFileExit()�ɂčs����B
	*/
	void *pInternalData;
} BSLFile;

/* globals */

/* locals */

/* function declarations */

void bslFile (BSLFile *obj);
void bslFileExit (BSLFile *obj);

void bslFileSwap (void *data, int size);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLFileh */
