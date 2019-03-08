package com.fantasystep.localization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.annotation.ValueOptionEntry;
import com.fantasystep.utils.FileUtil;
import com.fantasystep.utils.Option;
import com.fantasystep.utils.Option.InvalidOptionFormatException;
import com.fantasystep.utils.Option.StringOption;

public class LocalizationUtil
{
	private static Logger logger = LoggerFactory.getLogger(LocalizationUtil.class);
	public enum Language implements ValueOptionEntry
	{
		ABKHAZIAN( "ab" ), AFAN( "om" ), AFAR( "aa" ), AFRIKAANS( "af" ), ALBANIAN( "sq" ), AMHARIC( "am" ), ARABIC( "ar" ), ARMENIAN( "hy" ), ASSAMESE( "as" ), AYMARA( "ay" ), AZERBAIJANI( "az" ), BASHKIR(
				"ba" ), BASQUE( "eu" ), BENGALI( "bn" ), BHUTANI( "dz" ), BIHARI( "bh" ), BISLAMA( "bi" ), BRETON( "br" ), BULGARIAN( "bg" ), BURMESE( "my" ), BYELORUSSIAN( "be" ), CAMBODIAN( "km" ), CATALAN(
				"ca" ), CHINESE( "zh" ), CORSICAN( "co" ), CROATIAN( "hr" ), CZECH( "cs" ), DANISH( "da" ), DUTCH( "nl" ), ENGLISH( "en" ), ESPERANTO( "eo" ), ESTONIAN( "et" ), FAROESE( "fo" ), FIJI(
				"fj" ), FINNISH( "fi" ), FRENCH( "fr" ), FRISIAN( "fy" ), GALICIAN( "gl" ), GEORGIAN( "ka" ), GERMAN( "de" ), GREEK( "el" ), GREENLANDIC( "kl" ), GUARANI( "gn" ), GUJARATI( "gu" ), HAUSA(
				"ha" ), HEBREW( "he" ), HINDI( "hi" ), HUNGARIAN( "hu" ), ICELANDIC( "is" ), INDONESIAN( "id" ), INTERLINGUA( "ia" ), INTERLINGUE( "ie" ), INUKTITUT( "iu" ), INUPIAK( "ik" ), IRISH(
				"ga" ), ITALIAN( "it" ), JAPANESE( "ja" ), JAVANESE( "jw" ), KANNADA( "kn" ), KASHMIRI( "ks" ), KAZAKH( "kk" ), KINYARWANDA( "rw" ), KIRGHIZ( "ky" ), KIRUNDI( "rn" ), KOREAN( "ko" ), KURDISH(
				"ku" ), LAOTHIAN( "lo" ), LATIN( "la" ), LATVIAN( "lv" ), LINGALA( "ln" ), LITHUANIAN( "lt" ), MACEDONIAN( "mk" ), MALAGASY( "mg" ), MALAY( "ms" ), MALAYALAM( "ml" ), MALTESE( "mt" ), MAORI(
				"mi" ), MARATHI( "mr" ), MOLDAVIAN( "mo" ), MONGOLIAN( "mn" ), NAURU( "na" ), NEPALI( "ne" ), NORWEGIAN( "no" ), OCCITAN( "oc" ), ORIYA( "or" ), PASHTO( "ps" ), PERSIAN( "fa" ), POLISH(
				"pl" ), PORTUGUESE( "pt" ), PUNJABI( "pa" ), QUECHUA( "qu" ), RHAETOROMANCE( "rm" ), ROMANIAN( "ro" ), RUSSIAN( "ru" ), SAMOAN( "sm" ), SANGHO( "sg" ), SANSKRIT( "sa" ), SCOTS( "gd" ), SERBIAN(
				"sr" ), SERBOCROATIAN( "sh" ), SESOTHO( "st" ), SETSWANA( "tn" ), SHONA( "sn" ), SINDHI( "sd" ), SINHALESE( "si" ), SISWATI( "ss" ), SLOVAK( "sk" ), SLOVENIAN( "sl" ), SOMALI( "so" ), SPANISH(
				"es" ), SUNDANESE( "su" ), SWAHILI( "sw" ), SWEDISH( "sv" ), TAGALOG( "tl" ), TAJIK( "tg" ), TAMIL( "ta" ), TATAR( "tt" ), TELUGU( "te" ), THAI( "th" ), TIBETAN( "bo" ), TIGRINYA(
				"ti" ), TONGA( "to" ), TSONGA( "ts" ), TURKISH( "tr" ), TURKMEN( "tk" ), TWI( "tw" ), UIGHUR( "ug" ), UKRAINIAN( "uk" ), URDU( "ur" ), UZBEK( "uz" ), VIETNAMESE( "vi" ), VOLAPUK( "vo" ), WELSH(
				"cy" ), WOLOF( "wo" ), XHOSA( "xh" ), YIDDISH( "yi" ), YORUBA( "yo" ), ZHUANG( "za" ), ZULU( "zu" );

