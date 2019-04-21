package tk.lorddarthart.videostreamplayer;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class PortraitFragment extends Fragment {

    private String ARG_PARAM1 = "param1";
    private String ARG_PARAM2 = "param2";
    private String param1;
    private String param2;
    private Uri mData;
    private View mView;
    private FullscreenActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FullscreenActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param1 = getArguments().getString(ARG_PARAM1);
            param2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_portrait, container, false);
        mView.findViewById(R.id.btnPlay).setTag(R.drawable.ic_baseline_play_arrow);
        mView.findViewById(R.id.videoView).setTag("first");
        mData = mActivity.getIntent().getData();
        if (mData == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                    .setTitle("Please enter URL")
                    .setCancelable(false);
            final EditText input = new EditText(mActivity);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        mData = Uri.parse(input.getText().toString());
                        if (!URLUtil.isValidUrl(mData.toString())) {
                            throw new Exception();
                        } else {
                            dialog.cancel();
                            mView.findViewById(R.id.btnPlay).performClick();
                        }
                    } catch (Exception e) {
                        Snackbar.make(mActivity.findViewById(android.R.id.content), mActivity.getResources().getString(R.string.errorurl), Snackbar.LENGTH_SHORT).show();
                        mActivity.recreate();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }
            ).show();
        }
        ;
        // Кнопка проигрывания
        mView.findViewById(R.id.btnPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mView.findViewById(R.id.btnPlay).getTag().equals(R.drawable.ic_baseline_play_arrow)) {
                    if (mView.findViewById(R.id.videoView).getTag().equals("first")) {
                        clickBtnPlay(mView, mData); // Если видео только открыли
                    } else if (mView.findViewById(R.id.videoView).getTag().equals("pause")) {
                        clickBtnContinue(mView); // После паузы
                    }
                } else if (mView.findViewById(R.id.btnPlay).getTag().equals(R.drawable.ic_baseline_pause)) {
                    clickBtnPause(mView);
                } else if (mView.findViewById(R.id.btnPlay).getTag().equals(R.drawable.ic_baseline_repeat)) {
                    clickBtnRepeat(mView);
                }
            }
        });
        mView.findViewById(R.id.videoView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideNavBtns(mView);
            }
        });

        mView.findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtnStop(mView);
            }
        });

        mView.findViewById(R.id.btnForward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtnForward(mView);
            }
        });

        mView.findViewById(R.id.btnBackward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtnBackward(mView);
            }
        });

        mView.findViewById(R.id.btnFaster).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtnFaster(mView);
            }
        });

        mView.findViewById(R.id.btnSlower).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtnSlower(mView);
            }
        });

        mView.findViewById(R.id.btnVolumeOnOff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtnMute(mView);
            }
        });
        return mView;
    }

    private void clickBtnMute(View view) {
        AudioManager am = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        int volumeLevel = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (volumeLevel != 0) {
            am.setStreamMute(AudioManager.STREAM_MUSIC, true);
            ((ImageView)view.findViewById(R.id.btnVolumeOnOff)).setImageResource(R.drawable.ic_baseline_volume_off);
            view.findViewById(R.id.btnVolumeOnOff).setTag(R.drawable.ic_baseline_volume_off);
        } else {
            am.setStreamMute(AudioManager.STREAM_MUSIC, false);
            ((ImageView)view.findViewById(R.id.btnVolumeOnOff)).setImageResource(R.drawable.ic_baseline_volume_up);
            view.findViewById(R.id.btnVolumeOnOff).setTag(R.drawable.ic_baseline_volume_up);
        }
        new Timer().schedule(new MyTimerTask(), 5000);
    }

    private void clickBtnForward(View view) {
        ((VideoView)view.findViewById(R.id.videoView)).seekTo(((VideoView)view.findViewById(R.id.videoView)).getDuration());
        ((VideoView)view.findViewById(R.id.videoView)).pause();
        ((ImageView)view.findViewById(R.id.btnPlay)).setImageResource(R.drawable.ic_baseline_repeat);
        view.findViewById(R.id.btnPlay).setTag(R.drawable.ic_baseline_repeat);
        new Timer().schedule(new MyTimerTask(), 5000);
    }

    private void clickBtnBackward(View view) {
        ((VideoView)view.findViewById(R.id.videoView)).pause();
        ((VideoView)view.findViewById(R.id.videoView)).seekTo(0);
        ((VideoView)view.findViewById(R.id.videoView)).setTag("pause");
        ((ImageView)view.findViewById(R.id.btnPlay)).setImageResource(R.drawable.ic_baseline_play_arrow);
        view.findViewById(R.id.btnPlay).setTag(R.drawable.ic_baseline_play_arrow);
        new Timer().schedule(new MyTimerTask(), 5000);
    }

    private void clickBtnFaster(View view) {
        ((VideoView)view.findViewById(R.id.videoView)).seekTo(((VideoView)view.findViewById(R.id.videoView)).getCurrentPosition() + 10000);
        new Timer().schedule(new MyTimerTask(), 5000);
    }

    private void clickBtnSlower(View view) {
        ((VideoView)view.findViewById(R.id.videoView)).seekTo(((VideoView)view.findViewById(R.id.videoView)).getCurrentPosition() - 10000);
        new Timer().schedule(new MyTimerTask(), 5000);
    }

    private void clickBtnStop(View view) {
        ((VideoView)view.findViewById(R.id.videoView)).pause();
        ((VideoView)view.findViewById(R.id.videoView)).seekTo(0);
        ((VideoView)view.findViewById(R.id.videoView)).setTag("first");
        ((ImageView)view.findViewById(R.id.btnPlay)).setImageResource(R.drawable.ic_baseline_play_arrow);
        view.findViewById(R.id.btnPlay).setTag(R.drawable.ic_baseline_play_arrow);
        new Timer().schedule(new MyTimerTask(), 5000);
    }

    private void hideNavBtns(View view) { // Скрыть-показать панель
        if (view.findViewById(R.id.clNavBtns).getVisibility() == View.VISIBLE) {
            view.findViewById(R.id.clNavBtns).setVisibility(View.GONE);
            new Timer().schedule(new MyTimerTask(), 5000);
        } else if (view.findViewById(R.id.clNavBtns).getVisibility() == View.GONE) {
            view.findViewById(R.id.clNavBtns).setVisibility(View.VISIBLE);
            new Timer().schedule(new MyTimerTask(), 5000);
        }
    }

    private void clickBtnContinue(View view) { // Продолжить после паузы
        ((VideoView)view.findViewById(R.id.videoView)).start();
        ((ImageView)view.findViewById(R.id.btnPlay)).setImageResource(R.drawable.ic_baseline_pause);
        ((ImageView)view.findViewById(R.id.btnPlay)).setTag(R.drawable.ic_baseline_pause);
        view.findViewById(R.id.videoView).setTag("play");
        new Timer().schedule(new MyTimerTask(), 5000);
    }

    private void clickBtnPause(View view) { // Поставить паузу
        ((VideoView)view.findViewById(R.id.videoView)).pause();
        ((ImageView)view.findViewById(R.id.btnPlay)).setImageResource(R.drawable.ic_baseline_play_arrow);
        ((ImageView)view.findViewById(R.id.btnPlay)).setTag(R.drawable.ic_baseline_play_arrow);
        view.findViewById(R.id.videoView).setTag("pause");
        new Timer().schedule(new MyTimerTask(), 5000);
    }

    private void clickBtnRepeat(View view) { // Начать заново
        ((VideoView)view.findViewById(R.id.videoView)).resume();
        ((ImageView)view.findViewById(R.id.btnPlay)).setImageResource(R.drawable.ic_baseline_pause);
        ((ImageView)view.findViewById(R.id.btnPlay)).setTag(R.drawable.ic_baseline_pause);
        view.findViewById(R.id.videoView).setTag("play");
        new Timer().schedule(new MyTimerTask(), 5000);
    }

    private void clickBtnPlay(final View view, Uri uri) { // Запустить проигрывание
        // Показываем окно загрузки
        final ProgressDialog mDialog = new ProgressDialog(mActivity);

        try {
            mDialog.setMessage("Пожалуйста, подождите...");
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
            ((VideoView)view.findViewById(R.id.videoView)).setVideoURI(uri);
            ((VideoView)view.findViewById(R.id.videoView)).setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    ((ImageView)view.findViewById(R.id.btnPlay)).setImageResource(R.drawable.ic_baseline_repeat);
                    ((ImageView)view.findViewById(R.id.btnPlay)).setTag(R.drawable.ic_baseline_repeat);
                    if (view.findViewById(R.id.clNavBtns).getVisibility() == View.GONE) {
                        view.findViewById(R.id.clNavBtns).setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (Exception e) {
            Snackbar.make(view, mActivity.getResources().getString(R.string.error), Snackbar.LENGTH_LONG)
                    .show(); // В случае ошибки показать Snackbar с текстом
        }
        ((VideoView)view.findViewById(R.id.videoView)).requestFocus();
        // Проигрыватель готов к воспроизведению
        ((VideoView)view.findViewById(R.id.videoView)).setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mDialog.dismiss();
                mediaPlayer.setLooping(false);
                if (mediaPlayer.getVideoHeight() > mediaPlayer.getVideoWidth()) {
                    ((VideoView)view.findViewById(R.id.videoView)).setLayoutParams(new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.MATCH_PARENT
                    ));
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone((ConstraintLayout) view);
                    constraintSet.connect(((VideoView)view.findViewById(R.id.videoView)).getId(), ConstraintSet.LEFT, view.getId(), ConstraintSet.LEFT, 0);
                    constraintSet.connect(((VideoView)view.findViewById(R.id.videoView)).getId(), ConstraintSet.RIGHT, view.getId(), ConstraintSet.RIGHT, 0);
                    constraintSet.applyTo((ConstraintLayout) view);
                } else {
                    ((VideoView)view.findViewById(R.id.videoView)).setLayoutParams(new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.MATCH_PARENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT
                    ));
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone((ConstraintLayout) view);
                    constraintSet.connect(((VideoView)view.findViewById(R.id.videoView)).getId(), ConstraintSet.TOP, view.getId(), ConstraintSet.TOP, 0);
                    constraintSet.connect(((VideoView)view.findViewById(R.id.videoView)).getId(), ConstraintSet.BOTTOM, view.getId(), ConstraintSet.BOTTOM, 0);
                    constraintSet.applyTo((ConstraintLayout) view);
                }
                ((VideoView)view.findViewById(R.id.videoView)).start();
                ((ImageView)view.findViewById(R.id.btnPlay)).setImageResource(R.drawable.ic_baseline_pause);
                ((ImageView)view.findViewById(R.id.btnPlay)).setTag(R.drawable.ic_baseline_pause);
                ((VideoView)view.findViewById(R.id.videoView)).setTag("play");
                new Timer().schedule(new MyTimerTask(), 5000);
            }
        });
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mView.findViewById(R.id.clNavBtns).setVisibility(View.GONE);
                }
            });
        }
    }
}
