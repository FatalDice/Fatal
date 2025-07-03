package uk.akane.fatal.utils

// Evaluate exception
open class EvaluateException(message: String) : Exception(message)
class IllegalSyntaxException(message: String) : EvaluateException(message)
class ParseException(message: String) : EvaluateException(message)

// Roll exception
open class RollException(message: String) : Exception(message)
class RollNumberLessThanOneException(message: String) : RollException(message)
class RollNumberOutOfBoundsException(message: String) : RollException(message)

// Character card exception
open class CharacterCardException(message: String) : RollException(message)
class CharacterCardNotFoundException(message: String) : CharacterCardException(message)