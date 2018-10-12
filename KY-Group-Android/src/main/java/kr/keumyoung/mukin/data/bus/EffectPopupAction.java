package kr.keumyoung.mukin.data.bus;

import kr.keumyoung.mukin.elements.EffectsPopup;
import kr.keumyoung.mukin.elements.EffectsPopupItem;

/**
 *  on 21/03/18.
 * Project: KyGroup
 */

public class EffectPopupAction {
    private EffectsPopup.EffectOptions effectOptions;
    private EffectsPopupItem.SelectionState selectionState;

    public EffectPopupAction(EffectsPopup.EffectOptions effectOptions, EffectsPopupItem.SelectionState selectionState) {
        this.effectOptions = effectOptions;
        this.selectionState = selectionState;
    }

    public EffectsPopup.EffectOptions getEffectOptions() {
        return effectOptions;
    }

    public EffectsPopupItem.SelectionState getSelectionState() {
        return selectionState;
    }
}
