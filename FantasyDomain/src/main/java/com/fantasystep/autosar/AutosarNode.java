package com.fantasystep.autosar;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.IconHolder;

//import gautosar.ggenericstructure.ginfrastructure.GIdentifiable;

@DomainClass(validParents = { AutosarModel.class, AutosarNode.class }, label = "LABEL_AUTOSAR_NODE", icon = "dropline.png")
public class AutosarNode extends Node implements IconHolder {

	private static final long serialVersionUID = 9201939603074649503L;
	
//	private GIdentifiable gIdentifiable = null;
	
	private String iconName = null;
	
//	public AutosarNode(GIdentifiable gIdentifiable) {
//		this.setgIdentifiable(gIdentifiable);
//		setId(UUID.fromString(gIdentifiable.gGetUuid()));
//		setParentId(UUID.fromString(((GIdentifiable)gIdentifiable.eContainer()).gGetUuid()));
//		this.iconName = gIdentifiable.getClass().getSimpleName().toLowerCase() + ".png";
//	}
//
//	public GIdentifiable getgIdentifiable() {
//		return gIdentifiable;
//	}
//
//	public void setgIdentifiable(GIdentifiable gIdentifiable) {
//		this.gIdentifiable = gIdentifiable;
//	}

	public String getIconName() {
		return iconName;
	}

//	@Override
//	public List<Node> getChildren() {
//		List<Node> list = new ArrayList<Node>();
//		for(EObject child : this.gIdentifiable.eContents())
//			list.add(new AutosarNode((GIdentifiable)child));
//		return list;
//	}
//	
//	@Override
//	public void addChild(Node child) throws ValidationFailedException {
//		if(child instanceof AutosarNode)
//			this.gIdentifiable.eContents().add(((AutosarNode) child).getgIdentifiable());
//	}
//	
//	@Override
//	public void removeChild(Node child) {
//		if(child instanceof AutosarNode)
//			this.gIdentifiable.eContents().remove(((AutosarNode) child).getgIdentifiable());
//	}
//	
//	@Override
//	public String getLabel() {
//		return this.gIdentifiable.gGetShortName();
//	}

	@Override
	public String getIcon(int iconSize) {
		return String.format("icons/%s/%s", iconSize, this.iconName);
	}
}
