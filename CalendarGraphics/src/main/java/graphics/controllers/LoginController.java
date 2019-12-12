package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tab;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import utils.Constants.*;
import utils.Utils;

/**
 * LoginController extends SuperController
 *
 * @description Handles the functionality of the login screen.
 *
 * @edit_history
 * @edited Oct. 22, 2019: Travis Zuleger -> Added functionality that allows the
 *         access to the SQL database.
 * @edited Oct. 31, 2019: Travis Zuleger -> Added authenticate() method and
 *         updated class so it is conformative to the updated SQLTable library.
 * @edited Nov. 14, 2019: Travis Zuleger -> Updated the class to handle the new
 *         new Login page, which handles both signing in and signing up.
 *
 * @lastedited Nov. 14, 2019
 * @author Phil Sample
 * @date Oct 01, 2019
 */
public final class LoginController extends SuperController
{

	@ FXML private Tab signinTab, signupTab;

	// SIGN IN TAB
	@ FXML private TextField signInUsernameField;
	@ FXML private PasswordField signInPasswordField;
	@ FXML private Button signInButton;
	@ FXML private Text errorMessage;

	// SIGN UP TAB
	@ FXML private TextField firstNameField, lastNameField, emailField, signUpUsernameField;
	@ FXML private TextField areaCodeField, prefixField, postfixField;
	@ FXML private PasswordField signUpPasswordField, passwordCheckField;
	@ FXML private Text error1, error2, error3, error4;
	@ FXML private Button signUpButton;


	/**
	 * signIn()
	 *
	 * @description Checks the <signInUsernameField> and <signInPasswordField> to
	 *              check if they exist and authenticate the credentials. If they do
	 *              this method forwards the user to their respective page.
	 *
	 */
	@ FXML
	private void signIn()
	{
		String username = this.signInUsernameField.getText().toLowerCase();
		String password = this.signInPasswordField.getText();

		boolean error = false;
		AUTH_MSG msg = authenticate( username, password );

		if( msg == AUTH_MSG.VALID_CREDENTIALS )
		{
			SuperController.logUserIn(
					username,
					TABLES.USERS.table().getValue( "user_id", Utils.getHashCode( username ), "first_name" ),
					TABLES.USERS.table().getValue( "user_id", Utils.getHashCode( username ), "last_name" )
			);
			String title = TABLES.USERS.table().getValue( "user_id", Utils.getHashCode( username ), "title" );

			if( TITLES.HYGIENIST.name().equals( title ) )
			{
				SuperController.switchScreen( SCREEN.USER_SCREEN );
			}
			else if( TITLES.PATIENT.name().equals( title ) )
			{
				SuperController.switchScreen( SCREEN.USER_SCREEN );
			}
			else if( TITLES.ADMIN.name().equals( title ) )
			{
				SuperController.switchScreen( SCREEN.ADMIN_SCREEN );
			}
			else if( TITLES.DENTIST.name().equals( title ) )
			{
				SuperController.switchScreen( SCREEN.USER_SCREEN );
			}
			else
			{
				errorMessage.setText( "The username, \"" + username + "\", does not have a valid Title in the database." );
				errorMessage.setVisible( true );
			}
			SuperController.refreshAll();
			clearSignInFields();
		}
		else if( msg == AUTH_MSG.INVALID_CREDENTIALS )
		{
			errorMessage.setText( "Invalid password entered." );
			errorMessage.setVisible( true );
		}
		else if( msg == AUTH_MSG.USER_DOES_NOT_EXIST )
		{
			errorMessage.setText( "The username, \"" + username + "\", does not exist." );
			errorMessage.setVisible( true );
		}
		else if( msg == AUTH_MSG.VOID_FIELD )
		{
			errorMessage.setText( "The username or password field is empty." );
			errorMessage.setVisible( true );
		}
		else if( msg == AUTH_MSG.DISABLED_ACCOUNT )
		{
			errorMessage.setText( "This account has been disabled, please contact a System Administrator for more information." );
			errorMessage.setVisible( true );
		}
		else
		{
			errorMessage.setVisible( false );
		}
	}


