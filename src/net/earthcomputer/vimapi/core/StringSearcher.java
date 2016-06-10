package net.earthcomputer.vimapi.core;

import java.util.List;

import com.google.common.collect.Lists;

public class StringSearcher {

	private static final int[] CONSTANT_SIZES = new int[] { -1, -1, -1, 4, 4, 8, 8, 2, 2, 4, 4, 4, 4, -1, -1, 3, 2, 4 };

	private byte[] bytes;
	private List<String> strings = Lists.newArrayList();

	public StringSearcher(byte[] bytes) {
		this.bytes = bytes;
	}

	public void search() {
		int cpSize = unsignedShort(8) - 1;
		int idx = 10;
		for (int i = 0; i < cpSize; i++) {
			int tag = unsignedByte(idx);
			idx++;
			if (tag == 1) {
				// This is the string tag we want
				int byteLength = unsignedShort(idx);

				idx += 2;
				int stringLength = 0;
				char[] chars = new char[byteLength];
				for (int byteOffset = 0; byteOffset < byteLength; byteOffset++) {
					int b1 = unsignedByte(idx + byteOffset);
					if (b1 >= 0x01 && b1 <= 0x7F) {
						// Single-byte char
						chars[stringLength] = (char) b1;
					} else {
						// Two-byte char (this is as high as it goes in MC)
						int b2 = unsignedByte(idx + byteOffset + 1);
						chars[stringLength] = (char) (((b1 & 0x1F) << 6) + (b2 & 0x3F));
						byteOffset++;
					}
					stringLength++;
				}
				idx += byteLength;
				String str = new String(chars, 0, stringLength);
				strings.add(str);
			} else {
				idx += CONSTANT_SIZES[tag];
				if (tag == 5 || tag == 6) {
					// longs and doubles take up 2
					i++;
				}
			}
		}
	}

	private int unsignedByte(int idx) {
		return bytes[idx] & 0xFF;
	}

	private int unsignedShort(int idx) {
		return (unsignedByte(idx) << 8) | unsignedByte(idx + 1);
	}

	public List<String> getFoundStrings() {
		return strings;
	}

}
