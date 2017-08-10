package org.tameter.partialorder.source

import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import org.tameter.partialorder.lib.jquery.JQueryXHR
import org.tameter.partialorder.lib.jquery.jQuery
import org.tameter.partialorder.lib.jquery.toPouchDB
import org.tameter.partialorder.source.kpouchdb.GitHubSourceSpecDoc

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

// From https://developer.github.com/v3/#pagination, the maximum page size is 100
// Recursively request pages until we have found all issues
private const val PAGE_SIZE_LIMIT = 100

class GitHubSource(val spec: GitHubSourceSpecDoc) : Source {
    override fun populate(db: PouchDB): Promise<PouchDB> {
        return doPopulate(db = db, page = 0)

        // TODO 2016-04-01 HughG: Should sanity-check for cycles in the graph.
    }

    override val sourceId = "github:${spec.repo}/${spec.user}"

    private fun doPopulate(db: PouchDB, page: Int): Promise<PouchDB> {
        return jQuery.getJSON(
                queryURL(page)
        ).then({ data: Any, _: String, _: JQueryXHR ->
            val issues = data.unsafeCast<Array<GitHubIssue>>()
            val issueDocs: Array<NodeDoc> = issues.map {
                NodeDoc(sourceId, spec._id, spec.description, it.number, it.title)
            }.toTypedArray()
            console.log("GitHub bulk store inputs, page ${page}:")
            for (it in issueDocs) {
                console.log(it)
            }
            issueDocs
        }).toPouchDB().thenP {
            db.bulkDocs(it)
        }.thenP { results ->
            console.log("GitHub bulk store results, page ${page}")
            for (it in results) {
                console.log(it)
            }

            if (results.size < PAGE_SIZE_LIMIT) {
                kotlin.js.Promise.resolve(db)
            } else {
                doPopulate(db, page + 1)
            }
        }
    }

    private fun queryURL(page: Int) =
            "https://api.github.com/repos/${spec.user}/${spec.repo}/issues?page=${page}&per_page=${PAGE_SIZE_LIMIT}"
}