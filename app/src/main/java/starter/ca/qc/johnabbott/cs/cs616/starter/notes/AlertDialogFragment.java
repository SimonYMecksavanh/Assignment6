package starter.ca.qc.johnabbott.cs.cs616.starter.notes;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * A fragment containing a simple AlertDialog.
 *  - Add custom title, message, action text and event handler.
 *
 * Usage:
 *
 *    DialogFragment newFragment = AlertDialogFragment.create(title, message, "OK", new DialogInterface.OnClickListener() {
 *        @Override
 *        public void onClick(DialogInterface dialogInterface, int which) {
 *            // TODO
 *        }
 *    });
 *    newFragment.show(getFragmentManager(), "alert");
 *
 * Usage (with dismiss):
 *
 *    DialogFragment newFragment = AlertDialogFragment.create(title, message, "OK", new DialogInterface.OnClickListener() {
 *        @Override
 *        public void onClick(DialogInterface dialogInterface, int which) {
 *            // TODO
 *        }
 *    }).setDismiss("Cancel", new DialogInterface.OnClickListener() {
 *        @Override
 *        public void onClick(DialogInterface dialogInterface, int which) {
 *            // TODO
 *        }
 *    });
 *    newFragment.show(getFragmentManager(), "alert");
 *
 *
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
    private String dismiss;
    private DialogInterface.OnClickListener dismissListener;

    /**
     * Set the dismiss action text and the click listener that executes when dismiss is clicked
     * - The click listener will also run when the dialog is dismissed by clicking away
     * @param dismiss
     * @param dismissListener
     * @return
     */
    public AlertDialogFragment setDismiss(String dismiss, DialogInterface.OnClickListener dismissListener) {
        this.dismiss = dismiss;
        this.dismissListener = dismissListener;
        return this;
    }

    /**
     * Set the click listener that executes when this dialog is dismissed by clicking away
     * - The click listener will also run when the dialog is dismissed by clicking away
     * @param dismissListener
     * @return
     */
    public AlertDialogFragment setDismissListener(DialogInterface.OnClickListener dismissListener) {
        this.dismissListener = dismissListener;
        return this;
    }

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
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(dismissListener != null)
            dismissListener.onClick(dialog, -1);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_error_black_48dp)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(action, actionListener)
                .setNegativeButton(dismiss, dismissListener)
                .create();
    }

}
