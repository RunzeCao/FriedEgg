package com.example.friedegg.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.friedegg.R;
import com.example.friedegg.callback.LoadFinishCallBack;
import com.example.friedegg.callback.LoadResultCallBack;
import com.example.friedegg.modul.Comment4FreshNews;
import com.example.friedegg.modul.Commentator;
import com.example.friedegg.net.Request4CommentList;
import com.example.friedegg.net.RequestManager;
import com.example.friedegg.utils.String2TimeUtil;
import com.example.friedegg.view.floorview.FloorView;
import com.example.friedegg.view.floorview.SubComments;
import com.example.friedegg.view.floorview.SubFloorFactory;
import com.example.friedegg.view.imageloader.ImageLoaderProxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by CRZ on 2016/5/30 16:00.
 * CommentAdapter
 * This Adapter is for Comment List, the comments for fresh news is special
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private ArrayList<Commentator> commentators;
    private ArrayList<Comment4FreshNews> commentators4FreshNews;

    private Activity mActivity;
    private String thread_key;
    private String thread_id;
    private LoadResultCallBack mLoadResultCallBack;
    private boolean isFromFreshNews;

    public CommentAdapter(Activity activity, String thread_key, boolean isFromFreshNews, LoadResultCallBack loadResultCallBack) {
        mActivity = activity;
        this.thread_key = thread_key;
        this.isFromFreshNews = isFromFreshNews;
        mLoadResultCallBack = loadResultCallBack;
        if (isFromFreshNews) {
            commentators4FreshNews = new ArrayList<>();
        } else {
            commentators = new ArrayList<>();
        }
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Commentator.TYPE_HOT:
            case Commentator.TYPE_NEW:
                return new CommentViewHolder(mActivity.getLayoutInflater().inflate(R.layout.item_comment_flag, parent, false));
            case Commentator.TYPE_NORMAL:
                return new CommentViewHolder(mActivity.getLayoutInflater().inflate(R.layout.item_comment, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Commentator commentator;
        if (isFromFreshNews) {
            commentator = commentators4FreshNews.get(position);
        } else {
            commentator = commentators.get(position);
        }

        switch (commentator.getType()) {
            case Commentator.TYPE_HOT:
                holder.tv_flag.setText("热门评论");
                break;
            case Commentator.TYPE_NEW:
                holder.tv_flag.setText("最新评论");
                break;
            case Commentator.TYPE_NORMAL:
                final Commentator comment = commentator;
                holder.tv_name.setText(commentator.getName());
                holder.tv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                if (isFromFreshNews) {

                } else {
                    String timeString = commentator.getCreated_at().replace("T", " ");
                    timeString = timeString.substring(0, timeString.indexOf("+"));
                    holder.tv_time.setText(String2TimeUtil.dateString2GoodExperienceFormat(timeString));
                    holder.tv_content.setText(commentator.getMessage());
                    if (commentator.getAvatar_url()!= null) {
                        ImageLoaderProxy.displayHeadIcon(commentator.getAvatar_url().trim(), holder.img_header);
                    }else {
                        holder.img_header.setImageResource(R.drawable.ic_loading_small);
                    }
                }

                //盖楼
                if (commentator.getFloorNum() > 1) {
                    SubComments subComments;
                    if (isFromFreshNews) {
                        subComments = new SubComments(addFloors4FreshNews((Comment4FreshNews) commentator));
                    } else {
                        subComments = new SubComments(addFloors(commentator));
                    }
                    holder.floors_parent.setComments(subComments);
                    holder.floors_parent.setFactory(new SubFloorFactory());
                    holder.floors_parent.setBoundDrawer(mActivity.getResources().getDrawable(R.drawable.bg_comment));
                    holder.floors_parent.init();
                } else {
                    holder.floors_parent.setVisibility(View.GONE);
                }
                break;
        }
    }


    @Override
    public int getItemCount() {
        if (isFromFreshNews) {
            return commentators4FreshNews.size();
        } else {
            return commentators.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isFromFreshNews) {
            return commentators4FreshNews.get(position).getType();
        } else {
            return commentators.get(position).getType();
        }
    }

    private List<Comment4FreshNews> addFloors4FreshNews(Comment4FreshNews commentator) {
        return commentator.getParentComments();
    }

    private List<Commentator> addFloors(Commentator commentator) {
        //只有一层
        if (commentator.getFloorNum() == 1) {
            return null;
        }
        List<String> parentIds = Arrays.asList(commentator.getParents());
        ArrayList<Commentator> commentators = new ArrayList<>();
        for (Commentator comm : this.commentators) {
            if (parentIds.contains(comm.getPost_id())) {
                commentators.add(comm);
            }
        }
        Collections.reverse(commentators);
        return commentators;
    }

    public String getThreadId() {
        return thread_id;
    }

    public void loadData() {
        RequestManager.addRequest(new Request4CommentList(Commentator.getUrlCommentList(thread_key), new Response.Listener<ArrayList<Commentator>>() {
            @Override
            public void onResponse(ArrayList<Commentator> response) {
                if (response.size() == 0) {
                    mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_NONE, null);
                } else {
                    commentators.clear();
                    ArrayList<Commentator> hotCommentator = new ArrayList<>();
                    ArrayList<Commentator> normalComment = new ArrayList<>();
                    //添加热门评论
                    for (Commentator commentator : response) {
                        if (commentator.getTag().equals(Commentator.TAG_HOT)) {
                            hotCommentator.add(commentator);
                        } else {
                            normalComment.add(commentator);
                        }
                    }

                    //添加热门评论标签
                    if (hotCommentator.size() != 0) {
                        Collections.sort(hotCommentator);
                        Commentator hotCommentFlag = new Commentator();
                        hotCommentFlag.setType(Commentator.TYPE_HOT);
                        hotCommentator.add(0, hotCommentFlag);
                        commentators.addAll(hotCommentator);
                    }

                    //添加最新评论及标签
                    if (normalComment.size() != 0) {
                        Commentator newCommentFlag = new Commentator();
                        newCommentFlag.setType(Commentator.TYPE_NEW);
                        commentators.add(newCommentFlag);
                        Collections.sort(normalComment);
                        commentators.addAll(normalComment);
                    }
                    notifyDataSetChanged();
                    mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET, volleyError.getMessage());
            }
        }, new LoadFinishCallBack() {
            @Override
            public void loadFinish(Object obj) {
                thread_id = (String) obj;
            }
        }), mActivity);
    }

    public void loadData4FreshNews() {
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_content;
        TextView tv_flag;
        TextView tv_time;
        ImageView img_header;
        FloorView floors_parent;

        public CommentViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_flag = (TextView) itemView.findViewById(R.id.tv_flag);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            img_header = (ImageView) itemView.findViewById(R.id.img_header);
            floors_parent = (FloorView) itemView.findViewById(R.id.floors_parent);
            setIsRecyclable(false);
        }
    }

}
