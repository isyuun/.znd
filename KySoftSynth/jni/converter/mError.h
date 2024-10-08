/* mError.h - */

/* */

/* 
modification history
--------------------
*/

/* 
DESCRIPTION 
*/

#ifndef __INCmErrorh
#define __INCmErrorh

/* defines */

enum
{
	// file
	kErrFileOpen = 1,
	kErrFileCreate,
	kErrFileSize,
	kErrFileSizeOver,
	kErrFileRead,
	kErrFileReadEOF,
	kErrFileWrite,
	kErrFileSeek,
	kErrFileNotReady,
	kErrFileUnknownStructure,
	kErrFileRiffHeader,
	kErrFileUnsupportedFormat,

	// memory
	kErrMemAlloc,
	kErrMemFree,
	kErrMemHandle,
	kErrMemHeapCreate,
	kErrMemHeapDestroy,

	// c++
	kErrClassConstruct,
	kErrClassDestruct,

	// os
	kErrOsDllNotFound,
	kErrOsThreadCreate,

	// param
	kErrParamWrong,
	kErrParamRange,
	kErrParamChecksum,

	// smf
	kErrSmfHeaderChunk,
	kErrSmfHeaderChunkSize,
	kErrSmfFormat,                       // 非対応format
	kErrSmfTracks0,                      // format 0においてtrack数 > 0である
	kErrSmfTracks,                       // track数 > kMidSmfTracks
	kErrSmfDivisionFormat,
	kErrSmfDivision,
	kErrSmfTrackChunk,
	kErrSmfNoEOT,
	kErrSmfVarLenRead,
	kErrSmfNoStatus,
	kErrSmfUndefinedStatus,
	kErrSmfSysexSize,
	kErrSmfSysexSize2,
	kErrSmfSysexData,
	kErrSmfSysexData2,
	kErrSmfChannelMsgData,
	kErrSmfChannel,
	kErrSmfMetaSize,
	kErrSmfMetaData,
	kErrSmfMetaType,
	kErrSmfNoTrackName,
	kErrSmfNon1stTrackName,   
	kErrSmfNoData,
	kErrSmfMemoryEmpty,
	kErrSmfTrackSelectFail, 

	// undefined
	kErrUndefined = -1
};

#endif /* __INCmErrorh */

