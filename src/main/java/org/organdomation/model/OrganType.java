package org.organdomation.model;

public enum OrganType {
    HEART("Heart"),
    KIDNEY("Kidney"),
    LIVER("Liver"),
    LUNGS("Lungs"),
    PANCREAS("Pancreas"),
    SMALL_INTESTINE("Small Intestine"),
    CORNEA("Cornea"),
    BONE_MARROW("Bone Marrow"),
    SKIN("Skin");
    
    private final String displayName;
    
    OrganType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}