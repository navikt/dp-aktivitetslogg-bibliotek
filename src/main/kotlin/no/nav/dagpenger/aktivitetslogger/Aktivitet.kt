package no.nav.dagpenger.aktivitetslogger

import java.time.LocalDateTime

/**
 * Representerer en aktivitet som er gjort i tilknytning til en hendelse.
 *
 * @property melding knyttet til aktiviteten
 * @property kontekst liste med [Kontekst] for aktiviteten
 * @property tidsstempel for når aktiviteten ble gjort. Blir automatisk satt til tidspunktet for når
 * Aktivitet blir laget hvis ikke annet er angitt
 */
class Aktivitet(
    private val melding: String,
    private val kontekst: List<Kontekst>,
    private val tidsstempel: LocalDateTime = LocalDateTime.now(),
) {

    /**
     * @return meldingen i aktiviteten
     */
    fun melding() = melding

    /**
     * @return tidsstempelet til aktiviteten
     */
    fun tidsstempel() = tidsstempel

    /**
     * @return liste over kontekster knyttet til aktiviteten
     */
    fun kontekst() = kontekst
}
