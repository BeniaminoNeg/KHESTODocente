package it.univaq.khestodocente.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.univaq.khestodocente.Controller.Controller;
import it.univaq.khestodocente.Model.Chat;
import it.univaq.khestodocente.Model.Course;
import it.univaq.khestodocente.Model.File;
import it.univaq.khestodocente.Model.Message;
import it.univaq.khestodocente.Model.Section;
import it.univaq.khestodocente.Model.User;

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

    public static List<Message> parseMessages(String requestResult){

        try{
            if(requestResult != null) {

                if(isJsonObject(requestResult)) {

                    JSONObject jRoot = new JSONObject(requestResult);
                    JSONArray jMessages = jRoot.getJSONArray("result");
                    if (jMessages != null) {

                        ArrayList<Message> messages = new ArrayList<Message>();
                        for (int i = 0; i < jMessages.length(); ++i) {

                            JSONObject jMessage = jMessages.getJSONObject(i);

                            Message message = new Message();
                            message.setId(jMessage.optLong("id"));
                            message.setIdProfessor(-1);
                            message.setIdCourse(jMessage.optLong("chatid"));
                            message.setIdUser(jMessage.optLong("userid"));
                            message.setSurname("");
                            message.setName(jMessage.optString("username"));
                            message.setMessage(jMessage.optString("message"));
                            message.setTimestamp(jMessage.optLong("timestamp"));

                            messages.add(message);
                        }
                        return messages;
                    }
                }
                else {

                    JSONArray jMessages = new JSONArray(requestResult);
                    if (jMessages != null) {

                        ArrayList<Message> messages = new ArrayList<Message>();
                        for (int i = 0; i < jMessages.length(); ++i) {

                            JSONObject jMessage = jMessages.getJSONObject(i);

                            Message message = new Message();
                            message.setId(jMessage.optLong("id"));
                            message.setIdCourse(jMessage.optLong("id_course"));
                            message.setIdProfessor(jMessage.optLong("id_professor"));
                            message.setIdUser(jMessage.optLong("id_user"));
                            message.setSurname(jMessage.optString("surname"));
                            message.setName(jMessage.optString("name"));
                            message.setMessage(jMessage.optString("text"));
                            message.setTimestamp(jMessage.optLong("timestamp"));

                            messages.add(message);
                        }

                        return messages;
                    }
                }
            }
        }
        catch(JSONException e){ e.printStackTrace(); }

        return new ArrayList<Message>();
    }

    public static List<File> parseFilesCourse (String requestResult){
        ArrayList<File>files= new ArrayList<>();
        try {
            if (requestResult != null){
                if (isJsonObject(requestResult)){
                    JSONObject jRoot = new JSONObject(requestResult);
                    JSONArray jFiles = jRoot.getJSONArray("result");
                    if (jFiles != null){
                        for (int i= 0; i<jFiles.length(); i++){
                            JSONObject jfile = jFiles.getJSONObject(i);

                            File file = new File();

                            file.setId(jfile.optLong("id"));
                            file.setTime(jfile.optLong("timemodified"));
                            file.setSize(jfile.optLong("filesize"));
                            file.setAuthor(jfile.optString("author"));
                            file.setMimetype(jfile.optString("mimetype"));
                            file.setFilename(jfile.optString("filename"));
                            file.setUrl(jfile.optString("url"));
                            file.setUserid(jfile.optLong("userid"));
                            file.setSectionid(jfile.optLong("section"));

                            files.add(file);
                        }
                    }
                }
            }
        }
        catch(JSONException e){ e.printStackTrace(); }
        return files;
    }


    private static boolean isJsonObject(String result){

        try{
            JSONObject jsonObject = new JSONObject(result);
            return true;
        }
        catch (JSONException e){ return false; }
    }
}
