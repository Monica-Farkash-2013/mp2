package com.example.android.bitmapfun.ui;

import java.util.ArrayList;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

public class ConnexusStream   implements Parcelable {

    long id;
    String name;
    String tags;
    Date createDate;
    String coverImageUrl;

    public ConnexusStream(Parcel in) {
    	readFromParcel(in);
    }

    @SuppressWarnings("serial")
    public static class List extends ArrayList<ConnexusStream>{

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
			arg0.writeString(name);	
			arg0.writeString(tags);	
			arg0.writeString(createDate.toString());
			arg0.writeString(coverImageUrl);
		}
	}

	 public static final Parcelable.Creator<ConnexusStream> CREATOR = new Parcelable.Creator<ConnexusStream>() {
	        public ConnexusStream createFromParcel(Parcel in) {
	            return new ConnexusStream(in);
	        }

	        public ConnexusStream[] newArray(int size) {
	            return new ConnexusStream[size];
	        }
	    };



	public void readFromParcel(Parcel in){
		if(in != null) {
			this.id = in.readLong();
		    this.name = in.readString();
		    this.tags = in.readString();
		    DateFormat.format(in.readString(), this.createDate);
		    this.coverImageUrl = in.readString();
		}
	}
}