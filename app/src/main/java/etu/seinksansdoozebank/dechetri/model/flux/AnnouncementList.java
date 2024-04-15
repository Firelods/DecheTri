package etu.seinksansdoozebank.dechetri.model.flux;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnnouncementList extends ArrayList<Announcement> {

    public AnnouncementList() {
        super();
        this.add(new Announcement("1","Collecte des déchets", "La collecte des déchets aura lieu le 12/12/2021.\nliorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec nisl ac nisi tincidunt tincidunt. Donec auctor, nunc nec ultricies ultricies, nunc nisl ultricies elit, nec ultricies elit nisl nec nisl. Nullam nec nisl ac nisi tincidunt tincidunt. Donec auctor, nunc nec ultricies ultricies, nunc nisl ultricies elit, nec ultricies elit nisl nec nisl. Nullam nec nisl ac nisi tincidunt tincidunt. Donec auctor, nunc nec ultricies ultricies, nunc nisl ultricies elit, nec ultricies elit nisl nec nisl.", AnnouncementType.EVENT,new Date()));
        this.add(new Announcement("2","Collecte des déchets",  "La collecte des déchets aura lieu le 19/12/2021.", AnnouncementType.EVENT,new Date()));
        this.add(new Announcement("3","Collecte des déchets",  "La collecte des déchets aura lieu le 26/12/2021.", AnnouncementType.NEWS,new Date()));
        this.add(new Announcement("4","Collecte des déchets",  "La collecte des déchets aura lieu le 02/01/2022.", AnnouncementType.NEWS,new Date()));
        this.add(new Announcement("5","Collecte des déchets",  "La collecte des déchets aura lieu le 09/01/2022.", AnnouncementType.NEWS,new Date()));
        this.add(new Announcement("6","Collecte des déchets",  "La collecte des déchets aura lieu le 16/01/2022.", AnnouncementType.NEWS,new Date()));
        this.add(new Announcement("7","Collecte des déchets",  "La collecte des déchets aura lieu le 23/01/2022.", AnnouncementType.EVENT,new Date()));
        this.add(new Announcement("8","Collecte des déchets",  "La collecte des déchets aura lieu le 30/01/2022.", AnnouncementType.EVENT,new Date()));
        this.add(new Announcement("9","Collecte des déchets",  "La collecte des déchets aura lieu le 06/02/2022.", AnnouncementType.EVENT,new Date()));
    }
}
