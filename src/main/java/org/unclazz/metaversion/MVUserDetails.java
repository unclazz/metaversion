package org.unclazz.metaversion;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.unclazz.metaversion.entity.User;

public class MVUserDetails extends org.springframework.security.core.userdetails.User {
	private static final long serialVersionUID = 2213449577870703888L;
	
	private static List<GrantedAuthority> emptyAuthorities() {
		return new ArrayList<GrantedAuthority>();
	}
	private static List<GrantedAuthority> operatorAuthorities() {
		final List<GrantedAuthority> l = emptyAuthorities();
		l.add(MVGrantedAuthority.OPERATOR);
		return l;
	}
	private static List<GrantedAuthority> administratorAuthorities() {
		final List<GrantedAuthority> l = operatorAuthorities();
		l.add(MVGrantedAuthority.ADMINISTRATOR);
		return l;
	}
	public static MVUserDetails of(final User user) {
		return new MVUserDetails(user.getId(), user.getName(), user.getPassword(), user.isAdmin());
	}
	public static MVUserDetails of(final Principal principal) {
		return (MVUserDetails) ((Authentication) principal).getPrincipal();
	}
	
	public MVUserDetails() {
        super("INVALID", "INVALID", false, false, false, false, emptyAuthorities());
    }
	
	public MVUserDetails(final int id, final String username, final String passeord, final boolean admin) {
		super(username, passeord, true, true, true, true, admin ? administratorAuthorities() : operatorAuthorities());
		this.admin = admin;
	}
	
	private int id;
	private boolean admin;
	
	public int getId() {
		return id;
	}
	public boolean isAdmin() {
		return admin;
	}
	public User toUser() {
		final User user = new User();
		user.setId(id);
		user.setName(super.getUsername());
		user.setPassword(super.getPassword());
		user.setAdmin(admin);
		return user;
	}
}
