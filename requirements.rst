Introduction
============

This is a Requirements Specification in Tom Gilb's Planguage.

Abbreviations:

:DC: Design Constraint
:DI: Design Idea
:FR: Function Requirement
:PR: Performance Requirement
:QR: Quality Requirement
:RC: Resource Constraint
:Sys: System
:WCR: Workload Capacity Requirement

Sys:PartialOrder
================

:Vision: I'm looking for one or more pieces of software I can use to derive partial rankings for some set of items. By "partial ranking" I mean that several items can end up with the same rank.

:Mission: Help people put things in order, to make it easier to decide what to do next.

:Description: I will create some number of criteria or axes on which to partially rank each thing, then have some function to derive an overall ranking -- for example, a linear combination of the individual rankings.

:Notes: Some time after I came up with the idea for this, I read "Systematic (Software) Innovation" by Darell Mann, a software-specialised book on TRIZ, and realised I'm applying Principle 17, "Another Dimension", by going from 1D (a "to do" list) or 2D ("urgent" vs "important") to a user-configurable number of dimensions.  I wonder what Contradiction(s) I'm resolving thereby and what other Principles I might apply to make it even better.  I suspect I'm trying to improve Accuracy (fidelity to reality) -- or maybe Adaptability, or Ease of Use -- without worsening Interface, Ease of Use, System Complexity, or Control Complexity.

    Or, looking at Trends, I'm first going for Increasing Use of Dimensions (or is it Degrees of Freedom) and possibly Connections (Discretely Switchable -- graph edges -- so, room for improvement to Continuously switchable?).  If I allow combination of other kinds of ordering like explicit numerical things such as relative date ordering, I may then be going for something like Mono-Bi-Poly (Various).  I could imagine adding Nesting (Down) by allowing for groups of "axes" which can be turned on and off in terms of their contribution to the output partial ranking.  Or maybe Nesting (Up) by making it a plugin or callable service.

WCR:Items.Number
----------------

I expect to handle at most a few hundred items at any one time, possibly far fewer.

FR:Input.Ranking.Derived
------------------------

I want to be able to derive the rankings on each axis from other sources.

:Sub-Functions:
  - `FR:Input.Ranking.ExplicitNumerical`_
  - `FR:Input.PartialOrdering`_

FR:Input.Ranking.ExplicitNumerical
----------------------------------

I want to be able to directly provide numerical values, integral or real, as input rankings.

FR:Input.PartialOrdering
------------------------

I want to be able to provide an explicit partial ordering from which to derive the partial ranking.

:Description: To look at it another way, a directed acyclic graph expresses a partial ordering, in which case the derived ranking I want is just the rank of each node in the graph.

:Linked To:
  - `FR:Input.Change.Incremental`_

:Is Impacted By:
  - `DI:PartialOrdering.SQL`_
  - `DI:PartialOrdering.NoSQL`_
  - `DI:PartialOrdering.GraphDB`_

FR:Input.PartialOrdering.SupportingItems
----------------------------------------

[When: Future] I'd like to be able to break an item down into sub-items or, more generally, "supporting items", where one item may support doing several other (conceptually higher-level) items.  There's maybe some half-understood requirement about the system not asking you the relative ordering of an item versus something it supports, because they must somehow be the same -- but maybe not, if one item supports several things, some perhaps only incidentally.

:Related To:
  - `FR:Input.PartialOrdering.SupportingItems.Goals`_

FR:Input.PartialOrdering.SupportingItems.Goals
----------------------------------------------

[When: Future] I'd like to be able to say that item A supports goals B and C, and item X supports goals Y and Z, and have the system work out something about the relative ordering and/or relative ranking of A and X based on the relationships between B, C, X, and Y.  What I'm getting at is that an item which supports more important goals and/or many goals should possibly be done earlier.

FR:Input.Change.Incremental
---------------------------

I want to be able to add and remove items over time, and add and remove new criteria and combining functions.

