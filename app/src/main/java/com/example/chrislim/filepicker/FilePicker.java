package com.example.chrislim.filepicker;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * Created by chris.lim on 2015. 8. 31..
 */
public class FilePicker extends ListActivity {

    private static final int SORT_NONE = 	0;
    private static final int SORT_DATE = 	1;
    private static final int SORT_TYPE = 	2;
    private static final int SORT_SIZE = 	3;

    int mSortType ;
    Context mContext;
    TextView title;


    private boolean mShowHiddenFiles = false;

    private Stack<String> mPathStack = new Stack<String>();
    ArrayList<String> mDataSource;
    FilePickerAdapter fAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.file_picker);
        mSortType = SORT_DATE;
        mContext = this;

        title = (TextView) findViewById(R.id.title);

        mDataSource = new ArrayList<String>(setHomeDir(Environment.getExternalStorageDirectory().getPath()));

        fAdapter = new FilePickerAdapter(this, R.layout.list_item, mDataSource, getCurrentDir());
        setListAdapter(fAdapter);

        title.setText(getCurrentDir());
    }

    public ArrayList<String> setHomeDir(String homeDir) {

        mPathStack.clear();
        mPathStack.push("/");
        mPathStack.push(homeDir);

        return getFileList();
    }

    public ArrayList<String> getNextDir(String path, boolean isFullPath) {
        int size = mPathStack.size();

        if(!path.equals(mPathStack.peek()) && !isFullPath) {
            if(size == 1) {
                mPathStack.push("/" + path);
                Log.d("getNextDir", "[1] push: /" + path);
            } else {
                mPathStack.push(mPathStack.peek() + "/" + path);
                Log.d("getNextDir", "push: /" + path);
            }

        } else if(!path.equals(mPathStack.peek()) && isFullPath) {
            mPathStack.push(path);
        }

        return getFileList();
    }

    private final Comparator size = new Comparator<String>() {
        @Override
        public int compare(String arg0, String arg1) {
            String dir = mPathStack.peek();
            Long first = new File(dir + "/" + arg0).length();
            Long second = new File(dir + "/" + arg1).length();

            return second.compareTo(first);
        }
    };

    private final Comparator type = new Comparator<String>() {
        @Override
        public int compare(String arg0, String arg1) {
            String ext = null;
            String ext2 = null;
            int ret;

            try {
                ext = arg0.substring(arg0.lastIndexOf(".") + 1, arg0.length()).toLowerCase();
                ext2 = arg1.substring(arg1.lastIndexOf(".") + 1, arg1.length()).toLowerCase();

            } catch (IndexOutOfBoundsException e) {
                return 0;
            }
            ret = ext.compareTo(ext2);

            if (ret == 0)
                return arg0.toLowerCase().compareTo(arg1.toLowerCase());

            return ret;
        }
    };

    private final Comparator date = new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            String dir = mPathStack.peek();
            Long lValue = new File(dir + "/" + lhs).lastModified();
            Long rValue = new File(dir + "/" + rhs).lastModified();
            return rValue.compareTo(lValue);
        }
    };


    private ArrayList<String> getFileList() {
        ArrayList<String> fArrayList = new ArrayList<String>();

        File file = new File(mPathStack.peek());
        if(file.canRead()) {
            Log.d("getFileList", "canRead " + file.toString());

        } else {
            Log.d("getFileList", "Fail Read " + file.toString());
        }

        if(file.exists() ) {
            String[] list = file.list();

            for (String fileName : list) {
                if(fileName.charAt(0) != '.') {  //hidden file
                    fArrayList.add(fileName);
                    Log.d("File List", "add list : " + fileName);
                } else {
                    Log.d("File List", "skip files: " +fileName);
                }

            }

			/* sort the arraylist that was made from above for loop */
            switch(mSortType) {
                case SORT_NONE:
                    //no sorting needed
                    break;

                case SORT_SIZE:
                    int index = 0;
                    Object[] size_ar = fArrayList.toArray();
                    String dir = mPathStack.peek();

                    Arrays.sort(size_ar, size);

                    fArrayList.clear();
                    for (Object a : size_ar) {
                        if(new File(dir + "/" + (String)a).isDirectory()) {
                            fArrayList.add(index++, (String) a);
                        } else {
                            fArrayList.add((String) a);

                        }
                    }

                    break;

                case SORT_TYPE:
                    int dirindex = 0;
                    Object[] type_ar = fArrayList.toArray();
                    String current = mPathStack.peek();

                    Arrays.sort(type_ar, type);
                    fArrayList.clear();

                    for (Object a : type_ar) {
                        if(new File(current + "/" + (String)a).isDirectory())
                            fArrayList.add(dirindex++, (String)a);
                        else
                            fArrayList.add((String)a);
                    }
                    break;

                case SORT_DATE:
                    int dateindex = 0;
                    Object[] date_ar = fArrayList.toArray();
                    //String dir = mPathStack.peek();

                    Arrays.sort(date_ar, date);

                    fArrayList.clear();
                    for (Object a : date_ar) {
                        if(new File(mPathStack.peek() + "/" + (String)a).isDirectory())
                            fArrayList.add(dateindex++, (String)a);
                        else
                            fArrayList.add((String)a);
                    }
                    break;
            }

        } else {
            fArrayList.add("Emtpy");
            Log.d("FileList: ", "problem at : " + file.toString());
        }

        return fArrayList;
    }

    public String getData(int position) {

        if(position > mDataSource.size() - 1 || position < 0)
            return null;

        return mDataSource.get(position);
    }

    /**
     * called to update the file contents as the user navigates there
     * phones file system.
     *
     * @param content	an ArrayList of the file/folders in the current directory.
     */
    public void updateDirectory(ArrayList<String> content) {
        if(!mDataSource.isEmpty())
            mDataSource.clear();

        for(String data : content)
            mDataSource.add(data);

        fAdapter.setCurrentDir(getCurrentDir());
        fAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView parent, View view, int position, long id) {
        final String item = getData(position);

        File file = new File(getCurrentDir() + "/" + item);


        if (file.isDirectory()) {
            if (file.canRead()) {
                // mHandler.stopThumbnailThread();
                updateDirectory(getNextDir(item, false));
                title.setText(getCurrentDir());


            } else {
                Toast.makeText(this, "Can't read folder due to permissions",
                        Toast.LENGTH_SHORT).show();
            }
        } else { // file 인경우
            Intent i = new Intent();
            i.setData(Uri.fromFile(file));
            setResult(RESULT_OK,i);
            finish();
            Log.d("finish"," picker");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public String getCurrentDir() {
        return mPathStack.peek();
    }



    @Override
    public void onBackPressed() {
        Log.d("onBackPressed", "currentDir: " + getCurrentDir());

        if(getCurrentDir().equalsIgnoreCase("/storage/emulated/0")) {
            super.onBackPressed();
        } else {
            updateDirectory(getPreviousDir());
            if(title != null)
                title.setText(getCurrentDir());
        }

    }

    public ArrayList<String> getPreviousDir() {
        int size = mPathStack.size();

        if (size >= 2)
            mPathStack.pop();

        else if(size == 0)
            mPathStack.push("/");

        return getFileList();
    }
}
