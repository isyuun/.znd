/*
 *  BSLFileRiff.h - RIFF file base parser
 *
 *  Copyright (c) 2004 bismark. All rights reserved.
 *
 */

/*!
	@file BSLFileRiff.h
	@brief
	RIFF�t�@�C�����͐���I�u�W�F�N�g
*/

#ifndef __INCBSLFileRiffh
#define __INCBSLFileRiffh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

#include "BSLFile.h"

/* defines */

#define RIFF_MARKER (BSL_FILE_MARKER ('R', 'I', 'F', 'F'))
#define LIST_MARKER (BSL_FILE_MARKER ('L', 'I', 'S', 'T'))

/* typedefs */

/*!
	RIFF�t�@�C�����͏������s����{����I�u�W�F�N�g
	- �g�p�菇
		-# bslFileRiff()�ɂ�萧��I�u�W�F�N�g���\�z
		-# ���L�̃��\�b�h�̌p����ݒ肷��
			- BSLFileRiff::getListChunk()
			- BSLFileRiff::getChunk()
			- BSLFileRiff::checkTypeHeader()
			- BSLFileRiff::checkRiffHeader()
		-# BSLFile����I�u�W�F�N�g���\�z
		-# BSLFileRiff::parse ()�ɂ��RIFF�\���̃p�[�X���s��
		-# �C�ӏ���
		-# bslFileExit()�ɂ��BSLFile����I�u�W�F�N�g��j��
*/
typedef struct BSLFileRiff
{
	/*! RIFF��parse�������s�� */
	BSLErr (*parse) (struct BSLFileRiff *obj, BSLFile *file, void *user);
	/*! LIST Chunk�̎擾�������s�� */
	BSLErr (*getListChunk) (struct BSLFileRiff *obj, DWORD parentHeader, DWORD header, unsigned long offset, unsigned long size, int level, void *user);
	/*! Chunk�̎擾�������s�� */
	BSLErr (*getChunk) (struct BSLFileRiff *obj, DWORD parentHeader, DWORD header, unsigned long offset, unsigned long size, int level, void *user);
	/*! RIFF�w�b�_�̊m�F�������s�� */
	BSLErr (*checkRiffHeader) (struct  BSLFileRiff *obj, DWORD header, void *user);
	/*! Type�w�b�_�̊m�F�������s�� */
	BSLErr (*checkTypeHeader) (struct  BSLFileRiff *obj, DWORD header, void *user);
	/*! LIST Chunk��parse�������s�� */
	BSLErr (*parseListChunk) (struct BSLFileRiff *obj, DWORD header, unsigned long offset, unsigned long size, int level, void *user);

	BSLFile *file;
} BSLFileRiff;

/* globals */

/* locals */

/* function declarations */

BSLErr bslFileRiff (BSLFileRiff *obj);

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLFileRiffh */
