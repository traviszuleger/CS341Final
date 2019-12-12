package controllers;

import java.util.Arrays;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.shape.StrokeType;

import sql.*;
import utils.*;
import utils.Constants.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * AdminController extends SuperController implements Initializable
 *
 * @description Handles the behavior of the Admin's screen that shows a table of
 *              all employees. This screen also provides the Admin the ability
 *              to create new users, search for specific employees, delete users,
 *							and view appointments.
 *
 */
public final class AdminController extends SuperController implements Initializable
{

	@ FXML private VBox adminMenuBar;
	@ FXML private GridPane employeeRoster, rosterKey;

	private MenuButton partner;

	/**
	 * initialize(URL, ResourceBundle)
	 *
	 * @description Initializes the page for when it is loaded up.
	 *
	 */
	@ Override
	public void initialize( URL location, ResourceBundle resources )
	{
		partner = newMenuButton("");
		partner.setVisible(false);

		this.adminMenuBar.setPadding( new Insets( 90.0, 15.0, 10.0, 15.0 ) );
		this.adminMenuBar.setSpacing( 5.0 );

		adminDefaultMenuBar();

		filterEmployeesToRegister();
	}


	/**
	 * refresh()
	 *
	 * @description Refreshes all of the components on the screen to be in sync with
	 *              the Database.
	 *
	 */
	public void refresh()
	{
		filterEmployeesToRegister();
		addAllDentists();
		addAllHygienists();
	}


	/**
	 * adminDefaultMenuBar()
	 *
	 * @description Modifies <adminMenuBar> to have the default menu options.
	 *
	 */
	private void adminDefaultMenuBar()
	{
		this.adminMenuBar.getChildren().clear();

		// create the menu Buttons

		Font font = new Font( 16.0 );

		Button createNewAccount = newButton( "Create New Account" );
		createNewAccount.setOnAction( e -> adminAccountCreationBar() );

		Button logout = newButton( "Log Out" );
		logout.setOnAction( e -> SuperController.logUserOut() );
		this.adminMenuBar.getChildren().add( createNewAccount );
		this.adminMenuBar.getChildren().add( newGap() );
		this.adminMenuBar.getChildren().add( newGap() );
		initSearchArea();

		Button fullRoster = newButton("Full Roster");
		fullRoster.setOnAction( e -> filterEmployeesToRegister() );
		this.adminMenuBar.getChildren().add( fullRoster );
		for(int i = 0; i < 10; ++i)
		{
			this.adminMenuBar.getChildren().add( newGap() );
		}
		logout.setStyle( Constants.JFX_CSS_RED_BTN_IDLE );
		this.adminMenuBar.getChildren().add( logout );
	}

	private void initSearchArea()
	{
		TextField searchBy = newTextField("Select a search field");
		searchBy.setVisible( true );
		MenuButton searchSelector = newMenuButton( "Search Option" );

		MenuItem searchByUsername = newSearchMenuItem( searchSelector, searchBy, "Username" );
		MenuItem searchByUserID = newSearchMenuItem( searchSelector, searchBy, "User ID");
		MenuItem searchByName = newSearchMenuItem( searchSelector, searchBy, "Name" );
		MenuItem searchByTitle = newSearchMenuItem( searchSelector, searchBy, "Title" );
		MenuItem searchByPartner = newSearchMenuItem( searchSelector, searchBy, "Partner Username" );

		searchSelector.getItems().addAll( searchByUsername, searchByUserID, searchByName, searchByTitle, searchByPartner );

		Button confirmSearch = newButton( "Search" );

		confirmSearch.setOnAction( e ->
			{
				String value = searchBy.getText().toLowerCase();
				if(searchSelector.getText().equals("Username"))
				{
					filterEmployeesToRegister("user_id", Utils.getHashCode(value));
				}
				else if(searchSelector.getText().equals("User ID") )
				{
					filterEmployeesToRegister("user_id", value);
				}
				else if(searchSelector.getText().equals("Name"))
				{
					String[] values = value.split(" ");
					if(values.length != 2)
					{
						filterEmployeesToRegister();
					}
					else
					{
						filterEmployeesToRegister("first_name", "last_name", values[0], values[1]);
					}
				}
				else if(searchSelector.getText().equals("Title"))
				{
					filterEmployeesToRegister("title", value);
				}
				else if(searchSelector.getText().equals("Partner Username") )
				{
					filterEmployeesToRegister("partner_id", Utils.getHashCode(value));
				}
				else
				{
					filterEmployeesToRegister();
				}
			}
		);
		this.adminMenuBar.getChildren().add( searchSelector );
		this.adminMenuBar.getChildren().add( searchBy );
		this.adminMenuBar.getChildren().add( confirmSearch );
	}

