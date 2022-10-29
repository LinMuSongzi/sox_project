package com.example.cpp.business;

import androidx.annotation.Nullable;

import com.example.cpp.data.MusicInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.channels.Channels;

public interface IMusicAnalysis {
    String TAG = "MusicAnalysis";
    @Nullable
    default MusicInfo getMusicInfoByMusic(File file) {
        try {
            return getMusicInfoByMusic(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    default MusicInfo getMusicInfoByMusic(String path) {
        return getMusicInfoByMusic(new File(path));
    }

    @Nullable
    MusicInfo getMusicInfoByMusic(Channels channel);

    @Nullable
    MusicInfo getMusicInfoByMusic(InputStream inputStream);


}
