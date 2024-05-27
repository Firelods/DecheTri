package etu.seinksansdoozebank.dechetri;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
        userButton.setOnClickListener(v -> launchApp(getString(R.string.role_user_title), getString(R.string.role_user_id)));
        employeeButton.setOnClickListener(v -> launchApp(getString(R.string.role_employee_title), getString(R.string.role_employee_id)));
        managerButton.setOnClickListener(v -> launchApp(getString(R.string.role_manager_title), getString(R.string.role_manager_id)));
        adminButton.setOnClickListener(v -> launchApp(getString(R.string.role_admin_title), getString(R.string.role_admin_id)));
    }

    private void launchApp(String role, String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.shared_preferences_key_role), role);
        editor.putString(getString(R.string.shared_preferences_key_user_id), userId);
        editor.apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}