:Sub-Functions:
  - `FR:Input.Change.Incremental.DeleteNodes`_

:Quality Requirements:
  - `QR:Input.PartialOrdering.Change.Incremental.Ease`_
  - `QR:Input.PartialOrdering.Single.Add.Time`_
  - `QR:Input.PartialOrdering.Multiple.Add.Time`_

FR:Input.Change.Incremental.DeleteNodes
---------------------------------------

When a node is deleted, that should not change the partial ordering of any other nodes.

:Description: When a node B is to be deleted then, for all A, C such that there are edges A -> B -> C, a new edge A -> C must be added (and edges A -> B and B -> C will of course be deleted).


QR:Input.PartialOrdering.Change.Incremental.Ease
------------------------------------------------

In the case of an input ranking derived from a partial ordering, I want to be able to edit the ordering fairly *easily*: *quickly* and without *frustration*.

QR:Input.PartialOrdering.Single.Add.Time
----------------------------------------

After describing a new item, it should take *very little* time to position it on any given existing axis derived from a partial ordering.

:Scale: seconds to position a new item

:Goal: 60

:Is Impacted By:
  - `DI:Input.PartialOrdering.SuggestEdges.BinarySearch`_

QR:Input.PartialOrdering.Multiple.Add.Time
------------------------------------------

After describing a new item, it should take *little* time to position it on *3-4* axes derived from a partial ordering.

:Scale: seconds to position a new item

:Goal: 120

:Is Impacted By:
  - `DI:Input.PartialOrdering.SuggestEdges.MinimizeRankSize`_

DC:Input.Storage.Format.Diffable
--------------------------------

I want to be able to have partial orderings, explicit rankings, and combining functions (or, their parameters) under version control in an easily-diffable way, so no binary storage formats.

:Note: I don't currently require any special functional integration with version control systems, just a suitable data format.

:Is Impacted By:
  - `DI:PartialOrdering.NoSQL`_ because these often store JSON, which is diffable if you format it with line breaks and consistent ordering.

FR:Input.CombiningFunction
--------------------------

I want to be able to have any number of combining functions to choose the output ranking.

:Quality Requirements:
  - `QR:Input.CombiningFunction.Select.Ease`_
  
QR:Input.CombiningFunction.Select.Ease
--------------------------------------

I want to be able to *easily* switch the output between using any one of the combining functions: *quickly* and without *frustration*.

DC:Platform
-----------

Ideally the system will be usable on both desktop and mobile (Android 6) devices.

FR:Access.OnlineAndOffline
--------------------------

Even more ideally, it will be usable both online and offline.

:Issue: What should be done to resolve conflicts if edits are made on multiple devices "concurrently"?

:Is Imapacted By:
  - `DI:Platform.JVM`_
  - `DI:Platform.Web`_

FR:Export.Data
--------------

