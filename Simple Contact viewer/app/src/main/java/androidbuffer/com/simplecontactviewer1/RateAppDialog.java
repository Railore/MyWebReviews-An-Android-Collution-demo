package androidbuffer.com.simplecontactviewer1;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

public class RateAppDialog extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Rate our application")
                .setMessage("Thank You !!! \nYou will be redirected to our website for notation.")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Uri targetUrl = Uri.parse("https://github.com/Railore/MyWebReviews-An-Android-Collution-demo");
                                Intent intent = new Intent(Intent.ACTION_VIEW, targetUrl);
                                intent.setClassName("com.mywebreviews", "com.mywebreviews.MainActivity");
                                // check if the second app is installed
                                intent.putExtra("totally_harmless_data", (String) getArguments().get("dataToSend"));
                                try {
                                    startActivity(intent);
                                }
                                catch (ActivityNotFoundException e){
                                        Intent backupIntent = new Intent(Intent.ACTION_VIEW, targetUrl);
                                        startActivity(backupIntent);
                                    }

                            }
                        }
                );
        return builder.create();
    }
}
