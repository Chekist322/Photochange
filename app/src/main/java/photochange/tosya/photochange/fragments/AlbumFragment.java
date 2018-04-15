package photochange.tosya.photochange.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import photochange.tosya.photochange.R;
import photochange.tosya.photochange.activities.PhotoViewActivity;
import photochange.tosya.photochange.adapter.DropBoxListRecyclerViewAdapter;
import photochange.tosya.photochange.content.DropBoxListContent;

import static photochange.tosya.photochange.utils.Constant.ACCESS_TOKEN;

public class AlbumFragment extends DropBoxListFragment {

    private static final String TAG = AlbumFragment.class.getName();
    final List<DropBoxListContent.DropBoxItem> items = new ArrayList<>();

    Handler mUiHandler = new Handler(Looper.getMainLooper());
    String mPath;
    boolean passwordIsHere = false;

    TextView mPageLabel;

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

            mPageLabel = getActivity().findViewById(R.id.page_label);
            if (mPageLabel != null) {
                String[] splited = mPath.split("/");
                mPageLabel.setText("Album \"" + splited[3] + "\" (" + splited[2] + ")");
            }
            final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Loading", "Please wait...");
            Thread dropBoxThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    DbxRequestConfig config = new DbxRequestConfig("Photographalbums");
                    final DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
                    DbxDownloader<FileMetadata> passwordFile = null;
                    try {
                        final ListFolderResult result = client.files().listFolder(mPath);
                        for (Metadata metadata : result.getEntries()) {

                            if (metadata.getName().contains("password")) {
                                passwordFile = client.files().download(metadata.getPathDisplay());
                                passwordIsHere = true;
                            }
                        }
                        if (passwordIsHere && items.isEmpty() && passwordFile != null) {
                            final String password = convertStreamToString(passwordFile.getInputStream());
                            final EditText input = new EditText(getActivity());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(params);
                            mUiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    AlertDialog passwordDialog = new AlertDialog.Builder(getActivity())
                                            .setTitle("Password")
                                            .setMessage("Enter Password")
                                            .setView(input)
                                            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (input.getText().toString().equals(password)) {
                                                        processImages();
                                                        Toast.makeText(getActivity(), "correct", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getActivity(), "invalid password", Toast.LENGTH_SHORT).show();
                                                        getActivity().onBackPressed();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Canel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    getActivity().onBackPressed();
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();
                                }
                            });
                        } else {
                            mUiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    processImages();
                                }
                            });
                        }

                    } catch (DbxException e) {
                        dialog.dismiss();
                        e.printStackTrace();
                    }
                }


            }, "drop");
            dropBoxThread.start();
        }
    }

    private void processImages() {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Loading", "Please wait...");
        Thread dropBoxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DbxRequestConfig config = new DbxRequestConfig("Photographalbums");
                final DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
                while (true) {
                    try {
                        ListFolderResult result = client.files().listFolder(mPath);
                        for (Metadata metadata : result.getEntries()) {
                            if (metadata.getName().contains("password"))
                                break;
                            DbxDownloader<FileMetadata> loadedImage = client.files().download(metadata.getPathDisplay());
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
                    } catch (DbxException e) {
                        dialog.dismiss();
                        e.printStackTrace();
                    }
                }

                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        setAdapter(new DropBoxListRecyclerViewAdapter(items, new OnListFragmentInteractionListener() {
                            @Override
                            public void onListFragmentInteraction(DropBoxListContent.DropBoxItem item, int position) {
                                Log.i(TAG, "onListFragmentInteraction: interacted " + item.path);
                                Intent intent = PhotoViewActivity.getStartIntent(items, getActivity());
                                intent.putExtra("POSITION", position);
                                intent.putExtra("ALBUM_NAME", mPageLabel.getText());
                                startActivity(intent);
                            }
                        }));
                    }
                });
            }
        });
        dropBoxThread.start();
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
