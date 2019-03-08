package com.fantasystep.panel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.vaadin.artur.icepush.ICEPush;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.component.common.ItemClickHandler;
import com.fantasystep.component.common.Login;
import com.fantasystep.component.layout.SimpleContainerLayout;
import com.fantasystep.component.layout.SplitContainerLayout;
import com.fantasystep.component.menu.NodeMenu;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.tree.MenuTree;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.EnvironmentUtil;
import com.fantasystep.component.utils.IconUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.LayoutUtil;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.domain.Node;
import com.fantasystep.domain.Permission;
import com.fantasystep.domain.User;
import com.fantasystep.form.CFormFactory;
import com.fantasystep.form.TabCForm;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.persistence.exception.InvalidCredentialsException;
import com.fantasystep.utils.Environment;
import com.fantasystep.utils.JSON2NodeUtil;
import com.fantasystep.utils.NodeClassUtil;
import com.fantasystep.utils.NodeUtil;
import com.fantasystep.utils.PermissionUtil;
import com.fantasystep.utils.PermissionUtil.PermissionDescriptor;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeTargetDetails;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class UIHandler {

	private static Logger logger = LoggerFactory.getLogger(UIHandler.class);
	
	private Button logout = null;
	private SplitContainerLayout mainLayout;
	// private ICEPush pusher = new ICEPush();
	private TabCForm tabSheet = new TabCForm();
	private MenuTree tree = null;

	private void buildLeftTree() {
		if (tree == null) {
			tree = new MenuTree(TreeHandler.getRootNode());
			ItemClickHandler itemh = new ItemClickHandler(tree, new NodeMenu(
					tree)) {
				private static final long serialVersionUID = 2772517174141104129L;

				@Override
				protected void handlLeftClick(List<Node> selectedNodes) {

					if (selectedNodes.size() == 0)
						return;

					if (selectedNodes.get(0)
							.equals(TreeHandler.getTargetNode()))
						return;

					tabSheet.setEnabled(selectedNodes.size() == 1);

					if (selectedNodes.size() == 1) {
						Node node = NodeUtil.getNode(selectedNodes.get(0)
								.getId(), TreeHandler.getRootNode());
						if(node.getSerializationNode() != null){
							JSONObject json = new JSONObject(node.getSerializationNode());
							node = JSON2NodeUtil.json2Node(node.getSerializationNode(), NodeClassUtil.getDynamicEntityClassByFullName(json.getString("type")));
						}
						VaadinService.getCurrentRequest().getWrappedSession()
								.setAttribute(Node.TARGET_NODE_PROPERTY, node);

						setTabContents();
					}
				}
			};
			tree.setHandler(itemh);
			tree.setDragMode(TreeDragMode.NODE);
			tree.setDropHandler(new DropHandler() {
				private static final long serialVersionUID = -5822108064624815402L;

				@Override
				public void drop(DragAndDropEvent dropEvent) {
					Transferable t = dropEvent.getTransferable();
					if (t.getSourceComponent() != tree
							|| !(t instanceof DataBoundTransferable))
						return;

					TreeTargetDetails dropData = ((TreeTargetDetails) dropEvent
							.getTargetDetails());

					Object sourceItemId = ((DataBoundTransferable) t)
							.getItemId();
					Object targetItemId = dropData.getItemIdOver();

					VerticalDropLocation location = dropData.getDropLocation();

					moveNode(sourceItemId, targetItemId, location);
				}

				@Override
				public AcceptCriterion getAcceptCriterion() {
					return new ServerSideCriterion() {
						private static final long serialVersionUID = 7657050128400743798L;

						@Override
						public boolean accept(DragAndDropEvent dragEvent) {
							DataBoundTransferable dragged = (DataBoundTransferable) dragEvent
									.getTransferable();
							if (dragged.getSourceComponent() != tree)
								return false;

							AbstractSelectTargetDetails target = (AbstractSelectTargetDetails) dragEvent
									.getTargetDetails();
							if (!VerticalDropLocation.MIDDLE.equals(target
									.getDropLocation()))
								return false;

							Node sourceNode = (Node) dragged.getItemId();
							Node targetNode = (Node) target.getItemIdOver();

							return isValidParent(sourceNode, targetNode);
						}
					};
				}

				private boolean isValidParent(Node source, Node target) {
					if (source.isDeleted() || target.isDeleted())
						return false;
					if (target.getClass().getAnnotation(DomainClass.class)
							.isPropertyNode())
						return false;
					if (!NodeUtil.getValidChildren(target.getClass()).contains(
							source.getClass()))
						return false;
					// if( source instanceof User && ( (User) source
					// ).hasAccount( WindowsAccount2008.class ) )
					// if( target.getChildren( AbstractWindowsAccount.class
					// ).isEmpty() )
					// return false;
					User user = TreeHandler.getUserNode();
					Node root = TreeHandler.getRootNode();
					Node sourceParent = NodeUtil.getNode(source.getParentId(),
							root);

					/**
					 * This limits to move user to a place which is out of scope
					 * or out of permissions scope.
					 */
					if (source instanceof User) {
						User u = (User) source;
						Node administrationNode = NodeUtil.getNode(
								u.getAdminNode(), root);
						if (administrationNode == null)
							return false;
						if (NodeUtil.getParents(administrationNode, root)
								.contains(target))
							return false;
						for (Node n : u.getChildren(Permission.class)) {
							Permission p = (Permission) n;
							Node tn = NodeUtil.getNode(p.getTargetNodeId(),
									root);
							if (NodeUtil.getParents(tn, root).contains(target))
								return false;
						}
					}
					if (NodeUtil.getChildren(source, root).contains(target))
						return false;

					PermissionDescriptor sourcePerm = PermissionUtil
							.getPermissionDescriptor(user, sourceParent,
									source.getClass(), root);
					PermissionDescriptor targetPerm = PermissionUtil
							.getPermissionDescriptor(user, target,
									source.getClass(), root);
					if (!(sourcePerm.hasUpdatePermission()
							&& sourcePerm.hasDeletePermission() && sourcePerm
							.hasDestroyPermission()))
						return false;
					else if (!(targetPerm.hasUpdatePermission() && targetPerm
							.hasInsertPermission()))
						return false;
					return true;
				}

				private void moveNode(Object sourceItemId, Object targetItemId,
						VerticalDropLocation location) {
					HierarchicalContainer container = (HierarchicalContainer) tree
							.getContainerDataSource();

					Node sourceNode = (Node) sourceItemId;
					Node newParent = (Node) targetItemId;

					if (!container.areChildrenAllowed(newParent))
						container.setChildrenAllowed(newParent, true);

					UUID oldParentId = sourceNode.getParentId();
					if (newParent.getId() == null) {
						TreeHandler.handleTreeException(new Exception());
						return;
					} else
						sourceNode.setParentId(newParent.getId());

					synchronized (CApplication.getCurrent()) {
						try {
							TreeHandler.get().modifyTreeNode(sourceNode);
						} catch (Exception e) {
							TreeHandler.handleTreeException(e);
						} finally {
							sourceNode.setParentId(oldParentId);
						}
					}
				}
			});

			mainLayout.addLeftComponent(tree.getTreeWithSearch(),
					Alignment.MIDDLE_LEFT);

			((CApplication) CApplication.getCurrent()).getEventHandler()
					.addListener(tree);
		}

		/** disabled temporarily **/
		// pushLayout( getCreateButton() );

		mainLayout.addLeftComponent(tree, Alignment.TOP_LEFT);
	}

	public Window buildLoginLayout() {
		final Window loginWindow = initStyleWindow();
		Login login = new Login() {
			private static final long serialVersionUID = 3127002911184924007L;

			@Override
			protected void login(String username, String password) {
				try {
					UUID uuid = TreeHandler.get().authenticate(username,
							password);
					if (uuid != null) {
						Map<String, List<String>> headers = new HashMap<String, List<String>>();
						headers.put(EnvironmentUtil.SESSION_KEY,
								Collections.singletonList(uuid.toString()));
						((BindingProvider) TreeHandler.get())
								.getRequestContext().put(
										MessageContext.HTTP_REQUEST_HEADERS,
										headers);
					}

					TreeHandler.updateTreeFromStorage(true);
					if (((CApplication) CApplication.getCurrent())
							.isDeveloperMode())
						buildDebugger();
					else
						showMainLayout();
					loginWindow.close();
				} catch (InvalidCredentialsException e) {
					Notification.show(String.format(
							LocalizationHandler.get(LabelUtil.LABEL_FAILED),
							"!"), LocalizationHandler
							.get(LabelUtil.LABEL_USER_PASSWORD_INCORRECT),
							Type.WARNING_MESSAGE);
				}
			}
		};
		loginWindow.setContent(LayoutUtil.addContainerLayout(login,
				Alignment.MIDDLE_CENTER));
		return loginWindow;
	}

	private void browserInfo() {
		WebBrowser browser = UI.getCurrent().getPage().getWebBrowser();
		logger.info(String.format("Connected using %s",
				browser.getBrowserApplication()));
	}

	private void buildMainLayout() {
		mainLayout = new SplitContainerLayout();
		UI.getCurrent().setContent(mainLayout);

		UI.getCurrent()
				.getPage()
				.addBrowserWindowResizeListener(
						new BrowserWindowResizeListener() {

							private static final long serialVersionUID = 8320226673080750680L;

							@Override
							public void browserWindowResized(
									BrowserWindowResizeEvent event) {
								for (Window w : UI.getCurrent().getWindows()) {
									w.setWidth("90%");
									w.setHeight("90%");
									w.center();
									w.setImmediate(true);
								}
							}
						});

		UI.getCurrent().setImmediate(true);
	}

	private void buildRightTab() {
		SimpleContainerLayout slayout = new SimpleContainerLayout();
		// TODO
		// slayout.addComponent(pusher);

		HorizontalLayout hz = new HorizontalLayout();

		hz.addComponent(new Label(String.format("%s %s%s", IconUtil.getRawIcon(
				User.class, IconUtil.SMALL_ICON_SIZE), CSSUtil.wrapStyle(
				TreeHandler.getUserNode().getLabel(),
				CSSUtil.YELLOW_NORMAL_TEXT), CSSUtil.wrapStyle(
				String.format("{%s}&nbsp;&nbsp;&nbsp;", TreeHandler
						.getUserNode().getEmail()),
				CSSUtil.GRAY_STRONG_ITALIC_TEXT)), ContentMode.HTML));
		hz.addComponent(getLogout());

		slayout.addHeader(hz, Alignment.TOP_RIGHT);
		slayout.getHeader().setMargin(new MarginInfo(true, true, false, false));
		slayout.getBodyContainer().setSizeFull();
		tabSheet.setSizeFull();
		slayout.addBodyComponent(tabSheet, Alignment.TOP_LEFT);
		((VerticalLayout) slayout.getBodyContainer()).setMargin(new MarginInfo(
				false, true, false, false));

		((HorizontalSplitPanel) mainLayout.getBodyContainer())
				.setSecondComponent(slayout);

		((CApplication) CApplication.getCurrent()).getEventHandler()
				.addListener(tabSheet);
		setTabContents();
	}

	public Button getLogout() {
		if (logout == null) {
			logout = new Button(LocalizationHandler.get(LabelUtil.LABEL_LOGOUT));
			logout.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 5835149209159661201L;

				@Override
				public void buttonClick(ClickEvent event) {
					UI.getCurrent().setContent(null);
					mainLayout = null;
					tree = null;
					tabSheet = new TabCForm();
					CApplication.getCurrent().close();
					UI.getCurrent().getPage().setLocation("/");
				}
			});
			logout.setStyleName(BaseTheme.BUTTON_LINK);
			logout.addStyleName(CSSUtil.BLACK_NORMAL_UNDERLINE_LINK);
		}
		return logout;
	}
	
	public Window initStyleWindow() {
		Window mainWindow = new Window(
				LocalizationHandler.get(LabelUtil.LABEL_KLOUD_APPLICATION)) {
			private static final long serialVersionUID = 2514109658590948095L;

			@Override
			public void paintContent(PaintTarget target) throws PaintException {
				browserInfo();
				super.paintContent(target);
			};
		};
		mainWindow.setWidth("400px");
		mainWindow.setHeight("300px");
		mainWindow.center();
		mainWindow.setClosable(false);
		mainWindow.setResizable(false);
		UI.getCurrent().addWindow(mainWindow);

		HttpServletRequest httpServletRequest = ((VaadinServletRequest)VaadinService.getCurrentRequest()).getHttpServletRequest();
		String requestUrl = httpServletRequest.getRequestURL().toString();
		Environment
				.addProperty(EnvironmentUtil.APPLICATION_BASE_PATH, String
						.format("%s%s%s/", requestUrl, "VAADIN/themes/", CApplication
								.getCurrent().getTheme()));
		
		CApplication.getCurrent().setTheme("mytheme");
		return mainWindow;
	}

	public void push() {
		// this.pusher.push();
	}

	private void setTabContents() {
		Map<String, String> labelMap = new HashMap<String, String>();
		Map<String, AbstractCContainer> map = CFormFactory
				.getTabsFromTargetNode(TreeHandler.getRootNode(),
						TreeHandler.getTargetNode(), Action.UPDATE, labelMap);
		tabSheet.setContainersMap(map);
		tabSheet.setFormLabel(labelMap);
		tabSheet.setCurrentPage();
	}

	public void showMainLayout() {
		buildMainLayout();
		buildLeftTree();
		buildRightTab();
	}

	public void buildDebugger() {
		// LoadDebugger deubugger = new LoadDebugger();
		// UI.getCurrent().setContent( deubugger );
	}
}
