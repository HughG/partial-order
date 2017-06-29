package org.tameter

import org.tameter.kotlin.js.doOrLogError
import org.tameter.kotlin.js.promise.catchAndLog
import org.tameter.kpouchdb.ChangeOptions
import org.tameter.kpouchdb.liveChanges
import org.tameter.kpouchdb.onChange
import org.tameter.kpouchdb.sinceNow
import org.tameter.partialorder.dag.Graph
import org.tameter.partialorder.dag.MultiGraph
import org.tameter.partialorder.source.GitHubSource
import org.tameter.partialorder.ui.controller.GraphUpdater

fun main(args: Array<String>) {
    doOrLogError {
        val graphs = MultiGraph().apply {
            addGraph("importance")
            addGraph("urgency")
        }
        resetDB().thenV { db ->
            val graphUpdater = GraphUpdater(db, graphs)
            db.liveChanges(ChangeOptions().apply {
                sinceNow()
                include_docs = true
            }).onChange(graphUpdater::handleChange)
            db
        }.thenP { db ->
            GitHubSource("HughG", "partial-order").populate(db)
        }.catchAndLog()
    }
}
