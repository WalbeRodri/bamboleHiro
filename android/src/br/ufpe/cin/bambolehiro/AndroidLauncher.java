package br.ufpe.cin.bambolehiro;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.util.List;

public class AndroidLauncher extends AndroidApplication implements Game.IOpenActivity, Game.IBluetooth {
	private BluetoothLeService mBluetoothLeService;
	private String mDeviceAddress = SampleGattAttributes.BT_ADDRESS;
	private boolean mConnected = false;
	private String BLEData = "GIROU";
    private List<BluetoothGattService> gattServices;
    private BluetoothGattCharacteristic btCharacteristic;

	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(">>>>>>>>>>>>>>>>>>>>", "Unable to initialize Bluetooth");
				finish();
			}
            Log.e(">>>>>>>>>>>>>>>>>>>>", "conecting to Bluetooth");

            // Automatically connects to the device upon successful start-up initialization.
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            mConnected = result;
			Log.e(">>>>>>>>>>." , "" +
					" CONXAO "+ result);
		}
		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
            }
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                gattServices = mBluetoothLeService.getSupportedGattServices();
                for (BluetoothGattService gattService : gattServices) {
                    String uuid = gattService.getUuid().toString();
                    if (uuid.equals(SampleGattAttributes.BAMBOLE_SERVICE)) {
                        List<BluetoothGattCharacteristic> characteristics = gattService.getCharacteristics();
                        for (BluetoothGattCharacteristic gattCharacteristic : characteristics) {
                            String charaUUID = gattCharacteristic.getUuid().toString();
                            if (charaUUID.equals(SampleGattAttributes.BAMBOLE_DATA)) {
                                btCharacteristic = gattCharacteristic;
                                mBluetoothLeService.setCharacteristicNotification(btCharacteristic, true);
                            }
                        }
                    }
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                setBLEData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //Log.d(">>>>>>>>>.", getBLEData());
            }
        }
    };
	private void updateConnectionState(final int resourceId) {
	    Log.d(">>>>>>>>>>>>>>>>>>>", "olha algo relacionado aa conexao"+ resourceId);
	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useWakelock = true;
		Game bambolehiro = new Game();
		bambolehiro.setOpenActivity(this);
		initialize(bambolehiro, config);

        bambolehiro.setBluetoothInterface(this);
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	@Override
    protected void onResume(){
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d( "Opa: ", "OLHA EU CONECTADO :D =" + result);
        }
    }
	private void displayData(String data) {
		if (data != null) {
			Log.d("do something with: ", data);
		}
	}



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

	@Override
	public void openScoreActivity(double score){
		Intent intent = new Intent(this, ScoreActivity.class);
		// do whatever you want with the supplied parameters.
		intent.putExtra("score", score);
		startActivity(intent);
	}


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public String getBLEData(){
        return this.BLEData;
    }

    public void setBLEData(String bleData){
        this.BLEData = bleData;
    }

	@Override
	public boolean isConnected() {
		return mConnected;
	}

    @Override
	public boolean readBLEData(){
        return getBLEData().contains("girou") || getBLEData().contains("GIROU");
    }

}
