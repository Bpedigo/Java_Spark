package com.bpedigo;

import com.bpedigo.model.CourseDAO;
import com.bpedigo.model.CourseIdea;
import com.bpedigo.model.NotFoundException;
import com.bpedigo.model.SimpleCourseDAO;
import spark.ModelAndView;
import spark.Request;
import spark.Service;
import spark.Spark;
import spark.staticfiles.StaticFilesConfiguration;
import spark.staticfiles.StaticFilesFolder;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.Objects;

import static spark.Spark.*;

/**
 * Created by Shelby on 1/25/2017.
 */
public class Main {

    private static final String FLASH_MESSAGE_KEY = "flash_message";

    public static void main(String[] args) {
        Spark.staticFileLocation("/public");

        CourseDAO dao = new SimpleCourseDAO();

        before((request, response) -> {
            if( request.cookie("username") != null){
                request.attribute("username", request.cookie("username"));
            }
        });

        before("/ideas",((request, response) -> {
            if(request.attribute("username") == null){
                setFlashMessage(request,"Whoops please sign in!");
                response.redirect("/");
                halt(); 
            }
        }));

        get("/hello", (req, res) -> "Hello World");

        get("/",((request, response) -> {
            Map<String,String> model = new HashMap<>();
            model.put("username",request.attribute("username"));
            model.put("flashMessage",captureFlashMessage(request));
            return new ModelAndView(model, "index.hbs");

        }),new HandlebarsTemplateEngine());

        get("/debug",((request, response) -> {
            Map<String,Object> model = new HashMap<>();
            model.put("username",request.attribute("username"));
            model.put("ideas", dao.findAll());
            return new ModelAndView( model,"debug.hbs");
        } ), new HandlebarsTemplateEngine());

//        this works as well because it links back to the index page
//        post("/sign-in",((request, response) -> {
//            Map<String,String> model = new HashMap<>();
//            String username = request.queryParams("username");
//            response.cookie("username",username);
//            model.put("username", username);
//            return new ModelAndView(model, "index.hbs");
//        }),new HandlebarsTemplateEngine());

        post("/sign-in",((request, response) -> {
            String username = request.queryParams("username");
            response.cookie("username",username);
            response.redirect("/");
            return null;
        }));

        get("/ideas",(request, response) -> {
            Map<String,Object> model = new HashMap<>();
//            model.put("flashMessage", captureFlashMessage(request));
            model.put("ideas", dao.findAll());
            model.put("flashMessage",captureFlashMessage(request));
            return new ModelAndView(model,"ideas.hbs");
        },new HandlebarsTemplateEngine());

        get("/ideas/:slug",(request, response) -> {
           Map<String,Object> model = new HashMap<>();
           model.put("idea",dao.findBySlug(request.params("slug")));
           return new ModelAndView(model,"idea.hbs");
        },new HandlebarsTemplateEngine());

        post("/ideas",(request, response) -> {
            String title = request.queryParams("title");
            CourseIdea courseIdea = new CourseIdea(title,
                    request.attribute("username"));
            dao.add(courseIdea);
            response.redirect("/ideas");
            return null;
        });

        post("/ideas/:slug/vote",(request, response) -> {
            CourseIdea idea = dao.findBySlug(request.params("slug"));
            boolean added = idea.addVoter(request.attribute("username"));
            if (added) {
                setFlashMessage(request, "Thanks for your vote");
            } else {
                setFlashMessage(request,"you already voted!");
            }
            response.redirect("/ideas");
            return null;
        });

        exception(NotFoundException.class,(exc, req, res) ->{
            res.status(404);
            HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();
            String html = engine.render(
                    new ModelAndView(null, "not-found.hbs"));
            res.body(html);
        });



    }//main method

    private static void setFlashMessage(Request request, String message) {
        request.session().attribute(FLASH_MESSAGE_KEY, message);
    }

    private static String getFlashMessage(Request request){
        if(request.session(false) == null){
            return null;
        }
        if(!request.session().attributes().contains(FLASH_MESSAGE_KEY)){
            return null;
        }
        return (String) request.session().attribute(FLASH_MESSAGE_KEY);
    }

    private static String captureFlashMessage(Request request){
        String message = getFlashMessage(request);
        if(message != null){
            request.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;
    }

}//class main
