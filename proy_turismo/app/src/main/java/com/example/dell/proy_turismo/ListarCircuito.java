package com.example.dell.proy_turismo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by user on 23/12/2014.
 */
public class ListarCircuito extends ActionBarActivity {

    int[] imagenRestaurantes = {
            R.drawable.restaurante_alexander,
            R.drawable.restaurante_angelo,
            R.drawable.restaurante_joe,
            R.drawable.restaurante_arriero
    };

    int[] imagenHoteles = {
            R.drawable.hotel_presidente,
            R.drawable.hotel_ritz,
            R.drawable.hotel_x,
            R.drawable.hotel_y,
            R.drawable.hotel_z
    };

    int[] imagenRutaDelVino = {
            R.drawable.rutadelvino_champaneramiguelmas,
            R.drawable.rutadelvino_champaneramiguelmas,
            R.drawable.rutadelvino_champaneramiguelmas,
            R.drawable.rutadelvino_lasmarianasbodegafamliar,
            R.drawable.rutadelvino_vinassegisa
    };


    String[] titulo;
    String[] contenido;

    private ListView lista;
    ListViewAdapter adapter;

    int currentViewPager;
    String nombreCircuito;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listar_circuito);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);  //ir atras

        Bundle extras = getIntent().getExtras();
        currentViewPager = extras.getInt("currentViewPager");
        nombreCircuito = extras.getString("nombreCircuito");
        Log.i("ramiro", "currentViewPager: " + currentViewPager);


        /**INDICAR TITULO **/
        actionBar.setTitle(nombreCircuito);

        lista = (ListView) findViewById(R.id.listView_listarCircuito);
        switch (currentViewPager){
            case 0: //restaurantes
                titulo = getResources().getStringArray(R.array.restaurantes_titulo);
                adapter = new ListViewAdapter(this, imagenRestaurantes, titulo);
                break;
            case 1: //circuito lunar
                titulo = getResources().getStringArray(R.array.hoteles_titulo);
                adapter = new ListViewAdapter(this, imagenHoteles, titulo);
                break;

            case 2: //ruta del vino
                titulo = getResources().getStringArray(R.array.miradores_titulo);
                adapter = new ListViewAdapter(this, imagenRutaDelVino, titulo);
                break;

            default:
                Toast.makeText(getApplicationContext(), "no esta cargado, pronto lo estará", Toast.LENGTH_SHORT).show();
        }
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ListarUnCircuito.class);
                i.putExtra("idcircuito",currentViewPager);
                i.putExtra("position", position);
                i.putExtra("nombreCircuito", nombreCircuito);
                i.putExtra("nombreSubCircuito", titulo[position]);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


            }
        });
    }


    /******************* LISTVIEW ADAPTER **************************/

    public class ListViewAdapter extends BaseAdapter {
        // Declare Variables
        Context context;
        int[] imagenes;
        String[] titulos;
        LayoutInflater inflater;

        public ListViewAdapter(Context context, int[] imagenes, String[] titulos) {
            this.context = context;
            this.imagenes = imagenes;
            this.titulos = titulos;
        }

        @Override
        public int getCount() {
            return titulos.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            // Declare Variables
            ImageView imgImg;
            TextView txtTitle;
            TextView txtContenido;

            //http://developer.android.com/intl/es/reference/android/view/LayoutInflater.html
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.single_post_circuito, parent, false);

            // Locate the TextViews in listview_item.xml
            imgImg = (ImageView) itemView.findViewById(R.id.imagen_single_post_circuito);
            txtTitle = (TextView) itemView.findViewById(R.id.tv_titulo_single_post_circuito);


            // Capture position and set to the TextViews
            imgImg.setImageResource(imagenes[position]);
            txtTitle.setText(titulos[position]);


            return itemView;
        }
    }
}