	/**
	 * adminAccountCreationBar()
	 *
	 * @description Modifies <adminMenuBar> to show account creation options.
	 *
	 */
	private void adminAccountCreationBar()
	{
		this.adminMenuBar.getChildren().clear();

		MenuButton accountType = newMenuButton( "SELECT TITLE" );

		String accountTitle;
		String[] titles = { "DENTIST", "HYGIENIST", "ADMIN" };
		MenuItem[] titleItems = new MenuItem[titles.length];
		for( int i = 0; i < titles.length; ++i )
		{
			titleItems[i] = new MenuItem( titles[i] );
			String tempTitle = titles[i];
			titleItems[i].setOnAction( e ->
				{
					accountType.setText( tempTitle );
					if(tempTitle.equals("DENTIST"))
					{
						partner.setText("Hygienist partner");
						partner.getItems().clear();
						addAllHygienists();
						partner.setVisible(true);
					}
					else if(tempTitle.equals("HYGIENIST"))
					{
						partner.setText( "Dentist partner" );
						partner.getItems().clear();
						addAllDentists();
						partner.setVisible(true);
					}
					else
					{
						partner.setVisible(false);
					}
				}
			);
		}

		accountType.getItems().addAll( titleItems );

		TextField firstName = newTextField( "First Name" );
		TextField lastName = newTextField( "Last Name" );
		TextField phoneNumber = newTextField( "Number: (###) ###-####" );
		TextField email = newTextField( "Email Address" );
		TextField accountLogin = newTextField( "Username" );
		Label passwordMsg1 = new Label();
		passwordMsg1.setText("New Password");
		PasswordField accountPassword = newPasswordField();
		Label passwordMsg2 = new Label();
		passwordMsg2.setText("Confirm Password");
		PasswordField checkPassword = newPasswordField();
		Button createAccount = newButton( "Create Account" );


		createAccount.setOnAction( e ->
		{
			// VALIDITY CHECK HERE
			if(
				!(
					firstName.getText().equals( "" ) ||
					lastName.getText().equals( "" ) ||
					accountLogin.getText().equals( "" ) ||
					accountPassword.getText().equals( "" ) ||
					accountType.getText().equals( "" )
				)
				&&
				accountPassword.getText().equals(checkPassword.getText())
			)
			{
				// Validates that partnerDB is valid and translates the firstname - lastname to userID
				String partnerDB = accountType.getText().equals("DENTIST") || accountType.getText().equals("HYGIENIST") ? partner.getText().toLowerCase() : null;
				partnerDB = partnerDB == null ? null : (partnerDB.equals("Hygienist partner") || partnerDB.equals("Dentist partner")) ? null : partnerDB;
				partnerDB = TABLES.USERS.table().getValue( new String[] { "first_name", "last_name" }, new String[] { partnerDB.split(" ")[0], partnerDB.split(" ")[1] }, "user_id" );
				String[] accountInfo =
				{
					Utils.getHashCode( accountLogin.getText().toLowerCase() ),
					firstName.getText().toLowerCase(),
					lastName.getText().toLowerCase(),
					partnerDB,
					email.getText().toLowerCase(),
					phoneNumber.getText().toLowerCase(),
					accountType.getText(),
					Utils.hash( accountLogin.getText().toLowerCase() + accountPassword.getText() ),
					"ENABLED"
				};
				if( TABLES.USERS.table().entryExists( "user_id", Utils.getHashCode( accountLogin.getText() ) ) )
				{
					// Raise error message here.
				} else
				{
					TABLES.USERS.table().insertEntry( accountInfo );
					SuperController.refreshAll();
				}
			}
		} );

		this.adminMenuBar.getChildren().add( accountType );
		this.adminMenuBar.getChildren().add( firstName );
		this.adminMenuBar.getChildren().add( lastName );
		this.adminMenuBar.getChildren().add( phoneNumber );
		this.adminMenuBar.getChildren().add( email );
		this.adminMenuBar.getChildren().add( accountLogin );
		this.adminMenuBar.getChildren().add( passwordMsg1 );
		this.adminMenuBar.getChildren().add( accountPassword );
		this.adminMenuBar.getChildren().add( passwordMsg2 );
		this.adminMenuBar.getChildren().add( checkPassword );
		this.adminMenuBar.getChildren().add( partner );
		this.adminMenuBar.getChildren().add( createAccount );

		Button defaultMenu = newButton( "Return" );
		defaultMenu.setOnAction( e -> adminDefaultMenuBar() );
		this.adminMenuBar.getChildren().add( defaultMenu );
	}

