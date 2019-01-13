package com.websarva.wings.android.databesesample;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ListIterator;

public class CockTailMemoActivity extends AppCompatActivity {

    int _cocktailId = -1;
    String _cocktailName = "";
    TextView _tvCocktailName;
    Button _btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cock_tail_memo);

        _tvCocktailName = findViewById(R.id.tvCocktailName);
        _btnSave = findViewById(R.id.btnSave);
        ListView lvCocktail = findViewById(R.id.lvCocktail);
        lvCocktail.setOnItemClickListener(new ListItemClickListener());

    }

    public void onSaveButtonClick(View view){
        EditText etNote = findViewById(R.id.etNote);
        String note = etNote.getText().toString();
        DatabaseHelper helper = new DatabaseHelper(CockTailMemoActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            //予めリストで選択されたアイテムのメモデータ(DBデータ)を削除
            String sqlDelete = "DELETE FROM cocktailmemo WHERE _id = ?";
            SQLiteStatement stmt = db.compileStatement(sqlDelete); //SQL文字列をもとにprepared statementを取得
            stmt.bindLong(1, _cocktailId);
            stmt.executeUpdateDelete();

            //その後DBにインサート
            String sqlInsert = "INSERT INTO cocktailmemo (_id, name, note) VALUES (?, ?, ?)"; //インサート用SQL文字列の用意
            stmt = db.compileStatement(sqlInsert); //SQL文字列をもとにprepared statementを取得
            stmt.bindLong(1, _cocktailId); //バインド:SQL文中に記述した"?"に変数を埋め込む。
            stmt.bindString(2, _cocktailName);
            stmt.bindString(3, note);
            stmt.executeInsert();
        }
        finally {
            db.close();
        }

        _tvCocktailName.setText(getString(R.string.tv_name));
        etNote.setText("");
        _btnSave.setEnabled(false); //[保存]ボタンをクリック不可能に設定
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            _cocktailId = position; //タップ行番号を主キーIDに代入
            _cocktailName = (String) parent.getItemAtPosition(position); //タップ行データ取得
            _tvCocktailName.setText(_cocktailName); //カクテル名表示
            _btnSave.setEnabled(true); //[保存]ボタンをクリック可能に再設定

            DatabaseHelper helper = new DatabaseHelper(CockTailMemoActivity.this);
            SQLiteDatabase db = helper.getWritableDatabase();
            try {
                String sql = "SELECT * FROM cocktailmemo WHERE _id = " + _cocktailId;
                Cursor cursor = db.rawQuery(sql, null);
                String note = "";
                while(cursor.moveToNext()){
                    int idxNote = cursor.getColumnIndex("note");
                    note = cursor.getString(idxNote);
                }
                EditText etNote = findViewById(R.id.etNote);
                etNote.setText(note);
            }
            finally {
                db.close();
            }
        }
    }
}


