package com.fantasystep.component.field.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.common.PopUpModel;
import com.fantasystep.component.field.AbstractCField;
import com.fantasystep.component.field.custom.AbstractCustomField;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.helper.VolatileFile;
import com.fantasystep.utils.Environment;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

class FileField extends AbstractCustomField
{
	public enum FileType
	{
		ASF, AVI, FLV, GIF, HTML, JPEG, JPG, LINK, MOV, MP3, MPG, PDF, PNG, SWF, TEXT, WMV, XML
	}

	private static final long	serialVersionUID	= 2762643045927425974L;

	// /**
	// * This class creates a PDF with the iText library. This class implements the StreamSource interface which defines
	// * the getStream method.
	// */
	// @SuppressWarnings("serial")
	// class Pdf implements StreamSource
	// {
	// private final ByteArrayOutputStream os;
	//
	// public Pdf()
	// {
	//
	// os = new ByteArrayOutputStream();
	// Document document = null;
	// try
	// {
	// document = new Document( PageSize.A4, 50, 50, 50, 50 );
	// PdfWriter.getInstance( document, os );
	// document.open();
	//
	// document.add( new Paragraph( "This is some content for the sample PDF!" ) );
	// } catch( Exception e )
	// {
	// e.printStackTrace();
	// } finally
	// {
	// if( document != null )
	// {
	// document.close();
	// }
	// }
	// }
	//
	// @Override
	// public InputStream getStream()
	// {
	// // Here we return the pdf contents as a byte-array
	// try
	// {
	// return new ByteArrayInputStream( getBytesFromFile( uploadedFile ) );
	// } catch( IOException e )
	// {
	// e.printStackTrace();
	// }
	// return null;
	// }
	// }

	private static String bytesToHexString( byte[] src )
	{
		StringBuilder stringBuilder = new StringBuilder();
		if( src == null || src.length <= 0 )
		{
			return null;
		}
		for( int i = 0; i < src.length; i++ )
		{
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString( v );
			if( hv.length() < 2 )
			{
				stringBuilder.append( 0 );
			}
			stringBuilder.append( hv );
		}
		return stringBuilder.toString();
	}

