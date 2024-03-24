        package com.example.cabinetchef.Recipe;

        import java.util.List;

        public class Recipe {
            public String title;
            List<RecipeDetail.Ingredient> ingredients;
            int readyInMinutes;
            public String image;
            List<String> instructions;

            // Constructors
            public Recipe(){}

            public Recipe(String title, List<RecipeDetail.Ingredient> ingredients, int readyInMinutes, String image, List<String> instructions) {
                this.title = title;
                this.ingredients = ingredients;
                this.readyInMinutes = readyInMinutes;
                this.image = image;
                this.instructions = instructions;
            }

            // Getters and Setters
            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public List<RecipeDetail.Ingredient> getIngredients() {
                return ingredients;
            }

            public void setIngredients(List<RecipeDetail.Ingredient> ingredients) {
                this.ingredients = ingredients;
            }

            public int getReadyInMinutes() {
                return readyInMinutes;
            }

            public void setReadyInMinutes(int readyInMinutes) {
                this.readyInMinutes = readyInMinutes;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public List<String> getInstructions() {
                return instructions;
            }

            public void setInstructions(List<String> instructions) {
                this.instructions = instructions;
            }

            @Override
            public String toString() {
                return "Recipe{" +
                        "title='" + (title != null ? title : "N/A") + '\'' +
                        ", ingredients=" + (ingredients != null ? ingredients.toString() : "[]") +
                        ", readyInMinutes=" + readyInMinutes +
                        ", image='" + (image != null ? image : "N/A") + '\'' +
                        ", instructions=" + (instructions != null ? instructions : "N/A") +
                        '}';
            }
        }
