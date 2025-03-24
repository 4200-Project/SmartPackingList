package com.example.a4200project;

import java.util.*;

public class ItemSuggester {

    private static final Map<String, List<String>> tripTypeMap = new HashMap<>();
    private static final Map<String, List<String>> destinationMap = new HashMap<>();
    private static final double THRESHOLD = 0.7; // Jaro–Winkler match threshold

    static {
        // ==============================
        //         TRIP TYPES
        // (All the defined categories, plus fallback "other")
        // ==============================

        // 1) Fallback "other"
        tripTypeMap.put("other", Arrays.asList(
                "Comfortable Clothing",
                "Chargers",
                "Basic Toiletries"
        ));

        // 2) Beach
        tripTypeMap.put("beach", Arrays.asList(
                "Swimsuit",
                "Sunscreen",
                "Flip-flops",
                "Beach Towel",
                "Beach Umbrella",
                "Sunglasses",
                "Waterproof Phone Case"
        ));

        // 3) Hiking
        tripTypeMap.put("hiking", Arrays.asList(
                "Hiking Boots",
                "Water Bottle",
                "First Aid Kit",
                "Backpack",
                "Trail Snacks",
                "Map or GPS",
                "Hiking Poles",
                "Rain Jacket"
        ));

        // 4) Business
        tripTypeMap.put("business", Arrays.asList(
                "Formal Attire",
                "Laptop",
                "Chargers",
                "Business Cards",
                "Dress Shoes",
                "Notepad & Pen",
                "Laptop Bag / Briefcase"
        ));

        // 5) Camping
        tripTypeMap.put("camping", Arrays.asList(
                "Tent",
                "Sleeping Bag",
                "Flashlight or Headlamp",
                "Insect Repellent",
                "Portable Stove",
                "Warm Clothing",
                "Fire Starters / Lighter",
                "Trash Bags"
        ));

        // 6) Cruise
        tripTypeMap.put("cruise", Arrays.asList(
                "Casual Clothing",
                "Formal Outfit (for dinners)",
                "Motion Sickness Medicine",
                "Swimsuit",
                "Flip-flops",
                "Passport/ID",
                "Small Daypack for Excursions"
        ));

        // 7) Ski (also used for "snowboard")
        tripTypeMap.put("ski", Arrays.asList(
                "Ski Jacket",
                "Ski Pants",
                "Gloves",
                "Ski Goggles",
                "Thermal Base Layers",
                "Warm Hat",
                "Neck Gaiter or Scarf"
        ));
        // Alias "snowboard" to the same list
        tripTypeMap.put("snowboard", tripTypeMap.get("ski"));

        // 8) Road Trip
        tripTypeMap.put("road trip", Arrays.asList(
                "Driver’s License",
                "Snacks & Drinks",
                "Car Charger & Phone Mount",
                "Roadside Emergency Kit",
                "Music Playlist / Podcasts",
                "Physical or Offline Map",
                "Cash for Tolls"
        ));

        // 9) Backpacking
        tripTypeMap.put("backpacking", Arrays.asList(
                "Lightweight Backpack",
                "Quick-Dry Clothing",
                "Multi-Tool (Swiss Army Knife)",
                "Travel Towel",
                "Portable Charger",
                "Compression Sacks",
                "Hostel Lock"
        ));

        // 10) Theme Park
        tripTypeMap.put("theme park", Arrays.asList(
                "Comfortable Shoes",
                "Small Backpack or Sling Bag",
                "Sunscreen",
                "Hat or Cap",
                "Water Bottle",
                "Portable Charger",
                "Poncho (water rides)"
        ));

        // 11) Safari
        tripTypeMap.put("safari", Arrays.asList(
                "Neutral-Colored Clothing",
                "Binoculars",
                "Camera with Zoom Lens",
                "Insect Repellent",
                "Sunscreen",
                "Hat or Cap",
                "Sturdy Closed-Toe Shoes"
        ));

        // 12) Romantic Getaway
        tripTypeMap.put("romantic", Arrays.asList(
                "Nice Dinner Outfit",
                "Comfortable Yet Stylish Shoes",
                "Small Gift or Surprise",
                "Candles / Portable Diffuser"
        ));

        // 13) Family Vacation
        tripTypeMap.put("family", Arrays.asList(
                "Games / Activities for Kids",
                "Child-Friendly Snacks",
                "Wet Wipes / Tissues",
                "Kid-Specific Medications",
                "Portable Entertainment (Tablet, Books)"
        ));

        // 14) Photography Trip
        tripTypeMap.put("photography", Arrays.asList(
                "Camera & Lenses",
                "Extra Memory Cards",
                "Tripod",
                "Camera Batteries & Charger",
                "Lens Cleaning Kit"
        ));

        // ==============================
        //       DESTINATIONS
        // (All the defined locations, plus fallback "other")
        // ==============================

        // 1) Fallback "other"
        destinationMap.put("other", Arrays.asList(
                "Universal Travel Adapter",
                "Comfortable Shoes",
                "Umbrella (Weather Varies)"
        ));

        // 2) Bali
        destinationMap.put("bali", Arrays.asList(
                "Sarong",
                "Travel Adapter",
                "Lightweight Clothing",
                "Insect Repellent"
        ));

        // 3) Switzerland
        destinationMap.put("switzerland", Arrays.asList(
                "Warm Jacket",
                "Travel Adapter (Type J)",
                "Swiss Franc or Travel Card",
                "Sturdy Walking Shoes"
        ));

        // 4) Japan
        destinationMap.put("japan", Arrays.asList(
                "Travel Adapter (Type A/B)",
                "Pocket Wi-Fi or SIM Card",
                "Comfortable Walking Shoes",
                "Reusable Chopsticks",
                "Phrasebook / Translation App"
        ));

        // 5) New York
        destinationMap.put("new york", Arrays.asList(
                "Comfortable Walking Shoes",
                "MetroCard / Contactless Payment",
                "Layered Clothing",
                "Tote Bag (for shopping)"
        ));

        // 6) Hawaii
        destinationMap.put("hawaii", Arrays.asList(
                "Swimsuit",
                "Sunscreen",
                "Beach Bag",
                "Flip-flops",
                "Snorkeling Gear (optional)"
        ));

        // 7) Australia
        destinationMap.put("australia", Arrays.asList(
                "Wide-Brimmed Hat",
                "High-SPF Sunscreen",
                "Travel Adapter (Type I)",
                "Insect Repellent",
                "Reusable Water Bottle"
        ));

        // 8) Canada
        destinationMap.put("canada", Arrays.asList(
                "Warm Coat (in winter)",
                "Gloves & Scarf (in winter)",
                "Travel Adapter (Type A/B)",
                "Hiking Boots (if outdoors)",
                "Umbrella (west coast)"
        ));

        // 9) London
        destinationMap.put("london", Arrays.asList(
                "Travel Adapter (Type G)",
                "Umbrella or Raincoat",
                "Comfortable Walking Shoes",
                "Oyster Card or Contactless for Tube"
        ));

        // 10) Paris
        destinationMap.put("paris", Arrays.asList(
                "Stylish Clothing (Parisians dress well)",
                "Comfortable Walking Shoes",
                "Travel Adapter (Type E)",
                "Light Jacket (weather can vary)",
                "Collapsible Tote for Shopping"
        ));

        // 11) Rome
        destinationMap.put("rome", Arrays.asList(
                "Comfortable Sandals or Walking Shoes",
                "Travel Adapter (Type F/L)",
                "Modest Clothing for Churches",
                "Phrasebook / Basic Italian Phrases"
        ));

        // 12) Bangkok
        destinationMap.put("bangkok", Arrays.asList(
                "Lightweight Clothing (humid climate)",
                "Travel Adapter (Type A/C)",
                "Insect Repellent",
                "Comfortable Slip-On Shoes (temples)"
        ));

        // 13) Iceland
        destinationMap.put("iceland", Arrays.asList(
                "Warm Layers (wind & cold)",
                "Swimsuit (for hot springs)",
                "Waterproof Jacket",
                "Travel Adapter (Type C/F)"
        ));

        // 14) South Africa
        destinationMap.put("south africa", Arrays.asList(
                "Travel Adapter (Type D/M/N)",
                "Insect Repellent (especially for safari areas)",
                "Light Jacket",
                "Binoculars (if safari)"
        ));
    }

