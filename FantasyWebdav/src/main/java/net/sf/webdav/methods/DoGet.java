/*
 * Copyright 1999,2004 The Apache Software Foundation.
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
 */
package net.sf.webdav.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.webdav.IMimeTyper;
import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;
import net.sf.webdav.WebdavStatus;
import net.sf.webdav.locking.ResourceLocks;

public class DoGet extends DoHead 
{
    public DoGet(IWebdavStore store, String dftIndexFile, String insteadOf404,ResourceLocks resourceLocks, IMimeTyper mimeTyper,int contentLengthHeader) 
    {
        super(store, dftIndexFile, insteadOf404, resourceLocks, mimeTyper,contentLengthHeader);
    }

    protected void doBody(ITransaction transaction, HttpServletResponse resp,String path) 
    {
        try 
        {
            StoredObject so = _store.getStoredObject(transaction, path, false);
            if (so.isNullResource()) 
            {
                String methodsAllowed = DeterminableMethod.determineMethodsAllowed(so);
                resp.addHeader("Allow", methodsAllowed);
                resp.sendError(WebdavStatus.SC_METHOD_NOT_ALLOWED);
                return;
            }
            
            try{
            	resp.addHeader("checksum", so.getChecksum());
            }catch(Exception ex)
            {
            }
            
            OutputStream out = resp.getOutputStream();
            
            InputStream in = _store.getResourceContent(transaction, path);
            
            try 
            {
            	
                int read = -1;
                byte[] copyBuffer = new byte[BUF_SIZE];
                
                int offset = 0;
                if(rangeHeader > 0 && rangeHeader <= Integer.MAX_VALUE)
                {
                	offset = (int)rangeHeader;
                }
                
                while ((read = in.read(copyBuffer, offset, copyBuffer.length)) != -1) 
                {
                    out.write(copyBuffer, offset, read);
                }
                
            } 
            finally 
            {
                try 
                {
                    in.close();
                } 
                catch (Exception e) 
                {
                }
                
                try 
                {
                    out.flush();
                    out.close();
                } 
                catch (Exception e) 
                {
                }
            }
        } catch (Exception e) {
        }
    }

    
    
