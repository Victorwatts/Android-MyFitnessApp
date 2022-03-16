package com.example.myfitnessapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "SettingsTag";
    LinearLayout View1, View2;
    TextView PImg;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    CircleImageView ProfilePic;
    String userID;
    android.os.Vibrator Vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        View1 = findViewById(R.id.view1);
        View2 = findViewById(R.id.view2);
        PImg = findViewById(R.id.btnPimg);
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        ProfilePic = findViewById(R.id.ImgProfile);
        LoadProfilePic();
        SetMeasurement();
        Vibrator = (android.os.Vibrator)getSystemService(HomeActivity.VIBRATOR_SERVICE);
        //User = findViewById(R.id.txtUser);

        Toolbar toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);

    }


    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    public void StoreInfo(View view) {
        Vibrator.vibrate(80);
        startActivity(new Intent(HomeActivity.this,UserInformationActivity.class));
    }

    public void Goals(View view) {
        Vibrator.vibrate(80);
        startActivity(new Intent(HomeActivity.this,TargetGoalsActivity.class));
    }

    public void DailyChange(View view) {
        Vibrator.vibrate(80);
        startActivity(new Intent(HomeActivity.this,DailyChangesActivity.class));
    }

    public void MealRecord(View view) {
        Vibrator.vibrate(80);
        startActivity(new Intent(HomeActivity.this,DailyMealsActivity.class));
    }

    public void Statistics(View view) {
        Vibrator.vibrate(80);
        startActivity(new Intent(HomeActivity.this,UserStatisticsActivity.class));
    }

    public void Profile(View view) {
        Vibrator.vibrate(80);
        startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
    }

    public void OpenMenu(View view) {
        AlertDialog.Builder Menu = new AlertDialog.Builder(view.getContext());
        View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.menuslide, (ViewGroup) findViewById(android.R.id.content), false);
        Menu.setView(viewInflated);
        final AlertDialog alertDialog = Menu.create();
        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

        alertDialog.show();
    }

    public void SetProfileImg(View view) {
        startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
    }

    public void LoadProfilePic(){


        String userID;
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        final DocumentReference documentReference2 = fstore.collection("ProfilePics").document(userID);
        documentReference2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                String Bit = documentSnapshot.getString("Image");
                if(Bit != null)
                {
                    PImg.setVisibility(View.INVISIBLE);
                    byte[] bytes = Base64.decode(Bit,Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    ProfilePic.setImageBitmap(bitmap);
                }


            }
        });


    }

    public void SetMeasurement(){
        final DocumentReference documentReference = fstore.collection("MeasurementSystem").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                String System = "Metric";
                Map<String, Object> MeasurementSystem =  new HashMap<>();
                MeasurementSystem.put("System",System);
                documentReference.set(MeasurementSystem).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: system saved" + userID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: system Not Saved" + e.getMessage());
                    }
                });
            }
        });
    }

}