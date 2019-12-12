package controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.fxml.Initializable;

import sql.Database;
import utils.*;
import utils.Constants.*;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.URL;

/**
 * UserController extends SuperController implements Initializable
 *
 * @description Controller of the Main User Screen, which applies to Hygienists,
 *              Dentists, and Patients.
 *
 * @lastedited Nov 14, 2019
 * @author Travis Zuleger
 * @date Nov 19, 2019
 */
public final class UserController extends SuperController implements Initializable
{

	// INTERFACES FOR STORING DYNAMIC FUNCTIONS.
	private static interface VoidFunction<ParameterType>
	{
		void invoke(ParameterType ... args);
	}

	private static interface ReturnFunction<ParameterType, ReturnType>
	{
		ReturnType invoke(ParameterType ... args);
	}

	// SCHEDULE OF DAY ANCHOR PANE
	@ FXML private AnchorPane scheduleOfDayAPane;

	// EDIT APPOINTMENT ANCHOR PANE
	@ FXML private AnchorPane editAptAPane;
	@ FXML private Button editConfirmButton, editCancelAptButton;
	@ FXML private ChoiceBox<String> editReqFacDropDown, editAptTypeDropDown, editTimeDropDown, editNewTimeDropDown, editMonthDropDown, editNewMonthDropDown;
	@ FXML private TextField editDayTextField, editNewDayTextField;
	@ FXML private Label editErrorMsg;

	// ADD APPOINTMENT ANCHOR PANE
	@ FXML private AnchorPane addAptAPane;
	@ FXML private Button addConfirmButton;
	@ FXML private ChoiceBox addReqFacDropDown, addAptTypeDropDown, addTimeDropDown;
	@ FXML private Label addErrorMsg;

	// SCHEDULE OF DAY ANCHOR PANE
	@ FXML private Label currentScheduleForLabel;
	@ FXML private ListView listOfAppointments;
	@ FXML private ChoiceBox<String> selectedEmployee;

	// CALENDAR ANCHOR PANE
	@ FXML private GridPane calendarGrid;
	@ FXML private Button prevMonthButton, nextMonthButton;
	@ FXML private Label monthLabel;

	@ FXML private Button toggleButton, logoutButton;

	private String mmddyyyy = null;

	/**
	 * initialize(URL, ResourceBundle)
	 *
	 * @description Initializes the screen with the proper components.
	 *
	 */
	public void initialize( URL loc, ResourceBundle res )
	{
		setCalendar( -1 );
		currentScheduleForLabel.setText( "No Appointments to Show" );
		listOfAppointments.getItems().clear();
		addErrorMsg.setVisible(false);
		editErrorMsg.setVisible(false);
		VoidFunction initNode = (args) ->
			{
				((Node) args[0]).setStyle( (String) args[1] );
				((Node) args[0]).setOnMouseEntered( e -> Utils.idleToHover( (Node) args[0] ) );
				((Node) args[0]).setOnMouseExited( e -> Utils.hoverToIdle( (Node) args[0] ) );
			};
		initNode.invoke( logoutButton, Constants.JFX_CSS_RED_BTN_IDLE );
		initNode.invoke( toggleButton, Constants.JFX_CSS_YEL_BTN_IDLE );
		initNode.invoke( editConfirmButton, Constants.JFX_CSS_YEL_BTN_IDLE );
		initNode.invoke( editCancelAptButton, Constants.JFX_CSS_RED_BTN_IDLE );
		initNode.invoke( addConfirmButton, Constants.JFX_CSS_YEL_BTN_IDLE );
		initNode.invoke( addReqFacDropDown, Constants.JFX_CSS_YEL_BTN_IDLE );
		initNode.invoke( addAptTypeDropDown, Constants.JFX_CSS_YEL_BTN_IDLE );
		initNode.invoke( addTimeDropDown, Constants.JFX_CSS_YEL_BTN_IDLE );
		initNode.invoke( editReqFacDropDown, Constants.JFX_CSS_YEL_BTN_IDLE );
		initNode.invoke( editAptTypeDropDown, Constants.JFX_CSS_YEL_BTN_IDLE );
		initNode.invoke( editTimeDropDown, Constants.JFX_CSS_YEL_BTN_IDLE );
		initNode.invoke( editNewTimeDropDown, Constants.JFX_CSS_YEL_BTN_IDLE );
		initNode.invoke( editMonthDropDown, Constants.JFX_CSS_YEL_BTN_IDLE );
		initNode.invoke( editNewMonthDropDown, Constants.JFX_CSS_YEL_BTN_IDLE );
	}


