package com.cookandroid.exam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    // 구글 로그인
    public String loginD=null;
    public int pay_money=0;
    SignInButton Google_Login;
    private static final int RC_SIGN_IN = 1000;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MainActivity";
    // 카카오 로그인
    private SessionCallback callback;
    // 페이스북 콜백 매니저
    private CallbackManager callbackManager;
    private LoginButton buttonFacebook;

    //파이어베이스 실시간 디비 사용을 위한 인스턴스 가져오기
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    /**
     * 로그인 버튼을 클릭 했을시 access token을 요청하도록 설정한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 구글 로그인
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth = FirebaseAuth.getInstance();
        Google_Login = findViewById(R.id.Google_Login);
        Google_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        // 카카오 로그인
        requestMe();
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        // 페이스북 콜백 등록
        callbackManager = CallbackManager.Factory.create();

        buttonFacebook = findViewById(R.id.login_button);
        buttonFacebook.setReadPermissions("email", "public_profile");
        // buttonFacebook.setFragment(this);
        buttonFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                //Toast.makeText(MainActivity.this, "페이스북 로그인 성공1", Toast.LENGTH_SHORT).show();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent menu_intent = new Intent(MainActivity.this,Menu_Activity.class);
                if (user != null) {
                    // Name, email address, and profile photo Url
                    String name = user.getDisplayName();
                    String email = user.getEmail();
                    Uri photoUrl = user.getPhotoUrl();

                    // Check if user's email is verified
                    boolean emailVerified = user.isEmailVerified();

                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getIdToken() instead.
                    String uid = user.getUid();
                    loginD = "Facebook";
                    Toast.makeText(getApplicationContext(), name + "님 안녕하세요", Toast.LENGTH_LONG).show();
                    //databaseReference.child("Google_user").child(uid).setValue(name);
                    writeNewUser(uid,name,pay_money);

                    menu_intent.putExtra("id",String.valueOf(uid));
                    menu_intent.putExtra("pay",String.valueOf(pay_money));
                    menu_intent.putExtra("name",name);
                    menu_intent.putExtra("loginD",loginD);
                }
                startActivity(menu_intent);

            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "페이스북 로그인 실패1", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {

            }
        });


    }

    /*
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }*/
    private void writeNewUser(String userId, String name, int pay) {
        User user = new User(name, pay);
        databaseReference.child(loginD+"_user").child(userId).setValue(user);
    }
    public class User {

        public String username;
        public int pay;

        public User() {

        }

        public User(String username, int pay) {
            this.username = username;
            this.pay = pay;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);
        // 카카오 로그인
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        // 공용(카카오, 구글, 페이스북)
        super.onActivityResult(requestCode, resultCode, data);
        // 구글 로그인
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //구글 로그인 성공해서 Firebase에 인증
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            //구글 로그인 실패
            else {
            }
        }

    }

    // 페이스북 로그인 이벤트
    // 사용자가 정상적으로 로그인한 후 페이스북 로그인 버튼의 onSuccess 콜백 메소드에서 로그인한 사용자의
    // 액세스 토큰을 가져와서 Firebase 사용자 인증 정보로 교환하고,
    // Firebase 사용자 인증 정보를 사용해 Firebase에 인증.
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                        } else {
                            // 로그인 실패
                        }
                    }
                });
    }

    // 카카오 로그인
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().requestMe(new MeResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        //에러로 인한 로그인 실패
//                        finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {

                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.

                    Log.e("UserProfile", userProfile.toString());
                    Log.e("UserProfile", userProfile.getId() + "");


                    long id = userProfile.getId();//개인 할당 된 아이디 (이메일 아님)
                    String name = userProfile.getNickname(); //닉네임
                    Toast.makeText(getApplicationContext(), name + "님 안녕하세요", Toast.LENGTH_LONG).show();
                    //토스트로 확dls
                    //databaseReference.child("Kakao_user").child(String.valueOf(id)).setValue(name);
                    //databaseReference.child("Kakao_user").child(String.valueOf(id)).child(name).child("pay_money").setValue(String.valueOf(pay_money));
                   // setContentView(R.layout.main_layout);
                    loginD = "Kakao";
                    writeNewUser(String.valueOf(id),name,pay_money);
                    Intent menu_intent = new Intent(MainActivity.this,Menu_Activity.class);
                    menu_intent.putExtra("id",String.valueOf(id));
                    menu_intent.putExtra("pay",String.valueOf(pay_money));
                    menu_intent.putExtra("name",name);
                    menu_intent.putExtra("loginD",loginD);
                    startActivity(menu_intent);

                }
            });
        }



        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
            Toast.makeText(MainActivity.this, "인증 실패", Toast.LENGTH_SHORT).show();
        }
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // 구글 로그인
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "인증 실패", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(MainActivity.this, "구글 로그인 인증 성공", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Intent menu_intent = new Intent(MainActivity.this,Menu_Activity.class);
                            if (user != null) {
                                // Name, email address, and profile photo Url
                                String name = user.getDisplayName();
                                String email = user.getEmail();
                                Uri photoUrl = user.getPhotoUrl();

                                // Check if user's email is verified
                                boolean emailVerified = user.isEmailVerified();

                                // The user's ID, unique to the Firebase project. Do NOT use this value to
                                // authenticate with your backend server, if you have one. Use
                                // FirebaseUser.getIdToken() instead.
                                String uid = user.getUid();
                                loginD = "Google";
                                Toast.makeText(getApplicationContext(), name + "님 안녕하세요", Toast.LENGTH_LONG).show();
                                //databaseReference.child("Google_user").child(uid).setValue(name);
                                writeNewUser(uid,name,pay_money);

                                menu_intent.putExtra("id",String.valueOf(uid));
                                menu_intent.putExtra("pay",String.valueOf(pay_money));
                                menu_intent.putExtra("name",name);
                                menu_intent.putExtra("loginD",loginD);
                            }
                            startActivity(menu_intent);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    //카톡
    public void requestMe() {
        //유저의 정보를 받아오는 함수 (카카오톡)

        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e(TAG, "error message=" + errorResult);
//                super.onFailure(errorResult);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {

                Log.d(TAG, "onSessionClosed1 =" + errorResult);
            }

            @Override
            public void onNotSignedUp() {
                //카카오톡 회원이 아닐시
                Log.d(TAG, "onNotSignedUp ");

            }

            @Override
            public void onSuccess(UserProfile result) {
                Log.e("UserProfile", result.toString());
                Log.e("UserProfile", result.getId() + "");
            }
        });
    }
