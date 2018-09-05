package kr.kymedia.karaoke.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Util {

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static short byteToShortLE(byte b1, byte b2) {
		return (short) (b1 & 0xFF | ((b2 & 0xFF) << 8));
	}

	/**
	 * Read audio data from input file (mono)
	 * 
	 * @param dst
	 *          mono audio data output buffer
	 * @param numSamples
	 *          number of samples to read
	 * 
	 * @return number of samples read
	 * 
	 * @throws IOException
	 *           if file I/O error occurs
	 */
	public static int pcmByteToShort(byte[] src, short[] dst, int bytesRead) throws IOException {
		int index = 0;

		for (int i = 0; i < bytesRead; i += 2) {
			dst[index] = byteToShortLE(src[i], src[i + 1]);
			index++;
		}

		return index;
	}

	/**
	 * Read audio data from input file (stereo)
	 * 
	 * @param left
	 *          left channel audio output buffer
	 * @param right
	 *          right channel audio output buffer
	 * @param numSamples
	 *          number of samples to read
	 * 
	 * @return number of samples read
	 * 
	 * @throws IOException
	 *           if file I/O error occurs
	 */
	public static int pcmByteToShort(byte[] src, short[] left, short[] right, int bytesRead)
			throws IOException {
		int index = 0;

		for (int i = 0; i < bytesRead; i += 2) {
			short val = byteToShortLE(src[0], src[i + 1]);
			if (i % 4 == 0) {
				left[index] = val;
			} else {
				right[index] = val;
				index++;
			}
		}

		return index;
	}

	public static short readShort(byte[] data, int offset) {
		return (short) (((data[offset] << 8)) | ((data[offset + 1] & 0xff)));
	}

	/**
	 * byte[] 를 short[] 로 변환시켜주는 메서드
	 * 
	 * @param args
	 *          변환하고자 하는 byte[] 값
	 * @return 변환된 short[] 값
	 */
	public static short[] byteToShort(byte args[]) {
		short result[] = null;
		try {
			DataInputStream din = new DataInputStream(new ByteArrayInputStream(args));
			result = new short[args.length / 2];
			for (int i = 0; i < args.length / 2; i++) {
				result[i] = din.readShort();
			}
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * short[] 를 byte[] 로 변환시켜주는 메서드
	 * 
	 * @param args
	 *          변환하고자 하는 short[] 값
	 * @return 변환된 byte[] 값
	 */
	public static byte[] shortToByteArray(short s) {
		return new byte[] { (byte) ((s & 0xFF00) >> 8), (byte) (s & 0x00FF) };
	}

	public static byte[] shortToByte(short args[]) {
		byte result[] = null;
		try {
			result = new byte[args.length * 2];
			int idx = 0;
			for (int i = 0; i < args.length; i++) {
				result[idx] = shortToByteArray(args[i])[0];
				result[idx + 1] = shortToByteArray(args[i])[1];
				idx += 2;
			}
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * byte[] 를 int[] 로 변환시켜주는 메서드
	 * 
	 * @param args
	 *          변환하고자 하는 byte[] 값
	 * @return 변환된 int[] 값
	 */
	public static int[] byteToInt(byte args[]) {
		int result[] = null;
		try {
			DataInputStream din = new DataInputStream(new ByteArrayInputStream(args));
			result = new int[args.length / 4];
			for (int i = 0; i < args.length / 4; i++) {
				result[i] = din.readInt();
			}
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * int 를 byte[]로 변환시켜주는 메서드
	 * 
	 * @param args
	 *          변환하고자 하는 int 값
	 * @return 변환된 byte[]값
	 */
	public static byte[] intToByte(int args) {
		ByteArrayOutputStream bOut = null;
		DataOutputStream dOut = null;
		try {
			bOut = new ByteArrayOutputStream();
			dOut = new DataOutputStream(bOut);
			dOut.writeInt(args);
		} catch (Exception e) {
		}
		return bOut.toByteArray();
	}

	/**
	 * int[] 을 byte[]로 변환시켜주는 메서드
	 * 
	 * @param args
	 *          변환하고자 하는 int[] 값
	 * @return 변환된 byte[]값
	 */
	public static byte[] intToByte(int args[]) {
		ByteArrayOutputStream bOut = null;
		DataOutputStream dOut = null;
		try {
			bOut = new ByteArrayOutputStream();
			dOut = new DataOutputStream(bOut);
			for (int i = 0; i < args.length; i++) {
				dOut.writeInt(args[i]);
			}
		} catch (Exception e) {
		}
		return bOut.toByteArray();
	}

	/**
	 * UTF문자를 byte[]로 변환시켜주는 메서드
	 * 
	 * @param args
	 *          변환하고자하는 UTF값
	 * @return 변환된 byte[]값
	 */
	public static byte[] utfToByte(String args) {
		byte buffer[] = null;
		ByteArrayOutputStream bOut = null;
		DataOutputStream dOut = null;
		try {
			bOut = new ByteArrayOutputStream();
			dOut = new DataOutputStream(bOut);
			dOut.writeUTF(args);
		} catch (Exception e) {
		}
		buffer = bOut.toByteArray();
		return buffer;
	}

	/**
	 * byte[]를 UTF문자로 변환시켜주는 메서드
	 * 
	 * @param args
	 *          변환하고자 하는 byte[]값
	 * @return 변환된 UTF문자값
	 */
	public static String byteToUtf(byte args[]) {
		String result = null;
		DataInputStream dIn = new DataInputStream(new ByteArrayInputStream(args));
		try {
			result = dIn.readUTF();
		} catch (IOException e) {
		}
		return result;
	}

	public static byte[] getLittleEndian(int v) {
		byte[] buf = new byte[4];
		buf[3] = (byte) ((v >>> 24) & 0xff);
		buf[2] = (byte) ((v >>> 16) & 0xff);
		buf[1] = (byte) ((v >>> 8) & 0xff);
		buf[0] = (byte) ((v >>> 0) & 0xff);

		return buf;
	}

	public static int getBigEndian(byte[] v) {
		int[] arr = new int[4];
		for (int i = 0; i < 4; i++) {
			arr[i] = (int) (v[3 - i] & 0xff);
		}

		return ((arr[0] << 24) + (arr[1] << 16) + (arr[2] << 8) + (arr[3] << 0));
	}

	public static int swap(int i) {
		int byte0 = i & 0xff;
		int byte1 = (i >> 8) & 0xff;
		int byte2 = (i >> 16) & 0xff;
		int byte3 = (i >> 24) & 0xff;

		return (byte0 << 24) | (byte1 << 16) | (byte2 << 8) | byte3;
	}

	public static String byteToString(byte args[]) {
		String ret = null;

		try {
			ret = new String(args, "KSC5601");
		} catch (UnsupportedEncodingException e) {
		}

		return ret.trim();
	}

	/**
	 * This method convets dp unit to equivalent device specific value in pixels.
	 * 
	 * @param dp
	 *          A value in dp(Device independent pixels) unit. Which we need to convert into pixels
	 * @param context
	 *          Context to get resources and device specific display metrics
	 * @return A float value to represent Pixels equivalent to dp according to device
	 */
	// //dp to px:
	// android.util.DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
	// return (int)((dp * displayMetrics.density) + 0.5);
	public static float dp2px(Context context, float dp) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		// float px = dp * (metrics.densityDpi / 160f);
		float px = (dp * metrics.density) + 0.5f;
		return px;
	}

	public static float dp2dp(Context context, float dp) {
		return dp;
	}

	/**
	 * This method converts device specific pixels to device independent pixels.
	 * 
	 * @param px
	 *          A value in px (pixels) unit. Which we need to convert into db
	 * @param context
	 *          Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	// //px to dp:
	// android.util.DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
	// return (int) ((px/displayMetrics.density)+0.5);
	public static float px2dp(Context context, float px) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		// float dp = px / (metrics.densityDpi / 160f);
		float dp = (px / metrics.density) + 0.5f;
		return dp;
	}

	public static float px2px(Context context, float px) {
		return px;
	}

	public static int getWidthPx(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();

		int px = metrics.widthPixels;
		return px;
	}

	public static int getHeightPx(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();

		int px = metrics.heightPixels;
		return px;
	}

	public static float getWidthDp(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();

		int px = metrics.widthPixels;
		float dp = (px / metrics.density) + 0.5f;
		return dp;
	}

	public static float getHeightDp(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();

		int px = metrics.heightPixels;
		float dp = (px / metrics.density) + 0.5f;
		return dp;
	}
}
