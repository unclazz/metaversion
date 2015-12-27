package org.unclazz.metaversion;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.unclazz.metaversion.entity.User;

public class MetaVersionUserDetails extends org.springframework.security.core.userdetails.User {
	private static final long serialVersionUID = 2213449577870703888L;
	
	private static List<GrantedAuthority> emptyAuthorities() {
		return new ArrayList<GrantedAuthority>();
	}
	private static List<GrantedAuthority> operatorAuthorities() {
		final List<GrantedAuthority> l = emptyAuthorities();
		l.add(MetaVersionGrantedAuthority.OPERATOR);
		return l;
	}
	private static List<GrantedAuthority> administratorAuthorities() {
		final List<GrantedAuthority> l = operatorAuthorities();
		l.add(MetaVersionGrantedAuthority.ADMINISTRATOR);
		return l;
	}
	public static MetaVersionUserDetails of(final User user) {
		return new MetaVersionUserDetails(user.getId(), user.getName(), user.getPassword(), user.isAdmin());
	}
	public static MetaVersionUserDetails of(final Principal principal) {
		return (MetaVersionUserDetails) ((Authentication) principal).getPrincipal();
	}
	
	public MetaVersionUserDetails() {
        super("INVALID", "INVALID", false, false, false, false, emptyAuthorities());
    }
	
	public MetaVersionUserDetails(final int id, final String username, final String passeord, final boolean isAdmin) {
		super(username, passeord, true, true, true, true, isAdmin ? administratorAuthorities() : operatorAuthorities());
		this.admin = isAdmin;
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
