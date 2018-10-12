package kr.keumyoung.mukin.data.model;

import org.joda.time.DateTime;

import javax.inject.Inject;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.helper.DateHelper;

/**
 *  on 13/01/18.
 */

public class Genre {
    private String name, genreId, icon;
    private int count;
    private DateTime createdOn, updatedOn;

    @Inject
    DateHelper dateHelper;

    public Genre() {
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCount(String count) {
        this.count = Integer.parseInt(count);
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public DateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = dateHelper.parseDate(createdOn);
    }

    public DateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = dateHelper.parseDate(updatedOn);
    }
}
