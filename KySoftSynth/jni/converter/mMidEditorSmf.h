/* mMidEditorSmf.h - smf defines */

/* */

/* 
modification history
--------------------
*/

#ifndef __INCmMidEditorSmfh
#define __INCmMidEditorSmfh

/* include */

#include <vector>

#include "mMidReaderSmf.h"
#include "mMidEditorSmfTrk.h"
#include "mFileSave.h"

/* defines */

/* typedefs */

typedef std::vector<MMidEditorSmfTrk *> MMidEditorSmfTrkType; 

/* class */

class MMidEditorSmf : public MMidReaderSmf, public MFileSave
{
public:

	MMidEditorSmf ();
	virtual ~MMidEditorSmf ();

	void fileNew (void);

	virtual int ready
		(
		int formatTypes = 0, // number of supported formats
		int *formatTypeList = NULL, // supported formats
		int divisionType = 0, // number of supported divisions
		int *divisionTypeList = NULL,	// supported divisions
		void *user = NULL // other parameter in ascii
		);

  virtual int rewind (DWORD startReadtick = 0UL);
  
	virtual int save_ (MFileRef fileRef, void *parameter);

	int trackAdd (MMidEditorSmfTrk *trk);
	MMidEditorSmfTrk *trackAdd (void);
	int trackDelete (int index);
	int trackDeleteAll (void);
	virtual MMidReaderBaseTrk *trackGet (int trk);
  
  int numberOfTracks (void);

	void useRunningStatus (BOOL status = true);
	void noteOffIs9nH (BOOL status = true);

protected:

private:

	MMidEditorSmfTrkType vTrk;

	BOOL bUseRunningStatus;
	BOOL bNoteOffIs9nH;
};

#endif /* __INCmMMidEditorSmfh */
