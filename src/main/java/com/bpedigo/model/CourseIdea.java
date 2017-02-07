package com.bpedigo.model;

import com.github.slugify.Slugify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Shelby on 1/27/2017.
 */
public class CourseIdea {

    private String slug;
    private String title;
    private String creator;
    private Set<String> voters;


    public CourseIdea(String title, String creator) {
        voters = new HashSet<>();
        this.title = title;
        this.creator = creator;
        Slugify slugify = null;
        try {
            slugify = new Slugify();
        } catch (IOException e) {
            e.printStackTrace();
        }
        slug = slugify.slugify(title);

    }

    public String getSlug() {
        return slug;
    }

    public List<String> getVoters (){
        return new ArrayList<>(voters);
    }

    public boolean addVoter(String voterUserName){
        return voters.add(voterUserName);
    }

    public int getVoteCount(){
        return voters.size();
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseIdea that = (CourseIdea) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return creator != null ? creator.equals(that.creator) : that.creator == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        return result;
    }
}
