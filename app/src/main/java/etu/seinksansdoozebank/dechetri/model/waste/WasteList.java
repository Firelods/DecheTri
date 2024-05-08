package etu.seinksansdoozebank.dechetri.model.waste;

import java.util.ArrayList;
import java.util.Date;


public class WasteList extends ArrayList<Waste> {
    public WasteList() {
        add(new Waste(
                "1",
                "Plastic Bottle",
                WasteType.GREEN,
                "Empty plastic bottle found on the roadside.",
                null, // Assuming no image data for this example
                new Date(),
                "123 Main St, City, Country",
                43.568649561971746,
                7.118045347165924,
                "user123"
        ));

        add(new Waste(
                "2",
                "Paper Wrapper",
                WasteType.HAZARDOUS,
                "Discarded paper wrapper from a snack.",
                null, // Assuming no image data for this example
                new Date(124, 4, 6),
                "456 Elm St, City, Country",
                43.5700324777817,
                7.114682674407959,
                "user456"
        ));

        add(new Waste(
                "3",
                "Glass Bottle",
                WasteType.HOUSEHOLD,
                "Broken glass bottle found in the park.",
                null, // Assuming no image data for this example
                new Date(124, 4, 7),
                "789 Oak St, City, Country",
                43.57135770627582,
                7.116297721862793,
                "user789"
        ));
        add(new Waste(
                "4",
                "Plastic Bottle",
                WasteType.INDUSTRIAL,
                "Empty plastic bottle found on the roadside.",
                null, // Assuming no image data for this example
                new Date(),
                "123 Main St, City, Country",
                43.568649561971746,
                7.118045347165924,
                "user123"
        ));
        add(new Waste(
                "5",
                "Paper Wrapper",
                WasteType.OTHER,
                "Discarded paper wrapper from a snack.",
                null, // Assuming no image data for this example
                new Date(),
                "456 Elm St, City, Country",
                43.5700324777817,
                7.114682674407959,
                "user456"
        ));

    }

}
