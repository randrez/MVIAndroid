package util

import java.text.SimpleDateFormat

fun convertDateFormat(value:String, format:String):String{
    val date = SimpleDateFormat(format).parse(value)
    return SimpleDateFormat(format).format(date)
}