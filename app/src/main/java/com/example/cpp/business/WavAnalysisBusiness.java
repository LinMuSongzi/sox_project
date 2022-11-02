package com.example.cpp.business;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cpp.data.MusicInfo;
import com.musongzi.core.base.business.BaseWrapBusiness;
import com.musongzi.core.itf.IViewInstance;

import java.io.File;
import java.io.FileOutputStream;
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
            musicInfo.setChannel(reductionByteToInt4(bytesOne[0x16]));
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

    public static void writeWaveFileHeader(byte[] header, long totalAudioLen,
                                     long totalDataLen, int sampleRate, int channels, int byteRate)
            throws IOException {
        /**
         *
         *
         * 0101 1101 1100 0000
         *0101110111000000
         *
         * 0111 0111 119
         *
         * 0000 0001
         * 1
         */
//        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
//        out.write(header, 0, 44);
    }

}
