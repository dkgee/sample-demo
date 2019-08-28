package com.example.demo.util;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public class MD5Util {

	public static String digest(String rawString) {
		if(rawString == null){
			return "";
		}
		try {
			return compute(new String(rawString.getBytes("UTF8"), "latin1"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Computes the MD5 fingerprint of a string.
	 *
	 * @return the MD5 digest of the input <code>String</code>
	 */
	private static String compute(String inStr) throws Exception {
		// convert input String to a char[]
		// convert that char[] to byte[]
		// get the md5 digest as byte[]
		// bit-wise AND that byte[] with 0xff
		// prepend "0" to the output StringBuffer to make sure that we don't end
		// up with
		// something like "e21ff" instead of "e201ff"
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		return toHexString(md5Bytes);
	}

	private static String toHexString(byte[] bytes) {
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			int val = ((int) bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getFileMD5(File file)throws Exception
	{
		MessageDigest messagedigest = MessageDigest.getInstance("MD5");
		FileInputStream in = new FileInputStream(file);
		FileChannel ch = in.getChannel();
		MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		messagedigest.update(byteBuffer);
		String result = bufferToHex(messagedigest.digest());
		ch.close();
		in.close();
		return result;
	}

	public static String getFileMD5(byte[] bytes)throws Exception
	{
		MessageDigest messagedigest = MessageDigest.getInstance("MD5");
		messagedigest.update(bytes);
		return bufferToHex(messagedigest.digest());
	}

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		char c0 = hexDigits[(bt & 0xf0) >> 4];
		char c1 = hexDigits[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	public static void main(String[] args) throws Exception {
//		String file = "D:\\file\\jyg.gif";
		//28552d21adc695cb506ff48e5df30d92
//		System.out.println(getFileMD5(new File(file)));

//		String file2 = "D:\\file\\jygdl.gif";
		//9210bc2159d00fffb641548964f86c8e
		String file2 = "D:\\file\\1_ycagri.jpg";
		long st = System.currentTimeMillis();
		String md5 = getFileMD5(new File(file2));
		long et = System.currentTimeMillis();
		System.out.println(md5 + "|" + (et - st) + "ms");
	}

}
