package valjevac.kresimir.homework3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class ConfirmationDialog extends DialogFragment {
    public static final String TITLE = "Title";
    public static final String MESSAGE = "Message";
    private static final Boolean RESULT_CLOSE = true;
    private static final Boolean RESULT_KEEP_OPEN = false;

    public ConfirmationDialog() { }

    public interface OnCompleteListener {
        void onComplete(boolean confirmation);
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

        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener = (OnCompleteListener) getActivity();
                listener.onComplete(RESULT_CLOSE);

                dismiss();
            }
        });

        dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener = (OnCompleteListener) getActivity();
                listener.onComplete(RESULT_KEEP_OPEN);

                dismiss();
            }
        });

        return dialogBuilder.create();
    }
}
