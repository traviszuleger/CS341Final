package graphics;

import javafx.application.Platform;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import sql.Database;
import utils.Constants.*;
import utils.Utils;
import controllers.AdminController;
import controllers.UserController;
import controllers.SuperController;

import java.lang.Thread;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;

/**
 * GraphicsMain extends Application
 *
 * @description Combines the functional and graphical code to create the Web
 *              Application.
 *
 * @edit_history
 * @edited Oct. 22, 2019: Travis Zuleger -> Added line of code that connects the
 *         Web Application to the SQL database.
 *
 * @lastedited Oct. 22, 2019
 * @author Phil Sample
 * @date Oct 01, 2019
 */
public final class GraphicsMain extends Application
{
	private static Stage calendarStage;
	private SCREEN currentScreen;
	private EnumMap<SCREEN, Parent> screenMap;

	private UserController userCon;
	private AdminController adminCon;


	/**
	 * CONSTRUCTOR ()
	 *
	 * @description Connects the Web Application to the database and loads the FXML
	 *              files necessary for the graphical side of the Web Application.
	 *
	 */
	public GraphicsMain()
	{
		// Database.connectTo( null, "cs341db" ); // LOCALHOST MYSQL DB
		// Database.init( true );
		Database.connectTo( "138.49.184.127", "cs341db" ); // MAIN DATABASE FOR PROJECT
		Database.init( false );
		// Database.connectTo( "138.49.184.127", "cs341db_test" ); // TEST DATABASE
		// Database.init( false );

		if( !( TABLES.USERS.table().entryExists( "user_id", Utils.getHashCode( "admin" ) ) ) )
		{
			String[] entry =
			{
				Utils.getHashCode( "admin" ),
				"Admin",
				"Admin",
				null,
				"admin@dental.com",
				"(555) 444-3333",
				"ADMIN",
				Utils.hash( "admin" + "admin" ),
				"ENABLED"
			};
			TABLES.USERS.table().insertEntry( entry );
		}

		this.screenMap = new EnumMap<>( SCREEN.class );
		try
		{
			loadScreen( PAGE.LOGIN, SCREEN.LOGIN_SCREEN );
			loadScreen( PAGE.ADMIN_EMPLOYEE_ROSTER, SCREEN.ADMIN_SCREEN );
			loadScreen( PAGE.USER_SCREEN, SCREEN.USER_SCREEN );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}


	/**
	 * loadScreen(String, ScreenEnum)
	 *
	 * @description Loads a new screen onto the Web Application.
	 *
	 * @param fileName -> String corresponding to the name of the FXML file
	 *                 provided.
	 * @param screen   -> Key used to keep track of what Screen the application is
	 *                 on.
	 * @throws IOException
	 */
	private void loadScreen( PAGE page, SCREEN screen ) throws IOException
	{

		Path path = Paths.get( page.getDir() ).toAbsolutePath();

		FXMLLoader loader = new FXMLLoader( getClass().getResource( path.toString() ) );

		// Parent parent = loader.load();
		Pane p = loader.load( getClass().getResource( page.getDir() ).openStream() );
		if( page == PAGE.ADMIN_EMPLOYEE_ROSTER )
		{
			if( adminCon == null )
			{
				adminCon = loader.getController();
			}
		}
		if( page == PAGE.USER_SCREEN )
		{
			if( userCon == null )
			{
				userCon = loader.getController();
			}
		}

		this.screenMap.put( screen, p );
	}


	/**
	 * start(Stage)
	 *
	 * @description Initializes <calendarStage> and initially displays the login
	 *              screen.
	 *
	 * @param stage -> ???
	 */
	@ Override
	public void start( Stage stage )
	{
		calendarStage = stage;

		Scene scene = new Scene( this.screenMap.get( SCREEN.LOGIN_SCREEN ) );
		calendarStage.setScene( scene );

		calendarStage.show();

		this.currentScreen = SCREEN.LOGIN_SCREEN;
	}


	/**
	 * switchScreen(ScreenEnum)
	 *
	 * @description Ultimately switches the screen to the provided <screenEnum>
	 *
	 * @param screen -> Key value corresponding the screen that is being switched
	 *               to.
	 *
	 * @todo Fix bug: IllegalArgumentException is thrown every time a new screen is
	 *       swapped.
	 */
	public void switchScreen( SCREEN screen )
	{

		if( this.currentScreen == screen )
		{
			// this.currentScreen( Constants.SCREEN.ERROR_SCREEN );
			System.out.println( "Ignore for now." );
			this.currentScreen = null;
			return;
		}

		// switch the root
		calendarStage.getScene().setRoot( this.screenMap.get( screen ) );

		this.currentScreen = screen;
	}


	/**
	 * refresh()
	 *
	 * @description Refreshes all of the screens so they are updated to the most
	 *              recent changes.
	 *
	 */
	public void refresh()
	{
		// Refresh admin_employee_roster screen
		adminCon.refresh();
		if( SuperController.isUserLoggedIn )
		{
			userCon.refresh();
		}

	}
}