	/**
	 * signUp()
	 *
	 * @description Method that handles the moment the user clicks on signup.
	 *
	 */
	@ FXML
	private void signUp()
	{
		// USERNAME IS 20 CHARACTERS MAX, ALPHANUMERIC CHARACTERS
		// PASSWORD IS 8-30 CHARACTERS LONG, UPPER CASE, LOWER CASE, NUMBER.

		String username = this.signUpUsernameField.getText().toLowerCase();
		String password = this.signUpPasswordField.getText();
		String phoneNumber = "(" + this.areaCodeField.getText() + ") " + this.prefixField.getText() + "-"
				+ this.postfixField.getText();
		String email = this.emailField.getText().toLowerCase();
		String firstName = this.firstNameField.getText().toLowerCase();
		String lastName = this.lastNameField.getText().toLowerCase();
		boolean passwordsMatch = password.equals( this.passwordCheckField.getText() );
		boolean errorExists = false;
		boolean usedEmail = false;
		boolean usedPhone = false;

		if( TABLES.USERS.table().entryExists( "user_id", Utils.getHashCode( username ) ) )
		{
			error4.setText( "Username already exists." );
			error4.setVisible( true );
			errorExists = true;
		}
		else
		{
			error4.setVisible( false );
		}

		if( !passwordsMatch )
		{
			error4.setText( "Passwords do not match." );
			error3.setVisible( true );
			errorExists = true;
		}
		else
		{
			error3.setVisible( false );
		}

		if( email.length() == 0 )
		{
			if( phoneNumber.length() != 14 )
			{
				error2.setText( "Phone Number entered is an invalid U.S. Number" );
				error2.setVisible( true );
				errorExists = true;
			}
			else
			{
				usedPhone = true;
				error2.setVisible( false );
			}
		}
		else
		{
			if( phoneNumber.length() != 14 && phoneNumber.length() != 4 )
			{
				error2.setText( "Phone Number entered is an invalid U.S. Number" );
				error2.setVisible( true );
				errorExists = true;
			}
			else
			{
				usedPhone = true;
				error2.setVisible( false );
			}

			if(!email.contains( "@" ) ||
				( !email.contains( ".com" ) &&
					!email.contains( ".net" )	&&
					!email.contains( ".org" ) &&
					!email.contains( ".edu" )
				)
			)
			{
				error1.setText( "Email entered is an invalid email. (Must contain '@' and a valid Top-Level Domain (.net, .com, .org, .edu))" );
				error1.setVisible( true );
				errorExists = true;
			}
			else
			{
				usedEmail = true;
				error1.setVisible( false );
			}
		}

		if( !errorExists )
		{
			email = usedEmail ? email : null;
			phoneNumber = usedPhone ? phoneNumber : null;
			String[] values =
			{
				Utils.getHashCode( username ),
				firstName,
				lastName,
				null,
				email,
				phoneNumber,
				TITLES.PATIENT.name(),
				Utils.hash( username + password ),
				"ENABLED"
			};
			TABLES.USERS.table().insertEntry( values );
			SuperController.refreshAll();
			clearSignUpFields();
		}
	}


	/**
	 * authenticate(<arg_type1> <arg_type2> ... <arg_typeN>)
	 *
	 * @description <DESCRIPTION HERE>
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	private AUTH_MSG authenticate( String username, String password )
	{
		if( username.length() == 0 )
		{
			return AUTH_MSG.VOID_FIELD;
		}

		if( TABLES.USERS.table().entryExists( "user_id", Utils.getHashCode( username ) ) )
		{
			String phash = TABLES.USERS.table().getValue( "user_id", Utils.getHashCode( username ), "pass_hash" );

			if( phash.equals( Utils.hash( username + password ) ) )
			{
				if( TABLES.USERS.table().getValue( "user_id", Utils.getHashCode( username ), "status" ).equals("DISABLED") )
				{
					return AUTH_MSG.DISABLED_ACCOUNT;
				}
				return AUTH_MSG.VALID_CREDENTIALS;
			}
			else
			{
				return AUTH_MSG.INVALID_CREDENTIALS;
			}
		}
		return AUTH_MSG.USER_DOES_NOT_EXIST;
	}


	/**
	 * clearSignUpFields(<arg_type1> <arg_type2> ... <arg_typeN>)
	 *
	 * @description <DESCRIPTION HERE>
	 *
	 */
	private void clearSignUpFields()
	{
		firstNameField.setText( "" );
		lastNameField.setText( "" );
		emailField.setText( "" );
		signUpUsernameField.setText( "" );
		areaCodeField.setText( "" );
		prefixField.setText( "" );
		postfixField.setText( "" );
		signUpPasswordField.setText( "" );
		passwordCheckField.setText( "" );
		error1.setText( "" );
		error2.setText( "" );
		error3.setText( "" );
		error4.setText( "" );
	}

	private void clearSignInFields()
	{
		signInUsernameField.setText( "" );
		signInPasswordField.setText( "" );
	}
}
