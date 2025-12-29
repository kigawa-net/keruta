package net.kigawa.keruta.ktse.reader

import tools.jackson.core.JacksonException

class InvalidFormatFrameErr(
    val e: JacksonException,
): FrameDecodeErr()
