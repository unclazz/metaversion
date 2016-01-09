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
	
	public void doPasswordEncode(final User user) {
		user.setEncodedPassword(passwordEncoder.encode(MVUtils.charArrayToCharSequence(user.getPassword())));
	}
	
	public void doPasswordSupplement(final User user) {
		final User dbUser = userMapper.selectOneById(user.getId());
		user.setEncodedPassword(dbUser.getEncodedPassword());
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
		return userMapper.selectOneById(id);
	}
	
	public List<User> getUserList(final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("name", Order.ASC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);
		return userMapper.selectAll(orderBy, limitOffset);
	}
	
	public int getUserCount() {
		return userMapper.selectCount();
	}
}
