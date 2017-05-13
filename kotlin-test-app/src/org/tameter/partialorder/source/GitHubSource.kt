package org.tameter.partialorder.source

import org.tameter.kotlin.js.promise.async
import org.tameter.kotlin.js.promise.await
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import org.tameter.partialorder.lib.jquery.await
import org.tameter.partialorder.lib.jquery.jQuery
import kotlin.js.Promise

/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */

external interface GitHubIssue {
//    val url: String
    val number: Long
    val title: String
}

class GitHubSource(
        val user: String,
        val repo: String
) : Source {

    override fun populate(db: PouchDB) : Promise<PouchDB> = async {
        val data = jQuery.getJSON("https://api.github.com/repos/${user}/${repo}/issues").await()
        val issues = data.unsafeCast<Array<GitHubIssue>>()
        val issueDocs: Array<NodeDoc> = issues.map {
            NodeDoc("github:${repo}/${user}/${it.number}", it.title)
        }.toTypedArray()
        console.log("Bulk store inputs:")
        issueDocs.forEach { console.log(it) }
        val results = db.bulkDocs(issueDocs).await()
        console.log("Bulk store results:")
        results.forEach { console.log(it) }
        db

        // TODO 2016-04-01 HughG: Should sanity-check for cycles in the graph.

        // TODO 2017-05-13 HughG: Deal with pagination from GitHub APIs: https://developer.github.com/v3/#pagination
    }
}