package org.tameter.partialorder.scoring

import org.tameter.partialorder.dag.Node

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */
interface NodeSet {
    val nodes: Collection<Node>
    fun addNode(node: Node)
    fun findNodeById(id: String): Node?
}