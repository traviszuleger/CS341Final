package utils;

import sql.Database;
import sql.SQLTable;

/**
 * Constants
 *
 * @description Owns all of the enums that are associated with the Dentist
 *              Office application.
 *
 * @lastedited Nov. 19, 2019
 * @author Travis Zuleger
 * @date Nov 19, 2019
 */
public final class Constants
{
	// JFX QUICK STYLING
	public final static String
		JFX_CSS_RED_BTN_IDLE = "-fx-background-color: #ff0000; -fx-border-color: #000000; -fx-border-width: 1; -fx-border-style: solid; -fx-cursor: hand;",
		JFX_CSS_RED_BTN_HOVER = "-fx-background-color: #dd0000; -fx-border-color: #000000; -fx-border-width: 2; -fx-border-style: solid; -fx-cursor: hand;",
		JFX_CSS_GRN_BTN_IDLE = "-fx-background-color: #00ff00; -fx-border-color: #000000; -fx-border-width: 1; -fx-border-style: solid; -fx-cursor: hand;",
		JFX_CSS_GRN_BTN_HOVER = "-fx-background-color: #00dd00; -fx-border-color: #000000; -fx-border-width: 2; -fx-border-style: solid; -fx-cursor: hand;",
		JFX_CSS_YEL_BTN_IDLE = "-fx-background-color: #fff000; -fx-border-color: #000000; -fx-border-width: 1; -fx-border-style: solid; -fx-cursor: hand;",
		JFX_CSS_YEL_BTN_HOVER = "-fx-background-color: #ddd000; -fx-border-color: #000000; -fx-border-width: 2; -fx-border-style: solid; -fx-cursor: hand;",
		JFX_CSS_LGRY_BTN_IDLE = "-fx-background-color: #cccccc; -fx-border-color: #000000; -fx-border-width: 1; -fx-border-style: solid;",
		JFX_CSS_LGRY_BTN_HOVER = "-fx-background-color: #cccccc; -fx-border-color: #000000; -fx-border-width: 2; -fx-border-style: solid;",
		JFX_CSS_DGRY_BTN_IDLE = "-fx-background-color: #aaaaaa; -fx-border-color: #000000; -fx-border-width: 1; -fx-border-style: solid;",
		JFX_CSS_DGRY_BTN_HOVER = "-fx-background-color: #888888; -fx-border-color: #000000; -fx-border-width: 2; -fx-border-style: solid;",
		JFX_CSS_BLU_BTN_IDLE = "-fx-background-color: #0000ff; -fx-border-color: #000000; -fx-border-width: 1; -fx-border-style: solid;",
		JFX_CSS_BLU_BTN_HOVER = "-fx-background-color: #0000dd; -fx-border-color: #000000; -fx-border-width: 2; -fx-border-style: solid;";

	// Titles held by employees at the Dentist Office.
	public enum TITLES
	{
		PATIENT, HYGIENIST, DENTIST, ADMIN;
	}

	// Tables associated with the SQL database.
	public enum TABLES
	{
		USERS(
			new String[] { "user_id", "first_name", "last_name", "partner_id", "email", "phone_number", "title", "pass_hash", "status" },
			new int[] { 10, 20, 20, 10, 50, 14, 10, 32, 8 },
			new boolean[] { false, false, false, true, true, true, false, false, false }
		),
		APPOINTMENTS(
			new String[] { "patient_id", "employee_id", "description", "block_start", "block_end", "MM_DD_YYYY"},
			new int[] { 10, 10, 50, 4, 4, 11 },
			new boolean[] { false, false, false, false, false, false }
		);

		private String fields[];
		private int varCharSizes[];
		private boolean canHoldNull[];


		TABLES( String[] fields, int[] varCharSizes, boolean[] canHoldNull )
		{
			this.fields = fields;
			this.varCharSizes = varCharSizes;
			this.canHoldNull = canHoldNull;
		}


		public SQLTable table()
		{
			return Database.getTable( this.name() );
		}


		public String getField( int idx )
		{
			return fields[idx];
		}


		public int getFieldIdx( String field )
		{
			for(int i = 0; i < fields.length; ++i)
			{
				if( field.equals(fields[i]) ) return i;
			}
			return -1;
		}


		public String getAllFields()
		{
			String s = "";
			for( int i = 0; i < fields.length - 1; ++i )
			{
				s += fields[i] + ", ";
			}
			s += fields[fields.length - 1];
			return s;
		}


		public String[] fields()
		{
			return fields;
		}


		public int[] varCharSizes()
		{
			return varCharSizes;
		}


		public boolean[] canHoldNull()
		{
			return canHoldNull;
		}
	}

	// Screens that the application switches between.
	public enum SCREEN
	{
		USER_SCREEN, LOGIN_SCREEN, ADMIN_SCREEN;
	}

	// FXML pages that go hand in hand with the SCREEN enum.
	public enum PAGE
	{
		ADMIN_EMPLOYEE_ROSTER( "/graphics/fxml/admin_employee_roster.fxml" ),
		LOGIN( "/graphics/fxml/loginPage.fxml" ),
		USER_SCREEN( "/graphics/fxml/basePage.fxml" );

		private String dir;


		PAGE( String dir )
		{
			this.dir = dir;
		}


		public String getDir()
		{
			return dir;
		}
	}

	// Authentication Messages that manage the Authenticity of a user's login
	// credentials.
	public enum AUTH_MSG
	{
		USER_DOES_NOT_EXIST, INVALID_CREDENTIALS, VALID_CREDENTIALS, VOID_FIELD, DISABLED_ACCOUNT;
	}


	/**
	 * getTable(String)
	 *
	 * @description Returns a TABLE enum based on the name given.
	 *
	 * @param name -> Name of the TABLE.
	 * @return TABLE enum that holds all of the metadata associated with the SQL
	 *         table.
	 */
	public static TABLES getTable( String name )
	{
		for( TABLES t : TABLES.values() )
		{
			if( t.name().equals( name ) )
			{
				return t;
			}
		}
		return null;
	}

}