	/**
	 * refresh()
	 *
	 * @description Refreshes the screen so the components are in sync with the SQL
	 *              database.
	 *
	 */
	public void refresh()
	{
		setCalendar( -1 );
		fillAppointments();
		initChoiceBoxes();
	}


	/**
	 * logout()
	 *
	 * @description Handles the action when the User clicks on the logout button.
	 *
	 */
	public void logout()
	{
		SuperController.logUserOut();
	}


	/**
	 * toggle()
	 *
	 * @description Handles the Add/Edit pane when the "Toggle to ADD/Toggle to
	 *              EDIT" button is pressed.
	 *
	 */
	public void toggle()
	{
		if( addAptAPane.isVisible() )
		{
			addAptAPane.setVisible( false );
			editAptAPane.setVisible( true );
			toggleButton.setText( "Toggle to ADD" );
		} else
		{
			addAptAPane.setVisible( true );
			editAptAPane.setVisible( false );
			toggleButton.setText( "Toggle to EDIT" );
		}
	}


	/**
	 * confirmEdit()
	 *
	 * @description Handles the action when the User presses Confirm when editing an
	 *              appointment.
	 *
	 */
	public void confirmEdit()
	{
		if( editTimeDropDown.getValue() == null )
		{
			editErrorMsg.setText( "Please select the time you would like to change." );
			editErrorMsg.setVisible( true );
			return;
		}
		String start = ( ( String ) editTimeDropDown.getValue() ).split( " - " )[0];
		String end = ( ( String ) editTimeDropDown.getValue() ).split( " - " )[1];
		String title = TABLES.USERS.table().getValue( "user_id", SuperController.getUserId(), "title" );
		boolean isEmployee = title.equals("DENTIST") || title.equals("HYGIENIST");
		String[] fields, values;
		if ( isEmployee )
		{
			fields = new String[] { "employee_id", "MM_DD_YYYY", "block_start", "block_end" };
			values = new String[] { SuperController.getUserId(), mmddyyyy, start, end };
		}
		else
		{
			fields = new String[] { "patient_id", "MM_DD_YYYY", "block_start", "block_end" };
			values = new String[] { SuperController.getUserId(), mmddyyyy, start, end };
		}
		boolean aptExists = TABLES.APPOINTMENTS.table().entryExists( fields, values );

		if( aptExists )
		{
			editErrorMsg.setVisible( false );
			String[] originalEntry = TABLES.APPOINTMENTS.table().getEntry( fields, values );
			String[] newEntry = Arrays.copyOf( originalEntry, originalEntry.length );
			if( editNewTimeDropDown.getValue() != null )
			{
				String[] vals = ( ( String ) editNewTimeDropDown.getValue() ).split( " - " );
				newEntry[TABLES.APPOINTMENTS.getFieldIdx("block_start")] = vals[0];
				newEntry[TABLES.APPOINTMENTS.getFieldIdx("block_end")] = vals[1];
			}
			if( editReqFacDropDown.getValue() != null ) // Edit the faculty of the appointment.
			{
				String[] nameTokens = ((String) editReqFacDropDown.getValue()).split(" ");
				String newEmployeeID = TABLES.USERS.table().getValue(
					new String[] { "first_name", "last_name" },
					new String[] { nameTokens[0], nameTokens[1] },
					"user_id"
				);
				newEntry[TABLES.APPOINTMENTS.getFieldIdx("employee_id")] = newEmployeeID;
			}
			if( editAptTypeDropDown.getValue() != null ) // Edit the description of the appointment.
			{
				newEntry[TABLES.APPOINTMENTS.getFieldIdx("description")] = ( String ) editAptTypeDropDown.getValue();
			}
			String newDay = editNewDayTextField.getText();
			String newMonth = (String) editNewMonthDropDown.getValue();
			String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
			for(int i = 0; i < months.length; ++i)
			{
				if( newMonth.equals(months[i]) )
				{
					newMonth = "" + (i + 1);
				}
			}
			String newMMDDYYYY = newMonth + "/" + newDay + "/" + mmddyyyy.split("/")[2];
			//if( newDay < 1 || newDay > getDaysInMonth(newMonth) )
			newEntry[TABLES.APPOINTMENTS.getFieldIdx("MM_DD_YYYY")] = newMMDDYYYY;
			TABLES.APPOINTMENTS.table().editEntry(
				TABLES.APPOINTMENTS.fields(),
				originalEntry,
				TABLES.APPOINTMENTS.fields(),
				newEntry
			);
			editErrorMsg.setVisible( false );
			fillAppointments();
			setCalendar( -1 );
		}
		else
		{
			editErrorMsg.setText( "The time you had selected does not have an appointment \nor it does not belong to you." );
			editErrorMsg.setVisible( true );
		}
	}


	/**
	 * cancelEdit()
	 *
	 * @description Handles the action when the User clicks on the Cancel
	 *              Appointment button
	 *
	 */
	public void cancelEdit()
	{
		if( editTimeDropDown.getValue() == null )
		{
			editErrorMsg.setText( "Please select the time you would like to change." );
			editErrorMsg.setVisible( true );
			return;
		}
		String start = ( ( String ) editTimeDropDown.getValue() ).split( " - " )[0];
		String end = ( ( String ) editTimeDropDown.getValue() ).split( " - " )[1];
		String[] fields = { "patient_id", "MM_DD_YYYY", "block_start", "block_end" };
		String[] values = { SuperController.getUserId(), mmddyyyy, start, end };
		boolean aptExists1 = TABLES.APPOINTMENTS.table().entryExists( fields, values );
		fields = new String[] { "employee_id", "MM_DD_YYYY", "block_start", "block_end" };
		values = new String[] { SuperController.getUserId(), mmddyyyy, start, end };
		boolean aptExists2 = TABLES.APPOINTMENTS.table().entryExists( fields, values );
		if( aptExists1 || aptExists2 )
		{
			fields = new String[] { aptExists1 ? "patient_id" : "employee_id", "MM_DD_YYYY", "block_start", "block_end" };
			values = new String[] { SuperController.getUserId(), mmddyyyy, start, end };
			TABLES.APPOINTMENTS.table().deleteEntry( fields, values );
			SuperController.refreshAll();
		}
	}


