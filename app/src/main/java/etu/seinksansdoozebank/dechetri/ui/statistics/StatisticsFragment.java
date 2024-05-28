package etu.seinksansdoozebank.dechetri.ui.statistics;




import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.ValueLineChart;

import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;


import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;

import java.util.Comparator;

import java.util.List;
import java.util.stream.Collectors;

import etu.seinksansdoozebank.dechetri.R;

import etu.seinksansdoozebank.dechetri.databinding.FragmentStatisticsBinding;
import etu.seinksansdoozebank.dechetri.model.flux.Announcement;
import etu.seinksansdoozebank.dechetri.model.flux.AnnouncementList;
import etu.seinksansdoozebank.dechetri.model.flux.AnnouncementListObserver;
import etu.seinksansdoozebank.dechetri.model.flux.AnnouncementType;
import etu.seinksansdoozebank.dechetri.model.waste.Waste;
import etu.seinksansdoozebank.dechetri.model.waste.WasteList;
import etu.seinksansdoozebank.dechetri.model.waste.WasteListObserver;


public class StatisticsFragment extends Fragment implements AnnouncementListObserver, WasteListObserver {

    private FragmentStatisticsBinding binding;
    private AnnouncementList announcementList;
    private WasteList wasteList;

    private final static String TAG = "emma";
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    public ArrayList<Integer> colorString = new ArrayList<>();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        colorString.add(getResources().getColor(R.color.orange_600, null));
        colorString.add(getResources().getColor(R.color.green_700, null));
        Log.d(TAG, "onCreateView: "+colorString);
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(announcementList::updateList);
        swipeRefreshLayout.setOnRefreshListener(wasteList::updateList);
        return view;
    }


    /**
     * Build a pie chart with all the annoucements created sorted by the type : news and events
     *
     * @param view        view
     * @param colorString tab of colors
     */
    public void buildPieChart(@NonNull View view, @NonNull ArrayList<Integer> colorString) {
        PieChart mPieChart = view.findViewById(R.id.piechart);
        mPieChart.clearChart();
        int newsAnnoucement = 0;
        int eventAnnoucement = 0;


        for (Announcement announcement : announcementList) {
            if (announcement.getType().equals(AnnouncementType.NEWS)) {
                newsAnnoucement++;
            } else {
                eventAnnoucement++;
            }
        }

        int[] stats = {newsAnnoucement, eventAnnoucement};
        for (AnnouncementType announcementType : AnnouncementType.values()) {
            Log.d(TAG, "buildPieChart: "+colorString.get(announcementType.ordinal()));
            mPieChart.addPieSlice(new PieModel(announcementType.getName(), stats[announcementType.ordinal()],colorString.get(announcementType.ordinal())));
        }

        mPieChart.startAnimation();
    }


    public void buildLineChart(@NonNull View view) {

        ValueLineChart mCubicValueLineChart = view.findViewById(R.id.cubiclinechart);
        ValueLineSeries series = new ValueLineSeries();
        mCubicValueLineChart.clearChart();
        series.setColor(getResources().getColor(R.color.green_700, null));



        int numberOfWasteThisDay = 1;
        int colorChosen = 0;

        //On trie la liste des déchets dans l'ordre croissant
        List<Waste> sortedList = wasteList.stream()
                .sorted(Comparator.comparing(Waste::getReportDate))
                .collect(Collectors.toList());

        Waste previous = sortedList.get(0);
        int listSize = sortedList.size();

        for (int i = 1; i < listSize; i++) {

            //Initialisation des dates pour les comparer
            Calendar previousDate = Calendar.getInstance();
            previousDate.setTime(previous.getReportDate());
            int previousDay = previousDate.get(Calendar.DAY_OF_MONTH);
            int previousMonth = previousDate.get(Calendar.MONTH);
            int previousYear = previousDate.get(Calendar.YEAR);
            Calendar currentDate = Calendar.getInstance();
            currentDate.setTime(sortedList.get(i).getReportDate());
            int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
            int currentMonth = currentDate.get(Calendar.MONTH);
            int currentYear = currentDate.get(Calendar.YEAR);

            //Formattage de la date en JJ/MM/AAAA pour affichage
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dateFormatted = sdf.format(previous.getReportDate());

            if (previousDay != currentDay || previousMonth != currentMonth || previousYear != currentYear) {
                previous = sortedList.get(i);
                series.addPoint(new ValueLinePoint(dateFormatted, numberOfWasteThisDay));
                numberOfWasteThisDay = 1;
            } else {
                numberOfWasteThisDay++;
            }
            if (i == listSize - 1) {
                series.addPoint(new ValueLinePoint(dateFormatted, numberOfWasteThisDay));
            }
        }
        mCubicValueLineChart.addSeries(series);
        mCubicValueLineChart.startAnimation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        announcementList = new AnnouncementList(getActivity(), getContext());
        announcementList.addObserver(this);
        wasteList = new WasteList(getActivity());
        wasteList.addObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        announcementList.updateList();
        wasteList.updateList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        announcementList.removeObserver(this);
        wasteList.removeObserver(this);
    }

    @Override
    public void onAnnouncementListChanged() {
        requireActivity().runOnUiThread(() -> {
            Log.d(TAG + "onAnnouncementListChanged", "emma annoucement" + announcementList);
            swipeRefreshLayout.setRefreshing(false);
            if (!announcementList.isEmpty()) {
                buildPieChart(view, colorString);
            }
        });
    }


    @Override
    public void onWasteListChanged() {
        requireActivity().runOnUiThread(() -> {
            swipeRefreshLayout.setRefreshing(false);
            if (!wasteList.isEmpty()) {
                buildLineChart(view);
            }
        });
    }
}