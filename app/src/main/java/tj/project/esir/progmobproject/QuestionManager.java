package tj.project.esir.progmobproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

public class QuestionManager {

    private static final String TABLE_NAME = "question";
    public static final String ID_QUESTION="id_question";
    public static final String TITLE_QUESTION="title_question";
    public static final String TEXT_RESPONSE1="text_response1";
    public static final String TEXT_RESPONSE2="text_response2";
    public static final String TEXT_RESPONSE3="text_response3";
    public static final String VALUE_RESPONSE1="value_response1";
    public static final String VALUE_RESPONSE2="value_response2";
    public static final String VALUE_RESPONSE3="value_response3";

    public static final String CREATE_TABLE_QUESTION = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+ID_QUESTION+" INTEGER primary key,"+
            " "+TITLE_QUESTION+" TEXT" +
            " "+TEXT_RESPONSE1+" TEXT" +
            " "+TEXT_RESPONSE2+" TEXT" +
            " "+TEXT_RESPONSE3+" TEXT" +
            " "+VALUE_RESPONSE1+" BOOLEAN" +
            " "+VALUE_RESPONSE2+" BOOLEAN" +
            " "+VALUE_RESPONSE3+" BOOLEAN" +
            ");";

    private MySQLite maBaseSQLite; // notre gestionnaire du fichier SQLite
    private SQLiteDatabase db;

    public QuestionManager(Context context) {
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

    public long addQuestion(Question question) {
        // Ajout d'un enregistrement dans la table
        ContentValues values = new ContentValues();
        values.put(TITLE_QUESTION, question.getTitle());
        values.put(TEXT_RESPONSE1, question.getResponse1().first);
        values.put(TEXT_RESPONSE2, question.getResponse2().first);
        values.put(TEXT_RESPONSE3, question.getResponse3().first);
        values.put(VALUE_RESPONSE1, question.getResponse1().second);
        values.put(VALUE_RESPONSE2, question.getResponse2().second);
        values.put(VALUE_RESPONSE3, question.getResponse3().second);

        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
        return db.insert(
                TABLE_NAME,null,values);
    }


    public int updateQuestion(Question question) {
        // modification d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la requête

        ContentValues values = new ContentValues();
        values.put(TITLE_QUESTION, question.getTitle());
        values.put(TEXT_RESPONSE1, question.getResponse1().first);
        values.put(TEXT_RESPONSE2, question.getResponse2().first);
        values.put(TEXT_RESPONSE3, question.getResponse3().first);
        values.put(VALUE_RESPONSE1, question.getResponse1().second);
        values.put(VALUE_RESPONSE2, question.getResponse2().second);
        values.put(VALUE_RESPONSE3, question.getResponse3().second);

        String where = ID_QUESTION+" = ?";
        String[] whereArgs = {question.getId()+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public int deleteQuestion(Question question) {
        // suppression d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la clause WHERE, 0 sinon

        String where = ID_QUESTION+" = ?";
        String[] whereArgs = {question.getId()+""};

        return db.delete(TABLE_NAME, where, whereArgs);
    }


    public Question getRandomQuestion() {
        // Retourne l'animal dont l'id est passé en paramètre

        Question question=new Question();

        Cursor c = db.rawQuery(
                "SELECT * FROM "+TABLE_NAME+" ORDER BY RANDOM() "+
                        "LIMIT 1", null);

        if (c.moveToFirst()) {
            question.setId(c.
                    getInt(c.getColumnIndex(ID_QUESTION)));
            question.setTitle(c.
                    getString(c.getColumnIndex(TITLE_QUESTION)));
            question.setResponse1(new Pair<>(c.getString(c.getColumnIndex(TEXT_RESPONSE1)),c.getInt(c.getColumnIndex(VALUE_RESPONSE1))));
            question.setResponse2(new Pair<>(c.getString(c.getColumnIndex(TEXT_RESPONSE2)),c.getInt(c.getColumnIndex(VALUE_RESPONSE2))));
            question.setResponse3(new Pair<>(c.getString(c.getColumnIndex(TEXT_RESPONSE3)),c.getInt(c.getColumnIndex(VALUE_RESPONSE3))));

            c.close();
        }
        return question;
    }

    public Question getQuestion(int id) {
        // Retourne l'animal dont l'id est passé en paramètre

        Question question=new Question();

        Cursor c = db.rawQuery(
                "SELECT * FROM "+TABLE_NAME+" WHERE "+
                        ID_QUESTION+"="+id, null);

        if (c.moveToFirst()) {
            question.setId(c.
                    getInt(c.getColumnIndex(ID_QUESTION)));
            question.setTitle(c.
                    getString(c.getColumnIndex(TITLE_QUESTION)));
            question.setResponse1(new Pair<>(c.getString(c.getColumnIndex(TEXT_RESPONSE1)),c.getInt(c.getColumnIndex(VALUE_RESPONSE1))));
            question.setResponse2(new Pair<>(c.getString(c.getColumnIndex(TEXT_RESPONSE2)),c.getInt(c.getColumnIndex(VALUE_RESPONSE2))));
            question.setResponse3(new Pair<>(c.getString(c.getColumnIndex(TEXT_RESPONSE3)),c.getInt(c.getColumnIndex(VALUE_RESPONSE3))));

            c.close();
        }
        return question;
    }

    public Cursor getQuestions() {
        // sélection de tous les enregistrements de la table
        return db.rawQuery("SELECT * FROM "+TABLE_NAME,
                null);
    }
}
