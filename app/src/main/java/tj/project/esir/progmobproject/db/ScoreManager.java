package tj.project.esir.progmobproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import tj.project.esir.progmobproject.models.Question;
import tj.project.esir.progmobproject.models.Score;

public class ScoreManager {

    private static final String TABLE_NAME = "score";
    public static final String ID_SCORE="id_score";
    public static final String NAME_GAME="name_game";
    public static final String SCORE="score";


    public static final String CREATE_TABLE_SCORE = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+ID_SCORE+" INTEGER primary key,"+
            " "+NAME_GAME+" TEXT," +
            " "+SCORE+" INTEGER" +
            ");";

    private MySQLite maBaseSQLite; // notre gestionnaire du fichier SQLite
    private SQLiteDatabase db;

    public ScoreManager(Context context) {
        maBaseSQLite = MySQLite.getInstance(context);
    }

    public void open() {
        //on ouvre la table en lecture/écriture
        db = maBaseSQLite.getWritableDatabase();
    }

    public void close() {
        //on ferme l'accès à la BDD
        db.close();
    }

    public long addScore(Score score) {
        // Ajout d'un enregistrement dans la table
        ContentValues values = new ContentValues();
        values.put(ID_SCORE, score.getId());
        values.put(NAME_GAME, score.getName_game());
        values.put(SCORE, score.getScore());

        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
        return db.insert(
                TABLE_NAME,null,values);
    }


    public int updateScore(Score score) {
        // modification d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la requête

        ContentValues values = new ContentValues();
        values.put(ID_SCORE, score.getId());
        values.put(NAME_GAME, score.getName_game());
        values.put(SCORE, score.getScore());

        String where = ID_SCORE+" = ?";
        String[] whereArgs = {score.getId()+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public int deleteScore(int id) {
        // suppression d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la clause WHERE, 0 sinon

        String where = ID_SCORE+" = ?";
        String[] whereArgs = {id+""};

        return db.delete(TABLE_NAME, where, whereArgs);
    }



    public Score getScore(int id) {
        // Retourne l'animal dont l'id est passé en paramètre
        Score score = new Score();
        Cursor c = db.rawQuery(
                "SELECT * FROM "+TABLE_NAME+" WHERE "+
                        ID_SCORE+"="+id, null);

        if (c.moveToFirst()) {
            score.setId(c.
                    getInt(c.getColumnIndex(ID_SCORE)));
            score.setName_game(c.
                    getString(c.getColumnIndex(NAME_GAME)));
            score.setScore(c.
                    getInt(c.getColumnIndex(SCORE)));

            c.close();
        }
        return score;
    }



    public ArrayList<Score> getAllScore(){

        ArrayList<Score> tabScore = new ArrayList<Score>();

        Cursor c = db.rawQuery(
                "SELECT * FROM "+TABLE_NAME, null);
        if (c.moveToFirst()) {
            do {
                tabScore.add(
                        new Score(c.getInt(c.getColumnIndex(ID_SCORE)),c.
                        getString(c.getColumnIndex(NAME_GAME)),c.
                        getInt(c.getColumnIndex(SCORE))));
            } while (c.moveToNext());
            c.close();
        }

        return tabScore;
    }

}
