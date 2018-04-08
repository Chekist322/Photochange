package photochange.tosya.photochange.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import photochange.tosya.photochange.R;

import static photochange.tosya.photochange.utils.Constant.ACCESS_TOKEN;

public class PhotographersFragment extends DropBoxListFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView mPageLabel = getActivity().findViewById(R.id.page_label);
        if (mPageLabel != null) {
            mPageLabel.setText("Photographers");
        }
        Thread dropBoxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DbxRequestConfig config = new DbxRequestConfig("Photograph");
                DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

                try {
                    ListFolderResult result = client.files().listFolder("/photographers/");
                    while (true) {
                        for (Metadata metadata : result.getEntries()) {
                            System.out.println(metadata.getPathLower());
                        }
                        if (!result.getHasMore()) {
                            break;
                        }
                        result = client.files().listFolderContinue(result.getCursor());
                    }
                } catch (DbxException e) {
                    e.printStackTrace();
                }


            }
        }, "drop");
        dropBoxThread.start();
    }
}
