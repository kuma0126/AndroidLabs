package com.example.androidlabs;


public class Message {


    String message;
    private boolean checker;
    private long id;



    public Message(String message,long id, boolean checker){

        this.message=message;
        this.checker=checker;
        this.id=id;

    }

    public Message()

    {
        this("unknown",0,false);
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isChecker() {
        return checker;
    }

    public void setChecker(boolean checker) {
        this.checker = checker;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}