	/**
	* filterAppointmentsToRegister(String ...)
	*
	* @description Filters the EmployeeRoster to hold all appointments for a specific employee.
	*
	*/
	private void filterAppointmentsToRegister( String ... args )
	{
		String[] fields = { "Patient ID", "Employee ID", "Description", "Start Time", "End Time", "Date" };
		while( rosterKey.getColumnConstraints().size() > fields.length )
		{
			rosterKey.getColumnConstraints().remove( rosterKey.getColumnConstraints().size() - 1 );
			employeeRoster.getColumnConstraints().remove( employeeRoster.getColumnConstraints().size() - 1 );
		}

		rosterKey.getChildren().clear();
		for(int i = 0; i < fields.length; ++i)
		{
			Text t = newText(fields[i]);
			t.setFont(new Font(18.0));
			t.setStrokeType( StrokeType.OUTSIDE );
			t.setTextAlignment( TextAlignment.CENTER );
			rosterKey.add(t, i, 0);
			GridPane.setHalignment(t, HPos.CENTER);
		}

		if(args.length == 2)
		{
			this.employeeRoster.getChildren().clear();
			int x = 0, y = 0;
			String[][] entries = TABLES.APPOINTMENTS.table().getAllEntriesFor(args[0], args[1]);
			for(int i = 0; i < entries.length; ++i )
			{
				for(int j = 0; j < entries[i].length; ++j )
				{
					Text text = newText( entries[i][j] );
					this.employeeRoster.add( text, x, y );
					x++;
				}
				RowConstraints rowConstraints = newRowConstraints();
				employeeRoster.getRowConstraints().add( rowConstraints );
				y++;
				x = 0;
			}
		}

	}