	/**
	 * confirmAdd()
	 *
	 * @description Handles the action when the User clicks on the Confirm button
	 *              when adding an appointment.
	 *
	 */
	public void confirmAdd()
	{
		if( addTimeDropDown.getValue() == null )
		{
			addErrorMsg.setText( "Please select the time you would like to change." );
			addErrorMsg.setVisible( true );
			return;
		}
		String start = ( ( String ) addTimeDropDown.getValue() ).split( " - " )[0];
		String end = ( ( String ) addTimeDropDown.getValue() ).split( " - " )[1];
		String empName[] = ( ( String ) addReqFacDropDown.getValue() ).split(" ");
		String employeeID = TABLES.USERS.table().getValue( new String[] { "first_name", "last_name" }, new String[] { empName[0], empName[1] }, "user_id");
		String[] fields = { "employee_id", "MM_DD_YYYY", "block_start", "block_end" };
		String[] values = { employeeID, mmddyyyy, start, end };
		boolean isUnavailable = TABLES.APPOINTMENTS.table().entryExists( fields, values );
		if( isUnavailable )
		{
			handleAptAddError();
			return;
		}

		String[] vals;
		if(TABLES.USERS.table().getValue("user_id", employeeID, "title").equals("PATIENT"))
		{
			vals =
			new String[] {
				employeeID,
				SuperController.getUserId(),
				( String ) addAptTypeDropDown.getValue(),
				( ( String ) addTimeDropDown.getValue() ).split( " - " )[0],
				( ( String ) addTimeDropDown.getValue() ).split( " - " )[1],
				mmddyyyy
			};
		}
		else
		{
			vals =
			new String[] {
				SuperController.getUserId(),
				employeeID,
				( String ) addAptTypeDropDown.getValue(),
				( ( String ) addTimeDropDown.getValue() ).split( " - " )[0],
				( ( String ) addTimeDropDown.getValue() ).split( " - " )[1],
				mmddyyyy
			};
		}
		TABLES.APPOINTMENTS.table().insertEntry( vals );
		fillAppointments();
		setCalendar( -1 );
	}


	/**
	 * setCalendar(int)
	 *
	 * @description Graphically creates and refreshes the calendar initially and
	 *              when Refresh is called.
	 *
	 */
	private void setCalendar( int month )
	{
		ReturnFunction style = args ->
			{
				Button b = new Button( (String) args[0] );
				b.setStyle( (String) args[1] );
				b.setOnMouseEntered( e -> Utils.idleToHover( b ) );
				b.setOnMouseExited( e -> Utils.hoverToIdle( b ) );
				b.setMaxWidth( calendarGrid.getPrefWidth() );
				b.setMaxHeight( calendarGrid.getPrefHeight() );
				return b;
			};

		DateFormat mm = new SimpleDateFormat( "MM" );
		DateFormat yyyy = new SimpleDateFormat( "yyyy" );
		Date date = new Date();
		if( month == -1 )
		{
			GregorianCalendar cal = new GregorianCalendar();
			int today = cal.get( Calendar.DAY_OF_MONTH );
			cal.set( Calendar.DAY_OF_MONTH, 1 );
			int firstWeekDay = cal.get( Calendar.DAY_OF_WEEK );
			int dayOfMonth = 1;
			int dayOfWeek = firstWeekDay;
			for( int i = 0; i < calendarGrid.getRowCount(); ++i )
			{
				for( int j = 0; j < calendarGrid.getColumnCount(); ++j )
				{
					if( isInvalidDay( dayOfMonth, firstWeekDay, i, j ) )
					{
						Button b = (Button) style.invoke( "", Constants.JFX_CSS_LGRY_BTN_IDLE );
						calendarGrid.add(b, j, i);
						continue;
					}
					Button b = (Button) style.invoke( "" + dayOfMonth, Constants.JFX_CSS_GRN_BTN_IDLE );
					if( isOffDay( dayOfMonth, today, dayOfWeek, cal.get( Calendar.MONTH ) ) )
					{
						b.setStyle( Constants.JFX_CSS_DGRY_BTN_IDLE );
						calendarGrid.add( b, j, i );
						dayOfMonth++;
						dayOfWeek = dayOfWeek >= 7 ? 1 : dayOfWeek + 1; // Roll over day of the week
						continue;
					}
					String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
					final String mmf = mm.format( date ), yyyyf = yyyy.format( date ); // MM format
					final String mmddyyyy = mmf + "/" + dayOfMonth + "/" + yyyyf; // YYYY format
					final int d = dayOfMonth;
					final String m = months[Integer.parseInt(mmf) - 1]; // Month as name.
					b.setOnAction( e ->
						{
							editMonthDropDown.setValue( m );
							editNewMonthDropDown.setValue( m );
							editDayTextField.setText( "" + d );
							editNewDayTextField.setText( "" + d );
							this.mmddyyyy = mmf + "/" + d + "/" + yyyyf;
							fillAppointments();
						}
					);

					if( TABLES.APPOINTMENTS.table().entryExists( "MM_DD_YYYY", mmddyyyy ) )
					{
						String newStyle;
						if( TABLES.APPOINTMENTS.table().getAllEntriesFor( "MM_DD_YYYY", mmddyyyy ).length == 8)
						{
							newStyle = Constants.JFX_CSS_RED_BTN_IDLE;
						}
						else
						{
							newStyle = Constants.JFX_CSS_YEL_BTN_IDLE;
						}
						b.setStyle( newStyle );
					}
					calendarGrid.add( b, j, i );
					dayOfMonth++;
					dayOfWeek = dayOfWeek >= 7 ? 1 : dayOfWeek + 1; // Roll over day of the week.
				}
			}
		}
	}

