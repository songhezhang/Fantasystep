package com.fantasystep.component.field.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.field.AbstractMultiNodeCField;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.domain.Node;
import com.vaadin.ui.ComboBox;

public class NodeTreeComboboxCField extends AbstractMultiNodeCField
{
	public NodeTreeComboboxCField( FieldAttributeAccessor fieldAttributes, List<Node> nodes )
	{
		super( fieldAttributes, nodes );
	}

	@Override
	public void initField()
	{
		Map<UUID,Integer> indentationMap = new HashMap<UUID,Integer>();

		field = new ComboBox( fieldAttributes.getLabel());
		ComboBox combo = (ComboBox) field;
		String label;
		int indentation;
		for( Node n : getNodes() )
		{
			indentation = 0;
			if( indentationMap.containsKey( n.getParentId() ) )
				indentation = indentationMap.get( n.getParentId() ) + 1;
			indentationMap.put( n.getId(), indentation );

			if( n.getLabel() != null && n.getLabel().equals( "null null" ) )
				label = String.format( "%s%s", "-", LocalizationHandler.get( LabelUtil.LABEL_CURRENT_USER ) );
			else
				label = String.format( "%s%s", "-", n.getLabel() );
			combo.addItem( n.getId() );
			combo.setItemCaption( n.getId(), label );
		}
//		combo.setNewItemsAllowed( false );
//		combo.setInvalidAllowed(false);
//		combo.setNullSelectionAllowed( false );
//		combo.setReadOnly(false);
//		combo.setTextInputAllowed( true );
	}
}
