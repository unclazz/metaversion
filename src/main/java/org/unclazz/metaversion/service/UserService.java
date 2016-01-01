package org.unclazz.metaversion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.entity.User;
import org.unclazz.metaversion.mapper.UserMapper;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;
import org.unclazz.metaversion.vo.Paging;
import org.unclazz.metaversion.vo.OrderByClause.Order;

@Service
public class UserService {
	@Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
	private UserMapper userMapper;
	
	public User composeValueObject(final String username, final char[] password, final boolean admin) {
		final User user = new User();
		user.setName(username);
		user.setPassword(passwordEncoder.encode(MVUtils.charArrayToCharSequence(password)));
		user.setAdmin(admin);
		return user;
	}
	
	public User composeValueObject(final int id, final String username, final char[] password, final boolean admin) {
		final User user = composeValueObject(username, password, admin);
		user.setId(id);
		return user;
	}
	
	@Transactional
	public void registerUser(final User user, final MVUserDetails auth) {
		user.setId(userMapper.selectNextVal());
		userMapper.insert(user, auth);
	}
	
	@Transactional
	public void modifyUser(final User user, final MVUserDetails auth) {
		if (userMapper.update(user, auth) != 1) {
			throw MVUtils.illegalArgument("Update target user(id=%s) is not found.", user.getId());
		}
	}
	
	@Transactional
	public void removeUser(final int id, final MVUserDetails auth) {
		if (userMapper.delete(id) != 1) {
			throw MVUtils.illegalArgument("Delete target user(id=%s) is not found.", id);
		}
	}
	
	public User getUser(final int id) {
		return userMapper.selectUserHasNoPasswordOneById(id);
	}
	
	public List<User> getUserList(final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("name", Order.ASC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);
		return userMapper.selectUserHasNoPasswordAll(orderBy, limitOffset);
	}
}