	/**
	* filterEmployeesToRegister(String ...)
	*
	* @description Filters the Employee Roster (employeeRoster) to show all employees or specific employees (per the search bar)
	*
	*/
	private void filterEmployeesToRegister( String ... args )
	{
		String[] fields = { "User ID", "First Name", "Last Name", "Partner ID", "Email", "Phone Number", "Title", "Appointments", "Status" };
		while( rosterKey.getColumnConstraints().size() < fields.length )
		{
			rosterKey.getColumnConstraints().add( newColumnConstraints() );
			ColumnConstraints cc = newColumnConstraints();
			cc.setHalignment( HPos.CENTER );
			employeeRoster.getColumnConstraints().add( cc );
		}
		rosterKey.getChildren().clear();


		for(int i = 0; i < fields.length; ++i)
		{
			Text t = newText(fields[i]);
			t.setFont(new Font(18.0));
			t.setStrokeType( StrokeType.OUTSIDE );
			t.setTextAlignment( TextAlignment.CENTER );
			rosterKey.add(t, i, 0);
			GridPane.setHalignment(t, HPos.CENTER);
		}

		this.employeeRoster.getChildren().clear();
		if(args.length == 4) // searching by two fields.
		{
			String[][] entries = TABLES.USERS.table().getAllEntriesFor( new String[] { args[0], args[1] }, new String[] { args[2], args[3] } );
			fill(entries);
		}
		else if(args.length == 2) // searching by one field.
		{
			String[][] entries = TABLES.USERS.table().getAllEntriesFor(args[0], args[1]);
			fill(entries);
		}
		else
		{
			String[] titles = { "ADMIN", "DENTIST", "HYGIENIST" };
			// first dimension: TITLE,
			// second dimension: EMPLOYEES OF THAT TITLE,
			// third dimension: VALUES BELONGING TO THAT EMPLOYEE.
			// Example: { { ADMIN } { {"33333", "John", "Doe", etc... "DENTIST" } } { HYGIENIST } { PATIENT } }
			String[][][] employeeData = new String[titles.length][][];
			for( int i = 0; i < titles.length; ++i )
			{
				employeeData[i] = TABLES.USERS.table().getAllEntriesFor( "title", titles[i] );
			}

			for( int i = 0; i < employeeData.length; ++i )
			{
				fill(employeeData[i]);
			}
		}
	}

	/**
	* fill(String[][])
	*
	* @description Method to make filterEmployeesToRegister cleaner, adds entries to the Employee Roster
	*
	*/
	private void fill(String[][] entries)
	{
		int x = 0, y = 0;
		for(int i = 0; i < entries.length; ++i )
		{
			for(int j = 0; j < entries[i].length; ++j )
			{
				if(j == TABLES.USERS.getFieldIdx("pass_hash"))
				{
					addAppointmentsButton(entries, i, j, x, y);
				}
				else if(j == TABLES.USERS.getFieldIdx("status"))
				{
					addStatusButton(entries, i, j, x, y);
				}
				else
				{
					Text text = newText( entries[i][j] );
					this.employeeRoster.add( text, x, y );
				}
				x++;
			}
			RowConstraints rowConstraints = newRowConstraints();
			employeeRoster.getRowConstraints().add( rowConstraints );
			y++;
			x = 0;
		}
	}

	/**
	* addAppointmentsButton(String[][], int ...)
	*
	* @description Method to make filterEmployeesToRegister cleaner, adds entries to the Employee Roster
	*
	*/
	private void addAppointmentsButton(String[][] entries, int ... args)
	{
		Button apts = newButton("Appointments");
		apts.setFont( new Font( 10.0 ) );
		int a = args[0], b = args[1];
		String idField = entries[a][TABLES.USERS.getFieldIdx("title")].equals("PATIENT") ? "patient_id" : "employee_id";
		apts.setOnAction(e ->
		{
			// LEAVING OFF ON THIS: HYGIENIST NEEDS TO SHOW APPOINTMENTS, BUT NO APPOINTMENTS WILL EVER STORE A HYGIENIST ID, ONLY DENTIST/PATIENT IDS.
			if( idField.equals("employee_id") && entries[a][TABLES.USERS.getFieldIdx("title")].equals("HYGIENIST") )
			{
				filterAppointmentsToRegister( idField, entries[a][TABLES.USERS.getFieldIdx("partner_id")] );
			}
			else
			{
				filterAppointmentsToRegister( idField, entries[a][TABLES.USERS.getFieldIdx("user_id")] );
			}
		}
		);
		this.employeeRoster.add( apts, args[2], args[3] );
	}

