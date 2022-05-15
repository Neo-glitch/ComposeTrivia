package com.neo.composetrivia.data


/**
 * wrapper around retrofit response to get result if successful, show loading state when boolean is true and get error if unsuccessful
 */
data class DataOrException<T, Boolean, E: Exception>(
    var data: T? = null,
    var loading: Boolean? =null,
    var error: E? = null
)