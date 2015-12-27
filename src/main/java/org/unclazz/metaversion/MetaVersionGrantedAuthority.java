package org.unclazz.metaversion;

import org.springframework.security.core.GrantedAuthority;

public class MetaVersionGrantedAuthority implements GrantedAuthority {
	private static final long serialVersionUID = -4297213634794564411L;
	public static final MetaVersionGrantedAuthority OPERATOR = new MetaVersionGrantedAuthority("OPERATOR"); 
	public static final MetaVersionGrantedAuthority ADMINISTRATOR = new MetaVersionGrantedAuthority("ADMINISTRATOR"); 
	private static final MetaVersionGrantedAuthority[] knownAuthorities = {OPERATOR, ADMINISTRATOR};
	
	public static MetaVersionGrantedAuthority of(final String name) {
		final String upperCased = name.toUpperCase();
		for (final MetaVersionGrantedAuthority auth : knownAuthorities) {
			if (auth.equals(upperCased)) {
				return auth;
			}
		}
		throw new IllegalArgumentException(String.format("Unknown authority \"%s\"", name));
	}
	
	private final String name;
	private final boolean admin;
	
	private MetaVersionGrantedAuthority(String name) {
		this.name = name;
		this.admin = name.equals("ADMINISTRATOR");
	}
	
	public boolean isAdmin() {
		return admin;
	}
	public String getName() {
		return name;
	}
	@Override
	public String getAuthority() {
		return getName();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (admin ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaVersionGrantedAuthority other = (MetaVersionGrantedAuthority) obj;
		if (admin != other.admin)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
