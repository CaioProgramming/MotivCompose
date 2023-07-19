package com.ilustris.motiv.foundation.model

import com.silent.ilustriscore.core.bean.BaseBean

data class Cover(
    val url: String = DEFAULT_USER_BACKGROUND,
    override var id: String = ""
) : BaseBean(id)