	/**
	* isInvalidDay(int, int ,int, int )
	*
	* @description Returns a boolean based on whether a pane in the calendar is within the month or not.
	*
	*/
	private boolean isInvalidDay( int day, int firstWeekDay, int i, int j )
	{
		return (i == 0 && j < (firstWeekDay - 1)) || day > 31;
	}

	/**
	* isOffDay(int, int ,int, int )
	*
	* @description Returns a boolean based on whether that day is an off day.
	*
	*/
	private boolean isOffDay( int day, int today, int dayOfWeek, int month )
	{
		month++;
		String[] holidays = { "12/24", "12/25", "12/31", "01/01" }; // Only these holidays for the demo and project, otherwise insert all known holidays that rely on day of the month.
		boolean isHoliday = false;
		String todayString = month + "/" + day;
		for(int k = 0; k < holidays.length; ++k)
		{
			isHoliday = todayString.equals(holidays[k]);
			if( isHoliday ) return isHoliday;
		}
		return dayOfWeek == 1 || dayOfWeek == 7 || day < today;
	}

	/**
	 * fillAppointments()
	 *
	 * @description Fills the appointments list with all appointments for that day.
	 *
	 */
	private void fillAppointments()
	{
		listOfAppointments.getItems().clear();
		if(mmddyyyy == null)
		{
			currentScheduleForLabel.setText( "No Appointments to Show" );
			return;
		}
		currentScheduleForLabel.setText("Schedule for " + mmddyyyy);

		VoidFunction<String> __add_to_pane__ = (args) ->
			{
				listOfAppointments.getItems().add( args[0] + " - " + args[1] + ": " + args[2] );
				listOfAppointments.setOnMouseClicked( e ->
				{
					String item = "" + listOfAppointments.getSelectionModel().getSelectedItem();
					String blockTime = item.split(": ")[0];
					String desc = item.split(": ")[1];
					String fac = (String) selectedEmployee.getValue();
					if( editAptAPane.isVisible() )
					{
						if( desc.equals("AVAILABLE") )
						{
							editErrorMsg.setText("You must choose a day that is not available!");
							editErrorMsg.setVisible(true);
							editTimeDropDown.setValue(null);
							editAptTypeDropDown.setValue(null);
							editNewTimeDropDown.setValue(null);
							editReqFacDropDown.setValue(null);
						}
						else
						{
							editTimeDropDown.setValue(blockTime);
							editAptTypeDropDown.setValue(desc);
							editReqFacDropDown.setValue(fac);
						}
					}
					else
					{
						addTimeDropDown.setValue(blockTime);
						addReqFacDropDown.setValue(fac);
						if( !desc.equals("AVAILABLE") )
						{
							addAptTypeDropDown.setValue(desc);
						}
					}
				});
			};

		final int BUSINESS_HOURS_START = 8, BUSINESS_HOURS_END = 16;
		if(TABLES.USERS.table().getValue("user_id", SuperController.getUserId(), "title").equals("PATIENT"))
		{
			patientFA( __add_to_pane__ );
		}
		else
		{
			employeeFA( __add_to_pane__ );
		}
	}

