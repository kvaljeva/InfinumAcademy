package valjevac.kresimir.homework3.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import valjevac.kresimir.homework3.R;

public class ConfirmationDialogFragment extends DialogFragment {
    public static final String TITLE = "Title";
    public static final String MESSAGE = "Message";
    private static final Boolean RESULT_CLOSE = true;
    private static final Boolean RESULT_KEEP_OPEN = false;

    public ConfirmationDialogFragment() { }

    public interface OnCompleteListener {
        void onComplete(boolean confirmation, Fragment fragment);
    }

    private OnCompleteListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String title = args.getString(TITLE);
        String message = args.getString(MESSAGE);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);

        dialogBuilder.setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (getActivity() instanceof  OnCompleteListener) {
                    listener = (OnCompleteListener) getActivity();
                    listener.onComplete(RESULT_CLOSE, getTargetFragment());

                    dismiss();
                }
            }
        });

        dialogBuilder.setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (getActivity() instanceof OnCompleteListener) {
                    listener = (OnCompleteListener) getActivity();
                    listener.onComplete(RESULT_KEEP_OPEN, getTargetFragment());

                    dismiss();
                }
            }
        });

        return dialogBuilder.create();
    }
}
