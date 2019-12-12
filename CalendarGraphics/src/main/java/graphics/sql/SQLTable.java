package sql;

import utils.Constants;
import utils.Utils;

import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQLTable
 *
 * @description Provides ease of access to a table within a database.
 *
 * @author Travis Zuleger
 */
public class SQLTable
{
	// tableName -> Name of the table in the database.
	// amountFields -> Amount of columns that are contained in this table.
	// fields -> All column names that are contained in this table.
	public final String tableName;
	private int amountFields;
	private String[] fields;


	/**
	 * CONSTRUCTOR (String, String[], int[], boolean[])
	 *
	 * @description Creates a new SQLTable object with the given parameters in the
	 *              program runtime. Also adds it into the SQL database.
	 */
	public SQLTable( String name, String[] fields, int[] varCharSize, boolean[] isNull )
	{
		tableName = name;
		if( !Database.tableExists( name ) )
		{
			amountFields = fields.length;
			String fieldsToAdd = "(";
			for( int i = 0; i < amountFields; ++i )
			{
				fieldsToAdd += fields[i] + " VARCHAR(" + varCharSize[i] + ") " + ( isNull[i] ? "NULL," : "NOT NULL," );
			}
			fieldsToAdd += ")";
			Database.update( "CREATE TABLE " + name + fieldsToAdd );
		}
		Database.addTable( this );
	}


	/**
	 * CONSTRUCTOR (String)
	 *
	 * @description Creates a SQLTable expecting that the database already contains
	 *              it. If it does contain the table, then it extracts all
	 *              information needed to create the SQLTable.
	 */
	public SQLTable( String name )
	{
		tableName = name;
		try
		{
			if( Database.tableExists( name ) )
			{
				ResultSet fields = Database.query( "SELECT * FROM " + name );
				ResultSetMetaData rsMetaData = fields.getMetaData();
				int size = rsMetaData.getColumnCount();
				this.fields = new String[size];
				for( int i = 1; i < size + 1; ++i )
				{
					this.fields[i - 1] = rsMetaData.getColumnName( i );
				}
				amountFields = this.fields.length;
			}
		} catch( SQLException e )
		{
			e.printStackTrace();
			System.out.println( "Error creating SQL table in Java Runtime." );
		}
	}


	/**
	 * addField(String, int, boolean)
	 *
	 * @description Adds a new field into the table.
	 */
	public void addField( String field, int varCharSize, boolean isNull )
	{
		Database.update(
				"ALTER TABLE" + tableName + " ADD " + field + " VARCHAR(" + varCharSize + ") "
						+ ( isNull ? "NULL" : "NOT NULL" )
				);
	}


	/**
	* deleteField(String)
	*
	* @description Deletes a field from a table in the database.
	*/
	public void deleteField( String field )
	{
		if( this.fieldExists( field ) )
		{
			Database.update( "ALTER TABLE " + tableName + " DROP COLUMN " + field );
		}
	}


	/**
	 * insert(String[])
	 *
	 * @description Adds a new entry into the table.
	 *
	 */
	public void insertEntry( String[] entryValues )
	{
		if( entryValues.length != amountFields )
		{
			return;
		}
		String toSend = "INSERT INTO " + tableName + " ("
				+ Constants.getTable( tableName ).getAllFields() + ") VALUES ('";
		String fieldsToAdd = "";
		for( int i = 0; i < amountFields - 1; ++i )
		{
			fieldsToAdd += entryValues[i] + "', '";
		}
		fieldsToAdd += entryValues[amountFields - 1] + "')";
		toSend += fieldsToAdd;

		Database.update( toSend );
	}


