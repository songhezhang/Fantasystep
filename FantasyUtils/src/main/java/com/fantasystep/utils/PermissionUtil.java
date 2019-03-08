package com.fantasystep.utils;


import java.util.List;

import com.fantasystep.domain.Node;
import com.fantasystep.domain.Permission;
import com.fantasystep.domain.User;

public class PermissionUtil {

	public static PermissionDescriptor getPermissionDescriptor(User user,
			Node node, Node node2) {
		return new AllTruePermissionDescriptor();
	}
	public static class AllTruePermissionDescriptor extends PermissionDescriptor{

		public boolean hasDeletePermission() {
			return true;
		}

		public boolean hasBrowsePermission() {
			return true;
		}

		public boolean hasDestroyPermission() {
			return true;
		}

		public boolean hasInsertPermission() {
			return true;
		}

		public boolean hasSelectPermission() {
			return true;
		}

		public boolean hasUpdatePermission() {
			return true;
		}

		public static PermissionDescriptor mergePermissionDescriptiors(
				List<PermissionDescriptor> descriptors) {
			return new AllTruePermissionDescriptor();
		}

		public void apply(Permission p) {
			
		}
	}
	public static class PermissionDescriptor {

		public boolean hasDeletePermission() {
			return false;
		}

		public boolean hasBrowsePermission() {
			return false;
		}

		public boolean hasDestroyPermission() {
			return false;
		}

		public boolean hasInsertPermission() {
			return false;
		}

		public boolean hasSelectPermission() {
			return false;
		}

		public boolean hasUpdatePermission() {
			return false;
		}

		public static PermissionDescriptor mergePermissionDescriptiors(
				List<PermissionDescriptor> descriptors) {
			return new AllTruePermissionDescriptor();
		}

		public void apply(Permission p) {
			
		}
	}
	public static PermissionDescriptor getPermissionDescriptor(User user,
			Node target, Class<? extends Node> clazz, Node root) {
		return new AllTruePermissionDescriptor();
	}
}
