package com.example.myfitnessapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 101;
    public static final int CR_CODE = 102;
    private static final String TAG = "SettingsTag";
    EditText UserName, Name, Surname;
    String userID,   System, Xsystem;
    Button PwRst, sUname, sName, sSname;
    Switch  MSys;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    CircleImageView ProfilePic;
    TextView MsysText;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        UserName = findViewById(R.id.edtChangeUserName);
        sUname = findViewById(R.id.btnChangeUserName);
        Name = findViewById(R.id.edtChangeName);
        sName = findViewById(R.id.btnChangeName);
        Surname = findViewById(R.id.edtChangeSurname);
        sSname = findViewById(R.id.btnChangeSurname);
        MSys = findViewById(R.id.switchImperial);
        MsysText = findViewById(R.id.txtImperial);
        PwRst = findViewById(R.id.btnRstPws);
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        ProfilePic = findViewById(R.id.ImgProfile);

        LoadProfilePic();
        getSystem();

        sUname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(UserName)) {
                    Toast t = Toast.makeText(SettingsActivity.this, "You must enter a username!", Toast.LENGTH_SHORT);
                    t.show();
                }else
                getUsername();
            }


        });

        sName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(Name)) {
                    Toast t = Toast.makeText(SettingsActivity.this, "You must enter a First name!", Toast.LENGTH_SHORT);
                    t.show();
                }else
                getFirstName();


            }


        });

        sSname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(Surname)) {
                    Toast t = Toast.makeText(SettingsActivity.this, "You must enter a Surname!", Toast.LENGTH_SHORT);
                    t.show();
                }else
                getSurName();

            }


        });

    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    public void ResetPw(View view) {
        final EditText resetMail = new EditText(view.getContext());
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
        passwordResetDialog.setTitle("Reset Password?");
        passwordResetDialog.setMessage("Enter Your Email To Receive Reset Link");
        passwordResetDialog.setView(resetMail);

        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String mail = resetMail.getText().toString();
                mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SettingsActivity.this, "Reset Link Has Been Sent To Your Email", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, "Error! Reset Link Not Sent To Your Email" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        passwordResetDialog.create().show();
    }

    public void getUsername(){
        final DocumentReference documentReference = fstore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                String Firstname,SurName,Username;

                Firstname = documentSnapshot.getString("Firstname");
                SurName = documentSnapshot.getString("Surname");
                Username = UserName.getText().toString();
                Map<String, Object> user =  new HashMap<>();
                user.put("Username",Username);
                user.put("Firstname",Firstname);
                user.put("Surname",SurName);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: User profile and data saved" + userID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: User Not Saved" + e.getMessage());
                    }
                });
            }
        });
    }

    public void getFirstName(){
        final DocumentReference documentReference = fstore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                String Firstname,SurName,Username;

                Firstname = Name.getText().toString();
                SurName = documentSnapshot.getString("Surname");
                Username = documentSnapshot.getString("Username");
                Map<String, Object> user =  new HashMap<>();
                user.put("Username",Username);
                user.put("Firstname",Firstname);
                user.put("Surname",SurName);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: User profile and data saved" + userID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: User Not Saved" + e.getMessage());
                    }
                });
            }
        });
    }

    public void getSurName(){
        final DocumentReference documentReference = fstore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                String Firstname,SurName,Username;

                Firstname = documentSnapshot.getString("Firstname");
                SurName = Surname.getText().toString();
                Username = documentSnapshot.getString("Username");
                Map<String, Object> user =  new HashMap<>();
                user.put("Username",Username);
                user.put("Firstname",Firstname);
                user.put("Surname",SurName);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: User profile and data saved" + userID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: User Not Saved" + e.getMessage());
                    }
                });
            }
        });
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

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    public void SetProfileImg(View view) {
        askCameraPermission();
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

             mAuth = FirebaseAuth.getInstance();
             userID = mAuth.getCurrentUser().getUid();

            DocumentReference documentReference = fstore.collection("ProfilePics").document(userID);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, baos);
            String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            Map<String, Object> ProfilePics =  new HashMap<>();
            ProfilePics.put("Image",imageEncoded);
            documentReference.set(ProfilePics).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: User img saved" + userID);
                    Toast.makeText(SettingsActivity.this,  "Image saved", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: User img not Saved" + e.getMessage());
                    Toast.makeText(SettingsActivity.this,  "Image Not Saved", Toast.LENGTH_SHORT).show();
                }
            });
        }

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
                    MSys.setChecked(true);
                    MsysText.setText("Metric System");
                }else if (Xsystem.equals("Imperial")){
                    MSys.setChecked(false);
                    MsysText.setText("Imperial System");
                }

            }
        });
    }

    public void SetSystem(View view) {
        if (MSys.isChecked()){
            MsysText.setText("Metric System");
            System = "Metric";
        }else if (!MSys.isChecked()){
            MsysText.setText("Imperial System");
            System = "Imperial";
        }
        SetMeasurement();
    }

    public void SetMeasurement(){
        final DocumentReference documentReference = fstore.collection("MeasurementSystem").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

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







