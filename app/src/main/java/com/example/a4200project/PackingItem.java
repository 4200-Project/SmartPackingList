package com.example.a4200project;

public class PackingItem {
    private String name;
    private boolean checked;

    public PackingItem(String name, boolean checked) {
        this.name = name;
        this.checked = checked;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
}