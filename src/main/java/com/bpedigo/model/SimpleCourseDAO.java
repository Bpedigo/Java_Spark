package com.bpedigo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shelby on 1/27/2017.
 */
public class SimpleCourseDAO implements CourseDAO {
    private List<CourseIdea> ideas;

    public SimpleCourseDAO() {
        ideas = new ArrayList<>();
    }

    @Override
    public boolean add(CourseIdea idea) {
        return ideas.add(idea);
    }

    @Override
    public List<CourseIdea> findAll() {
        return new ArrayList<>(ideas);
    }

    @Override
    public CourseIdea findBySlug(String slug) {
        return ideas.stream()
                .filter(idea -> idea.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}
