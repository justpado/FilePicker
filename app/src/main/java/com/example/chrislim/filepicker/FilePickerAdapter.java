package com.example.chrislim.filepicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chris.lim on 2015. 9. 1..
 */
public class FilePickerAdapter extends ArrayAdapter<String> {

    String currentDir;
    ArrayList<String> mDataSource;
    Context mContext;


    public void setCurrentDir(String dir) {
        this.currentDir = dir;
    }

    public FilePickerAdapter(Context context, int resId, ArrayList<String> dataSource, String currentDir) {
        super(context, resId, dataSource);
        this.currentDir = currentDir;
        this.mDataSource = dataSource;
        this.mContext = context;
    }

    private  class ViewHolder {
        TextView fileName;
        TextView fileInfo;
        ImageView icon;
    }

    public String getFilePermissions(File file) {
        String per = "-";

        if (file.isDirectory())
            per += "d";
        if (file.canRead())
            per += "r";
        if (file.canWrite())
            per += "w";

        return per;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder mViewHolder;


        File file = new File(currentDir + "/" + mDataSource.get(position));
        Date lastModified = new Date(file.lastModified());
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);

            mViewHolder = new ViewHolder();
            mViewHolder.fileName = (TextView) convertView.findViewById(R.id.fileName);
            mViewHolder.icon = (ImageView) convertView.findViewById(R.id.fileImage);
            mViewHolder.fileInfo = (TextView) convertView.findViewById(R.id.bottom_view);

            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        String permission = getFilePermissions(file);

//file info
        if(file != null) {
            if (file.isDirectory()) {
                if (file.canRead() && file.list().length > 0)  //todo : check null check  ,  file.canRead() 를 빼고  데이터 넣을때 read 가능한것만 넣기
                    mViewHolder.icon.setImageResource(R.drawable.folder_full);
                else
                    mViewHolder.icon.setImageResource(R.drawable.folder);

                mViewHolder.fileInfo.setText(file.list().length + " items | " + permission);
            } else {
                String fname = file.toString();
                String fext = fname.substring(fname.lastIndexOf(".") + 1);

                mViewHolder.icon.setImageResource(getFileIcon(fext));


                mViewHolder.fileInfo.setText(getFileSize(file.length()) + " | " + dFormat.format(lastModified) + " | " + permission);

            }
        }

        mViewHolder.fileName.setText(file.getName());

        return convertView;
    }

    private String getFileSize(double size) {
        final int KB = 1024;
        final int MG = KB * KB;
        final int GB = MG * KB;

        String displaySize;
        if (size > GB)
            displaySize = String.format("%.2f Gb ", (double) size / GB);
        else if (size < GB && size > MG)
            displaySize = String.format("%.2f Mb ", (double) size / MG);
        else if (size < MG && size > KB)
            displaySize = String.format("%.2f Kb ", (double) size / KB);
        else
            displaySize = String.format("%.2f bytes ", (double) size);

        return displaySize;
    }

    private int getFileIcon(String fext) {
        int ImageResource= R.drawable.text;

        if (fext.equalsIgnoreCase("pdf")) {
            ImageResource = R.drawable.pdf;

        } else if (fext.equalsIgnoreCase("mp3") ||
                fext.equalsIgnoreCase("wav") ||
                fext.equalsIgnoreCase("ogg") ||
                fext.equalsIgnoreCase("wma") ||
                fext.equalsIgnoreCase("m4a") ||
                fext.equalsIgnoreCase("m4p")) {

            ImageResource = R.drawable.music;

        } else if (fext.equalsIgnoreCase("png") ||
                fext.equalsIgnoreCase("jpg") ||
                fext.equalsIgnoreCase("jpeg") ||
                fext.equalsIgnoreCase("gif") ||
                fext.equalsIgnoreCase("tiff")) {
            ImageResource = R.drawable.icon_image;


        } else if (fext.equalsIgnoreCase("zip") ||
                fext.equalsIgnoreCase("gzip") ||
                fext.equalsIgnoreCase("gz")) {

            ImageResource = R.drawable.zip;

        } else if (fext.equalsIgnoreCase("m4v") ||
                fext.equalsIgnoreCase("wmv") ||
                fext.equalsIgnoreCase("3gp") ||
                fext.equalsIgnoreCase("mp4")) {

            ImageResource = R.drawable.movies;

        } else if (fext.equalsIgnoreCase("doc") ||
                fext.equalsIgnoreCase("docx")) {

            ImageResource = R.drawable.word;

        } else if (fext.equalsIgnoreCase("xls") ||
                fext.equalsIgnoreCase("xlsx")) {

            ImageResource = R.drawable.excel;

        } else if (fext.equalsIgnoreCase("ppt") ||
                fext.equalsIgnoreCase("pptx")) {

            ImageResource = R.drawable.ppt;

        } else if (fext.equalsIgnoreCase("html")) {
            ImageResource = R.drawable.html32;

        } else if (fext.equalsIgnoreCase("xml")) {
            ImageResource = R.drawable.xml32;

        } else if (fext.equalsIgnoreCase("conf")) {
            ImageResource = R.drawable.config32;

        } else if (fext.equalsIgnoreCase("apk")) {
            ImageResource = R.drawable.appicon;

        } else if (fext.equalsIgnoreCase("jar")) {
            ImageResource = R.drawable.jar32;

        } else {
            ImageResource = R.drawable.text;
        }

        return ImageResource;
    }
}
