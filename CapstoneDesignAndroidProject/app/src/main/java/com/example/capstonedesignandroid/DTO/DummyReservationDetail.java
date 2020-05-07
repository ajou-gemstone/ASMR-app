package com.example.capstonedesignandroid.DTO;

public class DummyReservationDetail {
//    {date: "YYYY-MM-DD", day(요일): "월", startTime: "8:00", lastTime:"10:00", lectureRoom:"성101",
//              userid: ["user1", "user2", ...], beforeUri: "beforeuri", afterUri: "afteruri}
    String date;
    String day;
    String startTime;
    String lastTime;
    String lectureRoom;
    String[] userid;
    String beforeuri;
    String afteruri;
    String reservationIntent;
    String beforeUploadTime;
    String afterUploadTime;

    public DummyReservationDetail(String date, String day, String startTime, String lastTime, String lectureRoom, String[] userid, String beforeuri,
                                  String afteruri, String reservationIntent, String beforeUploadTime, String afterUploadTime) {
        this.date = date;
        this.day = day;
        this.startTime = startTime;
        this.lastTime = lastTime;
        this.lectureRoom = lectureRoom;
        this.userid = userid;
        this.beforeuri = beforeuri;
        this.afteruri = afteruri;
        this.reservationIntent = reservationIntent;
        this.beforeUploadTime = beforeUploadTime;
        this.afterUploadTime = afterUploadTime;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public String getLectureRoom() {
        return lectureRoom;
    }

    public String[] getUserid() {
        return userid;
    }

    public String getBeforeuri() {
        return beforeuri;
    }

    public String getAfteruri() {
        return afteruri;
    }

    public String getReservationIntent() {
        return reservationIntent;
    }

    public String getBeforeUploadTime() {
        return beforeUploadTime;
    }

    public String getAfterUploadTime() {
        return afterUploadTime;
    }
}