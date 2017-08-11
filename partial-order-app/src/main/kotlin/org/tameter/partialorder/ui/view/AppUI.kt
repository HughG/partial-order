package org.tameter.partialorder.ui.view

import net.yested.core.html.*
import net.yested.core.properties.Property
import net.yested.core.utils.with
import net.yested.ext.bootstrap3.NavbarMenu
import net.yested.ext.bootstrap3.container
import net.yested.ext.bootstrap3.navbar
import org.tameter.Databases
import org.tameter.MakeSource
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.*
import org.tameter.partialorder.scoring.Scoring
import org.tameter.partialorder.service.proposeEdges
import org.tameter.partialorder.source.GitHubSource
import org.tameter.partialorder.source.RedmineSource
import org.w3c.dom.HTMLElement
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.dom.appendText

object AppUI {

    private const val DEFAULT_TAB_NAME = "Nodes"
    val activeTabProperty = Property(DEFAULT_TAB_NAME)
    val databasesProperty: Property<Databases?> = Property(null)

    init {
        fun NavbarMenu.navBarItem(name: String): Any {

            fun isActiveTab() = activeTabProperty.get() == name

            val active = Property(isActiveTab())
            activeTabProperty.onNext { active.set(isActiveTab()) }

            return item(active = active) {
                onclick = { activeTabProperty.set(name) }
                a {
                    appendText(name)
                }
            }
        }
        
        val appElement = document.getElementById("app") as HTMLElement? ?: throw Error("Failed to find app element")
        appElement.with {
            navbar(inverted = true) {
                navbar.addClass("my-custom-navbar")
                brand {
                    onclick = { activeTabProperty.set(DEFAULT_TAB_NAME) }
                    appendText("Partial Order Application")
                }
                menu {
                    navBarItem("Nodes")
                    navBarItem("Edges")
                    navBarItem("Proposed Edges")
                    navBarItem("Config")
                }
            }

            container {
                id = "container"
            }
        }
    }

    private val container: HTMLElement = document.getElementById("container") as HTMLElement?
            ?: throw Error("element of id container must be added in init() method")

    fun render(scoring: CompositeScoring) {
        val databases = databasesProperty.get()

        if(databases != null) {
            while (container.firstChild != null) {
                container.removeChild(container.firstChild!!)
            }

            when (activeTabProperty.get()) {
                "Nodes", "Default" -> {
                    val nodesByCombinedRank = scoring.nodes.groupBy { scoring.score(it) }
                    renderNodesByRank(container, nodesByCombinedRank)
                }
                "Edges" -> {
                    container.with {
                        div { id = "edges" }
                    }

                    renderEdges(document.getElementById("edges")!! as HTMLElement, databases.scoringDatabase, scoring)
                }
                "Proposed Edges" -> {
                    val proposedEdges = proposeEdges(scoring)
                    renderProposedEdges(container, databases.scoringDatabase, scoring, proposedEdges)
                }
                "Config" -> {
                    renderConfigsTab(container, databases)
                }
                else -> {
                    console.warn("Unknown tab ${activeTabProperty.get()}")
                }
            }
        }
    }

