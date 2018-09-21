package kr.keumyoung.mukin.data.bus;

import kr.keumyoung.mukin.elements.ControlPanel;
import kr.keumyoung.mukin.elements.ControlPanelItem;

/**
 *  on 16/01/18.
 */

public class ControlPanelItemAction {
    private ControlPanel.ControlPanelOption panelOption;
    private ControlPanelItem.SelectionState selectionState;

    public ControlPanelItemAction(ControlPanel.ControlPanelOption panelOption, ControlPanelItem.SelectionState selectionState) {
        this.panelOption = panelOption;
        this.selectionState = selectionState;
    }

    public ControlPanel.ControlPanelOption getPanelOption() {
        return panelOption;
    }

    public ControlPanelItem.SelectionState getSelectionState() {
        return selectionState;
    }
}
