package kr.keumyoung.mukin.data.bus;

import kr.keumyoung.mukin.elements.ModePopup;
import kr.keumyoung.mukin.elements.ModePopupItem;

/**
 *  on 21/03/18.
 * Project: KyGroup
 */

public class ModePopupAction {
    private ModePopup.ModeOptions modeOptions;
    private ModePopupItem.SelectionState selectionState;

    public ModePopupAction(ModePopup.ModeOptions modeOptions, ModePopupItem.SelectionState selectionState) {
        this.modeOptions = modeOptions;
        this.selectionState = selectionState;
    }

    public ModePopup.ModeOptions getModeOptions() {
        return modeOptions;
    }

    public ModePopupItem.SelectionState getSelectionState() {
        return selectionState;
    }
}