    private fun renderConfigsTab(element: HTMLElement, databases: Databases) {

        data class ConfigData(val description: String, val type: String, val data: List<Pair<String, String>>)

        databases.GetAllConfigs()
                .thenV { results ->
                    element.with {
                        results.rows.forEach {
                            val source = it.doc?.MakeSource()
                            if (source != null) {
                                val rowData = when (source) {
                                    is GitHubSource -> {
                                        val data = arrayListOf(
                                                "User" to source.spec.user,
                                                "Repo" to source.spec.repo
                                        )
                                        ConfigData(source.spec.description, "GitHub", data)
                                    }
                                    is RedmineSource -> {
                                        val data = arrayListOf(
                                                "URL" to source.spec.url,
                                                "API Key" to (source.spec.apiKey ?: ""),
                                                "Project ID" to (source.spec.projectId ?: "")
                                        )
                                        ConfigData(source.spec.description, "Redmine", data)
                                    }
                                    else -> ConfigData("Error", "Invalid", emptyList())
                                }
                                p {
                                    table {
                                        thead {
                                            th { appendText(rowData.description) }
                                            th { appendText(rowData.type) }
                                        }
                                        rowData.data.forEach {
                                            tr {
                                                td { appendText(it.first) }
                                                td { appendText(it.second) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
    }


    private fun renderNodesByRank(element: HTMLElement, nodesByCombinedRank: Map<Int, List<Node>>) {
        element.with {
            table {
                tr {
                    th { appendText("Rank") }
                    th { appendText("Source") }
                    th { appendText("Description") }
                }
                for (rank in nodesByCombinedRank.keys.sorted()) {
                    val nodes = nodesByCombinedRank[rank]!!
                    if (nodes.isEmpty()) {
                        console.warn("Empty node list for rank $rank")
                        continue
                    }
                    tr {
                        th {
                            rowSpan = nodes.size
                            appendText(rank.toString())
                        }
                        val node = nodes[0]
                        td { appendText(node.source) }
                        td { appendText(node.description) }
                    }
                    for (node in nodes.drop(1)) {
                        tr {
                            td { appendText(node.source) }
                            td { appendText(node.description) }
                        }
                    }
                }
            }
        }
    }

    private fun renderEdges(element: HTMLElement, db: PouchDB, graphs: CompositeScoring) {
        element.with {
            table {
                tr {
                    for (graph in graphs.scorings.filterIsInstance<Graph>()) {
                        td { renderEdges(this, db, graph) }
                    }
                }
            }
        }
    }

    private fun renderEdges(element: HTMLElement, db: PouchDB, graph: Graph) {
        element.table {
            tr {
                th {
                    colSpan = 2
                    appendText("From")
                }
                th {
                    colSpan = 2
                    appendText("To")
                }
                th {
                    appendText("Remove")
                }
            }
            for (edge in graph.edges) {
                tr {
                    td { appendText(graph.scoreById(edge.fromId).toString()) }
                    td { appendText(getNodeDescription(graph, edge.fromId)) }
                    td { appendText(graph.scoreById(edge.toId).toString()) }
                    td { appendText(getNodeDescription(graph, edge.toId)) }
                    td {
                        className = "button"
                        onclick = {
                            console.log(edge.toPrettyString())
                            edge.remove(db)
                        }
                        appendText("[X]")
                    }
                }
            }
        }
    }

    private fun renderProposedEdges(
            element: HTMLElement,
            db: PouchDB,
            compositeScoring: CompositeScoring,
            possibleEdges: Collection<Edge>
                           ) {
        element.with {
            table {
                tr {
                    th {
                        appendText("Axis")
                    }
                    th {
                        colSpan = 2
                        appendText("From")
                    }
                    th {
                        colSpan = 2
                        appendText("To")
                    }
                }
                for (edge in possibleEdges) {
                    val scoring = compositeScoring.findScoringById(edge.graphId)!!
                    tr {
                        td { appendText(scoring.id) }
                        td { appendText(compositeScoring.scoreById(edge.fromId).toString()) }
                        td {
                            className = "button"
                            onclick = {
                                console.log(edge.toPrettyString())
                                edge.store(db)
                            }
                            appendText(getNodeDescription(scoring, edge.fromId))
                        }
                        td { compositeScoring.scoreById(edge.toId).toString() }
                        td {
                            className = "button"
                            onclick = {
                                console.log(edge.toPrettyString())
                                edge.reverse().store(db)
                            }
                            appendText(getNodeDescription(scoring, edge.toId))
                        }
                    }
                }
            }
        }
    }

    private fun getNodeDescription(graph: Scoring, fromId: String) = graph.owner.findNodeById(fromId)?.description ?: "???"
}