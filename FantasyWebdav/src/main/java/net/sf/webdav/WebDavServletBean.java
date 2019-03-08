package net.sf.webdav;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fantasystep.persistence.client.TreeManagerWSService;

import net.sf.webdav.exceptions.UnauthenticatedException;
import net.sf.webdav.exceptions.WebdavException;
import net.sf.webdav.fromcatalina.MD5Encoder;
import net.sf.webdav.locking.ResourceLocks;
import net.sf.webdav.methods.DoCopy;
import net.sf.webdav.methods.DoDelete;
import net.sf.webdav.methods.DoGet;
import net.sf.webdav.methods.DoHead;
import net.sf.webdav.methods.DoLock;
import net.sf.webdav.methods.DoMkcol;
import net.sf.webdav.methods.DoMove;
import net.sf.webdav.methods.DoNotImplemented;
import net.sf.webdav.methods.DoOptions;
import net.sf.webdav.methods.DoPropfind;
import net.sf.webdav.methods.DoProppatch;
import net.sf.webdav.methods.DoPut;
import net.sf.webdav.methods.DoUnlock;

public class WebDavServletBean extends HttpServlet {

	private static final long serialVersionUID = -5658470433298338646L;

	/**
     * MD5 message digest provider.
     */
    protected static MessageDigest MD5_HELPER;

    /**
     * The MD5 helper object for this class.
     */
    protected static final MD5Encoder MD5_ENCODER = new MD5Encoder();
    
	private static TreeManagerWSService service = null;

    private static final boolean READ_ONLY = false;
    private ResourceLocks _resLocks;
    private IWebdavStore _store;
    private HashMap<String, IMethodExecutor> _methodMap = new HashMap<String, IMethodExecutor>();

    public WebDavServletBean() {
        _resLocks = new ResourceLocks();

        try {
            MD5_HELPER = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException();
        }
    }

    public void init(IWebdavStore store, String dftIndexFile,
            String insteadOf404, int nocontentLenghHeaders,
            boolean lazyFolderCreationOnPut) throws ServletException {
        
        _store = store;

        IMimeTyper mimeTyper = new IMimeTyper() {
            public String getMimeType(String path) {
                return getServletContext().getMimeType(path);
            }
        };

        register("GET", new DoGet(store, dftIndexFile, insteadOf404, _resLocks,
                mimeTyper, nocontentLenghHeaders));
        register("HEAD", new DoHead(store, dftIndexFile, insteadOf404,
                _resLocks, mimeTyper, nocontentLenghHeaders));
        DoDelete doDelete = (DoDelete) register("DELETE", new DoDelete(store,
                _resLocks, READ_ONLY));
        DoCopy doCopy = (DoCopy) register("COPY", new DoCopy(store, _resLocks,
                doDelete, READ_ONLY));
        register("LOCK", new DoLock(store, _resLocks, READ_ONLY));
        register("UNLOCK", new DoUnlock(store, _resLocks, READ_ONLY));
        register("MOVE", new DoMove(_resLocks, doDelete, doCopy, READ_ONLY));
        register("MKCOL", new DoMkcol(store, _resLocks, READ_ONLY));
        register("OPTIONS", new DoOptions(store, _resLocks));
        register("PUT", new DoPut(store, _resLocks, READ_ONLY,
                lazyFolderCreationOnPut));
        register("PROPFIND", new DoPropfind(store, _resLocks, mimeTyper));
        register("PROPPATCH", new DoProppatch(store, _resLocks, READ_ONLY));
        register("*NO*IMPL*", new DoNotImplemented(READ_ONLY));
    }

    private IMethodExecutor register(String methodName, IMethodExecutor method) {
        _methodMap.put(methodName, method);
        return method;
    }
    
    private static TreeManagerWSService getService() {
    	if (WebDavServletBean.service == null) {
    		URL url = null;
    		try {
    			url = new URL("http://192.168.99.100:8080/persistence/cxf/TreeManagerService?wsdl");
    		} catch (MalformedURLException e) {
    			e.printStackTrace();
    		}
    		WebDavServletBean.service = new TreeManagerWSService(url);
    	}
    	return WebDavServletBean.service;
    }

    /**
     * Handles the special WebDAV methods.
     */
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String methodName = req.getMethod();
        ITransaction transaction = null;
        boolean needRollback = false;

        try {
        	String sessionKey = req.getParameter("sessionKey");
        	if(sessionKey == null || getService().getTreeManagerPort().authenticateWithSessionKey(UUID.fromString(sessionKey)) == null)
        		throw new UnauthenticatedException("Unrecognized session key");
        	req.getSession().setAttribute("sessionKey", sessionKey);
        	System.out.println("sessionkey " + sessionKey);
            Principal userPrincipal = req.getUserPrincipal();
                        
            transaction = _store.begin(userPrincipal);
            needRollback = true;
            _store.checkAuthentication(transaction);
            resp.setStatus(WebdavStatus.SC_OK);

            try {
                IMethodExecutor methodExecutor = (IMethodExecutor) _methodMap
                        .get(methodName);
                if (methodExecutor == null) {
                    methodExecutor = (IMethodExecutor) _methodMap
                            .get("*NO*IMPL*");
                }

                methodExecutor.execute(transaction, req, resp);

                _store.commit(transaction);
                needRollback = false;
            } catch (IOException e) {
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                e.printStackTrace(pw);
                resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
                _store.rollback(transaction);
                throw new ServletException(e);
            }
        } catch (UnauthenticatedException e) {
            resp.sendError(WebdavStatus.SC_FORBIDDEN);
        } catch (WebdavException e) {
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            throw new ServletException(e);
        } catch (Exception e) {
        	e.printStackTrace();
            resp.sendError(WebdavStatus.SC_NOT_FOUND);
        } finally {
            if (needRollback)
                _store.rollback(transaction);
        }

    }

//    private void debugRequest(String methodName, HttpServletRequest req) {
//
//        Enumeration<?> e = req.getHeaderNames();
//        while (e.hasMoreElements()) {
//            String s = (String) e.nextElement();
//        }
//        e = req.getAttributeNames();
//        while (e.hasMoreElements()) {
//            String s = (String) e.nextElement();            
//        }
//        e = req.getParameterNames();
//        while (e.hasMoreElements()) {
//            String s = (String) e.nextElement();            
//        }
//    }

}
