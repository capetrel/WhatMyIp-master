package com.capetrel.whatmyip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.capetrel.whatmyip.adapter.FavorisAdapter;
import com.capetrel.whatmyip.models.Favoris;
import com.capetrel.whatmyip.tool.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class ShowActivity extends AppCompatActivity {

    private static final String PREF_FILE_NAME = "pref_file_name";
    private static final String PREF_KEY_IP = "key_ip";

    private String ipAdressFromAlertDialog;
    private String ipAdressInFavoris;

    private String line;
    private String response;

    private Handler mHandler;
    private TextView mTextViewFaiName;
    private TextView mTextViewMyIp;
    private TextView mTextViewCity;
    private TextView mTextViewCountry;
    private GoogleMap mMapView;
    private ListView mlistFavoris;

    private ProgressDialog progress;

    private Context mContext;

    private FavorisAdapter mFavorisAdapter;

    public ArrayList<Favoris> mFavoris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        mContext = this;

        mHandler = new Handler();

        mFavoris = Util.getFavoriteFromPrefs(this);

        // Récupération du ListView présent dans notre IHM
        mlistFavoris = (ListView) findViewById(R.id.list_view_favoris);

        mFavorisAdapter = new FavorisAdapter(this, mFavoris);
        mlistFavoris.setAdapter(mFavorisAdapter);

        // ici la gestion du click sur un item de la liste permet d'actualiser les info de la vue principal avec c'elles des favoris
        mlistFavoris.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewName, int position, long id) {

                //recuperer les données

                Favoris favoris = mFavoris.get(position);

                try {
                    JSONObject valor =new JSONObject(favoris.strJson);

                    Log.d("lol", "--------> clic item" + valor.toString());

                    // on les donne à update Ui
                    updateUi(valor);

                }catch (Exception e) {
                    Toast.makeText(ShowActivity.this, "il y a eu une erreur dans la traitements des données, réeesayez", Toast.LENGTH_SHORT).show();
                }


            }
        });
        // Le clic long permet de supprimer un favoris
        mlistFavoris.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteFavoris(position);
                return true;
            }
        });

        //TODO
        //faire une barre de chargement

        //TODO
        // sauvegarder les favoris quand on desinstalle l'application

        //TODO
        // graphisme :
        // Faire apparaitre les infos au dessus de la carte
        // faire les barre de séparation style
        // Agrandir le bouton "+" et la zone clicquable

        new Thread() {
            public void run() {
                // Do stuff

                try {
                    URL url = new URL("http://ip-api.com/json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    response = "";
                    line = "";
                    while ((line = reader.readLine()) != null) {
                        response += line;
                    }
                    reader.close();

                    final JSONObject data = new JSONObject(response);

                    Log.d("lol", "---------> data : " + data.toString());

                    // ce bout de code est executé dans le thread principal
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateUi(data);

                        }
                    });

                } catch (Exception e) {
                    Log.d("lol", "---------> problème reseau");
                }
            }
        }.start();
    }

    public void updateUi(JSONObject dataFromThread) {
        try {

            String ip = dataFromThread.getString("query");
            String isp = dataFromThread.getString("isp");
            String city = dataFromThread.getString("city");
            String country = dataFromThread.getString("country");
            Double lat = dataFromThread.getDouble("lat");
            Double lon = dataFromThread.getDouble("lon");

            Log.d("lol", "---------> info utilisé : " + ip + ", " + isp + ", " + city + ", " + country + ", " + lat + ", " + lon);

            mTextViewMyIp = (TextView) findViewById(R.id.text_view_my_ip);
            mTextViewMyIp.setText(ip);

            mTextViewFaiName = (TextView) findViewById(R.id.text_view_fai_Name);
            mTextViewFaiName.setText(isp);

            mTextViewCity = (TextView) findViewById(R.id.text_view_city);
            mTextViewCity.setText(city);

            mTextViewCountry = (TextView) findViewById(R.id.text_view_country);
            mTextViewCountry.setText(country);

            createMapView(lat, lon);


        } catch (Exception e) {
            Toast.makeText(ShowActivity.this, "ce n'est pas un fichier json ou ce dernier est mal formaté", Toast.LENGTH_SHORT).show();
        }

    }


    private void createMapView(Double lat, Double lon) {

        LatLng selectIpPoint = new LatLng(lat, lon);


        try {
            if (mMapView == null) {
                mMapView = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_view)).getMap();

                // Pour faire joujou avec l'API google map : https://developers.google.com/maps/documentation/android-api/views?hl=fr

                mMapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMapView.addMarker(new MarkerOptions().position(selectIpPoint).title("Position de l'I.P."));
                mMapView.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                mMapView.moveCamera(CameraUpdateFactory.newLatLngZoom(selectIpPoint, 12));

                Log.d("lol", "---------> coordonnées" + selectIpPoint.toString());

                if (mMapView == null) {
                    Toast.makeText(ShowActivity.this, "Erreur lors de la création de la map", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException exception) {
            Log.e("lol", exception.toString());
        }
    }

    public void onClickAlertDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
        builder.setTitle("Ajouter un favoris").setMessage("Saisissez une I.P.");

        // Rajoute des editText à l'alert dialog V2
        View content = getLayoutInflater().inflate(R.layout.alert_dialog, null);

        final EditText one = (EditText) content.findViewById(R.id.edit_view_ip_one);
        final EditText two = (EditText) content.findViewById(R.id.edit_view_ip_two);
        final EditText three = (EditText) content.findViewById(R.id.edit_view_ip_three);
        final EditText four = (EditText) content.findViewById(R.id.edit_view_ip_four);

        builder.setView(content);

        // On affiche les boutons de l'alert dialog
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // On récupère les 4 entrées.
                String ipOne = (one.getText().toString());
                String ipTwo = (two.getText().toString());
                String ipThree = (three.getText().toString());
                String ipFour = (four.getText().toString());

                // Au clic sur ok il faut traiter les infos

                final String ipAdressFromAlertDialog = ipOne + "." + ipTwo + "." + ipThree + "." + ipFour;

                // Vérification que l'adresse ip est conforme

                if (Util.isIpAddress(ipAdressFromAlertDialog) == true) {
                    // On sauvegarde dans le shared preferences
                    saveDataIp();
                    Log.d("lol", "---------> sauvegarde IP : " + ipAdressFromAlertDialog);

                    // Et on envoie l'ip à l'api
                    new Thread() {
                        public void run() {

                            try {
                                URL url = new URL("http://ip-api.com/json/" + ipAdressFromAlertDialog);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                                response = "";
                                line = "";
                                while ((line = reader.readLine()) != null) {
                                    response += line;
                                }
                                reader.close();

                                final JSONObject dataForFav = new JSONObject(response);

                                Log.d("lol", "---------> data for fav : " + dataForFav.toString());

                                // ce bout de code est executé dans le thread principal
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // on passe l'objet JSon à la méthode updateListView
                                        updateListView(response);

                                        // on met à jours la vue principale avec les infos du favoris
                                        updateUi(dataForFav);
                                    }
                                });

                            } catch (Exception e) {
                                Toast.makeText(ShowActivity.this, "problème de réseau", Toast.LENGTH_SHORT).show();

                                Log.d("lol", "---------> problème reseau");
                            }
                        }
                    }.start();
                    // Sinon l'alert dialog se ferme et un pop up d'erreur apparait
                } else {
                    Toast.makeText(ShowActivity.this, "L'adresse IP est invalide", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // Créer un méthode pour le shared preference sauvegarde de l'ip
    private void saveDataIp() {
        SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_KEY_IP, "" + ipAdressFromAlertDialog);
        editor.commit();
        // map pour recupérer en log le contenu du shared preferences
        Map<String, ?> keys = preferences.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("lol map values", entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    // Créer un méthode pour le shared preference pour supprimer les données
    private void deleteFavoris(final int position) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("Supprimer un favoris");
        builder.setMessage("Voulez vous vraiment supprimer ?");

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mFavoris.remove(position);
                mFavorisAdapter.notifyDataSetChanged();
                Util.writeFavouritePrefs(mContext, mFavoris);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.create();
        builder.show();
    }

    private void updateListView(String dataForFav) {

        try {

            Favoris favoris = new Favoris(dataForFav);

            // Transmettre les données aux textview de favoris adapter
            mFavoris.add(favoris);
            mFavorisAdapter.notifyDataSetChanged();

            // sauvegarde le favoris dans le share preference
            Util.writeFavouritePrefs(mContext, mFavoris);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ShowActivity.this, "Il y a eu une erreur, essayez à nouveau", Toast.LENGTH_SHORT).show();
        }

    }


}
