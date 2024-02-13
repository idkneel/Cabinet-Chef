package com.example.cabinetchef;

import java.util.List;

public class RecipeDetail {
    private static int id;
    private String title;
    private int readyInMinutes;
    private String image;
    private List<Ingredient> extendedIngredients;
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

    public String getInstructions() { return instructions; }

    public void setInstructions(String instructions) { this.instructions = instructions; }
    public static class Ingredient {
        private String name;
        private double amount;
        private String unit;
        public Ingredient() {}

        public Ingredient(String name, double amount, String unit) {
            this.name = name;
            this.amount = amount;
            this.unit = unit;
        }
        public String getName() { return name; }

        public void setName(String name) { this.name = name; }

        public double getAmount() { return amount; }

        public void setAmount(double amount) { this.amount = amount; }

        public String getUnit() { return unit; }

        public void setUnit(String unit) { this.unit = unit; }
    }
}
