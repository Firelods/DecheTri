package etu.seinksansdoozebank.dechetri;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RoleChoiceActivity extends AppCompatActivity {

    Button userButton;
    Button employeeButton;
    Button managerButton;
    Button adminButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_role_choice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userButton = findViewById(R.id.btnUser);
        employeeButton = findViewById(R.id.btnEmployee);
        managerButton = findViewById(R.id.btnManager);
        adminButton = findViewById(R.id.btnAdmin);
        userButton.setOnClickListener(v -> launchApp("user"));
        employeeButton.setOnClickListener(v -> launchApp("employee"));
        managerButton.setOnClickListener(v -> launchApp("manager"));
        adminButton.setOnClickListener(v -> launchApp("admin"));
    }

    private void launchApp(String role) {
        //add role to shared preferences
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.shared_preferences_key_role), role);
        editor.apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}