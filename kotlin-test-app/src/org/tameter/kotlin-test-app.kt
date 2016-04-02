package org.tameter

import org.tameter.kotlinjs.jsobject
import org.tameter.kotlinjs.promise.Promise
import org.tameter.kotlinjs.promise.catchAndLog
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.Edge
import org.tameter.partialorder.dag.Graph
import org.tameter.partialorder.dag.Node
import org.tameter.partialorder.dag.kpouchdb.EdgeDoc
import org.tameter.partialorder.dag.kpouchdb.NodeDoc

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
    val graphClone = Graph().apply { graph.nodes.forEach { nodes.add(Node(this, it)) } }

    // Find set of all possible edges
    for (from in graph.nodes) {
        for (to in graph.nodes) {
            // TODO 2016-04-01 HughG: Really, to avoid cycles I have to filter out all edges for which there is already
            // a path from "to" to "from", not just self-edges.
            if (from !== to) {
                graphClone.edges.add(Edge(graphClone, from, to))
            }
        }
    }
    console.log("Possible Edges:")
    graphClone.edges.forEach { console.log(it.toString()) }

    // TODO 2016-04-01 HughG: Would be quicker to just avoid adding these edges in the first place :-)
    graphClone.edges.removeAll { possibleE ->
        graph.edges.any { actualE ->
            console.log("  Comparing")
            console.log("    from")
            console.log("      ${actualE.from}")
            console.log("      ${possibleE.from}")
            console.log("    to")
            console.log("      ${actualE.to}")
            console.log("      ${possibleE.to}")
            val result = actualE.from == possibleE.from &&
                    actualE.to == possibleE.to
            if (result) {
                console.log("Removing ${actualE.toString()}: match with ${possibleE.toString()}")
            }
            result
        }
    }
    console.log("Remaining Edges:")
    graphClone.edges.forEach { console.log(it.toString()) }

    // Return result
    return graphClone.edges
}
