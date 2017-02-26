package com.jmulla.musicplayer;

/***
 * Created by Jamal on 21/02/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

//Adapter used to show the search results
class SongsRecyclerAdapter extends RecyclerView.Adapter {
    //different types that the view could be
    private static final int TYPE_DIVIDER = 0;
    private static final int TYPE_SONG = 1;
    private static final int TYPE_ARTIST = 2;
    private static final int TYPE_ALBUM = 3;
    private int tabColor1;
    private int tabColor2;
    private ArrayList<Object> dataSet;

    //constructor//
    SongsRecyclerAdapter(Context context, ArrayList<Object> results) {
        dataSet = results;
        TypedArray ta = context.getTheme().obtainStyledAttributes(R.styleable.ViewStyle);
        tabColor1 = ta.getColor(R.styleable.ViewStyle_colorBar1, 0);
        tabColor2 = ta.getColor(R.styleable.ViewStyle_colorBar2, 0);
    }

    //inflate the view depending on what type it is
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case TYPE_SONG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new SongType(view);
            case TYPE_ARTIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artists_list, parent, false);
                return new ArtistType(view);
            case TYPE_ALBUM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_list, parent, false);
                return new AlbumType(view);
            case TYPE_DIVIDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row_header, parent, false);
                return new DividerType(view);
        }
        return null;
    }

    private Object getItem(int position) {
        return dataSet.get(position);
    }

    //return the type of that item
    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Song) {
            return TYPE_SONG;
        } else if (getItem(position) instanceof ArtistModel) {
            return TYPE_ARTIST;
        } else if (getItem(position) instanceof AlbumModel) {
            return TYPE_ALBUM;
        }

        return TYPE_DIVIDER;
    }

    //inflates and shows the relevant details of each row in the list
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {
        Object object = getItem(listPosition);
        switch (getItemViewType(listPosition)) {
            case TYPE_SONG:
                //it's a song so put song details in the correct places
                Song song = (Song) object;
                final SongType songHolder = ((SongType) holder);
                if (songHolder.getAdapterPosition() % 2 == 0) {
                    songHolder.itemView.setBackgroundColor(tabColor1);
                } else {
                    songHolder.itemView.setBackgroundColor(tabColor2);
                }
                songHolder.title.setText(song.title);
                songHolder.artist.setText(song.artist);
                songHolder.duration.setText(song.duration);
                //if this song is clicked, play the song
                songHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Song> s = new ArrayList<>();
                        s.add((Song) getItem(songHolder.getAdapterPosition()));
                        Manager.currentSongList = new ArrayList<>(s);
                        Manager.currentSong = (Song) getItem(songHolder.getAdapterPosition());
                        Intent intent = new Intent(v.getContext(), CurrentSong.class);
                        intent.putExtra("START_POSITION", 0);
                        v.getContext().startActivity(intent);
                    }
                });

                break;
            case TYPE_ARTIST:
                //it's an artist so put the correct artist details in the right place
                ArtistModel artistModel = (ArtistModel) object;
                final ArtistType artistHolder = ((ArtistType) holder);
                if (artistHolder.getAdapterPosition() % 2 == 0) {
                    artistHolder.itemView.setBackgroundColor(tabColor1);
                } else {
                    artistHolder.itemView.setBackgroundColor(tabColor2);
                }
                artistHolder.artistName.setText(artistModel.getArtist());
                artistHolder.artistTracks.setText(String.valueOf(artistModel.getNumberOfTracks()));
                //if an artist is clicked, open a list of the songs belonging to that artist
                artistHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), GenericSongList.class);
                        intent.putExtra("com.jmulla.musicplayer.SONGS", ((ArtistModel) getItem(artistHolder.getAdapterPosition())).getSongs());
                        intent.putExtra("NAME", ((ArtistModel) getItem(artistHolder.getAdapterPosition())).getArtist());
                        v.getContext().startActivity(intent);
                    }
                });
                break;
            case TYPE_ALBUM:
                //it's an album so put the correct album details in the right place
                AlbumModel albumModel = (AlbumModel) object;
                final AlbumType albumHolder = ((AlbumType) holder);
                if (albumHolder.getAdapterPosition() % 2 == 0) {
                    albumHolder.itemView.setBackgroundColor(tabColor1);
                } else {
                    albumHolder.itemView.setBackgroundColor(tabColor2);
                }
                albumHolder.albumName.setText(albumModel.getAlbum());
                albumHolder.albumArtist.setText(albumModel.getArtist());
                //if an album is clicked, open a list of the songs belonging to that album
                albumHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), GenericSongList.class);
                        intent.putExtra("com.jmulla.musicplayer.SONGS", ((AlbumModel) getItem(albumHolder.getAdapterPosition())).getSongs());
                        intent.putExtra("NAME", ((AlbumModel) getItem(albumHolder.getAdapterPosition())).getAlbum());
                        v.getContext().startActivity(intent);
                    }
                });
                break;
            case TYPE_DIVIDER:
                //type is a divider so get the divider header text and set it
                DividerType dividerType = ((DividerType) holder);
                dividerType.dividerTitle.setText((String) object);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    //song view holder for the song info
    private static class SongType extends RecyclerView.ViewHolder {

        TextView title;
        TextView artist;
        TextView duration;
        RelativeLayout row_linearlayout;

        SongType(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.tv_search_title);
            this.artist = (TextView) itemView.findViewById(R.id.tv_search_artist);
            this.duration = (TextView) itemView.findViewById(R.id.tv_search_duration);
            this.row_linearlayout = (RelativeLayout) itemView.findViewById(R.id.rl_song_item);
        }
    }

    //artist view holder for the artist info
    private static class ArtistType extends RecyclerView.ViewHolder {

        TextView artistName;
        TextView artistTracks;

        ArtistType(View itemView) {
            super(itemView);

            this.artistName = (TextView) itemView.findViewById(R.id.artist_name);
            this.artistTracks = (TextView) itemView.findViewById(R.id.artist_tracks);
        }
    }

    //album view holder for the album info
    private static class AlbumType extends RecyclerView.ViewHolder {

        TextView albumName;
        TextView albumArtist;

        AlbumType(View itemView) {
            super(itemView);

            this.albumName = (TextView) itemView.findViewById(R.id.album_name);
            this.albumArtist = (TextView) itemView.findViewById(R.id.album_artist);
        }
    }

    //holder for the divider
    private static class DividerType extends RecyclerView.ViewHolder {

        TextView dividerTitle;

        DividerType(View itemView) {
            super(itemView);

            this.dividerTitle = (TextView) itemView.findViewById(R.id.headerTitle);
        }
    }
}