	/**
	 * initChoiceBoxes()
	 *
	 * @description Creates the Choice Boxes with the items that need to be added.
	 *
	 */
	private void initChoiceBoxes()
	{
		clearChoiceBoxes();

		monthAndTimeCBinit();

		if(TABLES.USERS.table().getValue( "user_id", SuperController.getUserId(), "title" ).equals( TITLES.PATIENT.name() ) )
		{
			patientCBinit();
		}
		else
		{
			employeeCBinit();
		}

		listenersCBinit();
	}

	/**
	 * handleAptAddError()
	 *
	 * @description Sets the error messages to visible and to their respective
	 *              messages when an add appointment fails.
	 *
	 */
	private void handleAptAddError()
	{
		if(selectedEmployee == null || selectedEmployee.getValue() == null) return;
		String start = ( ( String ) addTimeDropDown.getValue() ).split( " - " )[0]; // start = xx of xx:yy
		String end = ( ( String ) addTimeDropDown.getValue() ).split( " - " )[1]; // end = yy of xx:yy

		String[] fields = new String[] { "patient_id", "MM_DD_YYYY", "block_start", "block_end" };
		String[] values = new String[] { SuperController.getUserId(), mmddyyyy, start, end };
		boolean belongsToUser1 = TABLES.APPOINTMENTS.table().entryExists( fields, values );

		fields = new String[] { "employee_id", "MM_DD_YYYY", "block_start", "block_end" };
		values = new String[] { SuperController.getUserId(), mmddyyyy, start, end };
		boolean belongsToUser2 = TABLES.APPOINTMENTS.table().entryExists( fields, values );

		if( belongsToUser1 || belongsToUser2 )
		{
			addErrorMsg.setText( "You already reserved this time for an appointment!" );
		} else
		{
			addErrorMsg.setText( "This appointment time is already occupied!" );
		}
		addErrorMsg.setVisible( true );
		return;
	}

	/**
	* employeeFA( VoidFunction )
	*
	* @description Fills Appointments to the User Screen for when an employee is logged in.
	*
	*/
	private void employeeFA( VoidFunction<String> __add_to_pane__ )
	{
		if(selectedEmployee == null || selectedEmployee.getValue() == null) return;
		final int BUSINESS_HOURS_START = 8, BUSINESS_HOURS_END = 16;
		String[] patientName = ((String) selectedEmployee.getValue()).split(" ");
		String patientID = TABLES.USERS.table().getValue(new String[] { "first_name", "last_name" }, new String[] { patientName[0], patientName[1] }, "user_id");
		System.out.println(patientID);
		for(int i = BUSINESS_HOURS_START; i < BUSINESS_HOURS_END; ++i)
		{
			String start = i < 10 ? "0" + ( i * 100 ) : i * 100 + ""; // Conform to military time
			String end = ( i + 1 ) < 10 ? "0" + ( ( i + 1 ) * 100 ) : ( i + 1 ) * 100 + ""; // Conform to military time
			String description = "AVAILABLE";
			boolean patientHasApt = TABLES.APPOINTMENTS.table().entryExists(
				new String[] { "patient_id", "MM_DD_YYYY", "block_start", "block_end" },
				new String[] { patientID, mmddyyyy, start, end } ) ;
			String employeeID = SuperController.getUserId();

			if(TABLES.USERS.table().getValue("user_id", employeeID, "title").equals("HYGIENIST"))
			{
				employeeID = TABLES.USERS.table().getValue("user_id", employeeID, "partner_id");
			}
			boolean employeeHasApt = TABLES.APPOINTMENTS.table().entryExists(
				new String[] { "employee_id", "MM_DD_YYYY", "block_start", "block_end" },
				new String[] { employeeID, mmddyyyy, start, end } );
			if( patientHasApt && employeeHasApt )
			{
				description = TABLES.APPOINTMENTS.table().getValue( "employee_id", employeeID, "description" );
			}
			else if( patientHasApt )
			{
				description = "BUSY";
			}
			__add_to_pane__.invoke(start, end, description);
		}
	}

