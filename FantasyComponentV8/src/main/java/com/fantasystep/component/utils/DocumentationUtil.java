package com.fantasystep.component.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.fantasystep.utils.DownloadUtil;

public class DocumentationUtil
{

	// TODO make this configurable
	private static String	DOCUMENTATION_SERVER_URL	= "http://172.24.1.23/mediawiki/index.php/%s?action=render";

	public static String getDocumentation( String documentationReference )
	{
		URL url = null;
		try
		{
			url = new URL( String.format( DOCUMENTATION_SERVER_URL, documentationReference ) );
			return DownloadUtil.downloadURL( url );
		} catch( MalformedURLException e2 )
		{
			e2.printStackTrace();
		} catch( IOException e )
		{
			e.printStackTrace();
		}
		return "";
	}

}
