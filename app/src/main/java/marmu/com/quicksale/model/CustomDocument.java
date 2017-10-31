package marmu.com.quicksale.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Created by azharuddin on 21/11/17.
 */

public class CustomDocument implements Parcelable{

    private DocumentSnapshot documentSnapshot;
    private int mData;

    public CustomDocument(Parcel documentSnapshot) {
        mData = documentSnapshot.readInt();
    }

    public static final Creator<CustomDocument> CREATOR = new Creator<CustomDocument>() {
        @Override
        public CustomDocument createFromParcel(Parcel in) {
            return new CustomDocument(in);
        }

        @Override
        public CustomDocument[] newArray(int size) {
            return new CustomDocument[size];
        }
    };

    public DocumentSnapshot getDocumentSnapshot() {
        return documentSnapshot;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mData);
    }
}
