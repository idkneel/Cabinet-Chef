package com.example.cabinetchef.Recipe;

import java.util.List;

public class RecipesResponse {
    private List<RecipeSummary> results;

    public List<RecipeSummary> getResults() {
        return results;
    }

    public void setResults(List<RecipeSummary> results) {
        this.results = results;
    }
}
