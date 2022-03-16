package com.example.myfitnessapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInformationActivity extends AppCompatActivity {
    private static final String TAG = "UserInfoSet";
    String userID, Xsystem;
    TextView Male, Female, bmi_Height, bmi_Weight, bmi_Age, bmi_Gender,BMI,Change;
    EditText Height, Weight, Age;
    Switch Gender;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    CircleImageView ProfilePic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        Change = findViewById(R.id.txtChange);
        BMI = findViewById(R.id.txtBMI);
        bmi_Height = findViewById(R.id.txtHeight);
        bmi_Weight = findViewById(R.id.txtWeight);
        bmi_Age = findViewById(R.id.txtAge);
        bmi_Gender = findViewById(R.id.txtGender);

        Male = findViewById(R.id.txtMale);
        Female = findViewById(R.id.txtFemale);
        Height = findViewById(R.id.edtHeight);
        Weight = findViewById(R.id.edtWeight);
        Gender = findViewById(R.id.switchgender);
        Age = findViewById(R.id.edtAge);
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        ProfilePic = findViewById(R.id.ImgProfile);
        LoadProfilePic();
        getSystem();
        final DocumentReference documentReference = fstore.collection("physiological").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                DecimalFormat df = new DecimalFormat("#.##");
               String w = documentSnapshot.getString("Height");
               if (w == null){

               }else {
                   if(Xsystem.equals("Imperial")){
                       double xy = Double.parseDouble(w)/30.479988094875;
                       xy = Double.valueOf(df.format(xy));
                       String bb =  xy + " ft";
                       bmi_Height.setText(bb);
                   }else if (Xsystem.equals("Metric")){
                       String a = w + " cm";
                       bmi_Height.setText(a);
                   }

               }
                String x = documentSnapshot.getString("Weight");
                if (x == null){

                }else {
                    if(Xsystem.equals("Imperial")) {
                        double yz = Double.parseDouble(x) * 2.20462;
                        yz = Double.valueOf(df.format(yz));
                        String bc = yz + "lb";
                        bmi_Weight.setText(bc);
                    } else if(Xsystem.equals("Metric")) {
                        String b = x + " kg";
                        bmi_Weight.setText(b);
                    }


                }
                String y =documentSnapshot.getString("Age");
                if (y == null){

                }else {
                    bmi_Age.setText(y);
                }
                String z = documentSnapshot.getString("Gender");
                if (z == null){

                }else {
                    bmi_Gender.setText(z);
                }

                double height,weight,bmi,e;
                if(w == null && x == null){

                }else {
                    height = Double.parseDouble(w);
                    weight = Double.parseDouble(x);


                    e = (height/100)*(height/100);
                    bmi = weight/e;
                    String Underweight, Normal, Overweight, Obese;
                    Underweight = "Underweight";
                    Normal = "Normal";
                    Overweight = "Overweight";
                    Obese = "Obese";
                    if (bmi < 18.5){
                        BMI.setText(Underweight);
                        BMI.setTextColor(Color.parseColor("#FF0000"));
                        Change.setText("Gain Weight");
                    }else
                    if(bmi >= 18.5 && bmi <= 24.9 ){
                        BMI.setText(Normal);
                        BMI.setTextColor(Color.parseColor("#4bff29"));
                        Change.setText("None");
                    }else
                    if(bmi >= 25 && bmi <= 29.6 ) {
                        BMI.setText(Overweight);
                        BMI.setTextColor(Color.parseColor("#ffa429"));
                        Change.setText("Lose Weight");
                    }else
                    if(bmi >= 30 ){
                        BMI.setText(Obese);
                        BMI.setTextColor(Color.parseColor("#FF0000"));
                        Change.setText("Seek Help");
                    }
                }

            }
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }


    public void ChangeGender(View view) {
        if (Gender.isChecked())
        {
            Female.setVisibility(View.VISIBLE);
            Male.setVisibility(View.INVISIBLE);
        }else if (!Gender.isChecked())
        {
            Female.setVisibility(View.INVISIBLE);
            Male.setVisibility(View.VISIBLE);
        }


    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    public void StoreAll(View view) {



        if (isEmpty(Height)) {
            Toast t = Toast.makeText(UserInformationActivity.this, "You must enter a Height!", Toast.LENGTH_SHORT);
            t.show();
        }else if (isEmpty(Weight)) {
            Toast t = Toast.makeText(UserInformationActivity.this, "You must enter a Weight!", Toast.LENGTH_SHORT);
            t.show();
        }else if (isEmpty(Age)) {
            Toast t = Toast.makeText(UserInformationActivity.this, "You must enter a Age!", Toast.LENGTH_SHORT);
            t.show();
        }else{

            String gender;
        if (Gender.isChecked())
            gender = Gender.getTextOn().toString();
        else
            gender = Gender.getTextOff().toString();

        userID = mAuth.getCurrentUser().getUid();

            String hHeight, wWeight;
            double xHeight,yWeight;
            hHeight = Height.getText().toString();
            wWeight = Weight.getText().toString();
            xHeight = Double.parseDouble(hHeight);
            yWeight = Double.parseDouble(wWeight);
            if(Xsystem.equals("Metric")){
                hHeight = String.valueOf(xHeight);
                wWeight = String.valueOf(yWeight);
            }else if (Xsystem.equals("Imperial")){
                xHeight = xHeight * 30.479988094875;
                yWeight = yWeight / 2.20462;
                hHeight = String.valueOf(xHeight);
                wWeight = String.valueOf(yWeight);
            }

        DocumentReference documentReference = fstore.collection("physiological").document(userID);
        Map<String, Object> physiologicalInfo =  new HashMap<>();
        physiologicalInfo.put("Height",hHeight);
        physiologicalInfo.put("Weight",wWeight);
        physiologicalInfo.put("Age",Age.getText().toString());
        physiologicalInfo.put("Gender",gender);
        documentReference.set(physiologicalInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: User height saved" + userID);
                Toast.makeText(UserInformationActivity.this,  "User Physiological Data Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UserInformationActivity.this,HomeActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: User height not Saved" + e.getMessage());
                Toast.makeText(UserInformationActivity.this,  "User Physiological Data Not Saved", Toast.LENGTH_SHORT).show();
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
                   Height.setHint("Enter Height: (cm)");
                    Weight.setHint("Enter Weight: (kg)");
                }else if (Xsystem.equals("Imperial")){
                    Height.setHint("Enter Height: (ft)");
                    Weight.setHint("Enter Weight: (lb)");
                }

            }
        });
    }
}