Ideally I could get at least a list of item IDs out, and maybe also rankings, in such a way as to sync semi-automatically with other systems. (Not sure what, but I'm bound to come up with something ...)

FR:Multi-User
-------------

It's fine if it's single-user, though multiple-user would be a bonus.

FR:ServiceConnections
---------------------

[When: Future] The system could be configured to automatically add or remove nodes based on some external database or, equivalently, some external source of create/update/delete events.  It could also be configured to send the derived partial ranking (or several of them?) to another service -- possibly even the same service, to allow it to re-order its items.  For example, Trello could be one such service.

RC:System.Creation.Effort
-------------------------

It's fine if I have to write a bit of code to tie things together, but I don't want to have to write layers of GUI, DB/VCS management, authentication, etc. if I can avoid it.

RC:System.Money
---------------

I don't mind paying a little for this – maybe up to £200 total, or £10/month.

DI:PartialOrdering.SQL
----------------------

Use SQL relations to represent the graph edges.

DI:PartialOrdering.NoSQL
------------------------

Use a NoSQL-style document with ID references between nodes.

DI:PartialOrdering.GraphDB
--------------------------

Use a "graph database" -- is there such a thing, distinct from "NoSQL"?

DI:Platform.JVM
---------------

Write web and Android apps in a JVM-based language.

:Sub-Designs:
  - `DI:Language.JVM.Kotlin`_
  - `DI:Language.JVM.Ceylon`_

DI:Platform.Web
---------------

Write a web app with HTML 5 offline features, for use on desktop and mobile (rather than writing a separate Android app).

DI:Language.JVM.Kotlin
----------------------

Try using IntelliJ's Kotlin, which is a static improvement to Java, closely compatible with Java.

DI:Language.JVM.Ceylon
----------------------

Try using Ceylon, which is a static language significantly different from Java.

DI:UI
-----

The user interface could consist of 3 sections or tabs:
  # One to enter new items.
  # One to show a list of suggested edges and let the user select or reject them.
  # One to show a list of items grouped by rank, with the groups in ascending order.

It might make sense to combine the first and last, by having an "unranked" group at the top (which would really be all nodes of rank 0 which have no outgoing edges) and allowing the user to add nodes directly in that list some how.

I'm not sure what the UI for adding or editing a node should be.  For now I just expect them to have a very short title and maybe a longer text description.

DI:UI.Reactive
--------------

The UI could clearly benefit from a reactive approach (and I would find it interesting).  For an HTML 5 app, likely candidates would be `RxJS <https://github.com/Reactive-Extensions/RxJS>`_, linked from `ReactiveX <http://reactivex.io/>`_ or Facebook's React (because I know there's a Kotlin wrapper `Reakt <https://github.com/andrewoma/reakt>`_).  For a JVM app, ReactiveX also has a "Reactive Kotlin" flavour.

DI:Input.PartialOrdering.SuggestEdges
-------------------------------------

Once nodes are added, the system could suggest edges to add based on the current state of the (multi-)graph, ordered in some way which is an attempt to try to minimise the number of edges which need to be added (or rejected) before I have a clear ordering.

FR:Input.PartialOrdering.SuggestEdges.RejectedEdges
---------------------------------------------------

[When: `DI:Input.PartialOrdering.SuggestEdges`_] If the system makes suggestions I must be able to reject them and have the system not offer them again.  I must then also be able to take back a rejection so that the system can offer it again.  I might need a way to see and/or search through all rejected edges.

DI:Input.PartialOrdering.SuggestEdges.NonRedundant
--------------------------------------------------

The system could avoid offering any redundant edges to be added; i.e., if one axis of the multi-graph already has edges A -> B ad B -> C, the system could avoid offering A -> C.

DI:Input.PartialOrdering.SuggestEdges.CutOff
--------------------------------------------

The system could stop offering edges to add once the size of rank 0 (i.e., the number of nodes with rank 0) is below a certain threshold (e.g., less than 4), because that should mean that will be *easy enough* to decide what to do next.

This could extend to a number of separate thresholds for the top N ranks.

The system might not stop offering edges altogether but it might indicate somehow that it's not necessary to keep adding edges to get to a "decidable" state.

DI:Input.PartialOrdering.SuggestEdges.BinarySearch
--------------------------------------------------

If I add a new item, the system could do a binary search over the graph of existing items, asking me to say whether the new item should rank higher or lower than that item, until ... hmm, not sure what the termination condition is, but it shouldn't be too hard to work out. The "binary" split point should probably be based on the number of nodes either side of that point, rather than the rank of the node. Not clear what to do when (as seems quite likely) there are several disconnected subgraphs.

DI:Input.PartialOrdering.SuggestEdges.MinimizeRankSize
------------------------------------------------------

Rather than positioning on each axis in turn, it might be helpful to have different search steps look at different axes. This might work particularly well if the system considered one or more output functions while searching, and not just the input partial orderings. I suppose the overall goal would be to minimise the number of items which share any given rank, possibly with a bias to minimising sharing in lower ranks. The intent of that is that, if I look at the top few items in the output of a given ranking function, there should be as clear a separation of ranks as possible.
