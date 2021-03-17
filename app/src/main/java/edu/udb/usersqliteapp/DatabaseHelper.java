package edu.udb.usersqliteapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;


/* Estructura Base Datos

Table User:
- ID   1
- NAME Alex

Table City:
- ID    1
- City  San Salvador

Table Hobby:
- ID     1
- Hobby  Dormir
 */


public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "user_database";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USER = "users";
    private static final String TABLE_USER_HOBBY = "users_hobby";
    private static final String TABLE_USER_CITY = "users_city";

    private static final String KEY_ID = "id";
    private static final String KEY_FIRSTNAME = "name";
    private static final String KEY_HOBBY = "hobby";
    private static final String KEY_CITY = "city";


    private static final String CREATE_TABLE_STUDENTS = "CREATE TABLE "
            + TABLE_USER + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_FIRSTNAME + " TEXT );";

    private static final String CREATE_TABLE_USER_HOBBY = "CREATE TABLE "
            + TABLE_USER_HOBBY + "(" + KEY_ID + " INTEGER,"+ KEY_HOBBY + " TEXT );";

    private static final String CREATE_TABLE_USER_CITY = "CREATE TABLE "
            + TABLE_USER_CITY + "(" + KEY_ID + " INTEGER,"+ KEY_CITY + " TEXT );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("table", CREATE_TABLE_STUDENTS);
    }


    /*
    En el método onCreate () , se escribe la declaración de creación para la tabla.
    */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STUDENTS);
        db.execSQL(CREATE_TABLE_USER_HOBBY);
        db.execSQL(CREATE_TABLE_USER_CITY);
    }

    /*
    En el método onUpgrade () , las tablas que ya existen se eliminan y luego se vuelven  a crear todas las tablas.

    En los siguientes escenarios, debe actualizar el número DATABASE_VERSION en orden de incremento.

    Cuando agrega, actualiza o elimina cualquier columna de cualquier tabla en toda la base de datos.
    Cuando actualiza cualquier nombre de columna de cualquier tabla.
    Cuando agrega, actualiza o elimina cualquier tabla.
    Cuando actualiza el nombre de la tabla.
    Cuando  DATABASE_VERSION se actualiza  en orden de incremento, se llama al método onUpgrade () .
    */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_USER + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_USER_HOBBY + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_USER_CITY + "'");
        onCreate(db);
    }

    //El método addUser () agregará el usuario a la base de datos SQLite.
    public void addUser(String name, String hobby, String city) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //tabla Usuario
        values.put(KEY_FIRSTNAME, name);
        long id = db.insertWithOnConflict(TABLE_USER, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        /*
        SQLiteDatabase.CONFLICT_IGNORE funciona. Al intentar insertar, si no hay una fila en conflicto,
        insertará una nueva fila con los valores dados y devolverá la identificación de la fila recién insertada.
        Por otro lado, si ya hay una fila en conflicto (con la misma clave única), los valores entrantes
        se ignorarán y la fila existente se mantendrá y el valor de retorno será -1 para indicar un escenario de conflicto
         */

        //tabla pasatiempo
        ContentValues valuesHobby = new ContentValues();
        valuesHobby.put(KEY_ID, id);
        valuesHobby.put(KEY_HOBBY, hobby);
        db.insertOrThrow (TABLE_USER_HOBBY, null, valuesHobby);

        //tabla ciudad
        ContentValues valuesCity = new ContentValues();
        valuesCity.put(KEY_ID, id);
        valuesCity.put(KEY_CITY, city);
        db.insertOrThrow(TABLE_USER_CITY, null, valuesCity);
    }

    //El método getAllUsers () obtendrá todos los usuarios de la base de datos SQLite.
    public ArrayList<UserModel> getAllUsers() {
        ArrayList<UserModel> userModelArrayList = new ArrayList<UserModel>();

        String selectQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                UserModel userModel = new UserModel();
                userModel.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                userModel.setName(c.getString(c.getColumnIndex(KEY_FIRSTNAME)));

                            //getting user hobby where id = id from user_hobby table
                            String selectHobbyQuery = "SELECT  * FROM " + TABLE_USER_HOBBY +" WHERE "+KEY_ID+" = "+ userModel.getId();
                            Log.d("oppp",selectHobbyQuery);

                            Cursor cHobby = db.rawQuery(selectHobbyQuery, null);
                                        if (cHobby.moveToFirst()) {
                                            do {
                                                userModel.setHobby(cHobby.getString(cHobby.getColumnIndex(KEY_HOBBY)));
                                            } while (cHobby.moveToNext());
                                        }

                            String selectCityQuery = "SELECT  * FROM " + TABLE_USER_CITY+" WHERE "+KEY_ID+" = "+ userModel.getId();;
                            //SQLiteDatabase dbCity = this.getReadableDatabase();
                            Cursor cCity = db.rawQuery(selectCityQuery, null);

                            if (cCity.moveToFirst()) {
                                do {
                                    userModel.setCity(cCity.getString(cCity.getColumnIndex(KEY_CITY)));
                                } while (cCity.moveToNext());
                            }

                    userModelArrayList.add(userModel);
                } while (c.moveToNext());
         }
        return userModelArrayList;
    }

    //El método updateUser () actualizará la información del usuario.
    public void updateUser(int id, String name, String hobby, String city) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FIRSTNAME, name);
        db.update(TABLE_USER, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        ContentValues valuesHobby = new ContentValues();
        valuesHobby.put(KEY_HOBBY, hobby);
        db.update(TABLE_USER_HOBBY, valuesHobby, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        ContentValues valuesCity = new ContentValues();
        valuesCity.put(KEY_CITY, city);
        db.update(TABLE_USER_CITY, valuesCity, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    //El método deleteUser () eliminará al usuario de SQLite.
    public void deleteUSer(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_USER, KEY_ID + " = ?",new String[]{String.valueOf(id)});

        db.delete(TABLE_USER_HOBBY, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        db.delete(TABLE_USER_CITY, KEY_ID + " = ?",new String[]{String.valueOf(id)});
    }

}

