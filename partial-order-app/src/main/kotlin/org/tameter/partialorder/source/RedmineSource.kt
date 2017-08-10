package org.tameter.partialorder.source

import org.tameter.kotlin.js.jsobject
import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import org.tameter.partialorder.lib.jquery.*
import org.tameter.partialorder.source.kpouchdb.RedmineSourceSpecDoc
import kotlin.js.json

external interface RedmineIssue {
    val id: Long
    val subject: String
}

external interface RedmineIssueResponse {
    val issues: Array<RedmineIssue>
}

external interface RedmineSourceSpec {
    val url: String
    val apiKey: String?
    val projectId: String?
}

// By default, a REST query returns 25 pages, and returns only open issues
// The version of Redmine used allows a maximum of 100 issues per query via the 'limit' keyword
// Later versions allow the administrator to increase this maximum
// The 'offset' keyword allows us to skip a number of issues
// We use this keyword to recursively request until we have all issues
private const val PAGE_SIZE_LIMIT    = 100

class RedmineSource(val spec: RedmineSourceSpecDoc) : Source {

    override fun populate(db: PouchDB): Promise<PouchDB> {
        return doPopulate(db = db, pageNumber = 0)
    }

    // Recursively populate database with requests until all pages are found
    private fun doPopulate(db: PouchDB, pageNumber: Int): Promise<PouchDB> {
        return makeRequest(pageNumber).thenP {
            db.bulkDocs(it)
        }.thenP { results ->
            console.log("Redmine bulk store results, page ${pageNumber}:")
            for (it in results) {
                console.log(it)
            }
            if (results.size < PAGE_SIZE_LIMIT) {
                kotlin.js.Promise.resolve(db)
            } else {
                doPopulate(db, pageNumber + 1)
            }
        }
    }

    override val sourceId = "${spec.url}/issues"

    // Perform the Redmine REST API request for a page of open issues, and convert these into NodeDoc format.
    private fun makeRequest(pageNumber: Int): Promise<Array<NodeDoc>> {
        return jQuery.get(jsobject<JQueryAjaxSettings> {
            dataType = "json"
            if (spec.apiKey != null) {
                headers = json("X-Redmine-API-Key" to spec.apiKey)
            }
            url = queryUrl(pageNumber)
        }).then({ data: Any, _: String, _: JQueryXHR ->
            val issues = data.unsafeCast<RedmineIssueResponse>().issues
            issues.map {
                NodeDoc(sourceId, spec._id, spec.description, it.id, it.subject)
            }.toTypedArray()
        }).toPouchDB()
    }

    // Form the query URL for the specified results page.
    private fun queryUrl(pageNumber: Int): String {
        // TODO 2017-08-10 HughG: Retrieve the user ID using the apiKey, and add that to the query filter, to get only
        // tasks assigned to "me" (and, optionally, not assigned).
        val offset = pageNumber * PAGE_SIZE_LIMIT
        var queryURL = "${spec.url}/issues.json?offset=${offset}&limit=${PAGE_SIZE_LIMIT}"
        if (spec.projectId != null) {
            queryURL += "&project_id=${spec.projectId}"
        }
        return queryURL
    }
}