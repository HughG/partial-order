package org.tameter

import org.tameter.kotlin.delegates.setOnce
import org.tameter.kotlin.js.doOrLogError
import org.tameter.kotlin.js.jsobject
import org.tameter.kotlin.js.promise.catchAndLog
import org.tameter.kpouchdb.*
import org.tameter.partialorder.dag.CompositeScoring
import org.tameter.partialorder.dag.Graph
import org.tameter.partialorder.source.GitHubSource
import org.tameter.partialorder.source.RedmineSource
import org.tameter.partialorder.source.kpouchdb.*
import org.tameter.partialorder.ui.controller.GraphUpdater

private val SCORING_DB_NAME = "http://localhost:5984/scoring"
private val CONFIG_DB_NAME = "http://localhost:5984/scoring_config"

fun main(args: Array<String>) {
    doOrLogError {
        val graphs = CompositeScoring("root").apply {
             addScoring(Graph("importance"))
             addScoring(Graph("fun"))
             addScoring(Graph("interesting"))
             addScoring(Graph("non-scary"))
        }
        var databases: Databases by setOnce()
        initialiseDatabases(SCORING_DB_NAME, CONFIG_DB_NAME).thenV { db ->
            databases = db
            val graphUpdater = GraphUpdater(databases.scoringDatabase, graphs)
            databases.scoringDatabase.liveChanges(ChangeOptions().apply {
                sinceNow()
                include_docs = true
            }).onChange(graphUpdater::handleChange)
        }.thenP {
            // TODO 2017-08-08 HughG: Consider how to use liveChanges to read all existing config, plus later changes.
            // TODO 2017-08-08 HughG: Consider how cope with dynamically adding and removing sources.
            databases.configDatabase.allDocs<SourceSpecDoc>(jsobject<AllDocsOptions> {
                include_docs = true
                startkey = SOURCE_SPEC_DOC_TYPE
                endkey = SOURCE_SPEC_DOC_TYPE + '\uFFFF'
            })
        }.thenP { results ->
            loadSourceSpecs(databases.scoringDatabase, results.rows, 0, results.rows.size)
        }.catchAndLog()
    }
}

fun loadSourceSpecs(
        scoringDatabase: PouchDB,
        rows: Array<BulkQueryRow<SourceSpecDoc>>,
        i: Int,
        total_rows: Int
): kotlin.js.Promise<Unit> {
    if (i == total_rows) {
        return kotlin.js.Promise.resolve(Unit)
    } else {
        val sourceSpecDoc = rows[i].doc!!
        @Suppress("UNCHECKED_CAST_TO_NATIVE_INTERFACE")
        val source = when (sourceSpecDoc.type) {
            GITHUB_SOURCE_SPEC_DOC_TYPE -> GitHubSource(sourceSpecDoc as GitHubSourceSpecDoc)
            REDMINE_SOURCE_SPEC_DOC_TYPE -> RedmineSource(sourceSpecDoc as RedmineSourceSpecDoc)
            else -> throw RuntimeException("Unknown source spec doc type ${sourceSpecDoc.type}")
        }
        return source.populate(scoringDatabase).thenP {
            loadSourceSpecs(scoringDatabase, rows, i + 1, total_rows)
        }
    }
}

