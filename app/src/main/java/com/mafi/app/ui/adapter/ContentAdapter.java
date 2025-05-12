package com.mafi.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mafi.app.R;
import com.mafi.app.data.model.Content;
import com.mafi.app.data.model.ContentType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private Context context;
    private List<Content> contentList;
    private OnItemClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnItemClickListener {
        void onItemClick(Content content);
        void onItemLongClick(Content content, View view);
    }

    public ContentAdapter(Context context, List<Content> contentList) {
        this.context = context;
        this.contentList = contentList;
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_content, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        Content content = contentList.get(position);

        holder.titleTextView.setText(content.getTitle());
        holder.descriptionTextView.setText(content.getDescription());
        holder.dateTextView.setText(dateFormat.format(new Date(content.getCreatedAt())));

        // İçerik tipine göre ikon ayarla
        int iconResId = 0;
        switch (content.getContentTypeId()) {
            case ContentType.TYPE_TEXT:
                iconResId = R.drawable.ic_text;
                break;
            case ContentType.TYPE_IMAGE:
                iconResId = R.drawable.ic_image;
                break;
            case ContentType.TYPE_AUDIO:
                iconResId = R.drawable.ic_audio;
                break;
            case ContentType.TYPE_VIDEO:
                iconResId = R.drawable.ic_video;
                break;
            default:
                iconResId = R.drawable.ic_content;
                break;
        }

        holder.typeIconImageView.setImageResource(iconResId);
    }

    @Override
    public int getItemCount() {
        return contentList == null ? 0 : contentList.size();
    }

    public void updateContentList(List<Content> newContentList) {
        this.contentList = newContentList;
        notifyDataSetChanged();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView descriptionTextView;
        TextView dateTextView;
        ImageView typeIconImageView;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.text_content_title);
            descriptionTextView = itemView.findViewById(R.id.text_content_description);
            dateTextView = itemView.findViewById(R.id.text_content_date);
            typeIconImageView = itemView.findViewById(R.id.image_content_type);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(contentList.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(contentList.get(position), v);
                    return true;
                }
                return false;
            });
        }
    }
}