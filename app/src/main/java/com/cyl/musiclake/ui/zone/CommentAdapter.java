package com.cyl.musiclake.ui.zone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyl.musiclake.R;
import com.cyl.musiclake.utils.FormatUtil;

import java.util.List;

/**
 * Created by Monkey on 2015/6/29.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentRecyclerViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

    }

    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public Context mContext;
    public List<Comment> comments;
    public LayoutInflater mLayoutInflater;

    public CommentAdapter(Context mContext, List<Comment> comments) {
        this.mContext = mContext;
        this.comments = comments;
        mLayoutInflater = LayoutInflater.from(mContext);

    }

    /**
     * 创建ViewHolder
     */
    @Override
    public CommentRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = mLayoutInflater.inflate(R.layout.item_comment, parent, false);
        CommentRecyclerViewHolder mViewHolder = new CommentRecyclerViewHolder(mView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(final CommentRecyclerViewHolder holder, final int position) {

        holder.user_comment.setText(comments.get(position).getUser().getName().toString() + "");
        holder.content_comment.setText(comments.get(position).getComment().toString() + "");
        String time = comments.get(position).getComment_time().toString() + "";
        holder.time_comment.setText(FormatUtil.getTimeDifference(time));

    }


    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentRecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView user_comment;
        private TextView content_comment;
        private TextView time_comment;

        private CommentRecyclerViewHolder(View mView) {
            super(mView);
            user_comment = (TextView) mView.findViewById(R.id.user_comment);
            content_comment = (TextView) mView.findViewById(R.id.content_comment);
            time_comment = (TextView) mView.findViewById(R.id.time_comment);
        }
    }
}
