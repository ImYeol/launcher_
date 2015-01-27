package com.example.Companion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.SoundEffectConstants;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.Camera.TakePictureCallback;
import com.example.launcher.R;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardScrollView;
import com.google.android.glass.widget.Slider;

public class ImageTransferHelper extends Activity {

	public final static String TAG = "ImageTransferHelper";
	public static final int REQUEST_TO_ENABLE_BT = 100;
	private BluetoothAdapter mBluetoothAdapter;
	private UUID MY_UUID = UUID
			.fromString("D04E3068-E15B-4482-8306-4CABFA1726E7");
	private final static String CBT_SERVER_DEVICE_NAME = "IM-T100K";
	private BluetoothSocket sock;
	private Uri fileUri;

	private String uri;
	private String comment;
	private ProgressBar fileTransferStateBar;

	private CardScrollView mCardScroller;
    private Slider mSlider;
    private Slider.Indeterminate mIndeterminate;
    private Slider.GracePeriod mGracePeriod;
    private AudioManager audio;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.transfer_dialog);
		setActivitySize();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		fileTransferStateBar = (ProgressBar) findViewById(R.id.file_transfer_progressbar);
		audio=(AudioManager)getSystemService(AUDIO_SERVICE);
		Intent intent = getIntent();
		uri = intent.getExtras().getString("Uri");
		comment=intent.getExtras().getString("comment");
		if (mBluetoothAdapter == null) {
			Log.v(TAG, "Device does not support Bluetooth");
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				Log.v(TAG, "Bluetooth supported but not enabled");
				Toast.makeText(ImageTransferHelper.this,
						"Bluetooth supported but not enabled",
						Toast.LENGTH_LONG).show();
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_TO_ENABLE_BT);
			} else {
				Log.v(TAG, "Bluetooth supported and enabled");
				// discover new Bluetooth devices
				discoverBluetoothDevices();

				// find devices that have been paired
				getBondedDevices();
			}
		}
	}
	private void setActivitySize()
	{
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		
		Point size = new Point();
		display.getSize(size);
		
		int width = (int) (size.x * 0.8);

		int height = (int) (size.y * 0.5);  

		getWindow().getAttributes().width = width;

		getWindow().getAttributes().height = height;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_TO_ENABLE_BT) {
			discoverBluetoothDevices();
			getBondedDevices();
			return;
		}
	}

	private void discoverBluetoothDevices() {
		// register a BroadcastReceiver for the ACTION_FOUND Intent
		// to receive info about each device discovered.
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		mBluetoothAdapter.startDiscovery();
	}

	// for each device discovered, the broadcast info is received
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Log.v(TAG, "BroadcastReceiver on Receive - " + device.getName()
						+ ": " + device.getAddress());
				String name = device.getName();

				// found another Android device of mine and start communication
				if (name != null
						&& name.equalsIgnoreCase(CBT_SERVER_DEVICE_NAME)) {
					new ConnectThread(device).start();
				}
			}
		}
	};
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (sock.isConnected()) {
			try {
				sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// bonded devices are those that have already paired with the current device
	// sometime in the past (and have not been unpaired)
	private void getBondedDevices() {
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				Log.v(TAG, "bonded device - " + device.getName() + ": "
						+ device.getAddress());
				if (device.getName().equalsIgnoreCase(CBT_SERVER_DEVICE_NAME)) {
					Log.d(TAG, CBT_SERVER_DEVICE_NAME);
					new ConnectThread(device).start();
					break;
				}
			}
		} else {
			Toast.makeText(ImageTransferHelper.this, "No bonded devices",
					Toast.LENGTH_LONG).show();
		}
	}

	private class ConnectThread extends Thread {
		int bytesRead;
		int total;
		private final BluetoothSocket mmSocket;

		public ConnectThread(BluetoothDevice device) {
			BluetoothSocket tmp = null;
			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				Log.v(TAG, "before createRfcommSocketToServiceRecord");
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
				Log.v(TAG, "after createRfcommSocketToServiceRecord");
			} catch (IOException e) {
				Log.v(TAG,
						" createRfcommSocketToServiceRecord exception: "
								+ e.getMessage());
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			mBluetoothAdapter.cancelDiscovery();
			Log.d(TAG, "ready to connect");
			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();

			} catch (IOException e) {
				Log.v(TAG, e.getMessage());
				try {
					mmSocket.close();
				} catch (IOException closeException) {
				}
				return;
			}
			Log.d(TAG, "success to connect");
			sock = mmSocket;
			manageConnectedSocket(sock);
		}

		private void manageConnectedSocket(BluetoothSocket socket) {
			byte[] buffer = new byte[512];

			OutputStream mOutStream = null;
			try {
				mOutStream = socket.getOutputStream();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Log.d(TAG, "cant get stream");
				e1.printStackTrace();
			}
			Bitmap bm = TakePictureCallback.getBitmap(uri);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(CompressFormat.JPEG, 100, baos);
			byte[] imageByte = baos.toByteArray();
			fileTransferStateBar.setMax(imageByte.length);
			int len=0;
			final int size = 512;
			byte[] sendByte = new byte[size];
			ByteArrayInputStream bais = new ByteArrayInputStream(imageByte);
			Log.d(TAG, "bais :"+bais	);
			try {
				bytesRead = 0;
				byte[] flagByte=new byte[1];
				flagByte[0]=0;
				mOutStream.write(flagByte);
				mOutStream.flush();
				Log.d(TAG, "aaa");
				if(comment == null)
					comment="happ new year";
				byte[] commentByte=comment.getBytes();
				Log.d(TAG, "comment :"+comment	);
				mOutStream.write(commentByte);
				mOutStream.flush();
				
				flagByte[0]=1;
				
				mOutStream.write(flagByte);
				mOutStream.flush();
				
				while ((len = bais.read(sendByte)) != -1) {
					if (len < 512) {
						byte[] EOF = new byte[len];
						for (int eof = 0; eof < EOF.length; eof++) {
							EOF[eof] = sendByte[eof];
						}
						 mOutStream.write(EOF);

					} else {
						mOutStream.write(sendByte);
					}
					progressBarHandle.sendEmptyMessage(len);
					 mOutStream.flush();
				}
				flagByte[0]=2;
				
				mOutStream.write(flagByte);
				mOutStream.flush();
				mOutStream.close();

			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
			runOnUiThread(new Runnable() {
				public void run() {
					TakePictureCallback.resetBitmap(uri);
					audio.playSoundEffect(Sounds.DISMISSED);
					ImageTransferHelper.this.setResult(RESULT_OK);
					ImageTransferHelper.this.finish();
				}
			});
		}
	}
	
	Handler progressBarHandle=new Handler()
	{
		private int size;
		public void init()
		{
			size=0;
		}
		public void handleMessage(android.os.Message msg) {
			size+=msg.what;
			fileTransferStateBar.setProgress(size);			
		}
	};
}