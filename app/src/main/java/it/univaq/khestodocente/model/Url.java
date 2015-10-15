package it.univaq.khestodocente.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

/**
 * Created by beniamino on 23/09/15.
 */
public class Url {

    //private static final String ADDRESS = "http://192.168.1.3";//CASA
    //private static final String ADDRESS = "http://172.22.15.144";//Fabio
    //private static final String ADDRESS = "http://10.150.18.41";//HOTSPOT
    //private static final String ADDRESS = "http://192.168.43.46";//NEXUS HOTSPOT
    //private static final String ADDRESS = "http://10.175.50.235";//UNIVAQ

    private static final String ADDRESS = "http://khesto.univaq.it";//SERVER REMOTO

    //private static final String BASE_URL = ":8080/KHE-STO-ON-BOARD/api/moodle";//LOCALHOST
    private static final String BASE_URL = "/KHE-STO/api/moodle";//SERVER REMOTO

    private static final String PARAM_FILES = "files_course?courseid={0}";
    private static final String PARAM_COURSES = "student_courses?userid={0}";
    private static final String PARAM_SECTIONS = "sections_course?courseid={0}";
    private static final String PARAM_LOGIN = "?username={0}&password={1}&token=";
    private static final String PARAM_UPLOAD = "/uploadfile";
    private static final String PARAM_MOODLE_MESSAGES = "/chat_messages?chatid={0}&page={1}&notfrom=";
    private static final String PARAM_MOODLE_PUT_MESSAGE = "/put_chat_message?chatid={0}&userid={1}&message={2}&courseId={3}";


    public static final String URL_UPLOAD =  ADDRESS + BASE_URL + PARAM_UPLOAD;
    public static final String URL_LOGIN =  ADDRESS + BASE_URL + PARAM_LOGIN;
    public static final String URL_FILES =  ADDRESS + BASE_URL + PARAM_FILES;
    public static final String URL_COURSES =  ADDRESS + BASE_URL + PARAM_COURSES;
    public static final String URL_SECTIONS = ADDRESS + BASE_URL + PARAM_SECTIONS;
    public static final String URL_MOODLE_MESSAGES  = ADDRESS + BASE_URL + PARAM_MOODLE_MESSAGES;
    public static final String URL_MOODLE_PUT_MESSAGE = ADDRESS + BASE_URL + PARAM_MOODLE_PUT_MESSAGE;


    public static URL getLoginURL (String ... params) {
        try {
            URL url = new URL(MessageFormat.format(URL_LOGIN, params[0],params[1]));
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        return null;
    }

    public static URL getStudentcoursesURL (Long ... params) {
        try {
            URL url = new URL(MessageFormat.format(URL_COURSES, params[0]));
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        return null;
    }

    public static URL getFilescourseURL (String ... params) {
        try {
            URL url = new URL(MessageFormat.format(URL_FILES, params[0]));
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        return null;
    }

    public static URL getCourseSectionURL (String ... params) {
        try {
            URL url = new URL(MessageFormat.format(URL_SECTIONS, params[0]));
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        return null;
    }

    public static URL getUploadfileURL (){
        try {
            URL url = new URL(URL_UPLOAD);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL getMoodleMessageURL (long idchat, long page) {
        try {
            URL url = new URL(MessageFormat.format(URL_MOODLE_MESSAGES, idchat, page, 0 ));
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL getPutMessageURL (long chatid, long userid, String message, long courseid){
        try {
            URL url = new URL(MessageFormat.format(URL_MOODLE_PUT_MESSAGE,chatid,userid,message,courseid));
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
