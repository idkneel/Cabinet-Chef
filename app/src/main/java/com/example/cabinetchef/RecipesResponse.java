package com.example.cabinetchef;

import java.util.List;

public class RecipesResponse {
    private List<Recipe> results;

    public RecipesResponse() {}

    public List<Recipe> getResults() {
        return results;
    }

    public void setResults(List<Recipe> results) {
        this.results = results;
    }
}
