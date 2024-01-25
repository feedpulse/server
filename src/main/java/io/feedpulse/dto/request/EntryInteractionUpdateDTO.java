package io.feedpulse.dto.request;

import org.springframework.lang.Nullable;

import java.util.UUID;

public class EntryInteractionUpdateDTO {

    private UUID uuid;

    @Nullable
    private Boolean read;

    @Nullable
    private Boolean favorite;

    @Nullable
    private Boolean bookmark;

    public EntryInteractionUpdateDTO() {
    }

    public EntryInteractionUpdateDTO(UUID uuid, Boolean read, Boolean favorite, Boolean bookmark) {
        this.uuid = uuid;
        this.read = read;
        this.favorite = favorite;
        this.bookmark = bookmark;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Nullable
    public Boolean isRead() {
        return read;
    }

    public void setRead(@Nullable Boolean read) {
        this.read = read;
    }

    @Nullable
    public Boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(@Nullable Boolean favorite) {
        this.favorite = favorite;
    }

    @Nullable
    public Boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(@Nullable Boolean bookmark) {
        this.bookmark = bookmark;
    }

    @Override
    public String toString() {
        return "EntryInteractionUpdateDTO{" +
                "uuid=" + uuid +
                ", read=" + read +
                ", favorite=" + favorite +
                ", bookmark=" + bookmark +
                '}';
    }
}
