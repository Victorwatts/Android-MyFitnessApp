package com.example.myfitnessapp;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class DailyMealsActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 101;
    public static final int CR_CODE = 102;
    ImageView imageview;
    Button photoButton, yes, no;
    LinearLayout heading, YesNo, Lin;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    CircleImageView ProfilePic;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_meals);
        yes = findViewById(R.id.btn_yes);
        no =  findViewById(R.id.btn_no);
        heading =  findViewById(R.id.t_btn);
        YesNo =  findViewById(R.id.v_btn);
        Lin =  findViewById(R.id.LinV);
        photoButton = findViewById(R.id.btnAddMeal);
        imageview =  findViewById(R.id.edtImg);
        ProfilePic = findViewById(R.id.ImgProfile);
        fstore = FirebaseFirestore.getInstance();
        LoadProfilePic();


        addMealImg();


        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermission();

            }


        });
    }





    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void addMealImg(){


        String userID;
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){


                      for(DataSnapshot meald : dataSnapshot.getChildren()){
                         String cal = (String) meald.child("Calories In Meal").getValue();
                          String date = (String) meald.child("Date").getValue();
                          String bt = (String) meald.child("Image").getValue();
                          byte[] bytes = Base64.decode(bt,Base64.DEFAULT);
                         Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                          String Name = (String) meald.child("Meal Name").getValue();



                          LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(670, 240);
                          LinearLayout.LayoutParams Imageparams = new LinearLayout.LayoutParams(180, 200);
                          LinearLayout.LayoutParams textParams2 = new LinearLayout.LayoutParams(437, 200);


                          layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                          layoutParams.topMargin = 30;
                          Imageparams.gravity = Gravity.CENTER;
                          textParams2.gravity = Gravity.CENTER;

                          Imageparams.leftMargin = 10;
                          textParams2.leftMargin = 10;

                          LinearLayout ll = new LinearLayout(DailyMealsActivity.this);
                          ll.setOrientation(LinearLayout.HORIZONTAL);
                          ll.setBackgroundResource(R.drawable.biostats);
                          ll.setLayoutParams(layoutParams);
                          ll.setId(R.id.layoutImg);
                          Lin.addView(ll);

                          ImageView MealPic = new ImageView(DailyMealsActivity.this);
                          MealPic.setLayoutParams(Imageparams);
                          MealPic.setImageBitmap(bitmap);
                          ll.addView(MealPic);

                          TextView MealData = new TextView(DailyMealsActivity.this);
                          MealData.setText("Meal: " + Name +"\n Date: " + date + "\n Calories: " + cal);
                          MealData.setLayoutParams(textParams2);
                          MealData.setTextSize(15);
                          MealData.setBackgroundResource(R.drawable.myedittextbg);
                          MealData.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                          ll.addView(MealData);

                          // Toast.makeText(getApplicationContext(), Meal, Toast.LENGTH_LONG).show();
                      }


                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }



    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();

            } else {
                Toast.makeText(this, "give cam", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CR_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CR_CODE && resultCode == RESULT_OK) {
            final Bitmap image = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(image);
            YesNo.setVisibility(View.VISIBLE);
            heading.setVisibility(View.VISIBLE);
            //
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder AddImgDialogue = new AlertDialog.Builder(view.getContext());
                    DialogInterface dia = new DialogInterface() {
                        @Override
                        public void cancel() {

                        }

                        @Override
                        public void dismiss() {

                        }
                    };

                    View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.imagedata, (ViewGroup) findViewById(android.R.id.content), false);
                    final ImageView Img = viewInflated.findViewById(R.id.edtImg);
                    Img.setImageBitmap(image);
                    final EditText Meal_Name =  viewInflated.findViewById(R.id.edtMealName);
                    final EditText Meal_Calories =  viewInflated.findViewById(R.id.edtCalIntake);
                    final Button Yes =  viewInflated.findViewById(R.id.btn_yes);
                    final Button No =  viewInflated.findViewById(R.id.btn_no);
                    AddImgDialogue.setView(viewInflated);
                    final AlertDialog alertDialog = AddImgDialogue.create();

                    Yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String Mname,Mcal,userID;
                            Mname = Meal_Name.getText().toString();
                            Mcal = Meal_Calories.getText().toString();
                            mAuth = FirebaseAuth.getInstance();
                            userID = mAuth.getCurrentUser().getUid();

                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                            String formattedDate = df.format(c);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            Map<String, Object> user =  new HashMap<>();
                            user.put("Image",imageEncoded);
                            user.put("Meal Name",Mname);
                            user.put("Date",formattedDate);
                            user.put("Calories In Meal",Mcal);
                            ref.child("users").child(userID).push().setValue(user);

                            Toast.makeText(DailyMealsActivity.this,"Image "+Mname+" saved",Toast.LENGTH_SHORT).show();
                            YesNo.setVisibility(View.INVISIBLE);
                            heading.setVisibility(View.INVISIBLE);
                            imageview.setImageBitmap(null);
                            alertDialog.dismiss();
                        }
                    });
                    No.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(DailyMealsActivity.this,"Image Not saved",Toast.LENGTH_SHORT).show();
                            YesNo.setVisibility(View.INVISIBLE);
                            heading.setVisibility(View.INVISIBLE);
                            imageview.setImageBitmap(null);
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YesNo.setVisibility(View.INVISIBLE);
                    heading.setVisibility(View.INVISIBLE);
                    imageview.setImageBitmap(null);
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

}



