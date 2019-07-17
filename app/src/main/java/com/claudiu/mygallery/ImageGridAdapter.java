package com.claudiu.mygallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class ImageGridAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<ImageInfo> mImagesDataList;

    public ImageGridAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public ImageGridAdapter(Context context, List<ImageInfo> paramImages) {
        mInflater = LayoutInflater.from(context);
        this.mImagesDataList = paramImages;
    }

    public List<ImageInfo> getData() {
        return mImagesDataList;
    }

    public void setData(List<ImageInfo> data) {
        if (mImagesDataList == data) {
            return;
        }
        mImagesDataList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mImagesDataList == null) {
            return 0;
        } else {
            return mImagesDataList.size();
        }
    }

    @Override
    public Object getItem(int i) {
        if (mImagesDataList == null) {
            return null;
        } else {
            return mImagesDataList.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private View newView(final int position, final ViewGroup parent, final int viewType) {
        View view = mInflater.inflate(R.layout.cell_image_grid, null);
        ImageGridAdapter.ViewHolder holder = new ImageGridAdapter.ViewHolder();
        holder.image =  view.findViewById(R.id.ivImageCell);
        holder.textView =  view.findViewById(R.id.tvImageTitle);
        view.setTag(holder);
        return view;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int viewType = getItemViewType(i);
        View v;
        if (view == null) {
            v = newView(i, viewGroup, viewType);
        } else {
            v = view;
        }
        bindView(v, i, viewType);
        return v;
    }

    private void bindView(final View convertView, final int position, final int viewType) {
        ImageGridAdapter.ViewHolder holder = (ImageGridAdapter.ViewHolder) convertView.getTag();
        if (holder == null) {
            return;
        }
        try {
            ImageInfo info = mImagesDataList.get(position);
            Bitmap b = null;
            File f = new File(info.getPathString());
            b = BitmapFactory.decodeStream(new FileInputStream(f));

            holder.image.setImageBitmap(b);
            holder.textView.setText(info.getFileName());

        } catch (Exception ex) {

        }

    }

    private class ViewHolder {
        ImageView image;
        TextView textView;
    }
}
