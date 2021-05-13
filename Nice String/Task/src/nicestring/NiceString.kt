package nicestring

fun String.isNice(): Boolean {
    val firstCondition: Boolean = !listOf("bu", "ba", "be").any { this.contains(it, true) }
    val secondCondition: Boolean = this.filter { listOf('a', 'e', 'i', 'o', 'u').contains(it) }.count() > 2
    val thirdCondition: Boolean = this.zipWithNext().any { it.first == it.second }
    return listOf(firstCondition, secondCondition, thirdCondition).filter { it }.count() > 1
}
