package com.example.cabinetchef.Recipe;

import java.util.List;

public class RecipeDetail {
    private static int id;
    private String title;
    private int readyInMinutes;
    private String image;
    private List<Ingredient> extendedIngredients;
    private List<String> simpleIngredients;
    private String instructions;

    public RecipeDetail() {}

    public RecipeDetail(int id, String title, int readyInMinutes, String image, List<Ingredient> extendedIngredients, String instructions) {
        this.id = id;
        this.title = title;
        this.readyInMinutes = readyInMinutes;
        this.image = image;
        this.extendedIngredients = extendedIngredients;
        this.instructions = instructions;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public int getReadyInMinutes() { return readyInMinutes; }

    public void setReadyInMinutes(int readyInMinutes) { this.readyInMinutes = readyInMinutes; }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public List<Ingredient> getExtendedIngredients() { return extendedIngredients; }

    public void setExtendedIngredients(List<Ingredient> extendedIngredients) { this.extendedIngredients = extendedIngredients; }
    public List<String> getSimpleIngredients() {
        return simpleIngredients;
    }

    public void setSimpleIngredients(List<String> simpleIngredients) {
        this.simpleIngredients = simpleIngredients;
    }

    public String getInstructions() { return instructions; }

    public void setInstructions(String instructions) { this.instructions = instructions; }
    public static class Ingredient {
        private String name;
        private String amount;
        private String unit;
        public Ingredient() {}

        public Ingredient(String name, String amount, String unit) {
            this.amount = amount;
            this.name = name;
            this.unit = unit;
        }

        public String getAmount() { return amount; }

        public void setAmount(String amount) { this.amount = amount; }

        public String getName() { return name; }

        public void setName(String name) { this.name = name; }

        public String getUnit() { return unit; }

        public void setUnit(String unit) { this.unit = unit; }
    }
}
