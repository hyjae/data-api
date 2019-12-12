package kr.datasolution.ds.api.vo;

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
