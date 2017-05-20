if (typeof kotlin === 'undefined') {
  throw new Error("Error loading module 'kotlin-test-app'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kotlin-test-app'.");
}
this['kotlin-test-app'] = function (_, Kotlin) {
  'use strict';
  var drop = Kotlin.kotlin.collections.drop_ba2ldo$;
  var firstOrNull = Kotlin.kotlin.collections.firstOrNull_2p1efm$;
  var max = Kotlin.kotlin.collections.max_exjks8$;
  var emptyList = Kotlin.kotlin.collections.emptyList_287e2$;
  var joinToString = Kotlin.kotlin.collections.joinToString_fmv235$;
  var contains = Kotlin.kotlin.collections.contains_2ws7j4$;
  var compareBy = Kotlin.kotlin.comparisons.compareBy_bvgy4j$;
  var sortedWith = Kotlin.kotlin.collections.sortedWith_eknfly$;
  var Lazy = Kotlin.kotlin.Lazy;
  var Any = Object;
  var Regex = Kotlin.kotlin.text.Regex_61zpoe$;
  var Exception = Kotlin.kotlin.Exception;
  var toSet = Kotlin.kotlin.collections.toSet_7wnvza$;
  var Enum = Kotlin.kotlin.Enum;
  Axis.prototype = Object.create(AbstractPouchDoc.prototype);
  Axis.prototype.constructor = Axis;
  Edge_0.prototype = Object.create(DocWrapper.prototype);
  Edge_0.prototype.constructor = Edge_0;
  GraphEdge_1.prototype = Object.create(Edge_0.prototype);
  GraphEdge_1.prototype.constructor = GraphEdge_1;
  Node.prototype = Object.create(DocWrapper.prototype);
  Node.prototype.constructor = Node;
  GraphNode_0.prototype = Object.create(Node.prototype);
  GraphNode_0.prototype.constructor = GraphNode_0;
  SearchType.prototype = Object.create(Enum.prototype);
  SearchType.prototype.constructor = SearchType;
  VisitResult.prototype = Object.create(Enum.prototype);
  VisitResult.prototype.constructor = VisitResult;
  GraphElementDoc.prototype = Object.create(AbstractPouchDoc.prototype);
  GraphElementDoc.prototype.constructor = GraphElementDoc;
  EdgeDoc_1.prototype = Object.create(GraphElementDoc.prototype);
  EdgeDoc_1.prototype.constructor = EdgeDoc_1;
  NodeDoc.prototype = Object.create(GraphElementDoc.prototype);
  NodeDoc.prototype.constructor = NodeDoc;
  var DB_NAME;
  function initDB$lambda(db) {
    return addDummyData(db);
  }
  function initDB() {
    console.log('initDB');
    return resetDB().then(initDB$lambda);
  }
  function resetDB$lambda$lambda(closure$db) {
    return function (it) {
      console.log(it);
      return closure$db;
    };
  }
  function resetDB$lambda(it) {
    var db = new PouchDB(DB_NAME);
    return db.info().then(resetDB$lambda$lambda(db));
  }
  function resetDB() {
    console.log('resetDB');
    return (new PouchDB(DB_NAME)).destroy().then(resetDB$lambda);
  }
  function addDummyData$lambda(closure$db) {
    return function (results) {
      console.log('Bulk store results:');
      var tmp$;
      for (tmp$ = 0; tmp$ !== results.length; ++tmp$) {
        var element = results[tmp$];
        console.log(element);
      }
      return closure$db;
    };
  }
  function addDummyData(db) {
    console.log('addDummyData');
    console.log('addDummyData : g');
    var g = new Graph();
    console.log('addDummyData : nodes');
    var readNode = GraphNode(g, 'Investigate stuff');
    var sighNode = GraphNode(g, 'Be frustrated at difficulty of new stuff');
    var grumpNode = GraphNode(g, 'Grumble to self about difficulty of new stuff');
    var us1Node = GraphNode(g, 'Understand Promises better');
    console.log('addDummyData : edges');
    var graphEdge = GraphEdge(g, readNode, sighNode);
    console.log('addDummyData : graphEdge');
    console.log('addDummyData : graphEdge = ' + graphEdge);
    var edge1 = graphEdge;
    var edge2 = GraphEdge(g, sighNode, us1Node);
    console.log('addDummyData : dummyGraphObjects');
    var $receiver = [readNode, sighNode, grumpNode, us1Node, edge1, edge2];
    var destination = Kotlin.kotlin.collections.ArrayList_init_ww73n8$($receiver.length);
    var tmp$;
    for (tmp$ = 0; tmp$ !== $receiver.length; ++tmp$) {
      var item = $receiver[tmp$];
      destination.add_11rb$(item.doc_mmz446$_0);
    }
    var dummyGraphObjects = Kotlin.kotlin.collections.copyToArray(destination);
    var tmp$_0;
    for (tmp$_0 = 0; tmp$_0 !== dummyGraphObjects.length; ++tmp$_0) {
      var element = dummyGraphObjects[tmp$_0];
      console.log(element);
      var prop;
      for (prop in element) {
        console.log(prop);
      }
    }
    console.log('addDummyData : bulkDocs');
    return db.bulkDocs(dummyGraphObjects).then(addDummyData$lambda(db));
  }
  function main$lambda(db) {
    return loadGraph(db);
  }
  function main$lambda_0(graph) {
    listByRank(graph);
    return graph;
  }
  function main$lambda_1(graph) {
    var possibleEdges = proposeEdges(graph);
    var randomIndex = Math.floor(Math.random() * possibleEdges.size);
    var edge = firstOrNull(drop(possibleEdges, randomIndex));
    if (edge != null) {
      var graphEdge = GraphEdge_0(graph, edge);
      graph.addEdge_iaziua$(graphEdge);
      console.log('Added ' + graphEdge);
    }
     else {
      console.log('No edges to add');
    }
    listByRank(graph);
  }
  function main(args) {
    catchAndLog(initDB().then(main$lambda).then(main$lambda_0).then(main$lambda_1));
  }
  function listByRank(graph) {
    var tmp$, tmp$_0;
    console.info('Nodes by rank ...');
    var $receiver = graph.ranks.keys;
    var destination = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$();
    var tmp$_1;
    tmp$_1 = $receiver.iterator();
    while (tmp$_1.hasNext()) {
      var element = tmp$_1.next();
      var tmp$_3;
      var key = (tmp$_3 = graph.ranks.get_11rb$(element)) != null ? tmp$_3 : -1;
      var tmp$_2;
      var value = destination.get_11rb$(key);
      if (value == null) {
        var answer = Kotlin.kotlin.collections.ArrayList_init_ww73n8$();
        destination.put_xwzc9p$(key, answer);
        tmp$_2 = answer;
      }
       else {
        tmp$_2 = value;
      }
      var list = tmp$_2;
      list.add_11rb$(element);
    }
    var nodesByRank = destination;
    var maxRank = (tmp$ = max(nodesByRank.keys)) != null ? tmp$ : -1;
    for (var rank = 0; rank <= maxRank; rank++) {
      var nodes = (tmp$_0 = nodesByRank.get_11rb$(rank)) != null ? tmp$_0 : emptyList();
      var tmp$_4 = console;
      var tmp$_5 = rank.toString() + ': ';
      var transform = Kotlin.getPropertyCallableRef('description', 1, function ($receiver_0) {
        return $receiver_0.description;
      });
      var destination_0 = Kotlin.kotlin.collections.ArrayList_init_ww73n8$(Kotlin.kotlin.collections.collectionSizeOrDefault_ba2ldo$(nodes, 10));
      var tmp$_6;
      tmp$_6 = nodes.iterator();
      while (tmp$_6.hasNext()) {
        var item = tmp$_6.next();
        destination_0.add_11rb$(transform(item));
      }
      tmp$_4.info(tmp$_5 + joinToString(destination_0));
    }
  }
  function loadGraph$lambda(closure$g) {
    return function (it) {
      console.log('Nodes:');
      console.log(it);
      var $receiver = it.rows;
      var tmp$;
      for (tmp$ = 0; tmp$ !== $receiver.length; ++tmp$) {
        var element = $receiver[tmp$];
        var closure$g_0 = closure$g;
        var node = element.doc;
        if (node == null) {
          console.log('No node doc in ' + element);
        }
         else {
          var graphNode = new GraphNode_0(closure$g_0, node);
          console.log(graphNode.toPrettyString());
          closure$g_0.addNode_ib5ht3$(graphNode);
        }
      }
      return it;
    };
  }
  function loadGraph$lambda_0(closure$db) {
    return function (it) {
      var tmp$ = closure$db;
      var $receiver = AllDocsOptions();
      $receiver.startkey = 'E_';
      $receiver.endkey = 'E_\uFFFF';
      $receiver.include_docs = true;
      return tmp$.allDocs($receiver);
    };
  }
  function loadGraph$lambda_1(closure$g) {
    return function (it) {
      console.log('Edges:');
      console.log(it);
      var $receiver = it.rows;
      var tmp$;
      for (tmp$ = 0; tmp$ !== $receiver.length; ++tmp$) {
        var element = $receiver[tmp$];
        var closure$g_0 = closure$g;
        var edge = element.doc;
        if (edge == null) {
          console.log('No edge doc in ' + element);
        }
         else {
          var graphEdge = new GraphEdge_1(closure$g_0, edge);
          console.log(graphEdge.toPrettyString());
          closure$g_0.addEdge_iaziua$(graphEdge);
        }
      }
      console.log('Loading graph ... done.');
      return closure$g;
    };
  }
  function loadGraph(db) {
    var g = new Graph();
    console.log('Loading graph ...');
    var $receiver = AllDocsOptions();
    $receiver.startkey = 'N_';
    $receiver.endkey = 'N_\uFFFF';
    $receiver.include_docs = true;
    return db.allDocs($receiver).then(loadGraph$lambda(g)).then(loadGraph$lambda_0(db)).then(loadGraph$lambda_1(g));
  }
  function proposeEdges$lambda(closure$graph) {
    return function (it) {
      var tmp$, tmp$_0;
      tmp$_0 = (tmp$ = closure$graph.findNodeById_61zpoe$(it.toId)) != null ? tmp$ : Kotlin.throwNPE();
      return closure$graph.rank_tjnu7f$(tmp$_0);
    };
  }
  function proposeEdges$lambda_0(closure$maxRank, closure$graph) {
    return function (it) {
      var tmp$, tmp$_0;
      tmp$_0 = (tmp$ = closure$graph.findNodeById_61zpoe$(it.fromId)) != null ? tmp$ : Kotlin.throwNPE();
      return closure$maxRank - closure$graph.rank_tjnu7f$(tmp$_0) | 0;
    };
  }
  function proposeEdges$truncateTo($receiver, targetLength) {
    var tmp$;
    if ($receiver.length <= targetLength)
      tmp$ = $receiver;
    else {
      var endIndex = targetLength - 3 | 0;
      tmp$ = $receiver.substring(0, endIndex) + '...';
    }
    return tmp$;
  }
  function proposeEdges(graph) {
    var tmp$, tmp$_0, tmp$_1;
    var allPossibleEdges = Kotlin.kotlin.collections.LinkedHashSet_init_287e2$();
    tmp$ = graph.nodes.iterator();
    while (tmp$.hasNext()) {
      var from = tmp$.next();
      tmp$_0 = graph.nodes.iterator();
      while (tmp$_0.hasNext()) {
        var to = tmp$_0.next();
        var possibleEdge = Edge(from, to);
        if (!graph.hasPath_kap0lo$(to, from) && !contains(graph.edges, possibleEdge)) {
          allPossibleEdges.add_11rb$(possibleEdge);
        }
      }
    }
    var maxRank = graph.maxRank;
    console.log('Max rank = ' + Kotlin.toString(maxRank));
    if (maxRank != null) {
      tmp$_1 = sortedWith(allPossibleEdges, compareBy([proposeEdges$lambda(graph), proposeEdges$lambda_0(maxRank, graph)]));
    }
     else {
      tmp$_1 = emptyList();
    }
    var sortedPossibleEdges = tmp$_1;
    var truncateTo = proposeEdges$truncateTo;
    console.log('Possible Edges:');
    var tmp$_2;
    tmp$_2 = sortedPossibleEdges.iterator();
    while (tmp$_2.hasNext()) {
      var element = tmp$_2.next();
      var tmp$_3, tmp$_4;
      var fromNode = (tmp$_3 = graph.findNodeById_61zpoe$(element.fromId)) != null ? tmp$_3 : Kotlin.throwNPE();
      var toNode = (tmp$_4 = graph.findNodeById_61zpoe$(element.toId)) != null ? tmp$_4 : Kotlin.throwNPE();
      console.log(element.toPrettyString(), "'" + truncateTo(fromNode.description, 15) + "' -> '" + truncateTo(toNode.description, 15) + "'");
    }
    return allPossibleEdges;
  }
  function Cached() {
  }
  Cached.$metadata$ = {
    kind: Kotlin.Kind.INTERFACE,
    simpleName: 'Cached',
    interfaces: [Lazy]
  };
  var getValue = Kotlin.defineInlineFunction('kotlin-test-app.org.tameter.kotlinjs.getValue_bvm8ky$', function ($receiver, thisRef, property) {
    return $receiver.value;
  });
  function CachedImpl(getValue_1) {
    this.getValue_0 = getValue_1;
    this.initialized_0 = false;
    this._value_0 = null;
  }
  Object.defineProperty(CachedImpl.prototype, 'value', {
    get: function () {
      var tmp$;
      if (!this.initialized_0) {
        this._value_0 = this.getValue_0();
        this.initialized_0 = true;
      }
      return (tmp$ = this._value_0) == null || Kotlin.isType(tmp$, Any) ? tmp$ : Kotlin.throwCCE();
    }
  });
  CachedImpl.prototype.isInitialized = function () {
    return this.initialized_0;
  };
  CachedImpl.prototype.clear = function () {
    this.initialized_0 = false;
    this._value_0 = null;
  };
  CachedImpl.prototype.toString = function () {
    return this.isInitialized() ? Kotlin.toString(this.value) : 'Not currently initialized';
  };
  CachedImpl.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'CachedImpl',
    interfaces: [Cached]
  };
  function cached(initializer) {
    return new CachedImpl(initializer);
  }
  var GUID_TEMPLATE;
  function makeGuid() {
    var d = {v: Math.floor((new Date()).getTime()) + Math.floor(window.performance.now()) | 0};
    var $receiver = GUID_TEMPLATE;
    var regex = Regex('[xy]');
    var replace_20wsma$result;
    replace_20wsma$break: {
      var match_0 = regex.find_905azu$($receiver);
      if (match_0 == null) {
        replace_20wsma$result = $receiver.toString();
        break replace_20wsma$break;
      }
      var lastStart = 0;
      var length = $receiver.length;
      var sb = Kotlin.kotlin.text.StringBuilder_init_za3lpa$(length);
      do {
        var foundMatch = match_0 != null ? match_0 : Kotlin.throwNPE();
        sb.append_ezbsdh$($receiver, lastStart, foundMatch.range.start);
        var tmp$ = sb.append_gw00v9$;
        var r = Math.floor(d.v + Math.random() * 16) % 16;
        d.v = d.v / 16 | 0;
        tmp$.call(sb, (Kotlin.equals(foundMatch.value, 'x') ? r : r & 3 | 8).toString(16));
        lastStart = foundMatch.range.endInclusive + 1 | 0;
        match_0 = foundMatch.next();
      }
       while (lastStart < length && match_0 != null);
      if (lastStart < length) {
        sb.append_ezbsdh$($receiver, lastStart, length);
      }
      replace_20wsma$result = sb.toString();
    }
    var uuid = replace_20wsma$result;
    return uuid;
  }
  function AbstractPouchDoc(_id, type) {
    this.id_sthjnt$_0 = _id;
    this.type_sthjnt$_0 = type;
    this.rev_sthjnt$_0 = null;
  }
  Object.defineProperty(AbstractPouchDoc.prototype, 'id', {
    get: function () {
      return this.id_sthjnt$_0;
    },
    set: function (_id) {
      this.id_sthjnt$_0 = _id;
    }
  });
  Object.defineProperty(AbstractPouchDoc.prototype, 'type', {
    get: function () {
      return this.type_sthjnt$_0;
    },
    set: function (type) {
      this.type_sthjnt$_0 = type;
    }
  });
  Object.defineProperty(AbstractPouchDoc.prototype, 'rev', {
    get: function () {
      return this.rev_sthjnt$_0;
    },
    set: function (rev) {
      this.rev_sthjnt$_0 = rev;
    }
  });
  AbstractPouchDoc.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'AbstractPouchDoc',
    interfaces: []
  };
  function toStringForNative($receiver) {
    return '{_id: ' + $receiver.id + ', type: ' + $receiver.type + ', rev: ' + Kotlin.toString($receiver.rev) + '}';
  }
  function PouchDBOptions() {
  }
  function AllDocsOptions() {
  }
  function catchAndLog$lambda(it) {
    console.log(it);
  }
  function catchAndLog($receiver) {
    $receiver.catch(catchAndLog$lambda);
  }
  function Axis(_id) {
    AbstractPouchDoc.call(this, _id, 'A');
  }
  Axis.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'Axis',
    interfaces: [AbstractPouchDoc]
  };
  function DocWrapper(doc) {
    this.doc_mmz446$_0 = doc;
  }
  Object.defineProperty(DocWrapper.prototype, '_id', {
    get: function () {
      return this.doc_mmz446$_0.id;
    }
  });
  Object.defineProperty(DocWrapper.prototype, 'type_mmz446$_0', {
    get: function () {
      return this.doc_mmz446$_0.type;
    }
  });
  Object.defineProperty(DocWrapper.prototype, 'rev_mmz446$_0', {
    get: function () {
      return this.doc_mmz446$_0.rev;
    }
  });
  DocWrapper.prototype.equals = function (other) {
    if (this === other)
      return true;
    if (!Kotlin.isType(other, DocWrapper))
      return false;
    if (!Kotlin.equals(this._id, other._id))
      return false;
    if (!Kotlin.equals(this.type_mmz446$_0, other.type_mmz446$_0))
      return false;
    if (!Kotlin.equals(this.rev_mmz446$_0, other.rev_mmz446$_0))
      return false;
    return true;
  };
  DocWrapper.prototype.hashCode = function () {
    var result = Kotlin.hashCode(this._id);
    result = result + ((31 * result | 0) + Kotlin.hashCode(this.type_mmz446$_0)) | 0;
    var tmp$;
    result = result + ((31 * result | 0) + Kotlin.hashCode((tmp$ = this.rev_mmz446$_0) != null ? tmp$ : '')) | 0;
    return result;
  };
  DocWrapper.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'DocWrapper',
    interfaces: []
  };
  function Edge_0(doc) {
    DocWrapper.call(this, doc);
  }
  Object.defineProperty(Edge_0.prototype, 'fromId', {
    get: function () {
      return this.doc_mmz446$_0.fromId;
    }
  });
  Object.defineProperty(Edge_0.prototype, 'toId', {
    get: function () {
      return this.doc_mmz446$_0.toId;
    }
  });
  Edge_0.prototype.equals = function (other) {
    if (this === other)
      return true;
    if (!Kotlin.isType(other, Edge_0))
      return false;
    if (!Kotlin.equals(this.fromId, other.fromId))
      return false;
    if (!Kotlin.equals(this.toId, other.toId))
      return false;
    return DocWrapper.prototype.equals.call(this, other);
  };
  Edge_0.prototype.hashCode = function () {
    var result = DocWrapper.prototype.hashCode.call(this);
    result = result + ((31 * result | 0) + Kotlin.hashCode(this.fromId)) | 0;
    result = result + ((31 * result | 0) + Kotlin.hashCode(this.toId)) | 0;
    return result;
  };
  Edge_0.prototype.toString = function () {
    return 'Edge(from ' + this.fromId + ', to ' + this.toId + ', doc ' + this.doc_mmz446$_0 + ')';
  };
  Edge_0.prototype.toPrettyString = function () {
    return 'Edge ' + this.fromId + ' to ' + this.toId;
  };
  Edge_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'Edge',
    interfaces: [DocWrapper]
  };
  function Edge(from, to) {
    return new Edge_0(EdgeDoc(from.doc_mmz446$_0.id, to.doc_mmz446$_0.id));
  }
  function Edge_1(edge) {
    return new Edge_0(EdgeDoc_0(edge.doc_mmz446$_0));
  }
  function store$lambda(this$store) {
    return function (result) {
      if (!result.ok) {
        throw new Exception('Failed to store ' + this$store.doc_mmz446$_0);
      }
      this$store.doc_mmz446$_0.rev = result.rev;
      return this$store;
    };
  }
  function store($receiver, db) {
    return db.put($receiver.doc_mmz446$_0).then(store$lambda($receiver));
  }
  function Graph() {
    this._nodes_0 = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$();
    this._edges_0 = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$();
    this.cachedHasPathFrom_0 = cached(Graph$cachedHasPathFrom$lambda(this));
    this.hasPathFrom$delegate = this.cachedHasPathFrom_0;
    this.cachedRanks_0 = cached(Graph$cachedRanks$lambda(this));
    this.ranks$delegate = this.cachedRanks_0;
  }
  Object.defineProperty(Graph.prototype, 'nodes', {
    get: function () {
      return this._nodes_0.values;
    }
  });
  Object.defineProperty(Graph.prototype, 'edges', {
    get: function () {
      return this._edges_0.values;
    }
  });
  Graph.prototype.findNodeById_61zpoe$ = function (id) {
    return this._nodes_0.get_11rb$(id);
  };
  Graph.prototype.findEdge_tjhv8m$ = function (edge) {
    return this._edges_0.get_11rb$(edge);
  };
  Object.defineProperty(Graph.prototype, 'hasPathFrom', {
    get: function () {
      var $receiver = this.hasPathFrom$delegate;
      new Kotlin.PropertyMetadata('hasPathFrom');
      return $receiver.value;
    }
  });
  Object.defineProperty(Graph.prototype, 'ranks', {
    get: function () {
      var $receiver = this.ranks$delegate;
      new Kotlin.PropertyMetadata('ranks');
      return $receiver.value;
    }
  });
  Object.defineProperty(Graph.prototype, 'maxRank', {
    get: function () {
      return max(this.ranks.values);
    }
  });
  Graph.prototype.addNode_ib5ht3$ = function (node) {
    if (!Kotlin.equals(node.graph, this)) {
      throw new Exception('Cannot add node because it belongs to a different graph: ' + node);
    }
    this.cachedRanks_0.clear();
    var $receiver = this._nodes_0;
    var key = node._id;
    $receiver.put_xwzc9p$(key, node);
  };
  Graph.prototype.addEdge_iaziua$ = function (edge) {
    if (!Kotlin.equals(edge.graph, this)) {
      throw new Exception('Cannot add edge because it belongs to a different graph: ' + edge);
    }
    if (this.hasPath_kap0lo$(edge.to, edge.from)) {
      throw new Exception('Cannot add edge because it would create a cycle: ' + edge);
    }
    this.cachedHasPathFrom_0.clear();
    this.cachedRanks_0.clear();
    this._edges_0.put_xwzc9p$(edge, edge);
  };
  Object.defineProperty(Graph.prototype, 'roots', {
    get: function () {
      var result = Kotlin.kotlin.collections.LinkedHashSet_init_287e2$();
      result.addAll_brywnq$(this.nodes);
      var tmp$;
      tmp$ = this.edges.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        result.remove_11rb$(element.to);
      }
      return result;
    }
  });
  Graph.prototype.hasPath_kap0lo$ = function (from, to) {
    var tmp$, tmp$_0;
    if (!Kotlin.equals(from.graph, this) || !Kotlin.equals(to.graph, this)) {
      return false;
    }
    if (from != null ? from.equals(to) : null) {
      return true;
    }
    return (tmp$_0 = (tmp$ = this.hasPathFrom.get_11rb$(to)) != null ? tmp$.contains_11rb$(from) : null) != null ? tmp$_0 : false;
  };
  Graph.prototype.rank_tjnu7f$ = function (node) {
    var tmp$;
    tmp$ = this.ranks.get_11rb$(node);
    if (tmp$ == null) {
      throw new Exception('Cannot determine rank of node not in graph: ' + node);
    }
    return tmp$;
  };
  function Graph$cachedHasPathFrom$lambda$lambda(closure$hasPathFrom) {
    return function (f, f_0, node, f_1, prevNode) {
      var tmp$;
      var hasPathFromNodeQ = closure$hasPathFrom.get_11rb$(node);
      if (hasPathFromNodeQ == null) {
        var tmp$_0 = closure$hasPathFrom;
        var value = Kotlin.kotlin.collections.ArrayList_init_ww73n8$();
        tmp$_0.put_xwzc9p$(node, value);
        hasPathFromNodeQ = closure$hasPathFrom.get_11rb$(node);
      }
      var hasPathFromNode = hasPathFromNodeQ != null ? hasPathFromNodeQ : Kotlin.throwNPE();
      if (prevNode != null) {
        hasPathFromNode.add_11rb$(prevNode);
        var hasPathsFromPrevNode = (tmp$ = closure$hasPathFrom.get_11rb$(prevNode)) != null ? tmp$ : Kotlin.throwNPE();
        hasPathFromNode.addAll_brywnq$(hasPathsFromPrevNode);
      }
      return VisitResult$Continue_getInstance();
    };
  }
  function Graph$cachedHasPathFrom$lambda(this$Graph) {
    return function () {
      var hasPathFrom = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$();
      search(this$Graph, SearchType$DepthFirst_getInstance(), Graph$cachedHasPathFrom$lambda$lambda(hasPathFrom));
      return hasPathFrom;
    };
  }
  function Graph$cachedRanks$lambda$lambda(closure$ranks) {
    return function (index, depth, node, f, prevNode) {
      console.log(index.toString() + ' ' + depth + " '" + node.description + "' <- '" + Kotlin.toString(prevNode != null ? prevNode.description : null) + "'");
      if (!closure$ranks.containsKey_11rb$(node)) {
        closure$ranks.put_xwzc9p$(node, depth);
      }
      return VisitResult$Continue_getInstance();
    };
  }
  function Graph$cachedRanks$lambda(this$Graph) {
    return function () {
      var ranks = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$();
      console.log('Caching ranks ...');
      search(this$Graph, SearchType$DepthFirst_getInstance(), Graph$cachedRanks$lambda$lambda(ranks));
      console.log('Caching ranks ... done');
      return ranks;
    };
  }
  Graph.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'Graph',
    interfaces: []
  };
  function GraphEdge_1(graph, doc) {
    Edge_0.call(this, doc);
    this.graph = graph;
    this.from;
    this.to;
    this.graph.addEdge_iaziua$(this);
  }
  Object.defineProperty(GraphEdge_1.prototype, 'from', {
    get: function () {
      return this.nodeFromGraph_0('from', this.doc_mmz446$_0.fromId);
    }
  });
  Object.defineProperty(GraphEdge_1.prototype, 'to', {
    get: function () {
      return this.nodeFromGraph_0('to', this.doc_mmz446$_0.toId);
    }
  });
  GraphEdge_1.prototype.toString = function () {
    return 'GraphEdge(from ' + this.from + ', to ' + this.to + ', doc ' + this.doc_mmz446$_0 + ')';
  };
  GraphEdge_1.prototype.toPrettyString = function () {
    return 'GraphEdge ' + this.from.toPrettyString() + ' to ' + this.to.toPrettyString();
  };
  GraphEdge_1.prototype.nodeFromGraph_0 = function (nodeType, nodeId) {
    var tmp$;
    tmp$ = this.graph.findNodeById_61zpoe$(nodeId);
    if (tmp$ == null) {
      throw new Exception("No '" + nodeType + "' node " + nodeId);
    }
    return tmp$;
  };
  GraphEdge_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'GraphEdge',
    interfaces: [Edge_0]
  };
  function GraphEdge_0(graph, edge) {
    return new GraphEdge_1(graph, EdgeDoc(edge.fromId, edge.toId));
  }
  function GraphEdge(graph, from, to) {
    return new GraphEdge_1(graph, EdgeDoc(from.doc_mmz446$_0.id, to.doc_mmz446$_0.id));
  }
  function GraphEdge_2(graph, edge) {
    return new GraphEdge_1(graph, EdgeDoc_0(edge.doc_mmz446$_0));
  }
  function GraphNode_0(graph, doc) {
    Node.call(this, doc);
    this.graph = graph;
    this.graph.addNode_ib5ht3$(this);
  }
  GraphNode_0.prototype.outgoing = function () {
    var $receiver = this.graph.edges;
    var destination = Kotlin.kotlin.collections.ArrayList_init_ww73n8$();
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var tmp$_0;
      if ((tmp$_0 = element.from) != null ? tmp$_0.equals(this) : null) {
        destination.add_11rb$(element);
      }
    }
    return toSet(destination);
  };
  GraphNode_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'GraphNode',
    interfaces: [Node]
  };
  function GraphNode(graph, description) {
    var $receiver = new NodeDoc(makeGuid(), '');
    $receiver.description = description;
    return new GraphNode_0(graph, $receiver);
  }
  function GraphNode_1(graph, node) {
    return new GraphNode_0(graph, NodeDoc_0(node.doc_mmz446$_0));
  }
  function Node(doc) {
    DocWrapper.call(this, doc);
  }
  Object.defineProperty(Node.prototype, 'description', {
    get: function () {
      return this.doc_mmz446$_0.description;
    }
  });
  Node.prototype.equals = function (other) {
    if (this === other)
      return true;
    if (!Kotlin.isType(other, Node))
      return false;
    if (!Kotlin.equals(this.description, other.description))
      return false;
    return DocWrapper.prototype.equals.call(this, other);
  };
  Node.prototype.hashCode = function () {
    var result = DocWrapper.prototype.hashCode.call(this);
    result = result + ((31 * result | 0) + Kotlin.hashCode(this.description)) | 0;
    return result;
  };
  Node.prototype.toString = function () {
    return 'GraphNode(dscr ' + this.description + ', doc ' + toStringForNative_0(this.doc_mmz446$_0) + ')';
  };
  Node.prototype.toPrettyString = function () {
    return '{G ' + this._id + ' ' + this.description + '}';
  };
  Node.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'Node',
    interfaces: [DocWrapper]
  };
  function Node_0(description) {
    var $receiver = new NodeDoc(makeGuid(), '');
    $receiver.description = description;
    var doc = $receiver;
    var node = new Node(doc);
    return node;
  }
  function Node_1(node) {
    return new Node(NodeDoc_0(node.doc_mmz446$_0));
  }
  function store$lambda_0(this$store) {
    return function (result) {
      if (!result.ok) {
        throw new Exception('Failed to store ' + this$store.doc_mmz446$_0);
      }
      this$store.doc_mmz446$_0.rev = result.rev;
      return this$store;
    };
  }
  function store_0($receiver, db) {
    return db.put($receiver.doc_mmz446$_0).then(store$lambda_0($receiver));
  }
  function SearchType(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function SearchType_initFields() {
    SearchType_initFields = function () {
    };
    SearchType$BreadthFirst_instance = new SearchType('BreadthFirst', 0);
    SearchType$DepthFirst_instance = new SearchType('DepthFirst', 1);
  }
  var SearchType$BreadthFirst_instance;
  function SearchType$BreadthFirst_getInstance() {
    SearchType_initFields();
    return SearchType$BreadthFirst_instance;
  }
  var SearchType$DepthFirst_instance;
  function SearchType$DepthFirst_getInstance() {
    SearchType_initFields();
    return SearchType$DepthFirst_instance;
  }
  SearchType.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'SearchType',
    interfaces: [Enum]
  };
  function SearchType$values() {
    return [SearchType$BreadthFirst_getInstance(), SearchType$DepthFirst_getInstance()];
  }
  SearchType.values = SearchType$values;
  function SearchType$valueOf(name) {
    switch (name) {
      case 'BreadthFirst':
        return SearchType$BreadthFirst_getInstance();
      case 'DepthFirst':
        return SearchType$DepthFirst_getInstance();
      default:Kotlin.throwISE('No enum constant org.tameter.partialorder.dag.SearchType.' + name);
    }
  }
  SearchType.valueOf_61zpoe$ = SearchType$valueOf;
  function VisitResult(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function VisitResult_initFields() {
    VisitResult_initFields = function () {
    };
    VisitResult$Return_instance = new VisitResult('Return', 0);
    VisitResult$Continue_instance = new VisitResult('Continue', 1);
    VisitResult$Cancel_instance = new VisitResult('Cancel', 2);
  }
  var VisitResult$Return_instance;
  function VisitResult$Return_getInstance() {
    VisitResult_initFields();
    return VisitResult$Return_instance;
  }
  var VisitResult$Continue_instance;
  function VisitResult$Continue_getInstance() {
    VisitResult_initFields();
    return VisitResult$Continue_instance;
  }
  var VisitResult$Cancel_instance;
  function VisitResult$Cancel_getInstance() {
    VisitResult_initFields();
    return VisitResult$Cancel_instance;
  }
  VisitResult.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'VisitResult',
    interfaces: [Enum]
  };
  function VisitResult$values() {
    return [VisitResult$Return_getInstance(), VisitResult$Continue_getInstance(), VisitResult$Cancel_getInstance()];
  }
  VisitResult.values = VisitResult$values;
  function VisitResult$valueOf(name) {
    switch (name) {
      case 'Return':
        return VisitResult$Return_getInstance();
      case 'Continue':
        return VisitResult$Continue_getInstance();
      case 'Cancel':
        return VisitResult$Cancel_getInstance();
      default:Kotlin.throwISE('No enum constant org.tameter.partialorder.dag.VisitResult.' + name);
    }
  }
  VisitResult.valueOf_61zpoe$ = VisitResult$valueOf;
  function SearchResult(path, found) {
    this.path = path;
    this.found = found;
  }
  SearchResult.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'SearchResult',
    interfaces: []
  };
  SearchResult.prototype.component1 = function () {
    return this.path;
  };
  SearchResult.prototype.component2 = function () {
    return this.found;
  };
  SearchResult.prototype.copy_nwvlpx$ = function (path, found) {
    return new SearchResult(path === void 0 ? this.path : path, found === void 0 ? this.found : found);
  };
  SearchResult.prototype.toString = function () {
    return 'SearchResult(path=' + Kotlin.toString(this.path) + (', found=' + Kotlin.toString(this.found)) + ')';
  };
  SearchResult.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.path) | 0;
    result = result * 31 + Kotlin.hashCode(this.found) | 0;
    return result;
  };
  SearchResult.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.path, other.path) && Kotlin.equals(this.found, other.found)))));
  };
  function search$lambda(f, f_0, f_1, f_2, f_3) {
    return VisitResult$Continue_getInstance();
  }
  function search($receiver, searchType, fn) {
    if (fn === void 0)
      fn = search$lambda;
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3;
    var queue = Kotlin.kotlin.collections.ArrayList_init_ww73n8$();
    var connectedNodes = Kotlin.kotlin.collections.ArrayList_init_ww73n8$();
    var connectedBy = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$();
    var id2depth = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$();
    var visited = Kotlin.kotlin.collections.LinkedHashSet_init_287e2$();
    var index = 0;
    var found = null;
    tmp$ = $receiver.roots.iterator();
    while (tmp$.hasNext()) {
      var root = tmp$.next();
      queue.add_wxm5ur$(0, root);
      if (searchType === SearchType$BreadthFirst_getInstance()) {
        visited.add_11rb$(root._id);
        connectedNodes.add_11rb$(root);
      }
      var key = root._id;
      id2depth.put_xwzc9p$(key, 0);
    }
    while (queue.size !== 0) {
      if (Kotlin.equals(searchType, SearchType$BreadthFirst_getInstance()))
        tmp$_0 = queue.removeAt_za3lpa$(0);
      else if (Kotlin.equals(searchType, SearchType$DepthFirst_getInstance()))
        tmp$_0 = queue.removeAt_za3lpa$(queue.size - 1 | 0);
      else
        tmp$_0 = Kotlin.noWhenBranchMatched();
      var v = tmp$_0;
      if (searchType === SearchType$DepthFirst_getInstance()) {
        if (visited.add_11rb$(v._id)) {
          connectedNodes.add_wxm5ur$(connectedNodes.size, v);
        }
      }
      var depthQ = id2depth.get_11rb$(v._id);
      var depth = depthQ != null ? depthQ : Kotlin.throwNPE();
      var prevEdge = connectedBy.get_11rb$(v._id);
      var prevNode = prevEdge != null ? prevEdge.from : null;
      var ret = fn((tmp$_1 = index, index = tmp$_1 + 1 | 0, tmp$_1), depth, v, prevEdge, prevNode);
      if (ret === VisitResult$Return_getInstance()) {
        found = v;
        break;
      }
      if (ret === VisitResult$Cancel_getInstance()) {
        break;
      }
      var vwEdges = v.outgoing();
      tmp$_2 = vwEdges.iterator();
      while (tmp$_2.hasNext()) {
        var e = tmp$_2.next();
        var w = e.to;
        queue.add_wxm5ur$(queue.size, w);
        if (searchType === SearchType$BreadthFirst_getInstance()) {
          visited.add_11rb$(w._id);
          connectedNodes.add_wxm5ur$(connectedNodes.size, w);
        }
        var key_0 = w._id;
        connectedBy.put_xwzc9p$(key_0, e);
        var key_1 = w._id;
        var value = ((tmp$_3 = id2depth.get_11rb$(v._id)) != null ? tmp$_3 : 0) + 1 | 0;
        id2depth.put_xwzc9p$(key_1, value);
      }
    }
    var destination = Kotlin.kotlin.collections.ArrayList_init_ww73n8$();
    var tmp$_4;
    tmp$_4 = connectedNodes.iterator();
    while (tmp$_4.hasNext()) {
      var element = tmp$_4.next();
      var tmp$_5;
      if ((tmp$_5 = connectedBy.get_11rb$(element._id)) != null) {
        destination.add_11rb$(tmp$_5);
      }
    }
    var path = destination;
    return new SearchResult(path, found);
  }
  function EdgeDoc_1(_id, fromId, toId) {
    GraphElementDoc.call(this, _id, 'E');
    this.fromId = fromId;
    this.toId = toId;
  }
  EdgeDoc_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'EdgeDoc',
    interfaces: [GraphElementDoc]
  };
  function EdgeDoc(from, to) {
    return new EdgeDoc_1('f_' + from + '_t_' + to, from, to);
  }
  function EdgeDoc_0(doc) {
    return EdgeDoc(doc.fromId, doc.toId);
  }
  function GraphElementDoc(_id, type) {
    AbstractPouchDoc.call(this, _id, type);
  }
  GraphElementDoc.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'GraphElementDoc',
    interfaces: [AbstractPouchDoc]
  };
  function NodeDoc(_id, description) {
    GraphElementDoc.call(this, _id, 'N');
    this.description = description;
  }
  NodeDoc.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: 'NodeDoc',
    interfaces: [GraphElementDoc]
  };
  function NodeDoc_0(doc) {
    return new NodeDoc(doc.id, doc.description);
  }
  function toStringForNative_0($receiver) {
    return '{' + toStringForNative($receiver) + '; description: ' + $receiver.description + '}';
  }
  var package$org = _.org || (_.org = {});
  var package$tameter = package$org.tameter || (package$org.tameter = {});
  package$tameter.main_kand9s$ = main;
  package$tameter.listByRank_7kqed7$ = listByRank;
  package$tameter.loadGraph_ihhzkl$ = loadGraph;
  package$tameter.proposeEdges_7kqed7$ = proposeEdges;
  var package$kotlinjs = package$tameter.kotlinjs || (package$tameter.kotlinjs = {});
  package$kotlinjs.Cached = Cached;
  package$kotlinjs.getValue_bvm8ky$ = getValue;
  package$kotlinjs.cached_klfg04$ = cached;
  Object.defineProperty(package$kotlinjs, 'GUID_TEMPLATE', {
    get: function () {
      return GUID_TEMPLATE;
    }
  });
  package$kotlinjs.makeGuid = makeGuid;
  var package$kpouchdb = package$tameter.kpouchdb || (package$tameter.kpouchdb = {});
  package$kpouchdb.AbstractPouchDoc = AbstractPouchDoc;
  package$kpouchdb.toStringForNative_sthjo4$ = toStringForNative;
  package$kpouchdb.PouchDBOptions = PouchDBOptions;
  package$kpouchdb.AllDocsOptions = AllDocsOptions;
  var package$promise = package$kotlinjs.promise || (package$kotlinjs.promise = {});
  package$promise.catchAndLog_u071ol$ = catchAndLog;
  var package$partialorder = package$tameter.partialorder || (package$tameter.partialorder = {});
  var package$dag = package$partialorder.dag || (package$partialorder.dag = {});
  package$dag.Axis = Axis;
  package$dag.DocWrapper = DocWrapper;
  package$dag.Edge = Edge_0;
  package$dag.Edge_42fndi$ = Edge;
  package$dag.Edge_tjhv8m$ = Edge_1;
  package$dag.store_7t3wyq$ = store;
  package$dag.Graph = Graph;
  package$dag.GraphEdge = GraphEdge_1;
  package$dag.GraphEdge_gogurl$ = GraphEdge_0;
  package$dag.GraphEdge_nc81vh$ = GraphEdge;
  package$dag.GraphEdge_aancvd$ = GraphEdge_2;
  package$dag.GraphNode = GraphNode_0;
  package$dag.GraphNode_mrsr1l$ = GraphNode;
  package$dag.GraphNode_aatbu6$ = GraphNode_1;
  package$dag.Node = Node;
  package$dag.Node_61zpoe$ = Node_0;
  package$dag.Node_tjnu7f$ = Node_1;
  package$dag.store_5lwfrb$ = store_0;
  Object.defineProperty(SearchType, 'BreadthFirst', {
    get: SearchType$BreadthFirst_getInstance
  });
  Object.defineProperty(SearchType, 'DepthFirst', {
    get: SearchType$DepthFirst_getInstance
  });
  package$dag.SearchType = SearchType;
  Object.defineProperty(VisitResult, 'Return', {
    get: VisitResult$Return_getInstance
  });
  Object.defineProperty(VisitResult, 'Continue', {
    get: VisitResult$Continue_getInstance
  });
  Object.defineProperty(VisitResult, 'Cancel', {
    get: VisitResult$Cancel_getInstance
  });
  package$dag.VisitResult = VisitResult;
  package$dag.SearchResult = SearchResult;
  package$dag.search_qabqoc$ = search;
  var package$kpouchdb_0 = package$dag.kpouchdb || (package$dag.kpouchdb = {});
  package$kpouchdb_0.EdgeDoc = EdgeDoc_1;
  package$kpouchdb_0.EdgeDoc_puj7f4$ = EdgeDoc;
  package$kpouchdb_0.EdgeDoc_o6n9uk$ = EdgeDoc_0;
  package$kpouchdb_0.GraphElementDoc = GraphElementDoc;
  package$kpouchdb_0.NodeDoc = NodeDoc;
  package$kpouchdb_0.NodeDoc_szgd8h$ = NodeDoc_0;
  package$kpouchdb_0.toStringForNative_ovdbf4$ = toStringForNative_0;
  DB_NAME = 'http://localhost:5984/ranking';
  GUID_TEMPLATE = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx';
  Kotlin.defineModule('kotlin-test-app', _);
  main([]);
  return _;
}(typeof this['kotlin-test-app'] === 'undefined' ? {} : this['kotlin-test-app'], kotlin);

//@ sourceMappingURL=kotlin-test-app.js.map
