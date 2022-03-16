package com.example.myfitnessapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class DailyChangesActivity extends AppCompatActivity {
    private static final String TAG = "UserGoalSet";
    String userID, Xsystem;
    EditText Steps, Weight, Calories;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    CircleImageView ProfilePic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_changes);
        Steps = findViewById(R.id.edtStepchange);
        Weight = findViewById(R.id.edtWeightChange);
        Calories = findViewById(R.id.edtCalorieChange);
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        ProfilePic = findViewById(R.id.ImgProfile);
        userID = mAuth.getCurrentUser().getUid();
        LoadProfilePic();
        getSystem();
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    public void StoreAll(View view) {

        if (isEmpty(Weight)) {
            Toast t = Toast.makeText(DailyChangesActivity.this, "You must enter a Weight!", Toast.LENGTH_SHORT);
            t.show();
        }else if (isEmpty(Calories)) {
            Toast t = Toast.makeText(DailyChangesActivity.this, "You must enter a Calories!", Toast.LENGTH_SHORT);
            t.show();
        }else if (isEmpty(Steps)) {
            Toast t = Toast.makeText(DailyChangesActivity.this, "You must enter a Steps!", Toast.LENGTH_SHORT);
            t.show();
        }else{
        userID = mAuth.getCurrentUser().getUid();

            String  wWeight;
            double yWeight;

            wWeight = Weight.getText().toString();
            yWeight = Double.parseDouble(wWeight);
            if(Xsystem.equals("Metric")){

            }else if (Xsystem.equals("Imperial")){
                yWeight = yWeight / 2.20462;
            }

        DocumentReference documentReference = fstore.collection("DailyChanges").document(userID);
        Map<String, Double> Changes =  new HashMap<>();
        Changes.put("Weight Change", (double) yWeight);
        Changes.put("Calories Change", (double) Double.parseDouble(Calories.getText().toString()));
        Changes.put("Steps Change", (double) Double.parseDouble(Steps.getText().toString()));
        documentReference.set(Changes).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: User height saved" + userID);
                Toast.makeText(DailyChangesActivity.this,  "User Daily Changes Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DailyChangesActivity.this,HomeActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: User height not Saved" + e.getMessage());
                Toast.makeText(DailyChangesActivity.this,  "User Daily Changes Not Saved", Toast.LENGTH_SHORT).show();
            }
        });

        }
    }


    public void StoreInfo(View view) {
        startActivity(new Intent(this,UserInformationActivity.class));
    }

    public void Goals(View view) {
        startActivity(new Intent(this,TargetGoalsActivity.class));
    }

    public void DailyChange(View view) {
        startActivity(new Intent(this,DailyChangesActivity.class));
    }

    public void MealRecord(View view) {
        startActivity(new Intent(this,DailyMealsActivity.class));
    }

    public void Statistics(View view) {
        startActivity(new Intent(this,UserStatisticsActivity.class));
    }

    public void Profile(View view) {
        startActivity(new Intent(this,SettingsActivity.class));
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
                    byte[] bytes = Base64.decode(Bit,Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    ProfilePic.setImageBitmap(bitmap);
                }

            }
        });


    }

    public void getSystem(){
        final DocumentReference documentReference = fstore.collection("MeasurementSystem").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                Xsystem = documentSnapshot.getString("System");

                if(Xsystem.equals("Metric")){

                    Weight.setHint("Enter Weight: (kg)");
                }else if (Xsystem.equals("Imperial")){

                    Weight.setHint("Enter Weight: (lb)");
                }

            }
        });
    }
}