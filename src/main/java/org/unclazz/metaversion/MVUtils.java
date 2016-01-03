package org.unclazz.metaversion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
	public static CharSequence stackTraceToCharSequence(final Throwable error) {
		final String sep = System.getProperty("line.separator");
		final StringBuilder buff = new StringBuilder();
		for (final StackTraceElement e : error.getStackTrace()) {
			if (buff.length() > 0) {
				buff.append(sep);
			}
			buff.append("at ").append(e.getClassName()).append('.').append(e.getMethodName())
			.append(' ').append('(').append(e.getFileName()).append(':').append(e.getLineNumber()).append(')');
		}
		return buff;
	}
	
	/**
	 * {@code char[]}を{@code CharSequence}に変換する.
	 * @param charArray 変換前
	 * @return 変換後
	 */
	public static CharSequence charArrayToCharSequence(final char[] charArray) {
		return new StringBuilder().append(charArray);
	}
	
	/**
	 * HTTPステータスコード{@code 200 OK}を返すためのレスポンスエンティティを生成する.
	 * @return レスポンスエンティティ
	 */
	public static<T> ResponseEntity<T> httpResponseOfOk() {
		return new ResponseEntity<T>(HttpStatus.OK);
	}
	
	/**
	 * HTTPステータスコード{@code 200 OK}を返すためのレスポンスエンティティを生成する.
	 * @param value オブジェクト
	 * @return レスポンスエンティティ
	 */
	public static<T> ResponseEntity<T> httpResponseOfOk(final T value) {
		return new ResponseEntity<T>(value, HttpStatus.OK);
	}
	
	/**
	 * HTTPステータスコード{@code 404 Not Found}を返すためのレスポンスエンティティを生成する.
	 * @return レスポンスエンティティ
	 */
	public static<T> ResponseEntity<T> httpResponseOfNotFound() {
		return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
	}
	
	/**
	 * 引数が{@code null}の場合HTTPステータスコード{@code 404 Not Found}のレスポンスエンティティを生成する.
	 * そうでない場合はHTTPステータスコード{@code 200 OK}のレスポンスエンティティを生成する.
	 * 
	 * @param value オブジェクト
	 * @return レスポンスエンティティ
	 */
	public static<T> ResponseEntity<T> httpResponseOfOkOrNotFound(final T value) {
		if (value == null) {
			return httpResponseOfNotFound();
		} else {
			return httpResponseOfOk(value);
		}
	}
	
	/**
	 * HTTPステータスコード{@code 400 Bad Request}を返すためのレスポンスエンティティを生成する.
	 * @param message メッセージ
	 * @return レスポンスエンティティ
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static<T> ResponseEntity<T> httpResponseOfBadRequest(final String message) {
		// 戻り値型を揃えるため強引にキャストを行う
		// ＊イレイジャを前提としたトリック
		return (ResponseEntity<T>) new ResponseEntity(message, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * HTTPステータスコード{@code 500 Internal Server Error}を返すためのレスポンスエンティティを生成する.
	 * @param value オブジェクト
	 * @return レスポンスエンティティ
	 */
	public static<T> ResponseEntity<T> httpResponseOfInternalServerError(final T value) {
		return new ResponseEntity<T>(value, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * HTTPステータスコード{@code 500 Internal Server Error}を返すためのレスポンスエンティティを生成する.
	 * @param message メッセージ
	 * @return レスポンスエンティティ
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static<T> ResponseEntity<T> httpResponseOfInternalServerError(final String message) {
		// 戻り値型を揃えるため強引にキャストを行う
		// ＊イレイジャを前提としたトリック
		return (ResponseEntity<T>) new ResponseEntity(message, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
