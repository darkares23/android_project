package com.arpad.a209.plancelular;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.database.Cursor;

public class MainActivity extends AppCompatActivity {
    EditText etNumeroCelular, etValor;
    RadioButton rbIndefinido, rb1kMin, rb500min, rbSeleccion;
    Button btnGuardar, btnModificar, btnEliminar, btnConsultar;
    String tpOpcion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNumeroCelular = findViewById(R.id.etNumeroCelular);
        etValor = findViewById(R.id.etValor);

        rbIndefinido = findViewById(R.id.rbIndefinido);
        rb1kMin = findViewById(R.id.rb1kMin);
        rb500min = findViewById(R.id.rb500Min);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnModificar = findViewById(R.id.btnModificar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnConsultar = findViewById(R.id.btnConsultar);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Qué RB está seleccionado
                if (rbIndefinido.isChecked()){
                    tpOpcion = "opcion1";
                    rbSeleccion = rbIndefinido;
                } else if (rb1kMin.isChecked()){
                    tpOpcion = "opcion2";
                    rbSeleccion = rb1kMin;
                } else {
                    tpOpcion = "opcion3";
                    rbSeleccion = rb500min;
                }

                guardar(etNumeroCelular.getText().toString(),
                        tpOpcion,
                        etValor.getText().toString());
                etNumeroCelular.setText("");
                etValor.setText("");
                rbSeleccion.setChecked(false);
            }
        });
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listar(etNumeroCelular.getText().toString());
            }
        });

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbIndefinido.isChecked()){
                    tpOpcion="opcion1";
                    rbSeleccion= rbIndefinido;
                }else if (rb1kMin.isChecked()){
                    tpOpcion="opcion2";
                    rbSeleccion=rb1kMin;
                } else{
                    tpOpcion="opcion3";
                    rbSeleccion=rb500min;
                }
                modificar(etNumeroCelular.getText().toString(),
                tpOpcion,
                etValor.getText().toString());
            }


        });
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Eliminar(etNumeroCelular.getText().toString());
            }
        });
    }

    private void guardar (String numCel, String tipPlan, String valor){
        DatosBase helper = new DatosBase(this, "BDCELULAR", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues c = new ContentValues();
            c.put("NUMERO_CELULAR", numCel);
            c.put("TIPO_PLAN", tipPlan);
            c.put("VALOR", valor);

            db.insert("PCelular", null, c);
            db.close();
            Toast.makeText(this, "Plan creado con éxito", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, "¡ERROR! Plan no creado" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void listar(String numCel)
    {
        DatosBase helper = new DatosBase(this,"BDCELULAR",null,1);
        SQLiteDatabase db= helper.getWritableDatabase();
        try
        {
            String sql = "Select NUMERO_CELULAR, TIPO_PLAN, VALOR From PCelular where NUMERO_CELULAR = " + numCel;
            Cursor c = db.rawQuery(sql,null);
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                etValor.setText("" + c.getString(2));

                if (c.getString(1).equals("opcion1")){
                    rbIndefinido.setChecked(true);
                } else if (c.getString(1).equals("opcion2")){
                    rb1kMin.setChecked(true);
                } else {
                    rb500min.setChecked(true);
                }
            }
            else
            {
                Toast.makeText(this,"NO existe ", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void modificar(String numCel,String tipPlan, String valor){
        DatosBase helper = new DatosBase(this,"BDCELULAR",null,1);
        SQLiteDatabase db= helper.getWritableDatabase();
        try
        {
            ContentValues c = new ContentValues();
            c.put("VALOR",valor);
            c.put("TIPO_PLAN",tipPlan);

            String where ="NUMERO_CELULAR=?";
            String[] wherearg= new String[]{String.valueOf(numCel)};
            db.update("PCelular",c,where, wherearg);
            db.close();
            Toast.makeText(this,"Modificado Correctamente", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "PLAN no modificado"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void Eliminar(String numCel){
        DatosBase helper=new DatosBase(this, "BDCELULAR", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        try{

            ContentValues c = new ContentValues();
            String donde= "NUMERO_CELULAR=?";
            String[]dondearg= new String[]{String.valueOf(numCel)};
            db.delete("PCelular", donde,dondearg);
            db.close();

            Toast.makeText(this,"Elimindo Correctamente", Toast.LENGTH_SHORT).show();

        }catch (Exception e){

            Toast.makeText(this, "PLAN no Eliminado"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    public class DatosBase extends SQLiteOpenHelper {
        String tabla = "CREATE TABLE PCELULAR (NUMERO_CELULAR TEXT PRIMARY KEY, TIPO_PLAN TEXT, VALOR TEXT)";
        public DatosBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super (context, name, factory, version);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(tabla);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE PCELULAR");
            db.execSQL(tabla);
        }
    }


}
