package org.tameter

import org.tameter.kotlin.delegates.setOnce
import org.tameter.kotlin.js.doOrLogError
import org.tameter.kotlin.js.jsobject
import org.tameter.kotlin.js.promise.Promise
import org.tameter.kotlin.js.promise.catchAndLog
import org.tameter.kpouchdb.*
import org.tameter.partialorder.dag.CompositeScoring
import org.tameter.partialorder.dag.Graph
import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import org.tameter.partialorder.source.GitHubSource
import org.tameter.partialorder.source.RedmineSource
import org.tameter.partialorder.source.Source
import org.tameter.partialorder.source.kpouchdb.*
import org.tameter.partialorder.ui.controller.GraphUpdater
import org.tameter.partialorder.ui.view.AppUI

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
            AppUI.databasesProperty.set(databases)

            databases.configDatabase.liveChanges(jsobject {
                sinceNow()
                include_docs = true
            }).onChange(configChangedHandler(databases.scoringDatabase))

            val graphUpdater = GraphUpdater(graphs)
            databases.scoringDatabase.liveChanges(jsobject {
                sinceNow()
                include_docs = true
            }).onChange(graphUpdater::handleChange)
        }.thenP {
            databases.GetAllConfigs()
        }.thenP { results ->
            loadSourceSpecs(databases.scoringDatabase, results.rows, 0, results.rows.size)
        }.catchAndLog()
    }
}

private fun configChangedHandler(db: PouchDB): (Change) -> Unit {
    return { change: Change ->
        doOrLogError {
            val doc = change.doc
            if (doc != null) {
                // Always remove, and re-add if added/altered
                removeSourceSpec(db, doc)
                if (!change.deleted) {
                    loadSourceSpec(db, doc)
                }
            }
        }
    }
}

fun PouchDoc.MakeSource(): Source {
    @Suppress("UNCHECKED_CAST_TO_NATIVE_INTERFACE")
    return when (type) {
        GITHUB_SOURCE_SPEC_DOC_TYPE -> GitHubSource(this as GitHubSourceSpecDoc)
        REDMINE_SOURCE_SPEC_DOC_TYPE -> RedmineSource(this as RedmineSourceSpecDoc)
        else -> throw RuntimeException("Unknown source spec doc type ${type}")
    }
}

private fun loadSourceSpec(
        scoringDatabase: PouchDB,
        doc: PouchDoc
): Promise<PouchDB>
        = doc.MakeSource().populate(scoringDatabase)

private fun removeSourceSpec(
    scoringDatabase: PouchDB,
    specDoc: PouchDoc
): Promise<PouchDB> {
    // TODO 2017-08-10 HughG: See if there's a more efficient way to do this -- some kind of filtering.
    return scoringDatabase.allDocs<NodeDoc>(jsobject {
        include_docs = true
    }).thenV { results ->
        results.rows.forEach {
            val doc = it.doc
            if (doc?.sourceId == specDoc._id) {
                scoringDatabase.remove(doc)
            }
        }
        scoringDatabase
    }
}

private fun loadSourceSpecs(
        scoringDatabase: PouchDB,
        rows: Array<BulkQueryRow<SourceSpecDoc>>,
        i: Int,
        total_rows: Int
): kotlin.js.Promise<Unit> {
    if (i == total_rows) {
        return kotlin.js.Promise.resolve(Unit)
    } else {
        val sourceSpecDoc = rows[i].doc!!
        return loadSourceSpec(scoringDatabase, sourceSpecDoc).thenP {
            loadSourceSpecs(scoringDatabase, rows, i + 1, total_rows)
        }
    }
}

