package net.earthcomputer.vimapi.util;

import java.util.List;

public class BeforeAfterComparable<T> implements Comparable<BeforeAfterComparable<T>> {

	private String name;
	private T val;
	private List<String> before;
	private List<String> after;

	public BeforeAfterComparable(String name, T val, RelativePosition relativePos) {
		this.name = name;
		this.val = val;
		this.before = relativePos.getBefore();
		this.after = relativePos.getAfter();
	}

	public T getValue() {
		return val;
	}

	@Override
	public int compareTo(BeforeAfterComparable<T> other) {
		String thisName = this.name;
		String otherName = other.name;
		boolean thisBeforeOther = this.before.contains(otherName);
		boolean thisAfterOther = this.after.contains(otherName);
		boolean otherBeforeThis = other.before.contains(thisName);
		boolean otherAfterThis = other.after.contains(thisName);
		if (thisBeforeOther) {
			if (thisAfterOther || otherBeforeThis) {
				return 0;
			} else {
				return -1;
			}
		} else if (thisAfterOther) {
			if (otherAfterThis) {
				return 0;
			} else {
				return 1;
			}
		} else if (otherBeforeThis) {
			if (otherAfterThis) {
				return 0;
			} else {
				return 1;
			}
		} else if (otherAfterThis) {
			return -1;
		} else {
			return 0;
		}
	}

}