	/**
	 * deleteEntry(String[], String[])
	 *
	 * @description Deletes an entry from a Table in the connected database.
	 *
	 */
	public void deleteEntry( String[] fields, String[] values )
	{
		String toSend = "DELETE FROM " + tableName + " WHERE ";
		for( int i = 0; i < fields.length - 1; ++i )
		{
			toSend += fields[i] + " = '" + values[i] + "' AND ";
		}
		toSend += fields[fields.length - 1] + " = '" + values[fields.length - 1] + "'";
		Database.update( toSend );
	}


	/**
	 * editEntry(String[], String[], String[], String[])
	 *
	 * @description Edits values in a Table (from the connected Database)
	 *
	 */
	public void editEntry( String[] identifierFields, String[] identifierValues, String[] fieldsToEdit, String[] newValues )
	{
		String toSend = "UPDATE " + tableName + " SET ";
		for( int i = 0; i < fieldsToEdit.length - 1; ++i )
		{
			toSend += fieldsToEdit[i] + " = '" + newValues[i] + "', ";
		}
		toSend += fieldsToEdit[fieldsToEdit.length - 1] + " = '" + newValues[fieldsToEdit.length - 1] + "' ";
		toSend += " WHERE ";
		for( int i = 0; i < identifierFields.length - 1; ++i )
		{
			toSend += identifierFields[i] + " = '" + identifierValues[i] + "' AND ";
		}
		toSend += identifierFields[identifierFields.length - 1] + " = '" + identifierValues[identifierFields.length - 1] + "'";
		Database.update( toSend );
	}


