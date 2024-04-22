package etu.seinksansdoozebank.dechetri.model.flux;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnnouncementList extends ArrayList<Announcement> {

    public AnnouncementList() {
        super();
        this.add(new Announcement("0","Collecte de déchets", "Collecte de déchets le 12/12/2020 Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat", AnnouncementType.NEWS, new Date()));
        this.add(new Announcement("1","Collecte de déchets", "Collecte de déchets le 12/12/2020", AnnouncementType.NEWS, new Date()));
        this.add(new Announcement("2","Collecte de déchets", "Collecte de déchets le 12/12/2020", AnnouncementType.NEWS, new Date()));
        this.add(new Announcement("3","Collecte de déchets", "Collecte de déchets le 12/12/2020", AnnouncementType.NEWS, new Date()));
        this.add(new Announcement("4","Collecte de déchets", "Collecte de déchets le 12/12/2020", AnnouncementType.NEWS, new Date()));
        this.add(new Announcement("5","Event", "Event le 12/12/2020", AnnouncementType.EVENT, new Date()));
        this.add(new Announcement("6","Event", "Event le 12/12/2020", AnnouncementType.EVENT, new Date()));
        this.add(new Announcement("7","Event", "Event le 12/12/2020", AnnouncementType.EVENT, new Date()));
        this.add(new Announcement("8","Event", "Event le 12/12/2020", AnnouncementType.EVENT, new Date()));
    }
}
