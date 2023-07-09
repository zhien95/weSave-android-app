package com.yong.wesave.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.yong.wesave.R;
import com.yong.wesave.apiobject.Comment;
import com.yong.wesave.util.CommentHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CommentAdapter extends ArrayAdapter<Comment> {
  int resource;
  List<Comment> commentList;
  
  public CommentAdapter(Context context,int resource, ArrayList<Comment> commentList){
    super(context,resource,commentList);
    this.resource = resource;
    this.commentList = commentList;
  }
  
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = convertView;
    CommentHolder commentHolder;
    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) parent.getContext()
              .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(resource, null);
      commentHolder = new CommentHolder(view);
      commentHolder.username = view.findViewById(R.id.comment_user_name);
      commentHolder.comment = view.findViewById(R.id.userComment);
      commentHolder.datetime_posted = view.findViewById(R.id.comment_datetime);
      view.setTag(commentHolder);
    } else
      commentHolder = (CommentHolder) view.getTag();
    
    commentHolder.username.setText(commentList.get(position).username);
    commentHolder.comment.setText(commentList.get(position).comment);
    commentHolder.datetime_posted.setText(convertDateFormat(commentList.get(position).datetime_posted));
    return view;
  }
  public String convertDateFormat(String datetime_posted){
    try {
      //raw datetime_posted 2020-02-28T10:46:12.000Z
      DateFormat outputFormat = new SimpleDateFormat("MMM dd yyyy 'at' HH:mm:ss");
      outputFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
      DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      Date date = inputFormat.parse(datetime_posted);
      System.out.println(outputFormat.format(date));
      return outputFormat.format(date);
    }catch (Exception e){
      e.printStackTrace();
    }
    return datetime_posted;
  }

}
