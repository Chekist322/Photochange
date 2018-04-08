package photochange.tosya.photochange.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import photochange.tosya.photochange.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();
    FirebaseAuth mAuth;

    @BindView(R.id.email_edit_text)
    EditText mEmailEditText;
    @BindView(R.id.password_edit_text)
    EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.register_reference)
    void onClickRegisterRef() {
        startActivity(new Intent(getBaseContext(), RegistrationActivity.class));
    }

    @OnClick(R.id.login_button)
    void onClickLogin() {
        if (TextUtils.isEmpty(mEmailEditText.getText())) {
            mEmailEditText.setError("Field must not be empty!");
            return;
        }
        if (TextUtils.isEmpty(mPasswordEditText.getText())) {
            mPasswordEditText.setError("Field must not be empty!");
            return;
        }
        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "Sign In", "Please, wait...", true);

        mAuth.signInWithEmailAndPassword(mEmailEditText.getText().toString(),
                mPasswordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(
                                    LoginActivity.this, MainActivity.class)
                                    .putExtra("user", user));
                            LoginActivity.this.finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            LoginActivity.this.finish();
        }
    }
}
