package starter.ca.qc.johnabbott.cs.cs616.starter.notes;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import starter.ca.qc.johnabbott.cs.cs616.starter.notes.server.AsyncHttpRequest;
import starter.ca.qc.johnabbott.cs.cs616.starter.notes.server.HttpProgress;
import starter.ca.qc.johnabbott.cs.cs616.starter.notes.server.HttpResponse;
import starter.ca.qc.johnabbott.cs.cs616.starter.notes.server.User;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends Fragment {

    private Button loginButton;
    private EditText usernameText;
    private EditText passwordText;

    public LoginActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = (Button) root.findViewById(R.id.login_button);
        usernameText = (EditText) root.findViewById(R.id.username_editText);
        passwordText = (EditText) root.findViewById(R.id.password_editText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        passwordText.setText("");
                        Toast.makeText(getContext(), "Invalid Password", Toast.LENGTH_LONG).show();
                        valid[0] = false;
                    }

                }else{
                    usernameText.setText("");
                    passwordText.setText("");
                    Toast.makeText(getContext(), "Invalid User", Toast.LENGTH_LONG).show();
                    valid[0] = false;
                }
            }

            @Override
            public void onProgress(HttpProgress progress) {

            }

            @Override
            public void onError(Exception e) {

            }
        });

        task.execute();

        return valid[0];
    }
}

