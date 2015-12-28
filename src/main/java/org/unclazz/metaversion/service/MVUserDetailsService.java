package org.unclazz.metaversion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.MVProperties;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.User;
import org.unclazz.metaversion.mapper.UserMapper;

@Service
public class MVUserDetailsService implements UserDetailsService {
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private MVProperties props;
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	// 指定されたユーザ名をチェック
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("Username is empty");
        }
        
        final User user;
        // ユーザ名の内容によって処理を分岐
        if (username.equals(props.getDefaultAdminName())) {
        	// プロパティ・ファイルで指定されたアドミニストレータ名の場合
        	// プロパティ・ファイルの内容をもとにユーザ情報を作成
        	user = new User();
        	user.setId(props.getDefaultAdminId());
        	user.setName(username);
        	user.setPassword(encoder.encode(new StringBuilder().append(props.getDefaultAdminPassword())));
        	user.setAdmin(true);
        } else {
        	// それ以外の場合
	        // ユーザ名をキーとして使ってユーザ情報を取得してみる
	        user = userMapper.selectOneByName(username);
        }
        
        // 取得できなかった場合のためのチェック
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User not found \"%s\"", username));
        }
        
        // レコード情報からUserDetailsオブジェクトを作成して返す
        return MVUserDetails.of(user);
    }
}
