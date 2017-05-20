package org.tameter.partialorder.dag

import org.tameter.partialorder.dag.kpouchdb.AxisDoc

class Axis<E>(doc: AxisDoc<E>) : DocWrapper<AxisDoc<E>>(doc) {
//    var edges: Array<E>

    override fun toPrettyString(): String {
        TODO("not implemented")
    }
}