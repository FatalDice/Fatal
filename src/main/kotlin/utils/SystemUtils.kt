package uk.akane.fatal.utils

object SystemUtils {
    private var _systemCores = 0
    val systemCores: Int
        get() {
            if (_systemCores == 0) {
                _systemCores = Runtime.getRuntime().availableProcessors()
            }
            return _systemCores
        }
}