package no.nav.dagpenger.aktivitetslogger

import mu.KotlinLogging
import no.nav.dagpenger.aktivitetslogger.Configuration.config
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.KafkaRapid
import no.nav.helse.rapids_rivers.RapidsConnection
import java.time.LocalDateTime

/**
 * Aktivitetslogger benyttes for å kunne sende aktiviteter inn til den sentrale aktivitetsloggen.
 *
 * **Eksempel**
 * ```
 * val logger = Aktivitetslogger.logger()
 * logger.info(hendelse, aktivitet)
 * ```
 */
class Aktivitetslogger private constructor(private val kafkaRapid: RapidsConnection) {

    companion object {

        @Volatile
        private var logger: Aktivitetslogger? = null

        /**
         * @return Aktivitetslogger instans
         */
        fun logger(
            rapidsConnection: RapidsConnection = KafkaRapid.create(
                AktivitetsloggConfig.fromEnv(config),
                config.getValue("KAFKA_RAPID_TOPIC"),
            ),
        ) =
            logger ?: synchronized(this) {
                logger ?: Aktivitetslogger(rapidsConnection).also { logger = it }
            }
    }

    private val logger = KotlinLogging.logger { }

    init {
        logger.info { "Starter aktivitetslogg" }
    }

    /**
     * Sender en logg inn med [Alvorlighetsgrad.INFO]
     * @param hendelse hendelsen aktiviteten er knyttet til
     * @param aktivitet aktiviteten knyttet til hendelsen
     */
    fun info(hendelse: Hendelse, aktivitet: Aktivitet?) {
        send(hendelse, Alvorlighetsgrad.INFO, aktivitet)
    }

    /**
     * Sender en logg inn med [Alvorlighetsgrad.ADVARSEL]
     * @param hendelse hendelsen aktiviteten er knyttet til
     * @param aktivitet aktiviteten knyttet til hendelsen
     */
    fun advarsel(hendelse: Hendelse, aktivitet: Aktivitet?) {
        send(hendelse, Alvorlighetsgrad.ADVARSEL, aktivitet)
    }

    /**
     * Sender en logg inn med [Alvorlighetsgrad.BEHOV]
     * @param hendelse hendelsen aktiviteten er knyttet til
     * @param aktivitet aktiviteten knyttet til hendelsen
     */
    fun behov(hendelse: Hendelse, aktivitet: Aktivitet?) {
        send(hendelse, Alvorlighetsgrad.BEHOV, aktivitet)
    }

    /**
     * Sender en logg inn med [Alvorlighetsgrad.FEIL]
     * @param hendelse hendelsen aktiviteten er knyttet til
     * @param aktivitet aktiviteten knyttet til hendelsen
     */
    fun feil(hendelse: Hendelse, aktivitet: Aktivitet?) {
        send(hendelse, Alvorlighetsgrad.FEIL, aktivitet)
    }

    /**
     * Sender en logg inn med [Alvorlighetsgrad.ALVORLIG]
     * @param hendelse hendelsen aktiviteten er knyttet til
     * @param aktivitet aktiviteten knyttet til hendelsen
     */
    fun alvorlig(hendelse: Hendelse, aktivitet: Aktivitet?) {
        send(hendelse, Alvorlighetsgrad.ALVORLIG, aktivitet)
    }

    private fun send(hendelse: Hendelse, alvorlighetsgrad: Alvorlighetsgrad, aktivitet: Aktivitet?) {
        logger.info { "publishing aktivitetslogg event for meldingsreferanse=${hendelse.meldingsreferanseId()}" }
        kafkaRapid.publish(
            JsonMessage.newMessage(
                "aktivitetslogg",
                mapOf(
                    "hendelse" to mapOf(
                        "type" to hendelse.javaClass.simpleName,
                        "meldingsreferanseId" to hendelse.meldingsreferanseId(),
                    ),
                    "ident" to hendelse.ident(),
                    "aktivitet" to if (aktivitet != null) {
                        mapOf(
                            "kontekster" to aktivitet.kontekst(),
                            "alvorlighetsGrad" to alvorlighetsgrad,
                            "melding" to aktivitet.melding(),
                            "tidsstempel" to LocalDateTime.now(),
                        )
                    } else {
                        emptyMap<String, Any>()
                    },
                ),
            ).toJson(),
        )
    }
}
