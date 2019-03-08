/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.sf.webdav;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.webdav.exceptions.WebdavException;

/**
 * Reference Implementation of WebdavStore
 * 
 * @author joa
 * @author re
 */
public class LocalFileSystemStore implements IWebdavStore {

    //private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory
    //        .getLogger(LocalFileSystemStore.class);

    private static int BUF_SIZE = 65536;

    private File _root = null;

    public LocalFileSystemStore(File root) {
        _root = root;
    }

    public ITransaction begin(Principal principal) throws WebdavException {
        
        if (!_root.exists()) {
            if (!_root.mkdirs()) {
                throw new WebdavException("root path: "
                        + _root.getAbsolutePath()
                        + " does not exist and could not be created");
            }
        }
        return null;
    }

    public void checkAuthentication(ITransaction transaction)
            throws SecurityException {
        
        // checkAuthentication- not yet implemented!      

    }

    public void commit(ITransaction transaction) throws WebdavException {
        
        // commit - not yet implemented!
    }

    public void rollback(ITransaction transaction) throws WebdavException {
        
        // rollback - not yet implemented!      

    }

    public void createFolder(ITransaction transaction, String uri)
            throws WebdavException {

        File file = new File(_root, uri);
        if (!file.mkdir())
            throw new WebdavException("cannot create folder: " + uri);
    }

    public void createResource(ITransaction transaction, String uri)
            throws WebdavException {

        File file = new File(_root, uri);

        try {
            if (!file.createNewFile())
                throw new WebdavException("cannot create file: " + uri);
        } catch (IOException e) {
            throw new WebdavException(e);
        }
    }

    public long setResourceContent(ITransaction transaction, String uri,
            InputStream is, String contentType, String characterEncoding)
            throws WebdavException {

        File file = new File(_root, uri);
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(
                    file), BUF_SIZE);
            try {
                int read;
                byte[] copyBuffer = new byte[BUF_SIZE];

                while ((read = is.read(copyBuffer, 0, copyBuffer.length)) != -1) {
                    os.write(copyBuffer, 0, read);
                }
            } finally {
                try {
                    is.close();
                } finally {
                    os.close();
                }
            }
        } catch (IOException e) {

            throw new WebdavException(e);
        }
        long length = -1;

        try {
            length = file.length();
        } catch (SecurityException e) {
            
            throw new WebdavException(e);
        }

        return length;
    }
    
    public boolean checkAccess(String toCheck) {
        return true;
    }

    public String[] getChildrenNames(ITransaction transaction, String uri)
            throws WebdavException {
        
        boolean subTreeAccessGranted = false;
        
        if ( !uri.equals("/") && !checkAccess(uri) )
            return new String[] { "Access Denied" };
            
        else if ( checkAccess(uri) )
            subTreeAccessGranted = true;

        File file = new File(_root, uri);
        String[] childrenNames = null;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            List<String> childList = new ArrayList<String>();
            String name = null;
            for (int i = 0; i < children.length; i++) {
        		children[i] = removeIllegalCharacters(children[i]);
                
            	name = children[i].getName();
            	
                if (subTreeAccessGranted || checkAccess(name))
                    childList.add(name);
            }
            childrenNames = new String[childList.size()];
            childrenNames = (String[]) childList.toArray(childrenNames);
        }
        return childrenNames;
    }

    public void removeObject(ITransaction transaction, String uri)
            throws WebdavException {
        File file = new File(_root, uri);
        boolean success = file.delete();

        if (!success) {
            throw new WebdavException("cannot delete object: " + uri);
        }

    }

    public InputStream getResourceContent(ITransaction transaction, String uri)
            throws WebdavException {

        File file = new File(_root, uri);

        InputStream in;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
        } catch (IOException e) {

            throw new WebdavException(e);
        }
        return in;
    }

    public long getResourceLength(ITransaction transaction, String uri)
            throws WebdavException {

        File file = new File(_root, uri);
        return file.length();
    }

    public StoredObject getStoredObject(ITransaction transaction, String uri,boolean useCheckSum) {
        
        StoredObject so = null;

        if ( !checkAccess(uri) && !uri.equals("/") )
            return so;
        
        File file = new File(_root, uri);
        
        if (file.exists()) {
        	file = removeIllegalCharacters(file);

            so = new StoredObject();
            so.setFolder(file.isDirectory());
            so.setLastModified(new Date(file.lastModified()));
            so.setCreationDate(new Date(file.lastModified()));
            
            try{
            	so.setResourceLength(file.length());
            }catch (Exception e){
            	so.setResourceLength(-1);
            }
            if(useCheckSum)
            {
	            try {
					so.setChecksum(getMD5Checksum(file.getAbsolutePath()));
				} catch (Exception e) {
					so.setChecksum(null);
				}
            }
        }

        return so;
    }
    
    public static String getMD5Checksum(String filename) throws Exception 
    {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i=0; i < b.length; i++) 
        {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        
        return result;
    }
    
    public static byte[] createChecksum(String filename) throws Exception
    {
        InputStream fis =  new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do 
        {
            numRead = fis.read(buffer);
            if (numRead > 0) 
            {
                complete.update(buffer, 0, numRead);
            }
        } 
        while (numRead != -1);

        fis.close();
        return complete.digest();
    }
    
    private File removeIllegalCharacters(File file) {    	    
    	String regex = "[^a-zA-Z_0-9-+./:]";

    	try {
    		String newname = file.getName().replaceAll(" ", "_")
    				.replaceAll("�", "A")
    				.replaceAll("�", "A")
    				.replaceAll("�", "o")
    				.replaceAll("�", "a")
    				.replaceAll("�", "a")
    				.replaceAll("�", "o")
    				.replaceAll(regex,"");
    		
    		if(!newname.equals(file.getName()))
    		{
    			File newfile = new File(file.getParent(), newname);
    			if(file.renameTo(newfile))
    				return newfile;
    		}
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return file;
    }
}
