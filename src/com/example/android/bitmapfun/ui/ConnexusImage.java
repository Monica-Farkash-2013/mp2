package com.example.android.bitmapfun.ui;

import java.util.ArrayList;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

public class ConnexusImage   implements Parcelable {

    long id;
    long streamId;
    String comments;
    Date createDate;
    String bkUrl;

    public ConnexusImage(Parcel in) {
    	readFromParcel(in);
    }

    @SuppressWarnings("serial")
    public static class List extends ArrayList<ConnexusImage>{

    }
    
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		if(arg0 != null) {
			arg0.writeLong(id);
			arg0.writeLong(streamId);
			arg0.writeString(comments);	
			arg0.writeString(createDate.toString());
			arg0.writeString(bkUrl);
		}
	}

	 public static final Parcelable.Creator<ConnexusImage> CREATOR = new Parcelable.Creator<ConnexusImage>() {
	        public ConnexusImage createFromParcel(Parcel in) {
	            return new ConnexusImage(in);
	        }

	        public ConnexusImage[] newArray(int size) {
	            return new ConnexusImage[size];
	        }
	    };



	public void readFromParcel(Parcel in){
		if(in != null) {
			this.id = in.readLong();
		    this.streamId = in.readLong();
		    this.comments = in.readString();
		    DateFormat.format(in.readString(), this.createDate);
		    this.bkUrl = in.readString();
		}
	}
}