package com.lab.resizeservice

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import java.sql.Blob

@Entity
data class Image(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Lob
    var initialImage: Blob? = null,

    @Lob
    var resizedImage: Blob? = null,

    @Lob
    var imageWithWatermark: Blob? = null,

    @Lob
    var thumbnail: Blob? = null
)
