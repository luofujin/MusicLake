package com.cyl.musiclake.ui.music.local.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cyl.musiclake.R;
import com.cyl.musiclake.RxBus;
import com.cyl.musiclake.bean.Music;
import com.cyl.musiclake.bean.Playlist;
import com.cyl.musiclake.event.PlaylistEvent;
import com.cyl.musiclake.musicApi.MusicApiServiceImpl;
import com.cyl.musiclake.ui.my.user.UserStatus;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者：yonglong on 2016/9/14 15:56
 * 邮箱：643872807@qq.com
 * 版本：2.5
 */
@SuppressWarnings("ALL")
public class CreatePlaylistDialog extends DialogFragment {

    private static final String TAG = "CreatePlaylistDialog";
    private static final String TAG_MUSIC = "music";

    public static CreatePlaylistDialog newInstance() {
        return newInstance(null);
    }

    public static CreatePlaylistDialog newInstance(Music music) {
        CreatePlaylistDialog dialog = new CreatePlaylistDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(TAG_MUSIC, music);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Music music = getArguments().getParcelable(TAG_MUSIC);
        return new MaterialDialog.Builder(getActivity())
                .title("新建歌单")
                .positiveText("确定")
                .negativeText("取消")
                .inputRangeRes(2, 10, R.color.red)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("输入歌单名", "", false, (dialog, input) -> Log.e(TAG, input.toString()))
                .onPositive((dialog, which) -> {
                    String title = dialog.getInputEditText().getText().toString();
                    createPlaylist(title);
//                    long pid = PlaylistLoader.createPlaylist(getActivity(), title);
//                    if (pid != -1) {
//                        if (music != null) {
//                            PlaylistLoader.addToPlaylist(getActivity(), String.valueOf(pid), music.getId());
//                            RxBus.getInstance().post(new PlaylistInfo());
//                            ToastUtils.show(getActivity(), "添加成功");
//                        } else {
//                            ToastUtils.show(getActivity(), "新建歌单 " + title);
//                        }
//                    } else {
//                        ToastUtils.show(getActivity(), "创建失败" + title);
//                    }
                    Log.d(TAG, title);
                }).build();
    }

    private void createPlaylist(String name) {
        boolean mIsLogin = UserStatus.getstatus(getContext());
        if (mIsLogin) {
            MusicApiServiceImpl.createPlaylist(name).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Playlist>() {
                        @Override
                        public void onSubscribe(Disposable disposable) {

                        }

                        @Override
                        public void onNext(Playlist playlist) {
                            RxBus.getInstance().post(new PlaylistEvent());
                        }

                        @Override
                        public void onError(Throwable throwable) {
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }
}
