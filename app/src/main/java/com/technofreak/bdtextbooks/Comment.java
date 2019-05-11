package com.technofreak.bdtextbooks;

public class Comment {
    private String commentID;
    private String userName;
    private String text;
    String date;

    public Comment() {
    }

    public Comment(String commentID, String userName, String text, String date) {
        this.commentID = commentID;
        this.userName = userName;
        this.text = text;
        this.date = date;
    }

    public String getCommentID() {
        return commentID;
    }

    public String getUserName() {
        return userName;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getBookName(){
        int start = userName.indexOf('►');
        return userName.substring(start+2);
    }

}
