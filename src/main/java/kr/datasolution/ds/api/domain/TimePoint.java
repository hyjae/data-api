package kr.datasolution.ds.api.domain;

public enum TimePoint {
    FROM("from"),
    TO("to");

    private String point;

    TimePoint(String point) {
        this.point = point;
    }

    public String getTimePoint() {
        return point;
    }
}
