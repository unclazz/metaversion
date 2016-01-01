package org.unclazz.metaversion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.User;
import org.unclazz.metaversion.entity.UserHasNoPassword;
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
	
	@Transactional
	public void registerUser(String name, CharSequence rawPassord, boolean admin, MVUserDetails auth) {
		final User user = new User();
		user.setId(userMapper.selectNextVal());
		user.setName(name);
		user.setPassword(passwordEncoder.encode(rawPassord));
		user.setAdmin(admin);
		userMapper.insert(user, auth);
	}
	
	@Transactional
	public void modifyUser(String name, CharSequence rawPassord, boolean admin, MVUserDetails auth) {
		final User user = userMapper.selectOneByName(name);
		user.setName(name);
		user.setPassword(passwordEncoder.encode(rawPassord));
		user.setAdmin(admin);
		userMapper.update(user, auth);
	}
	
	@Transactional
	public void removeUser(String name, MVUserDetails auth) {
		final User user = userMapper.selectOneByName(name);
		userMapper.delete(user.getId());
	}
	
	public UserHasNoPassword getUser(final int id) {
		return userMapper.selectUserHasNoPasswordOneById(id);
	}
	
	public List<UserHasNoPassword> getUserList(Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("name", Order.ASC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);
		return userMapper.selectUserHasNoPasswordAll(orderBy, limitOffset);
	}
}
