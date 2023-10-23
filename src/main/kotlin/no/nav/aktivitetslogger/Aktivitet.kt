package no.sjovatsen.aktivitetslogger

import java.time.LocalDateTime

class Aktivitet(
    // val alvorlighetsgrad: Alvorlighetsgrad,
    val melding: String,
    val tidsstempel: LocalDateTime,
    val kontekst: List<Kontekst>,
) {

    // fun alvorlighetsgrad() = alvorlighetsgrad
    fun melding() = melding
    fun tidsstempel() = tidsstempel
    fun kontekst() = kontekst
}
