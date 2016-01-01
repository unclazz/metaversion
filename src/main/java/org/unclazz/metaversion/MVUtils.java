package org.unclazz.metaversion;

public final class MVUtils {
	private MVUtils() {}
	
	public static void argsMustBeGreaterThanOrEqual0(String target, int... args) {
		for (final int o : args) {
			if (!(o >= 0)) {
				throw illegalArgument("%s must be greater than or equal 0. But actual value is %s.", target, o);
			}
		}
	}
	public static void argsMustBeGreaterThan0(String target, int... args) {
		for (final int o : args) {
			if (!(o > 0)) {
				throw illegalArgument("%s must be greater than 0. But actual value is %s.", target, o);
			}
		}
	}
	public static void argsMustBeNotNull(String target, Object... args) {
		for (final Object o : args) {
			if (o == null) {
				throw illegalArgument("%s must be not null.", target);
			}
		}
	}
	public static void argMustBeExist(String target, Object ref) {
		if (ref == null) {
			throw new RuntimeException(String.format("%s is not exist.", target));
		}
	}
	public static void argsMustBeNotNullAndNotEmpty(String target, CharSequence... args) {
		for (final CharSequence o : args) {
			if (o == null || o.length() == 0) {
				throw illegalArgument("%s must be not null and not empty.", target);
			}
		}
	}
	public static IllegalArgumentException illegalArgument(String format, Object... args) {
		return new IllegalArgumentException(String.format(format, args));
	}
	public static RuntimeException unexpectedResult(String format, Object... args) {
		return new RuntimeException(String.format(format, args));
	}
	public static RuntimeException sqlOperationIsFailed(String operation) {
		return unexpectedResult("SQL operation is failed: %s .", operation);
	}
}
