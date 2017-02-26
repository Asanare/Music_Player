package com.jmulla.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;

/***
 * Created by Jamal on 07/02/2017.
 */
//Class for editing id3 tags of songs
public class TagEditor extends Activity {
    //member variables
    Song song;
    int pos;
    EditText et_title, et_artist, et_album;
    Button btn_cancel, btn_save;
    DatabaseHandler databaseHandler;

    //called when this class is created. Initialises everyhting
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tags);
        et_title = (EditText) findViewById(R.id.et_title);
        et_artist = (EditText) findViewById(R.id.et_artist);
        et_album = (EditText) findViewById(R.id.et_album);
        btn_save = (Button) findViewById(R.id.btn_save_tags);
        btn_cancel = (Button) findViewById(R.id.btn_cancel_tags);
        final Intent intent = getIntent();
        //get the song we're editing
        song = (Song) intent.getSerializableExtra("com.jmulla.musicplayer.TagEditor.SONG");
        pos = intent.getIntExtra("com.jmulla.musicplayer.TagEditor.POSITION", -1);
        File file = new File(song.location);
        AudioFile f = null;
        try {
            f = AudioFileIO.read(file);
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            e.printStackTrace();
        }
        Tag tag = null;
        if (f != null) {
            tag = f.getTag(); //get all tags
        }
        final Tag finalTag = tag;
        final AudioFile finalF = f;
        databaseHandler = new DatabaseHandler(getBaseContext());
        et_title.setText(song.title);
        et_artist.setText(song.artist);
        if (tag != null) {
            et_album.setText(tag.getFirst(FieldKey.ALBUM));
        }
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //if the user clicks save, update the song with the new tags
                    if (finalTag != null) {
                        finalTag.setField(FieldKey.ARTIST, et_artist.getText().toString());
                        finalTag.setField(FieldKey.TITLE, et_title.getText().toString());
                        finalTag.setField(FieldKey.ALBUM, et_album.getText().toString());
                        Song s = new Song(song.id, et_title.getText().toString(), et_artist.getText().toString(), et_album.getText().toString(), song.duration, song.location);
                        if (databaseHandler.updateSong(s) > 0) {
                            Manager.needToUpdate = true;
                        }
                    }

                } catch (FieldDataInvalidException e) {
                    e.printStackTrace();
                }
                try {
                    AudioFileIO.write(finalF);
                } catch (CannotWriteException e) {
                    e.printStackTrace();
                }
                finish();
            }


        });


    }
}
