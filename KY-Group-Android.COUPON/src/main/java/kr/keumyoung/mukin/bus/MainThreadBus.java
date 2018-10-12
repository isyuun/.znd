package kr.keumyoung.mukin.bus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *  on 2/18/16.
 */
public class MainThreadBus extends Bus {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private Set<Object> mObjectsToUnregister = new HashSet<>();

    @Override
    public void register(Object object) {
        if (!mObjectsToUnregister.contains(object)) {
            mObjectsToUnregister.add(object);
            super.register(object);
        }
    }

    @Override
    public void unregister(Object object) {
        if (mObjectsToUnregister.contains(object)) {
            mObjectsToUnregister.remove(object);
            super.unregister(object);
        }
    }

    public void unregisterStackEvents() {
        ArrayList<Object> todelete = new ArrayList<>();
        for (Object event : mObjectsToUnregister) {
            super.unregister(event);
            todelete.add(event);
        }
        mObjectsToUnregister.removeAll(todelete);
    }

    @Override
    public void post(final Object event) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event);
            } else {
                mHandler.post(() -> MainThreadBus.super.post(event));
            }
        } catch (RuntimeException ex) {
            // catch if bus already freed
            ex.printStackTrace();
        }
    }
}
