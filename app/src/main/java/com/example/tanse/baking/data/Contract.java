package com.example.tanse.baking.data;

/**
 * Created by tanse on 6/3/2017.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the recipe database.
 * there are 3 tables
 * recipe table holds the recipe names, ingrdients, steps etc
 * second is the ingredient table that holds the ingredients of the all the recipes
 * third is the steps table that holds the steps of the recipe selected by user,
 * this keeps overwriting when the user selects different recipe
 */
public class Contract {

    public static final String CONTENT_AUTHORITY = "com.example.tanse.baking";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RECIPE = "recipe";
    public static final String PATH_RECIPE_STEPS = "steps";
    public static final String PATH_RECIPE_INGREDIENTS = "ingredients";

    public static final class RecipeNameEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE;
        public static final String TABLE_NAME = "recipe";

        public static final String COLUMN_RECIPE_ID = "id";;
        public static final String COLUMN_RECIPE_NAME="name";
        public static final String COLUMN_RECIPE_INGREDIENTS = "ingredients";
        public static final String COLUMN_RECIPE_STEPS = "steps";
        public static final String COLUMN_RECIPE_SERVINGS = "servings";

        public static final String[] RECIPE_NAME_COLUMNS = {
                _ID,
                COLUMN_RECIPE_ID,
                COLUMN_RECIPE_NAME,
                //COLUMN_RECIPE_STEPS,
        };


        public static Uri buildRecipeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildRecipeIDUri(String recipeID) {
            return CONTENT_URI.buildUpon().appendPath(recipeID).build();
        }

        public static String getRecipeIDFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static final class RecipeDetailsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE_STEPS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE_STEPS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE_STEPS;
        public static final String TABLE_NAME = "steps";

        public static final String COLUMN_STEP_ID = "id";;
        public static final String COLUMN_STEP_SHORTDESCRIPTION="shortdescription";
        public static final String COLUMN_STEP_DESCRIPTION = "description";
        public static final String COLUMN_STEP_VIDEOURL = "videoURL";
        public static final String COLUMN_STEP_THUMBNAILURL = "thumbnailURL";

        public static Uri buildStepsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildStepsIDUri(String recipeID) {
            return CONTENT_URI.buildUpon().appendPath(recipeID).build();
        }

        public static String getRecipeStepIDFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }


    }

    public static final class RecipeIngredientsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE_INGREDIENTS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE_INGREDIENTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE_INGREDIENTS;
        public static final String TABLE_NAME = "ingredients";

        public static final String COLUMN_INGREDIENT_RECIPE_ID = "id";
        public static final String COLUMN_INGREDIENT_NAME = "ingredient";
        public static final String COLUMN_INGREDIENT_QUANTITY="quantity";
        public static final String COLUMN_INGREDIENT_MEASURE = "measure";

        public static final String[] RECIPE_INGREDIENTS_COLUMNS = {
                _ID,
                COLUMN_INGREDIENT_RECIPE_ID,
                COLUMN_INGREDIENT_NAME,
                COLUMN_INGREDIENT_QUANTITY,
                COLUMN_INGREDIENT_MEASURE,
        };

        public static Uri buildIngredientsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildIngredientsUri(String recipeID) {
            return CONTENT_URI.buildUpon().appendPath(recipeID).build();
        }

        public static String getRecipeIngredientsIDFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
}
