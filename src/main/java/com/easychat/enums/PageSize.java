package com.easychat.enums;

public enum PageSize {
    SIZE15(15), SIZE20(20), SIZE30(30), SIZE50(50), SIZE100(100);
    int size;

    PageSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
