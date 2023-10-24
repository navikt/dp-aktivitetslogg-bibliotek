package no.nav.dagpenger.aktivitetslogger

/**
 * Klassifisering av logghendelsene
 */
internal enum class Alvorlighetsgrad {
    /**
     * Brukes
     */
    INFO,
    ADVARSEL,
    BEHOV,
    FEIL,
    ALVORLIG,
}
