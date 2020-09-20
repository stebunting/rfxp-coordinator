package com.stevebunting.rfcoordinator;

import java.util.List;

final class Helpers {
    private Helpers() {}

    static <E extends Comparable<E>> boolean isSorted(List<E> list) {
        final int listSize = list.size();
        for (int i = 0; i < listSize - 1; i++) {
            if (list.get(i).compareTo(list.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }
}
