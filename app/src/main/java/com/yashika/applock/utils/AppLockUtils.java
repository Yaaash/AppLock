package com.yashika.applock.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.yashika.applock.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class contains all utils eatures of Application
 *
 * @author yashika.
 */
class AppLockUtils {

    /**
     * This method shows a default dialog box to alert user
     *
     * @param message String
     * @param context Context
     */
    static void showMessageAlertDialog(String message, Context context) {

        if(context != null && context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_dialog_layout, null);
            builder.setView(view);
            final ViewHolder holder = new ViewHolder(view);

            if(holder.alertDialogTitleTextView != null && holder.alertDialogMessageTextView != null) {

                holder.alertDialogTitleTextView.setText(context.getResources().getString(R.string.app_name));

                if(!TextUtils.isEmpty(message)) {
                    holder.alertDialogMessageTextView.setText(message);
                }
            }

            final AlertDialog alertDialog = builder.create();
                alertDialog.show();

            if(holder.alertDialogTitleTextView != null) {
                holder.alertDialogTitleTextView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        final int DRAWABLE_RIGHT = 2;

                        if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_DOWN) {
                            if(event.getRawX() >= (holder.alertDialogTitleTextView.getRight() - holder.alertDialogTitleTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                alertDialog.dismiss();
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
    }

      static class ViewHolder {
        @BindView(R.id.alert_dialog_title_text_view)
        TextView alertDialogTitleTextView;
        @BindView(R.id.alert_dialog_message_text_view)
        TextView alertDialogMessageTextView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
