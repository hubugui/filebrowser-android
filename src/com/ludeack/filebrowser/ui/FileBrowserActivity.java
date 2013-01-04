package com.ludeack.filebrowser.ui;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beem.project.beem.R;

public class FileBrowserActivity extends Activity implements OnClickListener {

    private static final String TAG = FileBrowserActivity.class.getSimpleName();

    /* browser, select_dir, select_file */
    private String mStartMode;
    private String mRootDir;

    private ListView mFileListView;
    private TextView mDirectoryCurrentTextView;
	private FileListAdapter mFileListAdapter = new FileListAdapter();
	private List<File> mFileMessages = new ArrayList<File>();

    public FileBrowserActivity() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	Log.d(TAG, "oncreate");
    	setContentView(R.layout.filebrowser);
    	setTitle(String.format("Pim - %s", getString(R.string.contact_list_menu_files)));

    	mStartMode = getIntent().getStringExtra("start_mode");
    	if (mStartMode == null || mStartMode.trim().length() == 0)
    		mStartMode = "browser";

    	mRootDir = getIntent().getStringExtra("root_dir");
    	if (mRootDir == null || mRootDir.trim().length() == 0)
    		mRootDir = "/sdcard/pim/downloads/";

    	mDirectoryCurrentTextView = (TextView) findViewById(R.id.directory_current);
    	mFileListView = (ListView) findViewById(R.id.filelist);
    	mFileListView.setAdapter(mFileListAdapter);

    	loadFiles();
    }
    
    public void onConfigurationChanged(Configuration newConfig) {    
        super.onConfigurationChanged(newConfig);
    }

    private class DirComparer implements Comparator {
    	public int compare(Object o1, Object o2) {
    		File f1 = (File) o1;
    		File f2 = (File) o2;
    		return f1.getName().compareTo(f2.getName());
    	}
    }

    private class FileComparer implements Comparator {
    	public int compare(Object o1, Object o2) {
    		File f1 = (File) o1;
    		File f2 = (File) o2;
    		
    		if (f1.lastModified() < f2.lastModified())
    			return 1;
    		else if (f1.lastModified() == f2.lastModified())
    			return 0;
    		return -1;
    	}
    }

    private void loadFiles() {
    	try {
	    	File fd = new File(mRootDir);
	    	File[] childs = fd.listFiles();
	    	
	    	List<File> dirs = new ArrayList<File>();
	    	List<File> files = new ArrayList<File>();
	    	
	    	for (File child:childs) {
	    		if (child.isDirectory())
	    			dirs.add(child);
	    		else
	    			files.add(child);
	    	}

	    	Collections.sort(dirs, new DirComparer());
	    	Collections.sort(files, new FileComparer());

	    	mFileMessages.clear();
	    	mFileMessages.addAll(dirs);
	    	mFileMessages.addAll(files);

	    	mDirectoryCurrentTextView.setText(mRootDir);
	    	mFileListAdapter.notifyDataSetChanged();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
    	super.onResume();
	}

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
    	super.onPause();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }

    public void onClick(View v) {
    	if (v.getId() == R.id.directory_up) {
    		String parentDir = new File(mRootDir).getParent();
    		if (parentDir != null && parentDir.trim().length() > 0) {
    			mRootDir = parentDir;
    			loadFiles();
    		}
    	}
    }
    
    /**
     * {@inheritDoc}.
     */
    private class FileListAdapter extends BaseAdapter {	
		/**
		 * Constructor.
		 */
		public FileListAdapter() {
		}
	
		@Override
		public int getCount() {
		    return mFileMessages.size();
		}
	
		@Override
		public Object getItem(int position) {
		    return mFileMessages.get(position);
		}
	
		@Override
		public long getItemId(int position) {
		    return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
		    View sv;
		    if (convertView == null) {
		    	LayoutInflater inflater = FileBrowserActivity.this.getLayoutInflater();
		    	sv = inflater.inflate(R.layout.filebrowser_row, null);
		    } else {
		    	sv = convertView;
		    }
		    File fd = mFileMessages.get(position);

		    TextView text = (TextView) sv.findViewById(R.id.text);
		    text.setText(fd.getName());

		    sv.setTag(fd);
		    sv.setOnClickListener(new OnClickListener() {
		    	public void onClick(View v) {
    	    		File fd = (File) v.getTag();
    	    		if (fd.isDirectory()) {
    	    			mRootDir = fd.getAbsolutePath();
    	    			loadFiles();
    	    		} else {
    	    			Intent intent = FileOpenIntent.openFile(fd.getAbsolutePath());
    	    			if (intent != null)
    	    				startActivity(intent);
    	    		}
		    	}
		    });

		    TextView info = (TextView) sv.findViewById(R.id.info);
		    ImageView icon = (ImageView) sv.findViewById(R.id.icon);
		    if (fd.isFile()) {		    	
		    	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
		    	Date date = new Date(fd.lastModified());
		    	String dateString = df.format(date);

			    info.setText(String.format("%.1fKB, %s", new Long(fd.length()).floatValue() / 1024, dateString));
			    icon.setBackgroundResource(R.drawable.file_icon);
		    } else {
		    	info.setText("");
		    	icon.setBackgroundResource(R.drawable.directory_icon);
		    }

		    return sv;
		}
    }
}
