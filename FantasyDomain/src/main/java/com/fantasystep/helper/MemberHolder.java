package com.fantasystep.helper;

import java.util.List;
import java.util.UUID;

import com.fantasystep.domain.Node;

public interface MemberHolder {
	
	public void removeMember(Node node);

	public void addMember(Node node);

	public List<UUID> getMembers();
	
	public void setMembers(List<UUID> members);
}
