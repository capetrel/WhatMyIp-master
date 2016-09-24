package com.capetrel.whatmyip.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.capetrel.whatmyip.R;
import com.capetrel.whatmyip.ShowActivity;
import com.capetrel.whatmyip.models.Favoris;

import java.util.ArrayList;

/**
 * Created by capetrel on 27/07/2016.
 */
public class FavorisAdapter extends BaseAdapter {

    public ArrayList<Favoris> mFavoris;
    private LayoutInflater inflater;

    public FavorisAdapter(Context context, ArrayList<Favoris> favoris) {
        inflater = LayoutInflater.from(context);
        mFavoris = favoris;
    }

    private class ViewHolder {
        private TextView mtextViewFavName;
        private TextView mTextViewFavIp;
        private TextView mTextViewFavCity;
        private TextView mTextViewFavCountry;
    }

    @Override
    public int getCount() {
        //Taille de la "bibliothèque"
        return mFavoris.size();
    }

    @Override
    public long getItemId(int position) {
        // La position
        return position;
    }

    @Override
    public Object getItem(int position) {
        // L'item correspondant à cette position
        return mFavoris.get(position);
    }

    // La méthode getView est appelé par le système à chaque fois qu’il faut afficher un item du ListView.
    // C’est dans cette méthode que vous allez instancier et modifier les éléments de l’item qui va être affiché.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Toujours prévoir un log pour voir le fonctionnement de la méthode.
        Log.d(" lol", "position------------>" + position);

        ViewHolder holder;

        //si holder est vide, première fois qu'on l'appel
        if (convertView == null) {
            // Nouvel objet avec les composants de l’item
            holder = new ViewHolder();

            // La nouvelle vue est initialisé avec le layout d’un item
            convertView = inflater.inflate(R.layout.list_view_item_favoris, null);

            // Initialisation des attributs de notre objet ViewHolder
            holder.mtextViewFavName = (TextView) convertView.findViewById(R.id.text_view_fav_name_ip);
            holder.mTextViewFavIp = (TextView) convertView.findViewById(R.id.text_view_fav_ip);
            holder.mTextViewFavCity = (TextView) convertView.findViewById(R.id.text_view_fav_city_ip);
            holder.mTextViewFavCountry = (TextView) convertView.findViewById(R.id.text_view_fav_country_ip);

            // On lie l’objet ViewHolder à la vue pour le sauvegarder grace à setTag
            convertView.setTag(holder);

        } else { // sinon holder à déjà été appellé.

            // On renvoie les valeurs stocké dans le setTag
            holder = (ViewHolder) convertView.getTag();
        }

        Favoris favoris = mFavoris.get(position);

        holder.mtextViewFavName.setText(favoris.mISP);
        holder.mTextViewFavIp.setText(favoris.mQuery);
        holder.mTextViewFavCity.setText(favoris.mCity);
        holder.mTextViewFavCountry.setText(favoris.mCountry);

        return convertView;
    }
}
