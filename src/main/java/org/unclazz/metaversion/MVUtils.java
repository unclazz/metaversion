package org.unclazz.metaversion;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		error.printStackTrace(pw);
		pw.flush();
		return sw.getBuffer();
	}
	
	/**
	 * {@code char[]}を{@code CharSequence}に変換する.
	 * @param charArray 変換前
	 * @return 変換後
	 */
	public static CharSequence charArrayToCharSequence(final char[] charArray) {
		return new StringBuilder().append(charArray == null ? new char[0] : charArray);
	}
	
	private static final Pattern doubleSlash = Pattern.compile("/+");
	public static CharSequence slashNormalize(final CharSequence original) {
		CharSequence result = original;
		
		if (result.length() == 0) {
			throw illegalArgument("Slash normatization is not support empty character sequence.");
		}
		
		if (result.charAt(0) != '/') {
			result = new StringBuilder().append('/').append(result);
		}
		
		if (result.charAt(result.length() - 1) == '/') {
			result = new StringBuilder().append(result.subSequence(0, result.length() - 2));
		}
		
		return doubleSlash.matcher(result).replaceAll("/");
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static<T> ResponseEntity<T> httpResponseOfBadRequest(final Throwable cause) {
		// 戻り値型を揃えるため強引にキャストを行う
		// ＊イレイジャを前提としたトリック
		return (ResponseEntity<T>) new ResponseEntity(cause, HttpStatus.BAD_REQUEST);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static<T> ResponseEntity<T> httpResponseOfInternalServerError(final Throwable cause) {
		// 戻り値型を揃えるため強引にキャストを行う
		// ＊イレイジャを前提としたトリック
		return (ResponseEntity<T>) new ResponseEntity(cause, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	public static boolean threadIsForAnonymousUser() {
		// 認証パス済みのときはAuthenticationが返される
		// しかしそうでないときも返されることがある
		final Authentication au = SecurityContextHolder.getContext().getAuthentication();
		if (au == null) {
			return true;
		}
		
		// 認証パス済みのときはUserDetailsが返される
		// そうでない時は文字列"anonymousUser"が返される
		final Object po = au.getPrincipal();

		// 型判定により認証状態を判定する
		return !(po instanceof MVUserDetails);
	}
	
	public static MVUserDetails userDetails() {
		// 認証パス済みのときはAuthenticationが返される
		// しかしそうでないときも返されることがある
		final Authentication au = SecurityContextHolder.getContext().getAuthentication();
		if (au == null) {
			return null;
		}
		
		// 認証パス済みのときはUserDetailsが返される
		// そうでない時は文字列"anonymousUser"が返される
		final Object po = au.getPrincipal();
		if (po instanceof MVUserDetails) {
			return (MVUserDetails) po;
		} else {
			return null;
		}
	}
}
