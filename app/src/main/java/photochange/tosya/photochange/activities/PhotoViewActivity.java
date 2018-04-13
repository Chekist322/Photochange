package photochange.tosya.photochange.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import photochange.tosya.photochange.R;

public class PhotoViewActivity extends AppCompatActivity {

    @BindView(R.id.photo_view)
    ImageView mPhotoView;
    @BindView(R.id.like_button)
    ImageButton mLikeButton;
    @BindView(R.id.dislike_button)
    ImageButton mDislikeButton;
    @BindView(R.id.reading_progress_bar)
    ProgressBar mBar;

    static Bitmap mBitmap;
    String mPath;
    boolean mEstimate;

    Handler mUiHandler = new Handler(Looper.getMainLooper());

    FirebaseDatabase mDatabase;
    DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        ButterKnife.bind(this);
        mLikeButton.setEnabled(false);
        mDislikeButton.setEnabled(false);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.arrow_left_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (mBitmap != null)
            mPhotoView.setImageBitmap(mBitmap);

        if (getIntent() != null) {
            mPath = getIntent().getStringExtra("PATH");
            setTitle(getIntent().getStringExtra("NAME"));
        }
        mBar.setVisibility(View.VISIBLE);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("estimate/" + md5(mPath));

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLikeButton.setEnabled(true);
                mDislikeButton.setEnabled(true);
                mBar.setVisibility(View.GONE);
                if (dataSnapshot.getValue() != null) {
                    mEstimate = (Boolean) dataSnapshot.getValue(); //194169036
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mEstimate) {
                                mLikeButton.setColorFilter(Color.argb(255, 213, 0, 0));
                                mDislikeButton.setColorFilter(Color.argb(0, 0, 0, 0));
                            } else {
                                mDislikeButton.setColorFilter(Color.argb(255, 213, 0, 0));
                                mLikeButton.setColorFilter(Color.argb(0, 0, 0, 0));
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Intent getStartIntent(Bitmap bitmap, Context context) {
        mBitmap = bitmap;
        return new Intent(context, PhotoViewActivity.class);
    }

    @OnClick(R.id.like_button)
    void likeButtonClick() {
        mReference.setValue(Boolean.TRUE).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                } else {
                    if (task.getException() != null) {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Server is busy", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @OnClick(R.id.dislike_button)
    void dislikeButtonClick() {
        mReference.setValue(Boolean.FALSE).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                } else {
                    if (task.getException() != null) {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Server is busy", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
