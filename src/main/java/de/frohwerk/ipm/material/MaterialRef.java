package de.frohwerk.ipm.material;

import de.frohwerk.ipm.core.JcoProperty;

public class MaterialRef {
    public String getMatnr() {
        return matnr;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "MaterialRef{" +
                "matnr='" + matnr + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @JcoProperty("MATERIAL")
    private String matnr;
    @JcoProperty("MATL_DESC")
    private String description;
}
