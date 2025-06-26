package uk.akane.fatal.utils

@Suppress("Unused")
class Bundle {
    private val map = mutableMapOf<String, Any?>()

    fun put(key: String, value: Any?) {
        map[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T? {
        return map[key] as? T
    }

    fun <T> getOrDefault(key: String, default: T): T {
        return get(key) ?: default
    }

    fun contains(key: String): Boolean = map.containsKey(key)

    fun remove(key: String) = map.remove(key)

    fun clear() = map.clear()

    override fun toString(): String = map.toString()
}
