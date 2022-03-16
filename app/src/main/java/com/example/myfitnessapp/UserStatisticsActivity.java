package com.example.myfitnessapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserStatisticsActivity extends AppCompatActivity {
    private BarChart barChart;
    private BarChart barChart1;
    private BarChart barChart2;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    String userID, Xsystem;
    int cCalories = 0;
    int gCalories = 0;
    double cWeight = 0;
    int cSteps = 0;
    double gWeight = 0;
    int gSteps = 0;
    CircleImageView ProfilePic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        ProfilePic = findViewById(R.id.ImgProfile);
        LoadProfilePic();
        getSystem();
        fstore.collection("UserGoals").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    fstore.collection("DailyChanges").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()){
                                getDailyChanges();
                                getTarget();
                            }
                        }
                    });
                }
            }
        });

      
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    public ArrayList getData() {

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, cCalories));
        entries.add(new BarEntry(1f, gCalories));

        return entries;
    }

    public ArrayList getData1() {

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, cSteps));
        entries.add(new BarEntry(1f, gSteps));

        return entries;
    }

    public ArrayList getData2() {

        ArrayList<BarEntry> entries = new ArrayList<>();
        if(Xsystem.equals("Metric")){
            entries.add(new BarEntry(0f, (float) cWeight));
            entries.add(new BarEntry(1f, (float) gWeight));
        }else if (Xsystem.equals("Imperial")){
            cWeight = cWeight * 2.20462;
            gWeight = gWeight * 2.20462;
            entries.add(new BarEntry(0f, (float) cWeight ));
            entries.add(new BarEntry(1f, (float) gWeight));
        }


        return entries;
    }





    public void getTarget() {

        final DocumentReference documentReference = fstore.collection("UserGoals").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                gCalories = documentSnapshot.getLong("Calories Intake Goal").intValue();
                gSteps = documentSnapshot.getLong("Daily Steps Goal").intValue();
                gWeight = documentSnapshot.getLong("Weight Goal").doubleValue();


                barChart = findViewById(R.id.barChart);
                BarDataSet barDataSet = new BarDataSet(getData(), "");
                barDataSet.setValueTextSize(20f);
                barDataSet.setBarBorderWidth(0.9f);
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                BarData barData = new BarData(barDataSet);
                XAxis xAxis = barChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                final String[] months = new String[]{"Current", "Target"};
                IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(months);
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(formatter);
                YAxis y = barChart.getAxisLeft();
                double Ycalories = 0;
                if (gCalories > cCalories){
                    Ycalories = gCalories;
                }else if(cCalories > gCalories){
                    Ycalories = cCalories;
                }
                y.setAxisMaximum((float) (Ycalories  + 1000));
                y.setAxisMinimum(0);
                YAxis y1 = barChart.getAxisRight();
                y1.setAxisMaximum((float) (Ycalories  + 1000));
                y1.setAxisMinimum(0);
                barChart.getXAxis().setTextSize(20);
                barChart.getAxisLeft().setTextSize(15);
                barChart.getAxisRight().setTextSize(15);
                barChart.setData(barData);
                barChart.setFitBars(true);
                barChart.animateXY(500, 500);
                barChart.invalidate();

                barChart1 = findViewById(R.id.barChart1);
                BarDataSet barDataSet1 = new BarDataSet(getData1(), "");
                barDataSet1.setValueTextSize(20f);
                barDataSet1.setBarBorderWidth(0.9f);
                barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
                BarData barData1 = new BarData(barDataSet1);
                XAxis xAxis1 = barChart1.getXAxis();
                xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
                final String[] months1 = new String[]{"Current", "Target"};
                IndexAxisValueFormatter formatter1 = new IndexAxisValueFormatter(months1);
                xAxis1.setGranularity(1f);
                xAxis1.setValueFormatter(formatter1);
                YAxis y2 = barChart1.getAxisLeft();
                double Ysteps = 0;
                if (gSteps > cSteps){
                    Ysteps = gSteps;
                }else if(cSteps > gSteps){
                    Ysteps = cSteps;
                }
                y2.setAxisMaximum((float) (Ysteps  + 1000));
                y2.setAxisMinimum(0);
                YAxis y3 = barChart1.getAxisRight();
                y3.setAxisMaximum((float) (Ysteps  + 1000));
                y3.setAxisMinimum(0);
                barChart1.getXAxis().setTextSize(20);
                barChart1.getAxisLeft().setTextSize(15);
                barChart1.getAxisRight().setTextSize(15);
                barChart1.setData(barData1);
                barChart1.setFitBars(true);
                barChart1.animateXY(500, 500);
                barChart1.invalidate();

                barChart2 = findViewById(R.id.barChart2);
                BarDataSet barDataSet2 = new BarDataSet(getData2(), "");
                barDataSet2.setValueTextSize(20f);
                barDataSet2.setBarBorderWidth(0.9f);
                barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);
                BarData barData2 = new BarData(barDataSet2);
                XAxis xAxis2 = barChart2.getXAxis();
                xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
                final String[] months2 = new String[]{"Current", "Target"};
                IndexAxisValueFormatter formatter2 = new IndexAxisValueFormatter(months2);
                xAxis2.setGranularity(1f);
                xAxis2.setValueFormatter(formatter2);
                YAxis y4 = barChart2.getAxisLeft();
                double Yweight = 0;
                if (gWeight > cWeight){
                    Yweight = gWeight;
                }else if(cWeight > gWeight){
                    Yweight = cWeight;
                }
                y4.setAxisMaximum((float) (Yweight  + 10));
                y4.setAxisMinimum(0);
                YAxis y5 = barChart2.getAxisRight();
                y5.setAxisMaximum((float) (Yweight + 10));
                y5.setAxisMinimum(0);
                barChart2.getXAxis().setTextSize(20);
                barChart2.getAxisLeft().setTextSize(15);
                barChart2.getAxisRight().setTextSize(15);
                barChart2.setData(barData2);
                barChart2.setFitBars(true);
                barChart2.animateXY(500, 500);
                barChart2.invalidate();

            }

        });

    }


    public void getDailyChanges() {
        final DocumentReference documentReference2 = fstore.collection("DailyChanges").document(userID);
        documentReference2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                cCalories = documentSnapshot.getLong("Calories Change").intValue();
                cSteps = documentSnapshot.getLong("Steps Change").intValue();
                cWeight = documentSnapshot.getLong("Weight Change").doubleValue();
                //Toast.makeText(UserStatisticsActivity.this,""+cCalories+"",Toast.LENGTH_SHORT).show();

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



            }
        });
    }

}