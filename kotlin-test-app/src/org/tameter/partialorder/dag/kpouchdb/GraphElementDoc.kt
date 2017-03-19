package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kpouchdb.AbstractPouchDoc

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

abstract class GraphElementDoc(_id: String, type: String) : AbstractPouchDoc(_id, type)