package com.cn.zhihengchuang.walkbank.util;

/**
 * @author longke
 * 
 * 数据转化工具类
 */
public class FormatTransfer {

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}

		String newHexStr = null;
		int length = hexString.length();
		if (length % 2 != 0)
			newHexStr = '0' + hexString;
		else
			newHexStr = hexString;

		String upper = newHexStr.toUpperCase();
		int newLength = upper.length() / 2;
		char[] hexChars = upper.toCharArray();
		byte[] d = new byte[newLength];
		for (int i = 0; i < newLength; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}
	
	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToDWordBytes(String hexString) {
		byte[] result = hexStringToBytes(hexString);
		byte[] datas = new byte[4];
		int wl = result.length;
		if(wl<4)
		{
			for(int i=0;i<4-wl;i++)
			{
				datas[i] = 0x00;
			}
			int index = 0;
			for(int i=4-wl;i<4;i++,index++)
				datas[i] = result[index];
			return datas;
		}
		else
		{
			return result;
		}
	}
	
	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToWordBytes(String hexString) {
		byte[] result = hexStringToBytes(hexString);
		byte[] datas = new byte[2];
		int wl = result.length;
		if(wl==1)
		{
			datas[0] = 0x00;
			datas[1] = result[0];
			return datas;
		}
		else
		{
			return result;
		}
		 
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	public static byte charToByte(char c) {
		int index = "0123456789ABCDEF".indexOf(c);
		return (byte) index;
	}
	
	public static String byte2bits(byte b) {
		int z = b;
		z |= 256;
		String str = Integer.toBinaryString(z);
		int len = str.length();
		return str.substring(len - 8, len);
	}

	public static String toHexString(byte b) {
		return Integer.toHexString(b & 0xFF);
	}

	/**
	 * ��intתΪ���ֽ���ǰ�����ֽ��ں��byte����
	 * 
	 * @param n
	 *            int
	 * @return byte[]
	 */
	public static byte[] toLH(int n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		return b;
	}

	/**
	 * ��intתΪ���ֽ���ǰ�����ֽ��ں��byte����
	 * 
	 * @param n
	 *            int
	 * @return byte[]
	 */
	public static byte[] toHH(int n) {
		byte[] b = new byte[4];
		b[3] = (byte) (n & 0xff);
		b[2] = (byte) (n >> 8 & 0xff);
		b[1] = (byte) (n >> 16 & 0xff);
		b[0] = (byte) (n >> 24 & 0xff);
		return b;
	}

	/**
	 * ����intתΪ���ֽ���ǰ�����ֽ��ں��byte����
	 * */
	public static byte[] toHH2(int number) {
		int temp = number;
		byte[] b = new byte[4];
		for (int i = b.length - 1; i > -1; i--) {
			b[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}

	/**
	 * ��shortתΪ���ֽ���ǰ�����ֽ��ں��byte����
	 * 
	 * @param n
	 *            short
	 * @return byte[]
	 */
	public static byte[] toLH(short n) {
		byte[] b = new byte[2];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		return b;
	}

	/**
	 * ��shortתΪ���ֽ���ǰ�����ֽ��ں��byte����
	 * 
	 * @param n
	 *            short
	 * @return byte[]
	 */
	public static byte[] toHH(short n) {
		byte[] b = new byte[2];
		b[1] = (byte) (n & 0xff);
		b[0] = (byte) (n >> 8 & 0xff);
		return b;
	}

	public static byte[] IntToByteArray(int i) {
		byte[] abyte0 = new byte[4];
		abyte0[3] = (byte) (0xff & i);
		abyte0[2] = (byte) ((0xff00 & i) >> 8);
		abyte0[1] = (byte) ((0xff0000 & i) >> 16);
		abyte0[0] = (byte) ((0xff000000 & i) >> 24);
		return abyte0;
	}

	/**
	 * ��floatתΪ���ֽ���ǰ�����ֽ��ں��byte����
	 */
	public static byte[] toLH(float f) {
		return toLH(Float.floatToRawIntBits(f));
	}

	/**
	 * ��floatתΪ���ֽ���ǰ�����ֽ��ں��byte����
	 */
	public static byte[] toHH(float f) {
		return toHH(Float.floatToRawIntBits(f));
	}

	/**
	 * ��StringתΪbyte����
	 */
	public static byte[] stringToBytes(String s, int length) {
		while (s.getBytes().length < length) {
			s += " ";
		}
		return s.getBytes();
	}

	/**
	 * ���ֽ�����ת��ΪString
	 * 
	 * @param b
	 *            byte[]
	 * @return String
	 */
	public static String bytesToString(byte[] b) {
		StringBuffer result = new StringBuffer("");
		int length = b.length;
		for (int i = 0; i < length; i++) {
			result.append((char) (b[i] & 0xff));
		}
		return result.toString();
	}

	/**
	 * ���ַ���ת��Ϊbyte����
	 * 
	 * @param s
	 *            String
	 * @return byte[]
	 */
	public static byte[] stringToBytes(String s) {
		return s.getBytes();
	}

	/**
	 * �����ֽ�����ת��Ϊint
	 * 
	 * @param b
	 *            byte[]
	 * @return int
	 */
	public static int hBytesToInt(byte[] b) {
		int s = 0;
		for (int i = 0; i < 3; i++) {
			if (b[i] >= 0) {
				s = s + b[i];
			} else {
				s = s + 256 + b[i];
			}
			s = s * 256;
		}
		if (b[3] >= 0) {
			s = s + b[3];
		} else {
			s = s + 256 + b[3];
		}
		return s;
	}

	/**
	 * �����ֽ�����ת��Ϊint
	 * 
	 * @param b
	 *            byte[]
	 * @return int
	 */
	public static int lBytesToInt(byte[] b) {
		int s = 0;
		for (int i = 0; i < 3; i++) {
			if (b[3 - i] >= 0) {
				s = s + b[3 - i];
			} else {
				s = s + 256 + b[3 - i];
			}
			s = s * 256;
		}
		if (b[0] >= 0) {
			s = s + b[0];
		} else {
			s = s + 256 + b[0];
		}
		return s;
	}

	/**
	 * ���ֽ����鵽short��ת��
	 * 
	 * @param b
	 *            byte[]
	 * @return short
	 */
	public static short hBytesToShort(byte[] b) {
		int s = 0;
		if (b[0] >= 0) {
			s = s + b[0];
		} else {
			s = s + 256 + b[0];
		}
		s = s * 256;
		if (b[1] >= 0) {
			s = s + b[1];
		} else {
			s = s + 256 + b[1];
		}
		short result = (short) s;
		return result;
	}

	/**
	 * ���ֽ����鵽short��ת��
	 * 
	 * @param b
	 *            byte[]
	 * @return short
	 */
	public static short lBytesToShort(byte[] b) {
		int s = 0;
		if (b[1] >= 0) {
			s = s + b[1];
		} else {
			s = s + 256 + b[1];
		}
		s = s * 256;
		if (b[0] >= 0) {
			s = s + b[0];
		} else {
			s = s + 256 + b[0];
		}
		short result = (short) s;
		return result;
	}

	/**
	 * ���ֽ�����ת��Ϊfloat
	 * 
	 * @param b
	 *            byte[]
	 * @return float
	 */
	public static float hBytesToFloat(byte[] b) {
		int i = 0;
		Float F = new Float(0.0);
		i = ((((b[0] & 0xff) << 8 | (b[1] & 0xff)) << 8) | (b[2] & 0xff)) << 8
				| (b[3] & 0xff);
		return F.intBitsToFloat(i);
	}

	/**
	 * ���ֽ�����ת��Ϊfloat
	 * 
	 * @param b
	 *            byte[]
	 * @return float
	 */
	public static float lBytesToFloat(byte[] b) {
		int i = 0;
		Float F = new Float(0.0);
		i = ((((b[3] & 0xff) << 8 | (b[2] & 0xff)) << 8) | (b[1] & 0xff)) << 8
				| (b[0] & 0xff);
		return F.intBitsToFloat(i);
	}

	/**
	 * ��byte�����е�Ԫ�ص�������
	 */
	public static byte[] bytesReverseOrder(byte[] b) {
		int length = b.length;
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[length - i - 1] = b[i];
		}
		return result;
	}

	/**
	 * ��ӡbyte����
	 */
	public static void printBytes(byte[] bb) {
		int length = bb.length;
		for (int i = 0; i < length; i++) {
			System.out.print(bb + " ");
		}
		System.out.println("");
	}

	public static void logBytes(byte[] bb) {
		int length = bb.length;
		String ut = "";
		for (int i = 0; i < length; i++) {
			ut = ut + bb[i] + " ";
		}
	}

	/**
	 * ��int���͵�ֵת��Ϊ�ֽ���ߵ�������Ӧ��intֵ
	 * 
	 * @param i
	 *            int
	 * @return int
	 */
	public static int reverseInt(int i) {
		int result = FormatTransfer.hBytesToInt(FormatTransfer.toLH(i));
		return result;
	}

	/**
	 * ��short���͵�ֵת��Ϊ�ֽ���ߵ�������Ӧ��shortֵ
	 * 
	 * @param s
	 *            short
	 * @return short
	 */
	public static short reverseShort(short s) {
		short result = FormatTransfer.hBytesToShort(FormatTransfer.toLH(s));
		return result;
	}

	/**
	 * ��float���͵�ֵת��Ϊ�ֽ���ߵ�������Ӧ��floatֵ
	 * 
	 * @param f
	 *            float
	 * @return float
	 */
	public static float reverseFloat(float f) {
		float result = FormatTransfer.hBytesToFloat(FormatTransfer.toLH(f));
		return result;
	}
	
	/**
	 * 
	 */
	public static short byteToShort(byte b){
		short s = (short) (b&0x00ff);
		return s;
	}
}
