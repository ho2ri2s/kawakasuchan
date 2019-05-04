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
    //サンプリングレート 44.1kHz
    private static final int SAMPLE_RATE = 44100;
    //ボーダー音量
    private short borderVolume = 8000;


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
        //優先順位を設定
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
        //音量データのバッファサイズ（byte）
        //デバイスの要求する最小値より大きくする必要がある
        int buffersize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        //AudioRecordのインスタンス化
        AudioRecord audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, buffersize);

        short[] buffer = new short[buffersize];
        //録音開始
        audioRecord.startRecording();
        while (isRecording){
            //音声データ読み込み
            audioRecord.read(buffer, 0, buffersize);

            short maxVolume = 0;
            for(int i = 0; i < buffersize; i++){
                maxVolume = (short)Math.max(maxVolume, buffer[i]);
                if(maxVolume > borderVolume){
                    if(this.listener != null){
                        this.listener.onReachedVolume(maxVolume);
                        break;
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

    public void removeListener(){
        this.listener = null;
    }

}