	/**
	* patientFA( VoidFunction )
	*
	* @description Fills Appointments to the User Screen for when an patient is logged in.
	*
	*/
	private void patientFA( VoidFunction<String> __add_to_pane__ )
	{
		final int BUSINESS_HOURS_START = 8, BUSINESS_HOURS_END = 16;
		String[] employeeName = ((String) selectedEmployee.getValue()).split(" ");
		String employeeID = TABLES.USERS.table().getValue(new String[] { "first_name", "last_name" }, new String[] { employeeName[0], employeeName[1] }, "user_id");
		for(int i = BUSINESS_HOURS_START; i < BUSINESS_HOURS_END; ++i)
		{
			String start = i < 10 ? "0" + ( i * 100 ) : i * 100 + ""; // Conform to military time
			String end = ( i + 1 ) < 10 ? "0" + ( ( i + 1 ) * 100 ) : ( i + 1 ) * 100 + ""; // Conform to military time
			String description = "AVAILABLE";
			boolean employeeHasApt = TABLES.APPOINTMENTS.table().entryExists(
				new String[] { "employee_id", "MM_DD_YYYY", "block_start", "block_end" },
				new String[] { employeeID, mmddyyyy, start, end } ) ;
			boolean patientHasApt = TABLES.APPOINTMENTS.table().entryExists(
				new String[] { "patient_id", "MM_DD_YYYY", "block_start", "block_end" },
				new String[] { SuperController.getUserId(), mmddyyyy, start, end } );
			if( employeeHasApt && patientHasApt )
			{
				description = TABLES.APPOINTMENTS.table().getValue( "patient_id", SuperController.getUserId(), "description" );
			}
			else if( employeeHasApt )
			{
				description = "BUSY";
			}
			__add_to_pane__.invoke(start, end, description);
		}
	}

	/**
	* monthAndTimeCBinit()
	*
	* @description Initializes the Month and Time ChoiceBoxes
	*
	*/
	private void monthAndTimeCBinit()
	{
		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		for(int i = 0; i < 12; ++i)
		{
			editMonthDropDown.getItems().add( "" + months[i] );
			editNewMonthDropDown.getItems().add( "" + months[i] );
		}
		for( int i = 8; i < 16; ++i )
		{
			String start = i < 10 ? "0" + ( i * 100 ) : i * 100 + "";
			String end = ( i + 1 ) < 10 ? "0" + ( ( i + 1 ) * 100 ) : ( i + 1 ) * 100 + "";
			addTimeDropDown.getItems().add( start + " - " + end );
			editTimeDropDown.getItems().add( start + " - " + end );
			editNewTimeDropDown.getItems().add( start + " - " + end );
		}

		addAptTypeDropDown.getItems().add( "Cleaning and Checkup" );
		editAptTypeDropDown.getItems().add( "Cleaning and Checkup" );
		addAptTypeDropDown.getItems().add( "X-Ray and Cavity-filling" );
		editAptTypeDropDown.getItems().add( "X-Ray and Cavity-filling" );
		addAptTypeDropDown.getItems().add( "Tooth Extraction" );
		editAptTypeDropDown.getItems().add( "Tooth Extraction" );
		addAptTypeDropDown.getItems().add( "Root Canal I" );
		editAptTypeDropDown.getItems().add( "Root Canal I" );
		addAptTypeDropDown.getItems().add( "Root Canal II" );
		editAptTypeDropDown.getItems().add( "Root Canal II" );
	}

