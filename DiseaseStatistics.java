package com.polyclinic.registry;

public class DiseaseStatistics {
    private String diseaseName;
    private int count;

    public DiseaseStatistics(String diseaseName, int count) {
        this.diseaseName = diseaseName;
        this.count = count;
    }

    public String getDiseaseName() { return diseaseName; }
    public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    @Override
    public String toString() {
        return diseaseName + ": " + count + " случаев";
    }
}