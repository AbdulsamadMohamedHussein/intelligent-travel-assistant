package com.example.travelbackend.model;

/** Null Object pattern: used when no real place is left, so the plan never contains null. */
public class FreeTimePlace extends Place {

    public FreeTimePlace(String cityName, TimeSlot slot) {
        super("Free time", cityName, 0, "Flexible", slot, "", describe(cityName, slot), Integer.MAX_VALUE);
    }

    private static String describe(String cityName, TimeSlot slot) {
        return switch (slot) {
            case MORNING -> "Sleep in, enjoy a slow breakfast or revisit your favourite spot in " + cityName + ".";
            case EVENING -> "Enjoy a relaxed dinner at a local restaurant of your choice.";
            default -> "Relax, shop for souvenirs or explore local cafes and neighbourhoods in " + cityName + ".";
        };
    }

    @Override
    public CostCategory getCostCategory() {
        return CostCategory.NONE;
    }

    @Override
    public String describeCost() {
        return "No cost";
    }
}
