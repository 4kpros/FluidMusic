package com.prosabdev.common.persistence.models

import com.prosabdev.common.constants.MainConst

data class SortOrganizeItemSP (
    var isInvertSort : Boolean = false,
    var sortOrderBy : String = "id",
    var organizeListGrid : Int = MainConst.ORGANIZE_LIST_MEDIUM
)
