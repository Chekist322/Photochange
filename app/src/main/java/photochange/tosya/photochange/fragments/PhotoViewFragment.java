package photochange.tosya.photochange.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import photochange.tosya.photochange.R;

public class PhotoViewFragment extends Fragment{

    int mPageNumber;
    Bitmap mBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_view, container, false);
        ImageView imageView = view.findViewById(R.id.photo_view);
        imageView.setImageBitmap(mBitmap);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt("NUMBER");
        mBitmap = getArguments().getParcelable("PHOTO");
    }

    public static PhotoViewFragment newInstance(int photoNumber, Bitmap bitmap) {
        PhotoViewFragment fragment = new PhotoViewFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("NUMBER", photoNumber);
        arguments.putParcelable("PHOTO", bitmap);
        fragment.setArguments(arguments);
        return fragment;
    }
}
