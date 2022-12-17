package com.prosabdev.fluidmusic.workers

class WorkerConstantValues {
    companion object {
        const val WORKER_OUTPUT_DATA = "WORKER_OUTPUT_DATA"

        const val ITEM_LIST = "ITEM_LIST" //ARGS for list of string to add, update or delete
        const val ITEM_LIST_MODEL_TYPE = "ITEM_LIST_MODEL_TYPE" //ARGS for the model TAG to be checked
        const val WHERE_COLUMN_INDEX = "WHERE_COLUMN_INDEX" //ARGS for the index column of songs to match in order where the model type is not a song (Eg. Folder, Album, Artist)
        const val ITEM_LIST_WHERE = "ITEM_LIST_WHERE" //ARGS for where clause. It can be where like(generally for search results) or where equal(generally for content explorer)
        const val ITEM_LIST_WHERE_EQUAL = "ITEM_LIST_WHERE_EQUAL" //ARGS for where equal clause.
        const val ITEM_LIST_WHERE_LIKE = "ITEM_LIST_WHERE_LIKE" //ARGS for where like
    }
}