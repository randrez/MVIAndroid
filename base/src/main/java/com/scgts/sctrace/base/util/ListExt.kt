package com.scgts.sctrace.base.util

fun List<String>.toFormattedString(): String = this.let { listOfString ->
    when {
        listOfString.size <= 2 -> listOfString.joinToString(" and ")
        else -> listOfString.joinToString { string -> if (listOfString.last() == string) "and $string" else string }
    }
}

fun List<String>.toSQLString(): String = this.let { listOfString ->
    listOfString.joinToString { "\'$it\'" }
}