	/**
	* addStatusButton(String[][], int ...)
	*
	* @description Method to make filterEmployeesToRegister cleaner, adds entries to the Employee Roster
	*
	*/
	private void addStatusButton(String[][] entries, int ... args)
	{
		Button status = newButton("");
		int a = args[0], b = args[1];
		status.setOnMouseEntered( e -> Utils.idleToHover( status ) );
		status.setOnMouseExited( e -> Utils.hoverToIdle( status ) );
		status.setStyle( entries[a][TABLES.USERS.getFieldIdx("status")].equals("DISABLED") ? Constants.JFX_CSS_RED_BTN_IDLE : Constants.JFX_CSS_GRN_BTN_IDLE );
		status.setOnAction( e ->
			{
				boolean isDisabled = TABLES.USERS.table().getValue(
					"user_id",
					entries[a][TABLES.USERS.getFieldIdx("user_id")],
					"status"
				).equals("DISABLED");
				if( isDisabled )
				{
					status.setStyle( Constants.JFX_CSS_GRN_BTN_IDLE );
				}
				else
				{
					status.setStyle( Constants.JFX_CSS_RED_BTN_IDLE );
					deleteAllAppointmentsFor(
						entries[a][TABLES.USERS.getFieldIdx("title")],
						entries[a][TABLES.USERS.getFieldIdx("user_id")]
					);
				}
				TABLES.USERS.table().editEntry(
					new String[] { "user_id" },
					new String[] { entries[a][TABLES.USERS.getFieldIdx("user_id")] },
					new String[] { "status" },
					new String[] { isDisabled ? "ENABLED" : "DISABLED" }
				);
			}
		);
		this.employeeRoster.add( status, args[2], args[3] );
	}

	/**
	* deleteAllAppointmentsFor(String title, String userID)
	*
	* @description Deletes all appointments for <title> <userID>
	*
	*/
	private void deleteAllAppointmentsFor(String title, String userID)
	{
		String[][] allAppointments;
		if(title.equals("DENTIST"))
		{
			if(title.equals("HYGIENIST"))
			{
				userID = TABLES.USERS.table().getValue("user_id", userID, "partner_id");
			}
		 	allAppointments = TABLES.APPOINTMENTS.table().getAllEntriesFor("employee_id", userID);
		}
		else
		{
			allAppointments = TABLES.APPOINTMENTS.table().getAllEntriesFor("patient_id", userID);
		}
		for(int i = 0; i < allAppointments.length; ++i)
		{
			TABLES.APPOINTMENTS.table().deleteEntry(TABLES.APPOINTMENTS.fields(), allAppointments[i]);
		}
	}

	/**
	* addAllDentists()
	*
	* @description Adds all dentists to menu buttons <partner>
	*
	*/
	private void addAllDentists()
	{
		String[][] allDentists = TABLES.USERS.table().getAllEntriesFor( "title", "DENTIST" );
		for(int i = 0; i < allDentists.length; ++i)
		{
			String name = allDentists[i][TABLES.USERS.getFieldIdx("first_name")] + " " + allDentists[i][TABLES.USERS.getFieldIdx("last_name")];
			MenuItem m = new MenuItem(name);
			m.setOnAction( e ->
				{
					partner.setText(name);
				}
			);
			partner.getItems().add( m );
		}
	}

	/**
	* addAllHygienists()
	*
	* @description Adds all hygienists to menu buttons <partner>
	*
	*/
	private void addAllHygienists()
	{
		String[][] allHygienists = TABLES.USERS.table().getAllEntriesFor( "title", "HYGIENIST" );
		for(int i = 0; i < allHygienists.length; ++i)
		{
			String name = allHygienists[i][TABLES.USERS.getFieldIdx("first_name")] + " " + allHygienists[i][TABLES.USERS.getFieldIdx("last_name")];
			MenuItem m = new MenuItem(name);
			m.setOnAction( e ->
				{
					partner.setText(name);
				}
			);
			partner.getItems().add( m );
		}
	}

	/**
	* newRowConstraints()
	*
	* @description Creates a new RowConstraints object and returns it.
	*
	*/
	private RowConstraints newRowConstraints()
	{
		RowConstraints rowConstraints = new RowConstraints();
		rowConstraints.setMaxHeight( 30.0 );
		rowConstraints.setPrefHeight( 30.0 );
		rowConstraints.setMinHeight( 30.0 );
		rowConstraints.setValignment( VPos.TOP );
		return rowConstraints;
	}

