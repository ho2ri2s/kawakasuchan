package com.kurus.kawakasuchan;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;

public class SoundDetection implements Runnable {

    //ボリューム感知リスナー
    private OnReachedVolumeListener listener;
    //録音中フラグ
    private boolean isRecording = true;
    //サンプリングレート 80.0kHz
    private static final int SAMPLE_RATE = 8000;
    //ボーダー音量
    // TODO: 2019/04/02 ドライヤーが起動したとわかるような音量を設定 
    private short borderVolume = 10000;


    public void setBorderVolume(short borderVolume){
        this.borderVolume = borderVolume;
    }
    public short getBorderVolume(){
      return borderVolume;
    }
    public void stop(){
        isRecording = false;
    }
    public void setOnReachedVolumeListener(OnReachedVolumeListener listener){
        this.listener = listener;
    }
    //ボーダー音量を超える音量を検知した際のリスナー
    public interface OnReachedVolumeListener{
        void onReachedVolume(short volume);
    }
    //スレッド開始（録音開始）
    public void run(){
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
        int buffersize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, buffersize);
        short[] buffer = new short[buffersize];
        audioRecord.startRecording();
        while (isRecording){
            audioRecord.read(buffer, 0, buffersize);
            short maxVolume = 0;
            for(int i = 0; i < buffersize; i++){
                // TODO: 2019/04/02 minVolumeを定義。ボーダーよりも小さければ読み込み終了。 
                maxVolume = (short)Math.max(maxVolume, buffer[i]);
                if(maxVolume > borderVolume){
                    if(listener != null){
                        listener.onReachedVolume(maxVolume);
                    }
                }
            }
        }
        audioRecord.stop();
        audioRecord.release();
    }

    public boolean getIsRecording(){
        return isRecording;
    }

}
