package com.fantasystep.container.single;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.common.PopUpModel;
import com.fantasystep.component.field.common.TextAreaCField;
import com.fantasystep.component.field.custom.AbstractCustomField;
import com.fantasystep.component.field.custom.LabelCField;
import com.fantasystep.component.panel.ConcreteNodeEvent;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.IconUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.LayoutUtil;
import com.fantasystep.component.utils.TableUtil;
import com.fantasystep.component.utils.UINodeUtil;
import com.fantasystep.domain.Entity;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.MemberHolder;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.util.CNodeUtil;
import com.porotype.codelabel.CodeLabel;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.BaseTheme;

public class NodeDisplayCContainer extends AbstractNodeCContainer {
	private static final long serialVersionUID = 2835928312890755661L;

	public enum Mode {
		WITH_UPDATE_BUTTON, WITHOUT_UPDATE_BUTTON
	}

	private Button updateButton;
	private Mode updateMode;

	public NodeDisplayCContainer(Node node, Mode updateMode) {
		super(node, Action.UPDATE, FormMode.TAB);
		this.updateMode = updateMode;
		getForm();
		setPrimaryFields(node);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Field createFieldWithPropertyId(Node node, Object propertyId) {

		Field field = null;
		FieldAttributeAccessor attributes = AnnotationsParser.getAttributes(
				node.getClass(), propertyId.toString());

		if (attributes.getControlType().equals(ControlType.PASSWORD)
				|| attributes.getControlType().equals(ControlType.MEMBERLIST))
			return null;

		else {
			field = new LabelCField(attributes).getField();
			BeanItem<Node> bean = new BeanItem<Node>(node);
			Object value = bean.getItemProperty(propertyId).getValue();
			if (attributes.getSpecialDisplay())
				field.setValue(TableUtil.getValueByClass(value,
						TreeHandler.getRootNodeByApplication()));
			else if (value instanceof Date) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
				field.setValue(formatter.format((Date) value));
			} else if (attributes.getControlType() == ControlType.TEXTAREA) {
				if(node instanceof Entity) {
					field = new CodeLabelField(attributes.getLabel());
					field.setValue(value);
				} else {
					field = new TextAreaCField( attributes ).getField();
					field.setReadOnly(true);
					field.setHeight("600px");
					field.addStyleName("prettyprint");
					field.addStyleName("lang-java");
				}
			} else if (value instanceof Date) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
				field.setValue(formatter.format((Date) value));
			} else
				field.setValue(value);
		}

		if (field != null
				&& com.fantasystep.component.panel.Listener.class
						.isAssignableFrom(field.getClass()))
			this.listeners
					.add((com.fantasystep.component.panel.Listener) field);

		return field;
	}

	@Override
	protected void initDisplay() {
		addHeaderTitle(LabelUtil.getDomainLabel(node.getClass()),
				CSSUtil.BLACK_FEATURED_TITLE);

		if (updateMode.equals(Mode.WITH_UPDATE_BUTTON)) {
			updateButton = new Button(String.format("%s %s",
					LocalizationHandler.get(LabelUtil.LABEL_UPDATE),
					LabelUtil.getDomainLabel(getNode().getClass())),
					new ClickListener() {
						private static final long serialVersionUID = -3145796741413370040L;

						@Override
						public void buttonClick(ClickEvent event) {
							PopUpModel popup = new PopUpModel();

							if (!(node instanceof MemberHolder))
								popup.setWidth(LayoutUtil.PERCENTAGE_MEDIUM_SIZE
										+ "%");

							SingleNodeCContainer container = new SingleNodeCContainer(
									getNode(), action, FormMode.TAB);
							Panel panel = LayoutUtil.addScrollablePanel(
									container, true);
							if (MemberHolder.class.isAssignableFrom(node
									.getClass())) {
								panel.getContent().setWidth(
										LayoutUtil.CONST_MEMBERS_SIZE + "%");
								popup.setWidth(LayoutUtil.PERCENTAGE_LARGE_SIZE
										+ "%");
							} else
								popup.setWidth(LayoutUtil.PERCENTAGE_MEDIUM_SIZE
										+ "%");

							popup.setContent(panel);
							popup.setCaption(String.format("%s", LabelUtil
									.getDomainLabel(getNode().getClass())));
							UI.getCurrent().addWindow(popup);
						}
					});

			if (!CNodeUtil.getPermissionDescriptor(node.getClass())
					.hasUpdatePermission())
				updateButton.setEnabled(false);

			updateButton.setIcon(IconUtil.getMediumSizeIcon(node));
			updateButton.setStyleName(BaseTheme.BUTTON_LINK);

			HorizontalLayout toplayout = new HorizontalLayout();
			toplayout.setMargin(true);
			toplayout.setStyleName(CSSUtil.BUTTONS_BLOCK);
			toplayout.addComponent(updateButton);
			addHeader(toplayout, Alignment.TOP_LEFT);
		}

		addBodyComponent(getForm(), Alignment.TOP_LEFT);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initItemDataSource(Item newDataSource) {
		if (newDataSource != null) {
			getForm().setItemDataSource(newDataSource, uiFields);
			setReadOnly(false);
			getForm().getFooter().setVisible(true);
		} else {
			getForm().setItemDataSource(null);
			getForm().getFooter().setVisible(false);
		}
	}

	@Override
	public void notify(NodeEvent event) {
		if (event.getNode().equals(getNode())) {
			this.node = event.getNode();
			resetContents();

		} else if (UINodeUtil.hasPrimaryAnnotation(event.getNode().getClass())) {
			this.node = ((ConcreteNodeEvent) event).getTargetNode();
			resetContents();
		}
	}

	private void resetContents() {
		getBodyContainer().removeAllComponents();
		getHeader().removeAllComponents();
		addHeaderTitle(LabelUtil.getDomainLabel(node.getClass()),
				CSSUtil.BLACK_FEATURED_TITLE);
		this.form = null;
		getForm();
		setPrimaryFields(this.node);
	}

	@SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
	private void setPrimaryFields(Node node) {
		List<Node> list = UINodeUtil.getPrimaryNodes(node);
		if (!list.isEmpty()) {
			Label label = new Label(
					CSSUtil.wrapStyle(LocalizationHandler
							.get(LabelUtil.LABEL_PRIMARY_PROPERTIES),
							CSSUtil.GRAY_NORMAL_TITLE), ContentMode.HTML);
			getForm().getLayout().addComponent(label);

			for (Node n : list) {
				Field f = new LabelCField(null).getField();
				f.setCaption(UINodeUtil.getPrimaryFieldLabel(n));

				f.setValue(CSSUtil.wrapStyle(LayoutUtil.wrapFieldsLayout(n),
						CSSUtil.GREEN_ITALIC_TEXT));
				this.form.addField(n.getId(), f); // first parameter is FieldID
			}
		}
	}
	private class CodeLabelField extends AbstractCustomField {
		
		private static final long serialVersionUID = 709374797531226244L;
		
		private Label lblField = new CodeLabel();

		public CodeLabelField(String caption) {
			super(caption, String.class);
			lblField.setImmediate(true);
			setCompositionRoot(lblField);
		}

		@Override
		public Object getValue() {
			return lblField.getValue();
		}

		@Override
		public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
			lblField.setValue(newValue.toString());
		}
	}
}
