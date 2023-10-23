package no.nav.aktivitetslogger

import java.util.UUID

abstract class Hendelse(
    private val meldingsreferanseId: UUID,
    private val ident: String,
) {

    fun ident() = ident
    fun meldingsreferanseId() = meldingsreferanseId
}
