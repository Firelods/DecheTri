package etu.seinksansdoozebank.dechetri;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RoleChoiceActivity extends AppCompatActivity {

    Button userButton;
    Button employeeButton;
    Button managerButton;
    Button adminButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_choice);
        userButton = findViewById(R.id.btnUser);
        employeeButton = findViewById(R.id.btnEmployee);
        managerButton = findViewById(R.id.btnManager);
        adminButton = findViewById(R.id.btnAdmin);
        userButton.setOnClickListener(v -> launchApp(getString(R.string.role_user_title)));
        employeeButton.setOnClickListener(v -> launchApp(getString(R.string.role_employee_title)));
        managerButton.setOnClickListener(v -> launchApp(getString(R.string.role_manager_title)));
        adminButton.setOnClickListener(v -> launchApp(getString(R.string.role_admin_title)));
    }

    private void launchApp(String role) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.shared_preferences_key_role), role);
        editor.apply();

        // Verify that the role has been saved
        String savedRole = sharedPreferences.getString(getString(R.string.shared_preferences_key_role), "");
        if (role.equals(savedRole)) {
            Log.d("RoleChoiceActivity", "Role saved: " + role);
        } else {
            Log.d("RoleChoiceActivity", "Failed to save role");
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}