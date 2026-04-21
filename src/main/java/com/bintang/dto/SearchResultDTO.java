package com.bintang.dto;

public class SearchResultDTO {
    private String title;
    private String subtitle;
    private String url;
    private String type;
    private String icon;

    public SearchResultDTO(String title, String subtitle, String url, String type, String icon) {
        this.title = title;
        this.subtitle = subtitle;
        this.url = url;
        this.type = type;
        this.icon = icon;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}