	/**
	* newText()
	*
	* @description Creates a new Text object and returns it.
	*
	*/
	private Text newText( String label )
	{
		Text t = new Text( label );
		t.setFont( new Font( 16.0 ) );
		t.prefHeight( 30.0 );
		t.maxHeight( 30.0 );
		return t;
	}


	/**
	 * newTextField(String)
	 *
	 * @description Helps with formatting and keeping code clear and concise to
	 *              read.
	 *
	 * @param label -> Label to the text field being created.
	 * @return TextField object that appears on the screen.
	 */
	private TextField newTextField( String label )
	{
		Font font = new Font( 16.0 );
		TextField tf = new TextField( label );
		tf.setFont( font );
		tf.setMinWidth( 100.0 );
		tf.setMinHeight( 30.0 );
		tf.setMaxWidth( Double.MAX_VALUE );
		return tf;
	}


	/**
	 * newPasswordField()
	 *
	 * @description Helps with formatting and keeping code clear and concise to
	 *              read.
	 *
	 * @return PasswordField object that appears on the screen.
	 */
	private PasswordField newPasswordField()
	{
		Font font = new Font( 16.0 );
		PasswordField tf = new PasswordField();
		tf.setFont( font );
		tf.setMinWidth( 100.0 );
		tf.setMinHeight( 30.0 );
		tf.setMaxWidth( Double.MAX_VALUE );
		return tf;
	}


	/**
	 * newButton(String)
	 *
	 * @description Helps with formatting and keeping code clear and concise to
	 *              read.
	 *
	 * @param label -> Label to the button being created.
	 * @return Button object that appears on the screen.
	 */
	private Button newButton( String label )
	{
		Font font = new Font( 12.0 );
		Button b = new Button( label );
		b.setFont( font );
		b.setMinWidth( 100.0 );
		b.setMinHeight( 30.0 );
		b.setMaxWidth( Double.MAX_VALUE );
		b.setStyle(Constants.JFX_CSS_LGRY_BTN_IDLE);
		b.setOnMouseEntered( e -> Utils.idleToHover(b) );
		b.setOnMouseExited( e -> Utils.hoverToIdle(b) );
		return b;
	}

	/**
	* newMenuButton( String )
	*
	* @description Creates a new MenuButton object and returns it.
	*
	*/
	private MenuButton newMenuButton( String s )
	{
		Font font = new Font( 16.0 );
		MenuButton newButton = new MenuButton( s );
		newButton.setFont( font );
		newButton.setMinWidth( 100.0 );
		newButton.setMinHeight( 30.0 );
		newButton.setMaxWidth( Double.MAX_VALUE );
		newButton.setContentDisplay( ContentDisplay.RIGHT );
		newButton.getItems().clear();
		newButton.setStyle(Constants.JFX_CSS_LGRY_BTN_IDLE);
		newButton.setOnMouseEntered( e -> Utils.idleToHover(newButton) );
		newButton.setOnMouseExited( e -> Utils.hoverToIdle(newButton) );
		return newButton;
	}

	/**
	* newSearchMenuItem( MenuButton, TextField, String )
	*
	* @description Creates a new SearchMenuItem object and returns it.
	*
	*/
	private MenuItem newSearchMenuItem( MenuButton menuButton, TextField searchBy, String s )
	{
		MenuItem m = new MenuItem( s );
		m.setOnAction( e ->
			{
				menuButton.setText( s );
				searchBy.setText( "" );
			}
		);
		return m;
	}

	/**
	* newGap()
	*
	* @description Returns a blank button.
	*
	*/
	private Button newGap()
	{
		Button b = new Button("");
		b.setVisible(false);
		return b;
	}

	/**
	* newColumnConstraints()
	*
	* @description Creates a new ColumnConstraints object and returns it.
	*
	*/
	private ColumnConstraints newColumnConstraints()
	{
		ColumnConstraints cc = new ColumnConstraints();
		cc.setHalignment( HPos.CENTER );
		cc.setPrefWidth( 100.0 );
		cc.setMinWidth( 10.0 );
		cc.setHgrow( Priority.SOMETIMES );
		return cc;
	}

}
