package org.unclazz.metaversion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled=true)
public class MVSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final PasswordEncoder passwordEncoder = new StandardPasswordEncoder();
	@Autowired
	private UserDetailsService userDetailsService;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            	// TODO 管理者のみアクセスを許されるパスの定義
            	.antMatchers("/foo").hasAuthority(MVGrantedAuthority.NAME_ADMINISTRATOR)
            	// HttpSecurityレベルではREST APIのパスへのアクセスはすべて許可する
            	// ＊ただしインターセプターにより認証済みユーザ以外へは403を返すようにしている
            	.antMatchers(MVApplication.REST_API_PATH_PREFIX + "/*").permitAll()
            	// マスタデータ等の初期化のためのパスへのアクセスはすべて許可する
            	.antMatchers(MVApplication.INIT_PAGE_PATH).permitAll()
            	// 静的リソースへのアクセスはすべて許可する
            	.antMatchers("/favicon.ico", "/css/*", "/img/*", "/js/*", "/fonts/*").permitAll()
            	// その他のパスへのアクセスには認証パスが必要とする
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage(MVApplication.LOGIN_PAGE_PATH)
                .failureUrl(MVApplication.LOGIN_PAGE_PATH)
                .defaultSuccessUrl(MVApplication.TOP_PAGE_PATH, true)
                .permitAll()
                .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher(MVApplication.LOGOUT_PAGE_PATH))
                .logoutSuccessUrl(MVApplication.LOGIN_PAGE_PATH)
                .permitAll();
    }
    
    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }
    
    @Bean
    public PasswordEncoder passwordEncorder() {
    	return passwordEncoder;
    }
}
