package org.unclazz.metaversion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class MVHandlerInterceptor implements HandlerInterceptor {
	@Override
	public void afterCompletion(
			final HttpServletRequest request, 
			final HttpServletResponse response, 
			final Object handler, 
			final Exception ex)
			throws Exception {
		// Do nothing.
	}

	@Override
	public void postHandle(
			final HttpServletRequest request, 
			final HttpServletResponse response, 
			final Object handler,
			final ModelAndView modelAndView)
			throws Exception {
		// Do nothing.
	}

	@Override
	public boolean preHandle(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Object handler)
			throws Exception {
		
		// 匿名ユーザとしてのアクセス かつ RESTコントローラへのマッピングがされているとき
		if (anonymousUser() && restApiUrl(handler)) {
			// アクセス権限不足としてアクセスを拒否する
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return false;
		}
		
		// それ以外の場合はアクセスを許可する
		return true;
	}
	
	private boolean anonymousUser() {
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
		return !(po instanceof UserDetails);
	}
	
	private boolean restApiUrl(final Object handler) {
		// ハンドラーがHandlerMethodでない場合は対象外
		if (!(handler instanceof HandlerMethod)) {
			return false;
		}
		
		// アノテーション情報を取得
		final HandlerMethod method = (HandlerMethod) handler;
		final RequestMapping methodLevel = method.getMethodAnnotation(RequestMapping.class);
		final RequestMapping classLevel = method.getMethod().getDeclaringClass().getAnnotation(RequestMapping.class);
		
		// いずれのレベルでもアノテーションが存在しなければ対象外
		if (methodLevel == null && classLevel == null) {
			return false;
		}
		
		// メソッドレベルのアノテーションをチェック
		if (methodLevel != null) {
			for (final String v : methodLevel.value()) {
				if (v.startsWith(MVApplication.REST_API_PATH_PREFIX)) {
					return true;
				}
			}
		}
		
		// クラスレベルのアノテーションをチェック
		if (classLevel != null) {
			for (final String v : classLevel.value()) {
				if (v.startsWith(MVApplication.REST_API_PATH_PREFIX)) {
					return true;
				}
			}
		}
		
		// いずれにも該当しない場合は対象外
		return false;
	}

}
