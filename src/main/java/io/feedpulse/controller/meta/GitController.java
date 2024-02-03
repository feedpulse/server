package io.feedpulse.controller.meta;

import io.feedpulse.dto.response.GitMetaPropertiesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meta/git")
public class GitController {

    @Value("${git.build.version}")
    private String version;

    @Value("${git.commit.id.full}")
    private String commitId;

    @Value("${git.commit.id.abbrev}")
    private String commitIdAbbrev;

    @Value("${git.build.time}")
    private String buildTime;

    @Value("${git.branch}")
    private String branch;

    @Value("${git.build.host}")
    private String buildHost;

    @Value("${git.commit.time}")
    private String commitTime;

    @Value("${git.commit.message.full}")
    private String commitMessage;

    @Value("${git.commit.message.short}")
    private String commitMessageShort;

    @GetMapping("/properties")
    public GitMetaPropertiesResponse getGitMetaProperties() {
        return new GitMetaPropertiesResponse(
                version,
                commitId,
                commitIdAbbrev,
                buildTime,
                branch,
                buildHost,
                commitTime,
                commitMessage,
                commitMessageShort
        );
    }

}


