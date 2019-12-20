package kr.datastation.api.vo;

public enum SortBy {
    DATE("date"),
    COUNT("count");

    private String sortBy;

    SortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortBy() {
        return sortBy;
    }
}
