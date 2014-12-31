package com.tigo.cs.android.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.helper.domain.BaseEntity;
import com.tigo.cs.android.util.CSTigoLogTags;
import com.tigo.cs.android.util.Cypher;
import com.tigo.cs.android.util.Notifier;

public abstract class AbstractHelper<T extends BaseEntity> extends SQLiteOpenHelper {

    /**
     * nombre de la base de datos a ser creada para la aplicacion
     */
    protected static String DATABASE_NAME = "cstigo.db";

    protected static String SD = "234orh830f0923hjf9289";

    /**
     * en el caso que la aplicacion cambie la version de sus tablas y objetos de
     * bases de datos, incrementar la version de la base de datos y el helper se
     * encargara de volver a recrear los objetos, invocando al metodo onUpgrade
     */
    private static final int DATABASE_VERSION = 22;

    protected Context context;
    protected String tableName;

    public AbstractHelper(Context context, String tableName) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.tableName = tableName;
    }

    /**
     * creamos la base de datos solo en el caso que no exista la misma, con el
     * script de la version
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        executeSQLScript(db, "sql/cstigo_v" + DATABASE_VERSION + ".sql");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
         * verificamos que la oldVersion sea siempre menor a newVersion si no es
         * este el caso lanzar una excepcion y solicitar la reinstalacion de la
         * aplicacion desde cero
         */
        if (oldVersion > newVersion) {
            // TODO: lanzar excepcion
        } else {
            /*
             * por cada version de base de datos existe un script que actualiza
             * la misma a su version inmediata superior, ejecutar cada uno de
             * los scripts desde la version inicial (oldVersion) hasta la
             * version final (newVersion), esto para no crear archivos cada vez
             * mas grandes, sino simplemente ir actualizando la base a versiones
             * inmediatamente superiores hasta llegar a la version final
             */

            for (int versionToUpgrade = oldVersion + 1; versionToUpgrade <= newVersion; versionToUpgrade++) {
                executeSQLScript(db, "sql/cstigo_v" + (versionToUpgrade - 1)
                    + "to" + versionToUpgrade + ".sql");
            }
        }
    }

    public T find(Long id) {
        return executeQuery("_id = ? ", new String[] { id.toString() });
    }

    public List<T> findLast(String where, String[] args, String limit) {

        boolean foundLocation = false;
        SQLiteDatabase database = getReadableDatabase();
        Cursor result = database.query(tableName, null, where, args, null,
                null, "_id DESC", limit);

        List<T> entityList = new ArrayList<T>();
        while (result.moveToNext()) {
            foundLocation = true;
            T location = cursorToEntity(result);
            entityList.add(location);
        }
        result.close();

        if (!foundLocation) {
            return null;
        }

        return entityList;
    }

    public List<T> findLast(String limit) {
        return findLast(null, null, limit);
    }

    public T findLast() {
        return findLast(null, null);
    }

    public T findLast(String where, String[] args) {

        boolean found = false;
        SQLiteDatabase database = getReadableDatabase();
        Cursor result = database.query(tableName, null, where, args, null,
                null, "_id DESC", "1");

        T entity = null;
        while (result.moveToNext()) {
            found = true;
            entity = cursorToEntity(result);
        }
        result.close();

        if (!found) {
            return null;
        }
        return entity;
    }

    public List<T> findAll(String where, String[] whereArgs, boolean desc) {
        boolean foundLocation = false;
        SQLiteDatabase database = getReadableDatabase();
        String orderBy = "_id";
        if (desc) {
            orderBy = orderBy.concat(" DESC");
        }
        Cursor result = database.query(tableName, null, where, whereArgs, null,
                null, orderBy, null);

        List<T> entityList = new ArrayList<T>();
        while (result.moveToNext()) {
            foundLocation = true;
            T location = cursorToEntity(result);
            entityList.add(location);
        }
        result.close();

        if (!foundLocation) {
            return new ArrayList<T>();
        }
        return entityList;
    }

    public List<T> findAll() {
        boolean foundLocation = false;
        SQLiteDatabase database = getReadableDatabase();
        Cursor result = database.query(tableName, null, null, null, null, null,
                "_id", null);

        List<T> entityList = new ArrayList<T>();
        while (result.moveToNext()) {
            foundLocation = true;
            T location = cursorToEntity(result);
            entityList.add(location);
        }
        result.close();

        if (!foundLocation) {
            return new ArrayList<T>();
        }
        return entityList;
    }

    public Long insert(T object) {
        Long result = null;
        try {
            SQLiteDatabase database = getWritableDatabase();
            result = database.insert(tableName, null, getContentValues(object));

            if (result != null && result >= 0) {
                Notifier.info(
                        getClass(),
                        CsTigoApplication.getContext().getString(
                                R.string.database_insert_success)
                            + " " + object.toString() + " " + result);
            } else {
                return null;
            }
        } catch (Exception e) {
            Notifier.error(
                    getClass(),
                    CsTigoApplication.getContext().getString(
                            R.string.database_insert_error)
                        + " " + object.toString() + " " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    public void update(T object) {
        Integer result = null;
        try {
            SQLiteDatabase database = getWritableDatabase();
            result = database.update(tableName, getContentValues(object),
                    "_id=?", new String[] { object.getId().toString() });
            if (result != null && result <= 0) {
                throw new Exception();
            }

            Notifier.info(
                    getClass(),
                    CsTigoApplication.getContext().getString(
                            R.string.database_update_success)
                        + " " + object.toString());
        } catch (Exception e) {
            Notifier.error(
                    getClass(),
                    CsTigoApplication.getContext().getString(
                            R.string.database_update_error)
                        + " " + object.toString() + e.getMessage());
        }

    }

    public void delete(T object) {
        Integer result = null;
        try {
            SQLiteDatabase database = getWritableDatabase();
            result = database.delete(tableName, "_id = ? ",
                    new String[] { object.getId().toString() });
            ;
            if (result != null && result <= 0) {
                throw new Exception();
            }

            Notifier.info(
                    getClass(),
                    CsTigoApplication.getContext().getString(
                            R.string.database_delete_success)
                        + " " + object.toString());
        } catch (Exception e) {
            Notifier.error(
                    getClass(),
                    CsTigoApplication.getContext().getString(
                            R.string.database_delete_error)
                        + " " + object.toString() + " " + e.getMessage());
        }

    }

    public void deleteAll() {
        Integer result = null;
        try {
            SQLiteDatabase database = getWritableDatabase();
            result = database.delete(tableName, null, null);
            ;
            if (result != null && result <= 0) {
                throw new Exception();
            }

            Notifier.info(
                    getClass(),
                    CsTigoApplication.getContext().getString(
                            R.string.database_delete_success));
        } catch (Exception e) {
            Notifier.error(
                    getClass(),
                    CsTigoApplication.getContext().getString(
                            R.string.database_delete_error)
                        + e.getMessage());
        }
    }

    protected abstract ContentValues getContentValues(T object);

    protected abstract T cursorToEntity(Cursor cursor);

    /**
     * Metodo utilitario que nos servira para obtener las sentencias a ser
     * ejecutadas de un asset dentro del paquete de la aplicacion
     * 
     * @param database
     * @param assetName
     */
    protected void executeSQLScript(SQLiteDatabase database, String assetName) {
        /*
         * variables locales para manejar la lectura del archivo de scripts
         */
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;

        try {
            /*
             * obtenemos el asset y lo convertimos a string
             */
            inputStream = assetManager.open(assetName);
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

            /*
             * desencriptamos el archivo
             */
            String sqlClear = Cypher.decrypt(SD, outputStream.toString());
            // String sqlClear = outputStream.toString();

            /*
             * separamos la cadena por el separador de sentencias
             */
            String[] createScript = sqlClear.split(";");

            /*
             * por cada sentencia del archivo ejecutamos la misma en la base de
             * datos
             */
            for (int i = 0; i < createScript.length; i++) {
                String sqlStatement = createScript[i].trim();

                if (sqlStatement.startsWith("--")) {
                    continue;
                }

                if (sqlStatement.length() > 0) {
                    Log.i(CsTigoApplication.getContext().getString(
                            CSTigoLogTags.DATABASE.getValue()),
                            CsTigoApplication.getContext().getString(
                                    R.string.database_exec_sql)
                                + sqlStatement);

                    try {
                        database.execSQL(sqlStatement + ";");
                    } catch (SQLException e) {
                        Notifier.error(
                                getClass(),
                                CsTigoApplication.getContext().getString(
                                        R.string.database_statement_exec)
                                    + e.getMessage());
                    } catch (Exception e) {
                        Notifier.error(
                                getClass(),
                                CsTigoApplication.getContext().getString(
                                        R.string.database_decypt_error)
                                    + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            Notifier.error(
                    getClass(),
                    CsTigoApplication.getContext().getString(
                            R.string.database_read_asset)
                        + e.getMessage());
        } catch (SQLException e) {
            Notifier.error(
                    getClass(),
                    CsTigoApplication.getContext().getString(
                            R.string.database_statement_exec)
                        + e.getMessage());
        } catch (Exception e) {
            Notifier.error(
                    getClass(),
                    CsTigoApplication.getContext().getString(
                            R.string.database_decypt_error)
                        + e.getMessage());
        }
    }

    protected T executeQuery(String where, String[] parameters) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.query(tableName, null, where, parameters, null,
                null, null);

        T entity = null;
        boolean foundParameter = false;
        while (result.moveToNext()) {
            if (!foundParameter) {
                entity = cursorToEntity(result);
                foundParameter = true;
                break;
            } else {
                result.close();
                Notifier.error(
                        getClass(),
                        CsTigoApplication.getContext().getString(
                                R.string.database_error_more_than_one));
                return null;
            }
        }
        result.close();
        return entity;
    }

    protected static Class getClass(String className) {
        if (className != null) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {

            }
        }
        return null;
    }
}