	/**
	* patientCBinit()
	*
	* @description Initializes the ChoiceBoxes when a patient is logged in.
	*
	*/
	private void patientCBinit()
	{
		int statusIdx = TABLES.USERS.getFieldIdx("status");
		int firstNameIdx = TABLES.USERS.getFieldIdx("first_name");
		int lastNameIdx = TABLES.USERS.getFieldIdx("last_name");
		String[][] allDentists = TABLES.USERS.table().getAllEntriesFor( "title", TITLES.DENTIST.name() );
		for( int i = 0; i < allDentists.length; ++i )
		{
			if(allDentists[i][statusIdx].equals("DISABLED")) continue;
			addReqFacDropDown.getItems().add( allDentists[i][firstNameIdx] + " " + allDentists[i][lastNameIdx] );
			editReqFacDropDown.getItems().add( allDentists[i][firstNameIdx] + " " + allDentists[i][lastNameIdx] );
			selectedEmployee.getItems().add( allDentists[i][firstNameIdx] + " " + allDentists[i][lastNameIdx] );
		}
		selectedEmployee.setVisible(true);
	}

	/**
	* employeeCBinit()
	*
	* @description Initializes the ChoiceBoxes when an employee is logged in.
	*
	*/
	private void employeeCBinit()
	{
		int statusIdx = TABLES.USERS.getFieldIdx("status");
		int firstNameIdx = TABLES.USERS.getFieldIdx("first_name");
		int lastNameIdx = TABLES.USERS.getFieldIdx("last_name");
		String[][] allPatients = TABLES.USERS.table().getAllEntriesFor( "title", TITLES.PATIENT.name() );
		for( int i = 0; i < allPatients.length; ++i )
		{
			if(allPatients[i][statusIdx].equals("DISABLED")) continue;
			addReqFacDropDown.getItems().add( allPatients[i][firstNameIdx] + " " + allPatients[i][lastNameIdx] );
			editReqFacDropDown.getItems().add( allPatients[i][firstNameIdx] + " " + allPatients[i][lastNameIdx] );
			selectedEmployee.getItems().add( allPatients[i][firstNameIdx] + " " + allPatients[i][lastNameIdx] );
		}
		selectedEmployee.setVisible(true);
	}

	/**
	* listenersCBinit()
	*
	* @description Adds listeners to Faculty Drop Down lists.
	*
	*/
	private void listenersCBinit()
	{
		editReqFacDropDown.getSelectionModel().selectedIndexProperty().addListener( (obs, o1, o2) ->
			{
				editReqFacDropDown.getSelectionModel().select((int) o2);
				fillAppointments();
			}
		);
		addReqFacDropDown.getSelectionModel().selectedIndexProperty().addListener( (obs, o1, o2) ->
			{
				selectedEmployee.getSelectionModel().select((int) o2);
				addReqFacDropDown.getSelectionModel().select((int) o2);
				fillAppointments();
			}
		);
		selectedEmployee.getSelectionModel().selectedIndexProperty().addListener( (obs, o1, o2) ->
			{
				selectedEmployee.getSelectionModel().select((int) o2);
				fillAppointments();
			}
		);
	}

	/**
	* clearChoiceBoxes()
	*
	* @description Clears all ChoiceBoxes
	*
	*/
	private void clearChoiceBoxes()
	{
		editNewMonthDropDown.getItems().clear();
		editMonthDropDown.getItems().clear();
		addTimeDropDown.getItems().clear();
		editTimeDropDown.getItems().clear();
		editNewTimeDropDown.getItems().clear();
		addAptTypeDropDown.getItems().clear();
		editAptTypeDropDown.getItems().clear();
		selectedEmployee.getItems().clear();
		addReqFacDropDown.getItems().clear();
		editReqFacDropDown.getItems().clear();
	}

}
