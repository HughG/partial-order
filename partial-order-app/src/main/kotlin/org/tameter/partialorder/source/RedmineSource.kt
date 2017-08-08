package org.tameter.partialorder.source

import org.tameter.kotlin.js.jsobject
import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import org.tameter.partialorder.lib.jquery.JQueryAjaxSettings
import org.tameter.partialorder.lib.jquery.JQueryXHR
import org.tameter.partialorder.lib.jquery.jQuery
import org.tameter.partialorder.lib.jquery.toPouchDB
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

class RedmineSource(val spec: RedmineSourceSpec) : Source {
    override fun populate(db: PouchDB): Promise<PouchDB> {
        var queryURL = "${spec.url}/issues.json"
        if (spec.projectId != null) {
            queryURL += "?project_id=${spec.projectId}"
        }
        return jQuery.get(jsobject<JQueryAjaxSettings> {
            dataType = "json"
            if (spec.apiKey != null) {
                headers = json("X-Redmine-API-Key" to spec.apiKey)
            }
            url = queryURL
        }).then({ data: Any, _: String, _: JQueryXHR ->
            val issues = data.unsafeCast<RedmineIssueResponse>().issues
            val issueDocs: Array<NodeDoc> = issues.map {
                NodeDoc("${spec.url}/issues/${it.id}", it.subject)
            }.toTypedArray()
            console.log("Bulk store inputs ${queryURL} :")
            issueDocs.forEach { console.log(it) }
            issueDocs
        }).toPouchDB().thenP {
            db.bulkDocs(it)
        }.thenV { results ->
            console.log("Bulk store results ${queryURL} :")
            results.forEach { console.log(it) }
            db
        }
    }
}
