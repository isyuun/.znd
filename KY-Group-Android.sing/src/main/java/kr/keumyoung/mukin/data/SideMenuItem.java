package kr.keumyoung.mukin.data;


import kr.keumyoung.mukin.fragment.BaseFragment;

/**
 *  on 21/09/17.
 */
// unused
public class SideMenuItem<T extends BaseFragment> {
    private String name;
    private int icon;
    private T fragment;

    public SideMenuItem(int icon, String name, T fragment) {
        this.name = name;
        this.icon = icon;
        this.fragment = fragment;
    }

    public T getFragment() {
        return fragment;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

}