/*
    //구글 회원 정보 가져오기
    public void requestMeGoogle() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
            loginD = "Google";
            Toast.makeText(getApplicationContext(), name + "님 안녕하세요", Toast.LENGTH_LONG).show();
            //databaseReference.child("Google_user").child(uid).setValue(name);
            writeNewUser(uid,name,pay_money);
            Intent menu_intent = new Intent(MainActivity.this,Menu_Activity.class);
            menu_intent.putExtra("id",String.valueOf(uid));
            menu_intent.putExtra("pay",String.valueOf(pay_money));
            menu_intent.putExtra("name",name);
            menu_intent.putExtra("loginD",loginD);
        }
    }
*/
/*
    //페이스북 회원 정보 가져오기
    public void requestMeFacebook() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
            loginD = "Facebook";
            Toast.makeText(getApplicationContext(), name + "님 안녕하세요", Toast.LENGTH_LONG).show();
            //databaseReference.child("Facebook_user").child(uid).setValue(name);
            writeNewUser(uid,name,pay_money);
            Intent menu_intent = new Intent(MainActivity.this,Menu_Activity.class);
            menu_intent.putExtra("id",String.valueOf(uid));
            menu_intent.putExtra("pay",String.valueOf(pay_money));
            menu_intent.putExtra("name",name);
            menu_intent.putExtra("loginD",loginD);
        }
    }
*/
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

}