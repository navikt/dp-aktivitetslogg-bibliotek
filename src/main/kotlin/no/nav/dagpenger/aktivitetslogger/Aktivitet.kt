package no.nav.dagpenger.aktivitetslogger

import java.time.LocalDateTime

class Aktivitet(
    // val alvorlighetsgrad: Alvorlighetsgrad,
    private val melding: String,
    private val kontekst: List<Kontekst>,
    private val tidsstempel: LocalDateTime = LocalDateTime.now(),
) {

    // fun alvorlighetsgrad() = alvorlighetsgrad
    fun melding() = melding
    fun tidsstempel() = tidsstempel
    fun kontekst() = kontekst
}
