package com.yong.wesave.apiobject;

public class Comment {
  public String username, comment, datetime_posted;
  
  public Comment(String username, String comment, String datetime_posted) {
    this.username = username;
    this.comment = comment;
    this.datetime_posted = datetime_posted;
  }
  
}
