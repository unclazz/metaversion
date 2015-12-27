package org.unclazz.metaversions.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.unclazz.metaversion.MetaVersionUserDetails;
import org.unclazz.metaversion.entity.User;
import org.unclazz.metaversion.mapper.UserMapper;

@Component
public class MetaVersionUserDetailsService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	// 指定されたユーザ名をチェック
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("Username is empty");
        }
        
        // ユーザ名をキーとして使ってユーザ情報を取得してみる
        final User user = userMapper.selectOneByName(username);
        
        // 取得できなかった場合のためのチェック
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User not found \"%s\"", username));
        }
        
        // レコード情報からUserDetailsオブジェクトを作成して返す
        return MetaVersionUserDetails.of(user);
    }
}
