package com.gatekeeperx.devicex.foodhub.startup

/**
 * Orders [StartAppTask] instances according to a developer-defined priority list.
 *
 * Tasks that appear earlier in [priorityOrder] will be launched first.
 * Tasks whose class is not present in [priorityOrder] are treated as lowest
 * priority and will be launched after all explicitly ordered tasks.
 *
 * ## Example priority list
 * ```
 * listOf(
 *     FirebaseStartAppTask::class.java,   // index 0 — highest priority
 *     GatekeeperXStartAppTask::class.java, // index 1
 * )
 * ```
 * Because [MainApplicationInitializer] launches tasks in parallel, "priority"
 * controls dispatch order (which coroutine is launched first), not wall-clock
 * completion order. Tasks that are critical and fast should come first so their
 * coroutine is scheduled earliest by the runtime.
 *
 * @param priorityOrder Ordered list of [StartAppTask] implementation classes.
 */
class MainApplicationComparator(
    private val priorityOrder: List<Class<out StartAppTask>>,
) : Comparator<StartAppTask> {

    override fun compare(a: StartAppTask, b: StartAppTask): Int {
        return indexFor(a).compareTo(indexFor(b))
    }

    /**
     * Returns the priority index for [task], or [Int.MAX_VALUE] if the
     * task is not in [priorityOrder] (lowest priority / run last).
     */
    private fun indexFor(task: StartAppTask): Int =
        priorityOrder.indexOfFirst { clazz -> clazz.isInstance(task) }
            .takeIf { it >= 0 } ?: Int.MAX_VALUE
}
