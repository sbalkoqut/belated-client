package qut.belated.helpers;

public class BitBuffer {
	public BitBuffer(int wordLength)
	{
		this.wordLength = wordLength;
		this.wordMask = getMask(wordLength);
		clear();
	}
	
	int buffer;
	int wordLength;
	int wordMask;
	int bitsUsed;
	
	public int getValue()
	{
		return buffer;
	}
	
	public int getWordLength()
	{
		return wordLength;
	}
	
	public void clear()
	{
		buffer = 0;
		bitsUsed = 0;
	}
	
	private int getMask(int length)
	{
		return 0xFFFFFFFF >>> (32 - length);
	}
	
	private int maskWord(int value)
	{
		return value & wordMask;
	}
	
	private void prependBits(int value)
	{
		value = value << bitsUsed;
		buffer = buffer | value;
	}
	
	private void usedBits(int count)
	{
		bitsUsed += count;
	}
	
	public void prependWord(int value)
	{
		value = maskWord(value);
		prependBits(value);
		usedBits(wordLength);
	}
	
	public boolean takeBit()
	{
		boolean bit = BitBuffer.testBit(buffer, 0);
		buffer = buffer >>> 1;
		bitsUsed -= 1;
		return bit;
	}
	
	public void invertBits()
	{
		buffer = (~buffer);
	}
	
	static int testValue;
	static int testMask;
	public static boolean testBit()
	{
		return (testValue & testMask) != 0;
	}
	
	private static int getBitMask(int powerOfTwo)
	{
		return 0x01 << powerOfTwo;
	}
	
	public static boolean testBit(int value, int powerOfTwo)
	{
		testValue = value;
		testMask = getBitMask(powerOfTwo);
		return testBit();
	}
}
