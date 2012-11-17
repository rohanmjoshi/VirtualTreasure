package com.northeastern.numad.virtual.treasure;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button lButton = (Button) findViewById(R.id.button1);
		OnClickListener sClick = new OnClickListener() {
			public void onClick(View v) {
				onCreateDialog().show();

			}
		};

		lButton.setOnClickListener(sClick);

	}

	public Dialog onCreateDialog() {

		final Intent intentThingsToDo = new Intent(this,
				ThingsToDoActivity.class);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		final View v = inflater.inflate(R.layout.login_dialog, null);
		final EditText usernameEditText = (EditText) v
				.findViewById(R.id.username);
		final EditText passwordEditText = (EditText) v
				.findViewById(R.id.password);
		builder.setView(v)
				.setPositiveButton("Enter",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								if (usernameEditText.getText().toString()
										.equals("")) {
									Toast toast = Toast.makeText(
											MainActivity.this,
											"UserName cannot be blank",
											Toast.LENGTH_LONG);
									toast.show();
								} else if (passwordEditText.getText()
										.toString().equals("")) {
									Toast toast = Toast.makeText(
											MainActivity.this,
											"Passowrd cannot be blank",
											Toast.LENGTH_LONG);
									toast.show();
								} else
									startActivity(intentThingsToDo);
							}
						})
				.setNegativeButton("Back",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								onCreateDialog().cancel();
							}
						});

		return builder.create();
	}
}
