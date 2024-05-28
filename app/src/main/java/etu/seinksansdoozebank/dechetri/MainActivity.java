package etu.seinksansdoozebank.dechetri;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
import etu.seinksansdoozebank.dechetri.model.user.Role;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private Role role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configNavigation();
        setupKeyboardResize();
    }

    private void setupKeyboardResize() {
        BottomNavigationView bottomNavView = findViewById(R.id.nav_view);
        final View rootView = findViewById(R.id.container);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;
            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is enough to determine keypad height.
                // Keyboard is opened
                bottomNavView.setVisibility(View.GONE);
            } else {
                // Keyboard is closed
                bottomNavView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void configNavigation() {
        BottomNavigationView bottomNavView = findViewById(R.id.nav_view);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
        String defaultRole = getResources().getString(R.string.role_user_title); //user by default
        role = Role.fromString(sharedPreferences.getString(getString(R.string.shared_preferences_key_role), defaultRole));

        bottomNavView.getMenu().clear(); //user menu by default
        bottomNavView.inflateMenu(R.menu.menu_item_flux);
        bottomNavView.inflateMenu(R.menu.menu_item_map);
        bottomNavView.inflateMenu(R.menu.menu_item_report);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        if (role.equals(Role.ADMIN)) {
            navController.setGraph(R.navigation.nav_admin);
        } else if (role.equals(Role.USER)) {
            navController.setGraph(R.navigation.nav_user);
        } else if (role.equals(Role.MANAGER)) {
            bottomNavView.inflateMenu(R.menu.menu_item_personnal_task_list);
            bottomNavView.inflateMenu(R.menu.menu_item_unassigned_task_list);
            navController.setGraph(R.navigation.nav_manager);
        } else if (role.equals(Role.EMPLOYEE)) {
            bottomNavView.inflateMenu(R.menu.menu_item_personnal_task_list);
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
        MenuItem statisticMenuItem = menu.findItem(R.id.navigation_statistics);
        statisticMenuItem.setVisible(role.canSeeStats());
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> statisticMenuItem.setVisible(role.canSeeStats() && destination.getId() != R.id.navigation_statistics));
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
        } else if (item.getItemId() == R.id.navigation_statistics) {
            navController.navigate(R.id.navigation_statistics);
        }
        return super.onOptionsItemSelected(item);
    }

    private void disconnect() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getString(R.string.shared_preferences_key_role));
        editor.remove(getString(R.string.shared_preferences_key_user_id));
        editor.apply();
        finish();
    }
}
