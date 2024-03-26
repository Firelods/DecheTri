package etu.seinksansdoozebank.dechetri;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import etu.seinksansdoozebank.dechetri.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.nav_view);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
        String defaultRole = getResources().getString(R.string.role_user_title); //user by default
        String role = sharedPreferences.getString(getString(R.string.shared_preferences_key_role), defaultRole);

        navView.getMenu().clear(); //user menu by default
        navView.inflateMenu(R.menu.menu_item_flux);
        navView.inflateMenu(R.menu.menu_item_map);
        navView.inflateMenu(R.menu.menu_item_report);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        if (role.equals(getString(R.string.role_admin_title))) {
            navView.inflateMenu(R.menu.menu_item_statistics);
            navController.setGraph(R.navigation.mobile_navigation_admin);
        } else if (role.equals(getString(R.string.role_user_title))) {
            navController.setGraph(R.navigation.mobile_navigation_user);
        } else if (role.equals(getString(R.string.role_manager_title))) {
            navView.inflateMenu(R.menu.menu_item_task_list);
            navView.inflateMenu(R.menu.menu_item_statistics);
            navView.setSelectedItemId(R.id.navigation_taskList);
            navController.setGraph(R.navigation.mobile_navigation_manager);
        } else if (role.equals(getString(R.string.role_employee_title))) {
            navView.inflateMenu(R.menu.menu_item_task_list);
            navController.setGraph(R.navigation.mobile_navigation_employee);
        }

        navView.setOnItemSelectedListener(item -> {
            Log.d("MainActivity", "onNavigationItemSelected: " + item.getTitle());
            getSupportActionBar().setTitle(item.getTitle());
            return true;
        });
        navView.setSelectedItemId(navView.getMenu().getItem(0).getItemId());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.navigation_disconnect) {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(getString(R.string.shared_preferences_key_role));
            editor.apply();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}