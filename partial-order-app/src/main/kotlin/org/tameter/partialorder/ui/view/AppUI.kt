package org.tameter.partialorder.ui.view

import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.*
import org.tameter.partialorder.scoring.Scoring
import org.tameter.partialorder.service.proposeEdges
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import kotlin.browser.document
import net.yested.core.html.*
import net.yested.core.properties.*
import net.yested.core.utils.*
import net.yested.ext.bootstrap3.*
import org.tameter.Databases
import org.tameter.MakeSource
import org.tameter.partialorder.source.GitHubSource
import org.tameter.partialorder.source.RedmineSource
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
                    appendText("Partial Order Application")
                    onclick = { activeTabProperty.set(DEFAULT_TAB_NAME) }
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
                    container.append {
                        div { id = "edges" }
                    }

                    renderEdges(document.getElementById("edges")!!, databases.scoringDatabase, scoring)
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

    private fun renderConfigsTab(element: Element, databases: Databases) {

        data class ConfigData(val description: String, val type: String, val data: List<Pair<String, String>>)

        databases.GetAllConfigs()
                .thenV { results ->
                    element.append {
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
                                            th { +rowData.description }
                                            th { +rowData.type }
                                        }
                                        rowData.data.forEach {
                                            tr {
                                                td { +it.first }
                                                td { +it.second }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
    }


    private fun renderNodesByRank(element: Element, nodesByCombinedRank: Map<Int, List<Node>>) {
        element.append {
            table {
                tr {
                    th { +"Rank" }
                    th { +"Source" }
                    th { +"Description" }
                }
                for (rank in nodesByCombinedRank.keys.sorted()) {
                    val nodes = nodesByCombinedRank[rank]!!
                    if (nodes.isEmpty()) {
                        console.warn("Empty node list for rank $rank")
                        continue
                    }
                    tr {
                        th {
                            attributes["rowspan"] = nodes.size.toString()
                            +rank.toString()
                        }
                        val node = nodes[0]
                        td { +node.source }
                        td { +node.description }
                    }
                    for (node in nodes.drop(1)) {
                        tr {
                            td { +node.source }
                            td { +node.description }
                        }
                    }
                }
            }
        }
    }

    private fun renderEdges(element: Element, db: PouchDB, graphs: CompositeScoring) {
        element.append {
            table {
                tr {
                    for (graph in graphs.scorings.filterIsInstance<Graph>()) {
                        td { renderEdges(this, db, graph) }
                    }
                }
            }
        }
    }

    private fun renderEdges(element: HtmlBlockTag, db: PouchDB, graph: Graph) {
        element.table {
            tr {
                th {
                    attributes["colspan"] = "2"
                    +"From"
                }
                th {
                    attributes["colspan"] = "2"
                    +"To"
                }
                th {
                    +"Remove"
                }
            }
            for (edge in graph.edges) {
                tr {
                    td { +graph.scoreById(edge.fromId).toString() }
                    td { +getNodeDescription(graph, edge.fromId) }
                    td { +graph.scoreById(edge.toId).toString() }
                    td { +getNodeDescription(graph, edge.toId) }
                    td(classes = "button") {
                        +"[X]"
                        onClickFunction = {
                            console.log(edge.toPrettyString())
                            edge.remove(db)
                        }
                    }
                }
            }
        }
    }

    private fun renderProposedEdges(
            element: Element,
            db: PouchDB,
            compositeScoring: CompositeScoring,
            possibleEdges: Collection<Edge>
                           ) {
        element.append {
            table {
                tr {
                    th {
                        +"Axis"
                    }
                    th {
                        attributes["colspan"] = "2"
                        +"From"
                    }
                    th {
                        attributes["colspan"] = "2"
                        +"To"
                    }
                }
                for (edge in possibleEdges) {
                    val scoring = compositeScoring.findScoringById(edge.graphId)!!
                    tr {
                        td { +scoring.id }
                        td { +compositeScoring.scoreById(edge.fromId).toString() }
                        td(classes = "button") {
                            +getNodeDescription(scoring, edge.fromId)
                            onClickFunction = {
                                console.log(edge.toPrettyString())
                                edge.store(db)
                            }
                        }
                        td { +compositeScoring.scoreById(edge.toId).toString() }
                        td(classes = "button") {
                            +getNodeDescription(scoring, edge.toId)
                            onClickFunction = {
                                console.log(edge.toPrettyString())
                                edge.reverse().store(db)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getNodeDescription(graph: Scoring, fromId: String) = graph.owner.findNodeById(fromId)?.description ?: "???"
}