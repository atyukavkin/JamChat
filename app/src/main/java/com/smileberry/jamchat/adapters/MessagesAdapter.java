package com.smileberry.jamchat.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smileberry.jamchat.MainActivity;
import com.smileberry.jamchat.R;
import com.smileberry.jamchat.application.CustomApplicationContext;
import com.smileberry.jamchat.model.Message;

import java.util.List;

public class MessagesAdapter extends ArrayAdapter<Message> {

    private LayoutInflater inflater;
    private List<Message> items;
    private Context context;

    public MessagesAdapter(Context context, List<Message> items) {
        super(context, R.layout.chat_message, items);
        inflater = (LayoutInflater) (getContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup viewGroup) {
        final ViewHolder viewHolder;

        final Message message = getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chat_message, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.textLeft = (TextView) convertView.findViewById(R.id.textleft);
            viewHolder.left = (RelativeLayout) convertView.findViewById(R.id.left);
            viewHolder.leftIcon = (ImageView) convertView.findViewById(R.id.leftIcon);
            viewHolder.textRight = (TextView) convertView.findViewById(R.id.textRight);
            viewHolder.right = (RelativeLayout) convertView.findViewById(R.id.right);
            viewHolder.rightIcon = (ImageView) convertView.findViewById(R.id.rightIcon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivityContext = (MainActivity) context;
                //Switch view to map and prepare buttons
                mainActivityContext.updateViewWhenRedirectingFromChat(message);
            }
        };

        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showUnitsDialog(message.getMessage());

                return false;
            }
        };

        if (!message.isTheSameDevice(((CustomApplicationContext) context.getApplicationContext()).getDeviceId())) {
            viewHolder.textLeft.setText(message.getMessage());
            viewHolder.left.setVisibility(View.VISIBLE);
            viewHolder.right.setVisibility(View.GONE);
            viewHolder.leftIcon.setOnClickListener(onClickListener);
            viewHolder.textLeft.setOnLongClickListener(onLongClickListener);
        } else {
            viewHolder.textRight.setText(message.getMessage());
            viewHolder.right.setVisibility(View.VISIBLE);
            viewHolder.left.setVisibility(View.GONE);
            viewHolder.rightIcon.setOnClickListener(onClickListener);
            viewHolder.textRight.setOnLongClickListener(onLongClickListener);
        }

        return convertView;
    }

    private void showUnitsDialog(final String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.copy_text_dialog);

        TextView copyText = (TextView) dialog.findViewById(R.id.copyText);
        copyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(message);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied message", message);
                    clipboard.setPrimaryClip(clip);
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public List<Message> getItems() {
        return items;
    }

    private static class ViewHolder {
        TextView textLeft;
        RelativeLayout left;
        ImageView leftIcon;
        RelativeLayout right;
        ImageView rightIcon;
        TextView textRight;
    }
}
