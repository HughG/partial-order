package org.tameter

import org.tameter.kotlinjs.jsobject
import org.tameter.kotlinjs.promise.Promise
import org.tameter.kotlinjs.promise.catchAndLog
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.*

fun main(args: Array<String>) {
    initDB().thenP { db ->
        loadGraph(db)
    }.thenV { graph ->
        proposeEdges(graph)
    }.catchAndLog()
}

fun loadGraph(db: PouchDB): Promise<Graph> {
    val g: Graph = Graph()

    console.log("Loading graph ...")

    // Load nodes
    return db.allDocs<NodeDoc>(jsobject {
        startkey = "N_"
        endkey = "N_\uffff"
        include_docs = true
    }).thenV {
        console.log("Nodes:")
        console.log(it)
        it.rows.forEach {
            var node: NodeDoc? = it.doc
            console.log(node?.description ?: "no desc")
            if (node != null) {
                g.nodes.add(Node(g, node))
            }
        }
        it
    }.thenP {
        // Load edges
        db.allDocs<EdgeDoc>(jsobject {
            startkey = "E_"
            endkey = "E_\uffff"
            include_docs = true
        })
    }.thenV {
        console.log("Edges:")
        console.log(it)
        it.rows.forEach {
            var edge: EdgeDoc? = it.doc
            console.log(edge?._id ?: "no ID")
            if (edge != null) {
                g.edges.add(Edge(g, edge))
            }
        }

        console.log("Loading graph ... done.")

        // Return result
        g
    }
}

fun proposeEdges(graph: Graph): Collection<Edge> {
    val allPossibleEdges: MutableCollection<Edge> = mutableListOf()

    // Find set of all possible edges
    for (from in graph.nodes) {
        for (to in graph.nodes) {
            // TODO 2016-04-01 HughG: Really, to avoid cycles I have to filter out all edges for which there is already
            // a path from "to" to "from", not just self-edges.
            if (from !== to) {
                allPossibleEdges.add(Edge(graph, from, to))
            }
        }
    }
    console.log("Possible Edges:")
    allPossibleEdges.forEach { console.log(it.toString()) }

    // TODO 2016-04-01 HughG: Would be quicker to just avoid adding these edges in the first place :-)
    allPossibleEdges.removeAll { possibleE ->
        graph.edges.any { actualE ->
            actualE.from == possibleE.from &&
                    actualE.to == possibleE.to
        }
    }
    console.log("Remaining Edges:")
    allPossibleEdges.forEach { console.log(it.toString()) }

    // Return result
    return allPossibleEdges
}
