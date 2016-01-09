package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.User;
import org.unclazz.metaversion.service.UserService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;
import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
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
	 * {@link User}オブジェクトのプロパティのうちパスワードが未設定の場合、データベースの内容が自動設定される。
	 * 何らかの理由で更新に失敗した場合は{@code 500 Internal Server Error}を返す。
	 * @param principal 認証情報
	 * @param user ユーザ情報
	 * @return 更新結果のユーザ情報
	 */
	@RequestMapping(value="/users/{id}", method=RequestMethod.PUT)
	public ResponseEntity<User> putUser(final Principal principal, @RequestBody final User user) {
		
		try {
			if (user.getPassword() == null) {
				userService.doPasswordSupplement(user);
			} else {
				userService.doPasswordEncode(user);
			}
			userService.modifyUser(user, MVUserDetails.of(principal));
			return httpResponseOfOk(user);
			
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e);
		}
	}
	
	/**
	 * リクエストパラメータをもとにユーザ情報を登録する.
	 * 何らかの理由で登録に失敗した場合は{@code 500 Internal Server Error}を返す。
	 * @param principal 認証情報
	 * @param user ユーザ情報
	 * @return 登録結果のユーザ情報
	 */
	@RequestMapping(value="/users", method=RequestMethod.POST)
	public ResponseEntity<User> postUser(final Principal principal, @RequestBody final User user) {
		
		try {
			userService.doPasswordEncode(user);
			userService.registerUser(user, MVUserDetails.of(principal));
			return httpResponseOfOk(user);
			
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e);
		}
	}
	
	@RequestMapping(value="/users/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<User> deleteUser(final Principal principal, @PathVariable("id") final int id) {
		try {
			userService.removeUser(id, MVUserDetails.of(principal));
			return httpResponseOfOk();
			
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e);
		}
	}
}
