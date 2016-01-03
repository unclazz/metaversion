package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.User;
import org.unclazz.metaversion.service.UserService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;
import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping("/rest")
public class UsersJsonController {
	@Autowired
	private UserService userService;
	
	/**
	 * ユーザ情報の一覧を返す.
	 * @param principal 認証情報
	 * @param paging リクエストパラメータ{@code page}と{@code size}の情報を格納したオブジェクト
	 * @return ユーザ情報一覧
	 */
	@RequestMapping(value="/users", method=RequestMethod.GET)
	public Paginated<User> getUserList(final Principal principal, @ModelAttribute final Paging paging) {
		return Paginated.of(paging, userService.getUserList(paging), userService.getUserCount());
	}
	
	/**
	 * IDで指定されたユーザ情報を返す.
	 * 該当するユーザ情報が見つからなかった場合は{@code 404 Not Found}を返す.
	 * @param principal 認証情報
	 * @param id ID
	 * @return ユーザ情報
	 */
	@RequestMapping(value="/users/{id}", method=RequestMethod.GET)
	public ResponseEntity<User> getUser(final Principal principal, @PathVariable("id") final int id) {
		return httpResponseOfOkOrNotFound(userService.getUser(id));
	}
	
	/**
	 * リクエストパラメータをもとにユーザ情報を更新する.
	 * 何らかの理由で更新に失敗した場合は{@code 500 Internal Server Error}を返す。
	 * @param principal 認証情報
	 * @param id ユーザID
	 * @param username ユーザ名
	 * @param password パスワード
	 * @param admin 管理者かどうか
	 * @return 更新結果のユーザ情報
	 */
	@RequestMapping(value="/users/{id}", method=RequestMethod.PUT)
	public ResponseEntity<User> putUser(final Principal principal,
			@PathVariable("id") final int id,
			@RequestParam("username") final String username, 
			@RequestParam("password") final char[] password, 
			@RequestParam("admin") final boolean admin) {
		
		try {
			final User user = userService.composeValueObject(id, username, password, admin);
			userService.modifyUser(user, MVUserDetails.of(principal));
			return httpResponseOfOk(user);
			
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
	
	/**
	 * リクエストパラメータをもとにユーザ情報を登録する.
	 * 何らかの理由で登録に失敗した場合は{@code 500 Internal Server Error}を返す。
	 * @param principal 認証情報
	 * @param username ユーザ名
	 * @param password パスワード
	 * @param admin 管理者かどうか
	 * @return 登録結果のユーザ情報
	 */
	@RequestMapping(value="/users", method=RequestMethod.POST)
	public ResponseEntity<User> postUser(final Principal principal,
			@RequestParam("username") final String username, 
			@RequestParam("password") final char[] password, 
			@RequestParam("admin") final boolean admin) {
		
		try {
			final User user = userService.composeValueObject(username, password, admin);
			userService.registerUser(user, MVUserDetails.of(principal));
			return httpResponseOfOk(user);
			
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
	
	// TODO 外部キー制約で縛られたレコードをどのようにするかが課題
	@RequestMapping(value="/users/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<User> deleteUser(final Principal principal, @PathVariable("id") final int id) {
		try {
			userService.removeUser(id, MVUserDetails.of(principal));
			return httpResponseOfOk();
			
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
}
