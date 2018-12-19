package kr.keumyoung.mukin.util;

/*
 * Author dsjung
 * 2018.09.12
 * check line headset & bluttooth mukin mic plug in & out
 * singleton class
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import kr.keumyoung.mukin.R;

public class MicChecker {

    private static final String TAG = MicChecker.class.getSimpleName();
    private static MicChecker instance;
    private Context mContext;
    private BroadcastReceiver broadcastReceiver;
    private MicCheckEventListener micCheckEventListener;
    private BluetoothProfile.ServiceListener mProfileListener;
    private MIC_CONNECTION_STATES states;
    private boolean isEarphoneON;
    private boolean isBlueToothON;
    //private String mukin_mic_name;
    private String last_bluetooth_device_name;

    private static String BLUETOOTH_MUKIN_K200 = ("KY Mukin K200");
    private static String BLUETOOTH_MUKIN_K200S = ("KY Mukin K200S");

    public enum MIC_CONNECTION_STATES {
        BLUETOOTH_MUKIN_K200,    //KY Mukin K200
        BLUETOOTH_MUKIN_K200S,    //KY Mukin K200S
        BLUETOOTH_OTHERS,   //블루투스 뮤젠 외 다른 마이크
        HEADSET,            //유선 헤드셋
        NONE                //NONE
    }

    String preDeviceAddress = new String();
    int preDeviceStates = -1;

    public MicChecker(Context context) {
        mContext = context;
        //mukin_mic_name = mContext.getResources().getString(R.string.mukin_bluetooth_mic_device_name);
        states = MIC_CONNECTION_STATES.NONE;
        BluetoothAdapter mBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();

        mProfileListener = new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceDisconnected(int profile) {
                Log.d(TAG, "onServiceDisconnected");
            }

            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                for (BluetoothDevice device : proxy.getConnectedDevices()) {
                    Log.d(TAG, "onServiceConnected" + " | " + device.getName() + " | " + device.getAddress() + " | " + proxy.getConnectionState(device) + "(connected = "
                            + BluetoothProfile.STATE_CONNECTED + ")");

                    //왜 두번들어와
                    if (preDeviceAddress.compareToIgnoreCase(device.getAddress()) == 0)
                        return;

                    preDeviceAddress = device.getAddress();

                    //HEADSET 타입이거나
                    //A2DP는 보통 마이크가 안달린넘인데 특별히 뮤즐만 예외로 체크 한다.
                    if ((profile == BluetoothProfile.HEADSET || profile == BluetoothProfile.A2DP) /*&& BLUETOOTH_MUKIN_K200.compareToIgnoreCase(device.getName()) == 0*/) {
                        if (BLUETOOTH_MUKIN_K200.equalsIgnoreCase(device.getName()) || BLUETOOTH_MUKIN_K200S.equalsIgnoreCase(device.getName())) {
                            switchBlueToothStates(device.getName(), true);
                        }
                    }
                }
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
            }
        };

        //HEAD = 마이크도 달린것
        //A2DP = 보통 헤드폰
        //그런데 뮤즐 마이크는 A2DP로 검색이됨. 마이크 입력을 안받기 때문인듯.
        //따라서 2가지 다 체크해야함
        //try{}... - isyuun@keumyoung.kr
        try {
            mBlueToothAdapter.getProfileProxy(mContext, mProfileListener, BluetoothProfile.A2DP);
            mBlueToothAdapter.getProfileProxy(mContext, mProfileListener, BluetoothProfile.HEADSET);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized MicChecker createInstance(Context context) {
        if (instance == null) {
            instance = new MicChecker(context);
            MicChecker.getInstance().registBrocastReceiver();
        }
        return instance;
    }

    public static synchronized MicChecker getInstance() {
        return instance;
    }

    public MIC_CONNECTION_STATES getStates() {
        return states;
    }

    private boolean switchBlueToothStates(String devicename, boolean bStates) {
        boolean bPreBlueToothStates = isBlueToothON;

        isBlueToothON = bStates;

        Log.d(TAG, devicename + " Is connected = " + Boolean.toString(isBlueToothON));

        //states = isBlueToothON ? MIC_CONNECTION_STATES.BLUETOOTH_MUKIN_K200 : MIC_CONNECTION_STATES.NONE;

        if (bPreBlueToothStates == false && isBlueToothON) {
            //뮤젠 마이크인경우
            if (BLUETOOTH_MUKIN_K200.compareToIgnoreCase(devicename) == 0)
                states = MIC_CONNECTION_STATES.BLUETOOTH_MUKIN_K200;
            else if (BLUETOOTH_MUKIN_K200S.compareToIgnoreCase(devicename) == 0)
                states = MIC_CONNECTION_STATES.BLUETOOTH_MUKIN_K200S;
            else
                states = MIC_CONNECTION_STATES.BLUETOOTH_OTHERS;

            last_bluetooth_device_name = devicename;
        } else {
            if (devicename.compareToIgnoreCase(last_bluetooth_device_name) == 0)
                states = MIC_CONNECTION_STATES.NONE;
        }

        //이어폰은 우선
        if (isEarphoneON)
            states = MIC_CONNECTION_STATES.HEADSET;

        if (micCheckEventListener != null)
            micCheckEventListener.onStateChangedEvent(states);


        //_Log.d(TAG, "states = " + states);
        Log.e("MicChecker", "switchBlueToothStates()" + ":" + devicename + ":" + bStates + ":" + states);

        return true;
    }

    private boolean switchHeadSetStates(boolean bStates) {
        Log.e("MicChecker", "switchHeadSetStates()" + ":" + last_bluetooth_device_name + ":" + bStates);

        isEarphoneON = bStates;

        Log.d(TAG, "HeadSet = " + Boolean.toString(isEarphoneON));

        states = isEarphoneON ? MIC_CONNECTION_STATES.HEADSET : MIC_CONNECTION_STATES.NONE;

        //이어폰 OFF / 블루투스 ON 인 경우
        if (isEarphoneON == false && isBlueToothON) {

            if (BLUETOOTH_MUKIN_K200.compareToIgnoreCase(last_bluetooth_device_name) == 0)
                states = MIC_CONNECTION_STATES.BLUETOOTH_MUKIN_K200;
            if (BLUETOOTH_MUKIN_K200S.compareToIgnoreCase(last_bluetooth_device_name) == 0)
                states = MIC_CONNECTION_STATES.BLUETOOTH_MUKIN_K200S;
            else
                states = MIC_CONNECTION_STATES.BLUETOOTH_OTHERS;
        }

        if (micCheckEventListener != null)
            micCheckEventListener.onStateChangedEvent(states);

        return true;
    }

    public interface MicCheckEventListener {
        void onStateChangedEvent(MIC_CONNECTION_STATES states);
    }

    public void setOnStateChangedEvent(MicCheckEventListener listener) {
        micCheckEventListener = listener;
    }

    public void registBrocastReceiver() {
        //브로드캐스트리시버를 이용하여 블루투스 장치가 연결이 되고, 끊기는 이벤트를 받아 올 수 있다.
        broadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                //연결된 장치를 intent를 통하여 가져온다.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //장치가 연결이 되었으면
                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    switchBlueToothStates(device.getName(), true);

                    //장치의 연결이 끊기면
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    switchBlueToothStates(device.getName(), false);

                    //헤드셋 플러그인&아웃 체크
                } else if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                    switchHeadSetStates((intent.getIntExtra("state", 0) > 0) ? true : false);
                }
            }
        };

        try {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
            mContext.registerReceiver(broadcastReceiver, filter);
            filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            mContext.registerReceiver(broadcastReceiver, filter);
            filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
            mContext.registerReceiver(broadcastReceiver, filter);
            Log.d(TAG, "MicChecker regist");
        } catch (Exception e) {
            Log.d(TAG, "MicChecker regist erroe");
            e.printStackTrace();
        }


    }

    public void unregistBrocastReceiver() {
        mContext.unregisterReceiver(broadcastReceiver);
        Log.d(TAG, "MicChecker unregist");
    }
}


