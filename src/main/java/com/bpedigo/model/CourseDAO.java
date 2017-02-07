package com.bpedigo.model;

import java.util.List;

/**
 * Created by Shelby on 1/27/2017.
 */
public interface CourseDAO {

    boolean add(CourseIdea idea);
    List<CourseIdea> findAll();
    CourseIdea findBySlug(String slug);
}
