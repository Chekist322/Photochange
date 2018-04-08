package photochange.tosya.photochange.fragments;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import photochange.tosya.photochange.R;
import photochange.tosya.photochange.model.RegisterModel;

public class ProfileFragment extends Fragment{

    private static final String TAG = ProfileFragment.class.getName();
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    TextView mPageLabel;

    @BindView(R.id.time_in_app_label)
    TextView mTimeInAppLabel;

    @BindView(R.id.profile_email_text_view)
    TextView mEmail;

    @BindView(R.id.profile_status_text_view)
    TextView mStatus;

    @BindView(R.id.profile_last_time_online_text_view)
    TextView mLastTimeOnline;

    @BindView(R.id.profile_location_text_view)
    TextView mLocation;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Fetching", "Please wait...");
        mLocation.setText("Unknown");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mPageLabel = getActivity().findViewById(R.id.page_label);
        RelativeLayout appbar = getActivity().findViewById(R.id.main_appbar_container);
        View profileImage = getActivity().findViewById(R.id.app_bar_profile_image);
        profileImage.setVisibility(View.INVISIBLE);
        appbar.setBackground(getActivity().getDrawable(R.drawable.blur_background));
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (150 * scale + 0.5f);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels);
        appbar.setLayoutParams(params);
        if (mPageLabel != null) {
            final FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                mDatabase.getReference("users/"+user.getUid()+"/registerModel").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RegisterModel registerModel = dataSnapshot.getValue(RegisterModel.class);
                        if (registerModel != null && mLocation != null) {
                            dialog.dismiss();
                            mLocation.setText(registerModel.getZone());
                            Calendar calendar = Calendar.getInstance();
                            long currentMillis = calendar.getTimeInMillis();
                            long resultMillis = currentMillis - registerModel.getTimeInMillisRegister();
                            String resultDays = Long.toString(resultMillis/8640000) + " days in app";
                            mTimeInAppLabel.setText(resultDays);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        dialog.dismiss();
                        FirebaseCrash.log("Failed to read value." + databaseError.toException());
                    }
                });
                FirebaseUserMetadata metadata = user.getMetadata();
                mStatus.setText("User");
                mPageLabel.setTextSize(20);
                mPageLabel.setText(user.getDisplayName());
                mEmail.setText(user.getEmail());
                if (metadata != null) {
                    Date date = new Date(metadata.getLastSignInTimestamp());
                    mLastTimeOnline.setText(date.toString());
                } else {
                    mLastTimeOnline.setText("Unknown");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        RelativeLayout appbar = getActivity().findViewById(R.id.main_appbar_container);
        appbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        View profileImage = getActivity().findViewById(R.id.app_bar_profile_image);
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (100 * scale + 0.5f);
        mPageLabel.setTextSize(18);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels);
        appbar.setLayoutParams(params);
        profileImage.setVisibility(View.VISIBLE);
        super.onDestroy();
    }
}
