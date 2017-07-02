package org.tameter.partialorder.scoring

import org.tameter.partialorder.dag.Node

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */
interface Scoring {
    val id: String

    val owner: NodeSet
    fun setOwner(owner: NodeSet)

    fun nodeAdded(node: Node)

    fun score(node: Node): Int

    fun scoreById(id: String): Int? {
        return owner.findNodeById(id)?.let { score(it) }
    }
}