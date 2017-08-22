package de.frohwerk.ipm.material;

import de.frohwerk.ipm.core.JcoProperty;

@SuppressWarnings("unused")
public class MaterialDetail {
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMaterialart() {
        return materialart;
    }

    public void setMaterialart(String materialart) {
        this.materialart = materialart;
    }

    public String getBasismengeneinheit() {
        return basismengeneinheit;
    }

    public void setBasismengeneinheit(String basismengeneinheit) {
        this.basismengeneinheit = basismengeneinheit;
    }

    public float getBruttogewicht() {
        return bruttogewicht;
    }

    public void setBruttogewicht(float bruttogewicht) {
        this.bruttogewicht = bruttogewicht;
    }

    public float getNettogewicht() {
        return nettogewicht;
    }

    public void setNettogewicht(float nettogewicht) {
        this.nettogewicht = nettogewicht;
    }

    public String getGewichtseinheit() {
        return gewichtseinheit;
    }

    public void setGewichtseinheit(String gewichtseinheit) {
        this.gewichtseinheit = gewichtseinheit;
    }

    @Override
    public String toString() {
        return "MaterialDetail{" +
                "description='" + description + '\'' +
                ", materialart='" + materialart + '\'' +
                ", basismengeneinheit='" + basismengeneinheit + '\'' +
                ", bruttogewicht=" + bruttogewicht +
                ", nettogewicht=" + nettogewicht +
                ", gewichtseinheit='" + gewichtseinheit + '\'' +
                '}';
    }

    @JcoProperty("MATL_DESC")
    private String description;
    @JcoProperty("MATL_TYPE")
    private String materialart;
    @JcoProperty("BASE_UOM")
    private String basismengeneinheit;
    @JcoProperty("GROSS_WT")
    private float bruttogewicht;
    @JcoProperty("NET_WEIGHT")
    private float nettogewicht;
    @JcoProperty("UNIT_OF_WT")
    private String gewichtseinheit;
}
