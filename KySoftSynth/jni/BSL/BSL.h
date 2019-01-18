/*
 *  BSL.h
 *
 *  Copyright (c) 2004-2009 bismark. All rights reserved.
 *
 */

/*!
	@file BSL.h
	@brief
	BSL基本ヘッダ
*/

/*! \mainpage
	-# 目的<BR>
		BSLは下記を目的とする汎用ライブラリである。
		- 開発資産の社内共有によるアプリケーション、ミドルウエア等の開発効率の向上
		- 環境非依存のコード構築によるアプリケーション、ミドルウエア等の移植性向上
		- 各種デスクトップOS
		- 各種組み込み環境
		これに従い、以下の基本姿勢に基づいてライブラリ開発を行う。
		- C言語によるコーディング
		- モジュール、オブジェクト的構築
		- モジュール、ソース毎のテストツールの整備による、信頼性検証手段ならびにサンプルの提供
		- ドキュメントの整備 (Doxygenによる)
	-# 用途
	-# モジュール<BR>
		本ライブラリは下記に示すモジュールに分類して構築される。
		- Debug: デバッグ用ユーティリティ
		- Eft: オーディオエフェクト処理
		- Enc: 暗号/復号化処理
		- Error: エラー管理
		- File: ファイル入出力
		- Lyr: カラオケ歌詞文字（表示、ファイルパース/ライト）
		- Math: 数値演算
		- Mem: メモリ管理
		- Mid: MIDI（入出力、MIDIシーケンスファイルパース/ライト）
		- Syn: ソフトウエアシンセサイザ
		- Text: テキスト処理
		- Wav: Wave（入出力、ファイルパース/ライト、コーデック）
		- Wtbl: Wave Table（ファイルパース/ライト）
	-# 制御オブジェクト<BR>
		本ライブラリはC言語にて構築するものであるが、
		ライブラリのオブジェクト指向 的発展を図るため、制御オブジェクトを定義する。
		制御オブジェクトとは、基本要素に対する制御を行うメソッド用関数ポインタ、
		基本要素に関する情報を保持するメンバー変数、をまとめたC構造体にて定義される。
		メソッド用関数ポインタを置き換えることで擬似的な制御オブジェクトの継承、
		メソッド用関数の仮想関数的使用を行うことを目的とするものである。
		- 基本構成
		下記に基本構成を示す。
\code
typedef struct BSLObj
{
	BSLErr(*method0) (struct BSLObj *obj, int method_parameter0);
	int parameter0;
} BSLObj;
\endcode
		下記に実装例を示す。
		- BSLObj.h: 基本オブジェクト
\code
typedef struct BSLObj
{
	int (*sample) (struct BSLObj *obj);
	int parameter
} BSLObj;

void bslObj (BSLObj *obj);
void bslObjExit (BSLObj *obj);
\endcode
		制御オブジェクトは最低限以下の構成をとる。
			-# メソッドならびにメンバを定義する構造体定義 typedef struct BSLXxx。
			-# 構築関数：制御オブジェクトを作成する void bslXxx (BSLXxx *obj, ...) 形式の関数。継承されたオブジェクト毎に個別の構築関数が用意される。
			-# 破棄関数：制御オブジェクトの破棄を宣言する void bslXxxExit (BSLXxx *obj)の形式の関数。継承された全オブジェクトで共通の破棄方法をとる。
		- BSLObj.c: 基本オブジェクト
\code
static void sample(BSLObj *obj)
{
	printf (“param is %d\n”, obj->parameter);
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
		- BSLObj2.h: 継承オブジェクト
\code
void BSLObj2 (BSLObj *obj);
\endcode
		- BSLObj2.c: 継承オブジェクト
\code
static void sample(BSLObj *obj)
{
	printf (“param is %d\n”, obj->parameter + 1);
}

void CrObj2 (BSLObject *obj)
{
	parameter = 0;
	obj->sample = sample;
}
\endcode
		- main.c: サンプル
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
		制御オブジェクトの採用は同時にアプリケーションの可搬性を高めることを目的とするものである。
		従って、制御オブジェクトを記述する構造体の内容に関してはそれぞれアクセス用の関数を予め定義し、
		アプリケーションが構造体の内容へ直接アクセスすることを防ぐことが望ましい。
	-# コーディング
		-# 言語<BR>
			C言語
		-# コンパイラ<BR>
			ANSI C準拠コンパイラの対応が可能であること。以下をリファレンス環境とする
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
		-# 動作環境<BR>
		　基本は環境非依存とする。以下をリファレンス環境とする。
			- Windows XP SP2 / 2000 SP4 / Me / 98SE
			- Windows CE 3.x (Windows Mobile 2002), 4.x (Windows Mobile 2003)
			- Mac OS X 10.3
			- RedHat Linux 9
	-# コーディング規則
		-# ファイル名
			- BSL[ModuleName (先頭のみ大文字)][任意文字列 (先頭のみ大文字)][…].c
				- ex) BSLWavSample.c
			- BSL[ModuleName (先頭のみ大文字)][任意文字列 (先頭のみ大文字)] […].h
				- ex) BSLWavSample.h
		-# 定義名（deine / typedef）
			-# 本ライブラリ外部へ公開される定義
				- BSL_[Module Name (大文字)]_[任意文字列 (大文字)] […]
					- ex) BSL_WAV_SAMPLE
			-# 非公開定義
				- _[任意文字列 (大文字)]_ [任意文字列 (大文字)] […]
					- ex) _MAX_SAMPLE
			-# 制御オブジェクト定義
				- BSL[ModuleName (先頭のみ大文字)][任意文字列 (先頭のみ大文字)]
					- ex) BSLSample
		-# 関数名
			-# 本ライブラリ外部へ公開される関数
				- bsl[Module Name (先頭のみ大文字)] [任意動詞文字列 (先頭のみ大文字)][任意文字列 (先頭のみ大文字)] […]
					- ex) bslWavReadSample ()
			-# 本ライブラリ内部へのみ公開される関数
				- _[Module Name (小文字)] [任意動詞文字列 (先頭のみ大文字)][任意文字列 (先頭のみ大文字)] […]
					- ex) _bslWavReadSample ()
					- 主に継承された制御オブジェクトが基本または親の制御オブジェクトのメソッドを使用する場合に用いる。
			-# 非公開関数
				- _[任意動詞文字列 (小文字)] [任意文字列 (先頭のみ大文字)]…
					- ex) _getData ()
					- static宣言を行うこと
	-# モジュール共通項目
		-# ファイル
			- BSL.h: 本ライブラリの基本定義を行う
			- BSLConf.h: ターゲット依存定義を行う。常にBSL.hよりincludeされる。
		-# プラットフォーム定義
			対応プラットフォームに従い、BSLConf.hにて下記のdefineを排他的に定義する。
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

/*! BSLライブラリのバージョンを示す */
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

