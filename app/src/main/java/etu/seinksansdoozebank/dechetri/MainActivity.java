package etu.seinksansdoozebank.dechetri;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
        String defaultRole = getResources().getString(R.string.role_user_title); //user by default
        String role = sharedPreferences.getString(getString(R.string.shared_preferences_key_role), defaultRole);

        navView.getMenu().clear(); //user menu by default
        navView.inflateMenu(R.menu.menu_item_flux);
        navView.inflateMenu(R.menu.menu_item_map);
        navView.inflateMenu(R.menu.menu_item_report);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder( //user configuration by default
                R.id.navigation_flux, R.id.navigation_map, R.id.navigation_report)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        if (role.equals(getString(R.string.role_admin_title))) {
            navView.inflateMenu(R.menu.menu_item_statistics);
            navController.setGraph(R.navigation.mobile_navigation_admin);
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_flux, R.id.navigation_map, R.id.navigation_report, R.id.navigation_statistics)
                    .build();
        } else if (role.equals(getString(R.string.role_user_title))) {
            navController.setGraph(R.navigation.mobile_navigation_user);
        } else if (role.equals(getString(R.string.role_manager_title))) {
            navView.inflateMenu(R.menu.menu_item_task_list);
            navView.inflateMenu(R.menu.menu_item_statistics);
            navView.setSelectedItemId(R.id.navigation_taskList);
            navController.setGraph(R.navigation.mobile_navigation_manager);
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_taskList, R.id.navigation_map, R.id.navigation_report, R.id.navigation_statistics, R.id.navigation_flux)
                    .build();
        } else if (role.equals(getString(R.string.role_employee_title))) {
            navView.inflateMenu(R.menu.menu_item_task_list);
            navController.setGraph(R.navigation.mobile_navigation_employee);
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_taskList, R.id.navigation_map, R.id.navigation_report, R.id.navigation_flux)
                    .build();
        }

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}
