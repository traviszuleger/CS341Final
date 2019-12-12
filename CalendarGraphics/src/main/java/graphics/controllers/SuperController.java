package controllers;

import graphics.GraphicsMain;
import utils.Constants.*;
import utils.Utils;

/**
 * SuperController
 *
 * @description Shares functionality of GraphicsMain with different
 *              SuperControllers. This is also used to keep track of the current
 *              User (if logged in) and their information.
 *
 * @edit_history
 * @edited Oct. 22, 2019: Travis Zuleger -> Added the ability to hold
 *         <username>, <firstName>, <lastName>
 * @edited Oct. 26, 2019: Travis Zuleger -> Added the methods <login(String,
 *         String, String)> and <logout()>.
 *
 * @lastedited Oct. 26, 2019
 * @author Phil Sample
 * @date Oct 01, 2019
 */
public abstract class SuperController
{

	public static boolean isUserLoggedIn = false;

	private static GraphicsMain graphicsMain = new GraphicsMain();
	private static String user_id, username, firstName, lastName;

	/**
	 * switchScreen(ScreenEnum)
	 *
	 * @description Switches the screen to the key associated with <screenEnum>
	 *
	 * @param screen -> Enum that is used as a key to what screen is being
	 *                   displayed.
	 */
	public static void switchScreen( SCREEN screen )
	{
		graphicsMain.switchScreen( screen );
	}

	/**
	 * refreshAll()
	 *
	 * @description <DESCRIPTION HERE>
	 *
	 */

	public static void refreshAll()
	{
		graphicsMain.refresh();
	}


	/**
	 * login(String, String, String)
	 *
	 * @description Saves the strings <username>, <firstName>, and <lastName> to the
	 *              static variables in SuperController
	 *
	 * @param username  -> Username of the given user who just logged in.
	 * @param firstName -> First name of the given user who just logged in.
	 * @param lastName  -> Last name of the given user who just logged in.
	 */
	public static void logUserIn( String username, String firstName, String lastName )
	{
		SuperController.user_id = Utils.getHashCode( username );
		SuperController.username = username;
		SuperController.firstName = firstName;
		SuperController.lastName = lastName;
		SuperController.isUserLoggedIn = true;
		SuperController.refreshAll();
	}


	/**
	 * logout()
	 *
	 * @description Set the static variables in SuperController to null, as the user
	 *              should no longer be accessed.
	 *
	 */
	public static void logUserOut()
	{
		SuperController.username = null;
		SuperController.firstName = null;
		SuperController.lastName = null;
		SuperController.isUserLoggedIn = false;
		SuperController.switchScreen( SCREEN.LOGIN_SCREEN );
	}


	// GETTERS
	public static String getUserId()
	{
		return user_id;
	}


	public static String getUsername()
	{
		return username;
	}


	public static String getFirstName()
	{
		return firstName;
	}

	public static String getLastName()
	{
		return lastName;
	}
}
