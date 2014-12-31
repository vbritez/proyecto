package com.tigo.cs.android.util;

import com.tigo.cs.android.R;

public enum CSTigoLogTags {

    DATABASE(R.string.cstigo_database, "TAG de errores o notificaciones de base dedatos"),
    LOCATION(R.string.location_gps, "Localizacion del dispositivo");

    protected final int value;
    protected final String description;

    private CSTigoLogTags(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

}
