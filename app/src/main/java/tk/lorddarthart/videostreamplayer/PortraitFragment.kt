@file:Suppress("DEPRECATION")

package tk.lorddarthart.videostreamplayer


import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_portrait.view.*
import java.lang.Exception
import android.support.constraint.ConstraintSet
import java.util.*
import android.app.AlertDialog
import android.content.Context
import android.media.AudioManager
import android.text.InputType
import android.webkit.URLUtil
import android.widget.EditText
import android.widget.Toast


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PortraitFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
@Suppress("DEPRECATED_IDENTITY_EQUALS")
class PortraitFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var data: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_portrait, container, false)
        view.btnPlay.tag = R.drawable.ic_baseline_play_arrow
        view.videoView.tag = "first"
        data = activity?.intent?.data
        if (data == null) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Please enter URL")
            builder.setCancelable(false)
            val input = EditText(activity)
            builder.setView(input)

            builder.setPositiveButton(
                "OK"
            ) { dialog, _ ->
                try {
                    data = Uri.parse(input.text.toString())
                    if (!URLUtil.isValidUrl(data.toString())) {
                        throw Exception()
                    } else {
                        dialog.cancel()
                        view.btnPlay.performClick()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, resources.getString(R.string.errorurl), Toast.LENGTH_LONG).show()
                    activity!!.recreate()
                }
            }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

            builder.show()
        }
        // Кнопка проигрывания
        view.btnPlay.setOnClickListener {
            when (view.btnPlay.tag) {
                R.drawable.ic_baseline_play_arrow -> {
                    when (view.videoView.tag) {
                        "first" -> clickBtnPlay(view, data!!) // Если видео только открыли
                        "pause" -> clickBtnContinue(view) // После паузы
                    }
                }
                R.drawable.ic_baseline_pause -> {
                    clickBtnPause(view)
                }
                R.drawable.ic_baseline_repeat -> {
                    clickBtnRepeat(view)
                }
            }
        }
        view.videoView.setOnClickListener {
            hideNavBtns(view)
        }
        view.btnStop.setOnClickListener {
            clickBtnStop(view)
        }
        view.btnForward.setOnClickListener {
            clickBtnForward(view)
        }
        view.btnBackward.setOnClickListener {
            clickBtnBackward(view)
        }
        view.btnFaster.setOnClickListener {
            clickBtnFaster(view)
        }
        view.btnSlower.setOnClickListener {
            clickBtnSlower(view)
        }
        view.btnVolumeOnOff.setOnClickListener {
            clickBtnMute(view)
        }
        return view
    }

    private fun clickBtnMute(view: View) {
        val am = this.context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val volumeLevel = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (volumeLevel != 0) {
            am.setStreamMute(AudioManager.STREAM_MUSIC, true)
            view.btnVolumeOnOff.setImageResource(R.drawable.ic_baseline_volume_off)
            view.btnVolumeOnOff.tag = R.drawable.ic_baseline_volume_off
        } else {
            am.setStreamMute(AudioManager.STREAM_MUSIC, false)
            view.btnVolumeOnOff.setImageResource(R.drawable.ic_baseline_volume_up)
            view.btnVolumeOnOff.tag = R.drawable.ic_baseline_volume_up
        }
        Timer().schedule(MyTimerTask(), 5000)
    }

    private fun clickBtnForward(view: View) {
        view.videoView.seekTo(view.videoView.duration)
        view.videoView.pause()
        view.btnPlay.setImageResource(R.drawable.ic_baseline_repeat)
        view.btnPlay.tag = R.drawable.ic_baseline_repeat
        Timer().schedule(MyTimerTask(), 5000)
    }

    private fun clickBtnBackward(view: View) {
        view.videoView.pause()
        view.videoView.seekTo(0)
        view.videoView.tag = "pause"
        view.btnPlay.setImageResource(R.drawable.ic_baseline_play_arrow)
        view.btnPlay.tag = R.drawable.ic_baseline_play_arrow
        Timer().schedule(MyTimerTask(), 5000)
    }

    private fun clickBtnFaster(view: View) {
        view.videoView.seekTo(view.videoView.currentPosition + 10000)
        Timer().schedule(MyTimerTask(), 5000)
    }

    private fun clickBtnSlower(view: View) {
        view.videoView.seekTo(view.videoView.currentPosition - 10000)
        Timer().schedule(MyTimerTask(), 5000)
    }

    private fun clickBtnStop(view: View) {
        view.videoView.pause()
        view.videoView.seekTo(0)
        view.videoView.tag = "first"
        view.btnPlay.setImageResource(R.drawable.ic_baseline_play_arrow)
        view.btnPlay.tag = R.drawable.ic_baseline_play_arrow
        Timer().schedule(MyTimerTask(), 5000)
    }

    private fun hideNavBtns(view: View) { // Скрыть-показать панель
        if (view.clNavBtns.visibility == View.VISIBLE) {
            view.clNavBtns.visibility = View.GONE
            Timer().schedule(MyTimerTask(), 5000)
        } else if (view.clNavBtns.visibility == View.GONE) {
            view.clNavBtns.visibility = View.VISIBLE
            Timer().schedule(MyTimerTask(), 5000)
        }
    }

    private fun clickBtnContinue(view: View) { // Продолжить после паузы
        view.videoView.start()
        view.btnPlay.setImageResource(R.drawable.ic_baseline_pause)
        view.btnPlay.tag = R.drawable.ic_baseline_pause
        view.videoView.tag = "play"
        Timer().schedule(MyTimerTask(), 5000)
    }

    private fun clickBtnPause(view: View) { // Поставить паузу
        view.videoView.pause()
        view.btnPlay.setImageResource(R.drawable.ic_baseline_play_arrow)
        view.btnPlay.tag = R.drawable.ic_baseline_play_arrow
        view.videoView.tag = "pause"
        Timer().schedule(MyTimerTask(), 5000)
    }

    private fun clickBtnRepeat(view: View) { // Начать заново
        view.videoView.resume()
        view.btnPlay.setImageResource(R.drawable.ic_baseline_pause)
        view.btnPlay.tag = R.drawable.ic_baseline_pause
        view.videoView.tag = "play"
        Timer().schedule(MyTimerTask(), 5000)
    }

    private fun clickBtnPlay(view: View, uri: Uri) { // Запустить проигрывание
        // Показываем окно загрузки
        val mDialog = ProgressDialog(activity)

        try {
            mDialog.setMessage("Пожалуйста, подождите...")
            mDialog.setCanceledOnTouchOutside(false)
            mDialog.show()
            view.videoView.setVideoURI(uri)
            view.videoView.setOnCompletionListener {
                view.btnPlay.setImageResource(R.drawable.ic_baseline_repeat)
                view.btnPlay.tag = R.drawable.ic_baseline_repeat
                if (view.clNavBtns.visibility == View.GONE) {
                    view.clNavBtns.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            Snackbar.make(view, resources.getString(R.string.error), Snackbar.LENGTH_LONG)
                .show() // В случае ошибки показать Snackbar с текстом
        }
        view.videoView.requestFocus()
        // Проигрыватель готов к воспроизведению
        view.videoView.setOnPreparedListener { mediaPlayer ->
            mDialog.dismiss()
            mediaPlayer.isLooping = false
            if (mediaPlayer.videoHeight > mediaPlayer.videoWidth) {
                view.videoView.layoutParams =
                    ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT
                    )
                val constraintSet = ConstraintSet()
                constraintSet.clone(view as ConstraintLayout)
                constraintSet.connect(view.videoView.id, ConstraintSet.LEFT, view.id, ConstraintSet.LEFT, 0)
                constraintSet.connect(view.videoView.id, ConstraintSet.RIGHT, view.id, ConstraintSet.RIGHT, 0)
                constraintSet.applyTo(view)
            } else {
                view.videoView.layoutParams =
                    ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                    )
                val constraintSet = ConstraintSet()
                constraintSet.clone(view as ConstraintLayout)
                constraintSet.connect(view.videoView.id, ConstraintSet.TOP, view.id, ConstraintSet.TOP, 0)
                constraintSet.connect(view.videoView.id, ConstraintSet.BOTTOM, view.id, ConstraintSet.BOTTOM, 0)
                constraintSet.applyTo(view)
            }
            view.videoView.start()
            view.btnPlay.setImageResource(R.drawable.ic_baseline_pause)
            view.btnPlay.tag = R.drawable.ic_baseline_pause
            view.videoView.tag = "play"
            Timer().schedule(MyTimerTask(), 5000)
        }
    }

    internal inner class MyTimerTask : TimerTask() {

        override fun run() {
            activity!!.runOnUiThread { view!!.clNavBtns.visibility = View.GONE }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PortraitFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PortraitFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
