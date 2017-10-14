package com.example.tanse.baking.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by tanse on 6/3/2017.
 */

public class Provider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;

    static final int RECIPE = 100;
    static final int RECIPE_WITH_ID = 101;
    static final int RECIPE_DETAILS = 102;
    static final int RECIPE_DETAILS_WITH_ID = 103;
    static final int RECIPE_INGREDIENTS = 104;
    static final int RECIPE_INGREDIENTS_WITH_ID = 105;

    // select individual recipe
    private static final String sRecipeSelection = Contract.RecipeNameEntry.TABLE_NAME
            + "."
            + Contract.RecipeNameEntry.COLUMN_RECIPE_ID
            + " = ? ";

    // select individual recipe details
    private static final String sRecipeStepSelection = Contract.RecipeDetailsEntry.TABLE_NAME
            + "."
            + Contract.RecipeDetailsEntry.COLUMN_STEP_ID
            + " = ? ";

    // select individual recipe ingredients
    private static final String sRecipeIngredientSelection = Contract.RecipeIngredientsEntry.TABLE_NAME
            + "."
            + Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_RECIPE_ID
            + " = ? ";

    /*
        This UriMatcher will match each URI to the Recipe integer constants defined above.
     */
    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        matcher.addURI(authority, Contract.PATH_RECIPE,RECIPE);
        matcher.addURI(authority, Contract.PATH_RECIPE +"/*",RECIPE_WITH_ID);
        matcher.addURI(authority, Contract.PATH_RECIPE_STEPS, RECIPE_DETAILS);
        matcher.addURI(authority, Contract.PATH_RECIPE_STEPS + "/*",RECIPE_DETAILS_WITH_ID);
        matcher.addURI(authority, Contract.PATH_RECIPE_INGREDIENTS, RECIPE_INGREDIENTS);
        matcher.addURI(authority, Contract.PATH_RECIPE_INGREDIENTS + "/*", RECIPE_INGREDIENTS_WITH_ID);
        return matcher;
    }

    /*
        create a new MoviesDbHelper for later use here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case RECIPE:
                return Contract.RecipeNameEntry.CONTENT_TYPE;
            case RECIPE_WITH_ID:
                return Contract.RecipeNameEntry.CONTENT_ITEM_TYPE;
            case RECIPE_DETAILS:
                return Contract.RecipeDetailsEntry.CONTENT_TYPE;
            case RECIPE_DETAILS_WITH_ID:
                return Contract.RecipeDetailsEntry.CONTENT_ITEM_TYPE;
            case RECIPE_INGREDIENTS:
                return Contract.RecipeIngredientsEntry.CONTENT_TYPE;
            case RECIPE_INGREDIENTS_WITH_ID:
                return Contract.RecipeIngredientsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        //final int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case RECIPE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Contract.RecipeNameEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case RECIPE_WITH_ID: {
                retCursor = getRecipeByID(uri, projection, sortOrder);
                break;
            }
            case RECIPE_DETAILS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Contract.RecipeDetailsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case RECIPE_DETAILS_WITH_ID: {
                retCursor = getRecipeStepsByID(uri, projection, sortOrder);
                break;
            }
            case RECIPE_INGREDIENTS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Contract.RecipeIngredientsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case RECIPE_INGREDIENTS_WITH_ID: {
                retCursor = getRecipeIngredientsByID(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getRecipeByID(Uri uri, String[] projection, String sortOrder) {
        String recipeID = Contract.RecipeNameEntry.getRecipeIDFromUri(uri);
        String[] selectionArgs;
        String selection;
        selection = sRecipeSelection;
        selectionArgs = new String[]{recipeID};

        return mOpenHelper.getReadableDatabase().query(
                Contract.RecipeNameEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getRecipeStepsByID(Uri uri, String[] projection, String sortOrder) {
        String recipeStepID = Contract.RecipeDetailsEntry.getRecipeStepIDFromUri(uri);
        String[] selectionArgs;
        String selection;
        selection = sRecipeStepSelection;
        selectionArgs = new String[]{recipeStepID};

        return mOpenHelper.getReadableDatabase().query(
                Contract.RecipeDetailsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getRecipeIngredientsByID(Uri uri, String[] projection, String sortOrder) {
        String recipeIngredientsID = Contract.RecipeIngredientsEntry.getRecipeIngredientsIDFromUri(uri);
        String[] selectionArgs;
        String selection;
        selection = sRecipeIngredientSelection;
        selectionArgs = new String[]{recipeIngredientsID};

        return mOpenHelper.getReadableDatabase().query(
                Contract.RecipeIngredientsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case RECIPE: {
                long _id = db.insert(Contract.RecipeNameEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = Contract.RecipeNameEntry.buildRecipeUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case RECIPE:
                rowsDeleted = db.delete(
                        Contract.RecipeNameEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RECIPE_DETAILS:
                rowsDeleted = db.delete(
                        Contract.RecipeDetailsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RECIPE_INGREDIENTS:
                rowsDeleted = db.delete(
                        Contract.RecipeIngredientsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case RECIPE:
                rowsUpdated = db.update(Contract.RecipeNameEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPE:
                db.beginTransaction();
                int returnRecipeCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(Contract.RecipeNameEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnRecipeCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnRecipeCount;
            case RECIPE_DETAILS:
                db.beginTransaction();
                int returnStepsCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(Contract.RecipeDetailsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnStepsCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnStepsCount;
            case RECIPE_INGREDIENTS:
                db.beginTransaction();
                int returnIngredientsCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(Contract.RecipeIngredientsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnIngredientsCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnIngredientsCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
