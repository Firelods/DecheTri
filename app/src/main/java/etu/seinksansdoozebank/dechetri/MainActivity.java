package etu.seinksansdoozebank.dechetri;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import etu.seinksansdoozebank.dechetri.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private MenuItem addAnnouncementMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configNavigation();
    }

    public void configNavigation() {
        BottomNavigationView bottomNavView = findViewById(R.id.nav_view);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
        String defaultRole = getResources().getString(R.string.role_user_title); //user by default
        String role = sharedPreferences.getString(getString(R.string.shared_preferences_key_role), defaultRole);
//
        bottomNavView.getMenu().clear(); //user menu by default
        bottomNavView.inflateMenu(R.menu.menu_item_flux);
        bottomNavView.inflateMenu(R.menu.menu_item_map);
        bottomNavView.inflateMenu(R.menu.menu_item_report);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (addAnnouncementMenuItem != null) {
                if (destination.getId() == R.id.navigation_flux) {
                    addAnnouncementMenuItem.setVisible(true);
                } else {
                    addAnnouncementMenuItem.setVisible(false);
                }
            }
        });

        if (role.equals(getString(R.string.role_admin_title))) {
            bottomNavView.inflateMenu(R.menu.menu_item_statistics);
            navController.setGraph(R.navigation.nav_admin);
        } else if (role.equals(getString(R.string.role_user_title))) {
            navController.setGraph(R.navigation.nav_user);
        } else if (role.equals(getString(R.string.role_manager_title))) {
            bottomNavView.inflateMenu(R.menu.menu_item_task_list);
            bottomNavView.inflateMenu(R.menu.menu_item_statistics);
            navController.setGraph(R.navigation.nav_manager);
        } else if (role.equals(getString(R.string.role_employee_title))) {
            bottomNavView.inflateMenu(R.menu.menu_item_task_list);
            navController.setGraph(R.navigation.nav_employee);
        }

        NavigationUI.setupWithNavController(bottomNavView, navController);
        List<Integer> menuIds = new ArrayList<>();
        for (int i = 0; i < bottomNavView.getMenu().size(); i++) {
            menuIds.add(bottomNavView.getMenu().getItem(i).getItemId());
        }
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(menuIds.stream().mapToInt(i -> i).toArray()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        addAnnouncementMenuItem = menu.findItem(R.id.add_announcement);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            navController.navigateUp();
            return true;
        } else if (item.getItemId() == R.id.navigation_disconnect) {
            disconnect();
        }
        return super.onOptionsItemSelected(item);
    }

    private void disconnect() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getString(R.string.shared_preferences_key_role));
        editor.apply();
        finish();
    }
}