	// Returns the contents of the file in a byte array.
	public static byte[] getBytesFromFile( File file ) throws IOException
	{
		InputStream is = new FileInputStream( file );

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if( length > Integer.MAX_VALUE )
		{
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while( offset < bytes.length && ( numRead = is.read( bytes, offset, bytes.length - offset ) ) >= 0 )
		{
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if( offset < bytes.length )
		{
			is.close();
			throw new IOException( "Could not completely read file " + file.getName() );
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	private FileUploader	fu;

	private VerticalLayout	layout			= new VerticalLayout();

	private File			uploadedFile	= null;

	private VolatileFile	volatileFile	= null;

	@SuppressWarnings("serial")
	public FileField( String label, String pathname, Object volatileFile, boolean enablePreview )
	{
		super( label, String.class );
		this.volatileFile = (VolatileFile) volatileFile;
		Button button = null;

		if( enablePreview )
			button = new Button( LocalizationHandler.get( "LABEL_PREVIEW" ), new Button.ClickListener()
			{
				@Override
				public void buttonClick( ClickEvent event )
				{
					if( !FileField.this.volatileFile.getFile().exists() )
						FileField.this.volatileFile.retrieve();

					File file = FileField.this.volatileFile.getFile();
					PopUpModel popup = new PopUpModel( LocalizationHandler.get( "LABEL_PREVIEW" ) );
					Panel panel = new Panel();
					FileField.this.volatileFile.setName( file.getName() );

					if( uploadedFile != null )
					{
						Embedded egif = new Embedded( null, new ExternalResource( "file:/" + uploadedFile.getAbsolutePath() + ".gif" ) );
						egif.setMimeType( "image/gif" );
						egif.setParameter( "allowFullScreen", "true" );
						panel.setContent( egif );
					} else

						switch( getFileType( file ) )
						{
							case GIF:
								Embedded egif = new Embedded( null, new ExternalResource( FileField.this.volatileFile.getUrl().toString() + ".gif" ) );
								egif.setMimeType( "image/gif" );
								egif.setParameter( "allowFullScreen", "true" );
								panel.setContent( egif );
								break;
							case JPEG:
								Embedded egpeg = new Embedded( null, new ExternalResource( FileField.this.volatileFile.getUrl().toString() + ".jpg" ) );
								egpeg.setMimeType( "image/jpeg" );
								egpeg.setParameter( "allowFullScreen", "true" );
								panel.setContent( egpeg );
								break;
							case PNG:
								Embedded ep = new Embedded( null, new ExternalResource( FileField.this.volatileFile.getUrl() + ".png" ) );
								ep.setMimeType( "image/png" );
								ep.setParameter( "allowFullScreen", "true" );
								panel.setContent( ep );
								break;
							case TEXT:
								Embedded eswf = new Embedded( null, new ExternalResource( FileField.this.volatileFile.getUrl().toString() + ".mp3" ) );
								eswf.setMimeType( "application/x-shockwave-flash" );
								eswf.setParameter( "allowFullScreen", "true" );
								panel.setContent( eswf );
								break;
							case LINK:
								Embedded e2 = new Embedded( null, new ExternalResource( "http://www.youtube.com/v/meXvxkn1Y_8&hl=en_US&fs=1&" ) );
								e2.setMimeType( "application/x-shockwave-flash" );
								e2.setParameter( "allowFullScreen", "true" );
								e2.setWidth( "320px" );
								e2.setHeight( "265px" );
								panel.setContent( e2 );
								break;
							case MP3:
								Embedded eMp3 = new Embedded( null, new ExternalResource( FileField.this.volatileFile.getUrl() + ".mp3" ) );
								eMp3.setMimeType( "audio/mpeg" );
								eMp3.setParameter( "allowFullScreen", "true" );
								panel.setContent( eMp3 );
								// SoundPlayer player = new SoundPlayer();
								break;
							case XML:
								Embedded eXml = new Embedded( null, new ExternalResource( FileField.this.volatileFile.getUrl() + ".xml" ) );
								eXml.setMimeType( "application/xml" );
								eXml.setParameter( "allowFullScreen", "true" );
								panel.setContent( eXml );
								break;
							case HTML:
								Embedded eHtml = new Embedded( null, new ExternalResource( FileField.this.volatileFile.getUrl() + ".html" ) );
								eHtml.setMimeType( "application/html" );
								eHtml.setParameter( "allowFullScreen", "true" );
								panel.setContent( eHtml );
								break;
							case PDF:
								// Embedded epdf = new Embedded( "PDF from a theme resource", new FileResource(
								// uploadedFile,
								// FileField.this.getWindow().getApplication() ) );
								// panel.addComponent( epdf );

								// Embedded epdf = new Embedded( null, new ExternalResource(
								// FileField.this.volatileFile.getUrl().toString() + ".pdf" ) );
								// epdf.setMimeType( "application/pdf" );
								// epdf.setParameter( "allowFullScreen", "true" );
								// epdf.setWidth( "320px" );
								// epdf.setHeight( "265px" );
								// panel.addComponent( epdf );

								// Embedded epdf = new Embedded();
								// epdf.setSizeFull();
								// epdf.setType( Embedded.TYPE_BROWSER );
								// StreamResource resource = new StreamResource( new Pdf(), uploadedFile.getName(),
								// FileField.this.getWindow().getApplication() );
								// resource.setMIMEType( "application/pdf" );
								// epdf.setSource( resource );
								// panel.addComponent( epdf );

								break;
							case FLV:
								Embedded eFlv = new Embedded( null, new ExternalResource( FileField.this.volatileFile.getUrl() + ".flv" ) );
								eFlv.setMimeType( "application/x-shockwave-flash" );
								eFlv.setParameter( "allowFullScreen", "true" );
								panel.setContent( eFlv );
								break;
							case SWF:
								Embedded eSwf = new Embedded( null, new ExternalResource( FileField.this.volatileFile.getUrl() + ".swf" ) );
								eSwf.setMimeType( "application/x-shockwave-flash" );
								eSwf.setParameter( "allowFullScreen", "true" );
								eSwf.setWidth( "480px" );
								eSwf.setHeight( "360px" );
								panel.setContent( eSwf );
								break;
							case AVI:
							case MOV:
								break;
							default:
								break;
						}

					popup.add( panel );
					// layout.addComponent( panel );
					UI.getCurrent().setImmediate( true );
					UI.getCurrent().addWindow( popup );
				}
			} );

		fu = new FileUploader( new File( pathname ), this.volatileFile.getId().toString() )
		{
			@Override
			public void updateProgress( long readBytes, long contentLength )
			{
			}

			@Override
			public void uploadFailed( FailedEvent event )
			{
			}

			@Override
			public void uploadFinished( FinishedEvent event )
			{
			}

			@Override
			public void uploadStarted( StartedEvent event )
			{
			}

			@Override
			public void uploadSucceeded( SucceededEvent event )
			{
				setUploadedFile( new File( String.format( "%s/%s", getTargetDirectory().toString(), this.fileNameId ) ) );
			}

		};

		layout.addComponent( fu );
		if( enablePreview )
			layout.addComponent( button );

		setCompositionRoot( layout );
	}

	private FileType getFileType( File file )
	{
		byte[] prefix = new byte[20];
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream( file );
			fis.read( prefix );
		} catch( FileNotFoundException e )
		{
			e.printStackTrace();
		} catch( IOException e )
		{
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String s = FileField.bytesToHexString( prefix );
		if( s.startsWith( "FFD8FF" ) || s.startsWith( "ffd8ff" ) )
			return FileType.JPEG;
		else if( s.startsWith( "89504E47" ) || s.startsWith( "89504e47" ) )
			return FileType.PNG;
		else if( s.startsWith( "47494638" ) )
			return FileType.GIF;
		else if( s.startsWith( "3C3F786D6C" ) || s.startsWith( "3c3f786d6c" ) )
			return FileType.XML;
		else if( s.startsWith( "68746D6C3E" ) || s.startsWith( "68746d6c3e" ) )
			return FileType.HTML;
		else if( s.startsWith( "255044462D312E" ) || s.startsWith( "255044462d312e" ) )
			return FileType.PDF;
		else if( s.startsWith( "41564920" ) )
			return FileType.AVI;
		else if( s.startsWith( "435753" ) )
			return FileType.SWF;
		else if( s.startsWith( "494433" ) )
			return FileType.MP3;
		else
			return FileType.TEXT;
	}

	public File getUploadedFile()
	{
		return uploadedFile;
	}

	@Override
	public Object getValue()
	{
		if( getUploadedFile() != null )
			return null;

		return new File( fu.getTargetDirectory().getAbsolutePath(), getUploadedFile().getName() );
	}

	private void setUploadedFile( File uploadedFile )
	{
		this.uploadedFile = uploadedFile;
	}

	@Override
	public void setValue( Object newValue ) throws ReadOnlyException, ConversionException
	{
		throw new ReadOnlyException();
	}

	@Override
	public void setBuffered(boolean buffered) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isBuffered() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeAllValidators() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addValueChangeListener(ValueChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeValueChangeListener(ValueChangeListener listener) {
		// TODO Auto-generated method stub
		
	}
}

abstract class FileUploader extends Upload implements Upload.SucceededListener, Upload.FailedListener, Upload.ProgressListener, Upload.FinishedListener, Upload.StartedListener, Upload.Receiver
{
	private static final long	serialVersionUID	= -706484137723243527L;
	protected String			fileNameId;
	protected File				targetDirectory;

	FileUploader( File targetDirectory, String fileNameId )
	{
		setTargetDirectory( targetDirectory );
		setReceiver( this );
		addSucceededListener( (Upload.SucceededListener) this );
		addFailedListener( (Upload.FailedListener) this );
		addProgressListener( (Upload.ProgressListener) this );
		addFinishedListener( (Upload.FinishedListener) this );
		addStartedListener( (Upload.StartedListener) this );
		this.fileNameId = fileNameId;
	}

	public String getFileNameId()
	{
		return fileNameId;
	}

	protected int getPercentage( long readBytes, long contentLength )
	{
		return (int) Math.round( 100 * ( (double) readBytes / (double) contentLength ) );
	}

	public File getTargetDirectory()
	{
		return targetDirectory;
	}

	@Override
	public OutputStream receiveUpload( String filename, String MIMEType )
	{
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream( String.format( "%s/%s", getTargetDirectory().toString(), this.fileNameId ) );
		} catch( final java.io.FileNotFoundException e )
		{
			e.printStackTrace();
			return null;
		}
		return fos;
	}

	public void setTargetDirectory( File targetDirectory )
	{
		this.targetDirectory = targetDirectory;
	}

}

public class FileUploaderCField extends AbstractCField
{
	private VolatileFile	volatileFile;

	public FileUploaderCField( FieldAttributeAccessor fieldAttributes, VolatileFile volatileFile )
	{
		super( fieldAttributes );
		this.volatileFile = volatileFile;
	}

	@Override
	public void initField()
	{
		field = new FileField( LocalizationHandler.get( fieldAttributes.getLabel() ), Environment.getProperty( VolatileFile.STORAGE_LOCATION_PROPERTY ).toString(), volatileFile, true );
	}
}