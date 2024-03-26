        package com.example.cabinetchef.Recipe;

        import java.util.List;

        public class Recipe {
            private String title;
            private static List<RecipeDetail.Ingredient> ingredients;
            private int readyInMinutes;
            private String image;
            private List<String> instructions;

            // Constructors
            public Recipe() {
            }

            public Recipe(String title, List<RecipeDetail.Ingredient> ingredients, int readyInMinutes, String image, List<String> instructions) {
                this.title = title;
                Recipe.ingredients = ingredients;
                this.readyInMinutes = readyInMinutes;
                this.image = image;
                this.instructions = instructions;
            }

            // Getters and Setters
            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {this.title = title;}

            public List<RecipeDetail.Ingredient> getIngredients() {
                return ingredients;
            }
            public int getReadyInMinutes() {
                return readyInMinutes;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image){this.image = image;}
            public List<String> getInstructions() {
                return instructions;
            }

            public static boolean containsAllergens(List<String> userAllergens) {
                for (RecipeDetail.Ingredient ingredient : ingredients) {
                    if (userAllergens.contains(ingredient.getName().toLowerCase())) {
                        return true;
                    }
                }
                return false;
            }



            @Override
            public String toString() {
                return "Recipe{" +
                        "title='" + title + '\'' +
                        ", ingredients=" + ingredients.toString() +
                        ", readyInMinutes=" + readyInMinutes +
                        ", image='" + image + '\'' +
                        ", instructions=" + instructions +
                        '}';
            }
        }
