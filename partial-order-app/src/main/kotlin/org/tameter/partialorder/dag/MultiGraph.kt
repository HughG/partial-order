package org.tameter.partialorder.dag

/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */
class MultiGraph {
    // --------------------------------------------------------------------------------
    // <editor-fold desc="Properties">

    private val _nodes: MutableMap<String, Node> = mutableMapOf()
    private val _graphs: MutableMap<String, Graph> = mutableMapOf()
    val nodes: Collection<Node> get() = _nodes.values
    val graphs: Collection<Graph> get() = _graphs.values

    fun findGraphById(id: String): Graph? = _graphs[id]

    fun findNodeById(id: String): Node? = _nodes[id]

    // </editor-fold>

    // --------------------------------------------------------------------------------
    // <editor-fold desc="Methods">

    fun addGraph(label: String) {
        _graphs[label] = Graph(label, this)
    }

    fun addNode(node: Node) {
        _nodes[node._id] = node
        for (graph in graphs) {
            graph.nodeAdded(node)
        }
    }

    // TODO 2016-04-02 HughG: When implementing removeNode, fail if there are connected edges.

    // </editor-fold>

    // --------------------------------------------------------------------------------
    // <editor-fold desc="Edge extensions">

    private fun nodeFromGraph(nodeType: String, nodeId: String): Node {
        return findNodeById(nodeId) ?: throw Exception("No '${nodeType}' node ${nodeId}")
    }
    // </editor-fold>
}