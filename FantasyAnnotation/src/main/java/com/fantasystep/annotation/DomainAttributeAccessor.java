package com.fantasystep.annotation;

public class DomainAttributeAccessor {

    private boolean isPropertyNode;

    private java.lang.String category;

    private java.lang.Class<?>[] validParents;

    private java.lang.String icon;

    private java.lang.String foreignKey;

    private java.lang.String label;

    public boolean getIsPropertyNode() {
        return isPropertyNode;
    }

    public java.lang.String getCategory() {
        return category;
    }

    public java.lang.Class<?>[] getValidParents() {
        return validParents;
    }

    public java.lang.String getIcon() {
        return icon;
    }

    public java.lang.String getForeignKey() {
        return foreignKey;
    }

    public java.lang.String getLabel() {
        return label;
    }

    public void setIsPropertyNode(boolean isPropertyNode) {
        this.isPropertyNode = isPropertyNode;
    }

    public void setCategory(java.lang.String category) {
        this.category = category;
    }

    public void setValidParents(java.lang.Class<?>[] validParents) {
        this.validParents = validParents;
    }

    public void setIcon(java.lang.String icon) {
        this.icon = icon;
    }

    public void setForeignKey(java.lang.String foreignKey) {
        this.foreignKey = foreignKey;
    }

    public void setLabel(java.lang.String label) {
        this.label = label;
    }

}
