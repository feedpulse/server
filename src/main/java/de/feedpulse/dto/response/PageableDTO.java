package de.feedpulse.dto.response;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.PagedModel;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record PageableDTO<T>(
        long page,
        long size,
        long totalElements,
        long totalPages,
        Links links,
        List<T> content
) implements Serializable, PageableDataDTO<T> {

    public static <T, S> PageableDTO<T> of(PagedModel<EntityModel<S>> pagedModel, List<T> values) {
        try {
            Objects.requireNonNull(pagedModel);
            Objects.requireNonNull(values);
            String first = getHrefOrElse(pagedModel.getLinks().getLink("first"), "");
            String last = getHrefOrElse(pagedModel.getLinks().getLink("last"), "");
            String next = getHrefOrElse(pagedModel.getLinks().getLink(LinkRelation.of("next")), "");
            String self = getHrefOrElse(pagedModel.getLinks().getLink(LinkRelation.of("self")), "");
            return new PageableDTO<>(
                    pagedModel.getMetadata().getNumber(),
                    pagedModel.getMetadata().getSize(),
                    pagedModel.getMetadata().getTotalElements(),
                    pagedModel.getMetadata().getTotalPages(),
                    new Links(first, last, next, self),
                    values
            );
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

    }

    private static String getHrefOrElse(Optional<Link> linkOpt,  String defaultValue) {
        try {
            Link link = linkOpt.orElseThrow();
            return link.getHref();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}

record Links(
        String first,
        String last,
        String next,
        String self) {
}
