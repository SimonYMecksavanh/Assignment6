package starter.ca.qc.johnabbott.cs.cs616.starter.notes.server;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import starter.ca.qc.johnabbott.cs.cs616.starter.notes.R;

/**
 * A fragment containing a simple AlertDialog.
 *  - Add custom title, message, action text and event handler.
 *
 * Usage:
 *    DialogFragment newFragment = AlertDialogFragment.create(title, message, "OK", new DialogInterface.OnClickListener() {
 *        @Override
 *        public void onClick(DialogInterface dialogInterface, int i) {
 *            // TODO
 *        }
 *    });
 *    newFragment.show(getFragmentManager(), "alert");
 *
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 */
public class AlertDialogFragment extends DialogFragment {

    /**
     * Create an AlertDialogFragment
     * @param title the dialog title.
     * @param message the dialog message.
     * @param action the action text.
     * @param actionListener the event handler for the action.
     * @return the AlertDialogFragment with the above set.
     */
    public static AlertDialogFragment create(String title, String message, String action, DialogInterface.OnClickListener actionListener) {
        return new AlertDialogFragment()
                .setTitle(title)
                .setMessage(message)
                .setAction(action)
                .setActionListener(actionListener);
    }

    /* fields */
    private String title;
    private String message;
    private String action;
    private DialogInterface.OnClickListener actionListener;

    /**
     * Set action event handler.
     * @param actionListener
     * @return
     */
    public AlertDialogFragment setActionListener(DialogInterface.OnClickListener actionListener) {
        this.actionListener = actionListener;
        return this;
    }

    /**
     * Set alert dialog message.
     * @param message
     * @return
     */
    public AlertDialogFragment setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Set alert dialog title.
     * @param title
     * @return
     */
    public AlertDialogFragment setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Set alert dialog action text.
     * @param action
     * @return
     */
    public AlertDialogFragment setAction(String action) {
        this.action = action;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_error_black_48dp)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(action, actionListener)
                .create();
    }

}
