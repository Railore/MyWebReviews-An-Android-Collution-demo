package com.mywebreviews;

public class WebReview {
    private int DBId;
    private int score;
    private String notes;
    private String domain;

    public WebReview(){
        DBId = -1;
        score = 0;
        notes = "";
        domain = "";

    }
    public WebReview(String url){
        this();
        setExtractedDomain(url);
    }
    public WebReview(String url, int score, String notes){
        this(url);
        setScore(score);
        setNotes(notes);
    }
    public WebReview(int DBId, String url, int score, String notes) {
        this(url, score, notes);
        setDBId(DBId);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getDBId() {
        return DBId;
    }

    public void setDBId(int DBId) {
        this.DBId = DBId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setExtractedDomain(String url){
        setDomain(extractDomain(url));
    }

    public static String extractDomain(String url){
        url = url.trim();
        url = url.replaceFirst("(.*)://", "");
        return url.replaceFirst("/(.*)","");
    }
}
