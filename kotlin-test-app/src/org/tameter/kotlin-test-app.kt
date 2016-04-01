package org.tameter

import org.tameter.kotlinjs.jsobject
import org.tameter.kotlinjs.promise.Promise
import org.tameter.kotlinjs.promise.catchAndLog
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.Edge
import org.tameter.partialorder.dag.Node

fun main(args: Array<String>) {
    initDB().thenP { db ->
        proposeEdges(db)
    }.catchAndLog()
}

fun proposeEdges(db: PouchDB): Promise<dynamic> {
    val allPossibleEdges: MutableCollection<Edge> = mutableListOf()

    // Read all nodes and edges
    return db.allDocs<Node>(jsobject {
        startkey = "N_"
        endkey = "N_\uffff"
        include_docs = true
    }).thenV {
        console.log(it)
        it.rows.forEach { console.log(it.doc?.description ?: "no desc") }
        it
    }.thenV {
        val nodes = it.rows.mapNotNull { it.doc }
        // Find set of all possible edges
        for (from in nodes) {
            for (to in nodes) {
                allPossibleEdges.add(Edge(from, to))
            }
        }
        console.log("Possible Edges:")
        allPossibleEdges.forEach { console.log(it) }
        it
    }.thenP {
        // Remove existing edges
        db.allDocs<Edge>(jsobject {
            startkey = "E_"
            endkey = "E_\uffff"
            include_docs = true
        })
    }.thenV {
        val edges = it.rows.mapNotNull { it.doc }
        console.log("Actual Edges:")
        edges.forEach { console.log(it) }
        allPossibleEdges.removeAll { possibleE ->
            edges.any { actualE ->
                actualE.from == possibleE.from &&
                        actualE.to == possibleE.to
            }
        }
        console.log("Remaining Edges:")
        allPossibleEdges.forEach { console.log(it) }

        // Return result
        allPossibleEdges
    }
}

