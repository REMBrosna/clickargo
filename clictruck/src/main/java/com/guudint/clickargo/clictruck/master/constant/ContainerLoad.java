package com.guudint.clickargo.clictruck.master.constant;

public enum ContainerLoad {

    F("Full Container Load", "Full"), P("Partial Container Load", "Partial"), E("Empty Container Load", "Empty");

    private final String label;
    private final String altLabel;
    
    private ContainerLoad(String label, String altLabel) {
        this.label = label;
        this.altLabel = altLabel;
    }

    public String getLabel() {
		return label;
	}

	public String getAltLabel() {
		return altLabel;
	}
    
}
