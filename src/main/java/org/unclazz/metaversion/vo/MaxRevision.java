package org.unclazz.metaversion.vo;

import org.unclazz.metaversion.MVUtils;

public final class MaxRevision {
	public static MaxRevision startsWith(final int init) {
		return new MaxRevision(init);
	}
	
	private int max;
	
	private MaxRevision(final int initial) {
		if (initial < 0) {
			throw MVUtils.illegalArgument("Initial value of MaxRevition "
					+ "object must be greater than or equal 0 (specified value=%s).", initial);
		}
		max = initial;
	}
	
	public boolean trySetNewValue(final int value) {
		if (max < value) {
			max = value;
			return true;
		} else {
			return false;
		}
	}
	public int getValue() {
		return max;
	}
}