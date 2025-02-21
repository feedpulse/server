package de.feedpulse.dto.response;

import java.util.List;

public interface PageableDataDTO<T> {
    long page();
    long size();
    long totalElements();
    long totalPages();
    Links links();
    List<T> content();
}
