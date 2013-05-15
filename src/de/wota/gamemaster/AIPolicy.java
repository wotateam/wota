package de.wota.gamemaster;

import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;

public class AIPolicy extends Policy {

	public PermissionCollection getPermissions(CodeSource codeSource) {
		Permissions p = new Permissions();
		p.add(new AllPermission());
		return p;
	}

	public void refresh() {
	}
}
