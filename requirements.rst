Introduction
========

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
========

:Vision: I’m looking for one or more pieces of software I can use to derive partial rankings for some set of items. By “partial ranking” I mean that several items can end up with the same rank.

:Description: I will create some number of criteria or axes on which to partially rank each thing, then have some function to derive an overall ranking – for example, a linear combination of the individual rankings.

WCR:Items.Number
--------

I expect to handle at most a few hundred items at any one time, possible far fewer.

FR:Input.Ranking.Derived
--------

I want to be able to derive the rankings on each axis from other sources.

:Sub-Functions:
  - `FR:Input.Ranking.ExplicitNumerical`_
  - `FR:Input.PartialOrdering`_

FR:Input.Ranking.ExplicitNumerical
--------

I want to be able to directly provide numerical values, integral or real, as input rankings.

FR:Input.PartialOrdering
--------

I want to be able to derive the partial ranking from an explicitly provided partial ordering.

:Description: To look at it another way, a directed acyclic graph expresses a partial ordering, in which case the derived ranking I want is just the rank of each node in the graph.

:Linked To:
  - `FR:Input.Change.Incremental`_

:Is Impacted By:
  - `DI:PartialOrdering.SQL`_
  - `DI:PartialOrdering.NoSQL`_
  - `DI:PartialOrdering.GraphDB`_

FR:Input.Change.Incremental
--------

I want to be able to add and remove items over time, and add and remove new criteria and combining functions.

:Quality Requirements:
  - `QR:Input.PartialOrdering.Change.Incremental.Ease`_
  - `QR:Input.PartialOrdering.Single.Add.Time`_
  - `QR:Input.PartialOrdering.Multiple.Add.Time`_

QR:Input.PartialOrdering.Change.Incremental.Ease
--------

In the case of an input ranking derived from a partial ordering, I want to be able to edit the ordering fairly *easily*: *quickly* and without *frustration*.

QR:Input.PartialOrdering.Single.Add.Time
--------

After describing a new item, it should take *very little* time to position it on any given existing axis derived from a partial ordering.

:Scale: seconds to position a new item

:Goal: 60

:Design Idea: If I add a new item, the system could do a binary search over the graph of existing items, asking me to say whether the new item should rank higher or lower than that item, until … hmm, not sure what the termination condition is, but it shouldn’t be too hard to work out. The “binary” split point should probably be based on the number of nodes either side of that point, rather than the rank of the node. Not clear what to do when (as seems quite likely) there are several disconnected subgraphs.

QR:Input.PartialOrdering.Multiple.Add.Time
--------

After describing a new item, it should take *little* time to position it on *3-4* axes derived from a partial ordering.

:Scale: seconds to position a new item

:Goal: 120

:Design Idea: Rather than positioning on each axis in turn, it might be helpful to have different search steps look at different axes. This might work particularly well if the system considered one or more output functions while searching, and not just the input partial orderings. I suppose the overall goal would be to minimise the number of items which share any given rank, possibly with a bias to minimising sharing in lower ranks. The intent of that is that, if I look at the top few items in the output of a given ranking function, there should be as clear a separation of ranks as possible.

DC:Input.Storage.Format.Diffable
--------

I want to be able to have partial orderings, explicit rankings, and combining functions (or, their parameters) under version control in an easily-diffable way, so no binary storage formats.

:Note: I don’t currently require any special functional integration with version control systems, just a suitable data format.

FR:Input.CombiningFunction
--------

I want to be able to have any number of combining functions to choose the output ranking.

:Quality Requirements:
  - `QR:Input.CombiningFunction.Select.Ease`_
  
QR:Input.CombiningFunction.Select.Ease
--------

I want to be able to *easily* switch the output between using any one of the combining functions: *quickly* and without *frustration*.

DC:Platform
--------

Ideally the system will be usable on both desktop and mobile (Android 6) devices.

FR:Access.OnlineAndOffline
--------

Even more ideally, it will be usable both online and offline.

:Issue: What should be done to resolve conflicts if edits are made on multiple devices “concurrently”?

:Is Imapacted By:
  - `DI:Platform.JVM`_
  - `DI:Platform.Web`_

FR:Export.Data
--------

Ideally I could get at least a list of item IDs out, and maybe also rankings, in such a way as to sync semi-automatically with other systems. (Not sure what, but I’m bound to come up with something …)

FR:Multi-User
--------

It’s fine if it’s single-user, though multiple-user would be a bonus.

RC:System.Creation.Effort
--------

It’s fine if I have to write a bit of code to tie things together, but I don’t want to have to write layers of GUI, DB/VCS management, authentication, etc. if I can avoid it.

RC:System.Money
--------

I don’t mind paying a little for this – maybe up to £200 total, or £10/month.

DI:PartialOrdering.SQL
--------

Use SQL relations to represent the graph edges.

DI:PartialOrdering.NoSQL
--------

Use a NoSQL-style document with ID references between nodes.

DI:PartialOrdering.GraphDB
--------

Use a "graph database" -- is there such a thing, distinct from "NoSQL"?

DI:Platform.JVM
--------

Write web and Android apps in a JVM-based language.

:Is Impacted By:
  - `DI:Langauge.JVM.Kotlin`_
  - `DI:Langauge.JVM.Ceylon`_

DI:Platform.Web
--------

Write a web app with HTML 5 offline features, for use on desktop and mobile (rather than writing a separate Android app).

DI:Langauge.JVM.Kotlin
--------

Try using IntelliJ's Kotlin, which is a static improvement to Java, closely compatible with Java.

DI:Langauge.JVM.Ceylon
--------

Try using Ceylon, which is a static language significantly different from Java.