	protected void folderBody(ITransaction transaction, String path, HttpServletResponse resp, HttpServletRequest req) throws IOException 
    {
    	String sort = null;
    	try{    		
    		sort = req.getParameter("sort");
    	}catch(Exception e){
    	}
    	
    	if(sort==null)
    		sort = "datedesc";

    	boolean fixpath = !path.endsWith("/");
    	
    	String completePath = req.getContextPath()+req.getServletPath()+getRelativePath(req)+(fixpath ? "/" : "");
    	
    	String sessionKey = "?sessionKey=" + req.getSession().getAttribute("sessionKey").toString();
    	
    	StoredObject so = _store.getStoredObject(transaction, path, false);
    	
        if (so == null) 
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, req
                    .getRequestURI());
        } else 
        {

            if (so.isNullResource()) 
            {
                String methodsAllowed = DeterminableMethod.determineMethodsAllowed(so);
                resp.addHeader("Allow", methodsAllowed);
                
                resp.sendError(WebdavStatus.SC_METHOD_NOT_ALLOWED);
                return;
            }
            
            // content type
            resp.setContentType("text/html; charset=UTF-8");
            
            if (so.isFolder()) {
                // TODO some folder response (for browsers, DAV tools
                // use propfind) in html?
                OutputStream out = resp.getOutputStream();
                String[] childrenArray = _store.getChildrenNames(transaction, path);
                childrenArray = childrenArray == null ? new String[] {} : childrenArray;
                
                // Sort
                List<String> children = Arrays.asList(childrenArray);
                
                TreeMap<String, String> foldersMap = new TreeMap<String, String>();                
                TreeMap<String, String> filesMap = new TreeMap<String, String>();      
                
                // children
                for (String child : children) {
                	StoredObject cso = _store.getStoredObject(transaction, path + "/" + child, false);
                	
                	if (cso.isFolder()) 
                	{
                		String s = "<tr>" +
                				"<td align=\"left\"><a href=\"" + (fixpath ? completePath : "") + child + "/" + sessionKey + "\">/" + child + "</a></td>" +
                				"</tr>\n";
                		
                		if(sort!=null && sort.startsWith("date"))
                		{
                			String k = cso.getLastModified().getTime()+"";
                			while(foldersMap.containsKey(k))
                			{
                				k+="a";
                			}
                			foldersMap.put(k,s);
                		}
                		else if(sort!=null && sort.startsWith("size"))
                		{
                			foldersMap.put(child,s);
                		}
                		else
                		{
                			foldersMap.put(child,s);
                		}
                	} else 
                	{
                		String s = "<tr>" +
                				"<td align=\"left\"><a href=\"" + (fixpath ? completePath : "") + child + sessionKey + "\">" + child +  "</a></td>"+
                				(child.endsWith(".mp4") ? "<td align=\"left\"><video controls><source src=\"" + (fixpath ? completePath : "") + child + sessionKey + "\" type=\"video/mp4\"></video></td>" : "") +
                				"<td width=\"20\"></td>" +
                				"<td align=\"left\"> " + cso.getLastModified().toString() +" </td>" +
                				"<td width=\"20\"></td>" +
                				"<td align=\"left\"> " + getSizeFromBytes(cso.getResourceLength()) +" </td>" +
                				"</tr>\n";
                		
                		if(sort!=null && sort.startsWith("date"))
                		{
                			String k = cso.getLastModified().getTime()+"";
                			while(filesMap.containsKey(k))
                			{
                				k+="a";
                			}
                			filesMap.put(k,s);
                		}
                		else if(sort!=null && sort.startsWith("size"))
                		{
                			String k = cso.getResourceLength()+"";
                			while(filesMap.containsKey(k))
                			{
                				k+="a";
                			}
                			filesMap.put(k,s);
                		}
                		else
                		{
                			filesMap.put(child,s);
                		}
                	}
                }

                //
                StringBuffer childrenTemp = new StringBuffer();
                // html
                childrenTemp.append("<html>\n" +
    								"<head>\n" +
									"<title>Collection: " + path + "</title>\n" +
									"<STYLE TYPE=\"text/css\">\n" +
									"TD{font-family: Courier; font-size: 10pt;}\n"+
									"TH{font-family: Courier; font-size: 10pt;}\n"+
									"</STYLE>\n"+
									"</head>\n" +
									"<body>\n" + 
									"<h3>Collection: " + completePath + "</h3>\n" +
									"<table>\n"+
									"<tr><th align=\"left\"><a href=\"?sort=name"+ (sort.endsWith("desc") ? "asc" : "desc") +"\">Name" + (sort.startsWith("name") ? (sort.endsWith("desc") ? " &or;" : " &and;") : "") + "</a></th>" +
									"<th> </th>" +
									"<th align=\"left\"><a href=\"?sort=date"+ (sort.endsWith("desc") ? "asc" : "desc") +"\">Last modified date" + (sort.startsWith("date") ? (sort.endsWith("desc") ? " &or;" : " &and;") : "") + "</a></th>" +
									"<th> </th>" +
									"<th align=\"left\"><a href=\"?sort=size"+ (sort.endsWith("desc") ? "asc" : "desc") +"\">Size" + (sort.startsWith("size") ? (sort.endsWith("desc") ? " &or;" : " &and;") : "") + "</a></th></tr>\n" +
									"<tr><td align=\"left\"><a href=\"../" + sessionKey + "\">..</a></td></tr>\n"
									);

                String[] folderkeys=new String[foldersMap.size()];   
                foldersMap.keySet().toArray(folderkeys);
                
                String[] fileskeys=new String[filesMap.size()];   
                filesMap.keySet().toArray(fileskeys);
                
                if(sort!=null && sort.endsWith("desc"))
                {
                	Arrays.sort(folderkeys, Collections.reverseOrder(new NaturalOrderComparator()));
                	Arrays.sort(fileskeys, Collections.reverseOrder(new NaturalOrderComparator()));
                }
                else
                {
                	Arrays.sort(folderkeys, new NaturalOrderComparator());
                	Arrays.sort(fileskeys, new NaturalOrderComparator());
                }
                
                
                for (String key:folderkeys) {  
                	childrenTemp.append(foldersMap.get(key));  
                }  
                
                for (String key:fileskeys) {  
                	childrenTemp.append(filesMap.get(key));  
                }
                
                childrenTemp.append("</table>\n" +
                					"</body>\n" +
            						"</html>\n");
                // write
                out.write(childrenTemp.toString().getBytes());
            }
        }
    }

	private String getSizeFromBytes(long resourceLength) 
	{
		String unit = "";
        String speed = "";

        if (resourceLength == 0)
        {
        	speed = "0";
        	unit = "kB";
        }
        else if (resourceLength < 1024)
        {
            speed = "1";
            unit = "kB";
        }
        else if (resourceLength < 1024 * 1024)
        {
            speed = String.valueOf(resourceLength/1024);
            unit = "kB";
        }
        else if(resourceLength < 1024 * 1024 * 1024)
        {
            speed = String.valueOf(resourceLength/(1024 * 1024));
            unit = "MB";
        }
        else
        {
        	speed = String.valueOf(resourceLength/(1024 * 1024 * 1024));
            unit = "GB";
        }

        return speed + " " + unit;
	}
    
    
	public class NaturalOrderComparator implements Comparator<String>
	{
	    int compareRight(String a, String b)
	    {
	        int bias = 0;
	        int ia = 0;
	        int ib = 0;

	        // The longest run of digits wins. That aside, the greatest
	        // value wins, but we can't know that it will until we've scanned
	        // both numbers to know that they have the same magnitude, so we
	        // remember it in BIAS.
	        for (;; ia++, ib++)
	        {
	            char ca = charAt(a, ia);
	            char cb = charAt(b, ib);

	            if (!Character.isDigit(ca) && !Character.isDigit(cb))
	            {
	                return bias;
	            }
	            else if (!Character.isDigit(ca))
	            {
	                return -1;
	            }
	            else if (!Character.isDigit(cb))
	            {
	                return +1;
	            }
	            else if (ca < cb)
	            {
	                if (bias == 0)
	                {
	                    bias = -1;
	                }
	            }
	            else if (ca > cb)
	            {
	                if (bias == 0)
	                    bias = +1;
	            }
	            else if (ca == 0 && cb == 0)
	            {
	                return bias;
	            }
	        }
	    }

	    public int compare(String a, String b)
	    {
	        int ia = 0, ib = 0;
	        int nza = 0, nzb = 0;
	        char ca, cb;
	        int result;

	        while (true)
	        {
	            // only count the number of zeroes leading the last number compared
	            nza = nzb = 0;

	            ca = charAt(a, ia);
	            cb = charAt(b, ib);

	            // skip over leading spaces or zeros
	            while (Character.isSpaceChar(ca) || ca == '0')
	            {
	                if (ca == '0')
	                {
	                    nza++;
	                }
	                else
	                {
	                    // only count consecutive zeroes
	                    nza = 0;
	                }

	                ca = charAt(a, ++ia);
	            }

	            while (Character.isSpaceChar(cb) || cb == '0')
	            {
	                if (cb == '0')
	                {
	                    nzb++;
	                }
	                else
	                {
	                    // only count consecutive zeroes
	                    nzb = 0;
	                }

	                cb = charAt(b, ++ib);
	            }

	            // process run of digits
	            if (Character.isDigit(ca) && Character.isDigit(cb))
	            {
	                if ((result = compareRight(a.substring(ia), b.substring(ib))) != 0)
	                {
	                    return result;
	                }
	            }

	            if (ca == 0 && cb == 0)
	            {
	                // The strings compare the same. Perhaps the caller
	                // will want to call strcmp to break the tie.
	                return nza - nzb;
	            }

	            if (ca < cb)
	            {
	                return -1;
	            }
	            else if (ca > cb)
	            {
	                return +1;
	            }

	            ++ia;
	            ++ib;
	        }
	    }

	    public char charAt(String s, int i)
	    {
	        if (i >= s.length())
	        {
	            return 0;
	        }
	        else
	        {
	            return s.charAt(i);
	        }
	    }
	}
}
