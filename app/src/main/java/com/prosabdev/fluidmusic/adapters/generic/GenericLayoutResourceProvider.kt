package com.prosabdev.fluidmusic.adapters.generic

import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.utils.ConstantValues

abstract class GenericLayoutResourceProvider {

    companion object {
        fun getLayoutResourceId(organizeListGrid: Int): Int {
            var layoutResourceId: Int = 0
            if(organizeListGrid == ConstantValues.ORGANIZE_LIST_SMALL){
                layoutResourceId = R.layout.item_generic_explore_list
            }

            return layoutResourceId
        }
    }
}