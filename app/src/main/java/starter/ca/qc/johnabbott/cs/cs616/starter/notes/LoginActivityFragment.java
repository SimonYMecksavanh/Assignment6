package starter.ca.qc.johnabbott.cs.cs616.starter.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import starter.ca.qc.johnabbott.cs.cs616.starter.notes.server.AsyncHttpRequest;
import starter.ca.qc.johnabbott.cs.cs616.starter.notes.server.HttpProgress;
import starter.ca.qc.johnabbott.cs.cs616.starter.notes.server.HttpResponse;
import starter.ca.qc.johnabbott.cs.cs616.starter.notes.server.User;
import starter.ca.qc.johnabbott.cs.cs616.starter.notes.AlertDialogFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends Fragment {

    private Button loginButton;
    private EditText usernameText;
    private EditText passwordText;
    private TextView errorUsernameText;
    private TextView errorPasswordText;
    private TextView usernameLabel;
    private TextView passwordLabel;
    private ProgressBar progressBar;

    public LoginActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = (Button) root.findViewById(R.id.login_button);
        usernameText = (EditText) root.findViewById(R.id.username_editText);
        passwordText = (EditText) root.findViewById(R.id.password_editText);
        errorUsernameText = (TextView) root.findViewById(R.id.errorUsername_textView);
        errorPasswordText = (TextView) root.findViewById(R.id.errorPassword_textView);
        usernameLabel = (TextView) root.findViewById(R.id.username_textView);
        passwordLabel = (TextView) root.findViewById(R.id.password_textView);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set default colors
                usernameText.getBackground().setColorFilter(getResources().getColor(R.color.defaultColor), PorterDuff.Mode.SRC_ATOP);
                passwordText.getBackground().setColorFilter(getResources().getColor(R.color.defaultColor), PorterDuff.Mode.SRC_ATOP);
                usernameLabel.setTextColor(getResources().getColor(R.color.defaultColor));
                passwordLabel.setTextColor(getResources().getColor(R.color.defaultColor));
                //Clear the error text
                errorUsernameText.setText("");
                errorPasswordText.setText("");
                //Display error for missing username
                if(usernameText.getText().toString().isEmpty()){
                    usernameText.getBackground().setColorFilter(getResources().getColor(R.color.errorColor), PorterDuff.Mode.SRC_ATOP);
                    usernameLabel.setTextColor(getResources().getColor(R.color.errorColor));
                    errorUsernameText.setText("Username is Required");
                //Display error for missing password
                }else if(passwordText.getText().toString().isEmpty()){
                    passwordText.getBackground().setColorFilter(getResources().getColor(R.color.errorColor), PorterDuff.Mode.SRC_ATOP);
                    passwordLabel.setTextColor(getResources().getColor(R.color.errorColor));
                    errorPasswordText.setText("Password is Required");
                }else
                    validUser();
            }
        });


        return root;
    }

    private boolean validUser(){
        final boolean[] valid = {false};
        String url = "http://10.0.2.2:9999/user/search/findByName?name=" + usernameText.getText().toString();
        AsyncHttpRequest task = new AsyncHttpRequest(url, AsyncHttpRequest.Method.GET);
        task.setOnResponseListener(new AsyncHttpRequest.OnResponse() {
            @Override
            public void onResult(HttpResponse response) {
                if(response.getStatus() == 200){
                    User user = User.parse(response.getBody());
                    if(user.isPassword(passwordText.getText().toString())){
                        Toast.makeText(getContext(), "Successfully logged in", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), NoteListActivity.class);
                        String userCreatedUrl = user.getUrl() + "/created";
                        intent.putExtra("userUrl", userCreatedUrl);
                        startActivityForResult(intent, 123);
                        valid[0] = true;
                    }else{
                        //Display errors for invalid password
                        passwordText.setText("");
                        Toast.makeText(getContext(), "Invalid Password", Toast.LENGTH_LONG).show();
                        passwordLabel.setTextColor(getResources().getColor(R.color.errorColor));
                        passwordText.getBackground().setColorFilter(getResources().getColor(R.color.errorColor), PorterDuff.Mode.SRC_ATOP);
                        errorPasswordText.setText("Invalid Password");
                        valid[0] = false;
                    }

                }else{
                    //Display error for invalid user
                    passwordText.setText("");
                    Toast.makeText(getContext(), "Invalid User", Toast.LENGTH_LONG).show();
                    usernameLabel.setTextColor(getResources().getColor(R.color.errorColor));
                    usernameText.getBackground().setColorFilter(getResources().getColor(R.color.errorColor), PorterDuff.Mode.SRC_ATOP);
                    errorUsernameText.setText("Invalid User");
                    valid[0] = false;
                }
                //reset progress bar to invisible
                progressBar.setVisibility(View.INVISIBLE);
                enable();
            }

            @Override
            public void onProgress(HttpProgress progress) {
                progressBar.setVisibility(View.VISIBLE);
                disable();
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                final AlertDialogFragment dialogFragment = AlertDialogFragment.create("Server Error", "There was a problem connecting to the server, Sorry for the inconvenience.", "Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        validUser();
                    }
                });
                dialogFragment.show(getFragmentManager(), "alert");


                enable();
            }
        });

        task.execute();

        return valid[0];
    }

    private void enable() {
        usernameText.setEnabled(true);
        passwordText.setEnabled(true);
        usernameLabel.setEnabled(true);
        passwordLabel.setEnabled(true);
        loginButton.setEnabled(true);
    }

    private void disable() {
        usernameText.setEnabled(false);
        passwordText.setEnabled(false);
        usernameLabel.setEnabled(false);
        passwordLabel.setEnabled(false);
        loginButton.setEnabled(false);
    }
}

