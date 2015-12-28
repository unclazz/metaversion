package org.unclazz.metaversion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.User;
import org.unclazz.metaversion.mapper.UserMapper;

@Service
public class UserService {
	@Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
	private UserMapper userMapper;
	
	public void registerUser(String name, CharSequence rawPassord, boolean isAdmin, MVUserDetails auth) {
		final User user = new User();
		user.setId(userMapper.selectNextVal());
		user.setName(name);
		user.setPassword(passwordEncoder.encode(rawPassord));
		user.setAdmin(isAdmin);
		userMapper.insert(user, auth);
	}
}
