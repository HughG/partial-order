package org.tameter.kotlin.collections

/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */
class MutableMultiSet<E>(
        wrapped: MutableMap<E, Int> = mutableMapOf()
) : Set<E> by wrapped.keys,
        MutableSet<E>
{
    private val counts = wrapped.withDefaultValue { 0 }

    override fun add(element: E): Boolean = add(element, 1)

    override fun addAll(elements: Collection<E>): Boolean {
        return elements.fold(false) { added, element -> add(element) || added }
    }

    override fun clear() = counts.clear()

    override fun remove(element: E): Boolean = remove(element, 1)

    override fun removeAll(elements: Collection<E>): Boolean {
        return elements.fold(false) { removed, element -> remove(element) || removed }
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        return elements.fold(false) { removed, element ->
            (elements.contains(element) && remove(element)) || removed
        }
    }

    override fun iterator(): MutableIterator<E> = counts.keys.iterator()

    fun count(element: E): Int = counts[element]

    fun addAndCount(element: E): Int {
        add(element)
        return counts[element]
    }

    fun add(element: E, count: Int): Boolean {
        val oldCount = counts[element]
        counts[element] = oldCount + count
        return oldCount == 0
    }

    fun remove(element: E, count: Int): Boolean {
        if (!counts.containsKey(element)) {
            return false
        }
        val oldCount = counts[element]
        counts[element] = minOf(0, oldCount - count)
        return oldCount <= count
    }
}

fun <E> mutableMultiSetOf() = MutableMultiSet<E>()