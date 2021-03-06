package com.cyl.musiclake.ui.music.local.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cyl.musiclake.R;
import com.cyl.musiclake.base.BaseFragment;
import com.cyl.musiclake.bean.Music;
import com.cyl.musiclake.bean.Playlist;
import com.cyl.musiclake.common.Extras;
import com.cyl.musiclake.musicApi.AddPlaylistUtils;
import com.cyl.musiclake.service.PlayManager;
import com.cyl.musiclake.ui.music.local.adapter.SongAdapter2;
import com.cyl.musiclake.ui.music.local.contract.PlaylistDetailContract;
import com.cyl.musiclake.ui.music.local.dialog.ShowDetailDialog;
import com.cyl.musiclake.ui.music.local.presenter.PlaylistDetailPresenter;
import com.cyl.musiclake.ui.zone.EditActivity;
import com.cyl.musiclake.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：yonglong on 2016/8/15 19:54
 * 邮箱：643872807@qq.com
 * 版本：2.5
 */
public class PlaylistDetailFragment extends BaseFragment implements PlaylistDetailContract.View {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.album_art)
    ImageView album_art;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @OnClick(R.id.fab)
    void onPlayAll() {
        PlayManager.setPlayList(musicList);
        PlayManager.play(0);
    }

    private SongAdapter2 mAdapter;
    private List<Music> musicList = new ArrayList<>();
    private Playlist mPlaylist;
    private PlaylistDetailPresenter mPresenter;

    public static PlaylistDetailFragment newInstance(Playlist playlist, boolean useTransition, String transitionName) {
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(Extras.PLAYLIST, playlist);
        args.putBoolean(Extras.TRANSITION, useTransition);
        if (useTransition) {
            args.putString(Extras.TRANSITIONNAME, transitionName);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_playlist_detail;
    }

    @Override
    public void initViews() {
        mPlaylist = (Playlist) getArguments().getSerializable(Extras.PLAYLIST);
        mToolbar.setTitle(mPlaylist != null ? mPlaylist.getName() : "");
        setHasOptionsMenu(true);
        if (getActivity() != null) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            appCompatActivity.setSupportActionBar(mToolbar);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (getArguments().getBoolean(Extras.TRANSITION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                album_art.setTransitionName(getArguments().getString("transition_name"));
            }
        }

        mPresenter = new PlaylistDetailPresenter(getContext());
        mPresenter.attachView(this);

        mAdapter = new SongAdapter2(musicList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerView);
    }

    @Override
    protected void loadData() {
        mPresenter.loadPlaylistSongs(mPlaylist.getId());
        mPresenter.loadPlaylistArt(mPlaylist.getId());
    }

    @Override
    protected void listener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() != R.id.iv_more) {
                PlayManager.setPlayList(musicList);
                PlayManager.play(position);
            }
        });
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.popup_song_play:
                        PlayManager.setPlayList(musicList);
                        PlayManager.play(position);
                        break;
                    case R.id.popup_song_detail:
                        ShowDetailDialog.newInstance((Music) adapter.getItem(position))
                                .show(getChildFragmentManager(), getTag());
                        break;
                    case R.id.popup_song_addto_queue:
                        AddPlaylistUtils.getPlaylist((AppCompatActivity) getActivity(), musicList.get(position));
                        break;
                    case R.id.popup_song_delete:
                        new MaterialDialog.Builder(getContext())
                                .title("提示")
                                .content("是否移除这首歌曲？")
                                .onPositive((dialog, which) -> {
                                    mPresenter.uncollectMusic(mPlaylist.getId(), position, musicList.get(position));
                                })
                                .positiveText("确定")
                                .negativeText("取消")
                                .show();
                        break;
                }
                return false;
            });
            popupMenu.inflate(R.menu.popup_playlist);
            popupMenu.show();
        });
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete_playlist:
                LogUtil.e("action_delete_playlist");
                new MaterialDialog.Builder(getActivity())
                        .title("提示")
                        .content("是否删除这个歌单？")
                        .onPositive((dialog, which) -> {
                            mPresenter.deletePlaylist(mPlaylist);
                        })
                        .positiveText("确定")
                        .negativeText("取消")
                        .show();
                break;
            case R.id.action_rename_playlist:
                LogUtil.e("action_rename_playlist");
                new MaterialDialog.Builder(getActivity())
                        .title("重命名歌单")
                        .positiveText("确定")
                        .negativeText("取消")
                        .inputRangeRes(2, 10, R.color.red)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("输入歌单名", mPlaylist.getName(), false, (dialog, input) -> Log.e("=====", input.toString()))
                        .onPositive((dialog, which) -> {
                            String title = dialog.getInputEditText().getText().toString();
                            mPresenter.renamePlaylist(mPlaylist, title);
                        })
                        .positiveText("确定")
                        .negativeText("取消")
                        .show();
                break;
            case R.id.action_share:
                Intent intent3 = new Intent(getActivity(), EditActivity.class);
                StringBuilder content = new StringBuilder();
                if (musicList.size() > 0) {
                    content = new StringBuilder("分享歌单\n");
                }
                for (int i = 0; i < musicList.size(); i++) {
                    content.append(musicList.get(i).getTitle()).append("---").append(musicList.get(i).getArtist());
                    content.append("\n");
                }
                intent3.putExtra("content", content.toString());
                startActivity(intent3);
//                break;
//            case R.id.action_add:
//                List<String> titles = new ArrayList<>();
//                List<Music> addMusicList = SongLoader.getSongsForDB(getContext());
//                for (Music music : addMusicList) {
//                    titles.add(music.getTitle() + "-" + music.getArtist());
//                }
//                new MaterialDialog.Builder(getActivity())
//                        .title("新增歌曲")
//                        .iconRes(R.drawable.ic_playlist_add)
//                        .content("快速添加歌曲，更加方便地添加所需要的歌曲到当前目录")
//                        .positiveText("确定")
//                        .items(titles)
//                        .itemsCallbackMultiChoice(null, (dialog, which, text) -> false)
//                        .onPositive((dialog, which) -> {
//                            dialog.dismiss();
//                            int sum = dialog.getSelectedIndices().length, num = 0;
//                            for (int i = 0; i < sum; i++) {
//                                int index = dialog.getSelectedIndices()[i];
//                                boolean success = PlaylistLoader.addToPlaylist(getContext(), mPlaylist.getId(), addMusicList.get(index).getId());
//                                if (success) {
//                                    num++;
//                                }
//                            }
//                            mPresenter.loadPlaylistSongs(mPlaylist.getId());
//                            mPresenter.loadPlaylistArt(mPlaylist.getId());
//                            ToastUtils.show(getContext(), num + "首添加成功，" + (sum - num) + "首已存在此歌单添加失败");
//                            RxBus.getInstance().post(new Playlist());
//                        }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onBackPress() {
        getActivity().onBackPressed();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_playlist_detail, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showPlaylistSongs(List<Music> songList) {
        musicList = songList;
        mAdapter.setNewData(musicList);
        if (musicList.size() == 0) {
            mAdapter.setEmptyView(R.layout.view_song_empty);
        }
    }

    @Override
    public void showPlaylistArt(Drawable playlistArt) {
        album_art.setImageDrawable(playlistArt);
    }

    @Override
    public void showPlaylistArt(Bitmap bitmap) {
        album_art.setImageBitmap(bitmap);
    }

    @Override
    public void removeMusic(int position) {
        musicList.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void success(int type) {
        if (type == 1) {
            onBackPress();
        }
    }
}
