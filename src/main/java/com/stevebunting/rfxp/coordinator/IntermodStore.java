package com.stevebunting.rfxp.coordinator;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

class IntermodStore implements Iterable<Intermod> {
    private List<Intermod> intermods;
    private List<List<Intermod>> backups;

    IntermodStore() {
        this.intermods = new ArrayList<>();
        this.backups = new ArrayList<>();
    }

    final void add(@NotNull final Intermod intermod) {
        intermods.add(intermod);
    }

    final void remove(@NotNull final Channel channel) {
        intermods.removeIf((Intermod intermod) ->
                intermod.getF1() == channel
                        || intermod.getF2() == channel
                        || intermod.getF3() == channel);
    }

    final void pushToBackupStack() {
        backups.add(intermods);
    }

    final void popFromBackupStack() {
        intermods = backups.remove(backups.size() - 1);
    }

    final void mergeIn(@NotNull final IntermodStore intermodStore) {
        List<Intermod> mergedList = new ArrayList<>();
        List<Intermod> a = intermods;
        List<Intermod> b = intermodStore.intermods;

        int indexA = 0;
        int indexB = 0;
        while (indexA < a.size() || indexB < b.size()) {
            if (indexA == a.size()) {
                mergedList.add(b.get(indexB));
                indexB++;
            } else if (indexB == b.size()) {
                mergedList.add(a.get(indexA));
                indexA++;
            } else if (a.get(indexA).compareTo(b.get(indexB)) < 0) {
                mergedList.add(a.get(indexA));
                indexA++;
            } else {
                mergedList.add(b.get(indexB));
                indexB++;
            }
        }
        intermods = mergedList;
    }

    private int getNextImIndex(final int limitLo) {
        return getNextImIndex(limitLo, 0, intermods.size() - 1);
    }

    private int getNextImIndex(
            final int limitLo,
            final int start,
            final int end
    ) {
        if (start > end) {
            return start;
        }

        final int mid = start + ((end - start) / 2);
        if (intermods.get(mid).getFreq() <= limitLo) {
            return mid + 1 < intermods.size() && intermods.get(mid + 1).getFreq() > limitLo
                    ? mid + 1
                    : getNextImIndex(limitLo, mid + 1, end);
        } else {
            return mid > 0 && intermods.get(mid - 1).getFreq() <= limitLo
                    ? mid
                    : getNextImIndex(limitLo, start, mid - 1);
        }
    }

    final void sort() {
        intermods.sort(null);
    }

    final int size() {
        return intermods.size();
    }

    final boolean isEmpty() {
        return intermods.size() == 0;
    }

    final Intermod[] getIntermodsArray() {
        return intermods.toArray(new Intermod[0]);
    }

    @NotNull
    @Override
    public Iterator<Intermod> iterator() {
        return intermods.iterator();
    }

    final void forRange(final int rangeLo, final int rangeHi, Consumer<Intermod> consumer) {
        int index = getNextImIndex(rangeLo);
        while (index < intermods.size() && intermods.get(index).getFreq() < rangeHi) {
            consumer.accept(intermods.get(index++));
        }
    }
}
