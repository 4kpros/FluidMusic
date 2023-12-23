package com.prosabdev.common.persistence.models

import com.prosabdev.common.components.Constants

data class SortOrganizeItemSP (
    var isInvertSort : Boolean = false,
    var sortOrderBy : String = "id",
    var organizeListGrid : Int = Constants.ORGANIZE_LIST_MEDIUM
)