/* Code Graveyard
 * 01 페어링 되어있는 블루투스 기기 장치 리스트 얻어오기
 * 단, 기기가 ON OFF 상태인지는 체크가 안됨
    private void checkBlueToothPairing() {
        //블루투스 Adapter를 가져온다
        BluetoothAdapter mBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBlueToothAdapter == null) {
            // 만약 블루투스 adapter가 없으면, 블루투스를 지원하지 않는 기기이거나 블루투스 기능을 끈 기기이다.
        } else {
            // 블루투스 adapter가 있으면, 블루투스 adater에서 페어링된 장치 목록을 불러올 수 있다.
            Set<BluetoothDevice> pairDevices = mBlueToothAdapter.getBondedDevices();

            //페어링된 장치가 있으면
            if (pairDevices.size() > 0) {
                for (BluetoothDevice device : pairDevices) {

                    int deviceClass = device.getBluetoothClass().getDeviceClass();
                    if (deviceClass == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET
                            || deviceClass == BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE
                            || deviceClass == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES
                            || deviceClass == BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE) {

                    }
                    //페어링된 장치 이름과, MAC주소를 가져올 수 있다.
                    //switchBlueToothStates(device.getName(), true);
                }
            } else {
                Toast.makeText(mContext, "no Device", Toast.LENGTH_SHORT).show();
            }
        }
    }
 */