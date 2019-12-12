package sql;

import utils.*;
import java.sql.*;

/**
 * Database
 *
 * @description Abstract class of Database that provides the functions needed
 *              for sending arbitrary commands and handling a lot of
 *              data-at-rest information.
 *
 */
public final class Database
{
	protected static Connection connection;
	protected static Statement statement;

	private static SQLTable[] sql_tables;

	private static boolean connected = false;
	private static boolean initialized = false;


	/**
	 * init()
	 *
	 * @description Initializes the program runtime with all current tables in the
	 *              database.
	 *
	 */
	public static void init( boolean resetTables )
	{
		if( initialized && connected )
			return;

		String[] tableNames = Database.getTableNames();
		if( tableNames == null ) return;

		if( resetTables )
		{
			for( int i = 0; i < tableNames.length; ++i )
			{
				update( "TRUNCATE " + tableNames[i] );
			}
		}
		for( Constants.TABLES table : Constants.TABLES.values() )
		{
			Database.newTable( table.name(), table.fields(), table.varCharSizes(), table.canHoldNull() );
		}
		initialized = true;
	}


	/**
	 * connectTo(String)
	 *
	 * @description Provides connection to an arbitrary SQL server, if one is
	 *              provided. If null, then this method connects to local host:
	 *              "jdbc:mysql://localhost:3306/cs341db?useJDBCCompliantTimezoneShift=true&serverTimezone=UTC"
	 *
	 */
	public static void connectTo( String ipAddress, String dbName )
	{
		String sqlServer;
		if( ipAddress == null )
		{
			sqlServer = "jdbc:mysql://localhost:3306/" + dbName + "?useJDBCCompliantTimezoneShift=true&serverTimezone=UTC";
		}
		else
		{
			sqlServer = "jdbc:mysql://" + ipAddress + ":3306/" + dbName;
		}

		try
		{
			connection = DriverManager.getConnection( sqlServer, "root", "password" );
			statement = connection.createStatement();
			connected = true;
			System.out.println( "Connected to the database." );
		} catch( SQLException e )
		{
			System.out.println( "Error connecting to SQL Database" );
			e.printStackTrace();
		}
	}


	/**
	 * getTable(String)
	 *
	 * @description Gets a table name from the list of tables from the database.
	 *
	 */
	public static SQLTable getTable( String name )
	{
		for( int i = 0; i < sql_tables.length; ++i )
		{
			if( sql_tables[i].tableName.equals( name ) )
			{
				return sql_tables[i];
			}
		}
		return null;
	}


	/**
	 * addTable(SQLTable)
	 *
	 * @description Updates <sql_tables> to have the newest SQLTable that has been
	 *              created.
	 *
	 */
	public static void addTable( SQLTable table )
	{
		SQLTable[] newTables;
		if( sql_tables == null )
		{
			newTables = new SQLTable[1];
		} else
		{
			newTables = new SQLTable[sql_tables.length + 1];
			for( int i = 0; i < sql_tables.length; ++i )
			{
				newTables[i] = sql_tables[i];
			}
		}
		newTables[newTables.length - 1] = table;
		sql_tables = newTables;
	}

	/**
	 * newTable(String, String[], int[], boolean[])
	 *
	 * @description Creates a new table and adds it to the MySQL database.
	 *
	 */
	public static void newTable( String name, String[] fields, int[] varCharSizes, boolean[] canHoldNull )
	{
		String cmd = "CREATE TABLE IF NOT EXISTS " + name + "( ";
		for( int i = 0; i < fields.length; ++i )
		{
			cmd += fields[i] + " VARCHAR(" + varCharSizes[i] + ") " + ( canHoldNull[i] ? "NULL" : "NOT NULL" )
					+ ( i < fields.length - 1 ? ", " : "" );
		}
		cmd += ")";
		update( cmd );
		SQLTable table = new SQLTable( name );
		Database.addTable( table );
	}


	/**
	 * tableExists(String)
	 *
	 * @description Checks if the table that goes by the name, <name>, is already in
	 *              the database.
	 *
	 */
	public static boolean tableExists( String name )
	{
		try
		{
			DatabaseMetaData dbm = connection.getMetaData();
			ResultSet tables = dbm.getTables( null, null, name, null );
			return tables.next();
		} catch( SQLException e )
		{
			System.out.println( "Error checking if table exists." );
			e.printStackTrace();
			return true;
		}
	}


	/**
	 * getTableNames()
	 *
	 * @description Queries the MySQL server for all of the tables in the current
	 *              database.
	 *
	 */
	public static String[] getTableNames()
	{
		try
		{
			Statement stmt = connection.createStatement();
			ResultSet tables = stmt.executeQuery( "Show tables" );
			int size = 0;
			while( tables.next() )
			{
				size++;
			}
			String[] tableNames = new String[size];
			int i = 0;
			Utils.reset( tables );
			while( tables.next() )
			{
				tableNames[i] = tables.getString( 1 );
				i++;
			}
			return tableNames;
		} catch( SQLException e )
		{
			System.out.println( "Error getting table names." );
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * query(String)
	 *
	 * @description Sends a command to the SQL server, querying the server for
	 *              something to return. This function returns with an SQLException
	 *              if the command is invalid on the server.
	 *
	 */
	public static ResultSet query( String command )
	{
		try
		{
			return statement.executeQuery( command );
		}
		catch( SQLException e )
		{
			System.out.println( "Error executing command: " + command );
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * update(String)
	 *
	 * @description Sends a command to the SQL server, updating the server with new
	 *              information. This function returns with an SQLException if the
	 *              command is invalid on the server.
	 *
	 */
	public static void update( String command )
	{
		try
		{
			statement.executeUpdate( command );
		}
		catch( SQLException e )
		{
			System.out.println( "Error executing command: " + command );
			e.printStackTrace();
		}
	}

}
