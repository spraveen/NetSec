package com.secure.nfcsecure;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.secure.nfcsecure.util.SystemUiHider;
//ara

//ara
/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */

	private static int RESULT_LOAD_IMAGE = 1;

	NfcAdapter mNfcAdapter;

	public final static String EXTRA_MESSAGE = "com.secure.nfcsecure.MESSAGE";

	private String sendPicturePath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		// Register callback to set NDEF message
		// mNfcAdapter.setNdefPushMessageCallback(null, this, this);
		// Register callback to listen for message-sent success
		// mNfcAdapter.setOnNdefPushCompleteCallback(null, this, this);
		System.out.println(mNfcAdapter);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.

		// TextView textView = (TextView) findViewById(R.id.fullscreen_content);
		if (mNfcAdapter != null)
			Toast.makeText(this, "NFC is present", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, "NFC is not present", Toast.LENGTH_SHORT)
					.show();

		Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
		buttonLoadImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

	}

	// ara
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			ImageView imageView = (ImageView) findViewById(R.id.imageView1);
			imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			// imageView.setImageResource(R.id.imageView1);
			sendPicturePath = new String(picturePath);

			// imageView.setImageURI(Uri.fromFile(picturePath));
			// @SuppressWarnings("deprecation")
			// Drawable drawable =new
			// BitmapDrawable(BitmapFactory.decodeFile(picturePath));
			// imageView.setImageDrawable(drawable);
			Log.v("com.secure.nfcsecure", "testing");
		}

	}

	// ara
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {

		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	public static void writeFullFile(String filename, byte[] bytes)
			throws Exception {
		FileOutputStream fos = new FileOutputStream(new File(filename));
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bos.write(bytes);
		bos.close();
	}

	@SuppressLint("NewApi")
	public void sendMessage(View view) throws Exception {
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String code = editText.getText().toString();

		MyCryptography mycrypto;
		mycrypto = new MyCryptography(code.toCharArray());
		// System.out.print(result);

		Intent intent = getIntent();

		Toast.makeText(this, "encryption code: " + code, Toast.LENGTH_SHORT)
				.show();

		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		// TextView textView = (TextView) findViewById(R.id.fullscreen_content);
		// textView.setText(message);
		intent.setComponent(new ComponentName("com.secure.nfcsecure",
				"com.secure.nfcsecure.MainActivity"));
		// ara

		Bitmap bmp = BitmapFactory.decodeFile(sendPicturePath);
		// Bitmap bmp = BitmapFactory.decodeResource(getResources(),
		// R.drawable.ic_launcher);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		// byte[] byteArray = stream.toByteArray();

//		String path = Environment.getExternalStorageDirectory().toString();
//		File filename = new File(path, "temp.txt");
//		FileOutputStream out = new FileOutputStream(filename);

		byte[] encryptByte = stream.toByteArray();
//		byte[] encryptByte = "Test\n".getBytes();
		byte[] decryptByte = null;

		byte[] byteArray = mycrypto.encrypt(encryptByte);
		try {
			decryptByte = mycrypto.decrypt(byteArray);
		} catch (Exception e) {
//			out.write(e.getMessage().getBytes());
//			out.close();
			Toast.makeText(this, "Exception decryption", Toast.LENGTH_SHORT)
					.show();
			return;
		}
//		Toast.makeText(this, "Strings failed to match", Toast.LENGTH_SHORT)
//				.show();
//		Toast.makeText(this, "Strings enc:" + encryptByte, Toast.LENGTH_LONG)
//				.show();
//		Toast.makeText(this, "Strings enc:" + decryptByte, Toast.LENGTH_LONG)
//				.show();
//
//		out.write(encryptByte);
//		
//		out.write("1.\n".getBytes());
//
//		out.write("2.\n".getBytes());
//		
//		out.write(decryptByte);
//		out.close();

		// NdefRecord picRecord = createMimeRecord("image/jpeg", byteArray);
		// NdefMessage msg = new NdefMessage(NdefRecord.createMime("image/jpeg",
		// byteArray));

		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"application/vnd.nfcsecure".getBytes(), new byte[] {},
				byteArray);
		NdefMessage msg = new NdefMessage(new NdefRecord[] { textRecord });
		// NdefRecord textRecord = new
		// NdefRecord(NdefRecord.TNF_MIME_MEDIA,"application/vnd.facebook.places".getBytes(),
		// new byte[] {}, byteArray);
		// NdefMessage msg=new NdefMessage(new NdefRecord[] { textRecord });
		// textView.setText(msg.toString());
		// Toast.makeText(this,"Created ndefmessage"
		// ,Toast.LENGTH_SHORT).show();
		/*
		 * 		*/

		// /Log.w("System.out",msg.toString() + " nfc "+
		// msg.toString().length());
		// ara
		intent.putExtra("NDEF", msg.toString());
		// Register callback to set NDEF message
		mNfcAdapter.setNdefPushMessage(msg, this, this);
		// Register callback to listen for message-sent success
		mNfcAdapter.setOnNdefPushCompleteCallback(null, this, this);
		Toast.makeText(this, "Ndef Message Ready For Dispatch",
				Toast.LENGTH_SHORT).show();
		// startActivity(intent);
	}

	// http://www.packtpub.com/article/new-connectivity-apis-android-beam
	/*
	 * public NdefMessage createNdefMessage(NfcEvent arg0) { Bitmap icon =
	 * BitmapFactory.decodeResource(this.getResources(), R.id.imageView1);
	 * ByteArrayOutputStream stream = new ByteArrayOutputStream();
	 * icon.compress(Bitmap.CompressFormat.PNG, 100, stream); byte[] byteArray =
	 * stream.toByteArray(); NdefMessage msg = new NdefMessage(new NdefRecord[]
	 * { //createMimeRecord("application/com.chapter9", byteArray),
	 * NdefRecord.createApplicationRecord("com.chapter9"); }); return msg; }
	 */
	public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("USASCII"));
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				mimeBytes, new byte[0], payload);
		return mimeRecord;
	}

	private void handleIntent(Intent intent) {
		// TODO: handle Intent
		handleViewIntent(intent);
	}

	private void handleViewIntent(Intent intent) {
		Log.v("com.secure.nfcsecure", intent.toString());
	}

	@Override
	public void onResume() {
		super.onResume();
		PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(
				this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this,
				intent, null, null);

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (NfcAdapter.getDefaultAdapter(this) != null)
			NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
	}

	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		setIntent(intent);

		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(intent);
		} else {
			System.err.println("onNewIntent detection...");
			Log.w("nfcsecure", "onNewIntent detection...");
			handleIntent(intent);

		}

	}

	void processIntent(Intent intent) {
		MyCryptography mycrypto;
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String code = editText.getText().toString();

		Parcelable[] msgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage[] nmsgs = new NdefMessage[msgs.length];
		Toast.makeText(this, "Messages received" + msgs.length,
				Toast.LENGTH_LONG).show();
		for (int i = 0; i < msgs.length; i++) {
			nmsgs[i] = (NdefMessage) msgs[i];
			Log.v("com.secure.nfcsecure", msgs[i].toString());
		}
		try {
			mycrypto = new MyCryptography(code.toCharArray());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		NdefRecord[] rec = nmsgs[0].getRecords();
		// byte[] infoRecord = rec[0].getPayload();
		byte[] pictureRecord = rec[0].getPayload();
		Toast.makeText(this, "Records received! Size:" + rec.length,
				Toast.LENGTH_SHORT).show();
		Intent mintent = getIntent();
		mintent.setComponent(new ComponentName("com.secure.nfcsecure",
				"com.secure.nfcsecure.MainActivity"));
		mintent.putExtra("NDEF", pictureRecord.toString());

		/*
		 * Toast.makeText(this, "Info received! Size:" + infoRecord.toString(),
		 * Toast.LENGTH_LONG).show();
		 * 
		 * Toast.makeText(this, "Pic received! Size:" +
		 * pictureRecord.toString(), Toast.LENGTH_LONG).show();
		 */

		// byte[] payload = nmsgs[0].getRecords()[0].getPayload();
		byte[] payload = pictureRecord;

		if (payload != null) {
			ImageView imgViewer = (ImageView) findViewById(R.id.imageView1);
			if (nmsgs[0].getRecords()[0].getTnf() == 2) {
				Toast.makeText(this,
						"New picture received! Size:" + payload.length,
						Toast.LENGTH_SHORT).show();

				// Bitmap bm = BitmapFactory.decodeByteArray(payload, 0,
				// payload.length);
				Bitmap bm;
				try {
					// String payloadString = mycrypto.decrypt(pictureRecord);
					byte[] data = mycrypto.decrypt(pictureRecord);
					payload = new byte[data.length];
					payload = data;
					// byte[] data = payloadString.getBytes();
					bm = BitmapFactory.decodeByteArray(data, 0, data.length);
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(this, "Decryption Error!!",
							Toast.LENGTH_LONG).show();

					return;
				}
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);

				imgViewer.setMinimumHeight(dm.heightPixels);
				imgViewer.setMinimumWidth(dm.widthPixels);

				Toast.makeText(
						this,
						"New picture Dimensions! height:" + dm.heightPixels
								+ " width: " + dm.widthPixels,
						Toast.LENGTH_SHORT).show();

				imgViewer.setImageBitmap(bm);

				try {
					String path = Environment.getExternalStorageDirectory()
							.toString();
					File filename = new File(path, "temp.jpeg");
					FileOutputStream out = new FileOutputStream(filename);
					// bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.write(payload);

					Toast.makeText(this, "Picture Saved!!",
							Toast.LENGTH_SHORT).show();

					out.flush();
					out.close();
				} catch (Exception e) {
					Log.e("nfcsecure", e.toString());
					e.printStackTrace();
				}

				// sendBroadcast(new Intent(
				// Intent.ACTION_MEDIA_MOUNTED,
				// Uri.parse("file://" +
				// Environment.getExternalStorageDirectory())));
				// ll1.addView(imgViewer);

				/*
				 * ByteArrayInputStream imageStream = new ByteArrayInputStream(
				 * payload); Bitmap b = BitmapFactory.decodeStream(imageStream);
				 * iv.setImageBitmap(b); // saveImage(b);
				 * 
				 * String sizeIV = "Size = " + b.getByteCount() + " Bytes";
				 * SizeIVTV.setText(sizeIV); // ll1.addView(SizeIVTV);
				 * 
				 * String heightIV = "Height = " + b.getHeight() + " Pixels";
				 * HeightIVTV.setText(heightIV); // ll1.addView(HeightIVTV);
				 * pageTitle.setText("New picture received!\n\n"); String
				 * widthIV = "Width = " + b.getWidth() + " Pixels";
				 * WidthIVTV.setText(widthIV); // ll1.addView(WidthIVTV); //
				 * ll1.addView(iv);
				 */
			}
		}
		// startActivity(mintent);
	}
	// process the msgs array
}
