package com.ssport.pagination;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomPage<T> {

    private final List<T> content = new ArrayList<>();
    private final Pageable pageable;
    private final long total;

    public CustomPage(List<T> content, Pageable pageable, long total) {
        this.content.addAll(content);
        this.pageable = pageable;
        this.total = pageable
                .toOptional()
                .filter((it) -> !content.isEmpty())
                .filter((it) -> it.getOffset() + (long) it.getPageSize() > total)
                .map((it) -> it.getOffset() + (long) content.size())
                .orElse(total);
    }

    private int getSize() {
        return this.pageable.isPaged() ? this.pageable.getPageSize() : 0;
    }

    private int getNumber() {
        return this.pageable.isPaged() ? this.pageable.getPageNumber() : 0;
    }

    private int getTotalPages() {
        return this.getSize() == 0 ? 1 : (int) Math.ceil((double) this.total / (double) this.getSize());
    }

    private boolean hasNext() {
        return this.getNumber() + 1 < this.getTotalPages();
    }

    public boolean isLast() {
        return !this.hasNext();
    }

    public List<T> getContent() {
        return Collections.unmodifiableList(this.content);
    }
}
