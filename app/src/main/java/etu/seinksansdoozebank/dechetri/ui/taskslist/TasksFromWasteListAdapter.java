package etu.seinksansdoozebank.dechetri.ui.taskslist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.model.waste.Waste;
import etu.seinksansdoozebank.dechetri.ui.wastemap.WasteDialogFragment;
import etu.seinksansdoozebank.dechetri.ui.wastemap.WasteDialogListener;

public class TasksFromWasteListAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;  //Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    private final FragmentActivity activity;
    private final List<Waste> wasteList;

    private WasteDialogListener wasteDialogListener;

    public TasksFromWasteListAdapter(WasteDialogListener wasteDialogListener, FragmentActivity activity, List<Waste> wasteList) {
        this.wasteDialogListener = wasteDialogListener;
        this.activity = activity;
        this.wasteList = wasteList;
        this.mInflater = LayoutInflater.from(activity.getBaseContext());
    }

    @Override
    public int getCount() {
        return wasteList.size();
    }

    @Override
    public Object getItem(int i) {
        return wasteList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem;

        // (1) : Réutilisation des layouts
        listItem = convertView == null ? mInflater.inflate(R.layout.item_taskslist, parent, false) : convertView;
        listItem.setElevation(5);

        if (!wasteList.isEmpty()) {
            // (2) : Récupération des TextView de notre layout
            TextView title = listItem.findViewById(R.id.taskList_title);
            ImageView image = listItem.findViewById(R.id.taskList_image);
            Waste waste = this.wasteList.get(position);

            title.setText(waste.getName());
            byte[] imageData = waste.getImageData();
            Bitmap bitmap = imageData != null ? new BitmapDrawable(activity.getResources(), Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length), 100, 100, true)).getBitmap() : null;
            Bitmap roundedBitMap = bitmap != null ? getRoundedBitmap(bitmap) : null;
            image.setImageBitmap(roundedBitMap);
            listItem.setOnClickListener(v -> showWasteDetails(waste));
        }

        return listItem;
    }

    public void showWasteDetails(Waste waste) {
        WasteDialogFragment wasteDialogFragment = new WasteDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable("waste", waste);

        wasteDialogFragment.setArguments(args);

        wasteDialogFragment.setWasteDialogListener(this.wasteDialogListener);

        wasteDialogFragment.show(activity.getSupportFragmentManager(), "WasteDialogFragment");
    }

    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        int diameter = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint);

        return output;
    }
}
