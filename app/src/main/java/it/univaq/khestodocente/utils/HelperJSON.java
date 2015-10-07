package it.univaq.khestodocente.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.univaq.khestodocente.controller.Controller;
import it.univaq.khestodocente.main.Model.Chat;
import it.univaq.khestodocente.main.Model.Course;
import it.univaq.khestodocente.main.Model.File;
import it.univaq.khestodocente.main.Model.Section;
import it.univaq.khestodocente.main.Model.User;

/**
 * Created by beniamino on 06/10/15.
 */
public class HelperJSON {
    /**
     * Parse http result to find error.
     *
     * @param requestResult: result of request
     * @return boolean true if error exists, false otherwise.
     */
    public static boolean parseError(String requestResult){

        try{
            JSONObject jRoot = new JSONObject(requestResult);
            return jRoot.optBoolean("error");
        }
        catch (JSONException e) { e.printStackTrace(); }

        return true;
    }
    /**
     * Parse http result to find user information.
     *
     *
     */
    public static User parseUserMoodle(JSONObject jUser){

        try{
            if(Controller.getInstance().getUser() == null) Controller.getInstance().setUser(new User());
            Controller.getInstance().getUser().setIdmoodle(jUser.optLong("id"));
            Controller.getInstance().getUser().setUsername(jUser.optString("username"));
            Controller.getInstance().getUser().setFirstname(jUser.optString("firstname"));
            Controller.getInstance().getUser().setLastname(jUser.optString("lastname"));
            Controller.getInstance().getUser().setUsername(jUser.optString("email"));
            Controller.getInstance().getUser().setAddress(jUser.optString("address"));
            Controller.getInstance().getUser().setCity(jUser.optString("city"));
            Controller.getInstance().getUser().setCountry(jUser.optString("country"));
            Controller.getInstance().getUser().setPicture("http://" + jUser.optString("picture_url"));

            ArrayList<Course> courses = new ArrayList<Course>();
            JSONArray jCourses = jUser.getJSONArray("courses");
            if(jCourses != null){
                for(int i = 0; i < jCourses.length(); i++){

                    JSONObject jCourse = jCourses.getJSONObject(i);
                    Course course = new Course();
                    course.setId(jCourse.optLong("id"));
                    course.setName(jCourse.optString("fullname"));
                    course.setDescription(jCourse.optString("summary"));

                    ArrayList<Section> sections = new ArrayList<Section>();
                    JSONArray jSections = jCourse.getJSONArray("sections");
                    System.out.println("Sono nell' helper jason, queste le sezioni del " + i + "esimmo  corso " + jSections.toString());
                    if(jSections != null){
                        for(int j = 0; j < jSections.length(); j++){

                            JSONObject jSection = jSections.getJSONObject(j);


                            Section section = new Section();
                            section.setId(jSection.optLong("id"));
                            section.setTitle(jSection.optString("name"));
                            section.setDescription(jSection.optString("summary"));
                            section.setNumber(jSection.optString("section"));
                            sections.add(section);

                        }

                    }

                    ArrayList<File> files = new ArrayList<File>();
                    JSONArray jFiles = jCourse.getJSONArray("files");
                    if(jFiles != null){
                        for(int j = 0; j < jFiles.length(); j++){

                            JSONObject jFile = jFiles.getJSONObject(j);


                            File file = new File();
                            file.setId(jFile.optLong("id"));
                            file.setTime(jFile.optLong("timemodified"));
                            file.setSize(jFile.optLong("filesize"));
                            file.setAuthor(jFile.optString("author"));
                            file.setMimetype(jFile.optString("mimetype"));
                            file.setFilename(jFile.optString("filename"));
                            file.setUrl(jFile.optString("url"));
                            file.setUserid(jFile.optLong("userid"));
                            file.setSectionid(jFile.optLong("section"));
                            files.add(file);

                        }
                    }

                    ArrayList<Chat> chats = new ArrayList<Chat>();
                    JSONArray jChats = jCourse.getJSONArray("chats");
                    if(jChats != null){
                        for(int j = 0; j < jChats.length(); j++){

                            JSONObject jChat = jChats.getJSONObject(j);


                            Chat chat = new Chat();
                            chat.setId(jChat.optLong("id"));
                            chat.setTitle(jChat.optString("name"));
                            chat.setDescription(jChat.optString("intro"));
                            chats.add(chat);

                        }
                    }

                    course.setSections(sections);
                    course.setFiles(files);
                    course.setChats(chats);
                    courses.add(course);
                }
            }
            Controller.getInstance().getUser().setCourses(courses);
            for (int i= 0; i<Controller.getInstance().getUser().getCourses().size(); i++)
            {
                System.out.println("sono nel helper, stampo i file dentro al singleton del corso " + i + "esimo " + Controller.getInstance().getUser().getCourses().get(i).getFiles().toString());

            }

            return Controller.getInstance().getUser();
        }
        catch (JSONException e) { e.printStackTrace(); }

        return null;
    }
}
