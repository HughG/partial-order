package org.tameter.partialorder.source

import org.tameter.kotlin.js.jsobject
import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import org.tameter.partialorder.lib.jquery.*
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
const val perRedmineRequestLimit = 100

class RedmineSource(val spec: RedmineSourceSpec) : Source {

    override fun populate(db: PouchDB): Promise<PouchDB> {
        return doPopulate(db = db, pageNumber = 0)
    }

    // Recursively populate database with requests until all pages are found
    private fun doPopulate(db: PouchDB, pageNumber: Int): Promise<PouchDB> {
        return makeRequest(pageNumber)
                .thenP {
                    db.bulkDocs(it)
                }.thenP { results ->
            console.log("Redmine bulk store results, page ${pageNumber}:")
            results.forEach { console.log(it) }
            if(results.size < perRedmineRequestLimit) {
                kotlin.js.Promise.resolve(db)
            } else {
                doPopulate(db, pageNumber + 1)
            }
        }
    }

    // Perform the Redmine REST API request for a page of open issues, and convert these into NodeDoc format
    private fun makeRequest(pageNumber: Int): Promise<Array<NodeDoc>> {
        return jQuery.get(jsobject<JQueryAjaxSettings> {
            dataType = "json"
            if (spec.apiKey != null) {
                headers = json("X-Redmine-API-Key" to spec.apiKey)
            }
            url = queryUrl(pageNumber)
        }).then({ data: Any, _: String, _: JQueryXHR ->
            val issues = data.unsafeCast<RedmineIssueResponse>().issues
            val issueDocs: Array<NodeDoc> = issues.map {
                NodeDoc("${spec.url}/issues/${it.id}", it.subject)
            }.toTypedArray()
            issueDocs
        }).toPouchDB()
    }

    // Form the query URL for the specified results page
    private fun queryUrl(pageNumber: Int): String {
        val offset = pageNumber * perRedmineRequestLimit
        var queryURL = "${spec.url}/issues.json?offset=${offset}&limit=${perRedmineRequestLimit}"
        if (spec.projectId != null) {
            queryURL += "&project_id=${spec.projectId}"
        }
        return queryURL
    }
}