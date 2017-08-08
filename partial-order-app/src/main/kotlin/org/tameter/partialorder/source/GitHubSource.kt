package org.tameter.partialorder.source

import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import org.tameter.partialorder.lib.jquery.JQueryXHR
import org.tameter.partialorder.lib.jquery.jQuery
import org.tameter.partialorder.lib.jquery.toPouchDB

/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */

external interface GitHubIssue {
//    val url: String
    val number: Long
    val title: String
}

external interface GitHubSourceSpec {
    val user: String
    val repo: String
}

class GitHubSource(val spec: GitHubSourceSpec) : Source {
    override fun populate(db: PouchDB) : Promise<PouchDB> {
        return jQuery.getJSON(
                "https://api.github.com/repos/${spec.user}/${spec.repo}/issues"
        ).then({ data: Any, _: String, _: JQueryXHR ->
            val issues = data.unsafeCast<Array<GitHubIssue>>()
            val issueDocs: Array<NodeDoc> = issues.map {
                NodeDoc("github:${spec.repo}/${spec.user}/${it.number}", it.title)
            }.toTypedArray()
            console.log("Bulk store inputs:")
            issueDocs.forEach { console.log(it) }
            issueDocs
        }).toPouchDB().thenP {
            db.bulkDocs(it)
        }.thenV { results ->
            console.log("Bulk store results:")
            results.forEach { console.log(it) }
            db
        }

        // TODO 2016-04-01 HughG: Should sanity-check for cycles in the graph.

        // TODO 2017-05-13 HughG: Deal with pagination from GitHub APIs: https://developer.github.com/v3/#pagination
    }
}