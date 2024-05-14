package etu.seinksansdoozebank.dechetri.ui.statistics;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;

import java.text.SimpleDateFormat;

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


public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private String TAG="emma";
    private AnnouncementList announcementList;
    private List<AnnouncementListObserver> observers;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        String[] colorString = {"#FE6DA8", "#56B7F1", "#CDA67F", "#FED70E", "#FE6DA8", "#56B7F1", "#CDA67F", "#FED70E"};

        buildPieChart(view, colorString);
        buildBarChart(view, colorString);
        return view;
        }


    /**
     * Build a pie chart with all the annoucements created sorted by the type : news and events
     * @param view view
     * @param colorString tab of colors
     */
        public void buildPieChart(@NonNull View view, @NonNull String[] colorString){
            PieChart mPieChart = (PieChart) view.findViewById(R.id.piechart);
            int newsAnnoucement = 0;
            int eventAnnoucement = 0;

            announcementList = new AnnouncementList(requireActivity(),getContext());
            Log.d(TAG, "buildPieChart: "    + announcementList);
            for (Announcement announcement :announcementList) {
                if (announcement.getType().equals(AnnouncementType.NEWS)) {
                    newsAnnoucement++;
                } else {
                    eventAnnoucement++;
                }
            }

            int[] stats = {newsAnnoucement, eventAnnoucement};
            for (AnnouncementType announcementType : AnnouncementType.values()) {
                mPieChart.addPieSlice(new PieModel(announcementType.getName(), stats[announcementType.ordinal()], Color.parseColor(colorString[announcementType.ordinal()])));
            }

            mPieChart.startAnimation();
        }

    /**
     * Build a bar chart with all the wastes created sorted by their date
     * @param view view
     * @param colorString tab of colors
     */
        public void buildBarChart(@NonNull View view, @NonNull String[] colorString){
            BarChart mBarChart = (BarChart) view.findViewById(R.id.barchart);

            int numberOfWasteThisDay=1;
            int colorChosen=0;

            WasteList wasteList = new WasteList();
            Log.d(TAG, "buildBarChart: "+wasteList);
            //On trie la liste des déchets dans l'ordre croissant
            List<Waste> sortedList = wasteList.stream()
                    .sorted(Comparator.comparing(Waste::getReportDate))
                    .collect(Collectors.toList());

            Waste previous=sortedList.get(0);
            int listSize=sortedList.size();

            for (int i = 1; i < listSize; i++){

                //Initialisation des dates pour les comparer
                Calendar previousDate= Calendar.getInstance();
                previousDate.setTime(previous.getReportDate());
                int previousDay=previousDate.get(Calendar.DAY_OF_MONTH);
                int previousMonth=previousDate.get(Calendar.MONTH);
                int previousYear=previousDate.get(Calendar.YEAR);
                Calendar currentDate= Calendar.getInstance();
                currentDate.setTime(sortedList.get(i).getReportDate());
                int currentDay=currentDate.get(Calendar.DAY_OF_MONTH);
                int currentMonth=currentDate.get(Calendar.MONTH);
                int currentYear=currentDate.get(Calendar.YEAR);

                //Formattage de la date en JJ/MM/AAAA pour affichage
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dateFormatted = sdf.format(previous.getReportDate());

                if(previousDay!=currentDay || previousMonth!=currentMonth || previousYear!=currentYear){
                    previous=sortedList.get(i);
                    mBarChart.addBar(new BarModel(dateFormatted, numberOfWasteThisDay, Color.parseColor(colorString[colorChosen++])));
                    numberOfWasteThisDay=1;
                }else{
                    numberOfWasteThisDay++;
                }
                if(i==listSize-1){
                    mBarChart.addBar(new BarModel(dateFormatted, numberOfWasteThisDay, Color.parseColor(colorString[colorChosen++])));
                }
            }
            mBarChart.startAnimation();

        }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




    public void addObserver(AnnouncementListObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(AnnouncementListObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (AnnouncementListObserver observer : observers) {
            observer.onAnnouncementListChanged();
        }
    }
}