    public static List<String> getSuggestions(String tripType, String destination, int duration) {
        List<String> suggestions = new ArrayList<>();

        // 1) Fuzzy match for trip type
        String matchedTripType = fuzzyMatchKey(tripType, tripTypeMap.keySet());
        if (matchedTripType != null) {
            suggestions.addAll(tripTypeMap.get(matchedTripType));
        } else {
            // If no match or user typed nothing, use fallback
            suggestions.addAll(tripTypeMap.get("other"));
        }

        // 2) Fuzzy match for destination
        String matchedDestination = fuzzyMatchKey(destination, destinationMap.keySet());
        if (matchedDestination != null) {
            suggestions.addAll(destinationMap.get(matchedDestination));
        } else {
            // Fallback if no close match
            suggestions.addAll(destinationMap.get("other"));
        }

        // 3) Duration logic
        if (duration > 5) {
            suggestions.add("Extra Clothes");
        }
        if (duration > 10) {
            suggestions.add("Laundry Detergent Packets or Access Plan");
        }

        return suggestions;
    }

    // =========== Fuzzy Matching Helpers =============

    /**
     * Attempts to find a close match for userInput in the given keys using Jaro–Winkler distance.
     * If best match is below threshold, returns null.
     */
    private static String fuzzyMatchKey(String userInput, Set<String> keys) {
        // If user typed nothing, return null => fallback
        if (userInput == null || userInput.isEmpty()) return null;

        userInput = userInput.toLowerCase();

        String bestMatch = null;
        double bestScore = 0.0;

        for (String key : keys) {
            double score = jaroWinklerDistance(userInput, key);
            if (score > bestScore) {
                bestScore = score;
                bestMatch = key;
            }
        }
        // If best match doesn't meet threshold => fallback
        return (bestScore >= THRESHOLD) ? bestMatch : null;
    }

