package com.example.recommendify4.RecomThreads;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.example.recommendify4.SpotifyItems.Song;
import java.util.ArrayList;

public class ContentThread implements Runnable{

    private final Song baseForRecommendations;
    private final ContentCallback callback;

    public ContentThread(Song baseForRecommendations, ContentCallback callback){
        this.baseForRecommendations = baseForRecommendations;
        this.callback = callback;
    }


    @Override
    public void run() {
        ArrayList<Song> recommendationsList = new ArrayList<>();
        Python py = Python.getInstance();
        PyObject pyf = py.getModule("FinalRecomendator");

        PyObject obj = pyf.callAttr("rank_song_similarity_by_measure", baseForRecommendations.toString(), 2);
        String recommendations = obj.toString();

        recommendations = recommendations.substring(1,recommendations.length() - 1);
        String[] Recoms = recommendations.split("],");

        for(int k = 1; k < Recoms.length - 1; k++){
            String title = Recoms[k].split(",")[0].substring(3,Recoms[k].split(",")[0].length()-1);
            String id =Recoms[k].split(",")[1].substring(2,Recoms[k].split(",")[1].length()-1);
            String artist = Recoms[k].split(",")[2].substring(2,Recoms[k].split(",")[2].length()-1);
            recommendationsList.add(new Song(title, artist, id));
        }
        //System.out.println("NUMBER OF RECOMMENDATIONS: " + recommendationsList.size() + ". FOR SONG: " + baseForRecommendations);
        callback.onComplete(recommendationsList);
    }

}




