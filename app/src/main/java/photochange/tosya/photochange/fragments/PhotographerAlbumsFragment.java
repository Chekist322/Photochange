package photochange.tosya.photochange.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.GetThumbnailBuilder;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.util.ArrayList;
import java.util.List;

import photochange.tosya.photochange.R;
import photochange.tosya.photochange.adapter.DropBoxListRecyclerViewAdapter;
import photochange.tosya.photochange.content.DropBoxListContent;

import static photochange.tosya.photochange.utils.Constant.ACCESS_TOKEN;

public class PhotographerAlbumsFragment extends DropBoxListFragment {

    private static final String TAG = PhotographerAlbumsFragment.class.getName();
    final List<DropBoxListContent.DropBoxItem> items = new ArrayList<>();

    Handler mUiHandler = new Handler(Looper.getMainLooper());
    String mPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mPath = getArguments().getString("PATH");

            TextView mPageLabel = getActivity().findViewById(R.id.page_label);
            if (mPageLabel != null) {
                String[] splited = mPath.split("/");
                mPageLabel.setText("Albums (" + splited[2] + ")");
            }
            final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Loading", "Please wait...");
            Thread dropBoxThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    DbxRequestConfig config = new DbxRequestConfig("Photographalbums");
                    DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
                    try {
                        ListFolderResult result = client.files().listFolder(mPath);
                        if (items.isEmpty())
                        while (true) {
                            for (Metadata metadata : result.getEntries()) {
                                if (metadata.getName().contains("avatar"))
                                    break;
                                DbxDownloader<FileMetadata> loadedImage = client.files().download(metadata.getPathDisplay() + "/cover.jpg");
                                Bitmap avatar = BitmapFactory.decodeStream(loadedImage.getInputStream());
                                Bitmap dstBmp;
                                if (avatar.getWidth() >= avatar.getHeight()) {

                                    dstBmp = Bitmap.createBitmap(
                                            avatar,
                                            avatar.getWidth() / 2 - avatar.getHeight() / 2,
                                            0,
                                            avatar.getHeight(),
                                            avatar.getHeight()
                                    );

                                } else {

                                    dstBmp = Bitmap.createBitmap(
                                            avatar,
                                            0,
                                            avatar.getHeight() / 2 - avatar.getWidth() / 2,
                                            avatar.getWidth(),
                                            avatar.getWidth()
                                    );
                                }

                                items.add(new DropBoxListContent.DropBoxItem(metadata.getName(), dstBmp, metadata.getPathDisplay()));
                            }
                            if (!result.getHasMore()) {
                                break;
                            }
                            result = client.files().listFolderContinue(result.getCursor());
                        }

                        mUiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                setAdapter(new DropBoxListRecyclerViewAdapter(items, new OnListFragmentInteractionListener() {
                                    @Override
                                    public void onListFragmentInteraction(DropBoxListContent.DropBoxItem item) {
                                        Log.i(TAG, "onListFragmentInteraction: interacted " + item.path);
                                        AlbumFragment fragment = new AlbumFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("PATH", item.path);
                                        bundle.putInt(ARG_COLUMN_COUNT, 2);
                                        fragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.main_container, fragment)
                                                .addToBackStack("album")
                                                .commit();
                                    }
                                }));
                            }
                        });

                    } catch (DbxException e) {
                        dialog.dismiss();
                        e.printStackTrace();
                    }
                }


            }, "drop");
            dropBoxThread.start();
        }
    }

}
