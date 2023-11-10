package com.tec.pokemon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText edtPokemon;
    Button btnbuscar;
    TextView txtResultados;

    ImageView imgPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtPokemon = findViewById(R.id.edtNombre);
        btnbuscar = findViewById(R.id.btnBuscar);
        txtResultados = findViewById(R.id.txtResultado);
        imgPokemon = findViewById(R.id.imageView);
    }

    public void buscarPokemon(View v) {
        String pkmo = edtPokemon.getText().toString();
        String url = "https://pokeapi.co/api/v2/pokemon/" + pkmo;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String pokemonName = response.getString("name");
                    Log.i("PKMN Info", "Nombre del Pokémon: " + pokemonName);

                    // Extract the URL of the Pokémon's image.
                    String imageUrl = response.getJSONObject("sprites").getString("front_default");

                    JSONArray typesArray = response.getJSONArray("types");
                    List<String> typeList = new ArrayList<>();
                    for (int i = 0; i < typesArray.length(); i++) {
                        JSONObject typeObject = typesArray.getJSONObject(i);
                        JSONObject typeInfo = typeObject.getJSONObject("type");
                        String typeName = typeInfo.getString("name");
                        typeList.add(typeName);
                    }


                    String tiposPokemon = TextUtils.join(", ", typeList);
                    Log.i("PKMN Info", "Tipos del Pokémon: " + tiposPokemon);

                    String resultado = "Nombre del Pokémon: " + pokemonName + "\nTipos del Pokémon: " + tiposPokemon;

                    txtResultados.setText(resultado);

                    Picasso.get().load(imageUrl).into(imgPokemon);
                } catch (Exception e) {
                    e.printStackTrace();
                    txtResultados.setText("Pokemon no encontrado");
                    // Clear the ImageView in case of an error.
                    imgPokemon.setImageResource(android.R.color.transparent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    txtResultados.setText("Pokemon no encontrado");
                    imgPokemon.setImageResource(android.R.color.transparent);
                } else {
                    Log.e("PKMN Error", "Error: " + error.toString());
                    txtResultados.setText("Error: " + error.toString());
                    imgPokemon.setImageResource(android.R.color.transparent);
                }
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}

