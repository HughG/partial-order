package org.tameter.kpouchdb

interface PouchDoc {
    var _id: String
    var type: String
    var rev: String?
}