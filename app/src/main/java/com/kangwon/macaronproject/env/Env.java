package com.kangwon.macaronproject.env;


// 오류?

public class Env {

    public static final int MAIN = 1000;
    public static final int SIGNIN = 1001;
    public static final int SIGNUP = 1002;
    public static final int BOARD = 1003;
    public static String ISOWNER;
    public static boolean checker;

    public static final String[] DBTABLES = new String[]{"users"};


    public static void setOwner(String isowner){

        if(isowner != null) {
            if (isowner.equals("true")) {
                checker = true;
                ISOWNER = "true";
            } else {
                checker = false;
                ISOWNER = "false";
            }
        }
    }
    public static int split_time(String end_time, String start_time) {

        int time;

        start_time = start_time.replace("시", "-");
        end_time = end_time.replace("시", "-");

        start_time = start_time.replace("분", "-");
        end_time = end_time.replace("분", "-");

        String start[] = start_time.split("-");
        String end[] = end_time.split("-");

        int s0 = Integer.parseInt(start[0]);
        int e0 = Integer.parseInt(end[0]);

        int s1 = Integer.parseInt(start[1]);
        int e1 = Integer.parseInt(end[1]);

        time = (e0 * 60 + e1) - (s0 * 60 + s1);

        return time;
    }
}
