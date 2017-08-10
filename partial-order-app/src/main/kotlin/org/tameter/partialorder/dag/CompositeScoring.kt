package org.tameter.partialorder.dag

import org.tameter.partialorder.scoring.NodeSet
import org.tameter.partialorder.scoring.Scoring
import org.tameter.partialorder.util.cached

/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */
class CompositeScoring(override val id: String) : NodeSet, Scoring {
    // --------------------------------------------------------------------------------
    // <editor-fold desc="Properties">

    private val _nodes: MutableMap<String, Node> = mutableMapOf()
    private val _scorings: MutableMap<String, Scoring> = mutableMapOf()
    val scorings: Collection<Scoring> get() = _scorings.values

    private val cachedScores = cached {
        val scores = _nodes.values.associate {
            node ->
            node to _scorings.values.map { it.score(node) }.sum()
        } as Map<Node, Int>
        console.log("Combined scores ...")
        for ((node, score) in scores) {
            console.log("$node: $score")
        }
        console.log("... done")
        scores
    }
    val scores by cachedScores

    fun findScoringById(id: String): Scoring? = _scorings[id]

    // </editor-fold>

    // --------------------------------------------------------------------------------
    // <editor-fold desc="Methods">

    fun addScoring(scoring: Scoring) {
        scoring.setOwner(this)
        _scorings[scoring.id] = scoring
    }

    fun removeNode(node: Node) {
        _nodes.remove(node._id)
        this.scoreChanged(node._id)
    }

    override fun addNode(node: Node) {
        _nodes[node._id] = node
        for (scoring in scorings) {
            scoring.nodeAdded(node)
        }
        this.nodeAdded(node)
    }

    // TODO 2016-04-02 HughG: When implementing removeNode, fail if there are connected edges.

    // </editor-fold>

    // --------------------------------------------------------------------------------
    // <editor-fold desc="NodeSet implementation">

    override val nodes: Collection<Node> get() = _nodes.values

    override fun findNodeById(id: String): Node? = _nodes[id]

    // </editor-fold>

    // --------------------------------------------------------------------------------
    // <editor-fold desc="Scoring implementation">

    override val owner: CompositeScoring = this

    override fun setOwner(owner: CompositeScoring) {
        throw IllegalStateException("Root scoring object is its own owner")
    }

    override fun nodeAdded(node: Node) {
        cachedScores.clear()
    }

    override fun score(node: Node): Int {
        return scores[node] ?: throw Exception("Cannot determine score of node not in node set: ${node}")
    }

    fun scoreChanged(id: String) {
        cachedScores.clear()
    }
    // </editor-fold>

}