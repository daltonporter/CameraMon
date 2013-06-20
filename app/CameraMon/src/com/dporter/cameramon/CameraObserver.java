package com.dporter.cameramon;

import java.util.Date;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.util.Log;

public class CameraObserver extends ContentObserver {
	private Context mContext;
	private boolean running;
	private final int maxTries=3;
	private Object theLock=new Object();
	public CameraObserver(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}

	public CameraObserver(Handler handler, Context ctx) {
        super(handler);
        mContext = ctx;
    }
	public void onChange(boolean selfChange) {
        try{
            Log.i("CMON","Notification on Camera observer");
            Thread t = new Thread(reporter);
            t.start();
        } catch(Exception e){
        	Log.e("CMON",e.toString());
        }
	}
	private Cursor getMedia(Uri uri, String filter){
		
		Cursor media=null;
		media=mContext.getContentResolver().query(uri, null,  null , null,  filter );
		if(media!=null){
			media.moveToFirst();
			long SizeLong = media.getLong(media.getColumnIndex("_size"));
			if(SizeLong==0){
				Log.i("CMON","zero found");
				media.close();
				media=null;
			}else{
				media.moveToPrevious();
			}
			
		}
		return media;
	}
	private Runnable reporter = new Runnable() {
		public void run() {
			
			Log.i("CMON","Thread "+Thread.currentThread().getId()+ " start");
			//try	{ Thread.sleep( 5000 ); } catch( Exception e)  {  }
			synchronized (theLock){ 
			
				Cursor currentMediaExternal = null;
				int tryCount=0;
				try {
					while(currentMediaExternal==null && tryCount < maxTries){
						currentMediaExternal=getMedia(Images.Media.EXTERNAL_CONTENT_URI,  " datetaken DESC ");
						if(currentMediaExternal==null){
							try{ Thread.currentThread().sleep(5000);} catch(Exception e){ }
							tryCount++;
						}
					}
					Log.d("CMON","No zeros found, trys="+(tryCount+1));
					//currentMediaExternal = mContext.getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI, null,  null , null,  " datetaken DESC " );
					int mExternal = currentMediaExternal.getCount();
					/*
					String cnames[]=currentMediaExternal.getColumnNames();
					for(int j=0;j<currentMediaExternal.getColumnCount();j++){
						Log.d("CMON",cnames[j]);
					}
					*/
					if (mExternal > 0)  {
		
						if (currentMediaExternal.moveToNext()) {
		
							for ( int i = 0; i < mExternal; ++i) {
		
								long SizeLong = currentMediaExternal.getLong(currentMediaExternal.getColumnIndex("_size"));
								String contentType = currentMediaExternal.getString(currentMediaExternal.getColumnIndex("mime_type"));
								long dateTakenLong = currentMediaExternal.getLong(currentMediaExternal.getColumnIndex("datetaken"));
								Date datetaken = new Date (dateTakenLong);
								Log.i("CMON",SizeLong+","+contentType+","+datetaken+","+dateTakenLong+" Thread "+Thread.currentThread().getId());
								currentMediaExternal.moveToNext();
							}
						//}
							currentMediaExternal.close();
							currentMediaExternal=null;
						}
					}
				} catch ( Exception e ) { currentMediaExternal = null ;	}
				
				Log.i("CMON","Thread "+Thread.currentThread().getId() +" end");
			}
		}
	};
}
