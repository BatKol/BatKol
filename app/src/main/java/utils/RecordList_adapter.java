package utils;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.batkol.R;

import java.util.List;

import models.RecordCard;

public class RecordList_adapter extends RecyclerView.Adapter<RecordList_adapter.ViewHolder>
{

    private LayoutInflater layoutInflater;
    private Context currentActivity;
    private List<RecordCard> data;


    public RecordList_adapter(Context context, List<RecordCard> data)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.currentActivity = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = layoutInflater.inflate(R.layout.audio_card_layout, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {

        // bind the textview with data received
        RecordCard c = data.get(i);
        String creator = c.getCreatorName();
        String Date = c.getPublishDate().toString();
        String recordUrl = c.getRecordUrl();

        viewHolder.getCreator().setText(creator);
        viewHolder.getPublishDate().setText(Date);

        fetchRecord(viewHolder, viewHolder.getMediaPlayer(), recordUrl);


/*        viewHolder.GetmyView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });*/
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private TextView tv_creatorName, tv_datePublish, tv_time, tv_duration;
        private Button btn_play;
        private View myView;
        private MediaPlayer recordPlayer;
        private SeekBar seekBarTime;

        public View GetmyView()
        {
            return myView;
        }

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            myView = itemView;

            tv_duration = itemView.findViewById(R.id.tvDuration);
            tv_time = itemView.findViewById(R.id.tvTime);
            seekBarTime = itemView.findViewById(R.id.seekBarTime);
            tv_creatorName = itemView.findViewById(R.id.record_creator);
            tv_datePublish = itemView.findViewById(R.id.record_publish_date);
            btn_play = itemView.findViewById(R.id.btnPlay);

        }

        public TextView getCreator() { return tv_creatorName; }

        public TextView getPublishDate() { return tv_datePublish; }

        public TextView getTv_time() { return tv_time; }

        public TextView getTv_duration() { return tv_duration; }

        public Button getBtn_play() { return btn_play; }

        public MediaPlayer getMediaPlayer(){ return recordPlayer; };


        public SeekBar getSeekBarTime() { return seekBarTime; }
    }

    public void initPlayer(ViewHolder view, MediaPlayer recordPlayer, String url)
    {


        recordPlayer = MediaPlayer.create(currentActivity, Uri.parse(url));
        
        recordPlayer.setLooping(false);
        recordPlayer.seekTo(0);
        recordPlayer.setVolume(0.5f, 0.5f);

        setPlayListener(view.getBtn_play(), recordPlayer);

        String duration = AlgorithmsLibrary.millisecondsToString(recordPlayer.getDuration());
        view.getTv_duration().setText(duration);

        view.getSeekBarTime().setMax(recordPlayer.getDuration());
        MediaPlayer record = recordPlayer;
        //record.start();

        view.getSeekBarTime().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser)
            {
                if(isFromUser)
                {
                    record.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (record != null) {
                    if(record.isPlaying()) {
                        try {
                            final double current = record.getCurrentPosition();
                            final String elapsedTime = AlgorithmsLibrary.millisecondsToString((int) current);

                            ((Activity)currentActivity).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.getTv_time().setText(elapsedTime);
                                    view.getSeekBarTime().setProgress((int) current);
                                }
                            });

                            Thread.sleep(1000);
                        }catch (InterruptedException e) {}
                    }
                }
            }
        }).start();
    }

    public void setPlayListener(Button play, MediaPlayer recordPlayer){
        if(recordPlayer != null)
        {
            play.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(recordPlayer.isPlaying())
                    {
                        // is playing
                        recordPlayer.pause();
                        play.setBackgroundResource(R.drawable.ic_play);
                    }
                    else
                    {
                        // on pause
                        recordPlayer.start();
                        play.setBackgroundResource(R.drawable.ic_pause);
                    }
                }
            });
        }

    }


    public void fetchRecord(ViewHolder view, MediaPlayer recordPlayer, String url){
        // call db to fetch record
        initPlayer(view, recordPlayer, url);
    }


}