package de.frohwerk.ipm.bom;

import de.frohwerk.ipm.core.JcoProperty;

@SuppressWarnings("unused")
public class BomPosition {
    public String getMatnr() {
        return matnr;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public float getKomponentenMenge() {
        return komponentenMenge;
    }

    public void setKomponentenMenge(float komponentenMenge) {
        this.komponentenMenge = komponentenMenge;
    }

    public String getKomponentenEinheit() {
        return komponentenEinheit;
    }

    public void setKomponentenEinheit(String komponentenEinheit) {
        this.komponentenEinheit = komponentenEinheit;
    }

    @Override
    public String toString() {
        return "BomPosition{" +
                "matnr='" + matnr + '\'' +
                ", komponentenMenge='" + komponentenMenge + '\'' +
                ", komponentenEinheit='" + komponentenEinheit + '\'' +
                '}';
    }

    @JcoProperty("COMPONENT")
    private String matnr;
    @JcoProperty("COMP_QTY")
    private float komponentenMenge;
    @JcoProperty("COMP_UNIT")
    private String komponentenEinheit;
}
