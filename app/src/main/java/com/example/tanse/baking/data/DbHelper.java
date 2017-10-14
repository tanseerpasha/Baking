package com.example.tanse.baking.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tanse on 6/3/2017.
 */

public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "recipes.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // main recipe table
        final String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + Contract.RecipeNameEntry.TABLE_NAME + " (" +
                Contract.RecipeNameEntry._ID + " INTEGER PRIMARY KEY," +
                Contract.RecipeNameEntry.COLUMN_RECIPE_ID + " TEXT UNIQUE NOT NULL, " +
                Contract.RecipeNameEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                Contract.RecipeNameEntry.COLUMN_RECIPE_INGREDIENTS + " TEXT NOT NULL, " +
                Contract.RecipeNameEntry.COLUMN_RECIPE_STEPS + " TEXT NOT NULL, " +
                Contract.RecipeNameEntry.COLUMN_RECIPE_SERVINGS + " TEXT NOT NULL" +
                " )";

        // recipe steps details
        final String SQL_CREATE_RECIPEDETAILS_TABLE = "CREATE TABLE " + Contract.RecipeDetailsEntry.TABLE_NAME + " (" +
                Contract.RecipeDetailsEntry._ID + " INTEGER PRIMARY KEY," +
                Contract.RecipeDetailsEntry.COLUMN_STEP_ID + " TEXT UNIQUE NOT NULL, " +
                Contract.RecipeDetailsEntry.COLUMN_STEP_SHORTDESCRIPTION + " TEXT NOT NULL, " +
                Contract.RecipeDetailsEntry.COLUMN_STEP_DESCRIPTION + " TEXT NOT NULL, " +
                Contract.RecipeDetailsEntry.COLUMN_STEP_VIDEOURL + " TEXT NOT NULL, " +
                Contract.RecipeDetailsEntry.COLUMN_STEP_THUMBNAILURL + " TEXT NOT NULL" +
                " )";

        // recipe ingredient details
        final String SQL_CREATE_RECIPEINGREDIENTS_TABLE = "CREATE TABLE " + Contract.RecipeIngredientsEntry.TABLE_NAME + " (" +
                Contract.RecipeIngredientsEntry._ID + " INTEGER PRIMARY KEY," +
                Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_RECIPE_ID + " TEXT NOT NULL, " +
                Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_NAME + " TEXT NOT NULL, " +
                Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_QUANTITY + " TEXT NOT NULL, " +
                Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_MEASURE + " TEXT NOT NULL" +
                " )";
        db.execSQL(SQL_CREATE_RECIPE_TABLE);
        db.execSQL(SQL_CREATE_RECIPEDETAILS_TABLE);
        db.execSQL(SQL_CREATE_RECIPEINGREDIENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.RecipeNameEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.RecipeDetailsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.RecipeIngredientsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
