package it.univaq.khestodocente.main.Model;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

/**
 * Created by beniamino on 23/09/15.
 */
public class Url {

    private static final String ADDRESS = "http://192.168.1.8";//CASA
    //private static final String ADDRESS = "http://172.22.15.144";//Fabio
    //private static final String ADDRESS = "http://10.150.18.41";//HOTSPOT
    //private static final String ADDRESS = "http://192.168.43.46";//NEXUS HOTSPOT
    //private static final String ADDRESS = "http://10.175.50.235";//UNIVAQ
    private static final String BASE_URL = ":8080/KHE-STO/api/moodle";

    private static final String PARAM_FILES = "files_course?courseid={0}";
    private static final String PARAM_COURSES = "student_courses?userid={0}";
    private static final String PARAM_SECTIONS = "sections_course?courseid={0}";
    private static final String PARAM_LOGIN = "?username={0}&password={1}&token=";
    private static final String PARAM_UPLOAD = "uploadfile";

    public static final String URL_UPLOAD =  ADDRESS + BASE_URL + PARAM_UPLOAD;
    public static final String URL_LOGIN =  ADDRESS + BASE_URL + PARAM_LOGIN;
    public static final String URL_FILES =  ADDRESS + BASE_URL + PARAM_FILES;
    public static final String URL_COURSES =  ADDRESS + BASE_URL + PARAM_COURSES;
    public static final String URL_SECTIONS = ADDRESS + BASE_URL + PARAM_SECTIONS;


    public URL getLoginURL (String ... params) {
        try {
            URL url = new URL(MessageFormat.format(URL_LOGIN, params[0],params[1]));
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        return null;
    }

    public URL getStudentcoursesURL (Long ... params) {
        try {
            URL url = new URL(MessageFormat.format(URL_COURSES, params[0]));
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        return null;
    }

    public URL getFilescourseURL (String ... params) {
        try {
            URL url = new URL(MessageFormat.format(URL_FILES, params[0]));
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        return null;
    }

    public URL getCourseSectionURL (String ... params) {
        try {
            URL url = new URL(MessageFormat.format(URL_SECTIONS, params[0]));
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        return null;
    }

    public URL getUploadfileURL (){
        try {
            URL url = new URL(URL_UPLOAD);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
