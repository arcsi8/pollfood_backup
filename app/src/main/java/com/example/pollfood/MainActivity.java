
package com.example.pollfood;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.Manifest;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pollfood.Base.BaseActivity;
import com.example.pollfood.Classes.Users;
import com.example.pollfood.Service.NotificationJobService;
import com.example.pollfood.databinding.ActivityLoginBinding;
import com.example.pollfood.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

public class MainActivity extends BaseActivity {

    Fragment activeFragment = null;
    ActivityMainBinding binding;
    private static final int NOTIFICATION_PERMISSION_CODE = 100;
    ProfileFragment profileFragment = new ProfileFragment();
    PollFragment pollFragment = new PollFragment();
    NoFamilyFragment nofamilyFragment = new NoFamilyFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "pollfood",                    // ID
                    "pollfood notifications",         // Name
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel for app notifications");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }


        //String username = mAuth.getCurrentUser().getDisplayName().toString();

        if(savedInstanceState==null){

            getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainerMain.getId(),new ProfileFragment()).commit();
            binding.bottomNavigation.setSelectedItemId(R.id.navigation_profile);

        }



        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected = null;
                var selectedmenuitem = item.getItemId();

                if (selectedmenuitem == R.id.navigation_profile){
                    selected = profileFragment;


                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"pollfood")
                            .setSmallIcon(R.drawable.recipes)
                            .setContentTitle("Pollfood")
                            .setContentText("Ideje csatlakozni egy családhoz!")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Permission not granted — request it
                            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
                            return false;
                        }
                    }
                    notificationManager.notify(1001, builder.build());
                } else if (selectedmenuitem == R.id.navigation_poll) {
                    selected = pollFragment;
                } else if (selectedmenuitem == R.id.navigation_family) {
                    selected = nofamilyFragment;
                    checkAndScheduleJob();
                }
                if (selected != null){
                    loadFragment(selected);
                    return true;
                }
                return false;
            }
        });


       // binding.maindemo.setText(username);

      /*  binding.logoutButton.setOnClickListener(l -> {

            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });*/

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }
    private void checkAndScheduleJob() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
                return;
            }
        }
        scheduleNotificationJob();
    }
    private void scheduleNotificationJob() {
        ComponentName componentName = new ComponentName(this, NotificationJobService.class);

        JobInfo.Builder builder = new JobInfo.Builder(101, componentName)
                .setMinimumLatency(10 * 1000) // delay 10 seconds
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE) // no network needed
                .setRequiresCharging(false);

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int result = jobScheduler.schedule(builder.build());

        if (result == JobScheduler.RESULT_SUCCESS) {
            Toast.makeText(this, "Notification job scheduled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to schedule notification job", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scheduleNotificationJob();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void loadFragment(Fragment fragment) {
        Log.d("debug", "debug4");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(binding.fragmentContainerMain.getId(), fragment);
        // fragmentTransaction.addToBackStack(null); // Optional: if you want back navigation between fragments
        fragmentTransaction.commit();
    }

    public void logout_user(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        FirebaseAuth.getInstance().signOut();

        Users.clearInstance();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void init_user(FirebaseFirestore db){

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            String uid = user.getUid();
            DocumentReference docref = db.collection("users").document(uid);
            docref.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()){
                    String email = documentSnapshot.getString("email");
                    String username = documentSnapshot.getString("username");
                    String fname = documentSnapshot.getString("fname");
                    String sname = documentSnapshot.getString("sname");
                    Long familyid = documentSnapshot.getLong("familyid");

                    Users singleton_user = Users.getInstance(uid,email,username);
                    if (fname != null){
                        singleton_user.setFirstname(fname);
                    }
                    if (sname != null){
                        singleton_user.setSecondname(sname);
                    }
                    if (familyid != null){
                        singleton_user.setFamilyid(familyid);
                    }
                    Log.d("users single instance", "kesz");
                    Log.d("users single instance", singleton_user.toString());
                }else {
                    Log.d("Firestore", "No such document");
                }
            }).addOnFailureListener(e ->{
                Log.w("Firestore", "Error fetching document", e);
            });
        }

    }
}