    /**
     * Jaro–Winkler distance high-level method
     */
    public static double jaroWinklerDistance(String s1, String s2) {
        double jaro = jaroDistance(s1, s2);

        // prefix length up to 4 chars
        int prefixLength = 0;
        for (int i = 0; i < Math.min(s1.length(), s2.length()); i++) {
            if (s1.charAt(i) == s2.charAt(i)) prefixLength++;
            else break;
        }
        prefixLength = Math.min(4, prefixLength);

        return jaro + (prefixLength * 0.1 * (1 - jaro));
    }

    /**
     * Jaro distance (used by Jaro–Winkler)
     */
    private static double jaroDistance(String s1, String s2) {
        if (s1.equals(s2)) return 1.0;

        int maxDist = (int)Math.floor(Math.max(s1.length(), s2.length()) / 2.0) - 1;
        boolean[] s1Matches = new boolean[s1.length()];
        boolean[] s2Matches = new boolean[s2.length()];

        int matches = 0;
        // Count matches
        for (int i = 0; i < s1.length(); i++) {
            int start = Math.max(0, i - maxDist);
            int end = Math.min(i + maxDist + 1, s2.length());
            for (int j = start; j < end; j++) {
                if (!s2Matches[j] && s1.charAt(i) == s2.charAt(j)) {
                    s1Matches[i] = true;
                    s2Matches[j] = true;
                    matches++;
                    break;
                }
            }
        }
        if (matches == 0) return 0.0;

        // Count transpositions
        double t = 0;
        int point = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (s1Matches[i]) {
                while (!s2Matches[point]) point++;
                if (s1.charAt(i) != s2.charAt(point)) t++;
                point++;
            }
        }
        t /= 2.0;

        // Jaro calculation
        return (matches / (double)s1.length()
                + matches / (double)s2.length()
                + (matches - t) / matches) / 3.0;
    }
}
