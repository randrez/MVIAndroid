package com.scgts.sctrace.network

data class GraphQlException(override val message: String?, val error: Error?) : RuntimeException()
