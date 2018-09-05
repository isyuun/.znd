package kr.kymedia.karaoke.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SongUtil {
	public static byte[] ShortToByte(short[] input)
	{
		int short_index, byte_index;
		int iterations = input.length;

		byte[] buffer = new byte[input.length * 2];

		short_index = byte_index = 0;

		for (/* NOP */; short_index != iterations; /* NOP */)
		{
			buffer[byte_index] = (byte) (input[short_index] & 0x00FF);
			buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);

			++short_index;
			byte_index += 2;
		}

		return buffer;
	}

	public static int[] byteToInt(byte args[]) {
		DataInputStream din = new DataInputStream(new ByteArrayInputStream(args));
		int[] result = new int[args.length / 4];

		try {
			for (int i = 0; i < args.length / 4; i++) {
				result[i] = din.readInt();
			}
		} catch (IOException e) {
			return null;
		}

		return result;
	}

	public static byte[] intToByte(int args) {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		DataOutputStream dOut = new DataOutputStream(bOut);
		try {
			dOut.writeInt(args);
		} catch (IOException e) {
			return null;
		}

		return bOut.toByteArray();
	}

	public static byte[] intToByte(int args[]) {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		DataOutputStream dOut = new DataOutputStream(bOut);
		try {
			for (int i = 0; i < args.length; i++) {
				dOut.writeInt(args[i]);
			}
		} catch (IOException e) {
			return null;
		}

		return bOut.toByteArray();
	}

	public static byte[] utfToByte(String args) {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		DataOutputStream dOut = new DataOutputStream(bOut);
		try {
			dOut.writeUTF(args);
		} catch (IOException e) {
			return null;
		}

		return bOut.toByteArray();
	}

	public static String byteToUtf(byte args[]) {
		String result = null;
		DataInputStream dIn = new DataInputStream(new ByteArrayInputStream(args));
		try {
			result = dIn.readUTF();
		} catch (IOException e) {
			return null;
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

	public static String byteToString(byte args[], String charset) {
		String ret = null;

		try {
			ret = new String(args, charset);
		} catch (UnsupportedEncodingException e) {
		}

		return ret.trim();
	}

	public static int Byte2Int(byte[] data, int offset) {
		int result = 0;

		for (int i = 0; i < 4; i++) {
			result += ((data[i + offset] & 0xff) << ((3 - i) * 8));
		}

		return swap(result);
	}

	public static long makeEncryptionKey(String str) {
		long key = 0x00000000;
		int length = str.length();
		long[] keyList = new long[length];

		byte[] temp = new byte[length];

		try {
			temp = str.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			return 0;
		}

		for (int i = 0; i < length; i++) {
			keyList[i / 4] += (long) temp[i] << (i * 8);
		}

		for (int i = 0; i <= length / 4; i++) {
			key = key ^ keyList[i];
		}

		return key;
	}

	public static String makeEncryption(String key, String data) {
		StringBuilder str = new StringBuilder();
		long keyValue = makeEncryptionKey(key);
		int length = data.length();

		int i = 0;
		int cnt = 0;
		byte[] temp = new byte[length];

		try {
			temp = data.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		while (cnt < length) {
			str.append((char) (byte) (temp[cnt] ^ ((long) keyValue >> i * 8)));
			i++;
			i %= 4;

			if (i == 0)
				keyValue++;

			cnt++;
		}

		return str.toString();
	}

	public static String makeDecryption(String key, String data) {
		StringBuilder str = new StringBuilder();
		long keyValue = makeEncryptionKey(key);
		int length = data.length();

		int i = 0;
		int cnt = 0;
		byte[] temp = new byte[length];

		try {
			temp = data.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		while (cnt < length) {
			str.append((char) (byte) (temp[cnt] ^ ((long) keyValue >> i * 8)));
			i++;
			i %= 4;

			if (i == 0)
				keyValue++;

			cnt++;
		}

		return str.toString();
	}
}
