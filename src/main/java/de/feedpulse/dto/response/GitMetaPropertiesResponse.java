package de.feedpulse.dto.response;

public record GitMetaPropertiesResponse(
        String version,
        String commitId,
        String commitIdAbbrev,
        String buildTime,
        String branch,
        String buildHost,
        String commitTime,
        String commitMessage,
        String commitMessageShort) {
}
