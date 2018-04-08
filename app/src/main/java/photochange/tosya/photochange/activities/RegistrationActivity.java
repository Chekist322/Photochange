package photochange.tosya.photochange.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import photochange.tosya.photochange.R;
import photochange.tosya.photochange.model.RegisterModel;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = RegistrationActivity.class.getName();
    @BindView(R.id.register_button)
    Button mRegisterButton;
    @BindView(R.id.registration_name_edit_text)
    EditText mNameEditText;
    @BindView(R.id.registration_email_edit_text)
    EditText mEmailEditText;
    @BindView(R.id.registration_password_edit_text)
    EditText mPasswordEditText;
    @BindView(R.id.registration_repassword_edit_text)
    EditText mRepasswordEditText;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
    }

    @OnClick(R.id.sign_in_reference)
    void onClickLoginRef() {
        onBackPressed();
    }

    @OnClick(R.id.register_button)
    void onClickRegister() {
        String password = mPasswordEditText.getText().toString();
        String repassword = mRepasswordEditText.getText().toString();
        if (TextUtils.isEmpty(mNameEditText.getText())) {
            mNameEditText.setError("Field must not be empty!");
            return;
        }
        if (TextUtils.isEmpty(mEmailEditText.getText())) {
            mEmailEditText.setError("Field must not be empty!");
            return;
        }
        if (TextUtils.isEmpty(mPasswordEditText.getText())) {
            mPasswordEditText.setError("Field must not be empty!");
            return;
        }
        if (TextUtils.isEmpty(mRepasswordEditText.getText())) {
            mRepasswordEditText.setError("Field must not be empty!");
            return;
        }
        if (!password.equals(repassword)) {
            mRepasswordEditText.setError("Passwords don't match!");
            return;
        }
        final ProgressDialog dialog = ProgressDialog.show(RegistrationActivity.this, "Registration", "Please, wait...", true);
        mAuth.createUserWithEmailAndPassword(mEmailEditText.getText().toString(),
                mPasswordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(mNameEditText.getText().toString())
                                    .build();
                            if (user != null) {
                                RegisterModel model = new RegisterModel(Calendar.getInstance().getTimeInMillis(),
                                        Calendar.getInstance().getTimeZone().getID());
                                DatabaseReference reference = mDatabase.getReference("users/" + user.getUid() +"/registerModel");
                                reference.setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });
                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FirebaseCrash.log("Profile updated");
                                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                        RegistrationActivity.this.finish();
                                    }
                                });

                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(RegistrationActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }
}
