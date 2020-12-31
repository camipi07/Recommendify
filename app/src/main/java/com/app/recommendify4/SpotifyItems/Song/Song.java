package com.app.recommendify4.SpotifyItems;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Song  implements Parcelable {

    private String name;
    private String album;
    private String id;
    private ArrayList<Artist> artists;
    private String ArtistString;
    private int timesInList;
    private boolean recommended;
    private String coverURL;
    private String prewviewURL;
    private int Coincidence;


    public Song(String songName, String albumName, ArrayList<Artist> artistsList, String songId, int timesInList){
        this.name = songName;
        this.album = albumName;
        this.artists = artistsList;
        this.id = songId;
        this.timesInList = timesInList;


    }

    public Song(String songName, String artistsList, String songId){
        this.name = songName;
        this.artists = getArtistsFromString(artistsList);
        this.id = songId;
        this.Coincidence = 0;
    }

    public Song(JSONObject songInfo, int timesInList) throws JSONException {
        this.artists = new ArrayList<>();
        this.name = songInfo.getString("name");
        this.id = songInfo.getString("id");
        this.album = songInfo.getJSONObject("album").getString("name");
        JSONArray artistsJSON = songInfo.getJSONArray("artists");
        for(int artist = 0; artist < artistsJSON.length(); artist++){
            String artistName = artistsJSON.getJSONObject(artist).getString("name");
            String artistId = artistsJSON.getJSONObject(artist).getString("id");
            this.artists.add(new Artist(artistName, artistId));
        }
        this.timesInList = timesInList;

    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public String getAlbum() {
        return album;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getCoverURL(){return coverURL; }

    public String getPrewviewURL(){ return prewviewURL; }

    public int getCoincidence(){return Coincidence; }

    public void setCoincidence(int Coincidence){this.Coincidence = Coincidence;}

    //public String getartists() {return Artist;}

    //public void setArtists(String Artist){this.Artist = Artist;}

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists = artists;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoverURL(String url) { this.coverURL = url; }

    public void setPrewviewURL(String url)  {this.prewviewURL = url; }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isRecommended() { return recommended; }

    public void setRecommended(boolean recommended) { this.recommended = recommended; }

    public void addOneToTimesInList(){ this.timesInList++; }

    public int getTimesInList() { return timesInList; }

    public void setTimesInList(int timesInList) { this.timesInList = timesInList; }

    private ArrayList<Artist> getArtistsFromString(String artistList){
        ArrayList<Artist> list = new ArrayList<>();
        String[] names = artistList.split(",");
        for(String name : names){
            Artist artist = new Artist(name);
            list.add(artist);
        }
        return list;
    }

    @Override
    public boolean equals(Object obj) {
        return this.id.equals(((Song) obj).id);
    }

    public String getartistsString() {return ArtistString;}

    @Override
    public String toString() {
        return "Song{" +
                "name='" + name + '\'' +
                ", album='" + album + '\'' +
                ", id='" + id + '\'' +
                ", artists=" + artists +
                ", timesInList=" + timesInList +
                '}';
    }

    //PARCELABLE IMPLEMENTATION
    private Song(Parcel in) {
        artists = new ArrayList<>();
        name = in.readString();
        album = in.readString();
        id = in.readString();
        in.readTypedList(artists, Artist.CREATOR);
        timesInList = in.readInt();
    }

    public static final Parcelable.Creator<Song> CREATOR
            = new Parcelable.Creator<Song>() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(album);
        dest.writeString(id);
        dest.writeTypedList(artists);
        dest.writeInt(timesInList);
    }


    public static Comparator<Song> Coincidences = new Comparator<Song>() {

        public int compare(Song s1, Song s2) {

            int song1 = s1.getCoincidence();
            int song2 = s2.getCoincidence();

            return song2-song1;

        }};
}