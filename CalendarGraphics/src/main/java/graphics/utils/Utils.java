package utils;

import java.lang.Math;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.scene.Node;

/**
 * Utils
 *
 * @description Miscellaneous methods that are used throughout the program by all classes.
 *
 * @edit_history
 *
 * @lastedited Nov 14, 2019
 * @author Travis Zuleger
 * @date Nov 14, 2019
 */
public final class Utils
{

	private static boolean updated;

	/**
	 * hash(String)
	 *
	 * @description Takes in a string and returns a 128-bit digest (as a
	 *              32-character long hexadecimal string) from an MD5 hash function.
	 *              This function supports data-at-rest security for passwords. This
	 *              does not act as encryption for data-at-rest.
	 *
	 * @param toHash -> String to hash.
	 * @return -> 32-character long hexadecimal string representing the 128-bit
	 *         digest.
	 */
	public static String hash( String toHash )
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance( "MD5" );
			md.update( toHash.getBytes() );
			byte[] digest = md.digest();
			StringBuilder checksum = new StringBuilder( digest.length * 2 );
			for( byte b : digest )
			{
				checksum.append( String.format( "%02x", b ) );
			}
			return checksum.toString();
		} catch( NoSuchAlgorithmException e )
		{
			System.out.println( "Error hashing " + toHash );
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * reset(ResultSet)
	 *
	 * @description Resets the ResultSet's .next factor so the ResultSet is read
	 *              properly the next time.
	 *
	 * @param set -> Any arbitrary ResultSet.
	 */
	public static void reset( ResultSet set )
	{
		try
		{
			set.beforeFirst();
		} catch( SQLException e )
		{
			e.printStackTrace();
		}
	}


	/**
	 * getHashCode(Object)
	 *
	 * @description Gets the Java Hash Code in String format and within the range of
	 *              100,000
	 *
	 * @param o -> Object to get hashed
	 * @return
	 */
	public static String getHashCode( Object o )
	{
		return Integer.toString( Math.abs( o.hashCode() % 100000 ) );
	}

	/**
	* idleToHover( Node )
	*
	* @description Used for setOnMouse movements to make node hovers look more visually appealing.
	*
	*/
	public static void idleToHover(Node b)
	{
		String style = b.getStyle();
		if( style.equals( Constants.JFX_CSS_GRN_BTN_IDLE ) )
		{
			style = Constants.JFX_CSS_GRN_BTN_HOVER;
		}
		else if( style.equals( Constants.JFX_CSS_YEL_BTN_IDLE ) )
		{
			style = Constants.JFX_CSS_YEL_BTN_HOVER;
		}
		else if( style.equals( Constants.JFX_CSS_RED_BTN_IDLE ) )
		{
			style = Constants.JFX_CSS_RED_BTN_HOVER;
		}
		else if( style.equals ( Constants.JFX_CSS_LGRY_BTN_IDLE ) )
		{
			style = Constants.JFX_CSS_LGRY_BTN_HOVER;
		}
		else if ( style.equals( Constants.JFX_CSS_DGRY_BTN_IDLE ) )
		{
			style = Constants.JFX_CSS_DGRY_BTN_HOVER;
		}
		else if ( style.equals( Constants.JFX_CSS_BLU_BTN_IDLE ) )
		{
			style = Constants.JFX_CSS_BLU_BTN_HOVER;
		}
		b.setStyle( style );
	}

	/**
	* hoverToIdle( Node )
	*
	* @description Used for setOnMouse movements to make node hovers look more visually appealing.
	*
	*/
	public static void hoverToIdle(Node b)
	{
		String style = b.getStyle();
		if( style.equals( Constants.JFX_CSS_GRN_BTN_HOVER ) )
		{
			style = Constants.JFX_CSS_GRN_BTN_IDLE;
		}
		else if( style.equals( Constants.JFX_CSS_YEL_BTN_HOVER ) )
		{
			style = Constants.JFX_CSS_YEL_BTN_IDLE;
		}
		else if( style.equals( Constants.JFX_CSS_RED_BTN_HOVER ) )
		{
			style = Constants.JFX_CSS_RED_BTN_IDLE;
		}
		else if( style.equals ( Constants.JFX_CSS_LGRY_BTN_HOVER ) )
		{
			style = Constants.JFX_CSS_LGRY_BTN_IDLE;
		}
		else if ( style.equals( Constants.JFX_CSS_DGRY_BTN_HOVER ) )
		{
			style = Constants.JFX_CSS_DGRY_BTN_IDLE;
		}
		else if ( style.equals( Constants.JFX_CSS_BLU_BTN_HOVER ) )
		{
			style = Constants.JFX_CSS_BLU_BTN_IDLE;
		}
		b.setStyle( style );
	}

}
