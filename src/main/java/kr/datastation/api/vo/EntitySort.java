package kr.datastation.api.vo;

public enum EntitySort {
    ENTITY_ASC("entity.asc"),
    ENTITY_DESC("entity.desc"),
    DATE_ASC("date.asc"),
    DATE_DESC("date.desc");

    private String entityOrder;

    EntitySort(String entityOrder) {
        this.entityOrder = entityOrder;
    }

    public String getEntityOrder() {
        return entityOrder;
    }
}