	/**
	 * getAllForField(String, String)
	 *
	 * @description Queries the table in the database for all entries that hold the
	 *              value of type field. (example: get("title", "DENTIST") -> { {
	 *              "123456", "johndoe12", "John", "Doe", "DENTIST",
	 *              "aabbccddeeffffeeddccbbaa11223344" } { "654321", "joemomma69",
	 *              "Joe", "Momma", "DENTIST", "44332211aabbccddeeffffeeddccbbaa" }
	 *              }
	 *
	 */
	public String[][] getAllEntriesFor( String field, String value )
	{
		if( !this.fieldExists( field ) )
		{
			return null;
		}
		String toSend = "SELECT * FROM " + tableName + " WHERE " + field + " = '" + value + "'";
		try
		{
			int size = 0, idx1 = 0;
			ResultSet set = Database.query( toSend );
			while( set.next() )
			{
				size++;
			}
			String[][] entries = new String[size][amountFields]; // INITIALIZE SI
			Utils.reset( set );
			while( set.next() )
			{
				for( int i = 0; i < amountFields; ++i )
				{
					entries[idx1][i] = set.getString( fields[i] );
				}
				idx1++;
			}

			return entries;
		} catch( SQLException e )
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * getAllForField(String[], String[])
	 *
	 * @description Queries the table in the database for all entries that hold the
	 *              value of type field. (example: get("title", "DENTIST") -> { {
	 *              "123456", "johndoe12", "John", "Doe", "DENTIST",
	 *              "aabbccddeeffffeeddccbbaa11223344" } { "654321", "joemomma69",
	 *              "Joe", "Momma", "DENTIST", "44332211aabbccddeeffffeeddccbbaa" }
	 *              }
	 *
	 */
	public String[][] getAllEntriesFor( String[] field, String[] value )
	{
		String toSend = "SELECT * FROM " + tableName + " WHERE ";
		for(int i = 0; i < field.length - 1; ++ i)
		{
			toSend += field[i] + " = '" + value[i] + "' AND ";
		}
		toSend += field[field.length - 1] + " = '" + value[value.length - 1] + "';";
		try
		{
			int size = 0, idx1 = 0;
			ResultSet set = Database.query( toSend );
			while( set.next() )
			{
				size++;
			}
			String[][] entries = new String[size][amountFields]; // INITIALIZE SI
			Utils.reset( set );
			while( set.next() )
			{
				for( int i = 0; i < amountFields; ++i )
				{
					entries[idx1][i] = set.getString( fields[i] );
				}
				idx1++;
			}

			return entries;
		} catch( SQLException e )
		{
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * getEntry(String, String)
	 *
	 * @description Queries the database for all values associated with the entry
	 *              that holds value of type field.
	 *
	 */
	public String[] getEntry( String field, String value )
	{
		String toSend = "SELECT * FROM " + tableName + " WHERE " + field + " = " + value;
		try
		{
			int i = 0;
			ResultSet set = Database.query( toSend );
			String[] entry = new String[amountFields];
			while( set.next() )
			{
				entry[i] = set.getString( fields[i] );
				i++;
			}

			return entry;
		} catch( SQLException e )
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * getEntry(String[], String[])
	 *
	 * @description Queries the database for all values associated with the entries
	 *              that holds corresponding values.
	 *
	 */
	public String[] getEntry( String[] fields, String[] values )
	{
		String toSend = "SELECT * FROM " + tableName + " WHERE ";
		for(int i = 0; i < fields.length - 1; ++i )
		{
			toSend += fields[i] + " = '" + values[i] + "' AND ";
		}
		toSend += fields[fields.length - 1] + " = '" + values[values.length - 1] + "';";
		try
		{
			ResultSet set = Database.query( toSend );
			String[] entry = new String[amountFields];
			set.next();
			for(int i = 0; i < entry.length; ++i )
			{
				entry[i] = set.getString( this.fields[i] );
			}

			return entry;
		} catch( SQLException e )
		{
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * getValue(String, String, String)
	 *
	 * @description Returns a specific value from an entry in this table.
	 *
	 */
	public String getValue( String field, String value, String wantedField )
	{
		String toSend = "SELECT * FROM " + tableName + " WHERE " + field + " = '" + value + "'";
		try
		{
			ResultSet set = Database.query( toSend );
			set.next();
			return set.getString( wantedField );
		} catch( SQLException e )
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	* getValue(String[], String[], String)
	*
	* @description Queries a value from a SQLTable using multiple fields and multiple values.
	*
	*/
	public String getValue( String fields[], String values[], String wantedField )
	{
		String toSend = "SELECT * FROM " + tableName + " WHERE ";
		for(int i = 0; i < fields.length - 1; ++i)
		{
			toSend += fields[i] + " = '" + values[i] + "' AND ";
		}
		toSend += fields[fields.length - 1] + " = '" + values[values.length - 1] + "';";
		try
		{
			ResultSet set = Database.query( toSend );
			set.next();
			return set.getString( wantedField );
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * entryExists(String, String)
	 *
	 * @description Checks if the entry <entry> belonging to the field <field>
	 *              exists in the table.
	 *
	 */
	public boolean entryExists( String field, String value )
	{
		ResultSet set = Database.query( "SELECT * FROM " + tableName + " WHERE " + field + " = '" + value + "'" );
		try
		{
			Utils.reset( set );
			return set.next();
		} catch( SQLException e )
		{
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * entryExists(String, String)
	 *
	 * @description Returns true or false based on whether the entry's values exist
	 *              in this table or not.
	 *
	 */
	public boolean entryExists( String[] fields, String[] values )
	{
		String toSend = "SELECT * FROM " + tableName + " WHERE ";
		for( int i = 0; i < fields.length - 1; ++i )
		{
			toSend += fields[i] + " = '" + values[i] + "' AND ";
		}
		toSend += fields[fields.length - 1] + " = '" + values[values.length - 1] + "'";
		ResultSet set = Database.query( toSend );
		try
		{
			Utils.reset( set );
			return set.next();
		} catch( SQLException e )
		{
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * fieldExists(String)
	 *
	 * @description Checks if the field <field> exists in the table.
	 *
	 */
	private boolean fieldExists( String field )
	{
		boolean exists = false;
		for( int i = 0; i < fields.length; ++i )
		{
			exists = fields[i].equals( field );
			if( exists )
			{
				break;
			}
		}
		return exists;
	}

}
