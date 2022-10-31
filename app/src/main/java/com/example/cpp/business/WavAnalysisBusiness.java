package com.example.cpp.business;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cpp.data.MusicInfo;
import com.musongzi.core.base.business.BaseWrapBusiness;
import com.musongzi.core.itf.IViewInstance;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;

import kotlin.jvm.functions.Function1;

public class WavAnalysisBusiness extends BaseWrapBusiness<IViewInstance> implements IMusicAnalysis {


    @Override
    public MusicInfo getMusicInfoByMusic(@NonNull Channels channel) {
        return null;
    }

    @Override
    public MusicInfo getMusicInfoByMusic(@NonNull InputStream input, byte[] bytes) {

        byte[] bytesOne = bytes != null ? bytes : new byte[0x24];
        MusicInfo musicInfo = null;
        try {
            input.read(bytesOne);
            musicInfo = new MusicInfo();
            musicInfo.setChannel(reductionByteToInt4(bytesOne[0x16], bytesOne[0x17], bytesOne[0x18], bytesOne[0x19]));
            musicInfo.setBit((reductionByteToInt4(bytesOne[0x22], bytesOne[0x23])));
            musicInfo.setSimpleRate(reductionByteToInt4(bytesOne[0x18], bytesOne[0x19], bytesOne[0x1a], bytesOne[0x1b]));
            if (bytesOne[0x12] == 0x12) {
            } else if (bytesOne[0x12] == 0x10) {
                musicInfo.setHeadBitSize(44);

            }
            Log.i(TAG, "handlerBusiness: musicInfo = " + musicInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return musicInfo;
    }

    @Override
    public MusicInfo getMusicInfoByMusic(@NonNull File file) {
        MusicInfo musicInfo = IMusicAnalysis.super.getMusicInfoByMusic(file);
        if (musicInfo != null) {
            long time = (file.length() - musicInfo.getHeadBitSize()) / (musicInfo.getSimpleRate() * musicInfo.getBit() * musicInfo.getChannel() / 8);
            musicInfo.setTimeSecond(time);
        }
        return null;
    }

    @Override
    public MusicInfo getMusicInfoByMusic(@NonNull String path) {
        return IMusicAnalysis.super.getMusicInfoByMusic(path);
    }

    /**
     * 93(01011 0101)   -64(1100 0000)
     * <p>
     * 01011 0101 1100 0000 = 24000
     *
     * @param bytes
     * @return
     */


    public static int reductionByteToInt4(byte... bytes) {
        int i = 1;
        int sum = reductionSinpleByte(bytes[0]);
        for (; i < bytes.length; i++) {
            sum |= reductionSinpleByte(bytes[i]) << (i * 8);
        }
        return sum;
    }


    public static int reductionSinpleByte(byte b) {
        if (b >= 0) {
            return b;
        } else {
            return (b * -1) | 128;
        }
    }

}
