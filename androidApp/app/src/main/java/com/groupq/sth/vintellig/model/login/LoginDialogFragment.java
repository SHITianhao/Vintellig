package com.groupq.sth.vintellig.model.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.groupq.sth.vintellig.R;
import com.groupq.sth.vintellig.ShareContext;
import com.groupq.sth.vintellig.model.connection.LoginConnection;

import java.util.concurrent.ExecutionException;

/**
 * Created by sth on 10/06/15.
 */
public class LoginDialogFragment extends DialogFragment {
    private ShareContext shareContext;
    public void setContext(ShareContext shareContext){
        this.shareContext = shareContext;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_signin, null);
        final EditText username = (EditText)view.findViewById(R.id.username);
        final EditText password = (EditText)view.findViewById(R.id.password);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        String name = username.getText().toString().trim();
                        String psw = password.getText().toString().trim();
                        try {
                            boolean loginResult = new LoginConnection(shareContext).execute(name,psw).get();
                            if (!loginResult)
                                Toast.makeText(getActivity().getBaseContext(),R.string.loginFailed,Toast.LENGTH_SHORT).show();
                            else {
                                // get username before fire login
                                shareContext.setUserName(name);
                                //fire login state changed
                                shareContext.setLoginState(true);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
