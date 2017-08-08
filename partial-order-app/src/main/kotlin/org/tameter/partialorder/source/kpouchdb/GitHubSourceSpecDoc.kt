package org.tameter.partialorder.source.kpouchdb

import org.tameter.partialorder.source.GitHubSourceSpec

external interface GitHubSourceSpecDoc : SourceSpecDoc, GitHubSourceSpec

val GITHUB_SOURCE_SPEC_DOC_TYPE = "${SOURCE_SPEC_DOC_TYPE}_github"

fun GitHubSourceSpecDoc(description: String, user: String, repo: String): GitHubSourceSpecDoc {
    return SourceSpecDoc<GitHubSourceSpecDoc>("${repo}/${user}", GITHUB_SOURCE_SPEC_DOC_TYPE, description).apply {
        asDynamic().user = user
        asDynamic().repo = repo
    }
}
