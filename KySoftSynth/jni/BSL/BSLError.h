/*
 *  BSLError.h
 *
 *  Copyright (c) 2004-2009 bismark. All rights reserved.
 *
 */

/*!
	@file BSLError.h
	@brief
	エラー管理 (エラーコード定義のみ)
*/

#ifndef __INCBSLErrorh
#define __INCBSLErrorh

#ifdef __cplusplus
extern "C" {
#endif

/* includes */

/* defines */

/*! エラーコード */
typedef enum {
	/*! 正常終了 */
	BSL_OK = 0,

	/*! ユーザーキャンセル */
	BSL_ERR_CANCELED,
	/*! 未定義エラー */
	BSL_ERR_UNDEFINED,
	/*! プロテクションエラー */
	BSL_ERR_PROTECTED,
	/*! 未対応項目 */
	BSL_ERR_NOT_SUPPORTED,
	/*! 外部モジュール */
	BSL_ERR_MODULE,


	/*! パラメータ：パラメータ異常 */
	BSL_ERR_PARAM_WRONG,
	/*! パラメータ：パラメータ範囲外 */
	BSL_ERR_PARAM_RANGE,


	/*! ファイル：オープン */
	BSL_ERR_FILE_OPEN,
	/*! ファイル：新規作成 */
	BSL_ERR_FILE_CREATE,
	/*! ファイル：サイズ取得 */
	BSL_ERR_FILE_SIZE,
	/*! ファイル：未オープン */
	BSL_ERR_FILE_NOT_READY,
	/*! ファイル：リード */
	BSL_ERR_FILE_READ,
	/*! ファイル：EOF */
	BSL_ERR_FILE_READ_EOF,
	/*! ファイル：ライト */
	BSL_ERR_FILE_WRITE,
	/*! ファイル：シーク */
	BSL_ERR_FILE_SEEK,
	/*! ファイル：未定義フォーマット */
	BSL_ERR_FILE_UNKNOWN_STRUCTURE,
	/*! ファイル：RIFFヘッダ関連 */
	BSL_ERR_FILE_RIFF_HEADER,
	/*! ファイル：未対応フォーマット */
	BSL_ERR_FILE_UNSUPPORTED_FORMAT,
	

	/*! メモリ：メモリ取得 */
	BSL_ERR_MEM_ALLOC,
	/*! メモリ：メモリ破棄 */
	BSL_ERR_MEM_FREE,
	/*! メモリ：ヒープ構築 */
	BSL_ERR_MEM_HEAP_CREATE,
	/*! メモリ：ヒープ破棄 */
	BSL_ERR_MEM_HEAP_DESTROY,
	

	/*! Mid：入力ドライバオープン */
	BSL_ERR_MID_IN_OPEN,
	/*! Mid：入力ドライバクローズ */
	BSL_ERR_MID_IN_CLOSE,
	/*! Mid：入力開始 */
	BSL_ERR_MID_IN_START,
	/*! Mid：入力停止 */
	BSL_ERR_MID_IN_STOP,


	/*! Mid：出力ドライバオープン */
	BSL_ERR_MID_OUT_OPEN,
	/*! Mid：出力ドライバクローズ */
	BSL_ERR_MID_OUT_CLOSE,
	/*! Mid：出力開始 */
	BSL_ERR_MID_OUT_START,
	/*! Mid：出力停止 */
	BSL_ERR_MID_OUT_STOP,


	/*! Wav：入力ドライバオープン */
	BSL_ERR_WAV_IN_OPEN,
	/*! Wav：入力ドライバクローズ */
	BSL_ERR_WAV_IN_CLOSE,
	/*! Wav：入力開始 */
	BSL_ERR_WAV_IN_START,
	/*! Wav：入力停止 */
	BSL_ERR_WAV_IN_STOP,
	/*! Wav：入力一時停止 */
	BSL_ERR_WAV_IN_PAUSE,
	/*! Wav：入力再開始 */
	BSL_ERR_WAV_IN_RESTART,
	/*! Wav：入力ドライバリセット */
	BSL_ERR_WAV_IN_RESET,
#if BSL_WINDOWS
	/*! Wav：入力バッファ */
	BSL_ERR_WAV_IN_PREPARE_HEADER,
	/*! Wav：入力バッファ */
	BSL_ERR_WAV_IN_UNPREPARE_HEADER,
#endif /* BSL_WINDOWS */
	/*! Wav：入力バッファ追加 */
	BSL_ERR_WAV_IN_ADD_BUFFER,
	/*! Wav：入力スレッド・セマフォ他 */
	BSL_ERR_WAV_IN_PROCESS,
	/*! Wav：出力ドライバオープン */
	BSL_ERR_WAV_OUT_OPEN,
	/*! Wav：出力ドライバクローズ */
	BSL_ERR_WAV_OUT_CLOSE,
	/*! Wav：出力開始 */
	BSL_ERR_WAV_OUT_START,
	/*! Wav：出力停止 */
	BSL_ERR_WAV_OUT_STOP,
	/*! Wav：出力一時停止 */
	BSL_ERR_WAV_OUT_PAUSE,
	/*! Wav：出力再開始 */
	BSL_ERR_WAV_OUT_RESTART,
	/*! Wav：出力ドライバリセット */
	BSL_ERR_WAV_OUT_RESET,
#if BSL_WINDOWS
	/*! Wav：出力バッファ */
	BSL_ERR_WAV_OUT_PREPARE_HEADER,
	/*! Wav：出力バッファ */
	BSL_ERR_WAV_OUT_UNPREPARE_HEADER,
#endif /* BSL_WINDOWS */
	/*! Wav：出力バッファライト */
	BSL_ERR_WAV_OUT_WRITE,
	/*! Wav：出力スレッド・セマフォ他 */
	BSL_ERR_WAV_OUT_PROCESS,
	/*! Wav：出力データ異常 */
	BSL_ERR_WAV_OUT_DATA,
	/*! Wav：サンプリング周波数設定 */
	BSL_ERR_WAV_SET_SAMPLE_RATE,
	/*! Wav：ビット精度設定 */
	BSL_ERR_WAV_SET_BITS_PER_SAMPLE,
	/*! Wav：ビットレート設定 */
	BSL_ERR_WAV_SET_BIT_RATE,
	/*! Wav：ブロックサイズ設定 */
	BSL_ERR_WAV_SET_BLOCK_SIZE,
	/*! Wav：チャンネル数設定 */
	BSL_ERR_WAV_SET_CHANNELS,
	/*! Wav：エンディアン設定 */
	BSL_ERR_WAV_SET_BIGENDIAN,
	/*! Wav：Unsigned設定 */
	BSL_ERR_WAV_SET_UNSIGNED,
	/*! Wav：バッファ数設定 */
	BSL_ERR_WAV_SET_BUFFERS,
	/*! Wav：コーデック設定 */
	BSL_ERR_WAV_SET_CODEC,
	/*! Wav：デコード処理 */
	BSL_ERR_WAV_DEC,
	/*! Wav：デコード処理-後続のデータが必要- */
	BSL_ERR_WAV_DEC_REQUEST_FOLLOWING,
	/*! Wav：デコード処理-最終データ不正- */
	BSL_ERR_WAV_DEC_LAST_DATA,
	/*! Wav：デコード処理-オーバーフロー- */
	BSL_ERR_WAV_DEC_OVERFLOW,
	/*! Wav：エンコード処理 */
	BSL_ERR_WAV_ENC,

	/*! Wavデータ：WAVEデータの再生時間が規定に満たない */
	BSL_ERR_WAVDATA_SHORT_PLAYTIME,

	/*! CDRIP : */
	BSL_ERR_CDRIP_OPEN,
	/*! CDRIP : */
	BSL_ERR_CDRIP_CLOSE,
	/*! CDRIP : */
	BSL_ERR_CDRIP_INVALID_HANDLE,
	/*! CDRIP : */
	BSL_ERR_CDRIP_MEDIA_CHANGED,
	/*! CDRIP : */
	BSL_ERR_CDRIP_GETTOC,
	/*! CDRIP : */
	BSL_ERR_CDRIP_READCD,

	BSL_ERR_HEX_WRONG_ENDRECORD,
	BSL_ERR_HEX_INVALID_CHECKSUM,
	BSL_ERR_HEX_INVALID_ENDRECORD,
	BSL_ERR_HEX_NO_ENDRECORD,
	BSL_ERR_HEX_NO_VALID_DATARECORD,
	BSL_ERR_HEX_INVALID_FORMAT,
	BSL_ERR_HEX_ADDRESS_OVERLAP,

	BSL_ERR_MAX
} BSLErr;

/* typedefs */

/* globals */

/* loclas */

/* function declarations */

#ifdef __cplusplus
}
#endif

#endif /* __INCBSLErrorh */

