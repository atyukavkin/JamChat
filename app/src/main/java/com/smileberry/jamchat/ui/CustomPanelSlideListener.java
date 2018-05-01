package com.smileberry.jamchat.ui;

import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class CustomPanelSlideListener implements SlidingUpPanelLayout.PanelSlideListener {

    private RelativeLayout.LayoutParams paramsSendMessage;
    private ListView chatList;

    public CustomPanelSlideListener(RelativeLayout.LayoutParams paramsSendMessage, ListView chatList) {
        this.paramsSendMessage = paramsSendMessage;
        this.chatList = chatList;
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
    }

    @Override
    public void onPanelExpanded(View panel) {
        paramsSendMessage.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        paramsSendMessage.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
        chatList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPanelCollapsed(View panel) {
        paramsSendMessage.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        paramsSendMessage.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        chatList.setVisibility(View.GONE);
    }

    @Override
    public void onPanelAnchored(View panel) {
    }

    @Override
    public void onPanelHidden(View panel) {
    }
}
