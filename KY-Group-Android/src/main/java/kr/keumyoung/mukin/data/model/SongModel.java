package kr.keumyoung.mukin.data.model;

import javax.inject.Inject;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.helper.DateHelper;

/**
 *  on 30/01/18.
 * Project: KyGroup
 */

public class SongModel {
    @Inject
    DateHelper dateHelper;

    public SongModel() {
        MainApplication.getInstance().getMainComponent().inject(this);
    }
}
