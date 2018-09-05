package kr.keumyoung.karaoke.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SongUtil {
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
			arr[i] = (v[3 - i] & 0xff);
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
}
