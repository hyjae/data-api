package kr.datastation.api.vo;

public enum DocumentOrder {
    KEY_ASC("key_asc"),
    KEY_DESC("key_desc");

    private String documentOrder;

    DocumentOrder(String documentOrder) {
        this.documentOrder = documentOrder;
    }

    public String getDocumentOrder() {
        return documentOrder;
    }
}
