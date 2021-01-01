package com.app.recommendify4.RecomThreads;

import com.app.recommendify4.SpotifyApi.RequestSender;
import com.app.recommendify4.SpotifyApi.ResponseProcessor;
import com.app.recommendify4.SpotifyItems.Song.RecommendedSong;
import com.app.recommendify4.SpotifyItems.Song.UserSong;
import com.app.recommendify4.ThreadManagers.ThreadLauncher;
import com.app.recommendify4.UserInfo.Credentials;
import com.app.recommendify4.UserInfo.UserProfile;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.util.ArrayList;

public class ContentThread implements Runnable{

    private final UserSong baseForRecommendations;
    private final ContentCallback callback;
    private final Credentials credentials;

    public ContentThread(UserSong baseForRecommendations, ContentCallback callback, Credentials credentials){
        this.baseForRecommendations = baseForRecommendations;
        this.callback = callback;
        this.credentials = credentials;
    }


    @Override
    public void run() {
        System.out.println("Content thread started");
        System.out.println("BASE SONG: " + baseForRecommendations);
        ArrayList<RecommendedSong> recommendationsList = new ArrayList<>();
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
            RecommendedSong recommendedSong = new RecommendedSong(title, artist, id, 0);
            ThreadLauncher builder_updateTrack = new ThreadLauncher();
            builder_updateTrack.execute(new Runnable() {
                @Override
                public void run() {
                    String response = RequestSender.getTrackInfo(credentials,recommendedSong.getId());
                    ResponseProcessor.processTrackResponse(response,recommendedSong);
                }
            });
            recommendationsList.add(recommendedSong);
            System.out.println("(***DEBUG_MESAGE) BASE SONG: "+ baseForRecommendations.getName()+" --> Recommended song: "+recommendedSong.getName()+" - "+recommendedSong.getArtists().toString());
        }
        //System.out.println("NUMBER OF RECOMMENDATIONS: " + recommendationsList.size() + ". FOR SONG: " + baseForRecommendations);
        callback.onComplete(recommendationsList);
    }

}




