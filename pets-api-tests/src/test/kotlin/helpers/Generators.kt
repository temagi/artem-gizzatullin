package helpers

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import models.Category
import models.Pet
import models.Status
import models.Tag

val petGenerator = arbitrary {
    Pet(
        id = Arb.int(11, 1000).bind(),
        name = Arb.string(10..20).bind(),
        category = Category(id = Arb.int(1, 10).bind(), name = Arb.string(10..20).bind()),
        photoUrls = listOf(
            Arb.string(10..20).bind(),
            Arb.string(1..5).bind()
        ),
        tags = listOf(
            Tag(id = Arb.int(1, 5).bind(), name = Arb.string(10..20).bind()),
            Tag(id = Arb.int(5, 20).bind(), name = Arb.string(1..5).bind())
        ),
        status = Arb.enum<Status>().bind().toString()
    )
}