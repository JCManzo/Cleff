package com.freneticlabs.cleff.utils;

import android.content.Context;

import com.freneticlabs.cleff.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by jcmanzo on 8/24/14.
 */
public class CleffJSONSerializer {

    private Context mContext;
    private String mFilename;

    public CleffJSONSerializer(Context context, String filename) {
        mContext = context;
        mFilename = filename;
    }

    public void saveLibrary(ArrayList<Song> songs)
            throws JSONException, IOException {
        // Builds the JSON array
        JSONArray jsonArray = new JSONArray();
        for(Song song : songs) {
            jsonArray.put(song.toJSON());
        }

        //Writes the file to disk
        Writer writer = null;
        try {
            OutputStream outputStream = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(outputStream);
            writer.write(jsonArray.toString());
        } finally {
            if(writer != null) {
                writer.close();
            }
        }
    }

    public ArrayList<Song> loadLibrary() throws IOException, JSONException {
        ArrayList<Song> songs = new ArrayList<Song>();
        BufferedReader bufferedReader = null;

        try {
            //Open and read the file
            InputStream inputStream = mContext.openFileInput(mFilename);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonString = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                // Line nreas are ommited and irrevelant
                jsonString.append(line);
            }

            // Parse JSON
            JSONArray jsonArray = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            //Build the song array
            for (int i = 0; i < jsonArray.length(); i++) {
                songs.add(new Song(jsonArray.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
                // Ignore this one. It happens when starting fresh
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return songs;
    }
}