		public static Language fromString( String str )
		{
			for( Language l : values() )
				if( l.getCode().equals( str.toLowerCase() ) || l.toString().equals( str.toUpperCase() ) )
					return l;

			return ENGLISH;
		}

		private String	code;

		Language( String code )
		{
			this.code = code;
		}

		public String getCode()
		{
			return code;
		}

		public String getLabel()
		{
			return toString();
		}

		public Object getValue()
		{
			return this;
		}
	}

	private static File			localeDirectory;
	public final static String	LOCALIZATIONUTIL_PROPERTY	= "LocalizationUtil.property";
	private Language			language					= Language.ENGLISH;

	public LocalizationUtil( Language language )
	{
		this.language = language;
	}

	public Language getLanguage()
	{
		return language;
	}

	public void setLanguage( Language language )
	{
		this.language = language;
	}

	private static Map<Language,Map<String,String>>	languageStrings	= new HashMap<Language,Map<String,String>>();

	static
	{
		try
		{
			System.setProperty( "file.encoding", "UTF-8" );

			Option.setConfigFileName( "/etc/fantasystep/localization.conf" );
			StringOption LOCALE_DIRECTORY = new StringOption( "locale.directory", "/var/lib/fantasystep/locale", true, "Directory containing locale files" );
			Option.load();

			setLocaleDirectory( new File( LOCALE_DIRECTORY.value() ) );
		} catch( InvalidOptionFormatException e )
		{
			e.printStackTrace();
		}
	}

	private static void addString( String key, String value, Language language )
	{
		if( !getLanguageStrings().containsKey( language ) )
			getLanguageStrings().put( language, new HashMap<String,String>() );
		getLanguageStrings().get( language ).put( key, value );
	}

	public String getString( String key )
	{
		if( getLanguageStrings().containsKey( language ) )
		{
			if( getLanguageStrings().get( language ).containsKey( key ) )
				return getLanguageStrings().get( language ).get( key );
			else return key;
		} else
		{
			if( getLanguageStrings().containsKey( Language.ENGLISH ) )
				if( getLanguageStrings().get( Language.ENGLISH ).containsKey( key ) )
					return getLanguageStrings().get( Language.ENGLISH ).get( key );
			return key;
		}
	}

	private static File getLocaleDirectory()
	{
		return localeDirectory;
	}

	private static Map<Language,Map<String,String>> getLanguageStrings()
	{
		return languageStrings;
	}

	public static void load( String localizationFile, Language language ) throws IOException
	{
		if( !getLanguageStrings().containsKey( language ) )
			getLanguageStrings().put( language, new HashMap<String,String>() );
		
		File file = new File( getLocaleDirectory(), String.format( "%s.%s", localizationFile, language.getCode() ) );
		if( !file.exists() )
		{
			file = new File( getLocaleDirectory(), String.format( "%s.%s", localizationFile, "en" ) );
			if( !file.exists() )
				throw new FileNotFoundException( file.getAbsolutePath() );
		}
		List<String> lines = FileUtil.getLines( file );
		for( String line : lines )
		{
			if( line.startsWith( "@include" ) )
			{
				logger.info( "Load include localization file : " + parseInclude( line ) );
				load( parseInclude( line ), language );
			} else
			{
				String[] data = line.split( "=", 2 );
				if( data.length == 2 )
					addString( data[0], data[1], language );
			}
		}
	}

	public static void main( String[] args ) throws IOException
	{
		load( "filebrowser", Language.ENGLISH );
		logger.info( new LocalizationUtil( Language.ENGLISH ).getString( "LABEL_OTHER" ) );
	}

	private static String parseInclude( String line )
	{
		return line.split( " ", 2 )[1];
	}

	private static void setLocaleDirectory( File localeDirectory )
	{
		LocalizationUtil.localeDirectory = localeDirectory;
	}
}
