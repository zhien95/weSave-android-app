package com.yong.wesave.util;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yong.wesave.R;

public class CommentHolder {
  public LinearLayout entry;
  public TextView username,comment, datetime_posted;
  
  public CommentHolder(View commentView){
    entry = commentView.findViewById(R.id.commentEntry);
    username = commentView.findViewById(R.id.comment_user_name);
    comment = commentView.findViewById(R.id.userComment);
    datetime_posted = commentView.findViewById(R.id.comment_datetime);
  }
}
