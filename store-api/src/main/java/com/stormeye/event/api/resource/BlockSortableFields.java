package com.stormeye.event.api.resource;

/** Enumeration of fields that a block can be sored on */
@SuppressWarnings("java:S115") // Suppress: Rename this constant name to match the regular expression ‘^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$’
enum BlockSortableFields {
    blockHeight,
    eraId,
    deployCount,
    transferCount,
    timestamp
}
